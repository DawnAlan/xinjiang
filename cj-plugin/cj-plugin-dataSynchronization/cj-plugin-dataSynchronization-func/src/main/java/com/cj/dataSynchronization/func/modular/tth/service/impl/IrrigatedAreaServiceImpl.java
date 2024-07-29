package com.cj.dataSynchronization.func.modular.tth.service.impl;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.NumberUtil;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.msg.entity.WarnDto;
import com.cj.dataSynchronization.func.modular.tth.IrrigatedAreaInvoke;
import com.cj.dataSynchronization.func.modular.tth.dtos.*;
import com.cj.dataSynchronization.func.modular.tth.service.IrrigatedAreaService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformData.entity.IrrigatedPlatformData;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformData.service.IrrigatedPlatformDataService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity.IrrigatedPlatformTree;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.service.IrrigatedPlatformTreeService;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.entity.StorageCapacityCurve;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.service.StorageCapacityCurveService;
import com.cj.msg.entity.OverallMsg;
import com.cj.msg.service.OverallMsgService;
import com.cj.waterresources.api.WaterResourceApi;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class IrrigatedAreaServiceImpl implements IrrigatedAreaService {

    @Autowired
    private IrrigatedPlatformTreeService irrigatedPlatformTreeService;

    @Autowired
    private IrrigatedPlatformDataService irrigatedPlatformDataService;

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StorageCapacityCurveService storageCapacityCurveService;

    @Autowired
    private WaterResourceApi waterResourceApi;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private OverallMsgService overallMsgService;

    @Override
    public RestResponse getAllTree() {
        List<QueryMonitorBasicDto> queryMonitorBasic = IrrigatedAreaInvoke.getQueryMonitorBasic();
        if(null == queryMonitorBasic || queryMonitorBasic.size()<0){
            return RestResponse.no("平台无数据");
        }
        List<IrrigatedPlatformTree> irrigatedPlatformTreeList = new ArrayList<>();
        for(QueryMonitorBasicDto dto : queryMonitorBasic){
            IrrigatedPlatformTree tree = new IrrigatedPlatformTree();
            tree.setId(dto.getID());
            tree.setName(dto.getNAME());
            tree.setParentId(dto.getPARENT_ID());
            //tree.setBeginTime(dto.getBEGIN_TIME());
            //tree.setBeginTimeMark(dto.getBEGIN_TIME_MARK());
            tree.setElevation(dto.getELEVATION());
            //tree.setIsWaterLevel(dto.getIS_WATER_LEVEL());
            //tree.setLocationType(dto.getLOCATION_TYPE());
            //tree.setLocationTypeName(dto.getLOCATION_TYPE_NAME());
            //tree.setMeasureType(dto.getMEASURE_TYPE());
            tree.setMonitorType(dto.getMONITOR_TYPE());
            //tree.setNodetype(dto.getNODETYPE());
            tree.setSelfCode(dto.getSELF_CODE());
            //tree.setWaterlevelNotnormal(dto.getWATERLEVEL_NOTNORMAL());
            irrigatedPlatformTreeList.add(tree);
        }
        boolean b = irrigatedPlatformTreeService.saveOrUpdateBatch(irrigatedPlatformTreeList);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse getDataByIdAndTime(Integer flag, String time) {
        try {
            String[] split = time.split(" ");
            List<IrrigatedPlatformTree> list = irrigatedPlatformTreeService.list();
            List<IrrigatedPlatformData> result = new ArrayList<>();
            Integer i = 1;
            if(null == list || list.size()<0){
                return RestResponse.no("平台无数据");
            }
            for(IrrigatedPlatformTree tree: list){
                if(StringUtils.isEmpty(tree.getMonitorType())){
                    continue;
                }
                List<AllHistoryDataDto> allHistoryData = IrrigatedAreaInvoke.getAllHistoryData(tree.getId(), split[0], split[1],tree.getMonitorType());
                if(null == allHistoryData  || allHistoryData.size()<0){
                    continue;
                }
                if(flag==1){
                    AllHistoryDataDto dto = allHistoryData.get(0);
                    IrrigatedPlatformData data = new IrrigatedPlatformData();
                    data.setMonitorName(tree.getName());
                    data.setRegionId(dto.getREGION_ID());
                    data.setVoltage(dto.getVOLTAGE());
                    data.setPTotalFlow(dto.getP_TOTAL_FLOW());
                    data.setUserId(dto.getUSER_ID());
                    data.setDownWater(dto.getDOWN_WATER()==null?"":dto.getDOWN_WATER().toString());
                    data.setIgCo(dto.getIG_CO()==null?"":dto.getIG_CO().toString());
                    data.setMonitorFlow(dto.getMONITOR_FLOW());
                    data.setMonitorId(dto.getMONITOR_ID());
                    data.setOperateTime(dto.getOPERATE_TIME());
                    data.setMonitorTime(dto.getMONITOR_TIME());
                    data.setGateOpenHoles(dto.getGATE_OPEN_HOLES()==null?"":dto.getGATE_OPEN_HOLES().toString());
                    data.setId(dto.getID());
                    data.setRemark(dto.getREMARK());
                    data.setWater(dto.getWATER());
                    data.setCapacity(dto.getCAPACITY());
                    data.setNTotalFlow(dto.getN_TOTAL_FLOW());
                    data.setIsSurpass(dto.getIS_SURPASS());
                    data.setUserName(dto.getUSER_NAME());
                    data.setInputFlow(dto.getINPUT_FLOW());
                    data.setGateHeight(dto.getGATE_HEIGHT());
                    data.setWaterLevel(dto.getWATER_LEVEL());
                    data.setTotalFlow(dto.getTOTAL_FLOW());
                    data.setMonitorFlowRate(dto.getMONITOR_FLOW_RATE());
                    data.setIgSp(dto.getIG_SP()==null?"":dto.getIG_SP().toString());
                    data.setDownLevel(dto.getDOWN_LEVEL()==null?"":dto.getDOWN_LEVEL().toString());
                    data.setGateHeightShow(dto.getGATE_HEIGHT_SHOW());
                    data.setIsNullPipe(dto.getIS_NULL_PIPE()==null?"":dto.getIS_NULL_PIPE().toString());
                    data.setPipePressure(dto.getPIPE_PRESSURE()==null?"":dto.getPIPE_PRESSURE().toString());
                    result.add(data);
                }else {
                    for(AllHistoryDataDto dto:allHistoryData){
                        IrrigatedPlatformData data = new IrrigatedPlatformData();
                        data.setMonitorName(tree.getName());
                        data.setRegionId(dto.getREGION_ID());
                        data.setVoltage(dto.getVOLTAGE());
                        data.setPTotalFlow(dto.getP_TOTAL_FLOW());
                        data.setUserId(dto.getUSER_ID());
                        data.setDownWater(dto.getDOWN_WATER()==null?"":dto.getDOWN_WATER().toString());
                        data.setIgCo(dto.getIG_CO()==null?"":dto.getIG_CO().toString());
                        data.setMonitorFlow(dto.getMONITOR_FLOW());
                        data.setMonitorId(dto.getMONITOR_ID());
                        data.setOperateTime(dto.getOPERATE_TIME());
                        data.setMonitorTime(dto.getMONITOR_TIME());
                        data.setGateOpenHoles(dto.getGATE_OPEN_HOLES()==null?"":dto.getGATE_OPEN_HOLES().toString());
                        data.setId(dto.getID());
                        data.setRemark(dto.getREMARK());
                        data.setWater(dto.getWATER());
                        data.setCapacity(dto.getCAPACITY());
                        data.setNTotalFlow(dto.getN_TOTAL_FLOW());
                        data.setIsSurpass(dto.getIS_SURPASS());
                        data.setUserName(dto.getUSER_NAME());
                        data.setInputFlow(dto.getINPUT_FLOW());
                        data.setGateHeight(dto.getGATE_HEIGHT());
                        data.setWaterLevel(dto.getWATER_LEVEL());
                        data.setTotalFlow(dto.getTOTAL_FLOW());
                        data.setMonitorFlowRate(dto.getMONITOR_FLOW_RATE());
                        data.setIgSp(dto.getIG_SP()==null?"":dto.getIG_SP().toString());
                        data.setDownLevel(dto.getDOWN_LEVEL()==null?"":dto.getDOWN_LEVEL().toString());
                        data.setGateHeightShow(dto.getGATE_HEIGHT_SHOW());
                        data.setIsNullPipe(dto.getIS_NULL_PIPE()==null?"":dto.getIS_NULL_PIPE().toString());
                        data.setPipePressure(dto.getPIPE_PRESSURE()==null?"":dto.getPIPE_PRESSURE().toString());
                        result.add(data);
                    }
                }
                i++;
            }
            log.info("count:"+i);
            boolean b = irrigatedPlatformDataService.saveOrUpdateBatch(result);
            if(b){
                return RestResponse.ok("ok");
            }else {
                return RestResponse.no("error");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse getDataById(){
        try {
            String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
            if(StringUtils.isEmpty(overall)){
                overall = waterResourceApi.getOverallSituationUnitMgrList();
            }
            List<OverallSituationUnitMgrDto> overallSituationUnitMgrDtoList = JSONObject.parseArray(overall, OverallSituationUnitMgrDto.class);
            List<IrrigatedPlatformTree> list = irrigatedPlatformTreeService.list();
            List<IrrigatedPlatformDataInfo> result = new ArrayList<>();
            if(null == list || list.size()<0){
                return RestResponse.no("平台无数据");
            }
            for (IrrigatedPlatformTree node : list){
                if(StringUtils.isEmpty(node.getMonitorType())){
                    continue;
                }
                List<QueryRealTimeDataDto> queryRealTimeData = IrrigatedAreaInvoke.getQueryRealTimeData(node.getId());
                if(null != queryRealTimeData && queryRealTimeData.size()>0){
                    IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
                    QueryRealTimeDataDto dto = queryRealTimeData.get(0);
                    info.setId(dto.getMONITOR_NAME()+"-"+sdf.parse(dto.getMONITOR_TIME()).getTime());
                    info.setMonitorId(dto.getID());
                    info.setWaterDaily(dto.getWATER_DAILY());
                    info.setSqCapacity(dto.getSQ_CAPACITY());
                    info.setYesterdayAvgFlow(dto.getYESTERDAY_AVG_FLOW());
                    info.setMonitorName(dto.getMONITOR_NAME());
                    info.setSqTotalFlow(dto.getSQ_TOTAL_FLOW());
                    info.setSqMonitorFlow(dto.getSQ_MONITOR_FLOW());
                    info.setAvgWaterLevel(dto.getAVG_WATER_LEVEL());
                    info.setAvgWaterDeep(dto.getAVG_WATER_DEEP());
                    info.setSqMonitorFlowRate(dto.getSQ_MONITOR_FLOW_RATE());
                    info.setBeginTime(dto.getBEGIN_TIME());
                    info.setYesterdayWaterDaily(dto.getYESTERDAY_WATER_DAILY());
                    info.setMonitorTime(DateUtil.parse(dto.getMONITOR_TIME(),"yyyy-MM-dd HH:mm"));
                    info.setRecordTime(DateUtil.parse(dto.getMONITOR_TIME(),"yyyy-MM-dd"));
                    info.setSqWaterLevel(dto.getSQ_WATER_LEVEL());
                    info.setVoltage(dto.getVOLTAGE());
                    info.setAvgFlow(dto.getAVG_FLOW());
                    info.setYearWaterDaily(dto.getYEAR_WATER_DAILY());
                    info.setQxRainFall(StringUtils.isEmpty(dto.getQX_RAIN_FALL())?null:Double.valueOf(dto.getQX_RAIN_FALL()));
                    info.setYqRainFallOne(StringUtils.isEmpty(dto.getYQ_RAIN_FALL_1())?null:Double.valueOf(dto.getYQ_RAIN_FALL_1()));
                    info.setYqRainFallThree(StringUtils.isEmpty(dto.getYQ_RAIN_FALL_3())?null:Double.valueOf(dto.getYQ_RAIN_FALL_3()));
                    info.setYqRainFallSix(StringUtils.isEmpty(dto.getYQ_RAIN_FALL_6())?null:Double.valueOf(dto.getYQ_RAIN_FALL_6()));
                    info.setYqRainFallTwelve(StringUtils.isEmpty(dto.getYQ_RAIN_FALL_12())?null:Double.valueOf(dto.getYQ_RAIN_FALL_12()));
                    info.setYqRainFallTwentyFour(StringUtils.isEmpty(dto.getYQ_RAIN_FALL_24())?null:Double.valueOf(dto.getYQ_RAIN_FALL_24()));
                    info.setGdIsNullPipe(dto.getGD_IS_NULL_PIPE());
                    info.setGdPipePressure(StringUtils.isEmpty(dto.getGD_PIPE_PRESSURE())?null:Double.valueOf(dto.getGD_PIPE_PRESSURE()));
                    info.setGdMonitorFlow(dto.getGD_MONITOR_FLOW());
                    info.setGdMonitorFlowRate(dto.getGD_MONITOR_FLOW_RATE());
                    info.setGdTotalFlow(dto.getGD_TOTAL_FLOW());
                    result.add(info);
                    List<OverallSituationUnitMgrDto> collect = overallSituationUnitMgrDtoList.stream().filter(t -> t.getMonitorId().equals(dto.getID())).collect(Collectors.toList());
                    if(!collect.isEmpty()){
                        OverallSituationUnitMgrDto overallSituationUnitMgrDto = collect.get(0);
                        redisUtil.set("irrigatedPlatform:today:"+overallSituationUnitMgrDto.getId(),info.getAvgFlow(),3600*24);
                        redisUtil.set("irrigatedPlatform:yesterday:"+overallSituationUnitMgrDto.getId(),info.getYesterdayAvgFlow(),3600*24);
                        redisUtil.set("irrigatedPlatform:sq:date:id:"+info.getMonitorTime()+":"+overallSituationUnitMgrDto.getId(),info.getSqMonitorFlow(),3600*24);
                    }
                    if(info.getMonitorName().equals("头屯河水库水位")){
                        redisUtil.set("irrigatedPlatform:sq:tth:waterLevel:"+sdf.format(info.getMonitorTime()),info.getSqWaterLevel(),3600*24*2);
                        redisUtil.set("irrigatedPlatform:sq:tth:capacity:"+sdf.format(info.getMonitorTime()),info.getSqCapacity(),3600*24*2);
                    }
                    if(info.getMonitorName().equals("出库流量")){
                        redisUtil.set("irrigatedPlatform:sq:tth:out:"+sdf.format(info.getMonitorTime()),info.getSqMonitorFlow(),3600*24*2);
                        String alertLevel = "";
                        if(info.getSqMonitorFlow()!=null){
                            alertLevel = info.getSqMonitorFlow()>=210?"FOUR":info.getSqMonitorFlow()>=160?"THREE":info.getSqMonitorFlow()>=120?"TWO":info.getSqMonitorFlow()>=100?"ONE":"";
                        }
                        if(StringUtils.isNotEmpty(alertLevel)){
                            OverallMsg msg = new OverallMsg();
                            msg.setId(UUIDUtils.getUUID());
                            msg.setIsRead(0);
                            msg.setSubject("waterLevel");
                            msg.setCreateUser("头屯河出库");
                            msg.setReceiveUser(alertLevel);
                            msg.setCreateTime(new Date());
                            msg.setCategory("告警");
                            WarnDto warnDto = new WarnDto();
                            warnDto.setTime(dto.getMONITOR_TIME());
                            warnDto.setFlow(info.getSqMonitorFlow());
                            warnDto.setWarnType("flow");
                            warnDto.setType("waterStation");
                            warnDto.setName("头屯河出库");
                            warnDto.setAlertLevel(alertLevel);
                            msg.setContent(JSONObject.toJSONString(warnDto));
                            List<OverallMsg> overallMsgs = overallMsgService.lambdaQuery().apply("content = '"+msg.getContent()+"'").list();
                            if(overallMsgs.isEmpty()){
                                waterResourceApi.sendMsg(msg.getContent());
                                overallMsgService.save(msg);
                            }
                        }
                    }
                    if(info.getMonitorName().equals("入库流量")){
                        redisUtil.set("irrigatedPlatform:sq:tth:input:"+sdf.format(info.getMonitorTime()),info.getSqMonitorFlow(),3600*24*2);
                        String alertLevel = "";
                        if(info.getSqMonitorFlow()!=null){
                            alertLevel = info.getSqMonitorFlow()>=210?"FOUR":info.getSqMonitorFlow()>=160?"THREE":info.getSqMonitorFlow()>=120?"TWO":info.getSqMonitorFlow()>=100?"ONE":"";
                        }
                        if(StringUtils.isNotEmpty(alertLevel)){
                            OverallMsg msg = new OverallMsg();
                            msg.setId(UUIDUtils.getUUID());
                            msg.setIsRead(0);
                            msg.setCreateUser("头屯河入库");
                            msg.setReceiveUser(alertLevel);
                            msg.setSubject("waterLevel");
                            msg.setCreateTime(new Date());
                            msg.setCategory("告警");
                            WarnDto warnDto = new WarnDto();
                            warnDto.setTime(dto.getMONITOR_TIME());
                            warnDto.setFlow(info.getSqMonitorFlow());
                            warnDto.setWarnType("flow");
                            warnDto.setType("waterStation");
                            warnDto.setName("头屯河入库");
                            warnDto.setAlertLevel(alertLevel);
                            msg.setContent(JSONObject.toJSONString(warnDto));
                            List<OverallMsg> overallMsgs = overallMsgService.lambdaQuery().apply("content = '"+msg.getContent()+"'").list();
                            if(overallMsgs.isEmpty()){
                                waterResourceApi.sendMsg(msg.getContent());
                                overallMsgService.save(msg);
                            }
                        }
                    }
                    if(info.getMonitorName().equals("八钢工业取水口")){
                        redisUtil.set("irrigatedPlatform:sq:tth:aq:"+sdf.format(info.getMonitorTime()),info.getSqMonitorFlow(),3600*24*2);
                    }
                }
            }
            boolean b = irrigatedPlatformDataInfoService.saveOrUpdateBatch(result);
            if(b){
                return RestResponse.ok("ok");
            }else {
                return RestResponse.no("error");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }

    @Override
    @SneakyThrows
    public RestResponse importHistoryData(MultipartFile file) {
        DecimalFormat df = new DecimalFormat("0.0");
        DecimalFormat df1 = new DecimalFormat("0.00");
        DecimalFormat df2 = new DecimalFormat("0.000");
        String tempString = (String) redisUtil.get("irrigatedPlatformTree");
        if (StringUtils.isEmpty(tempString)) {
            List<IrrigatedPlatformTree> list = irrigatedPlatformTreeService.list();
            redisUtil.set("irrigatedPlatformTree", JSONObject.toJSONString(list));
        }
        String treeString = (String) redisUtil.get("irrigatedPlatformTree");
        List<IrrigatedPlatformTree> treeList = JSONObject.parseArray(treeString, IrrigatedPlatformTree.class);
        List<IrrigatedPlatformDataInfo> resultList = new ArrayList<>();
        List<ImportHistoryDataDto> importHistoryDataDtos = ExcelUtils.importExcel(file, ImportHistoryDataDto.class);
        if (null != importHistoryDataDtos && importHistoryDataDtos.size() > 0) {
            for (ImportHistoryDataDto dto : importHistoryDataDtos) {
                if (dto.getMonitorId() != null) {
                    IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
                    List<IrrigatedPlatformTree> irrigatedPlatformTrees = treeList.stream().filter(t -> t.getId().equals(dto.getMonitorId())).collect(Collectors.toList());
                    IrrigatedPlatformTree tree = irrigatedPlatformTrees.get(0);
                    info.setMonitorId(dto.getMonitorId());
                    info.setMonitorName(tree.getName());
                    info.setMonitorTime(sdf.parse(dto.getMonitorTime()));
                    info.setId(info.getMonitorName() + "-" + sdf.parse(dto.getMonitorTime()).getTime());
                    info.setSqMonitorFlowRate(dto.getSqMonitorFlowRate() == null ? null : Double.valueOf(df2.format(dto.getSqMonitorFlowRate())));
                    //info.setSqMonitorFlow(dto.getSqMonitorFlow()==null?dto.getSqMonitorFlow1()==null?null:Double.valueOf(df2.format(dto.getSqMonitorFlow1())):Double.valueOf(df2.format(dto.getSqMonitorFlow())));
                    info.setSqTotalFlow(dto.getSqTotalFlow() == null ? null : Double.valueOf(df2.format(dto.getSqTotalFlow())));
                    info.setYqRainFallOne(dto.getYqRainFallOne() == null ? null : Double.valueOf(df2.format(dto.getYqRainFallOne())));
                    if (info.getMonitorName().equals("头屯河水库水位")) {
                        info.setSqWaterLevel(dto.getSqWaterLevel1() < 100 ? 949.14 + dto.getSqWaterLevel1() : dto.getSqWaterLevel1());
                        if (dto.getSqCapacity() == null) {
                            String tthString = (String) redisUtil.get("storageCapacityCurveTth");
                            if (StringUtils.isEmpty(tthString)) {
                                List<StorageCapacityCurve> tth = storageCapacityCurveService.lambdaQuery().eq(StorageCapacityCurve::getReservoir, "tth").list();
                                redisUtil.set("storageCapacityCurveTth", JSONObject.toJSONString(tth));
                                tthString = JSONObject.toJSONString(tth);
                            }
                            List<StorageCapacityCurve> tthList = JSONObject.parseArray(tthString, StorageCapacityCurve.class);
                            Double sqWaterLevel = info.getSqWaterLevel();
                            String[] split = df1.format(sqWaterLevel).split("\\.");
                            List<String> strings = NumberUtil.roundDecimal(split[0], split[1]);
                            List<StorageCapacityCurve> collect = tthList.stream().filter(t -> t.getWaterLevel().compareTo(new BigDecimal(strings.get(0))) == 0 && df.format(t.getInterpolation()).equals(strings.get(1).length() < 2 ? "0.0" : strings.get(1).split("\\.")[0] + "." + strings.get(1).split("\\.")[1].substring(0, 1))).collect(Collectors.toList());
                            StorageCapacityCurve storageCapacityCurve = collect.size() == 0 ? null : collect.get(0);
                            info.setSqCapacity(storageCapacityCurve == null ? null : storageCapacityCurve.getStorageCapacity().doubleValue());
                        } else {
                            info.setSqCapacity(dto.getSqCapacity());
                        }
                    }
                    resultList.add(info);
                }
            }
        }
        if (null != resultList && resultList.size() > 0) {
            boolean b = irrigatedPlatformDataInfoService.saveOrUpdateBatch(resultList);
            if (b) {
                return RestResponse.ok();
            } else {
                return RestResponse.no("save error");
            }
        } else {
            return RestResponse.no("import error");
        }
    }

    @Override
    public RestResponse selectHistoryData(String type, String id, String startTime, String endTime) {
        if(type.equals("water")){
            List<HistoryDataVo> historyDataForWater = IrrigatedAreaInvoke.getHistoryDataForWater(id, startTime, endTime);
            return RestResponse.ok(historyDataForWater);
        }
        if(type.equals("rain")){
            List<HistoryDataVo> historyDataForRain = IrrigatedAreaInvoke.getHistoryDataForRain(id, startTime, endTime);
            return RestResponse.ok(historyDataForRain);
        }
        if (type.equals("pipe")){
            List<HistoryDataVo> historyDataForPipeLine = IrrigatedAreaInvoke.getHistoryDataForPipeLine(id, startTime, endTime);
            return RestResponse.ok(historyDataForPipeLine);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public RestResponse calculateHistoryDataAverageValue(String id, String time) {
        Map<String,Object> result= new HashMap<>();
        Date startTime = sdf.parse(time + " 20:00");
        Date endTime = calculateTime(startTime, -24);
        List<HistoryDataVo> historyDataForWater = IrrigatedAreaInvoke.getHistoryDataForWater(id, sdf.format(endTime), sdf.format(startTime));
        if(historyDataForWater.isEmpty()){
            return RestResponse.no("该时间段暂无数据");
        }else {
            double flow = historyDataForWater.stream().filter(t->t.getMONITOR_FLOW()!=null).mapToDouble(HistoryDataVo::getMONITOR_FLOW).sum();
            double level = historyDataForWater.stream().filter(t->t.getWATER_LEVEL()!=null).mapToDouble(HistoryDataVo::getWATER_LEVEL).sum();
            result.put("flow_all", NumberUtil.holdDecimal(flow/historyDataForWater.size(),3));
            result.put("flow_*300", NumberUtil.holdDecimal(flow*300,3));
            result.put("level_all",NumberUtil.holdDecimal(level/historyDataForWater.size(),3));
            result.put("flow", NumberUtil.holdDecimal(flow/historyDataForWater.stream().filter(t->t.getMONITOR_FLOW()!=null).count(),3));
            result.put("level",NumberUtil.holdDecimal(level/historyDataForWater.stream().filter(t->t.getWATER_LEVEL()!=null).count(),3));

            return RestResponse.ok(result);
        }
    }

    @SneakyThrows
    @Override
    public RestResponse saveHistoryData(String id,String startTime, String endTime) {
        //monitorType 01-计量点  03-雨量站 04-管道
        if(StringUtils.isNotEmpty(id)){
            List<IrrigatedPlatformDataInfo> resultList = new ArrayList<>();
            IrrigatedPlatformTree byId = irrigatedPlatformTreeService.getById(id);
            if(byId.getMonitorType().equals("01")){
                List<HistoryDataVo> historyDataForWater = IrrigatedAreaInvoke.getHistoryDataForWater(id, startTime, endTime);
                if(historyDataForWater!=null && historyDataForWater.size()>0){
                    for(HistoryDataVo vo:historyDataForWater){
                        IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
                        info.setMonitorName(vo.getMONITOR_NAME());
                        info.setId(vo.getMONITOR_NAME()+"-"+sdf.parse(vo.getMONITOR_TIME()).getTime());
                        info.setVoltage(vo.getVOLTAGE());
                        info.setSqWaterLevel(vo.getWATER_LEVEL());
                        info.setMonitorTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd HH:mm"));
                        info.setRecordTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd"));
                        info.setSqMonitorFlow(vo.getMONITOR_FLOW());
                        info.setSqMonitorFlowRate(vo.getMONITOR_FLOW_RATE());
                        info.setMonitorId(vo.getMONITOR_ID());
                        info.setSqTotalFlow(vo.getTOTAL_FLOW());
                        resultList.add(info);
                    }
                }
            }
            if(byId.getMonitorType().equals("03")){
                List<HistoryDataVo> historyDataForWater = IrrigatedAreaInvoke.getHistoryDataForRain(id, startTime, endTime);
                if(historyDataForWater!=null && historyDataForWater.size()>0){
                    for(HistoryDataVo vo:historyDataForWater){
                        IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
                        info.setMonitorName(vo.getMONITOR_NAME());
                        info.setId(vo.getMONITOR_NAME()+"-"+sdf.parse(vo.getMONITOR_TIME()).getTime());
                        info.setMonitorTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd HH:mm"));
                        info.setRecordTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd"));
                        info.setMonitorId(vo.getMONITOR_ID());
                        info.setQxRainFall(vo.getRAIN_FALL());
                        long between = DateUtil.between(sdf.parse(startTime), sdf.parse(endTime), DateUnit.HOUR);
                        if(between>1 && between<3){
                            List<HistoryDataVo> one = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                            && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -1)))
                                    .collect(Collectors.toList());
                            info.setYqRainFallOne(one.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                            if(between>3 && between<6){
                                List<HistoryDataVo> three = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -3)))
                                        .collect(Collectors.toList());
                                info.setYqRainFallThree(three.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                if(between>6 && between<12){
                                    List<HistoryDataVo> six = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                    && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -6)))
                                            .collect(Collectors.toList());
                                    info.setYqRainFallSix(six.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                    if(between>12 && between<24){
                                        List<HistoryDataVo> twelve = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                        && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -12)))
                                                .collect(Collectors.toList());
                                        info.setYqRainFallTwelve(twelve.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                        if(between>24){
                                            List<HistoryDataVo> twentyFour = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                            && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -24)))
                                                    .collect(Collectors.toList());
                                            info.setYqRainFallTwentyFour(twentyFour.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                        }
                                    }
                                }
                            }
                        }
                        resultList.add(info);
                    }
                }
            }
            if(byId.getMonitorType().equals("04")){
                List<HistoryDataVo> historyDataForWater = IrrigatedAreaInvoke.getHistoryDataForPipeLine(id, startTime, endTime);
                if(historyDataForWater!=null && historyDataForWater.size()>0){
                    for(HistoryDataVo vo:historyDataForWater){
                        IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
                        info.setMonitorName(vo.getMONITOR_NAME());
                        info.setId(vo.getMONITOR_NAME()+"-"+sdf.parse(vo.getMONITOR_TIME()).getTime());
                        info.setMonitorTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd HH:mm"));
                        info.setRecordTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd"));
                        info.setMonitorId(vo.getMONITOR_ID());
                        info.setVoltage(vo.getVOLTAGE());
                        info.setGdMonitorFlowRate(vo.getMONITOR_FLOW_RATE());
                        info.setGdMonitorFlow(vo.getMONITOR_FLOW());
                        info.setGdTotalFlow(vo.getTOTAL_FLOW());
                        info.setGdIsNullPipe(vo.getIS_NULL_PIPE());
                        info.setGdPipePressure(vo.getPIPE_PRESSURE());
                        resultList.add(info);
                    }
                }
            }
            boolean b = irrigatedPlatformDataInfoService.saveOrUpdateBatch(resultList);
            if(b){
                return RestResponse.ok("保存历史数据成功");
            }else {
                return RestResponse.no("保存历史数据失败");
            }
        }else {
            List<IrrigatedPlatformTree> list = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getNodetype, "Monitor").ne(IrrigatedPlatformTree::getMonitorType, "21").isNotNull(IrrigatedPlatformTree::getMonitorType).list();
            List<IrrigatedPlatformDataInfo> resultList = new ArrayList<>();
            for(IrrigatedPlatformTree tree:list){
                if(tree.getMonitorType().equals("01")){
                    List<HistoryDataVo> historyDataForWater = IrrigatedAreaInvoke.getHistoryDataForWater(tree.getId(), startTime, endTime);
                    if(historyDataForWater!=null && historyDataForWater.size()>0){
                        for(HistoryDataVo vo:historyDataForWater){
                            IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
                            info.setMonitorName(vo.getMONITOR_NAME());
                            info.setId(vo.getMONITOR_NAME()+"-"+sdf.parse(vo.getMONITOR_TIME()).getTime());
                            info.setVoltage(vo.getVOLTAGE());
                            info.setSqWaterLevel(vo.getWATER_LEVEL());
                            info.setMonitorTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd HH:mm"));
                            info.setRecordTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd"));
                            info.setSqMonitorFlow(vo.getMONITOR_FLOW());
                            info.setSqMonitorFlowRate(vo.getMONITOR_FLOW_RATE());
                            info.setMonitorId(vo.getMONITOR_ID());
                            info.setSqTotalFlow(vo.getTOTAL_FLOW());
                            resultList.add(info);
                        }
                    }
                }
                if(tree.getMonitorType().equals("03")){
                    List<HistoryDataVo> historyDataForWater = IrrigatedAreaInvoke.getHistoryDataForRain(tree.getId(), startTime, endTime);
                    if(historyDataForWater!=null && historyDataForWater.size()>0){
                        for(HistoryDataVo vo:historyDataForWater){
                            IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
                            info.setMonitorName(vo.getMONITOR_NAME());
                            info.setId(vo.getMONITOR_NAME()+"-"+sdf.parse(vo.getMONITOR_TIME()).getTime());
                            info.setMonitorTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd HH:mm"));
                            info.setRecordTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd"));
                            info.setMonitorId(vo.getMONITOR_ID());
                            info.setQxRainFall(vo.getRAIN_FALL());
                            long between = DateUtil.between(sdf.parse(startTime), sdf.parse(endTime), DateUnit.HOUR);
                            if(between>1 && between<3){
                                List<HistoryDataVo> one = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -1)))
                                        .collect(Collectors.toList());
                                info.setYqRainFallOne(one.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                if(between>3 && between<6){
                                    List<HistoryDataVo> three = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                    && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -3)))
                                            .collect(Collectors.toList());
                                    info.setYqRainFallThree(three.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                    if(between>6 && between<12){
                                        List<HistoryDataVo> six = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                        && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -6)))
                                                .collect(Collectors.toList());
                                        info.setYqRainFallSix(six.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                        if(between>12 && between<24){
                                            List<HistoryDataVo> twelve = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                            && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -12)))
                                                    .collect(Collectors.toList());
                                            info.setYqRainFallTwelve(twelve.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                            if(between>24){
                                                List<HistoryDataVo> twentyFour = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                                && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -24)))
                                                        .collect(Collectors.toList());
                                                info.setYqRainFallTwentyFour(twentyFour.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                            }
                                        }
                                    }
                                }
                            }
                            resultList.add(info);
                        }
                    }

                }
                if(tree.getMonitorType().equals("04")){
                    List<HistoryDataVo> historyDataForWater = IrrigatedAreaInvoke.getHistoryDataForPipeLine(tree.getId(), startTime, endTime);
                    if(historyDataForWater!=null && historyDataForWater.size()>0){
                        for(HistoryDataVo vo:historyDataForWater){
                            IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
                            info.setMonitorName(vo.getMONITOR_NAME());
                            info.setId(vo.getMONITOR_NAME()+"-"+sdf.parse(vo.getMONITOR_TIME()).getTime());
                            info.setMonitorTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd HH:mm"));
                            info.setRecordTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd"));
                            info.setMonitorId(vo.getMONITOR_ID());
                            info.setVoltage(vo.getVOLTAGE());
                            info.setGdMonitorFlowRate(vo.getMONITOR_FLOW_RATE());
                            info.setGdMonitorFlow(vo.getMONITOR_FLOW());
                            info.setGdTotalFlow(vo.getTOTAL_FLOW());
                            info.setGdIsNullPipe(vo.getIS_NULL_PIPE());
                            info.setGdPipePressure(vo.getPIPE_PRESSURE());
                            resultList.add(info);
                        }
                    }
                }

            }
            boolean b = irrigatedPlatformDataInfoService.saveOrUpdateBatch(resultList);
            if(b){
                return RestResponse.ok("保存历史数据成功");
            }else {
                return RestResponse.no("保存历史数据失败");
            }
        }
    }

    @SneakyThrows
    @Override
    public RestResponse saveHistoryDataForRain(String id, String startTime, String endTime) {
        //monitorType 01-计量点  03-雨量站 04-管道
        if(StringUtils.isNotEmpty(id)){
            List<IrrigatedPlatformDataInfo> resultList = new ArrayList<>();
            List<HistoryDataVo> historyDataForWater = IrrigatedAreaInvoke.getHistoryDataForRain(id, startTime, endTime);
            if(historyDataForWater!=null && historyDataForWater.size()>0){
                for(HistoryDataVo vo:historyDataForWater){
                    IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
                    info.setMonitorName(vo.getMONITOR_NAME());
                    info.setId(vo.getMONITOR_NAME()+"-"+sdf.parse(vo.getMONITOR_TIME()).getTime());
                    info.setMonitorTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd HH:mm"));
                    info.setRecordTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd"));
                    info.setMonitorId(vo.getMONITOR_ID());
                    info.setQxRainFall(vo.getRAIN_FALL());
                    long between = DateUtil.between(sdf.parse(startTime), sdf.parse(endTime), DateUnit.HOUR);
                    if(between>1){
                        List<HistoryDataVo> one = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                        && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -1)))
                                .collect(Collectors.toList());
                        info.setYqRainFallOne(one.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                        if(between>3){
                            List<HistoryDataVo> three = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                            && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -3)))
                                    .collect(Collectors.toList());
                            info.setYqRainFallThree(three.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                            if(between>6){
                                List<HistoryDataVo> six = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -6)))
                                        .collect(Collectors.toList());
                                info.setYqRainFallSix(six.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                if(between>12){
                                    List<HistoryDataVo> twelve = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                    && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -12)))
                                            .collect(Collectors.toList());
                                    info.setYqRainFallTwelve(twelve.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                    if(between>24){
                                        List<HistoryDataVo> twentyFour = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                        && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -24)))
                                                .collect(Collectors.toList());
                                        info.setYqRainFallTwentyFour(twentyFour.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                    }
                                }
                            }
                        }
                    }
                    resultList.add(info);
                }
            }
            boolean b = irrigatedPlatformDataInfoService.saveOrUpdateBatch(resultList);
            if(b){
                return RestResponse.ok("保存历史数据成功");
            }else {
                return RestResponse.no("保存历史数据失败");
            }
        }else {
            List<IrrigatedPlatformTree> list = irrigatedPlatformTreeService.lambdaQuery().
                    eq(IrrigatedPlatformTree::getNodetype, "Monitor").
                    eq(IrrigatedPlatformTree::getMonitorType, "03").list();
            List<IrrigatedPlatformDataInfo> resultList = new ArrayList<>();
            for(IrrigatedPlatformTree tree:list){
                List<HistoryDataVo> historyDataForWater = IrrigatedAreaInvoke.getHistoryDataForRain(tree.getId(), startTime, endTime);
                if(historyDataForWater!=null && historyDataForWater.size()>0){
                    for(HistoryDataVo vo:historyDataForWater){
                        IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
                        info.setMonitorName(vo.getMONITOR_NAME());
                        info.setId(vo.getMONITOR_NAME()+"-"+sdf.parse(vo.getMONITOR_TIME()).getTime());
                        info.setMonitorTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd HH:mm"));
                        info.setRecordTime(DateUtil.parse(vo.getMONITOR_TIME(),"yyyy-MM-dd"));
                        info.setMonitorId(vo.getMONITOR_ID());
                        info.setQxRainFall(vo.getRAIN_FALL());
                        long between = DateUtil.between(sdf.parse(startTime), sdf.parse(endTime), DateUnit.HOUR);
                        if(between>1){
                            List<HistoryDataVo> one = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                            && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -1)))
                                    .collect(Collectors.toList());
                            info.setYqRainFallOne(one.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                            if(between>3){
                                List<HistoryDataVo> three = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -3)))
                                        .collect(Collectors.toList());
                                info.setYqRainFallThree(three.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                if(between>6){
                                    List<HistoryDataVo> six = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                    && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -6)))
                                            .collect(Collectors.toList());
                                    info.setYqRainFallSix(six.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                    if(between>12){
                                        List<HistoryDataVo> twelve = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                        && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -12)))
                                                .collect(Collectors.toList());
                                        info.setYqRainFallTwelve(twelve.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                        if(between>24){
                                            List<HistoryDataVo> twentyFour = historyDataForWater.stream().filter(t -> DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isAfter(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"))
                                                            && DateUtil.parse(t.getMONITOR_TIME(), "yyyy-MM-dd HH:mm").isBefore(calculateTime(DateUtil.parse(vo.getMONITOR_TIME(), "yyyy-MM-dd HH:mm"), -24)))
                                                    .collect(Collectors.toList());
                                            info.setYqRainFallTwentyFour(twentyFour.stream().filter(t->t.getRAIN_FALL() !=null).map(HistoryDataVo::getRAIN_FALL).reduce(Double::sum).orElse(0.00));
                                        }
                                    }
                                }
                            }
                        }
                        resultList.add(info);
                    }
                }
            }
            boolean b = irrigatedPlatformDataInfoService.saveOrUpdateBatch(resultList);
            if(b){
                return RestResponse.ok("保存历史数据成功");
            }else {
                return RestResponse.no("保存历史数据失败");
            }
        }
    }

    @Override
    public RestResponse insertWarningInfo(String startTime, String endTime) {
        List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.lambdaQuery().between(IrrigatedPlatformDataInfo::getMonitorTime, startTime, endTime).
                in(IrrigatedPlatformDataInfo::getMonitorName, "出库流量", "入库流量").
                gt(IrrigatedPlatformDataInfo::getSqMonitorFlow, 100).list();
        if(!list.isEmpty()){
            List<OverallMsg> msgList = new ArrayList<>();
            for (IrrigatedPlatformDataInfo info : list){
                if(info.getMonitorName().equals("出库流量")){
                    String alertLevel = "";
                    if(info.getSqMonitorFlow()!=null){
                        alertLevel = info.getSqMonitorFlow()>=210?"FOUR":info.getSqMonitorFlow()>=160?"THREE":info.getSqMonitorFlow()>=120?"TWO":info.getSqMonitorFlow()>=100?"ONE":"";
                    }
                    if(StringUtils.isNotEmpty(alertLevel)){
                        OverallMsg msg = new OverallMsg();
                        msg.setId(UUIDUtils.getUUID());
                        msg.setIsRead(0);
                        msg.setSubject("waterLevel");
                        msg.setCreateUser("头屯河出库");
                        msg.setReceiveUser(alertLevel);
                        msg.setCreateTime(info.getMonitorTime());
                        msg.setCategory("告警");
                        WarnDto warnDto = new WarnDto();
                        warnDto.setTime(sdf.format(info.getMonitorTime()));
                        warnDto.setFlow(info.getSqMonitorFlow());
                        warnDto.setWarnType("flow");
                        warnDto.setType("waterStation");
                        warnDto.setName("头屯河出库");
                        warnDto.setAlertLevel(alertLevel);
                        msg.setContent(JSONObject.toJSONString(warnDto));
                        List<OverallMsg> overallMsgs = overallMsgService.lambdaQuery().apply("content = '"+msg.getContent()+"'").list();
                        if(overallMsgs.isEmpty()){
                            msgList.add(msg);
                        }
                    }
                }
                if(info.getMonitorName().equals("入库流量")){
                    String alertLevel = "";
                    if(info.getSqMonitorFlow()!=null){
                        alertLevel = info.getSqMonitorFlow()>=210?"FOUR":info.getSqMonitorFlow()>=160?"THREE":info.getSqMonitorFlow()>=120?"TWO":info.getSqMonitorFlow()>=100?"ONE":"";
                    }
                    if(StringUtils.isNotEmpty(alertLevel)){
                        OverallMsg msg = new OverallMsg();
                        msg.setId(UUIDUtils.getUUID());
                        msg.setIsRead(0);
                        msg.setSubject("waterLevel");
                        msg.setCreateUser("头屯河入库");
                        msg.setReceiveUser(alertLevel);
                        msg.setCreateTime(info.getMonitorTime());
                        msg.setCategory("告警");
                        WarnDto warnDto = new WarnDto();
                        warnDto.setTime(sdf.format(info.getMonitorTime()));
                        warnDto.setFlow(info.getSqMonitorFlow());
                        warnDto.setWarnType("flow");
                        warnDto.setType("waterStation");
                        warnDto.setName("头屯河入库");
                        warnDto.setAlertLevel(alertLevel);
                        msg.setContent(JSONObject.toJSONString(warnDto));
                        List<OverallMsg> overallMsgs = overallMsgService.lambdaQuery().apply("content = '"+msg.getContent()+"'").list();
                        if(overallMsgs.isEmpty()){
                            msgList.add(msg);
                        }
                    }
                }
            }
            boolean b = overallMsgService.saveBatch(msgList);
            if(b){
                return RestResponse.ok("ok");
            }else {
                return RestResponse.no("error");
            }
        }
        return RestResponse.no("无数据");
    }

    private Date calculateTime(Date time,int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.HOUR,hour);
        Date date = calendar.getTime();
        return date;
    }
}
