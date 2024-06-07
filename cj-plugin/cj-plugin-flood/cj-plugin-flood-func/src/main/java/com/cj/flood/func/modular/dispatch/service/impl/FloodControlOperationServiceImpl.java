package com.cj.flood.func.modular.dispatch.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.func.core.common.feign.WaterSituationClient;
import com.cj.flood.func.core.common.feign.entity.DropDown;
import com.cj.flood.func.core.common.feign.entity.ExternStations;
import com.cj.flood.func.core.common.feign.entity.QuXT;
import com.cj.flood.func.core.common.feign.entity.RRs;
import com.cj.flood.func.modular.dispatch.bean.req.FloodControlOperationAddReq;
import com.cj.flood.func.modular.dispatch.bean.req.FloodControlOperationListReq;
import com.cj.flood.func.modular.dispatch.bean.res.FloodControlOperationListRes;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.model.func.core.util.MultipartFileUtil;
import com.cj.flood.func.modular.dispatch.mapper.FloodControlOperationMapper;
import com.cj.flood.func.modular.dispatch.entity.FloodControlOperation;
import com.cj.flood.func.modular.dispatch.service.FloodControlOperationService;
import com.cj.flood.func.modular.prediction.bean.dto.PredictionProcessDto;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.cj.flood.func.modular.prediction.service.IncomingWaterForecastService;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqCurve;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.res.ResOption;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.FloodPrevent.entity.DataFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import com.cj.model.func.modular.FloodPrevent.function.*;
import com.cj.model.func.modular.curve.service.CurveService;
import com.cj.model.func.modular.entity.Flood;
import io.minio.ObjectWriteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.cj.flood.func.core.common.PublicParam;

/**
 * 防洪调度表(FloodControlOperation)表服务实现类
 *
 * @author makejava
 * @since 2023-11-09 15:49:49
 */
@Service("floodControlOperationService")
public class FloodControlOperationServiceImpl extends ServiceImpl<FloodControlOperationMapper, FloodControlOperation> implements FloodControlOperationService {

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private CurveService curveService;

    @Autowired
    private IncomingWaterForecastService incomingWaterForecastService;

    @Autowired
    private WaterSituationClient waterSituationClient;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${floodModelFilePath}")
    private String floodModelFilePath;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmmm");
    @Override
    public RestResponse<Map<String,List<PredictionProcessDto>>> selectDetails(String id) {
        try {
            Map<String,List<PredictionProcessDto>> results = new LinkedHashMap<>();
            FloodControlOperation floodControlOperation = this.getById(id);
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
           return RestResponse.ok(results);
        }
        catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("查询失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(FloodControlOperationAddReq req) {
        try {
            SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
            String incomingWaterForecastId = req.getIncomingWaterForecastId();
            IncomingWaterForecast incomingWaterForecast = incomingWaterForecastService.getById(incomingWaterForecastId);
            incomingWaterForecast.setProgrammeName(sdf.format(new Date())+incomingWaterForecast.getProgrammeName());
            //结果列表
            List<FloodControlOperation> result = new ArrayList<>();
            //常规调度
            FloodControlOperation regularScheduling =  new FloodControlOperation();
            regularScheduling.setId(UUIDUtils.getUUID());
            regularScheduling.setCreateTime(new Date());
            regularScheduling.setSchemeName(incomingWaterForecast.getProgrammeName()+"-常规调度");
            regularScheduling.setForecastingSchemeId(incomingWaterForecastId);
            regularScheduling.setStatus(1);
            regularScheduling.setCreateBy(saBaseLoginUser.getName());
            regularScheduling.setForecastingTime(incomingWaterForecast.getPredictionTime());
            regularScheduling.setForecastingSchemeName(incomingWaterForecast.getProgrammeName());
            //灵活调度
            FloodControlOperation minimumContainment =  new FloodControlOperation();
            minimumContainment.setId(UUIDUtils.getUUID());
            minimumContainment.setCreateTime(new Date());
            minimumContainment.setSchemeName(incomingWaterForecast.getProgrammeName()+"-灵活调度");
            minimumContainment.setForecastingSchemeId(incomingWaterForecastId);
            minimumContainment.setStatus(1);
            minimumContainment.setCreateBy(saBaseLoginUser.getName());
            //预泄调度
            FloodControlOperation maximumPeakShaving =  new FloodControlOperation();
            maximumPeakShaving.setId(UUIDUtils.getUUID());
            maximumPeakShaving.setCreateTime(new Date());
            maximumPeakShaving.setSchemeName(incomingWaterForecast.getProgrammeName()+"-预泄调度");
            maximumPeakShaving.setForecastingSchemeId(incomingWaterForecastId);
            maximumPeakShaving.setStatus(1);
            maximumPeakShaving.setCreateBy(saBaseLoginUser.getName());
            //加入结果列表
            result.add(regularScheduling);
            result.add(minimumContainment);
            result.add(maximumPeakShaving);
            ExecutorService pool = Executors.newSingleThreadExecutor();
             pool.submit(new Runnable() {
                private FloodControlOperationServiceImpl floodControlOperationService = SpringUtil.getBean(FloodControlOperationServiceImpl.class);

                @Override
                public void run() {
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
                        paramReq.setBeginLevels(new HashMap<String, Double>()
                        {{
                            put("楼庄子", req.getH1_begin());
                            put("头屯河", req.getH2_begin());
                        }});
                        paramReq.setWeights(new HashMap<String, Double>()
                        {{
                            put("楼庄子", req.getStep1());
                            put("头屯河", req.getStep2());
                        }});
                        paramReq.setLimitLevels(new HashMap<String, double[]>()
                        {{
                            put("楼庄子", req.getLimitLevelsLzz());
                            put("头屯河", req.getLimitLevelsTth());
                        }});

//                        Map<String,double[]> eco = new HashMap<>();
//                        eco.put("楼庄子", new double[]{0.74,0.74,0.74,1.48,1.48,1.48,1.48,1.48,1.48,0.74,0.74,0.74});
//                        eco.put("头屯河", new double[]{0.74,0.74,0.74,1.48,1.48,1.48,1.48,1.48,1.48,0.74,0.74,0.74});
//                        paramReq.setEco(eco);
                        paramReq.setEco(new HashMap<String, double[]>()
                        {{
                            put("楼庄子", req.getEcosLzz());
                            put("头屯河", req.getEcosTth());
                        }});

                        List<ResOption> calculator = Cascade.calculator(JSONObject.toJSONString(PublicParam.basinParam), paramReq, getReqCurves());
                        for (ResOption resOption : calculator) {
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
                            floodControlOperationService.lambdaUpdate()
                                    .set(FloodControlOperation::getStatus, 2)
                                    .set(FloodControlOperation::getModelResultAddress, object)
                                    .set(StringUtils.hasText(resOption.getInform()), FloodControlOperation::getRemark, resOption.getInform())
                                    .eq(FloodControlOperation::getSchemeName, resOption.getName()).update();
                        }
                        tth.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        floodControlOperationService.lambdaUpdate().set(FloodControlOperation::getStatus, 3).set(FloodControlOperation::getRemark, e.getMessage() + ";请检查后重新生成").eq(FloodControlOperation::getForecastingSchemeId, req.getIncomingWaterForecastId()).update();
                    }
                }
            });
            boolean b = this.saveBatch(result);
            if(b){
                return RestResponse.ok("防洪调度生成中……");
            }else {
                return RestResponse.no("防洪调度生成失败");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("防洪调度生成错误");
        }
    }
  /* @Override
   @Transactional(rollbackFor = Exception.class)
   public RestResponse add(FloodControlOperationAddReq req) {
       try {
           String incomingWaterForecastId = req.getIncomingWaterForecastId();
           IncomingWaterForecast incomingWaterForecast = incomingWaterForecastService.getById(incomingWaterForecastId);
           incomingWaterForecast.setProgrammeName(sdf.format(new Date())+incomingWaterForecast.getProgrammeName());
           //结果列表
           List<FloodControlOperation> result = new ArrayList<>();
           //常规调度
           FloodControlOperation regularScheduling =  new FloodControlOperation();
           regularScheduling.setId(UUIDUtils.getUUID());
           regularScheduling.setCreateTime(new Date());
           regularScheduling.setSchemeName(incomingWaterForecast.getProgrammeName()+"-常规调度");
           regularScheduling.setForecastingSchemeId(incomingWaterForecastId);
           regularScheduling.setStatus(1);
           //最小拦蓄
           FloodControlOperation minimumContainment =  new FloodControlOperation();
           minimumContainment.setId(UUIDUtils.getUUID());
           minimumContainment.setCreateTime(new Date());
           minimumContainment.setSchemeName(incomingWaterForecast.getProgrammeName()+"-最小拦蓄");
           minimumContainment.setForecastingSchemeId(incomingWaterForecastId);
           minimumContainment.setStatus(1);
           //最大削峰
           FloodControlOperation maximumPeakShaving =  new FloodControlOperation();
           maximumPeakShaving.setId(UUIDUtils.getUUID());
           maximumPeakShaving.setCreateTime(new Date());
           maximumPeakShaving.setSchemeName(incomingWaterForecast.getProgrammeName()+"-最大削峰");
           maximumPeakShaving.setForecastingSchemeId(incomingWaterForecastId);
           maximumPeakShaving.setStatus(1);
           //加入结果列表
           result.add(regularScheduling);
           result.add(minimumContainment);
           result.add(maximumPeakShaving);

           boolean b = this.saveBatch(result);
           if(b){
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
                   List<DataFloodPrevent> interval = getDataFloodPrevent(floods,"楼头区间");
                   if(null != interval){
                       data.put("lat",interval);
                   }else {
                       data.put("lat",null);
                   }
                   List<DataFloodPrevent> lzzEntryStation  = getDataFloodPrevent(floods,"楼庄子");
                   if(null != lzzEntryStation){
                       data.put("lzz",lzzEntryStation);
                   }else {
                       data.put("lzz",null);
                   }
                   List<CurveParam> curveParams = curveService.selectList();
                   paramReq.setCurveParam(curveParams);
                   paramReq.setData(data);
                   paramReq.setH1_begin(req.getH1_begin());
                   paramReq.setH1_end(req.getH1_end());
                   paramReq.setH2_begin(req.getH2_begin());
                   paramReq.setH2_end(req.getH2_end());
                   paramReq.setStep1(req.getStep1());
                   paramReq.setStep2(req.getStep2());
                   paramReq.setLimitLevels_lzz(req.getLimitLevelsLzz());
                   paramReq.setLimitLevels_tth(req.getLimitLevelsTth());
                   List<ResOption> calculator = Cascade.calculator(paramReq);
                   for(ResOption resOption : calculator){
                       String path = resOption.getPath();
                       String[] pathSplit = path.split("\\\\");
                       Date date = new Date();
                       String yyyyMMdd = DateUtil.format(date, "yyyyMMdd");
                       String hh = DateUtil.format(date, "HH");
                       String mm = DateUtil.format(date, "mm");
                       String ss = DateUtil.format(date, "ss");
                       ObjectWriteResponse objectWriteResponse = minioUtils.putObject("tth", yyyyMMdd+"/"+hh+"/"+mm+"/"+ss+"/"+ UUID.fastUUID().toString(true)+"/"+pathSplit[pathSplit.length-1], path);
                       String object = objectWriteResponse.object();
                       this.lambdaUpdate().set(FloodControlOperation::getStatus,2).set(FloodControlOperation::getModelResultAddress,object).eq(FloodControlOperation::getSchemeName,resOption.getName()).update();
                   }
                   tth.close();
                   System.out.println(calculator.size());
                   return RestResponse.ok("防洪调度生成成功");
               }catch (Exception e){
                   e.printStackTrace();
                   return RestResponse.no("防洪调度生成失败");
               }
           }else {
               return RestResponse.no("防洪调度生成失败");
           }
       }catch (Exception e){
           e.printStackTrace();
           return RestResponse.no("防洪调度生成错误");
       }
   }*/
    @Override
    public RestResponse<IPage<FloodControlOperationListRes>> selectList(FloodControlOperationListReq req) {
        try {
            IPage<FloodControlOperationListRes> page = new Page<>(req.getPageNum(),req.getPageSize());
            IPage<FloodControlOperationListRes> resIPage = this.baseMapper.selectFloodControlOperationList(req, page);
            if(resIPage.getTotal()>0){
                return RestResponse.ok(resIPage);
            }else {
                return RestResponse.no("暂无数据");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("查询失败");
        }
    }

    @Override
    public RestResponse<Map<String,Object>> containmentCalculator(String ids) {
        try {
            Map<String,Object> result = new LinkedHashMap<>();
            List<String> collect = Arrays.stream(ids.split(",")).collect(Collectors.toList());
            List<FloodControlOperation> floodControlOperations = this.listByIds(collect);
            Map<String, List<Option>> options = new HashMap<>();
            for(FloodControlOperation op : floodControlOperations){
                String modelResultAddress = op.getModelResultAddress();
                InputStream tth = minioUtils.getObject("tth", modelResultAddress);
                String[] split = modelResultAddress.split("\\\\");
                String[] split1 = split[split.length - 1].split("\\.");
                MultipartFile multipartFile = MultipartFileUtil.inputStreamToMultipartFile(tth, split1[0]);
                List<Option> options1 = ExcelUtils.importExcel(multipartFile, Option.class);
//                options1.forEach(t->{
//                    List<Double> doubles = JSONObject.parseArray(t.getLimitString(), Double.class);
//                    t.setLimits(doubles);
//                });
                options.put(op.getSchemeName(),options1);
                tth.close();
            }
            //水库总览
            Map<String,Object> stringMapMap = Containment.ContainmentCalculator(options);
            result.put("水库总览",stringMapMap);
            //测站总览
            Map<String, Map<String, Map<String, Integer>>> stringMapMap1 = OverLevels.OverLevelsCalculator(options);
            result.put("测站总览",stringMapMap1);
            //单值详情
            Map<String, Map<String, Map<String, Object>>> stringMapMap2 = Extremal.ExtremalCalculator(options);
            result.put("单值详情",stringMapMap2);
            //过程详情
            Map<String, Map<String, Map<String, List<Object>>>> stringMapMap3 = ProcessDetail.ProcessDetailCalculator(options);
            result.put("过程详情",stringMapMap3);
            Map<String, Map<String, Map<String, String>>> stringMapMap4 = OverTimes.OverTimesCalculator(options);
            result.put("测站总览累计",stringMapMap4);
            Map<String, Map<String, Map<String, List<Object>>>> stringMapMap5 = GateDetail.GateDetailCalculator(options);
            result.put("闸门策略",stringMapMap5);
            return RestResponse.ok(result);
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("对比失败");
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
                DataFloodPrevent dataFloodPrevent = new DataFloodPrevent();
                dataFloodPrevent.setPre(flood.getPreQ());
                dataFloodPrevent.setTime(flood.getTime());
                dataFloodPrevent.setScale(Integer.parseInt(flood.getScale()));
                dataFloodPreventList.add(dataFloodPrevent);
            }
            return dataFloodPreventList;
        }else {
            return null;
        }
    }

    private ReqCurve getReqCurves() {
        ReqCurve reqCurve = new ReqCurve();
        reqCurve.setCapacityCurves(new HashMap<>());
        reqCurve.setGateCurves(new HashMap<>());
        List<RRs> rrs = JSONObject.parseArray(JSONObject.parseObject(waterSituationClient.queryRRs("0")).get("data").toString(), RRs.class);
        List<ExternStations> externStations = JSONObject.parseArray(JSONObject.parseObject(waterSituationClient.queryExternStations()).get("data").toString(), ExternStations.class);
        PublicParam.basinParam.getReservoirs().forEach(
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
                            reqCurve.getGateCurves().put(reservoir.getName(), new HashMap<String, List<CurveParam>>() {{put(gate.getName(), curveParamsGate);}});
                        } else {
                            reqCurve.getGateCurves().get(reservoir.getName()).put(gate.getName(), curveParamsGate);
                        }
                    });
                }
        );
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
}

