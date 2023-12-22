package com.cj.flood.func.modular.prediction.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.LzzRainfallStationService;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.model.func.core.util.MultipartFileUtil;
import com.cj.flood.func.modular.prediction.bean.dto.FloodPeakDto;
import com.cj.flood.func.modular.prediction.bean.dto.IncomingWaterForecastKVDto;
import com.cj.flood.func.modular.prediction.bean.dto.IncomingWaterForecastViewDto;
import com.cj.flood.func.modular.prediction.bean.dto.PredictionProcessDto;
import com.cj.flood.func.modular.prediction.bean.req.IncomingWaterForecastListReq;
import com.cj.flood.func.modular.prediction.bean.res.IncomingWaterForecastDetailsRes;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.cj.flood.func.modular.prediction.service.IncomingWaterForecastService;
import com.cj.flood.func.modular.prediction.mapper.IncomingWaterForecastMapper;
import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParamNew;
import com.cj.model.func.modular.FloodPredict.entity.IrrigatedHydrologyParam;
import com.cj.model.func.modular.FloodPredict.entity.LzzHydrologyParam;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import com.cj.model.func.modular.FloodPredict.model.TouTunHe;
import com.cj.model.func.modular.entity.Flood;
import io.minio.ObjectWriteResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

/**
* @author July Lion
* @description 针对表【INCOMING_WATER_FORECAST(来水预报)】的数据库操作Service实现
* @createDate 2023-11-03 11:17:56
*/
@Service
@Slf4j
public class IncomingWaterForecastServiceImpl extends ServiceImpl<IncomingWaterForecastMapper, IncomingWaterForecast>
    implements IncomingWaterForecastService{

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private IncomingWaterForecastService incomingWaterForecastService;

    @Autowired
    private LzzGaugingStationService lzzGaugingStationService;

    @Autowired
    private LzzRainfallStationService lzzRainfallStationService;

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    @Override
    public RestResponse add(IncomingWaterForecast incomingWaterForecast) {
        try {
            incomingWaterForecast.setId(UUIDUtils.getUUID());
            incomingWaterForecast.setCreateTime(new Date());
            if(incomingWaterForecast.getPeriodTimeType()==1){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep()*30);
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==2){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep()*10);
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==3){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep());
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==4){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.HOUR,incomingWaterForecast.getPeriodTimeStep());
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            boolean save = this.save(incomingWaterForecast);
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.submit(new Runnable() {
                private IncomingWaterForecastService incomingWaterForecastService = SpringUtil.getBean(IncomingWaterForecastService.class);
                private LzzGaugingStationService lzzGaugingStationService = SpringUtil.getBean(LzzGaugingStationService.class);
                private LzzRainfallStationService lzzRainfallStationService = SpringUtil.getBean(LzzRainfallStationService.class);
                private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService = SpringUtil.getBean(IrrigatedPlatformDataInfoService.class);

                @Override
                public void run() {
                    try {
                        ForcastInputParamNew forcastInputParamNew = new ForcastInputParamNew();
                        forcastInputParamNew.setPredictionTime(incomingWaterForecast.getPredictionTime());
                        forcastInputParamNew.setModelType(incomingWaterForecast.getModelType());
                        forcastInputParamNew.setPeriodTimeNum(incomingWaterForecast.getPeriodTimeNum());
                        forcastInputParamNew.setPeriodTimeStep(incomingWaterForecast.getPeriodTimeStep());
                        forcastInputParamNew.setPeriodTimeType(incomingWaterForecast.getPeriodTimeType());
                        LzzHydrologyParam lzzHydrologyParam = new LzzHydrologyParam();
                        lzzHydrologyParam.setThreeGaugingStation(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"3号桥水位站").list());
                        lzzHydrologyParam.setLzzInput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子入库水位站").list());
                        lzzHydrologyParam.setLzzOutput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子出库水位站").list());
                        lzzHydrologyParam.setLzzWaterLevel(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子库水位站").list());
                        lzzHydrologyParam.setKsgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"喀什沟自动雨量站").list());
                        lzzHydrologyParam.setHgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"黑沟自动雨量站").list());
                        lzzHydrologyParam.setMkgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"煤矿沟自动雨量站").list());
                        lzzHydrologyParam.setWmgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"无名沟自动雨量站").list());
                        lzzHydrologyParam.setJpsRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"加普沙自动雨量站").list());
                        lzzHydrologyParam.setZrdRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"宰尔德自动雨量站").list());
                        lzzHydrologyParam.setDngRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"东南沟自动雨量站").list());
                        lzzHydrologyParam.setBylcRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"八一林场自动雨量站").list());
                        lzzHydrologyParam.setSedwRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"萨尔达万自动雨量站").list());
                        lzzHydrologyParam.setZccRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"制材厂自动雨量站").list());
                        IrrigatedHydrologyParam irrigatedHydrologyParam = new IrrigatedHydrologyParam();
                        irrigatedHydrologyParam.setXqzGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"小渠子雨量站").list());
                        irrigatedHydrologyParam.setTjydGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"团结一队雨量站").list());
                        irrigatedHydrologyParam.setTthGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"头屯河水库雨量站").list());
                        irrigatedHydrologyParam.setTthInput(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"入库流量").list());
                        forcastInputParamNew.setLzzHydrologyParam(lzzHydrologyParam);
                        forcastInputParamNew.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
                        //调用模型方法生成模型结果，更新到数据库
                        //System.out.println("Hello pool");
                        TemporaryXlsx floodList = TouTunHe.getFloodList(forcastInputParamNew);
                        //生成模型结果文件
                        String fileAddress = floodList.getPath();
                        String[] split = fileAddress.split("\\\\");
                        Date date = new Date();
                        String yyyyMMdd = DateUtil.format(date, "yyyyMMdd");
                        String hh = DateUtil.format(date, "HH");
                        String mm = DateUtil.format(date, "mm");
                        String ss = DateUtil.format(date, "ss");
                        ObjectWriteResponse objectWriteResponse = minioUtils.putObject("tth", yyyyMMdd+"/"+hh+"/"+mm+"/"+ss+"/"+ UUID.fastUUID().toString(true)+"/"+split[split.length-1], fileAddress);
                        String object = objectWriteResponse.object();
                        incomingWaterForecastService.lambdaUpdate().set(IncomingWaterForecast::getModelResultAddress,object).eq(IncomingWaterForecast::getId,incomingWaterForecast.getId()).update();
                    }catch (Exception e) {
                        e.printStackTrace();
                        log.error("-------------------------------------------error-------------------------------------------");
                    }
                }
            });
            if(save){
                return RestResponse.ok("生成模型结果成功");
            }else {
                return RestResponse.no("生成模型结果失败");
            }
        }catch (Exception e) {
            log.error("生成模型结果错误:"+e.getMessage());
            e.printStackTrace();
            return RestResponse.no(e.getMessage());
        }
    }

   /* @Override
    public RestResponse add(IncomingWaterForecast incomingWaterForecast) {
        try {
            incomingWaterForecast.setId(UUIDUtils.getUUID());
            incomingWaterForecast.setCreateTime(new Date());
            if(incomingWaterForecast.getPeriodTimeType()==1){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep()*30);
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==2){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep()*10);
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==3){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep());
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==4){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.HOUR,incomingWaterForecast.getPeriodTimeStep());
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            boolean save = this.save(incomingWaterForecast);
            if(save){
                try {
                    ForcastInputParamNew forcastInputParamNew = new ForcastInputParamNew();
                    forcastInputParamNew.setPredictionTime(incomingWaterForecast.getPredictionTime());
                    forcastInputParamNew.setModelType(incomingWaterForecast.getModelType());
                    forcastInputParamNew.setPeriodTimeNum(incomingWaterForecast.getPeriodTimeNum());
                    forcastInputParamNew.setPeriodTimeStep(incomingWaterForecast.getPeriodTimeStep());
                    forcastInputParamNew.setPeriodTimeType(incomingWaterForecast.getPeriodTimeType());
                    LzzHydrologyParam lzzHydrologyParam = new LzzHydrologyParam();
                    lzzHydrologyParam.setThreeGaugingStation(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"3号桥水位站").list());
                    lzzHydrologyParam.setLzzInput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子入库水位站").list());
                    lzzHydrologyParam.setLzzOutput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子出库水位站").list());
                    lzzHydrologyParam.setLzzWaterLevel(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子库水位站").list());
                    lzzHydrologyParam.setKsgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"喀什沟自动雨量站").list());
                    lzzHydrologyParam.setHgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"黑沟自动雨量站").list());
                    lzzHydrologyParam.setMkgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"煤矿沟自动雨量站").list());
                    lzzHydrologyParam.setWmgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"无名沟自动雨量站").list());
                    lzzHydrologyParam.setJpsRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"加普沙自动雨量站").list());
                    lzzHydrologyParam.setZrdRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"宰尔德自动雨量站").list());
                    lzzHydrologyParam.setDngRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"东南沟自动雨量站").list());
                    lzzHydrologyParam.setBylcRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"八一林场自动雨量站").list());
                    lzzHydrologyParam.setSedwRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"萨尔达万自动雨量站").list());
                    lzzHydrologyParam.setZccRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"制材厂自动雨量站").list());
                    IrrigatedHydrologyParam irrigatedHydrologyParam = new IrrigatedHydrologyParam();
                    irrigatedHydrologyParam.setXqzGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"小渠子雨量站").list());
                    irrigatedHydrologyParam.setTjydGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"团结一队雨量站").list());
                    irrigatedHydrologyParam.setTthGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"头屯河水库雨量站").list());
                    irrigatedHydrologyParam.setTthInput(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"入库流量").list());
                    forcastInputParamNew.setLzzHydrologyParam(lzzHydrologyParam);
                    forcastInputParamNew.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
                    //调用模型方法生成模型结果，更新到数据库
                    TemporaryXlsx floodList = TouTunHe.getFloodList(forcastInputParamNew);
                    //生成模型结果文件
                    String fileAddress = floodList.getPath();
                    String[] split = fileAddress.split("\\\\");
                    Date date = new Date();
                    String yyyyMMdd = DateUtil.format(date, "yyyyMMdd");
                    String hh = DateUtil.format(date, "HH");
                    String mm = DateUtil.format(date, "mm");
                    String ss = DateUtil.format(date, "ss");
                    ObjectWriteResponse objectWriteResponse = minioUtils.putObject("tth", yyyyMMdd+"/"+hh+"/"+mm+"/"+ss+"/"+ UUID.fastUUID().toString(true)+"/"+split[split.length-1], fileAddress);
                    String object = objectWriteResponse.object();
                    incomingWaterForecastService.lambdaUpdate().set(IncomingWaterForecast::getModelResultAddress,object).eq(IncomingWaterForecast::getId,incomingWaterForecast.getId()).update();
                }catch (Exception e) {
                    e.printStackTrace();
                    log.error("-------------------------------------------error-------------------------------------------");
                }
                return RestResponse.ok("生成模型结果成功");
            }else {
                return RestResponse.no("生成模型结果失败");
            }
        }catch (Exception e) {
            log.error("生成模型结果错误:"+e.getMessage());
            e.printStackTrace();
            return RestResponse.no(e.getMessage());
        }
    }*/

    @Override
    public RestResponse delete(String id) {
        try {
            boolean b = this.removeById(id);
            if(b) {
                return RestResponse.ok("删除成功");
            }else {
                return RestResponse.no("删除失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no(e.getMessage());
        }
    }

    @Override
    public RestResponse update(IncomingWaterForecast incomingWaterForecast) {
        try {
            if(incomingWaterForecast.getPeriodTimeType()==1){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep()*30);
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==2){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep()*10);
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==3){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep());
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==4){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.HOUR,incomingWaterForecast.getPeriodTimeStep());
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            incomingWaterForecast.setModelResultAddress("");
            boolean save = this.updateById(incomingWaterForecast);
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.submit(new Runnable() {
                private IncomingWaterForecastService incomingWaterForecastService = SpringUtil.getBean(IncomingWaterForecastService.class);

                @Override
                public void run() {
                    try {
                        //调用模型方法生成模型结果，更新到数据库
                        ForcastInputParamNew forcastInputParamNew = new ForcastInputParamNew();
                        forcastInputParamNew.setPredictionTime(incomingWaterForecast.getPredictionTime());
                        forcastInputParamNew.setModelType(incomingWaterForecast.getModelType());
                        forcastInputParamNew.setPeriodTimeNum(incomingWaterForecast.getPeriodTimeNum());
                        forcastInputParamNew.setPeriodTimeStep(incomingWaterForecast.getPeriodTimeStep());
                        forcastInputParamNew.setPeriodTimeType(incomingWaterForecast.getPeriodTimeType());
                        LzzHydrologyParam lzzHydrologyParam = new LzzHydrologyParam();
                        lzzHydrologyParam.setThreeGaugingStation(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"3号桥水位站").list());
                        lzzHydrologyParam.setLzzInput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子入库水位站").list());
                        lzzHydrologyParam.setLzzOutput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子出库水位站").list());
                        lzzHydrologyParam.setLzzWaterLevel(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子库水位站").list());
                        lzzHydrologyParam.setKsgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"喀什沟自动雨量站").list());
                        lzzHydrologyParam.setHgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"黑沟自动雨量站").list());
                        lzzHydrologyParam.setMkgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"煤矿沟自动雨量站").list());
                        lzzHydrologyParam.setWmgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"无名沟自动雨量站").list());
                        lzzHydrologyParam.setJpsRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"加普沙自动雨量站").list());
                        lzzHydrologyParam.setZrdRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"宰尔德自动雨量站").list());
                        lzzHydrologyParam.setDngRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"东南沟自动雨量站").list());
                        lzzHydrologyParam.setBylcRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"八一林场自动雨量站").list());
                        lzzHydrologyParam.setSedwRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"萨尔达万自动雨量站").list());
                        lzzHydrologyParam.setZccRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"制材厂自动雨量站").list());
                        IrrigatedHydrologyParam irrigatedHydrologyParam = new IrrigatedHydrologyParam();
                        irrigatedHydrologyParam.setXqzGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"小渠子雨量站").list());
                        irrigatedHydrologyParam.setTjydGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"团结一队雨量站").list());
                        irrigatedHydrologyParam.setTthGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"头屯河水库雨量站").list());
                        irrigatedHydrologyParam.setTthInput(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"入库流量").list());
                        forcastInputParamNew.setLzzHydrologyParam(lzzHydrologyParam);
                        forcastInputParamNew.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
                        //调用模型方法生成模型结果，更新到数据库
                        TemporaryXlsx floodList = TouTunHe.getFloodList(forcastInputParamNew);
                        //生成模型结果文件
                        String fileAddress = floodList.getPath();
                        String[] split = fileAddress.split("\\\\");
                        Date date = new Date();
                        String yyyyMMdd = DateUtil.format(date, "yyyyMMdd");
                        String hh = DateUtil.format(date, "HH");
                        String mm = DateUtil.format(date, "mm");
                        String ss = DateUtil.format(date, "ss");
                        ObjectWriteResponse objectWriteResponse = minioUtils.putObject("tth", yyyyMMdd+"/"+hh+"/"+mm+"/"+ss+"/"+ UUID.fastUUID().toString(true)+"/"+split[split.length-1], fileAddress);
                        String object = objectWriteResponse.object();
                        incomingWaterForecastService.lambdaUpdate().set(IncomingWaterForecast::getModelResultAddress,object).eq(IncomingWaterForecast::getId,incomingWaterForecast.getId()).update();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if(save){
                return RestResponse.ok("更新模型结果成功");
            }else {
                return RestResponse.no("更新模型结果失败");
            }
        }catch (Exception e) {
            log.error("更新模型结果错误:"+e.getMessage());
            e.printStackTrace();
            return RestResponse.no(e.getMessage());
        }
    }

    @Override
    public RestResponse<IPage<IncomingWaterForecast>> selectList(IncomingWaterForecastListReq req) {
        try {
            IPage<IncomingWaterForecast> incomingWaterForecastPage = new Page<>(req.getPageNum(),req.getPageSize());
            IPage<IncomingWaterForecast> page = this.lambdaQuery().eq(StringUtils.isNotEmpty(req.getProgrammeName()), IncomingWaterForecast::getProgrammeName, req.getProgrammeName()).
                    eq(req.getPeriodTimeType() != null, IncomingWaterForecast::getPeriodTimeType, req.getPeriodTimeType()).
                    eq(StringUtils.isNotEmpty(req.getCreateBy()),IncomingWaterForecast::getCreateBy,req.getCreateBy()).
                    eq(req.getPredictionTime() != null, IncomingWaterForecast::getPredictionTime, req.getPredictionTime()).page(incomingWaterForecastPage);
            if(page.getSize()>0){
                return RestResponse.ok(page);
            }else {
                return RestResponse.no("暂无数据");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no(e.getMessage());
        }
    }

    @Override
    public RestResponse<IncomingWaterForecastDetailsRes> selectDetails(String id) {
        try {
            IncomingWaterForecastDetailsRes res = new IncomingWaterForecastDetailsRes();
            IncomingWaterForecast incomingWaterForecast = this.getById(id);
            if(null != incomingWaterForecast){
                res.setPredictionTime(incomingWaterForecast.getPredictionTime());
                res.setEndTime(incomingWaterForecast.getEndTime());
                res.setProgrammeName(incomingWaterForecast.getProgrammeName());
                String modelResultAddress = incomingWaterForecast.getModelResultAddress();
                if(StringUtils.isNotEmpty(modelResultAddress)){
                    InputStream tth = minioUtils.getObject("tth", modelResultAddress);
                    String[] split = modelResultAddress.split("/");
                    String[] split1 = split[split.length - 1].split("\\.");
                    MultipartFile multipartFile = MultipartFileUtil.inputStreamToMultipartFile(tth, split1[0]);
                    List<Flood> floods = ExcelUtils.importExcel(multipartFile, Flood.class);
                    Map<String, IncomingWaterForecastViewDto> view = new LinkedHashMap<>();
                    IncomingWaterForecastViewDto threeBridge = getIncomingWaterForecastViewDto(floods, "3号桥");
                    if(null != threeBridge){
                        view.put("3号桥",threeBridge);
                    }else {
                        view.put("3号桥",null);
                    }
                    IncomingWaterForecastViewDto lzzEntryStation = getIncomingWaterForecastViewDto(floods, "楼庄子");
                    if(null != lzzEntryStation){
                        view.put("楼庄子",lzzEntryStation);
                    }else {
                        view.put("楼庄子",null);
                    }
                    IncomingWaterForecastViewDto tthEntryStation = getIncomingWaterForecastViewDto(floods, "楼头区间");
                    if(null != tthEntryStation){
                        view.put("楼头区间",tthEntryStation);
                    }else {
                        view.put("楼头区间",null);
                    }
                    res.setView(view);
                    return RestResponse.ok(res);
                }else {
                    return RestResponse.no("正在生成模型计算结果，请稍后……");
                }
            }else {
                return RestResponse.no("查无数据");
            }

        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no(e.getMessage());
        }
    }

    public IncomingWaterForecastViewDto getIncomingWaterForecastViewDto(List<Flood> floods,String station){
        List<Flood> threeBridge = floods.stream().filter(t -> t.getLocation().equals(station)).collect(Collectors.toList());
        if(null != threeBridge && threeBridge.size() > 0) {
            IncomingWaterForecastViewDto incomingWaterForecastViewDto = new IncomingWaterForecastViewDto();
            List<Flood> threeBridgeFloodPeak = threeBridge.stream().filter(t -> null != t.getPeakIndex() && t.getPeakIndex() != 0).collect(Collectors.toList());
            if(null != threeBridgeFloodPeak && threeBridgeFloodPeak.size() >0){
                Map<Integer, FloodPeakDto> floodPeak = new HashMap<>();
                Map<Integer, List<Flood>> threeBridgeCollect = threeBridgeFloodPeak.stream().collect(Collectors.groupingBy(Flood::getPeakIndex));
                Set<Integer> threeBridgePeakIndex = threeBridgeCollect.keySet();
                for(Integer threeBridgeIndex : threeBridgePeakIndex){
                    List<Flood> threeBridgeFloodPeakDetailsList = threeBridgeCollect.get(threeBridgeIndex).stream().sorted(Comparator.comparing(Flood::getPeakTime)).collect(Collectors.toList());
                    FloodPeakDto floodPeakDto = new FloodPeakDto();
                    Flood floodTemp = threeBridgeFloodPeakDetailsList.get(0);
                    floodPeakDto.setFloodLevel(floodTemp.getFloodLevel());
                    floodPeakDto.setPeakTime(floodTemp.getPeakTime());
                    floodPeakDto.setPeakDuration(floodTemp.getPeakDuration());
                    floodPeak.put(threeBridgeIndex,floodPeakDto);
                }
                incomingWaterForecastViewDto.setFloodPeak(floodPeak);
            }
            List<PredictionProcessDto> predictionProcess = new ArrayList<>();
            for (Flood flood:threeBridge){
                PredictionProcessDto predictionProcessDto = new PredictionProcessDto();
                predictionProcessDto.setPreQ(flood.getPreQ());
                predictionProcessDto.setTime(flood.getTime());
                predictionProcess.add(predictionProcessDto);
            }
            incomingWaterForecastViewDto.setPredictionProcess(predictionProcess);
            Flood flood = threeBridge.get(0);
            List<IncomingWaterForecastKVDto> qCause = new ArrayList<>();
            String qCauseValue = flood.getQCause();
            String[] qCauseSplit = qCauseValue.split(",");
            for(String qCauseSplitTemp : qCauseSplit){
                IncomingWaterForecastKVDto dto = new IncomingWaterForecastKVDto();
                String[] split2 = qCauseSplitTemp.split(":");
                dto.setName(split2[0]);
                dto.setValue(Double.parseDouble(split2[1]));
                qCause.add(dto);
            }
            incomingWaterForecastViewDto.setQCause(qCause);
            List<IncomingWaterForecastKVDto> qComposition = new ArrayList<>();
            String qCompositionValue = flood.getQComposition();
            String[] qCompositionSplit = qCompositionValue.split(",");
            for(String qCompositionSplitTemp : qCompositionSplit){
                IncomingWaterForecastKVDto dto = new IncomingWaterForecastKVDto();
                String[] split2 = qCompositionSplitTemp.split(":");
                dto.setName(split2[0]);
                dto.setValue(Double.parseDouble(split2[1]));
                qComposition.add(dto);
            }
            incomingWaterForecastViewDto.setQComposition(qComposition);
            return incomingWaterForecastViewDto;
        }else {
            return null;
        }
    }
}




