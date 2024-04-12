package com.cj.flood.func.modular.prediction.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.func.modular.prediction.bean.dto.*;
import com.cj.flood.func.modular.prediction.bean.req.IncomingWaterForecastAddReq;
import com.cj.flood.func.modular.prediction.bean.req.WaterResourceAllocationTimeReq;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.LzzRainfallStationService;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.model.func.core.util.MultipartFileUtil;
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
import com.cj.model.func.modular.FloodPredict.utils.InputUtils;
import com.cj.model.func.modular.entity.Flood;
import io.minio.ObjectWriteResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
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
    private LzzGaugingStationService lzzGaugingStationService;

    @Autowired
    private LzzRainfallStationService lzzRainfallStationService;

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(IncomingWaterForecastAddReq req) {
        try {
            List<IncomingWaterForecast> list = this.lambdaQuery().eq(IncomingWaterForecast::getProgrammeName, req.getIncomingWaterForecast().getProgrammeName()).list();
            if(!list.isEmpty()){
                return RestResponse.no("请勿重复新增相同方案名称");
            }
            SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
            IncomingWaterForecast incomingWaterForecast = req.getIncomingWaterForecast();
            incomingWaterForecast.setId(UUIDUtils.getUUID());
            incomingWaterForecast.setCreateTime(new Date());
            //(1-月 2-旬 3-日 4-小时)
            if(incomingWaterForecast.getPeriodTimeType()==1){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.MONTH,incomingWaterForecast.getPeriodTimeStep()*incomingWaterForecast.getPeriodTimeNum());
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==2){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep()*incomingWaterForecast.getPeriodTimeNum()*10);
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==3){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep()*incomingWaterForecast.getPeriodTimeNum());
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==4){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.HOUR,incomingWaterForecast.getPeriodTimeStep()*incomingWaterForecast.getPeriodTimeNum());
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            incomingWaterForecast.setStatus(1);
            incomingWaterForecast.setCreateBy(saBaseLoginUser.getName());
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
                        if(!req.getIsSimulation()){
                            req.getRainFallDtos().forEach(t->t.setArea("面雨量"));
                        }else {
                            forcastInputParamNew.setPreFlow(req.getPreFlow());
                            forcastInputParamNew.setPreRainFall(req.getPreRainFall());
                        }
                        forcastInputParamNew.setIsSimulation(req.getIsSimulation());
                        forcastInputParamNew.setRainFallDtos(req.getRainFallDtos());
                        List<Date> dates = InputUtils.judgeDate(incomingWaterForecast.getPredictionTime(),incomingWaterForecast.getPeriodTimeNum());
                        if(dates.isEmpty()){
                            LzzHydrologyParam lzzHydrologyParam = new LzzHydrologyParam();
                            lzzHydrologyParam.setThreeGaugingStation(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"3号桥水位站").list());
                            lzzHydrologyParam.setLzzInput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"天谷自动水位站").list());
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
                            forcastInputParamNew.setDataStartTime(sdf.parse("2023-01-01 00:00:00"));
                        }else {
                            Date startTime = dates.get(0);
                            Date endTime = dates.get(1);
                            LzzHydrologyParam lzzHydrologyParam = new LzzHydrologyParam();
                            lzzHydrologyParam.setThreeGaugingStation(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"3号桥水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list());
                            lzzHydrologyParam.setLzzInput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"天谷自动水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list());
                            lzzHydrologyParam.setLzzOutput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子出库水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list());
                            lzzHydrologyParam.setLzzWaterLevel(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子库水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list());
                            lzzHydrologyParam.setKsgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"喀什沟自动雨量站").between(LzzRainfallStation::getTime,startTime,endTime).list());
                            lzzHydrologyParam.setHgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"黑沟自动雨量站").between(LzzRainfallStation::getTime,startTime,endTime).list());
                            lzzHydrologyParam.setMkgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"煤矿沟自动雨量站").between(LzzRainfallStation::getTime,startTime,endTime).list());
                            lzzHydrologyParam.setWmgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"无名沟自动雨量站").between(LzzRainfallStation::getTime,startTime,endTime).list());
                            lzzHydrologyParam.setJpsRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"加普沙自动雨量站").between(LzzRainfallStation::getTime,startTime,endTime).list());
                            lzzHydrologyParam.setZrdRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"宰尔德自动雨量站").between(LzzRainfallStation::getTime,startTime,endTime).list());
                            lzzHydrologyParam.setDngRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"东南沟自动雨量站").between(LzzRainfallStation::getTime,startTime,endTime).list());
                            lzzHydrologyParam.setBylcRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"八一林场自动雨量站").between(LzzRainfallStation::getTime,startTime,endTime).list());
                            lzzHydrologyParam.setSedwRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"萨尔达万自动雨量站").between(LzzRainfallStation::getTime,startTime,endTime).list());
                            lzzHydrologyParam.setZccRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName,"制材厂自动雨量站").between(LzzRainfallStation::getTime,startTime,endTime).list());
                            IrrigatedHydrologyParam irrigatedHydrologyParam = new IrrigatedHydrologyParam();
                            irrigatedHydrologyParam.setXqzGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"小渠子雨量站").between(IrrigatedPlatformDataInfo::getMonitorTime,startTime,endTime).list());
                            irrigatedHydrologyParam.setTjydGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"团结一队雨量站").between(IrrigatedPlatformDataInfo::getMonitorTime,startTime,endTime).list());
                            irrigatedHydrologyParam.setTthGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"头屯河水库雨量站").between(IrrigatedPlatformDataInfo::getMonitorTime,startTime,endTime).list());
                            irrigatedHydrologyParam.setTthInput(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"入库流量").between(IrrigatedPlatformDataInfo::getMonitorTime,startTime,endTime).list());
                            forcastInputParamNew.setLzzHydrologyParam(lzzHydrologyParam);
                            forcastInputParamNew.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
                            forcastInputParamNew.setDataStartTime(startTime);
                        }

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
                        incomingWaterForecastService.lambdaUpdate().set(IncomingWaterForecast::getStatus,2).set(IncomingWaterForecast::getModelResultAddress,object).eq(IncomingWaterForecast::getId,incomingWaterForecast.getId()).update();
                    }catch (Exception e) {
                        e.printStackTrace();
                        log.error("-------------------------------------------error-------------------------------------------");
                        incomingWaterForecastService.lambdaUpdate().set(IncomingWaterForecast::getStatus,3).eq(IncomingWaterForecast::getId,incomingWaterForecast.getId()).update();
                    }
                }
            });
            if(save){
                return RestResponse.ok("来水预报模型生成中……");
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
    public RestResponse add(IncomingWaterForecastAddReq req) {
        try {
            IncomingWaterForecast incomingWaterForecast = req.getIncomingWaterForecast();
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
            incomingWaterForecast.setStatus(1);
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
                    incomingWaterForecastService.lambdaUpdate().set(IncomingWaterForecast::getStatus,2).
                            set(IncomingWaterForecast::getModelResultAddress,object).
                            eq(IncomingWaterForecast::getId,incomingWaterForecast.getId()).update();
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
    public RestResponse delete(String ids) {
        try {
            boolean b = this.removeBatchByIds(Arrays.stream(ids.split(",")).collect(Collectors.toList()));
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
    @Transactional(rollbackFor = Exception.class)
    public RestResponse update(IncomingWaterForecast incomingWaterForecast) {
        try {
            //(1-月 2-旬 3-日 4-小时)
            if(incomingWaterForecast.getPeriodTimeType()==1){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.MONTH,incomingWaterForecast.getPeriodTimeStep()*incomingWaterForecast.getPeriodTimeNum());
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==2){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep()*incomingWaterForecast.getPeriodTimeNum()*10);
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==3){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.DATE,incomingWaterForecast.getPeriodTimeStep()*incomingWaterForecast.getPeriodTimeNum());
                Date targetDate = calendar.getTime();
                incomingWaterForecast.setEndTime(targetDate);
            }
            if(incomingWaterForecast.getPeriodTimeType()==4){
                Date predictionTime = incomingWaterForecast.getPredictionTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(predictionTime);
                calendar.add(Calendar.HOUR,incomingWaterForecast.getPeriodTimeStep()*incomingWaterForecast.getPeriodTimeNum());
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
            IPage<IncomingWaterForecast> page = this.lambdaQuery().like(StringUtils.isNotEmpty(req.getProgrammeName()), IncomingWaterForecast::getProgrammeName, req.getProgrammeName()).
                    eq(req.getPeriodTimeType() != null, IncomingWaterForecast::getPeriodTimeType, req.getPeriodTimeType()).
                    like(StringUtils.isNotEmpty(req.getCreateBy()),IncomingWaterForecast::getCreateBy,req.getCreateBy()).
                    eq(req.getModelType() != null, IncomingWaterForecast::getModelType,req.getModelType()).
                    eq(req.getStatus() != null, IncomingWaterForecast::getStatus,req.getStatus()).
                    like(req.getPredictionTime() != null, IncomingWaterForecast::getPredictionTime, req.getPredictionTime()==null?null:sdf1.format(req.getPredictionTime())).orderByDesc(IncomingWaterForecast::getCreateTime).
                    page(incomingWaterForecastPage);
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
                    List<Object> viewForFourPredictions = new LinkedList<>();
                    IncomingWaterForecastViewDto threeBridge = getIncomingWaterForecastViewDto(floods, "3号桥");
                    threeBridge.setSort(1);
                    if(null != threeBridge){
                        threeBridge.setName("3号桥");
                        viewForFourPredictions.add(JSONObject.parseObject(JSONObject.toJSONString(threeBridge)));
                        view.put("3号桥",threeBridge);
                    }else {
                        view.put("3号桥",null);
                    }
                    IncomingWaterForecastViewDto lzzEntryStation = getIncomingWaterForecastViewDto(floods, "楼庄子");
                    lzzEntryStation.setSort(2);
                    if(null != lzzEntryStation){
                        lzzEntryStation.setName("楼庄子");
                        viewForFourPredictions.add(JSONObject.parseObject(JSONObject.toJSONString(lzzEntryStation)));
                        view.put("楼庄子",lzzEntryStation);
                    }else {
                        view.put("楼庄子",null);
                    }
                    IncomingWaterForecastViewDto tthEntryStation = getIncomingWaterForecastViewDto(floods, "楼头区间");
                    tthEntryStation.setSort(3);
                    if(null != tthEntryStation){
                        tthEntryStation.setName("楼头区间");
                        viewForFourPredictions.add(JSONObject.parseObject(JSONObject.toJSONString(tthEntryStation)));
                        view.put("楼头区间",tthEntryStation);
                    }else {
                        view.put("楼头区间",null);
                    }
                    IncomingWaterForecastViewDto tREntryStation = getIncomingWaterForecastViewDto(floods, "头屯河");
                    tREntryStation.setSort(4);
                    if(null != tREntryStation){
                        tREntryStation.setName("头屯河");
                        viewForFourPredictions.add(JSONObject.parseObject(JSONObject.toJSONString(tREntryStation)));
                        view.put("头屯河",tREntryStation);
                    }else {
                        view.put("头屯河",null);
                    }
                    res.setView(view);
                    res.setViewForFourPredictions(viewForFourPredictions);
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

    @Override
    public List<IncomingWaterForecast> getPredictionListByTimeType(Integer timeType) {
        if(timeType==1){
            List<IncomingWaterForecast> predictionListForYear = this.baseMapper.getPredictionListForYear();
            return predictionListForYear;
        }
        if(timeType==2){
            List<IncomingWaterForecast> predictionListForMonth = this.baseMapper.getPredictionListForMonth();
            return predictionListForMonth;
        }
        if(timeType==3){
            List<IncomingWaterForecast> predictionListForTenDays = this.baseMapper.getPredictionListForTenDays();
            return predictionListForTenDays;
        }
        if(timeType==4){
            List<IncomingWaterForecast> predictionListForDay = this.baseMapper.getPredictionListForDay();
            return predictionListForDay;
        }
        return null;
    }

    @Override
    public Map<String, Object> getPredictionListByName(String id, String reservoir) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        try {
            Map<String, Object> result = new HashMap<>();
            RestResponse<IncomingWaterForecastDetailsRes> incomingWaterForecastDetailsResRestResponse = this.selectDetails(id);
            if(incomingWaterForecastDetailsResRestResponse.getCode()==200){
                IncomingWaterForecast incomingWaterForecast = this.getById(id);
                IncomingWaterForecastViewDto incomingWaterForecastViewDto = incomingWaterForecastDetailsResRestResponse.getData().getView().get(reservoir);
                List<PredictionDto> predictionProcess = incomingWaterForecastViewDto.getPredictionProcess();
                if(null!= predictionProcess && predictionProcess.size()>0){
                    Integer year = LocalDateTime.now().getYear();
                    Integer monthTemp = LocalDateTime.now().getMonth().getValue();
                    String month  = monthTemp.toString().length()==2?monthTemp.toString():"0"+monthTemp;
                    Integer day = LocalDateTime.now().getDayOfMonth();
                    List<ForecastPredictionDto> list = new ArrayList<>();
                    if(incomingWaterForecast.getPeriodTimeType()==2){
                        List<PredictionDto> collect = predictionProcess.stream().filter(t -> sdf1.format(t.getTime()).contains(year+"-"+month)).collect(Collectors.toList());
                        for(PredictionDto dto:collect){
                            ForecastPredictionDto forecastPredictionDto = new ForecastPredictionDto();
                            forecastPredictionDto.setTime(sdf.format(dto.getTime()));
                            forecastPredictionDto.setWaterAmount(dto.getFloodVolume());
                            list.add(forecastPredictionDto);
                        }
                    }else if(incomingWaterForecast.getPeriodTimeType()==3) {
                        Map<String, String> tenDaysTime = getTenDaysTime(day);
                        Date startTime = sdf1.parse(year + "-" + month + "-" + tenDaysTime.get("start"));
                        Date endTime = sdf1.parse(year + "-" + month + "-" + tenDaysTime.get("end"));
                        List<PredictionDto> collect = predictionProcess.stream().filter(t -> (t.getTime().compareTo(startTime) > 0 && t.getTime().compareTo(endTime) < 0)|| t.getTime().compareTo(endTime) == 0).collect(Collectors.toList());
                        for(PredictionDto dto:collect){
                            ForecastPredictionDto forecastPredictionDto = new ForecastPredictionDto();
                            forecastPredictionDto.setTime(sdf.format(dto.getTime()));
                            forecastPredictionDto.setWaterAmount(dto.getFloodVolume());
                            list.add(forecastPredictionDto);
                        }
                    }else {
                        for(PredictionDto dto:predictionProcess){
                            ForecastPredictionDto forecastPredictionDto = new ForecastPredictionDto();
                            forecastPredictionDto.setTime(sdf.format(dto.getTime()));
                            forecastPredictionDto.setWaterAmount(dto.getFloodVolume());
                            list.add(forecastPredictionDto);
                        }
                    }
                    Double aDouble = list.stream().map(ForecastPredictionDto::getWaterAmount).reduce(Double::sum).orElse(0.00);
                    result.put("list", list);
                    result.put("amount", decimalFormat.format(aDouble));
                    return result;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
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
            List<PredictionDto> predictionProcess = new ArrayList<>();
            for (Flood flood:threeBridge){
                PredictionDto predictionProcessDto = new PredictionDto();
                predictionProcessDto.setPreQ(flood.getPreQ());
                predictionProcessDto.setTime(flood.getTime());
                predictionProcessDto.setWaterLevel(flood.getWaterLevel());
                predictionProcessDto.setOutQ(flood.getOutQ());
                predictionProcessDto.setFloodVolume(flood.getFloodVolume());
                predictionProcess.add(predictionProcessDto);
            }
            incomingWaterForecastViewDto.setPredictionProcess(predictionProcess);
            Flood flood = threeBridge.get(0);
            List<IncomingWaterForecastKVDto> qCause = new ArrayList<>();
            String qCauseValue = flood.getQCause();
            if(StringUtils.isNotEmpty(qCauseValue)){
                String[] qCauseSplit = qCauseValue.split(",");
                for(String qCauseSplitTemp : qCauseSplit){
                    IncomingWaterForecastKVDto dto = new IncomingWaterForecastKVDto();
                    String[] split2 = qCauseSplitTemp.split(":");
                    dto.setName(split2[0]);
                    dto.setValue(Double.parseDouble(split2[1]));
                    qCause.add(dto);
                }
            }
            incomingWaterForecastViewDto.setQCause(qCause);
            List<IncomingWaterForecastKVDto> qComposition = new ArrayList<>();
            String qCompositionValue = flood.getQComposition();
            if(StringUtils.isNotEmpty(qCompositionValue)){
                String[] qCompositionSplit = qCompositionValue.split(",");
                for(String qCompositionSplitTemp : qCompositionSplit){
                    IncomingWaterForecastKVDto dto = new IncomingWaterForecastKVDto();
                    String[] split2 = qCompositionSplitTemp.split(":");
                    dto.setName(split2[0]);
                    dto.setValue(Double.parseDouble(split2[1]));
                    qComposition.add(dto);
                }
            }
            incomingWaterForecastViewDto.setQComposition(qComposition);
            List<Flood> collect = threeBridge.stream().filter(f -> f.getPeakIndex() == 1).collect(Collectors.toList());
            if(null != collect && collect.size() > 0){
                Flood flood1 = collect.get(0);
                incomingWaterForecastViewDto.setPeakFlood(flood1.getPeakFlood());
                incomingWaterForecastViewDto.setPeakVolume(flood1.getFloodVolume());
            }else {
                incomingWaterForecastViewDto.setPeakFlood(null);
                incomingWaterForecastViewDto.setPeakVolume(null);
            }
            return incomingWaterForecastViewDto;
        }else {
            return null;
        }
    }

    public Map<String,String> getTenDaysTime(Integer day){
        Map<String,String> result = new HashMap<>();
        if(day<=10){
            result.put("start","01");
            result.put("end","10");
            return result;
        }
        if(day<=20){
            result.put("start","11");
            result.put("end","20");
            return result;
        }
        if(day>20){
            // 获取当前月份
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            // 获取当前月份的天数
            Integer daysInMonth = getDaysInMonth(year, month);
            result.put("start","21");
            result.put("end",daysInMonth.toString());
            return result;
        }
        return null;
    }
    public static int getDaysInMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1); // 将日期设置为当前月份的第一天
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return daysInMonth;
    }

    @Override
    public RestResponse<List<IncomingWaterForecast>> selectListByTime(WaterResourceAllocationTimeReq req) {
        return RestResponse.ok(this.lambdaQuery().le(IncomingWaterForecast::getPredictionTime, req.getStartTime())
                .eq(req.getBucketType().equals(1), IncomingWaterForecast::getModelType, 1)//中长期
                .eq(req.getBucketType().equals(1), IncomingWaterForecast::getPeriodTimeType, 1)//月

                .eq(req.getBucketType().equals(2), IncomingWaterForecast::getModelType, 1)//中长期
                .eq(req.getBucketType().equals(2), IncomingWaterForecast::getPeriodTimeType, 2)//旬

                .eq(req.getBucketType().equals(3) || req.getBucketType().equals(4), IncomingWaterForecast::getModelType, 2)//短期

                .apply("case period_time_type \n" +
                        "when 1 then ADD_MONTHS(PREDICTION_TIME, PERIOD_TIME_STEP * PERIOD_TIME_NUM)\n" +
                        "when 2 then \n" +
                        "ADD_DAYS(ADD_MONTHS(PREDICTION_TIME, PERIOD_TIME_STEP * PERIOD_TIME_NUM / 3), PERIOD_TIME_STEP * PERIOD_TIME_NUM % 3 * 10)\n" +
                        "when 3 then ADD_DAYS(PREDICTION_TIME, PERIOD_TIME_STEP * PERIOD_TIME_NUM)\n" +
                        "when 4 then DATEADD(HH, PERIOD_TIME_STEP * PERIOD_TIME_NUM, PREDICTION_TIME)\n" +
                        "end >= to_date({0}, 'yyyy-mm-dd hh24:mi:ss')", sdf.format(req.getEndTime()))
                .list());
    }
}




