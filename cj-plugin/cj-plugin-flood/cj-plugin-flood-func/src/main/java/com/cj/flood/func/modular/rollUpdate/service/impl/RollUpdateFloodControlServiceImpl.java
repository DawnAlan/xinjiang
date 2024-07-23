package com.cj.flood.func.modular.rollUpdate.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.feign.WaterSituationClient;
import com.cj.common.feign.entity.DropDown;
import com.cj.common.feign.entity.ExternStations;
import com.cj.common.feign.entity.QuXT;
import com.cj.common.feign.entity.RRs;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.func.modular.dispatch.entity.FloodControlOperation;
import com.cj.flood.func.modular.prediction.bean.dto.PredictionProcessDto;
import com.cj.flood.func.modular.prediction.entity.BasinParam;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.cj.flood.func.modular.prediction.service.IncomingWaterForecastService;
import com.cj.flood.func.modular.rollUpdate.bean.dto.RealTimeEngineeringSituationDataDto;
import com.cj.flood.func.modular.rollUpdate.entity.ModelRollUpdate;
import com.cj.flood.func.modular.rollUpdate.mapper.RollUpdateFloodControlMapper;
import com.cj.flood.func.modular.rollUpdate.entity.RollUpdateFloodControl;
import com.cj.flood.func.modular.rollUpdate.service.RollUpdateFloodControlService;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.model.func.core.util.MultipartFileUtil;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqCurve;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.res.ResOption;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.FloodPrevent.entity.DataFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import com.cj.model.func.modular.FloodPrevent.function.Cascade;
import com.cj.model.func.modular.FloodPrevent.function.RollUpdate;
import com.cj.model.func.modular.entity.Flood;
import com.cj.waterresources.api.WaterResourceApi;
import io.minio.ObjectWriteResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * (RollUpdateFloodControl)表服务实现类
 *
 * @author makejava
 * @since 2024-07-19 14:59:38
 */
@Service("rollUpdateFloodControlService")
public class RollUpdateFloodControlServiceImpl extends ServiceImpl<RollUpdateFloodControlMapper, RollUpdateFloodControl> implements RollUpdateFloodControlService {

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private IncomingWaterForecastService incomingWaterForecastService;

    @Autowired
    private WaterSituationClient waterSituationClient;

    @Autowired
    private WaterResourceApi waterResourceApi;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private BasinParam loadBasinParam() throws IOException {
        InputStream tth = minioUtils.getObject("tth", "tthUseFile/Basin.json");
        String basin = IOUtils.toString(tth, StandardCharsets.UTF_8);
        return JSONObject.parseObject(basin, BasinParam.class);
    }

    @SneakyThrows
    @Override
    public void add(String incomingWaterId, ModelRollUpdate modelRollUpdate) {
        IncomingWaterForecast incomingWaterForecast = incomingWaterForecastService.getById(incomingWaterId);
        RollUpdateFloodControl rollUpdateFloodControl = new RollUpdateFloodControl();
        rollUpdateFloodControl.setId(UUIDUtils.getUUID());
        rollUpdateFloodControl.setCreateTime(new Date());
        rollUpdateFloodControl.setSchemeName(incomingWaterForecast.getProgrammeName()+"-"+(modelRollUpdate.getSchedulingScheme().equals("1")?"常规调度":"梯级联调"));
        rollUpdateFloodControl.setForecastingSchemeId(incomingWaterId);
        rollUpdateFloodControl.setStatus(1);
        rollUpdateFloodControl.setCreateBy(modelRollUpdate.getCreateBy());
        rollUpdateFloodControl.setForecastingTime(incomingWaterForecast.getPredictionTime());
        rollUpdateFloodControl.setForecastingSchemeName(incomingWaterForecast.getProgrammeName());
        rollUpdateFloodControl.setRollId(modelRollUpdate.getId());
        boolean b = this.save(rollUpdateFloodControl);
        ExecutorService executor = new ThreadPoolExecutor(20, 50, 2, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(50), new ThreadPoolExecutor.CallerRunsPolicy());
        Future<Boolean> future = executor.submit(new Callable<Boolean>() {

            private RollUpdateFloodControlService rollUpdateFloodControlService = SpringUtil.getBean(RollUpdateFloodControlService.class);
            public Boolean call() {
                try {
                    //模型参数
                    ReqFloodPrevent paramReq = new ReqFloodPrevent();
                    paramReq.setProgrammeName(incomingWaterForecast.getProgrammeName());
                    //水库数据
                    Map<String, List<DataFloodPrevent>> data = new HashMap<>();
                    //文件路径
                    String modelResultAddress = incomingWaterForecast.getModelResultAddress();
                    InputStream tth = minioUtils.getObject("tth", modelResultAddress);
                    String[] split = modelResultAddress.split("\\\\");
                    String[] split1 = split[split.length - 1].split("\\.");
                    MultipartFile multipartFile = MultipartFileUtil.inputStreamToMultipartFile(tth, split1[0]);
                    List<Flood> floods = ExcelUtils.importExcel(multipartFile, Flood.class);
                    List<DataFloodPrevent> interval = getDataFloodPrevent(floods, "楼头区间");
                    if (null != interval) {
                        data.put("头屯河", interval);
                    } else {
                        data.put("头屯河", null);
                    }
                    List<DataFloodPrevent> lzzEntryStation = getDataFloodPrevent(floods, "楼庄子");
                    if (null != lzzEntryStation) {
                        data.put("楼庄子", lzzEntryStation);
                    } else {
                        data.put("楼庄子", null);
                    }
                    paramReq.setIntervals(data);
                    //起调水位（实际水位）
                    Double h1_begin = 0.00;
                    Double h2_begin = 0.00;
                    String realTimeWaterLevelData = waterResourceApi.getRealTimeWaterLevelData(sdf.format(new Date()));
                    List<RealTimeEngineeringSituationDataDto> realTimeEngineeringSituationDataDtos = JSONObject.parseArray(realTimeWaterLevelData, RealTimeEngineeringSituationDataDto.class);
                    if (CollectionUtil.isNotEmpty(realTimeEngineeringSituationDataDtos)) {
                        for(RealTimeEngineeringSituationDataDto dto : realTimeEngineeringSituationDataDtos){
                            if(dto.getReservoirName().equals("楼庄子水库")){
                                h1_begin = dto.getRealTimeWaterLevel();
                            }
                            if(dto.getReservoirName().equals("头屯河水库")){
                                h2_begin = dto.getRealTimeWaterLevel();
                            }
                        }
                    }
                    HashMap<String, Double> stringDoubleHashMap = new HashMap<>();
                    stringDoubleHashMap.put("楼庄子", h1_begin);    //楼庄子水库
                    stringDoubleHashMap.put("头屯河", h2_begin);    //头屯河水库
                    paramReq.setBeginLevels(stringDoubleHashMap);
                    //权重
                    /* paramReq.setWeights(new HashMap<String, Double>() {{
                        put("楼庄子", req.getStep1());
                        put("头屯河", req.getStep2());
                    }});*/
                    //动态汛限水位（12数字的数组）
                    paramReq.setLimitLevels(new HashMap<String, double[]>() {{
                        put("楼庄子", getArrays(modelRollUpdate.getLimitLevelsLzz()));
                        put("头屯河", getArrays(modelRollUpdate.getLimitLevelsTth()));
                    }});

                    //生态流量（12数字的数组）
                    paramReq.setEco(new HashMap<String, double[]>() {{
                        put("楼庄子", getArrays(modelRollUpdate.getEcosLzz()));
                        put("头屯河", getArrays(modelRollUpdate.getEcosTth()));
                    }});


                    BasinParam basinParam = loadBasinParam();
                    List<ResOption> calculator = RollUpdate.calculator(JSONObject.toJSONString(basinParam), paramReq, getReqCurves(basinParam),Integer.parseInt(modelRollUpdate.getSchedulingScheme()));
                    if(calculator.isEmpty()){
                        return false;
                    }
                    ResOption resOption = calculator.get(0);
                    String path = resOption.getPath();
                    String[] pathSplit = path.split("\\\\");
                    Date date = new Date();
                    String yyyyMMdd = DateUtil.format(date, "yyyyMMdd");
                    String hh = DateUtil.format(date, "HH");
                    String mm = DateUtil.format(date, "mm");
                    String ss = DateUtil.format(date, "ss");
                    ObjectWriteResponse objectWriteResponse = minioUtils.putObject("tth", yyyyMMdd + "/" + hh + "/" + mm + "/" + ss + "/" + UUID.fastUUID().toString(true) + "/" + pathSplit[pathSplit.length - 1], path);
                    FileUtil.del(path);
                    String object = objectWriteResponse.object();
                    tth.close();
                    boolean update = rollUpdateFloodControlService.lambdaUpdate()
                            .set(RollUpdateFloodControl::getStatus, 2)
                            .set(RollUpdateFloodControl::getModelResultAddress, object)
                            .set(StringUtils.hasText(resOption.getInform()), RollUpdateFloodControl::getRemark, resOption.getInform())
                            .eq(RollUpdateFloodControl::getSchemeName, resOption.getName()).update();
                    if(update){
                        return true;
                    }else {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
        if (!future.get()){
           throw new RuntimeException("防洪调度滚动失败");
        }
    }

    @Override
    public Map<String, List<PredictionProcessDto>> selectDetails(String id) {
        try {
            Map<String,List<PredictionProcessDto>> results = new LinkedHashMap<>();
            RollUpdateFloodControl floodControlOperation = this.getById(id);
            String modelResultAddress = floodControlOperation.getModelResultAddress();
            InputStream tth = minioUtils.getObject("tth", modelResultAddress);
            String[] split = modelResultAddress.split("\\\\");
            String[] split1 = split[split.length - 1].split("\\.");
            MultipartFile multipartFile = MultipartFileUtil.inputStreamToMultipartFile(tth, split1[0]);
            List<Option> floods = ExcelUtils.importExcel(multipartFile, Option.class);
            List<PredictionProcessDto> interval = getPredictions(floods,"头屯河");
            if(null != interval){
                results.put("头屯河",interval);
            }else {
                results.put("头屯河",null);
            }
            List<PredictionProcessDto> lzzEntryStation  = getPredictions(floods,"楼庄子");
            if(null != lzzEntryStation){
                results.put("楼庄子",lzzEntryStation);
            }else {
                results.put("楼庄子",null);
            }
            tth.close();
            return results;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public List<PredictionProcessDto> getPredictions(List<Option> floods, String station){
        List<Option> floodList = floods.stream().filter(t -> t.getName().equals(station)).collect(Collectors.toList());
        if(null != floodList && floodList.size() > 0) {
            List<PredictionProcessDto> predictionProcess = new ArrayList<>();
            for (Option flood : floodList) {
                String qSingleString = flood.getQSingleString();
                List<Double> singleList = JSONObject.parseArray(qSingleString, Double.class);
                PredictionProcessDto predictionProcessDto = new PredictionProcessDto();
                predictionProcessDto.setPreQ(singleList.get(0));
                predictionProcessDto.setTime(flood.getTime());
                predictionProcessDto.setWaterLevel(flood.getH2());
                predictionProcessDto.setCapacity(flood.getV());
                predictionProcessDto.setRetain(flood.getRetain());
                predictionProcessDto.setQIn(flood.getQIn());
                predictionProcessDto.setQOut(flood.getQOut());
                predictionProcessDto.setQ1(singleList.get(0));
                predictionProcessDto.setQ2(singleList.get(1));
                if (singleList.size() == 3) {
                    predictionProcessDto.setQ3(singleList.get(2));
                }
                predictionProcessDto.setFloodStorageCapacityPercent(flood.getPercentage1());
                predictionProcessDto.setRegulatingStorageCapacityPercent(flood.getPercentage2());
                predictionProcess.add(predictionProcessDto);
            }
            return predictionProcess;
        }else {
            return null;
        }
    }

    public List<DataFloodPrevent> getDataFloodPrevent(List<Flood> floods, String station){
        List<Flood> floodList = floods.stream().filter(t -> t.getLocation().equals(station)).collect(Collectors.toList());
        if(null != floodList && floodList.size() > 0) {
            List<DataFloodPrevent> dataFloodPreventList = new ArrayList<>();
            for (Flood flood : floodList) {
                if (flood.getPreQ() != null) {
                    DataFloodPrevent dataFloodPrevent = new DataFloodPrevent();
                    dataFloodPrevent.setPre(flood.getPreQ());
                    dataFloodPrevent.setTime(flood.getTime());
                    dataFloodPrevent.setScale(Integer.parseInt(flood.getScale()));
                    dataFloodPreventList.add(dataFloodPrevent);
                }
            }
            return dataFloodPreventList;
        }else {
            return null;
        }
    }

    private ReqCurve getReqCurves(BasinParam basinParam) {
        ReqCurve reqCurve = new ReqCurve();
        reqCurve.setCapacityCurves(new HashMap<>());
        reqCurve.setGateCurves(new HashMap<>());
        try {
            List<RRs> rrs = JSONObject.parseArray(JSONObject.parseObject(waterSituationClient.queryRRs("0")).get("data").toString(), RRs.class);
            List<ExternStations> externStations = JSONObject.parseArray(JSONObject.parseObject(waterSituationClient.queryExternStations()).get("data").toString(), ExternStations.class);
            basinParam.getReservoirs().forEach(
                    reservoir ->
                    {
                        Optional<RRs> anyRRs = rrs.stream().filter(r -> r.getName().contains(reservoir.getName())).findAny();
                        if (!anyRRs.isPresent()) {
                            return;
                        }
                        List<CurveParam> curveParams = getCurves(anyRRs.get().getId());
                        if (CollectionUtil.isEmpty(curveParams)) {
                            return;
                        }
                        reqCurve.getCapacityCurves().put(reservoir.getName(), curveParams);

                        reservoir.getGates().forEach(gate -> {
                            Optional<ExternStations> anyGate = externStations.stream().filter(station -> station.getName().equals(gate.getName())).findAny();
                            if (!anyGate.isPresent()) {
                                return;
                            }
                            List<CurveParam> curveParamsGate = getCurves(anyGate.get().getId());
                            if (CollectionUtil.isEmpty(curveParamsGate)) {
                                return;
                            }
                            if (!reqCurve.getGateCurves().containsKey(reservoir.getName())) {
                                reqCurve.getGateCurves().put(reservoir.getName(), new HashMap<String, List<CurveParam>>() {{
                                    put(gate.getName(), curveParamsGate);
                                }});
                            } else {
                                reqCurve.getGateCurves().get(reservoir.getName()).put(gate.getName(), curveParamsGate);
                            }
                        });
                    }
            );
        } catch (Exception e) {
            log.error("水情服务获取库容闸门曲线异常" + e.getMessage());
        }
        return reqCurve;
    }
    private List<CurveParam> getCurves(String ndcdId) {
        List<DropDown> dropDowns = JSONObject.parseArray(JSONObject.parseObject(waterSituationClient.dropDown(ndcdId)).get("data").toString(), DropDown.class);
        Optional<DropDown> dropDownsTrue = dropDowns.stream().filter(d -> d.getEnable().equals("true")).findAny();
        if (!dropDownsTrue.isPresent()) {
            return null;
        }
        QuXT quXT = JSONObject.parseObject(JSONObject.parseObject(waterSituationClient.queryQuXT(dropDownsTrue.get().getId())).get("data").toString(), QuXT.class);
        List<CurveParam> curveParams = new ArrayList<>();
        quXT.getTab().forEach(tab ->
                curveParams.add(new CurveParam(){{
                    setLevel(tab.getV0());
                    setValue(tab.getV1());
                }}));
        return curveParams;
    }

    private double[] getArrays(String s){
        String[] split = s.split(",");
        if(split.length>0){
            double[] array = new double[split.length];
            for(int i=0;i<split.length;i++){
                array[i] = Double.parseDouble(split[i]);
            }
            return array;
        }else {
            return null;
        }
    }
}

