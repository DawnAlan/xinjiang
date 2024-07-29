package com.cj.dataSynchronization.func.modular.lzz.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.common.util.NumberUtil;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.dataSynchronization.func.modular.lzz.bean.ParamDto;
import com.cj.dataSynchronization.func.modular.lzz.bean.UserIdParam;
import com.cj.msg.entity.WarnDto;
import com.cj.dataSynchronization.func.modular.lzz.mapper.LzzPlatformMapper;
import com.cj.dataSynchronization.func.modular.lzz.mapper.LzzRainFailMapper;
import com.cj.dataSynchronization.func.modular.lzz.service.LzzPlatformService;
import com.cj.dataSynchronization.func.modular.lzz.service.PubUserService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzPlatformTree.entity.LzzPlatformTree;
import com.cj.middleDatabase.func.modular.lzz.lzzPlatformTree.service.LzzPlatformTreeService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.LzzRainfallStationService;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.entity.StorageCapacityCurve;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.service.StorageCapacityCurveService;
import com.cj.msg.entity.OverallMsg;
import com.cj.msg.service.OverallMsgService;
import com.cj.waterresources.api.WaterResourceApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LzzPlatformServiceImpl implements LzzPlatformService {


    @Autowired
    private PubUserService pubUserService;

    @Autowired
    private LzzRainfallStationService lzzRainfallStationService;

    @Autowired
    private LzzPlatformMapper lzzPlatformMapper;

    @Autowired
    private LzzRainFailMapper lzzRainFailMapper;

    @Autowired
    private StorageCapacityCurveService storageCapacityCurveService;

    @Autowired
    private WaterResourceApi waterResourceApi;


    @Autowired
    private LzzGaugingStationService lzzGaugingStationService;

    @Autowired
    private LzzPlatformTreeService lzzPlatformTreeService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OverallMsgService overallMsgService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeData = new SimpleDateFormat("yyyy-MM-dd HH:mm");



    @Override
    public RestResponse insertRainfallStationInfo(Date time) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("雨量站");
        List<LzzRainfallStation> rainfallStationList = new ArrayList<>();
        for(UserIdParam userPidParam :rainfallStationPidList){
            List<UserIdParam> userIdParams = pubUserService.selectRainfallStationIdList(userPidParam.getId());
            LzzRainfallStation station = new LzzRainfallStation();
            station.setStationName(userPidParam.getName());
            for(UserIdParam userIdParam :userIdParams){
                if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("雨量")){
                    ParamDto paramDto = lzzPlatformMapper.selectInfoByTime(userIdParam.getId(), sdf.format(time));
                    if(null != paramDto){
                        station.setTreeId(userPidParam.getId());
                        station.setRainfall(paramDto.getV());
                        station.setTime(paramDto.getTime());
                        station.setYear(String.valueOf(sdf.format(paramDto.getTime()).split("-")[0]));
                        station.setId(userPidParam.getName()+":"+paramDto.getTime().getTime());
                    }
                }
                if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("温度")){
                    ParamDto paramDto = lzzPlatformMapper.selectInfoByTime(userIdParam.getId(), sdf.format(time));
                    if(null != paramDto){
                        station.setTemperature(paramDto.getV());
                    }
                }
                redisUtil.set("lzz:rainfallStation:time:id"+timeData.format(station.getTime())+"|"+userPidParam.getId(),station.getRainfall(),86400 * 30);
            }
            if(StringUtils.isNotEmpty(station.getId())){
                rainfallStationList.add(station);
            }

        }
        boolean b = lzzRainfallStationService.saveOrUpdateBatch(rainfallStationList);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertReservoirLevel(Date time) {
        DecimalFormat df = new DecimalFormat("0.0");
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("水位站").stream().filter(t->t.getName().equals("库水位水位站")).collect(Collectors.toList());
        List<UserIdParam> userIdParams = pubUserService.selectReservoirLevelIdList(rainfallStationPidList.get(0).getId());
        LzzGaugingStation lzz = new LzzGaugingStation();
        for(UserIdParam userIdParam :userIdParams){
            if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("水位")){
                ParamDto paramDto = lzzPlatformMapper.selectInfoByTime(userIdParam.getId(), sdf.format(time));
                if(null != paramDto) {
                    lzz.setTreeId(rainfallStationPidList.get(0).getId());
                    lzz.setId("楼庄子库水位站:" + paramDto.getTime().getTime());
                    lzz.setGatherTime(paramDto.getTime());
                    lzz.setStationName("楼庄子库水位站");
                    lzz.setRelativeWaterLevel(paramDto.getV().doubleValue());
                    String lzzString = (String)redisUtil.get("storageCapacityCurvelzz");
                    if(StringUtils.isEmpty(lzzString)){
                        List<StorageCapacityCurve> tth = storageCapacityCurveService.lambdaQuery().eq(StorageCapacityCurve::getReservoir, "lzz").list();
                        redisUtil.set("storageCapacityCurvelzz", JSONObject.toJSONString(tth));
                        lzzString = JSONObject.toJSONString(tth);
                    }
                    List<StorageCapacityCurve> lzzList = JSONObject.parseArray(lzzString, StorageCapacityCurve.class);
                    String[] lzzSplit = paramDto.getV().toString().split("\\.");
                    List<String> strings = NumberUtil.roundDecimal(lzzSplit[0], lzzSplit[1]);
                    List<StorageCapacityCurve> LZZlist = lzzList.stream().filter(t -> t.getWaterLevel().compareTo(new BigDecimal(strings.get(0))) == 0 && df.format(t.getInterpolation()).equals(strings.get(1).length()<2?"0.0":strings.get(1).split("\\.")[0]+"."+strings.get(1).split("\\.")[1].substring(0,1))).collect(Collectors.toList());
                    if (null != LZZlist && LZZlist.size() > 0) {
                        StorageCapacityCurve storageCapacityCurve = LZZlist.get(0);
                        lzz.setStorageCapacity(storageCapacityCurve.getStorageCapacity().doubleValue());
                    } else {
                        lzz.setStorageCapacity(0.00);
                    }
                }
            }
            if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("温度")){
                ParamDto paramDto = lzzPlatformMapper.selectInfoByTime(userIdParam.getId(), sdf.format(time));
                if(null != paramDto) {
                    lzz.setTemperature(paramDto.getV().doubleValue());
                }
            }
        }
        if(StringUtils.isNotEmpty(lzz.getId())){
            boolean b = lzzGaugingStationService.saveOrUpdate(lzz);
            if(b){
                return RestResponse.ok("ok");
            }else {
                return RestResponse.no("error");
            }
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertGaugingStation(Date time) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("水位站").stream().filter(t->!t.getName().equals("库水位水位站")).collect(Collectors.toList());
        List<LzzGaugingStation> gaugingStationList = new ArrayList<>();
        for(UserIdParam userPidParam:rainfallStationPidList){
            List<UserIdParam> userIdParams = pubUserService.selectGaugingStationIdList(userPidParam.getId());
            LzzGaugingStation gaugingStation = new LzzGaugingStation();
            gaugingStation.setTreeId(userPidParam.getId());
            if(userPidParam.getName().equals("出库自动水位站")){
                gaugingStation.setStationName("楼庄子出库水位站");
            }
            if(userPidParam.getName().equals("入库自动水位站")){
                gaugingStation.setStationName("楼庄子入库水位站");
            }
            if(!userPidParam.getName().equals("出库自动水位站") && !userPidParam.getName().equals("入库自动水位站")){
                gaugingStation.setStationName(userPidParam.getName());
            }
            for(UserIdParam userIdParam:userIdParams){
                if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("水位")){
                    ParamDto paramDto = lzzPlatformMapper.selectInfoByTime(userIdParam.getId(), sdf.format(time));
                    if(null != paramDto) {
                        gaugingStation.setRelativeWaterLevel(paramDto.getV().doubleValue());
                        gaugingStation.setId(gaugingStation.getStationName()+":"+paramDto.getTime().getTime());
                        gaugingStation.setGatherTime(paramDto.getTime());
                    }
                }
                if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("温度")){
                    ParamDto paramDto = lzzPlatformMapper.selectInfoByTime(userIdParam.getId(), sdf.format(time));
                    if(null != paramDto) {
                        gaugingStation.setTemperature(paramDto.getV().doubleValue());
                    }
                }
                if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("流量")){
                    ParamDto paramDto = lzzPlatformMapper.selectInfoByTime(userIdParam.getId(), sdf.format(time));
                    if(null != paramDto) {
                        gaugingStation.setFlow(paramDto.getV().doubleValue());
                    }
                }
            }
            if(StringUtils.isNotEmpty(gaugingStation.getTreeId())){
                gaugingStationList.add(gaugingStation);
            }
        }
        boolean b = lzzGaugingStationService.saveOrUpdateBatch(gaugingStationList);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertLzzInfo(Date time) {
        ParamDto oneTemp = lzzPlatformMapper.selectLzzInfoByTime("9210201700600", sdf.format(time));
        ParamDto twoTemp = lzzPlatformMapper.selectLzzInfoByTime("9210201710600", sdf.format(time));
        LzzGaugingStation one = new LzzGaugingStation();
        one.setId("楼庄子水厂1号管道"+":"+oneTemp.getTime().getTime());
        one.setFlow(oneTemp.getV().doubleValue());
        one.setStationName("楼庄子水厂1号管道");
        one.setTreeId(oneTemp.getSenid());
        one.setGatherTime(oneTemp.getTime());
        boolean save1 = lzzGaugingStationService.save(one);
        LzzGaugingStation two = new LzzGaugingStation();
        two.setId("楼庄子水厂2号管道"+":"+twoTemp.getTime().getTime());
        two.setFlow(twoTemp.getV().doubleValue());
        two.setStationName("楼庄子水厂2号管道");
        two.setTreeId(twoTemp.getSenid());
        two.setGatherTime(twoTemp.getTime());
        boolean save2 = lzzGaugingStationService.save(two);
        if(save1 && save2){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertLzzBetweenTime(Date startTime, Date endTime) {
        List<LzzGaugingStation> lzzGaugingStationList = new ArrayList<>();
        List<ParamDto> one = lzzPlatformMapper.selectLzzInfoBetweenTime("9210201700600", sdf.format(startTime), sdf.format(endTime));
        List<ParamDto> two = lzzPlatformMapper.selectLzzInfoBetweenTime("9210201710600", sdf.format(startTime), sdf.format(endTime));
        for(ParamDto dto:one){
            LzzGaugingStation station = new LzzGaugingStation();
            station.setId("楼庄子水厂1号管道"+":"+dto.getTime().getTime());
            station.setFlow(dto.getV().doubleValue());
            station.setStationName("楼庄子水厂1号管道");
            station.setTreeId(dto.getSenid());
            station.setGatherTime(dto.getTime());
            station.setRecordTime(DateUtil.parse(sdf1.format(dto.getTime()),"yyyy-MM-dd"));
            lzzGaugingStationList.add(station);
            redisUtil.set("lzz:waterworks:1:"+timeData.format(dto.getTime()),dto.getV().doubleValue(),60*60*24*2);
        }
        for(ParamDto dto:two){
            LzzGaugingStation station = new LzzGaugingStation();
            station.setId("楼庄子水厂2号管道"+":"+dto.getTime().getTime());
            station.setFlow(dto.getV().doubleValue());
            station.setStationName("楼庄子水厂2号管道");
            station.setTreeId(dto.getSenid());
            station.setGatherTime(dto.getTime());
            station.setRecordTime(DateUtil.parse(sdf1.format(dto.getTime()),"yyyy-MM-dd"));
            lzzGaugingStationList.add(station);
            redisUtil.set("lzz:waterworks:2:"+timeData.format(dto.getTime()),dto.getV().doubleValue(),60*60*24*2);
        }


        boolean b = lzzGaugingStationService.saveOrUpdateBatch(lzzGaugingStationList);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertLzzKqRailBetweenTime(Date startTime, Date endTime) {
        List<LzzRainfallStation> rainfallStationList = new ArrayList<>();
        Date date = lzzRainFailMapper.selectNewTimeForKqRainFail();
        List<ParamDto> paramDtos = lzzPlatformMapper.selectInfoBetweenTimeForKq("9210201100100 ", sdf.format(date), sdf.format(endTime));
        if(paramDtos.size()>1){
            Comparator<ParamDto> comparing = Comparator.comparing(ParamDto::getTime);
            paramDtos.sort(comparing);
            for(int i= paramDtos.size();i>1;i--){
                ParamDto paramDto = paramDtos.get(i-1);
                LzzRainfallStation station = new LzzRainfallStation();
                station.setTreeId("9210201100100");
                station.setStationName("楼庄子库区自动雨量站");
                station.setRainfall(paramDto.getV().subtract(paramDtos.get(i-2).getV()));
                station.setTime(paramDto.getTime());
                station.setRecordTime(DateUtil.parse(sdf1.format(paramDto.getTime()),"yyyy-MM-dd"));
                station.setYear(String.valueOf(sdf.format(paramDto.getTime()).split("-")[0]));
                station.setId("楼庄子库区自动雨量站:"+paramDto.getTime().getTime());
                rainfallStationList.add(station);
            }
        }
        boolean b = lzzRainfallStationService.saveOrUpdateBatch(rainfallStationList);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertLzzKqRailBetweenTimeByMyself(Date startTime, Date endTime) {
        List<LzzRainfallStation> rainfallStationList = new ArrayList<>();
        List<ParamDto> paramDtos = lzzPlatformMapper.selectInfoBetweenTimeForKq("9210201100100 ", sdf.format(startTime), sdf.format(endTime));
        if(paramDtos.size()>1){
            Comparator<ParamDto> comparing = Comparator.comparing(ParamDto::getTime);
            paramDtos.sort(comparing);
            for(int i= 1;i<paramDtos.size();i++){
                ParamDto paramDto = paramDtos.get(i);
                LzzRainfallStation station = new LzzRainfallStation();
                station.setTreeId("9210201100100");
                station.setStationName("楼庄子库区自动雨量站");
                station.setRainfall(paramDto.getV().subtract(paramDtos.get(i-1).getV()));
                station.setTime(paramDto.getTime());
                station.setRecordTime(DateUtil.parse(sdf1.format(paramDto.getTime()),"yyyy-MM-dd"));
                station.setYear(String.valueOf(sdf.format(paramDto.getTime()).split("-")[0]));
                station.setId("楼庄子库区自动雨量站:"+paramDto.getTime().getTime());
                rainfallStationList.add(station);
            }
        }
        boolean b = lzzRainfallStationService.saveOrUpdateBatch(rainfallStationList);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertRainfallStationRainfallBetweenTime(Date startTime, Date endTime,String name) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList(name+"雨量站");
        List<LzzRainfallStation> result = new ArrayList<>();
        for(UserIdParam userPidParam :rainfallStationPidList){
            List<LzzRainfallStation> rainfallStationList = new ArrayList<>();
            List<UserIdParam> userIdParams = pubUserService.selectRainfallStationIdList(userPidParam.getId());
            for(UserIdParam userIdParam :userIdParams){
                if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("雨量")){
                    List<ParamDto> paramDtos = lzzPlatformMapper.selectInfoBetweenTime(userIdParam.getId(), sdf.format(startTime), sdf.format(endTime));
                    for(int i=1;i<paramDtos.size();i++){
                        ParamDto paramDto1 = paramDtos.get(i);
                        ParamDto paramDto0 = paramDtos.get(i-1);
                        LzzRainfallStation station = new LzzRainfallStation();
                        station.setTreeId(userPidParam.getId());
                        station.setStationName(userPidParam.getName());
                        station.setRainfall(paramDto1.getV().subtract(paramDto0.getV()));
                        station.setTime(paramDto1.getTime());
                        station.setRecordTime(DateUtil.parse(sdf1.format(paramDto1.getTime()),"yyyy-MM-dd"));
                        station.setYear(String.valueOf(sdf.format(paramDto1.getTime()).split("-")[0]));
                        station.setId(userPidParam.getName()+":"+paramDto1.getTime().getTime());
                        rainfallStationList.add(station);
                    }
                }
            }
            for(LzzRainfallStation station :rainfallStationList){
                if(StringUtils.isNotEmpty(station.getTreeId())){
                    result.add(station);
                }
            }
        }
        boolean b = lzzRainfallStationService.saveOrUpdateBatch(result);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertRainfallStationTemperatureBetweenTime(Date startTime, Date endTime,String name) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList(name+"雨量站");
        List<LzzRainfallStation> rainfallStationList = new ArrayList<>();
        for(UserIdParam userPidParam :rainfallStationPidList){
            List<UserIdParam> userIdParams = pubUserService.selectRainfallStationIdList(userPidParam.getId());
            for(UserIdParam userIdParam :userIdParams){
                if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("温度")){
                    List<ParamDto> paramDtos = lzzPlatformMapper.selectInfoBetweenTime(userIdParam.getId(), sdf.format(startTime), sdf.format(endTime));
                    for (ParamDto paramDto :paramDtos){
                        LzzRainfallStation station = new LzzRainfallStation();
                        station.setTemperature(paramDto.getV());
                        station.setId(userPidParam.getName()+":"+paramDto.getTime().getTime());
                        rainfallStationList.add(station);
                    }
                }
            }
        }
        boolean b = lzzRainfallStationService.saveOrUpdateBatch(rainfallStationList);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertRainfallStationInfoBetweenTime(Date startTime, Date endTime,String name) {
        RestResponse rainfall = this.insertRainfallStationRainfallBetweenTime(startTime, endTime,name);
        RestResponse temperature = this.insertRainfallStationTemperatureBetweenTime(startTime, endTime,name);
        if(rainfall.getCode()==200 && temperature.getCode()==200) {
            return RestResponse.ok("导入成功");
        }else {
            return RestResponse.no("导入失败");
        }
    }

    @Override
    public RestResponse insertReservoirLevelWaterLevelBetweenTime(Date startTime, Date endTime) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("水位站").stream().filter(t->t.getName().equals("库水位水位站")).collect(Collectors.toList());
        List<UserIdParam> userIdParams = pubUserService.selectReservoirLevelIdList(rainfallStationPidList.get(0).getId());
        List<LzzGaugingStation> reservoirLevelList = new ArrayList<>();
        for(UserIdParam userIdParam :userIdParams) {
            if (userIdParam.getName().substring(userIdParam.getName().length() - 2).contains("水位")) {
                List<ParamDto> paramDtoList = lzzPlatformMapper.selectInfoBetweenTime(userIdParam.getId(), sdf.format(startTime), sdf.format(endTime));
                for (ParamDto paramDto : paramDtoList) {
                    LzzGaugingStation lzz = new LzzGaugingStation();
                    lzz.setTreeId(rainfallStationPidList.get(0).getId());
                    lzz.setId("楼庄子库水位站:" + paramDto.getTime().getTime());
                    lzz.setGatherTime(paramDto.getTime());
                    lzz.setRecordTime(DateUtil.parse(sdf1.format(paramDto.getTime()),"yyyy-MM-dd"));
                    lzz.setStationName("楼庄子库水位站");
                    lzz.setRelativeWaterLevel(paramDto.getV().doubleValue());
                    String[] lzzSplit = paramDto.getV().toString().split("\\.");
                    List<String> strings = NumberUtil.roundDecimal(lzzSplit[0], lzzSplit[1]);
                    List<StorageCapacityCurve> LZZlist = storageCapacityCurveService.lambdaQuery().
                            eq(StorageCapacityCurve::getWaterLevel, strings.get(0)).
                            eq(StorageCapacityCurve::getInterpolation, strings.get(1)).
                            eq(StorageCapacityCurve::getReservoir, "lzz").
                            list();
                    if (null != LZZlist && LZZlist.size() > 0) {
                        StorageCapacityCurve storageCapacityCurve = LZZlist.get(0);
                        lzz.setStorageCapacity(storageCapacityCurve.getStorageCapacity().doubleValue());
                    } else {
                        lzz.setStorageCapacity(0.00);
                    }
                    if(StringUtils.isNotEmpty(lzz.getTreeId())){
                        reservoirLevelList.add(lzz);
                        redisUtil.set("lzz:waterLevel:"+timeData.format(paramDto.getTime()),lzz.getRelativeWaterLevel(),3600*24*2);
                        redisUtil.set("lzz:capacity:"+timeData.format(paramDto.getTime()),lzz.getStorageCapacity(),3600*24*2);
                    }
                }

            }
        }
        boolean b = lzzGaugingStationService.saveOrUpdateBatch(reservoirLevelList);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse<List<LzzGaugingStation>> getReservoirLevelWaterLevelBetweenTime(Date startTime, Date endTime) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("水位站").stream().filter(t->t.getName().equals("库水位水位站")).collect(Collectors.toList());
        List<UserIdParam> userIdParams = pubUserService.selectReservoirLevelIdList(rainfallStationPidList.get(0).getId());
        List<LzzGaugingStation> reservoirLevelList = new ArrayList<>();
        for(UserIdParam userIdParam :userIdParams) {
            if (userIdParam.getName().substring(userIdParam.getName().length() - 2).contains("水位")) {
                List<ParamDto> paramDtoList = lzzPlatformMapper.selectInfoBetweenTime(userIdParam.getId(), sdf.format(startTime), sdf.format(endTime));
                for (ParamDto paramDto : paramDtoList) {
                    LzzGaugingStation lzz = new LzzGaugingStation();
                    lzz.setTreeId(rainfallStationPidList.get(0).getId());
                    lzz.setId("楼庄子库水位站:" + paramDto.getTime().getTime());
                    lzz.setGatherTime(paramDto.getTime());
                    lzz.setStationName("楼庄子库水位站");
                    lzz.setRelativeWaterLevel(paramDto.getV().doubleValue());
                    String[] lzzSplit = paramDto.getV().toString().split("\\.");
                    List<String> strings = NumberUtil.roundDecimal(lzzSplit[0], lzzSplit[1]);
                    List<StorageCapacityCurve> LZZlist = storageCapacityCurveService.lambdaQuery().
                            eq(StorageCapacityCurve::getWaterLevel, strings.get(0)).
                            eq(StorageCapacityCurve::getInterpolation, strings.get(1)).
                            eq(StorageCapacityCurve::getReservoir, "lzz").
                            list();
                    if (null != LZZlist && LZZlist.size() > 0) {
                        StorageCapacityCurve storageCapacityCurve = LZZlist.get(0);
                        lzz.setStorageCapacity(storageCapacityCurve.getStorageCapacity().doubleValue());
                    } else {
                        lzz.setStorageCapacity(0.00);
                    }
                    reservoirLevelList.add(lzz);
                }

            }
        }
        if(reservoirLevelList.size()>0){
            return RestResponse.ok(reservoirLevelList);
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse<LzzGaugingStation> getReservoirLevelWaterLevelByTime(Date time) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("水位站").stream().filter(t->t.getName().equals("库水位水位站")).collect(Collectors.toList());
        List<UserIdParam> userIdParams = pubUserService.selectReservoirLevelIdList(rainfallStationPidList.get(0).getId());
        LzzGaugingStation lzz = null;
        for(UserIdParam userIdParam :userIdParams) {
            if (userIdParam.getName().substring(userIdParam.getName().length() - 2).contains("水位")) {
                ParamDto paramDto= lzzPlatformMapper.selectInfoByTime(userIdParam.getId(), sdf.format(time));
                lzz = new LzzGaugingStation();
                lzz.setTreeId(rainfallStationPidList.get(0).getId());
                lzz.setId("楼庄子库水位站:" + paramDto.getTime().getTime());
                lzz.setGatherTime(paramDto.getTime());
                lzz.setStationName("楼庄子库水位站");
                lzz.setRelativeWaterLevel(paramDto.getV().doubleValue());
                String[] lzzSplit = paramDto.getV().toString().split("\\.");
                List<String> strings = NumberUtil.roundDecimal(lzzSplit[0], lzzSplit[1]);
                List<StorageCapacityCurve> LZZlist = storageCapacityCurveService.lambdaQuery().
                        eq(StorageCapacityCurve::getWaterLevel, strings.get(0)).
                        eq(StorageCapacityCurve::getInterpolation, strings.get(1)).
                        eq(StorageCapacityCurve::getReservoir, "lzz").
                        list();
                if (null != LZZlist && LZZlist.size() > 0) {
                    StorageCapacityCurve storageCapacityCurve = LZZlist.get(0);
                    lzz.setStorageCapacity(storageCapacityCurve.getStorageCapacity().doubleValue());
                } else {
                    lzz.setStorageCapacity(0.00);
                }
            }
        }
        if(null !=lzz){
            return RestResponse.ok(lzz);
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse<Map<String,ParamDto>> getLzzInfoByTime(Date time) {
        Map<String,ParamDto> resultMap = new HashMap<>();
        ParamDto oneTemp = lzzPlatformMapper.selectLzzInfoByTime("9210201700600", sdf.format(time));
        ParamDto twoTemp = lzzPlatformMapper.selectLzzInfoByTime("9210201710600", sdf.format(time));
        if(null != oneTemp){
            resultMap.put("one",oneTemp);
        }
        if(null != twoTemp){
            resultMap.put("two",twoTemp);
        }
        if(resultMap.size()>0){
            return RestResponse.ok(resultMap);
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse<List<LzzRainfallStation>> selectRainfallStationDataByTime(Date time) {
        return null;
    }

    @Override
    public RestResponse<LzzGaugingStation> getLevelWaterLevelByTime(Date time) {
        return null;
    }

    @Override
    public RestResponse insertReservoirLevelTemperatureBetweenTime(Date startTime, Date endTime) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("水位站").stream().filter(t->t.getName().equals("库水位水位站")).collect(Collectors.toList());
        List<UserIdParam> userIdParams = pubUserService.selectReservoirLevelIdList(rainfallStationPidList.get(0).getId());
        List<LzzGaugingStation> reservoirLevelList = new ArrayList<>();
        for(UserIdParam userIdParam :userIdParams) {
            if (userIdParam.getName().substring(userIdParam.getName().length() - 2).contains("温度")) {
                List<ParamDto> paramDtoList = lzzPlatformMapper.selectInfoBetweenTime(userIdParam.getId(), sdf.format(startTime), sdf.format(endTime));
                for (ParamDto paramDto : paramDtoList) {
                    LzzGaugingStation lzz = new LzzGaugingStation();
                    lzz.setId("楼庄子库水位站:" + paramDto.getTime().getTime());
                    lzz.setTemperature(paramDto.getV().doubleValue());
                    reservoirLevelList.add(lzz);
                }
            }
        }
        boolean b = lzzGaugingStationService.saveOrUpdateBatch(reservoirLevelList);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertReservoirLevelBetweenTime(Date startTime, Date endTime) {
        RestResponse waterLevel = this.insertReservoirLevelWaterLevelBetweenTime(startTime, endTime);
        RestResponse temperature = this.insertReservoirLevelTemperatureBetweenTime(startTime, endTime);
        if(waterLevel.getCode()==200 && temperature.getCode()==200) {
            return RestResponse.ok("导入成功");
        }else {
            return RestResponse.no("导入失败");
        }
    }

    @Override
    public RestResponse insertGaugingStationWaterLevelBetweenTime(Date startTime, Date endTime) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("水位站").stream().filter(t->!t.getName().equals("库水位水位站")).collect(Collectors.toList());
        List<LzzGaugingStation> result = new ArrayList<>();
        for(UserIdParam userPidParam:rainfallStationPidList){
            List<LzzGaugingStation> gaugingStationList = new ArrayList<>();
            List<UserIdParam> userIdParams = pubUserService.selectGaugingStationIdList(userPidParam.getId());
            for(UserIdParam userIdParam:userIdParams){
                if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("水位")){
                    List<ParamDto> paramDtoList = lzzPlatformMapper.selectInfoBetweenTime(userIdParam.getId(), sdf.format(startTime),sdf.format(endTime));
                    for(ParamDto paramDto : paramDtoList){
                        LzzGaugingStation gaugingStation = new LzzGaugingStation();
                        gaugingStation.setTreeId(userPidParam.getId());
                        if(userPidParam.getName().equals("出库自动水位站")){
                            gaugingStation.setStationName("楼庄子出库水位站");
                        }
                        if(userPidParam.getName().equals("入库自动水位站")){
                            gaugingStation.setStationName("楼庄子入库水位站");
                        }
                        if(!userPidParam.getName().equals("出库自动水位站") && !userPidParam.getName().equals("入库自动水位站")){
                            gaugingStation.setStationName(userPidParam.getName());
                        }
                        gaugingStation.setRelativeWaterLevel(paramDto.getV().doubleValue());
                        gaugingStation.setId(gaugingStation.getStationName()+":"+paramDto.getTime().getTime());
                        gaugingStation.setGatherTime(paramDto.getTime());
                        gaugingStation.setRecordTime(DateUtil.parse(sdf1.format(paramDto.getTime()),"yyyy-MM-dd"));
                        if(StringUtils.isNotEmpty(gaugingStation.getTreeId())){
                            gaugingStationList.add(gaugingStation);
                        }
                    }
                }
            }
            result.addAll(gaugingStationList);
        }
        boolean b = lzzGaugingStationService.saveOrUpdateBatch(result);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertGaugingStationFlowBetweenTime(Date startTime, Date endTime) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("水位站").stream().filter(t->!t.getName().equals("库水位水位站")).collect(Collectors.toList());
        List<LzzGaugingStation> result = new ArrayList<>();
        for(UserIdParam userPidParam:rainfallStationPidList){
            List<LzzGaugingStation> gaugingStationList = new ArrayList<>();
            List<UserIdParam> userIdParams = pubUserService.selectGaugingStationIdList(userPidParam.getId());
            for(UserIdParam userIdParam:userIdParams){
                if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("流量")){
                    List<ParamDto> paramDtoList = lzzPlatformMapper.selectInfoBetweenTime(userIdParam.getId(), sdf.format(startTime),sdf.format(endTime));
                    for(ParamDto paramDto : paramDtoList){
                        LzzGaugingStation gaugingStation = new LzzGaugingStation();
                        if(userPidParam.getName().equals("出库自动水位站")){
                            gaugingStation.setStationName("楼庄子出库水位站");
                            redisUtil.set("lzz:out:"+timeData.format(paramDto.getTime()),paramDto.getV().doubleValue(),3600*24*2);
                        }
                        if(userPidParam.getName().equals("入库自动水位站")){
                            gaugingStation.setStationName("楼庄子入库水位站");
                            redisUtil.set("lzz:input:"+timeData.format(paramDto.getTime()),paramDto.getV().doubleValue(),3600*24*2);
                        }
                        if(!userPidParam.getName().equals("出库自动水位站") && !userPidParam.getName().equals("入库自动水位站")){
                            gaugingStation.setStationName(userPidParam.getName());
                        }
                        gaugingStation.setFlow(paramDto.getV().doubleValue());
                        String alertLevel = "";
                        if(gaugingStation.getFlow()!=null){
                            alertLevel = gaugingStation.getFlow()>=210?"FOUR":gaugingStation.getFlow()>=160?"THREE":gaugingStation.getFlow()>=120?"TWO":gaugingStation.getFlow()>=100?"ONE":"";
                        }
                        if(StringUtils.isNotEmpty(alertLevel)){
                            OverallMsg msg = new OverallMsg();
                            msg.setId(UUIDUtils.getUUID());
                            msg.setIsRead(0);
                            msg.setCreateTime(new Date());
                            msg.setCategory("告警");
                            WarnDto warnDto = new WarnDto();
                            warnDto.setTime(timeData.format(paramDto.getTime()));
                            warnDto.setFlow(gaugingStation.getFlow());
                            warnDto.setWarnType("flow");
                            warnDto.setType("waterStation");
                            warnDto.setName(gaugingStation.getStationName());
                            warnDto.setAlertLevel(alertLevel);
                            msg.setContent(JSONObject.toJSONString(warnDto));
                            List<OverallMsg> list =overallMsgService.lambdaQuery().apply("content = '"+msg.getContent()+"'").list();
                            if(list.isEmpty()){
                                waterResourceApi.sendMsg(msg.getContent());
                                overallMsgService.save(msg);
                            }
                        }
                        gaugingStation.setId(gaugingStation.getStationName()+":"+paramDto.getTime().getTime());
                        gaugingStationList.add(gaugingStation);
                    }
                }
            }
            result.addAll(gaugingStationList);
        }
        boolean b = lzzGaugingStationService.saveOrUpdateBatch(result);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertGaugingStationTemperatureBetweenTime(Date startTime, Date endTime) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("水位站").stream().filter(t->!t.getName().equals("库水位水位站")).collect(Collectors.toList());
        List<LzzGaugingStation> result = new ArrayList<>();
        for(UserIdParam userPidParam:rainfallStationPidList){
            List<LzzGaugingStation> gaugingStationList = new ArrayList<>();
            List<UserIdParam> userIdParams = pubUserService.selectGaugingStationIdList(userPidParam.getId());
            for(UserIdParam userIdParam:userIdParams){
                if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("温度")){
                    List<ParamDto> paramDtoList = lzzPlatformMapper.selectInfoBetweenTime(userIdParam.getId(), sdf.format(startTime),sdf.format(endTime));
                    for(ParamDto paramDto : paramDtoList){
                        LzzGaugingStation gaugingStation = new LzzGaugingStation();
                        if(userPidParam.getName().equals("出库自动水位站")){
                            gaugingStation.setStationName("楼庄子出库水位站");
                        }
                        if(userPidParam.getName().equals("入库自动水位站")){
                            gaugingStation.setStationName("楼庄子入库水位站");
                        }
                        if(!userPidParam.getName().equals("出库自动水位站") && !userPidParam.getName().equals("入库自动水位站")){
                            gaugingStation.setStationName(userPidParam.getName());
                        }
                        gaugingStation.setTemperature(paramDto.getV().doubleValue());
                        gaugingStation.setId(gaugingStation.getStationName()+":"+paramDto.getTime().getTime());
                        gaugingStationList.add(gaugingStation);
                    }
                }
            }
            result.addAll(gaugingStationList);
        }
        boolean b = lzzGaugingStationService.saveOrUpdateBatch(result);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertGaugingStationBetweenTime(Date startTime, Date endTime) {
        RestResponse flow = this.insertGaugingStationFlowBetweenTime(startTime, endTime);
        RestResponse waterLevel = this.insertGaugingStationWaterLevelBetweenTime(startTime, endTime);
        RestResponse temperature= this.insertGaugingStationTemperatureBetweenTime(startTime, endTime);
        if(waterLevel.getCode()==200 && temperature.getCode()==200 && flow.getCode()==200){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertLzzInputFlow() {
        List<LzzGaugingStation> list = lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getGatherTime,timeData.format(new Date())).
                in(LzzGaugingStation::getTreeId, "2023101301", "2023101302").orderByDesc(LzzGaugingStation::getGatherTime).last("limit 2").list();
        if(list.size()==2){
            LzzGaugingStation station = new LzzGaugingStation();
            station.setId("楼庄子进库流量:"+list.get(0).getGatherTime().getTime());
            station.setStationName("楼庄子进库流量");
            station.setTreeId("2023101303");
            LzzGaugingStation station1 = list.get(0);
            LzzGaugingStation station2 = list.get(1);
            Double waterLevel = NumberUtil.holdDecimal((
                    (station1.getRelativeWaterLevel()==null?0.00:station1.getRelativeWaterLevel()) +
                    (station1.getRelativeWaterLevelTwo() == null?0.00:station1.getRelativeWaterLevelTwo())+
                    (station1.getRelativeWaterLevelThree() == null?0.00:station1.getRelativeWaterLevelThree()) +
                    (station2.getRelativeWaterLevel() == null?0.00:station2.getRelativeWaterLevel()) +
                    (station2.getRelativeWaterLevelTwo() == null?0.00:station2.getRelativeWaterLevelTwo()) +
                    (station2.getRelativeWaterLevelThree() == null?0.00:station2.getRelativeWaterLevelThree())
                    ) / 6, 3);
            Double flowRate = NumberUtil.holdDecimal((
                    (station1.getFlowRate() == null?0.00:station1.getFlowRate())+
                    (station1.getFlowRateTwo() == null?0.00:station1.getFlowRateTwo())+
                    (station1.getFlowRateThree() == null?0.00:station1.getFlowRateThree())+
                    (station2.getFlowRate() == null?0.00:station2.getFlowRate())+
                    (station2.getFlowRateTwo() ==null?0.00:station2.getFlowRateTwo())+
                    (station2.getFlowRateThree() == null?0.00:station2.getFlowRateThree())
            ) / 6, 3);
            Double flow = NumberUtil.holdDecimal((
                    (station1.getFlow() == null?0.00:station1.getFlow()) +
                    (station1.getFlowTwo() == null?0.00:station1.getFlowTwo()) +
                    (station1.getFlowThree() == null?0.00:station1.getFlowThree()) +
                    (station2.getFlow() == null?0.00:station2.getFlow()) +
                    (station2.getFlowTwo() == null?0.00:station2.getFlowTwo()) +
                    (station2.getFlowThree() == null?0.00:station2.getFlowThree())
            ), 3);
            Double totalFlow = NumberUtil.holdDecimal((
                    (station1.getTotalFlow() == null?0.00:station1.getTotalFlow()) +
                    (station1.getTotalFlowTwo() == null?0.00:station1.getTotalFlowTwo()) +
                    (station1.getTotalFlowThree() == null?0.00:station1.getTotalFlowThree()) +
                    (station2.getTotalFlow() == null?0.00:station2.getTotalFlow()) +
                    (station2.getTotalFlowTwo() == null?0.00:station2.getTotalFlowTwo()) +
                    (station2.getTotalFlowThree() == null?0.00:station2.getTotalFlowThree())
            ), 3);
            station.setRelativeWaterLevel(waterLevel);
            station.setFlowRate(flowRate);
            station.setFlow(flow);
            String alertLevel = "";
            if(station.getFlow()!=null){
                alertLevel = station.getFlow()>=210?"FOUR":station.getFlow()>=160?"THREE":station.getFlow()>=120?"TWO":station.getFlow()>=100?"ONE":"";
            }
            if(StringUtils.isNotEmpty(alertLevel)){
                OverallMsg msg = new OverallMsg();
                msg.setId(UUIDUtils.getUUID());
                msg.setIsRead(0);
                msg.setSubject("waterLevel");
                msg.setCreateUser(station.getStationName());
                msg.setReceiveUser(alertLevel);
                msg.setCreateTime(new Date());
                msg.setCategory("告警");
                WarnDto warnDto = new WarnDto();
                warnDto.setTime(timeData.format(list.get(0).getGatherTime()));
                warnDto.setFlow(station.getFlow());
                warnDto.setWarnType("flow");
                warnDto.setType("waterStation");
                warnDto.setName(station.getStationName());
                warnDto.setAlertLevel(alertLevel);
                msg.setContent(JSONObject.toJSONString(warnDto));
                List<OverallMsg> overallMsgs = overallMsgService.lambdaQuery().apply("content = '"+msg.getContent()+"'").list();
                if(overallMsgs.isEmpty()){
                    waterResourceApi.sendMsg(msg.getContent());
                    overallMsgService.save(msg);
                }
            }
            station.setTotalFlow(totalFlow);
            station.setGatherTime(station1.getGatherTime());
            station.setRecordTime(DateUtil.parse(sdf1.format(list.get(0).getGatherTime()),"yyyy-MM-dd"));
            boolean save = lzzGaugingStationService.saveOrUpdate(station);
            if(save){
                return RestResponse.ok("ok");
            }else {
                return RestResponse.no("error");
            }
        }else {
            return RestResponse.ok("数据不全");
        }
    }

    @Override
    public RestResponse insertLzzInputFlowBetweenTime(String startTime, String endTime) {
        List<LzzGaugingStation> result = new ArrayList<>();
        List<LzzGaugingStation> list = lzzGaugingStationService.lambdaQuery().in(LzzGaugingStation::getTreeId, "2023101301", "2023101302").between(LzzGaugingStation::getRecordTime, startTime, endTime).list();
        Map<Date, List<LzzGaugingStation>> collect = list.stream().collect(Collectors.groupingBy(LzzGaugingStation::getGatherTime));
        collect.forEach((k,v)->{
            if(v.size()==2){
                LzzGaugingStation station = new LzzGaugingStation();
                station.setId("楼庄子进库流量:"+v.get(0).getGatherTime().getTime());
                station.setStationName("楼庄子进库流量");
                station.setTreeId("2023101303");
                LzzGaugingStation station1 = v.get(0);
                LzzGaugingStation station2 = v.get(1);
                Double waterLevel = NumberUtil.holdDecimal((station1.getRelativeWaterLevel() + station1.getRelativeWaterLevelTwo() + station1.getRelativeWaterLevelThree() +
                        station2.getRelativeWaterLevel() + station2.getRelativeWaterLevelTwo() + station2.getRelativeWaterLevelThree()) / 6, 3);
                Double flowRate = NumberUtil.holdDecimal((station1.getFlowRate() + station1.getFlowRateTwo() + station1.getFlowRateThree() +
                        station2.getFlowRate() + station2.getFlowRateTwo() + station2.getFlowRateThree()) / 6, 3);
                Double flow = NumberUtil.holdDecimal((station1.getFlow() + station1.getFlowTwo() + station1.getFlowThree() +
                        station2.getFlow() + station2.getFlowTwo() + station2.getFlowThree()), 3);
                Double totalFlow = NumberUtil.holdDecimal((station1.getTotalFlow() + station1.getTotalFlowTwo() + station1.getTotalFlowThree() +
                        station2.getTotalFlow() + station2.getTotalFlowTwo() + station2.getTotalFlowThree()), 3);
                station.setRelativeWaterLevel(waterLevel);
                station.setFlowRate(flowRate);
                station.setFlow(flow);
                station.setTotalFlow(totalFlow);
                station.setGatherTime(station1.getGatherTime());
                station.setRecordTime(DateUtil.parse(sdf1.format(v.get(0).getGatherTime()),"yyyy-MM-dd"));
                result.add(station);
            }
        });
        boolean b = lzzGaugingStationService.saveOrUpdateBatch(result);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertTree() {
        List<UserIdParam> userIdParams = pubUserService.selectPidList();
        List<LzzPlatformTree> lzzPlatformTrees = new ArrayList<>();
        userIdParams.forEach(t->{
            if(StringUtils.isNotEmpty(t.getName())){
                LzzPlatformTree tree = new LzzPlatformTree();
                BeanUtils.copyProperties(tree,t);
                lzzPlatformTrees.add(tree);
            }

        });
        boolean save = lzzPlatformTreeService.saveBatch(lzzPlatformTrees);
        if(save){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }
    @Override
    public RestResponse updateTree() {
        List<UserIdParam> userIdParams = pubUserService.selectPidList();
        List<LzzPlatformTree> lzzPlatformTrees = new ArrayList<>();
        userIdParams.forEach(t->{
            if(StringUtils.isNotEmpty(t.getName())) {
                LzzPlatformTree tree = new LzzPlatformTree();
                BeanUtils.copyProperties(t,tree);
                lzzPlatformTrees.add(tree);
            }
        });
        boolean save = lzzPlatformTreeService.saveOrUpdateBatch(lzzPlatformTrees);
        if(save){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertWarningInfo(String startTime, String endTime) {
        List<LzzGaugingStation> list = lzzGaugingStationService.lambdaQuery().between(LzzGaugingStation::getGatherTime, startTime, endTime).gt(LzzGaugingStation::getFlow, 100).list();
        if(!list.isEmpty()){
            List<OverallMsg> msgList = new ArrayList<>();
            for(LzzGaugingStation station:list){
                String alertLevel = "";
                if(station.getFlow()!=null){
                    alertLevel = station.getFlow()>=210?"FOUR":station.getFlow()>=160?"THREE":station.getFlow()>=120?"TWO":station.getFlow()>=100?"ONE":"";
                }
                if(StringUtils.isNotEmpty(alertLevel)){
                    OverallMsg msg = new OverallMsg();
                    msg.setId(UUIDUtils.getUUID());
                    msg.setIsRead(0);
                    msg.setSubject("waterLevel");
                    msg.setCreateUser(station.getStationName());
                    msg.setReceiveUser(alertLevel);
                    msg.setCreateTime(list.get(0).getGatherTime());
                    msg.setCategory("告警");
                    WarnDto warnDto = new WarnDto();
                    warnDto.setTime(timeData.format(list.get(0).getGatherTime()));
                    warnDto.setFlow(station.getFlow());
                    warnDto.setWarnType("flow");
                    warnDto.setType("waterStation");
                    warnDto.setName(station.getStationName());
                    warnDto.setAlertLevel(alertLevel);
                    msg.setContent(JSONObject.toJSONString(warnDto));
                    List<OverallMsg> overallMsgs = overallMsgService.lambdaQuery().apply("content = '"+msg.getContent()+"'").list();
                    if(overallMsgs.isEmpty()){
                        msgList.add(msg);
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

    private Date calculateTime(Date time,int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MINUTE,minute);
        Date date = calendar.getTime();
        return date;
    }
}
