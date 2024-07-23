package com.cj.flood.func.modular.prediction.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.exception.CommonException;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.func.modular.prediction.bean.dto.OverallSituationUnitMgrDto;
import com.cj.flood.func.modular.prediction.bean.req.*;
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
import com.cj.model.func.core.util.MinioUtils;
import com.cj.model.func.modular.FloodPredict.Calibration.ShanBeiCalibration;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationOutput;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationParam;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;

import com.cj.model.func.modular.FloodPredict.entity.FloodBasin;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.entity.RainFallDto;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    @Autowired
    private MinioUtils minioUtils;
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

    private FloodBasin loadFloodBasinParam() throws IOException {
        InputStream tth = minioUtils.getObject("tth", "tthUseFile/FloodBasin.json");
        String basin = IOUtils.toString(tth, StandardCharsets.UTF_8);
        return JSONObject.parseObject(basin, FloodBasin.class);
    }

    public Map<String, List<ModelParameters>> queryList(QueryListReq req) {
        return this.lambdaQuery()
                .eq(ModelParameters::getSiteName, req.getSiteName())
                .ge(req.getStartTime() != null, ModelParameters::getDate, req.getStartTime())
                .le(req.getEndTime() != null, ModelParameters::getDate, req.getEndTime())
                .like(StringUtils.hasText(req.getModelName()), ModelParameters::getModelName, req.getModelName())
                .orderBy(true, req.getDateAsc() == 1, ModelParameters::getDate)
                .list()
                .stream().collect(Collectors.groupingBy(n -> n.getModelName() == null ? "" : n.getModelName(), LinkedHashMap::new, Collectors.toList()));

//        IPage<ModelParameters> page = new Page<>(req.getPageNo(), req.getPageSize());
//        return this.lambdaQuery()
//                .eq(ModelParameters::getSiteName, req.getSiteName())
//                .ge(req.getStartTime() != null, ModelParameters::getDate, req.getStartTime())
//                .le(req.getEndTime() != null, ModelParameters::getDate, req.getEndTime())
//                .like(StringUtils.hasText(req.getModelName()), ModelParameters::getModelName, req.getModelName())
//                .page(page);
    }

    @Override
    public Map<String, List<ModelParameters>> queryDefaultList(String siteName) {
        return this.lambdaQuery()
                .eq(ModelParameters::getSiteName, siteName)
                .eq(ModelParameters::getIsDefault, 1)
                .list()
                .stream().collect(Collectors.groupingBy(n -> n.getModelName() == null ? "" : n.getModelName(), LinkedHashMap::new, Collectors.toList()));
    }

    @SneakyThrows
    @Override
    @Transactional
    public Map calibrate(CalibrateReq input) {
        String siteName = input.getParametersList().get(0).getSiteName();
        if (!StringUtils.hasText(input.getModelName())) {
            throw new RuntimeException("模型率定名称不能为空");
        }
        if (this.lambdaQuery().eq(ModelParameters::getSiteName, siteName).eq(ModelParameters::getModelName, input.getModelName()).count() > 0) {
            return new HashMap() {{put("msg", siteName + "已存在\"" + input.getModelName() +"\"模型率定名称");}};
        }

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
        calibrationParam.setTime(input.getTime());
        calibrationParam.setLocation(siteName);
        calibrationParam.setFloodBasin(loadFloodBasinParam());
        //固定或者默认模型参数
        calibrationParam.setHistoryParam(historyParam);
        //修改后的参数
        calibrationParam.setManualParam(manualParam);
        calibrationParam.setRainfall(setRainfallData(input.getTime(),siteName));
        calibrationParam.setFlowData(setWaterLevelData(input.getTime(),siteName));
        Map<String, CalibrationOutput> calibrationOutput = shanBeiCalibration.calibration(calibrationParam);
        //Assert.isTrue(!validError(calibrationOutput), "参数率定模型调用返回异常,请检查后重试");
        Date now = new Date();
        Map<String, Object> modelMap = new HashMap<>();
        CalibrationOutput calibrationOutputValue = calibrationOutput.get(siteName);
        modelMap.put("siteName", siteName);
        modelMap.put("error", calibrationOutputValue.getError());
        modelMap.put("rainfallStation", calibrationOutputValue.getParam());
        List<ModelParameters> modelParametersList = new ArrayList<>();
        String modelId = UUIDUtils.getUUID();
        calibrationOutputValue.getParam().forEach((k, v) -> {
            ModelParameters modelParameters = ModelParameters.builder()
                    .id(UUIDUtils.getUUID())
                    .modelId(modelId)
                    .modelName(input.getModelName())
                    .siteName(siteName)
                    .rainfallStation(k)
                    .area(v.getArea())
                    .b(v.getB())
                    .cs(v.getCS())
                    .date(now)
                    .kc(v.getKC())
                    .l(v.getL())
                    .fc(v.getFC())
                    .fm(v.getFM())
                    .k(v.getK())
                    .fb(v.getFB())
                    .wm(v.getWM())
                    .rate(v.getQC())
                    .isDefault(0)
                    .fromId(input.getParametersList().get(0).getModelId())
                    .timeRegion(JSONObject.toJSONString(input.getTime()))
                    .build();
            modelParametersList.add(modelParameters);
        });
        List<ModelParametersDetail> detailList = new ArrayList<>();
        calibrationOutputValue.getFlowList().forEach(flow -> {
            ModelParametersDetail detail = ModelParametersDetail.builder()
                    .id(UUIDUtils.getUUID())
                    .parentId(modelId)
                    .time(flow.getTime())
                    .historyFlow(flow.getHistoryFlow())
                    .preParamFlow(flow.getPreParamFlow())
                    .newParamFlow(flow.getNewParamFlow())
                    .preParamRainfall(flow.getHistoryRainfall())
                    .build();
            detailList.add(detail);
        });
        modelMap.put("flowList", detailList);
        modelParametersService.saveBatch(modelParametersList);
        modelParametersDetailService.saveBatch(detailList);
        return modelMap;
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
        param.put(r.getRainfallStation(), shanbeiParam);
    }

    private Map<String,List<RainFallDto>> setRainfallData(List<Date[]> time,String location) {
        Map<String,List<RainFallDto>> rainfall = new HashMap<>();
        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
        List<OverallSituationUnitMgrDto> overallSituationUnitMgrDtoList = JSONObject.parseArray(overall, OverallSituationUnitMgrDto.class);
        List<OverallSituationUnitMgrDto> collect = overallSituationUnitMgrDtoList.stream().filter(t -> t.getPName().equals("雨量站")).collect(Collectors.toList());
        if (location.equals("3号桥")||location.equals("楼庄子")){
            List<String> lzzIds = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 2 && org.apache.commons.lang3.StringUtils.isNotEmpty(t.getMonitorId())).map(OverallSituationUnitMgrDto::getMonitorId).collect(Collectors.toList());
            for(String id:lzzIds){
                List<LzzRainfallStation> lzzRainfallStations = new ArrayList<>();
                for (int i = 0; i < time.size(); i++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(time.get(i)[0]);
                    calendar.add(Calendar.DAY_OF_MONTH, -20);
                    Date startTime = calendar.getTime();
                    Date endTime = time.get(i)[1];
                    lzzRainfallStations.addAll(lzzRainfallStationService.selectInfoByCondition(id, null, sdf.format(startTime), sdf.format(endTime)));
                }
                if (!lzzRainfallStations.isEmpty()) {
                    List<RainFallDto> rainfallDtos = new ArrayList<>();
                    for(LzzRainfallStation lzzRainfallStation:lzzRainfallStations){
                        RainFallDto rainfallDto = new RainFallDto();
                        rainfallDto.setRainFall(lzzRainfallStation.getRainfall().doubleValue());
                        rainfallDto.setTemperature(lzzRainfallStation.getTemperature() == null ? 0 : lzzRainfallStation.getTemperature().doubleValue());
                        rainfallDto.setDate(sdf.format(lzzRainfallStation.getTime()));
                        rainfallDto.setArea(lzzRainfallStations.get(0).getStationName());
                        rainfallDtos.add(rainfallDto);
                    }
                    // 按日期去重
                    List<RainFallDto> uniqueRainfallDtos = rainfallDtos.stream()
                            .collect(Collectors.toMap(
                                    RainFallDto::getDate,  // 使用日期作为键
                                    dto -> dto,            // 值保持不变
                                    (existing, replacement) -> existing)) // 如果键重复，保留第一个出现的值
                            .values()
                            .stream()
                            .sorted(Comparator.comparing(RainFallDto::getDate)) // 按日期排序
                            .collect(Collectors.toList());
                    rainfall.put(lzzRainfallStations.get(0).getStationName(), uniqueRainfallDtos);
                }
            }
        }else {
            List<String> tthIds = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 1 && org.apache.commons.lang3.StringUtils.isNotEmpty(t.getMonitorId())).map(OverallSituationUnitMgrDto::getMonitorId).collect(Collectors.toList());
            for(String id:tthIds){
                List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = new ArrayList<>();
                for (int i = 0; i < time.size(); i++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(time.get(i)[0]);
                    calendar.add(Calendar.DAY_OF_MONTH, -20);
                    Date startTime = calendar.getTime();
                    Date endTime = time.get(i)[1];
                    irrigatedPlatformDataInfos.addAll(irrigatedPlatformDataInfoService.selectInfoByCondition(id, null, sdf.format(startTime), sdf.format(endTime)));
                }
                if (!irrigatedPlatformDataInfos.isEmpty()) {
                    List<RainFallDto> rainfallDtos = new ArrayList<>();
                    for(IrrigatedPlatformDataInfo irrigatedPlatformDataInfo:irrigatedPlatformDataInfos){
                        RainFallDto rainfallDto = new RainFallDto();
                        rainfallDto.setRainFall(irrigatedPlatformDataInfo.getYqRainFallOne());
                        rainfallDto.setDate(sdf.format(irrigatedPlatformDataInfo.getMonitorTime()));
                        rainfallDto.setArea(irrigatedPlatformDataInfos.get(0).getMonitorName());
                        rainfallDtos.add(rainfallDto);
                    }
                    // 按日期去重
                    List<RainFallDto> uniqueRainfallDtos = rainfallDtos.stream()
                            .collect(Collectors.toMap(
                                    RainFallDto::getDate,  // 使用日期作为键
                                    dto -> dto,            // 值保持不变
                                    (existing, replacement) -> existing)) // 如果键重复，保留第一个出现的值
                            .values()
                            .stream()
                            .sorted(Comparator.comparing(RainFallDto::getDate)) // 按日期排序
                            .collect(Collectors.toList());
                    rainfall.put(irrigatedPlatformDataInfos.get(0).getMonitorName(), uniqueRainfallDtos);
                }
            }
        }
        return rainfall;
    }

    private Map<String,List<PredictInputData>> setWaterLevelData(List<Date[]> time,String location) {
        Map<String,List<PredictInputData>> waterLevel = new HashMap<>();
        List<LzzGaugingStation> threeStation = new ArrayList<>();
        List<LzzGaugingStation> tianStation = new ArrayList<>();
        List<LzzGaugingStation> lzzOutStation = new ArrayList<>();
        List<IrrigatedPlatformDataInfo> tthStation = new ArrayList<>();
        for (int i = 0; i < time.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time.get(i)[0]);
            calendar.add(Calendar.DAY_OF_MONTH, -20);
            Date startTime = calendar.getTime();
            Date endTime = time.get(i)[1];
            switch (location){
                case "3号桥":
                    threeStation.addAll(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"3号桥水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list());
                    break;
                case "楼庄子":
                    tianStation.addAll(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"天谷自动水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list());
                    break;
                case "头屯河":
                case "楼头区间":
                    lzzOutStation.addAll(lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子出库水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list());
                    tthStation.addAll(irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName,"入库流量").between(IrrigatedPlatformDataInfo::getMonitorTime,startTime,endTime).list());
            }
        }
        waterLevel.put("3号桥", setWaterStationData(threeStation));
        waterLevel.put("楼庄子进库", setWaterStationData(tianStation));
        waterLevel.put("楼庄子出库", setWaterStationData(lzzOutStation));
        waterLevel.put("头屯河进库", setHourWater(setWaterStationIr(tthStation)));
        return waterLevel;
    }

    private List<PredictInputData> setWaterStationData(List<LzzGaugingStation> inputData){
        List<PredictInputData> result = new ArrayList<>();
        for (LzzGaugingStation inputDatum : inputData) {
            if (inputDatum.getFlow()!=null){
                PredictInputData data = new PredictInputData();
                data.setDates(inputDatum.getGatherTime());
                data.setLocation(inputDatum.getStationName());
                data.setFlow(inputDatum.getFlow());
                data.setTemperature(inputDatum.getTemperature());
                result.add(data);
            }
        }
        // 按日期去重
        List<PredictInputData> uniqueRainfallDtos = new ArrayList<>(result.stream()
                .collect(Collectors.toMap(
                        PredictInputData::getDates,  // 使用日期作为键
                        dto -> dto,            // 值保持不变
                        (existing, replacement) -> existing)) // 如果键重复，保留第一个出现的值
                .values()
                .stream()
                .sorted(Comparator.comparing(PredictInputData::getDates)) // 按日期排序
                .collect(Collectors.toList()));
        return uniqueRainfallDtos;
    }
    private List<PredictInputData> setWaterStationIr(List<IrrigatedPlatformDataInfo> inputData){
        List<PredictInputData> result = new ArrayList<>();
        for (IrrigatedPlatformDataInfo inputDatum : inputData) {
            if (inputDatum.getSqMonitorFlow()!=null){
                PredictInputData data = new PredictInputData();
                data.setDates(inputDatum.getMonitorTime());
                data.setLocation(inputDatum.getMonitorName());
                data.setFlow(inputDatum.getSqMonitorFlow());
                result.add(data);
            }
        }
        // 按日期去重
        List<PredictInputData> uniqueRainfallDtos = new ArrayList<>(result.stream()
                .collect(Collectors.toMap(
                        PredictInputData::getDates,  // 使用日期作为键
                        dto -> dto,            // 值保持不变
                        (existing, replacement) -> existing)) // 如果键重复，保留第一个出现的值
                .values()
                .stream()
                .sorted(Comparator.comparing(PredictInputData::getDates)) // 按日期排序
                .collect(Collectors.toList()));
        return uniqueRainfallDtos;
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
        for (int i = 0; i < input.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("MODEL_ID",input.get(i));
            this.removeByMap(map);
        }
        for (int i = 0; i < input.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("PARENT_ID",input.get(i));
            modelParametersDetailService.removeByMap(map);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean setDefault(SetDefaultParametersReq req) {
        List<ModelParameters> model = this.lambdaQuery().eq(ModelParameters::getSiteName, req.getSiteName())
                .eq(ModelParameters::getIsDefault, 1)
                .list();
        if (!CollectionUtils.isEmpty(model)) {
            model.forEach(n -> n.setIsDefault(0));
            this.updateBatchById(model);
        }
        model = this.lambdaQuery().eq(ModelParameters::getModelId, req.getModelId()).list();
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
        return RestResponse.ok(modelParametersDetailService.lambdaQuery().eq(ModelParametersDetail::getParentId, req.getModelId()).page(page));
    }
}

