package com.cj.flood.func.modular.prediction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.flood.func.modular.prediction.bean.req.ModelParametersReq;
import com.cj.flood.func.modular.prediction.mapper.ModelParametersMapper;
import com.cj.flood.func.modular.prediction.entity.ModelParameters;
import com.cj.flood.func.modular.prediction.service.ModelParametersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



/**
 * 陕北模型参数(ModelParameters)表服务实现类
 *
 * @author makejava
 * @since 2024-03-13 12:27:10
 */
@Service
@Slf4j
public class ModelParametersServiceImpl extends ServiceImpl<ModelParametersMapper, ModelParameters> implements ModelParametersService {

   /* private ShanBeiCalibration shanBeiCalibration;
    @Autowired
    private LzzGaugingStationService lzzGaugingStationService;
    @Autowired
    private LzzRainfallStationService lzzRainfallStationService;

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    public List<ModelParameters> queryList(ModelParametersReq input) {
        List<ModelParameters> parametersList = this.lambdaQuery()
                .like(!input.getSiteName().isEmpty(), ModelParameters::getSiteName, input.getSiteName())
                .between(input.getStartTime() != null, ModelParameters::getDate, input.getStartTime(), input.getEndTime())
                .list();
        return parametersList;

    }

    @Override
    @SneakyThrows
    public CalibrationOutput calibrate(CalibrationParam input) {
        // 获取当前日期的Calendar实例
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(input.getStartTime());
        // 设置日期为当前日期的前20天
        calendar.add(Calendar.DAY_OF_MONTH, -20);
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

        input.setLzzHydrologyParam(lzzHydrologyParam);
        input.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
        CalibrationOutput calibrationOutput = shanBeiCalibration.calibration(input);
        if (calibrationOutput.getParam().getQC() > 0.6) {
            ModelParameters modelParameters = new ModelParameters();

        }

        return shanBeiCalibration.calibration(input);
    }*/
}

