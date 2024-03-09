package com.cj.dataSynchronization.func.modular.lzz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.common.util.NumberUtil;
import com.cj.common.util.RedisUtil;
import com.cj.dataSynchronization.func.modular.lzz.bean.ParamDto;
import com.cj.dataSynchronization.func.modular.lzz.bean.UserIdParam;
import com.cj.dataSynchronization.func.modular.lzz.mapper.LzzPlatformMapper;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LzzPlatformServiceImpl implements LzzPlatformService {


    @Autowired
    private PubUserService pubUserService;

    @Autowired
    private LzzRainfallStationService lzzRainfallStationService;

    @Autowired
    private LzzPlatformMapper lzzPlatformMapper;

    @Autowired
    private StorageCapacityCurveService storageCapacityCurveService;


    @Autowired
    private LzzGaugingStationService lzzGaugingStationService;

    @Autowired
    private LzzPlatformTreeService lzzPlatformTreeService;

    @Autowired
    private RedisUtil redisUtil;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                redisUtil.set("lzz:time:waterLevel:"+timeData.format(lzz.getGatherTime()),lzz.getRelativeWaterLevel(),86400 * 30);
                redisUtil.set("lzz:time:capacity:"+timeData.format(lzz.getGatherTime()),lzz.getStorageCapacity(),86400 * 30);
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
            if(StringUtils.isNotEmpty(gaugingStation.getId())){
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
    public RestResponse insertRainfallStationRainfallBetweenTime(Date startTime, Date endTime) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("雨量站");
        List<LzzRainfallStation> result = new ArrayList<>();
        for(UserIdParam userPidParam :rainfallStationPidList){
            List<LzzRainfallStation> rainfallStationList = new ArrayList<>();
            List<UserIdParam> userIdParams = pubUserService.selectRainfallStationIdList(userPidParam.getId());
            for(UserIdParam userIdParam :userIdParams){
                if(userIdParam.getName().substring(userIdParam.getName().length()-2).contains("雨量")){
                    List<ParamDto> paramDtos = lzzPlatformMapper.selectInfoBetweenTime(userIdParam.getId(), sdf.format(startTime), sdf.format(endTime));
                    for (ParamDto paramDto :paramDtos){
                        LzzRainfallStation station = new LzzRainfallStation();
                        station.setTreeId(userPidParam.getId());
                        station.setStationName(userPidParam.getName());
                        station.setRainfall(paramDto.getV());
                        station.setTime(paramDto.getTime());
                        station.setYear(String.valueOf(sdf.format(paramDto.getTime()).split("-")[0]));
                        station.setId(userPidParam.getName()+":"+paramDto.getTime().getTime());
                        rainfallStationList.add(station);
                    }
                }
            }
            result.addAll(rainfallStationList);
        }
        boolean b = lzzRainfallStationService.saveOrUpdateBatch(result);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertRainfallStationTemperatureBetweenTime(Date startTime, Date endTime) {
        List<UserIdParam> rainfallStationPidList = pubUserService.selectPidList("雨量站");
        List<LzzRainfallStation> result = new ArrayList<>();
        for(UserIdParam userPidParam :rainfallStationPidList){
            List<LzzRainfallStation> rainfallStationList = new ArrayList<>();
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
            result.addAll(rainfallStationList);
        }
        boolean b = lzzRainfallStationService.saveOrUpdateBatch(result);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertRainfallStationInfoBetweenTime(Date startTime, Date endTime) {
        RestResponse rainfall = this.insertRainfallStationRainfallBetweenTime(startTime, endTime);
        RestResponse temperature = this.insertRainfallStationTemperatureBetweenTime(startTime, endTime);
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
                        }
                        if(userPidParam.getName().equals("入库自动水位站")){
                            gaugingStation.setStationName("楼庄子入库水位站");
                        }
                        if(!userPidParam.getName().equals("出库自动水位站") && !userPidParam.getName().equals("入库自动水位站")){
                            gaugingStation.setStationName(userPidParam.getName());
                        }
                        gaugingStation.setFlow(paramDto.getV().doubleValue());
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
        RestResponse waterLevel = this.insertGaugingStationWaterLevelBetweenTime(startTime, endTime);
        RestResponse flow = this.insertGaugingStationFlowBetweenTime(startTime, endTime);
        RestResponse temperature= this.insertGaugingStationTemperatureBetweenTime(startTime, endTime);
        if(waterLevel.getCode()==200 && temperature.getCode()==200 && flow.getCode()==200){
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
                BeanUtils.copyProperties(tree, t);
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
}
