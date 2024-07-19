package com.cj.flood.func.modular.prediction.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.exception.CommonException;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.func.modular.prediction.bean.dto.*;
import com.cj.flood.func.modular.prediction.bean.req.IncomingWaterForecastAddReq;
import com.cj.flood.func.modular.prediction.bean.req.WaterResourceAllocationTimeReq;
import com.cj.flood.func.modular.prediction.entity.BasinParam;
import com.cj.flood.func.modular.prediction.entity.ModelParameters;
import com.cj.flood.func.modular.prediction.service.ModelParametersService;
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
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.model.TouTunHe;
import com.cj.model.func.modular.FloodPredict.utils.InputUtils;
import com.cj.model.func.modular.entity.Flood;
import io.minio.ObjectWriteResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
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
    public RestResponse<BasinParam> getBasinParam() {
        try {
            return RestResponse.ok(loadBasinParam());
        } catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public RestResponse<FloodBasin> getFloodBasinParam() {
        try {
            return RestResponse.ok(loadFloodBasinParam());
        } catch (Exception e){
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public String autoGenerate(Date time) {
        try {
            SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
            IncomingWaterForecast incomingWaterForecast = new IncomingWaterForecast();
            incomingWaterForecast.setId(UUIDUtils.getUUID());
            incomingWaterForecast.setCreateTime(new Date());
            incomingWaterForecast.setProgrammeName(sdf2.format(time)+"一键来水");
            incomingWaterForecast.setModelType(2);
            incomingWaterForecast.setPredictionTime(sdf3.parse(sdf1.format(time)+" 00:00"));
            incomingWaterForecast.setPeriodTimeType(3);
            incomingWaterForecast.setPeriodTimeStep(1);
            incomingWaterForecast.setPeriodTimeNum(1);
            incomingWaterForecast.setStatus(1);
            incomingWaterForecast.setCreateBy(saBaseLoginUser.getName());
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
                private IncomingWaterForecastService incomingWaterForecastService = SpringUtil.getBean(IncomingWaterForecastService.class);
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
                        Map<String, ShanbeiParam> paramMap =  new HashMap<>();
                        modelParametersService.lambdaQuery().eq(ModelParameters::getIsDefault, 1).list()
                                .forEach(param -> {
                                    paramMap.put(param.getSiteName(), new ShanbeiParam(){{
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
                                    }});
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
                        incomingWaterForecastService.lambdaUpdate().set(IncomingWaterForecast::getStatus,2).
                                set(IncomingWaterForecast::getModelResultAddress,object).
                                eq(IncomingWaterForecast::getId,incomingWaterForecast.getId()).update();
                        return true;
                    }catch (Exception e) {
                        e.printStackTrace();
                        log.error("-------------------------------------------error-------------------------------------------");
                        log.error("报错信息："+getStringBuilder(e).toString());
                        incomingWaterForecastService.lambdaUpdate().set(IncomingWaterForecast::getStatus,3).
                                eq(IncomingWaterForecast::getId,incomingWaterForecast.getId()).update();
                        return false;
                    }
                }
            });
            // 获取结果
            Boolean res = future.get();
            if(res){
                return incomingWaterForecast.getId();
            }else {
                return null;
            }
        }catch (Exception e) {
            log.error("生成模型结果错误:"+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

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

                private IncomingWaterForecastMapper incomingWaterForecastMapper = SpringUtil.getBean(IncomingWaterForecastMapper.class);
                private ModelParametersService modelParametersService = SpringUtil.getBean(ModelParametersService.class);

                @Override
                public void run() {
                    try {
                        InputUtils.getData(minioUrl+floodModelFilePath);
                        ForecastInputParamNew forcastInputParamNew = new ForecastInputParamNew();
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
                        forcastInputParamNew.setIsReferenceWater(req.getIsReferenceWater());
                        forcastInputParamNew.setRainFallDtos(req.getRainFallDtos());
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
                            String startTime = year+"-01-01";
                            String endTime = sdf1.format(sdf1.parse(now.toString()));
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
                            List<PredictInputData> lzz = incomingWaterForecastMapper.selectResultLzzByPrediction(sdf1.format(startTime), sdf1.format(endTime));
                            lzz.forEach(t->t.setLocation("楼庄子"));
                            List<PredictInputData> tth = incomingWaterForecastMapper.selectResultTthByPrediction(sdf1.format(startTime), sdf1.format(endTime));
                            tth.forEach(t->t.setLocation("头屯河"));
                            resultListTemp.addAll(lzz);
                            resultListTemp.addAll(tth);
                            for(String id:lzzIds){
                                List<LzzRainfallStation> lzzRainfallStations = lzzRainfallStationService.selectInfoByCondition(id, null, sdf.format(startTime), sdf1.format(endTime));
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
                                List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = irrigatedPlatformDataInfoService.selectInfoByCondition(id, null, sdf.format(startTime), sdf1.format(endTime));
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
                        //System.out.println("Hello pool");
                        forcastInputParamNew.setBasinStr(JSONObject.toJSONString(req.getBasinParam() == null ? loadBasinParam() : req.getBasinParam()));
                        forcastInputParamNew.setFloodBasin(loadFloodBasinParam());
                        Map<String, ShanbeiParam> paramMap =  new HashMap<>();
                        modelParametersService.lambdaQuery().eq(ModelParameters::getIsDefault, 1).list()
                                .forEach(param -> {
                                    paramMap.put(param.getSiteName(), new ShanbeiParam(){{
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
                                    }});
                                });
                        forcastInputParamNew.setParamMap(paramMap);
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
                            // FileUtil.del(floodList.getUpdateFilePath());
                        }
                        ObjectWriteResponse objectWriteResponse = minioUtils.putObject("tth", yyyyMMdd+"/"+hh+"/"+mm+"/"+ss+"/"+ UUID.fastUUID().toString(true)+"/"+split[split.length-1], fileAddress);
                        FileUtil.del(fileAddress);
                        String object = objectWriteResponse.object();
                        incomingWaterForecastService.lambdaUpdate().set(IncomingWaterForecast::getStatus,2).
                                set(IncomingWaterForecast::getModelResultAddress,object).
                                eq(IncomingWaterForecast::getId,incomingWaterForecast.getId()).update();
                    }catch (Exception e) {
                        e.printStackTrace();
                        log.error("-------------------------------------------error-------------------------------------------");
                        log.error("报错信息："+getStringBuilder(e).toString());
                        //incomingWaterForecastService.lambdaUpdate().eq(IncomingWaterForecast::getId,incomingWaterForecast.getId()).remove();
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
                private ModelParametersService modelParametersService = SpringUtil.getBean(ModelParametersService.class);

                @Override
                public void run() {
                    try {
                        //调用模型方法生成模型结果，更新到数据库
                        ForecastInputParamNew forcastInputParamNew = new ForecastInputParamNew();
                        forcastInputParamNew.setPredictionTime(incomingWaterForecast.getPredictionTime());
                        forcastInputParamNew.setModelType(incomingWaterForecast.getModelType());
                        forcastInputParamNew.setPeriodTimeNum(incomingWaterForecast.getPeriodTimeNum());
                        forcastInputParamNew.setPeriodTimeStep(incomingWaterForecast.getPeriodTimeStep());
                        forcastInputParamNew.setPeriodTimeType(incomingWaterForecast.getPeriodTimeType());
                        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
                        List<OverallSituationUnitMgrDto> overallSituationUnitMgrDtoList = JSONObject.parseArray(overall, OverallSituationUnitMgrDto.class);
                        Map<String,List<RainFallDto>> rainfall = new HashMap<>();
                        Map<String,List<LzzGaugingStation>> waterLevel = new HashMap<>();
                        List<OverallSituationUnitMgrDto> collect = overallSituationUnitMgrDtoList.stream().filter(t -> t.getPName().equals("雨量站")).collect(Collectors.toList());
                        List<String> tthIds = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 1 && StringUtils.isNotEmpty(t.getMonitorId())).map(OverallSituationUnitMgrDto::getMonitorId).collect(Collectors.toList());
                        List<String> lzzIds = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 2 && StringUtils.isNotEmpty(t.getMonitorId())).map(OverallSituationUnitMgrDto::getMonitorId).collect(Collectors.toList());
                        for(String id:lzzIds){
                            List<LzzRainfallStation> lzzRainfallStations = lzzRainfallStationService.selectInfoByCondition(id, null, null, null);
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
                            List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = irrigatedPlatformDataInfoService.selectInfoByCondition(id, null, null, null);
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
                        waterLevel.put("3号桥水位站",lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"3号桥水位站").list());
                        waterLevel.put("天谷自动水位站",lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"天谷自动水位站").list());
                        forcastInputParamNew.setWaterLevel(waterLevel);
                        forcastInputParamNew.setRainfall(rainfall);
                        Map<String, ShanbeiParam> paramMap =  new HashMap<>();
                        modelParametersService.lambdaQuery().eq(ModelParameters::getIsDefault, 1).list()
                                .forEach(param -> {
                                    paramMap.put(param.getSiteName(), new ShanbeiParam(){{
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
                                    }});
                                });
                        forcastInputParamNew.setParamMap(paramMap);
                        forcastInputParamNew.setBasinStr(JSONObject.toJSONString(loadBasinParam()));
                        forcastInputParamNew.setFloodBasin(loadFloodBasinParam());
                        //调用模型方法生成模型结果，更新到数据库
                        TemporaryXlsx floodList = new TouTunHe().getFloodList(forcastInputParamNew);
                        //生成模型结果文件
                        String fileAddress = floodList.getPath();
                        String[] split = fileAddress.split("\\\\");
                        Date date = new Date();
                        String yyyyMMdd = DateUtil.format(date, "yyyyMMdd");
                        String hh = DateUtil.format(date, "HH");
                        String mm = DateUtil.format(date, "mm");
                        String ss = DateUtil.format(date, "ss");
                        ObjectWriteResponse objectWriteResponse = minioUtils.putObject("tth", yyyyMMdd+"/"+hh+"/"+mm+"/"+ss+"/"+ UUID.fastUUID().toString(true)+"/"+split[split.length-1], fileAddress);
                        FileUtil.del(fileAddress);
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
        log.error("--------------------------------进入查询模型详情接口");
        try {
            IncomingWaterForecastDetailsRes res = new IncomingWaterForecastDetailsRes();
            IncomingWaterForecast incomingWaterForecast = this.getById(id);
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
                .ge(IncomingWaterForecast::getEndTime, req.getEndTime())
                .eq(req.getBucketType().equals(1), IncomingWaterForecast::getModelType, 1)//中长期
                .eq(req.getBucketType().equals(1), IncomingWaterForecast::getPeriodTimeType, 1)//月

                .eq(req.getBucketType().equals(2), IncomingWaterForecast::getModelType, 1)//中长期
                .eq(req.getBucketType().equals(2), IncomingWaterForecast::getPeriodTimeType, 2)//旬

                .eq(req.getBucketType().equals(3) || req.getBucketType().equals(4), IncomingWaterForecast::getModelType, 2)//短期

//                .apply("case period_time_type \n" +
//                        "when 1 then ADD_MONTHS(PREDICTION_TIME, PERIOD_TIME_STEP * PERIOD_TIME_NUM)\n" +
//                        "when 2 then \n" +
//                        "ADD_DAYS(ADD_MONTHS(PREDICTION_TIME, PERIOD_TIME_STEP * PERIOD_TIME_NUM / 3), PERIOD_TIME_STEP * PERIOD_TIME_NUM % 3 * 10)\n" +
//                        "when 3 then ADD_DAYS(PREDICTION_TIME, PERIOD_TIME_STEP * PERIOD_TIME_NUM)\n" +
//                        "when 4 then DATEADD(HH, PERIOD_TIME_STEP * PERIOD_TIME_NUM, PREDICTION_TIME)\n" +
//                        "end >= to_date({0}, 'yyyy-mm-dd hh24:mi:ss')", sdf.format(req.getEndTime()))
                .list());
    }

    public static String getFileName(String fileUrl) {
        String[] parts = fileUrl.split("\\\\");
        return parts[parts.length - 1];
    }
}




