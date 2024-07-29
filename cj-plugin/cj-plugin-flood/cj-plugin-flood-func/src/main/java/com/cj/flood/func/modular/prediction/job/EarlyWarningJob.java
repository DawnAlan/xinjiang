package com.cj.flood.func.modular.prediction.job;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.util.NumberUtil;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.func.modular.prediction.bean.req.IncomingWaterForecastAddReq;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.cj.flood.func.modular.prediction.service.IncomingWaterForecastService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity.IrrigatedPlatformTree;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.service.IrrigatedPlatformTreeService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzPlatformTree.entity.LzzPlatformTree;
import com.cj.middleDatabase.func.modular.lzz.lzzPlatformTree.service.LzzPlatformTreeService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.LzzRainfallStationService;
import com.cj.msg.entity.OverallMsg;
import com.cj.msg.entity.WarnDto;
import com.cj.msg.service.OverallMsgService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class EarlyWarningJob {

    @Autowired
    private LzzPlatformTreeService lzzPlatformTreeService;

    @Autowired
    private LzzRainfallStationService lzzRainfallStationService;

    @Autowired
    private LzzGaugingStationService lzzGaugingStationService;

    @Autowired
    private IncomingWaterForecastService incomingWaterForecastService;

    @Autowired
    private IrrigatedPlatformTreeService irrigatedPlatformTreeService;

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    @Autowired
    private OverallMsgService overallMsgService;

    @Autowired
    private RedisUtil redisUtil;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @XxlJob("scanRainFall")
    private void scanRainFall(){
        log.info("--------------------------------雨情预警开始----------------------------");
        Map<String, String> map = new HashMap<>();
        try {
            Date date = new Date();
            String endTime = sdf.format(date);
            String startTime = calculateTime(date,-12);
            Boolean isSameDay = startTime.split(" ")[0].split("-")[2].equals(endTime.split(" ")[0].split("-")[2]);
            if(!isSameDay){
                startTime = endTime.split(" ")[0]+" 00:00";
            }
            List<LzzPlatformTree> lzzPlatformTrees = lzzPlatformTreeService.lambdaQuery().like(LzzPlatformTree::getName, "雨量站").list();
            for (LzzPlatformTree lzzPlatformTree : lzzPlatformTrees) {
                List<LzzRainfallStation> lzzRainfallStations = lzzRainfallStationService.selectInfoByCondition(lzzPlatformTree.getId(), null, startTime, endTime);
                Double d = 0.00;
                for(LzzRainfallStation station:lzzRainfallStations){
                    if(station.getRainfall()!=null){
                        d += station.getRainfall().doubleValue();
                    }
                }
                if(d>=10){
                    map.put(lzzPlatformTree.getName().replace(" ",""),d.toString());
                }
            }
            List<IrrigatedPlatformTree> irrigatedPlatformTreeList = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getMonitorType, 03).list();
            for(IrrigatedPlatformTree lzzPlatformTree : irrigatedPlatformTreeList){
                List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = irrigatedPlatformDataInfoService.selectInfoByCondition(lzzPlatformTree.getId(), null, startTime, endTime);
                if(!irrigatedPlatformDataInfos.isEmpty()){
                    IrrigatedPlatformDataInfo info = irrigatedPlatformDataInfos.get(0);
                    if(info.getYqRainFallTwelve()>=10){
                        map.put(lzzPlatformTree.getName().replace(" ",""),info.getYqRainFallTwelve().toString());
                    }
                }
            }
            String scanRainFall = (String) redisUtil.get("scanRainFall");
            if(StringUtils.isNotEmpty(scanRainFall)){
                Map<String, String> stringStringMap = convertStringToMap(scanRainFall, ",", "=");
                if(map.size()>0){
                    boolean b = areMapsEqual(stringStringMap, map);
                    if(!b){
                        IncomingWaterForecastAddReq req = new IncomingWaterForecastAddReq();
                        IncomingWaterForecast incomingWaterForecast = new IncomingWaterForecast();
                        incomingWaterForecast.setModelType(3);
                        incomingWaterForecast.setPeriodTimeNum(24);
                        incomingWaterForecast.setPeriodTimeStep(1);
                        incomingWaterForecast.setPeriodTimeType(4);
                        incomingWaterForecast.setPredictionTime(new Date());
                        incomingWaterForecast.setProgrammeName(sdf.format(new Date())+":雨量预警来水方案");
                        incomingWaterForecast.setRemark("");
                        req.setIncomingWaterForecast(incomingWaterForecast);
                        req.setPreRainFall(0.00);
                        req.setRainFallDtos(new ArrayList<>());
                        req.setPreFlow(0.00);
                        req.setIsSimulation(false);
                        req.setIsReferenceWater(false);
                        incomingWaterForecastService.addForScanRainFall(req);
                        redisUtil.set("scanRainFall",map.toString().replaceAll("(^\\{)|(\\}$)", ""));
                    }
                }
            }else {
                if(map.size()>0){
                    IncomingWaterForecastAddReq req = new IncomingWaterForecastAddReq();
                    IncomingWaterForecast incomingWaterForecast = new IncomingWaterForecast();
                    incomingWaterForecast.setModelType(3);
                    incomingWaterForecast.setPeriodTimeNum(24);
                    incomingWaterForecast.setPeriodTimeStep(1);
                    incomingWaterForecast.setPeriodTimeType(4);
                    incomingWaterForecast.setPredictionTime(new Date());
                    incomingWaterForecast.setProgrammeName(sdf.format(new Date())+":雨量预警来水方案");
                    incomingWaterForecast.setRemark("");
                    req.setIncomingWaterForecast(incomingWaterForecast);
                    req.setPreRainFall(0.00);
                    req.setRainFallDtos(new ArrayList<>());
                    req.setPreFlow(0.00);
                    req.setIsSimulation(false);
                    req.setIsReferenceWater(false);
                    incomingWaterForecastService.addForScanRainFall(req);
                    redisUtil.set("scanRainFall",map.toString().replaceAll("(^\\{)|(\\}$)", ""));
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
        log.info("--------------------------------雨情预警结束----------------------------");
    }

    @XxlJob("scanWaterLevel")
    private void scanWaterLevel(){
        log.info("--------------------------------水位变幅告警开始----------------------------");
        String lzzWaterLevelIds = "2023101303,92102001,92102010";
        String tthWaterLevelIds = "8a8181d2798e094b0179c6f7d04f0016";
        try {
            Date date = new Date();
            String endTime = sdf.format(date);
            String startTime = calculateTime(date,-1);
            Boolean isSameDay = startTime.split(" ")[0].split("-")[2].equals(endTime.split(" ")[0].split("-")[2]);
            if(!isSameDay){
                startTime = endTime.split(" ")[0]+" 00:00";
            }
            List<OverallMsg> msgList = new ArrayList<>();
            for(String id:lzzWaterLevelIds.split(",")){
                List<LzzGaugingStation> lzzGaugingStationList = lzzGaugingStationService.selectInfoByCondition(id, null, startTime, endTime);
                if(!lzzGaugingStationList.isEmpty()){
                    LzzGaugingStation start = lzzGaugingStationList.get(0);
                    LzzGaugingStation end = lzzGaugingStationList.get(lzzGaugingStationList.size() - 1);
                    //5、10、20
                    Double v = end.getFlow()-start.getFlow();
                    String alertLevel = v>=20?"THREE":v>=10?"TWO":v>=5?"ONE":"";
                    if(StringUtils.isNotEmpty(alertLevel)) {
                        OverallMsg msg = new OverallMsg();
                        msg.setId(UUIDUtils.getUUID());
                        msg.setIsRead(0);
                        msg.setSubject("waterLevel");
                        msg.setCreateUser(start.getStationName());
                        msg.setReceiveUser(alertLevel);
                        msg.setCreateTime(date);
                        msg.setCategory("变幅");
                        WarnDto warnDto = new WarnDto();
                        warnDto.setTime(sdf.format(date));
                        warnDto.setFlow(v);
                        warnDto.setWarnType("flow");
                        warnDto.setType("waterStation");
                        warnDto.setName(start.getStationName());
                        warnDto.setAlertLevel(alertLevel);
                        msg.setContent(JSONObject.toJSONString(warnDto));
                        List<OverallMsg> overallMsgs = overallMsgService.lambdaQuery().apply("content = '" + msg.getContent() + "'").list();
                        if (overallMsgs.isEmpty()) {
                            msgList.add(msg);
                        }
                    }
                }
            }
            for(String id:tthWaterLevelIds.split(",")){
                List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = irrigatedPlatformDataInfoService.selectInfoByCondition(id, null, startTime, endTime);
                if(!irrigatedPlatformDataInfos.isEmpty()){
                    IrrigatedPlatformDataInfo end = irrigatedPlatformDataInfos.get(0);
                    IrrigatedPlatformDataInfo start = irrigatedPlatformDataInfos.get(irrigatedPlatformDataInfos.size() - 1);
                    //5、10、20
                    Double v = end.getSqMonitorFlow()-start.getSqMonitorFlow();
                    String alertLevel = v>=20?"THREE":v>=10?"TWO":v>=5?"ONE":"";
                    if(StringUtils.isNotEmpty(alertLevel)) {
                        OverallMsg msg = new OverallMsg();
                        msg.setId(UUIDUtils.getUUID());
                        msg.setIsRead(0);
                        msg.setSubject("waterLevel");
                        msg.setCreateUser("头屯河"+start.getMonitorName());
                        msg.setReceiveUser(alertLevel);
                        msg.setCreateTime(date);
                        msg.setCategory("变幅");
                        WarnDto warnDto = new WarnDto();
                        warnDto.setTime(sdf.format(date));
                        warnDto.setFlow(NumberUtil.holdDecimal(v,3));
                        warnDto.setWarnType("flow");
                        warnDto.setType("waterStation");
                        warnDto.setName("头屯河"+start.getMonitorName());
                        warnDto.setAlertLevel(alertLevel);
                        msg.setContent(JSONObject.toJSONString(warnDto));
                        List<OverallMsg> overallMsgs = overallMsgService.lambdaQuery().apply("content = '" + msg.getContent() + "'").list();
                        if (overallMsgs.isEmpty()) {
                            msgList.add(msg);
                        }
                    }
                }
            }
            overallMsgService.saveBatch(msgList);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
        log.info("--------------------------------水位变幅告警结束----------------------------");
    }
    private String calculateTime(Date time,int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.HOUR,hour);
        Date date = calendar.getTime();
        return sdf.format(date);
    }

    public static boolean areMapsEqual(Map<String, String> map1, Map<String, String> map2) {
        if (map1.size() != map2.size()) {
            return false;
        }

        for (Map.Entry<String, String> entry : map1.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (map2.containsKey(key)) {
                String value2 = map2.get(key);
                if (!value.equals(value2)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static Map<String, String> convertStringToMap(String input, String pairDelimiter, String keyValueDelimiter) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = input.split(pairDelimiter);
        for (String pair : pairs) {
            String[] entry = pair.split(keyValueDelimiter);
            if (entry.length == 2) {
                map.put(entry[0].replace(" ",""), entry[1]);
            }
        }
        return map;
    }
}
