package com.cj.flood.func.modular.prediction.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.flood.func.modular.prediction.bean.dto.OverallSituationUnitMgrDto;
import com.cj.flood.func.modular.prediction.bean.req.CalibrateReq;
import com.cj.flood.func.modular.prediction.bean.req.ModelParameterDetailReq;
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

import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.entity.RainFallDto;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


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

    private ShanBeiCalibration shanBeiCalibration = new ShanBeiCalibration();
    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;
    @Autowired
    private ModelParametersDetailService modelParametersDetailService;
    @Autowired
    private ModelParametersService modelParametersService;

    @Value("${model.flood.lzzInputStationName:天谷自动水位站}")
    private String lzzInputStationName;

    @Autowired
    private RedisUtil redisUtil;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    TimeUtils tu = new TimeUtils();

    public Map<String, List<ModelParameters>> queryList() {
        List<ModelParameters> parametersList = this.lambdaQuery()
//                .like(!input.getSiteName().isEmpty(), ModelParameters::getSiteName, input.getSiteName())
//                .between(input.getStartTime() != null, ModelParameters::getDate, input.getStartTime(), input.getEndTime())
                .list();
        return parametersList.stream().collect(Collectors.groupingByConcurrent(ModelParameters::getSiteName));

    }

    @Override
    public List queryDefaultList() {
        return this.lambdaQuery().eq(ModelParameters::getIsDefault, 1).list();
    }

    @SneakyThrows
    @Override
    @Transactional
    public List calibrate(CalibrateReq input) {
        // 获取当前日期的Calendar实例
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(input.getStartTime());
        // 设置日期为当前日期的前20天
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        // 获取设置后的时间
        Date startTime = calendar.getTime();
        Date endTime = input.getEndTime();

        //固定或者默认模型参数
//        input.getHistoryParam();
        //修改后的参数
//        input.getManualParam();
        Map<String, ShanbeiParam> manualParam = new HashMap<>();
        List<String> ids = new ArrayList<>();
        input.getParametersList().forEach(r -> {
            setShanbeiParam(manualParam, r);
            ids.add(r.getId());
        });
        List<ModelParameters> historyList = this.baseMapper.selectBatchIds(ids);
        Map<String, ShanbeiParam> historyParam = new HashMap<>();
        historyList.forEach(r -> setShanbeiParam(historyParam, r));

        CalibrationParam calibrationParam = new CalibrationParam();
        calibrationParam.setIsAutomatic(input.getIsAutomatic());
        calibrationParam.setStartTime(input.getStartTime());
        calibrationParam.setEndTime(input.getEndTime());
        //固定或者默认模型参数
        calibrationParam.setHistoryParam(historyParam);
        //修改后的参数
        calibrationParam.setManualParam(manualParam);
        calibrationParam.setRainfall(setRainfallData(startTime, endTime));
        calibrationParam.setFlowData(setWaterLevelData(startTime, endTime));
        Map<String, CalibrationOutput> calibrationOutput = shanBeiCalibration.calibration(calibrationParam);
        //Assert.isTrue(!validError(calibrationOutput), "参数率定模型调用返回异常,请检查后重试");
        Date now = new Date();
        List modelList = new ArrayList<>();
        calibrationOutput.forEach((key, value) -> {
            if (StringUtils.hasText(value.getError())) {
                modelList.add(new HashMap<String, String>() {{
                    put("siteName", key);
                    put("error", value.getError());}}
                );
                return;
            }
            ModelParameters modelParameters = null;
//            ModelParameters modelParameters = ModelParameters.builder()
//                    .id(UUID.randomUUID().toString())
//                    .siteName(key)
//                    .area(value.getParam().getArea())
//                    .b(value.getParam().getB())
//                    .cs(value.getParam().getCS())
//                    .date(now)
//                    .kc(value.getParam().getKC())
//                    .l(value.getParam().getL())
//                    .fc(value.getParam().getFC())
//                    .fm(value.getParam().getFM())
//                    .k(value.getParam().getK())
//                    .fb(value.getParam().getFB())
//                    .wm(value.getParam().getWM())
//                    .rate(value.getParam().getQC())
//                    .isDefault(0)
//                    .fromId(input.getParametersList().stream().filter(n -> n.getSiteName().equals(key)).findFirst().orElse(ModelParameters.builder().build()).getId())
//                    .build();
            List<ModelParametersDetail> detailList = new ArrayList<>();
            for (int i = 0; i < value.getFlowList().size(); i++) {
                ModelParametersDetail detail = ModelParametersDetail.builder()
                        .id(UUID.randomUUID().toString())
                        .parentId(modelParameters.getId())
                        .time(value.getFlowList().get(i).getTime())
                        .historyFlow(value.getFlowList().get(i).getHistoryFlow())
                        .preParamFlow(value.getFlowList().get(i).getPreParamFlow())
                        .newParamFlow(value.getFlowList().get(i).getNewParamFlow())
                        .preParamRainfall(value.getFlowList().get(i).getHistoryRainfall())
                        .build();
                detailList.add(detail);
            }
            modelList.add(modelParameters);
            modelParametersService.save(modelParameters);
            modelParametersDetailService.saveBatch(detailList);
        });
        return modelList;
    }

    private void setShanbeiParam(Map<String, ShanbeiParam> param, ModelParameters r) {
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
        param.put(r.getSiteName(), shanbeiParam);
    }

    private Map<String,List<RainFallDto>> setRainfallData(Date startTime, Date endTime) {
        Map<String,List<RainFallDto>> rainfall = new HashMap<>();
        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
        List<OverallSituationUnitMgrDto> overallSituationUnitMgrDtoList = JSONObject.parseArray(overall, OverallSituationUnitMgrDto.class);
        List<OverallSituationUnitMgrDto> collect = overallSituationUnitMgrDtoList.stream().filter(t -> t.getPName().equals("雨量站")).collect(Collectors.toList());
        List<String> tthIds = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 1 && org.apache.commons.lang3.StringUtils.isNotEmpty(t.getMonitorId())).map(OverallSituationUnitMgrDto::getMonitorId).collect(Collectors.toList());
        List<String> lzzIds = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 2 && org.apache.commons.lang3.StringUtils.isNotEmpty(t.getMonitorId())).map(OverallSituationUnitMgrDto::getMonitorId).collect(Collectors.toList());
        for(String id:lzzIds){
            List<LzzRainfallStation> lzzRainfallStations = lzzRainfallStationService.selectInfoByCondition(id, null, sdf.format(startTime), sdf.format(endTime));
            if (!lzzRainfallStations.isEmpty()) {
                List<RainFallDto> rainfallDtos = new ArrayList<>();
                for(LzzRainfallStation lzzRainfallStation:lzzRainfallStations){
                    RainFallDto rainfallDto = new RainFallDto();
                    rainfallDto.setRainFall(lzzRainfallStation.getRainfall().doubleValue());
                    rainfallDto.setTemperature(lzzRainfallStation.getTemperature().doubleValue());
                    rainfallDto.setDate(sdf.format(lzzRainfallStation.getTime()));
                    rainfallDto.setArea(lzzRainfallStations.get(0).getStationName());
                    rainfallDtos.add(rainfallDto);
                }
                rainfall.put(lzzRainfallStations.get(0).getStationName(),rainfallDtos);
            }
        }
        for(String id:tthIds){
            List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = irrigatedPlatformDataInfoService.selectInfoByCondition(id, null, sdf.format(startTime), sdf.format(endTime));
            if (!irrigatedPlatformDataInfos.isEmpty()) {
                List<RainFallDto> rainfallDtos = new ArrayList<>();
                for(IrrigatedPlatformDataInfo irrigatedPlatformDataInfo:irrigatedPlatformDataInfos){
                    RainFallDto rainfallDto = new RainFallDto();
                    rainfallDto.setRainFall(irrigatedPlatformDataInfo.getYqRainFallOne());
                    rainfallDto.setDate(sdf.format(irrigatedPlatformDataInfo.getMonitorTime()));
                    rainfallDto.setArea(irrigatedPlatformDataInfos.get(0).getMonitorName());
                    rainfallDtos.add(rainfallDto);
                }
                rainfall.put(irrigatedPlatformDataInfos.get(0).getMonitorName(),rainfallDtos);
            }
        }
        return rainfall;
    }

    private Map<String,List<PredictInputData>> setWaterLevelData(Date startTime, Date endTime) {
        Map<String,List<PredictInputData>> waterLevel = new HashMap<>();
        List<LzzGaugingStation> threeStation = lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"3号桥水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list();
        waterLevel.put("3号桥", setWaterStationData(threeStation));
        List<LzzGaugingStation> tianStation = lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"天谷自动水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list();
        waterLevel.put("楼庄子进库", setWaterStationData(tianStation));
        List<LzzGaugingStation> lzzOutStation = lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子出库水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list();
        waterLevel.put("楼庄子出库", setWaterStationData(lzzOutStation));
        List<IrrigatedPlatformDataInfo> tthStation = irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"入库流量").between(IrrigatedPlatformDataInfo::getMonitorTime,startTime,endTime).list();
        waterLevel.put("头屯河进库", setHourWater(setWaterStationIr(tthStation)));
        return waterLevel;
    }

    private List<PredictInputData> setWaterStationData(List<LzzGaugingStation> inputData){
        List<PredictInputData> result = new ArrayList<>();
        for (LzzGaugingStation inputDatum : inputData) {
            PredictInputData data = new PredictInputData();
            data.setDates(inputDatum.getGatherTime());
            data.setLocation(inputDatum.getStationName());
            data.setFlow(inputDatum.getFlow());
            data.setTemperature(inputDatum.getTemperature());
            result.add(data);
        }
        return result;
    }
    private List<PredictInputData> setWaterStationIr(List<IrrigatedPlatformDataInfo> inputData){
        List<PredictInputData> result = new ArrayList<>();
        for (IrrigatedPlatformDataInfo inputDatum : inputData) {
            PredictInputData data = new PredictInputData();
            data.setDates(inputDatum.getMonitorTime());
            data.setLocation(inputDatum.getMonitorName());
            data.setFlow(inputDatum.getSqMonitorFlow());
            result.add(data);
        }
        return result;
    }
    private List<PredictInputData> setHourWater(List<PredictInputData> inputData){
        List<PredictInputData> result = new ArrayList<>();
        for (int i = 1; i < inputData.size(); i++) {
            PredictInputData info = new PredictInputData();
            Boolean isSameHour = tu.DateCompare(inputData.get(i-1).getDates(),inputData.get(i).getDates(),"小时");
            if (!isSameHour){
                info.setDates(inputData.get(i).getDates());
                info.setLocation(inputData.get(i).getLocation());
                info.setFlow(inputData.get(i).getFlow());
            }
        }
        return result;
    }

    private boolean validError(Map<String, CalibrationOutput> calibrationOutput) {
        AtomicBoolean hasErr = new AtomicBoolean(false);
        calibrationOutput.forEach((k, v) -> {
            if (StringUtils.hasText(v.getError())) {
                log.error(k + ":" + v.getError());
                hasErr.set(true);
            }
        });
        return hasErr.get();
    }
    @Override
    @Transactional
    public Boolean del(List<String> input) {
        Integer idDel = this.baseMapper.deleteBatchIds(input);
        for (int i = 0; i < input.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("PARENT_ID",input.get(i));
            modelParametersDetailService.removeByMap(map);
        }
        return idDel > 0;
    }

    @Override
    @Transactional
    public boolean setDefault(ModelParametersReq input) {
        List<ModelParameters> model = this.lambdaQuery().eq(ModelParameters::getSiteName,input.getSiteName())
                .eq(ModelParameters::getIsDefault, 1)
                .list();
        if (!CollectionUtils.isEmpty(model)) {
            model.forEach(n -> n.setIsDefault(0));
            this.updateBatchById(model);
        }
        model = this.lambdaQuery().eq(ModelParameters::getId, input.getId()).list();
        model.forEach(n -> n.setIsDefault(1));
        this.updateBatchById(model);
        return true;
    }

    @Override
    public boolean ls(ModelParametersReq input) {
        // 获取当前日期的Calendar实例
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(input.getStartTime());
        // 设置日期为当前日期的前20天
//        calendar.add(Calendar.DAY_OF_MONTH, -30);
        // 获取设置后的时间
        Date startTime = calendar.getTime();
        Date endTime = input.getEndTime();
/*        LzzHydrologyParam lzzHydrologyParam = new LzzHydrologyParam();
        lzzHydrologyParam.setThreeGaugingStation(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName, "3号桥水位站").between(LzzGaugingStation::getGatherTime, startTime, endTime).list());
        lzzHydrologyParam.setLzzInput(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName, lzzInputStationName).between(LzzGaugingStation::getGatherTime, startTime, endTime).list());

        IrrigatedHydrologyParam irrigatedHydrologyParam = new IrrigatedHydrologyParam();

        irrigatedHydrologyParam.setTthInput(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName, "入库流量").between(IrrigatedPlatformDataInfo::getMonitorTime, startTime, endTime).list());*/
        return false;
    }

    @Override
    public RestResponse paramDetail(ModelParameterDetailReq req) {
        IPage<ModelParametersDetail> page = new Page<>(req.getPageNo(), req.getPageSize());
        return RestResponse.ok(modelParametersDetailService.lambdaQuery().eq(ModelParametersDetail::getParentId, req.getId()).page(page));
    }
}

