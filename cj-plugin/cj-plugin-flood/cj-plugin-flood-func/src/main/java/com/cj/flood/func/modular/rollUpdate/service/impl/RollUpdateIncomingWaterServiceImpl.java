package com.cj.flood.func.modular.rollUpdate.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.func.modular.prediction.bean.dto.*;
import com.cj.flood.func.modular.prediction.bean.res.IncomingWaterForecastDetailsRes;
import com.cj.flood.func.modular.prediction.entity.BasinParam;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.cj.flood.func.modular.prediction.entity.ModelParameters;
import com.cj.flood.func.modular.prediction.mapper.IncomingWaterForecastMapper;
import com.cj.flood.func.modular.prediction.service.IncomingWaterForecastService;
import com.cj.flood.func.modular.prediction.service.ModelParametersService;
import com.cj.flood.func.modular.rollUpdate.entity.ModelRollUpdate;
import com.cj.flood.func.modular.rollUpdate.mapper.RollUpdateIncomingWaterMapper;
import com.cj.flood.func.modular.rollUpdate.entity.RollUpdateIncomingWater;
import com.cj.flood.func.modular.rollUpdate.service.RollUpdateIncomingWaterService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.LzzRainfallStationService;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.model.func.core.util.MultipartFileUtil;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.model.TouTunHe;
import com.cj.model.func.modular.FloodPredict.utils.InputUtils;
import com.cj.model.func.modular.entity.Flood;
import io.minio.ObjectWriteResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 滚动更新来水预报模型结果表(RollUpdateIncomingWater)表服务实现类
 *
 * @author makejava
 * @since 2024-07-19 14:59:57
 */
@Service("rollUpdateIncomingWaterService")
public class RollUpdateIncomingWaterServiceImpl extends ServiceImpl<RollUpdateIncomingWaterMapper, RollUpdateIncomingWater> implements RollUpdateIncomingWaterService {

    @Autowired
    private MinioUtils minioUtils;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${floodModelFilePath}")
    private String  floodModelFilePath;

    @Autowired
    private RedisUtil redisUtil;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

    private BasinParam loadBasinParam() throws IOException {
        InputStream tth = minioUtils.getObject("tth", "tthUseFile/Basin.json");
        String basin = IOUtils.toString(tth, StandardCharsets.UTF_8);
        return JSONObject.parseObject(basin, BasinParam.class);
    }

    private FloodBasin loadFloodBasinParam() throws IOException {
        InputStream tth = minioUtils.getObject("tth", "tthUseFile/FloodBasin.json");
        String basin = IOUtils.toString(tth, StandardCharsets.UTF_8);
        return JSONObject.parseObject(basin, FloodBasin.class);
    }

    @Override
    public String add(Date time, ModelRollUpdate modelRollUpdate, String user) {
        try {
            RollUpdateIncomingWater incomingWaterForecast = new RollUpdateIncomingWater();
            incomingWaterForecast.setId(UUIDUtils.getUUID());
            incomingWaterForecast.setCreateTime(new Date());
            incomingWaterForecast.setProgrammeName(modelRollUpdate.getSchemeName());
            incomingWaterForecast.setModelType(3);
            incomingWaterForecast.setPredictionTime(sdf3.parse(sdf3.format(time)));
            incomingWaterForecast.setPeriodTimeType(4);
            incomingWaterForecast.setPeriodTimeStep(1);
            incomingWaterForecast.setPeriodTimeNum(modelRollUpdate.getPeriodTimeCount());
            incomingWaterForecast.setStatus(1);
            incomingWaterForecast.setRollId(modelRollUpdate.getId());
            incomingWaterForecast.setCreateBy(user);
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
            this.save(incomingWaterForecast);
            ExecutorService executor = new ThreadPoolExecutor(20, 50, 2, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(50), new ThreadPoolExecutor.CallerRunsPolicy());
            Future<Boolean> future = executor.submit(new Callable<Boolean>() {
                private RollUpdateIncomingWaterService incomingWaterForecastService = SpringUtil.getBean(RollUpdateIncomingWaterService.class);
                private LzzGaugingStationService lzzGaugingStationService = SpringUtil.getBean(LzzGaugingStationService.class);
                private LzzRainfallStationService lzzRainfallStationService = SpringUtil.getBean(LzzRainfallStationService.class);
                private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService = SpringUtil.getBean(IrrigatedPlatformDataInfoService.class);

                private IncomingWaterForecastMapper incomingWaterForecastMapper = SpringUtil.getBean(IncomingWaterForecastMapper.class);
                private ModelParametersService modelParametersService = SpringUtil.getBean(ModelParametersService.class);
                public Boolean call() {
                    try {
                        InputUtils.getData(minioUrl+floodModelFilePath);
                        ForecastInputParamNew forcastInputParamNew = new ForecastInputParamNew();
                        forcastInputParamNew.setPredictionTime(incomingWaterForecast.getPredictionTime());
                        forcastInputParamNew.setModelType(incomingWaterForecast.getModelType());
                        forcastInputParamNew.setPeriodTimeNum(incomingWaterForecast.getPeriodTimeNum());
                        forcastInputParamNew.setPeriodTimeStep(incomingWaterForecast.getPeriodTimeStep());
                        forcastInputParamNew.setPeriodTimeType(incomingWaterForecast.getPeriodTimeType());
                        forcastInputParamNew.setIsSimulation(false);
                        forcastInputParamNew.setIsReferenceWater(false);
                        forcastInputParamNew.setPreFlow(0.0);
                        forcastInputParamNew.setPreRainFall(0.0);
                        forcastInputParamNew.setRainFallDtos(new ArrayList<>());
                        List<Date> dates = InputUtils.judgeDate(incomingWaterForecast.getPredictionTime(),incomingWaterForecast.getPeriodTimeNum());
                        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
                        List<OverallSituationUnitMgrDto> overallSituationUnitMgrDtoList = JSONObject.parseArray(overall, OverallSituationUnitMgrDto.class);
                        Map<String,List<RainFallDto>> rainfall = new HashMap<>();
                        Map<String,List<LzzGaugingStation>> waterLevel = new HashMap<>();
                        List<OverallSituationUnitMgrDto> collect = overallSituationUnitMgrDtoList.stream().filter(t -> t.getPName().equals("雨量站")).collect(Collectors.toList());
                        List<String> tthIds = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 1 && StringUtils.isNotEmpty(t.getMonitorId())).map(OverallSituationUnitMgrDto::getMonitorId).collect(Collectors.toList());
                        List<String> lzzIds = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 2 && StringUtils.isNotEmpty(t.getMonitorId())).map(OverallSituationUnitMgrDto::getMonitorId).collect(Collectors.toList());
                        if(dates.isEmpty()){
                            List<PredictInputData> resultListTemp = new ArrayList<>();
                            LocalDateTime now = LocalDateTime.now();
                            int year = now.getYear();
                            String startTime = year+"-01-01 00:00";
                            String endTime = sdf3.format(sdf3.parse(now.toString()));
                            List<PredictInputData> lzz = incomingWaterForecastMapper.selectResultLzzByPrediction(startTime, endTime);
                            lzz.forEach(t->t.setLocation("楼庄子"));
                            List<PredictInputData> tth = incomingWaterForecastMapper.selectResultTthByPrediction(startTime, endTime);
                            tth.forEach(t->t.setLocation("头屯河"));
                            resultListTemp.addAll(lzz);
                            resultListTemp.addAll(tth);
                            for(String id:lzzIds){
                                List<LzzRainfallStation> lzzRainfallStations = lzzRainfallStationService.selectInfoByCondition(id, null, startTime, endTime);
                                if (!lzzRainfallStations.isEmpty()) {
                                    List<RainFallDto> rainfallDtos = new ArrayList<>();
                                    for(LzzRainfallStation lzzRainfallStation:lzzRainfallStations){
                                        RainFallDto rainfallDto = new RainFallDto();
                                        rainfallDto.setRainFall(lzzRainfallStation.getRainfall().doubleValue());
                                        rainfallDto.setTemperature(lzzRainfallStation.getTemperature()==null?0.0:lzzRainfallStation.getTemperature().doubleValue());
                                        rainfallDto.setDate(sdf.format(lzzRainfallStation.getTime()));
                                        rainfallDto.setArea(lzzRainfallStations.get(0).getStationName());
                                        rainfallDtos.add(rainfallDto);
                                    }
                                    Comparator<RainFallDto> realFlowResComparator = Comparator.comparing(RainFallDto::getDate);
                                    //正序
                                    rainfallDtos.sort(realFlowResComparator);
                                    rainfall.put(lzzRainfallStations.get(0).getStationName(),rainfallDtos);
                                }
                            }
                            for(String id:tthIds){
                                List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = irrigatedPlatformDataInfoService.selectInfoByCondition(id, null, startTime, endTime);
                                if (!irrigatedPlatformDataInfos.isEmpty()) {
                                    List<RainFallDto> rainfallDtos = new ArrayList<>();
                                    for(IrrigatedPlatformDataInfo irrigatedPlatformDataInfo:irrigatedPlatformDataInfos){
                                        RainFallDto rainfallDto = new RainFallDto();
                                        rainfallDto.setRainFall(irrigatedPlatformDataInfo.getYqRainFallOne());
                                        rainfallDto.setDate(sdf.format(irrigatedPlatformDataInfo.getMonitorTime()));
                                        rainfallDto.setArea(irrigatedPlatformDataInfos.get(0).getMonitorName());
                                        rainfallDtos.add(rainfallDto);
                                    }
                                    Comparator<RainFallDto> realFlowResComparator = Comparator.comparing(RainFallDto::getDate);
                                    //正序
                                    rainfallDtos.sort(realFlowResComparator);
                                    rainfall.put(irrigatedPlatformDataInfos.get(0).getMonitorName(),rainfallDtos);
                                }
                            }
                            waterLevel.put("3号桥水位站",lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"3号桥水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list());
                            waterLevel.put("天谷自动水位站",lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"天谷自动水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list());
                            forcastInputParamNew.setInflowRunoffs(resultListTemp);
                            forcastInputParamNew.setDataStartTime(sdf.parse("2023-01-01 00:00:00"));
                        }else {
                            List<PredictInputData> resultListTemp = new ArrayList<>();
                            Date startTime = dates.get(0);
                            Date endTime = dates.get(1);
                            List<PredictInputData> lzz = incomingWaterForecastMapper.selectResultLzzByPrediction(sdf3.format(startTime), sdf3.format(endTime));
                            lzz.forEach(t->t.setLocation("楼庄子"));
                            List<PredictInputData> tth = incomingWaterForecastMapper.selectResultTthByPrediction(sdf3.format(startTime), sdf3.format(endTime));
                            tth.forEach(t->t.setLocation("头屯河"));
                            resultListTemp.addAll(lzz);
                            resultListTemp.addAll(tth);
                            for(String id:lzzIds){
                                List<LzzRainfallStation> lzzRainfallStations = lzzRainfallStationService.selectInfoByCondition(id, null, sdf3.format(startTime), sdf3.format(endTime));
                                if (!lzzRainfallStations.isEmpty()) {
                                    List<RainFallDto> rainfallDtos = new ArrayList<>();
                                    for(LzzRainfallStation lzzRainfallStation:lzzRainfallStations){
                                        RainFallDto rainfallDto = new RainFallDto();
                                        rainfallDto.setRainFall(lzzRainfallStation.getRainfall().doubleValue());
                                        rainfallDto.setTemperature(lzzRainfallStation.getTemperature()==null?0.0:lzzRainfallStation.getTemperature().doubleValue());
                                        rainfallDto.setDate(sdf.format(lzzRainfallStation.getTime()));
                                        rainfallDto.setArea(lzzRainfallStations.get(0).getStationName());
                                        rainfallDtos.add(rainfallDto);
                                    }
                                    Comparator<RainFallDto> realFlowResComparator = Comparator.comparing(RainFallDto::getDate);
                                    //正序
                                    rainfallDtos.sort(realFlowResComparator);
                                    rainfall.put(lzzRainfallStations.get(0).getStationName(),rainfallDtos);
                                }
                            }
                            for(String id:tthIds){
                                List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = irrigatedPlatformDataInfoService.selectInfoByCondition(id, null, sdf3.format(startTime), sdf3.format(endTime));
                                if (!irrigatedPlatformDataInfos.isEmpty()) {
                                    List<RainFallDto> rainfallDtos = new ArrayList<>();
                                    for(IrrigatedPlatformDataInfo irrigatedPlatformDataInfo:irrigatedPlatformDataInfos){
                                        RainFallDto rainfallDto = new RainFallDto();
                                        rainfallDto.setRainFall(irrigatedPlatformDataInfo.getYqRainFallOne());
                                        rainfallDto.setDate(sdf.format(irrigatedPlatformDataInfo.getMonitorTime()));
                                        rainfallDto.setArea(irrigatedPlatformDataInfos.get(0).getMonitorName());
                                        rainfallDtos.add(rainfallDto);
                                    }
                                    Comparator<RainFallDto> realFlowResComparator = Comparator.comparing(RainFallDto::getDate);
                                    //正序
                                    rainfallDtos.sort(realFlowResComparator);
                                    rainfall.put(irrigatedPlatformDataInfos.get(0).getMonitorName(),rainfallDtos);
                                }
                            }
                            waterLevel.put("3号桥水位站",lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"3号桥水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list());
                            waterLevel.put("天谷自动水位站",lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"天谷自动水位站").between(LzzGaugingStation::getGatherTime,startTime,endTime).list());
                            forcastInputParamNew.setInflowRunoffs(resultListTemp);
                            forcastInputParamNew.setDataStartTime(startTime);
                        }
                        forcastInputParamNew.setWaterLevel(waterLevel);
                        forcastInputParamNew.setRainfall(rainfall);
                        //调用模型方法生成模型结果，更新到数据库
                        Map<String, Map<String, ShanbeiParam>> paramMap =  new HashMap<>();
                        List<ModelParameters> defaultParamList = modelParametersService.lambdaQuery().eq(ModelParameters::getIsDefault, 1).list();
                        List<String> siteList = defaultParamList.stream().map(ModelParameters::getSiteName).distinct().collect(Collectors.toList());
                        siteList.forEach(site -> {
                            Map<String, ShanbeiParam> shanbeiParamMap = new HashMap<>();
                            defaultParamList.stream().filter(n -> n.getSiteName().equals(site))
                                    .forEach(param -> shanbeiParamMap
                                            .put(param.getRainfallStation(), new ShanbeiParam(){{
                                                setArea(param.getArea());
                                                setFC(param.getFc());
                                                setFM(param.getFm());
                                                setFB(param.getFb());
                                                setCS(param.getCs());
                                                setKC(param.getKc());
                                                setWM(param.getWm());
                                                setFM(param.getFm());
                                                setK(param.getK());
                                                setB(param.getB());
                                                setL(param.getL());
                                            }}));
                            paramMap.put(site, shanbeiParamMap);
                        });
                        forcastInputParamNew.setParamMap(paramMap);
                        forcastInputParamNew.setBasinStr(JSONObject.toJSONString(loadBasinParam()));
                        forcastInputParamNew.setFloodBasin(loadFloodBasinParam());
                        TemporaryXlsx floodList = new TouTunHe().getFloodList(forcastInputParamNew);
                        //生成模型结果文件
                        String fileAddress = floodList.getPath();
                        String[] split = fileAddress.split("\\\\");
                        Date date = new Date();
                        String yyyyMMdd = DateUtil.format(date, "yyyyMMdd");
                        String hh = DateUtil.format(date, "HH");
                        String mm = DateUtil.format(date, "mm");
                        String ss = DateUtil.format(date, "ss");
                        if (org.springframework.util.StringUtils.hasText(floodList.getUpdateFilePath())) {
                            minioUtils.putObject("tth", "/tthUseFile/"+getFileName(floodList.getUpdateFilePath()),floodList.getUpdateFilePath());
                        }
                        ObjectWriteResponse objectWriteResponse = minioUtils.putObject("tth", yyyyMMdd+"/"+hh+"/"+mm+"/"+ss+"/"+ UUID.fastUUID().toString(true)+"/"+split[split.length-1], fileAddress);
                        FileUtil.del(fileAddress);
                        String object = objectWriteResponse.object();
                        incomingWaterForecastService.lambdaUpdate().set(RollUpdateIncomingWater::getStatus,2).
                                set(RollUpdateIncomingWater::getModelResultAddress,object).
                                eq(RollUpdateIncomingWater::getId,incomingWaterForecast.getId()).update();
                        return true;
                    }catch (Exception e) {
                        e.printStackTrace();
                        log.error("-------------------------------------------error-------------------------------------------");
                        log.error("报错信息："+getStringBuilder(e).toString());
                        incomingWaterForecastService.lambdaUpdate().set(RollUpdateIncomingWater::getStatus,3).
                                eq(RollUpdateIncomingWater::getId,incomingWaterForecast.getId()).update();
                        return false;
                    }
                }
            });
            // 获取结果
            Boolean res = future.get();
            if(res){
                return incomingWaterForecast.getId();
            }else {
                throw new RuntimeException("生成来水预报模型失败");
            }
        }catch (Exception e) {
            log.error("生成模型结果错误:"+e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("生成来水预报模型失败");
        }
    }

    @Override
    public IncomingWaterForecastDetailsRes selectDetails(String id) {
        log.error("--------------------------------进入查询模型详情接口");
        try {
            IncomingWaterForecastDetailsRes res = new IncomingWaterForecastDetailsRes();
            RollUpdateIncomingWater incomingWaterForecast = this.getById(id);
            if(null != incomingWaterForecast){
                res.setPredictionTime(incomingWaterForecast.getPredictionTime());
                res.setEndTime(incomingWaterForecast.getEndTime());
                res.setProgrammeName(incomingWaterForecast.getProgrammeName());
                String modelResultAddress = incomingWaterForecast.getModelResultAddress();
                if(StringUtils.isNotEmpty(modelResultAddress)){
                    log.error("--------------------------------从minio前获取InputStream");
                    InputStream tth = minioUtils.getObject("tth", modelResultAddress);
                    log.error("--------------------------------从minio获取InputStream");
                    String[] split = modelResultAddress.split("/");
                    String[] split1 = split[split.length - 1].split("\\.");
                    MultipartFile multipartFile = MultipartFileUtil.inputStreamToMultipartFile(tth, split1[0]);
                    log.error("--------------------------------转换multipartFile");
                    List<Flood> floods = ExcelUtils.importExcel(multipartFile, Flood.class);
                    log.error("--------------------------------转换成list"+floods.toString());
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
                    return res;
                }else {
                    return null;
                }
            }else {
                return null;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
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
            List<PredictionDto> predictionProcess = new ArrayList<>();
            for (Flood flood:threeBridge){
                PredictionDto predictionProcessDto = new PredictionDto();
                predictionProcessDto.setPreQ(flood.getPreQ());
                predictionProcessDto.setTime(flood.getTime());
                predictionProcessDto.setWaterLevel(flood.getWaterLevel());
                predictionProcessDto.setOutQ(flood.getOutQ());
                predictionProcessDto.setFloodVolume(flood.getFloodVolume());
                predictionProcessDto.setRainfall(flood.getRainProcess());
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
            List<ConfluenceTimeDto> confluenceTime = new ArrayList<>();
            String confluenceTimeValue = flood.getConfluenceTime();
            if(StringUtils.isNotEmpty(confluenceTimeValue)){
                String[] confluenceTimeSplit = confluenceTimeValue.split(",");
                for(String qCauseSplitTemp : confluenceTimeSplit){
                    ConfluenceTimeDto dto = new ConfluenceTimeDto();
                    String[] split2 = qCauseSplitTemp.split(":");
                    dto.setName(split2[0]);
                    dto.setValue(split2[1]);
                    confluenceTime.add(dto);
                }
            }
            incomingWaterForecastViewDto.setConfluenceTime(confluenceTime);
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
            List<Date> overAlarmProcess = threeBridge.stream().filter(t ->t.getWarningTime() != null && t.getWarningTime() == 1).map(Flood::getTime).collect(Collectors.toList());
            incomingWaterForecastViewDto.setOverAlarmProcess(overAlarmProcess);
            return incomingWaterForecastViewDto;
        }else {
            return null;
        }
    }


    public static String getFileName(String fileUrl) {
        String[] parts = fileUrl.split("\\\\");
        return parts[parts.length - 1];
    }

    @NotNull
    private static StringBuilder getStringBuilder(Exception e) {
        StringBuilder sb = new StringBuilder();
        Throwable cause = e;
        int deep = 0;
        while (cause != null && deep < 3) {
            sb.append(e.getMessage()+ "\n");
            StackTraceElement[] stackTrace = cause.getStackTrace();
            for (StackTraceElement stack: stackTrace) {
                sb.append(stack.toString());
                sb.append("\n");
            }
            cause = e.getCause();
            deep++;
        }
        return sb;
    }
}

