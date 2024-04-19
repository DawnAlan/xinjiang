package com.cj.flood.func.modular.prediction.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.flood.func.modular.prediction.bean.req.CalibrateReq;
import com.cj.flood.func.modular.prediction.bean.req.ModelParametersReq;
import com.cj.flood.func.modular.prediction.entity.ModelParametersDetail;
import com.cj.flood.func.modular.prediction.mapper.ModelParametersMapper;
import com.cj.flood.func.modular.prediction.entity.ModelParameters;
import com.cj.flood.func.modular.prediction.service.ModelParametersDetailService;
import com.cj.flood.func.modular.prediction.service.ModelParametersService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.LzzRainfallStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.model.func.modular.FloodPredict.Calibration.ShanBeiCalibration;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationOutput;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationParam;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.entity.IrrigatedHydrologyParam;
import com.cj.model.func.modular.FloodPredict.entity.LzzHydrologyParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hpsf.GUID;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 陕北模型参数(ModelParameters)表服务实现类
 *
 * @author makejava
 * @since 2024-03-13 12:27:10
 */
@Service
@Slf4j
public class ModelParametersServiceImpl extends ServiceImpl<ModelParametersMapper, ModelParameters> implements ModelParametersService {



    @Autowired
    private LzzGaugingStationService lzzGaugingStationService;
    @Autowired
    private LzzRainfallStationService lzzRainfallStationService;

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;
    @Autowired
    private ModelParametersDetailService modelParametersDetailService;

    public List<ModelParameters> queryList(ModelParametersReq input) {
        List<ModelParameters> parametersList = this.lambdaQuery()
                .like(!input.getSiteName().isEmpty(), ModelParameters::getSiteName, input.getSiteName())
                .between(input.getStartTime() != null, ModelParameters::getDate, input.getStartTime(), input.getEndTime())
                .list();
        return parametersList;

    }

    @Override
    @SneakyThrows
    public ModelParameters calibrate(CalibrateReq input) {

        // 获取当前日期的Calendar实例
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(input.getStartTime());
        // 设置日期为当前日期的前20天
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        // 获取设置后的时间
        Date startTime = calendar.getTime();
        Date endTime = input.getEndTime();
        LzzHydrologyParam lzzHydrologyParam = new LzzHydrologyParam();
        lzzHydrologyParam.setThreeGaugingStation(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName, "3号桥水位站").between(LzzGaugingStation::getGatherTime, startTime, endTime).list());
        lzzHydrologyParam.setLzzInput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName, "天谷自动水位站").between(LzzGaugingStation::getGatherTime, startTime, endTime).list());
        lzzHydrologyParam.setLzzOutput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName, "楼庄子出库水位站").between(LzzGaugingStation::getGatherTime, startTime, endTime).list());
        lzzHydrologyParam.setLzzWaterLevel(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName, "楼庄子库水位站").between(LzzGaugingStation::getGatherTime, startTime, endTime).list());
        lzzHydrologyParam.setKsgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName, "喀什沟自动雨量站").between(LzzRainfallStation::getTime, startTime, endTime).list());
        lzzHydrologyParam.setHgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName, "黑沟自动雨量站").between(LzzRainfallStation::getTime, startTime, endTime).list());
        lzzHydrologyParam.setMkgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName, "煤矿沟自动雨量站").between(LzzRainfallStation::getTime, startTime, endTime).list());
        lzzHydrologyParam.setWmgRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName, "无名沟自动雨量站").between(LzzRainfallStation::getTime, startTime, endTime).list());
        lzzHydrologyParam.setJpsRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName, "加普沙自动雨量站").between(LzzRainfallStation::getTime, startTime, endTime).list());
        lzzHydrologyParam.setZrdRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName, "宰尔德自动雨量站").between(LzzRainfallStation::getTime, startTime, endTime).list());
        lzzHydrologyParam.setDngRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName, "东南沟自动雨量站").between(LzzRainfallStation::getTime, startTime, endTime).list());
        lzzHydrologyParam.setBylcRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName, "八一林场自动雨量站").between(LzzRainfallStation::getTime, startTime, endTime).list());
        lzzHydrologyParam.setSedwRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName, "萨尔达万自动雨量站").between(LzzRainfallStation::getTime, startTime, endTime).list());
        lzzHydrologyParam.setZccRainfallStation(lzzRainfallStationService.lambdaQuery().eq(LzzRainfallStation::getStationName, "制材厂自动雨量站").between(LzzRainfallStation::getTime, startTime, endTime).list());
        IrrigatedHydrologyParam irrigatedHydrologyParam = new IrrigatedHydrologyParam();
        irrigatedHydrologyParam.setXqzGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName, "小渠子雨量站").between(IrrigatedPlatformDataInfo::getMonitorTime, startTime, endTime).list());
        irrigatedHydrologyParam.setTjydGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName, "团结一队雨量站").between(IrrigatedPlatformDataInfo::getMonitorTime, startTime, endTime).list());
        irrigatedHydrologyParam.setTthGaugingStation(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName, "头屯河水库雨量站").between(IrrigatedPlatformDataInfo::getMonitorTime, startTime, endTime).list());
        irrigatedHydrologyParam.setTthInput(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName, "入库流量").between(IrrigatedPlatformDataInfo::getMonitorTime, startTime, endTime).list());
        //固定或者默认模型参数
//        input.getHistoryParam();
        //修改后的参数
//        input.getManualParam();
        Map<String, ShanbeiParam> manualParam = new HashMap<>();
        List<String> ids = new ArrayList<>();
        input.getParametersList().forEach(r -> {
            ShanbeiParam shanbeiParam = new ShanbeiParam();
            shanbeiParam.setArea(r.getArea());
            shanbeiParam.setFM(r.getFm());
            shanbeiParam.setFC(r.getFc());
            shanbeiParam.setFB(r.getFb());
            shanbeiParam.setCS(r.getCs());
            shanbeiParam.setKC(r.getKc());
            shanbeiParam.setK(r.getK());
            shanbeiParam.setFM(r.getFm());
            shanbeiParam.setQC(r.getRate());
            shanbeiParam.setWM(r.getWm());
            shanbeiParam.setB(r.getB());
            shanbeiParam.setL(r.getL());
            manualParam.put(r.getSiteName(), shanbeiParam);
            ids.add(r.getId());
        });
        List<ModelParameters> historyList = this.baseMapper.selectBatchIds(ids);
        Map<String, ShanbeiParam> historyParam = new HashMap<>();
        historyList.forEach(r -> {
            ShanbeiParam shanbeiParam = new ShanbeiParam();
            shanbeiParam.setArea(r.getArea());
            shanbeiParam.setFM(r.getFm());
            shanbeiParam.setFC(r.getFc());
            shanbeiParam.setFB(r.getFb());
            shanbeiParam.setCS(r.getCs());
            shanbeiParam.setKC(r.getKc());
            shanbeiParam.setK(r.getK());
            shanbeiParam.setFM(r.getFm());
            shanbeiParam.setQC(r.getRate());
            shanbeiParam.setWM(r.getWm());
            shanbeiParam.setB(r.getB());
            shanbeiParam.setL(r.getL());
            historyParam.put(r.getSiteName(), shanbeiParam);
        });

        CalibrationParam calibrationParam = new CalibrationParam();
        calibrationParam.setIsAutomatic(input.getIsAutomatic());
        calibrationParam.setStartTime(input.getStartTime());
        calibrationParam.setEndTime(input.getEndTime());
        //固定或者默认模型参数
        calibrationParam.setHistoryParam(historyParam);
        //修改后的参数
        calibrationParam.setManualParam(manualParam);
        calibrationParam.setLzzHydrologyParam(lzzHydrologyParam);
        calibrationParam.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
        ModelParameters model = ModelParameters.builder()
                .id(new GUID().toString())
                .state(1)
                .build();
        this.baseMapper.insert(model);
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.submit(new Runnable() {
            private ShanBeiCalibration shanBeiCalibration = SpringUtil.getBean(ShanBeiCalibration.class);
            @Autowired
            private ModelParametersService modelParametersService = SpringUtil.getBean(ModelParametersService.class);
            @Autowired
            private ModelParametersDetailService modelParametersDetailService = SpringUtil.getBean(ModelParametersDetailService.class);
            @Override
            public void run() {
                try {
                    Map<String, CalibrationOutput> calibrationOutput = shanBeiCalibration.calibration(calibrationParam);
                    calibrationOutput.forEach((key, value) -> {
                        ModelParameters modelParameters = ModelParameters.builder()
                                .id(model.getId())
                                .siteName(key)
                                .area(value.getParam().getArea())
                                .b(value.getParam().getB())
                                .cs(value.getParam().getCS())
                                .date(new Date())
                                .kc(value.getParam().getKC())
                                .l(value.getParam().getL())
                                .fc(value.getParam().getFC())
                                .fm(value.getParam().getFM())
                                .k(value.getParam().getK())
                                .fb(value.getParam().getFB())
                                .wm(value.getParam().getWM())
                                .rate(value.getParam().getQC())
                                .build();
                        List<ModelParametersDetail> detailList = new ArrayList<>();
                        for (int i = 0; i < value.getFlowList().size(); i++) {
                            ModelParametersDetail detail = ModelParametersDetail.builder()
                                    .id(new GUID().toString())
                                    .parentId(modelParameters.getId())
                                    .time(value.getFlowList().get(i).getTime())
                                    .historyFlow(value.getFlowList().get(i).getHistoryFlow())
                                    .preParamFlow(value.getFlowList().get(i).getPreParamFlow())
                                    .newParamFlow(value.getFlowList().get(i).getNewParamFlow())
                                    .build();
                            detailList.add(detail);
                        }
                        modelParametersService.save(modelParameters);
                        modelParametersDetailService.saveBatch(detailList);
                    });
                } catch (Exception e){
                    ModelParameters modelParameters = ModelParameters.builder()
                            .id(model.getId())
                            .state(2)
                            .build();
                    modelParametersService.save(modelParameters);
                }
            }
        });

        return model;
    }

    @Override
    public Boolean del(List<String> input) {
        Integer idDel = this.baseMapper.deleteBatchIds(input);
        for (int i = 0; i < input.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("PARENT_ID",input.get(i));
            modelParametersDetailService.removeByMap(map);
        }
        return idDel > 0 ? true : false;
    }
}

