package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.LzzRainfallStationService;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.mapper.DayWaterSituationStatisticsTableSyylMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.entity.DayWaterSituationStatisticsTableSyyl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.service.DayWaterSituationStatisticsTableSyylService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity.DayWaterSituationStatisticsTableZcc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 上游雨量日水情统计表(DayWaterSituationStatisticsTableSyyl)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 16:00:16
 */
@Service("dayWaterSituationStatisticsTableSyylService")
public class DayWaterSituationStatisticsTableSyylServiceImpl extends ServiceImpl<DayWaterSituationStatisticsTableSyylMapper, DayWaterSituationStatisticsTableSyyl> implements DayWaterSituationStatisticsTableSyylService {

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private LzzRainfallStationService lzzRainfallStationService;

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public RestResponse<List<DayWaterSituationStatisticsTableSyyl>> selectList(String date) {
        List<DayWaterSituationStatisticsTableSyyl> dayWaterSituationStatisticsTableZccs = this.baseMapper.selectList(date);
        if(null != dayWaterSituationStatisticsTableZccs && dayWaterSituationStatisticsTableZccs.size()>0){
            return RestResponse.ok(dayWaterSituationStatisticsTableZccs);
        }else {
            return RestResponse.no("fail");
        }
    }

    @Override
    public RestResponse add(List<DayWaterSituationStatisticsTableSyyl> dayWaterSituationStatisticsTableSyylList) {
        Date date = new Date();
        Date yesterday = calculateTime(date, -1);
        List<LzzRainfallStation> lzzRainfallStations = lzzRainfallStationService.selectYesterday(sdf.format(yesterday)+" 01:00",sdf.format(date)+" 01:00");
        List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfo = irrigatedPlatformDataInfoService.selectOneByConditionByTime(sdf.format(date)+" 00:00");
        for(DayWaterSituationStatisticsTableSyyl t:dayWaterSituationStatisticsTableSyylList){
            t.setId(UUIDUtils.getUUID());
            String paramName = trendsTableParamService.getById(t.getTableHeadId()).getParamName();
            if(paramName.equals("萨尔达万")){
                List<LzzRainfallStation> rainfallStationList = lzzRainfallStations.stream().filter(s -> s.getStationName().equals("萨尔达万自动雨量站")).collect(Collectors.toList());
                if(null!= rainfallStationList && rainfallStationList.size()>0){
                    Double tempValue = 0.0;
                    List<BigDecimal> collect = rainfallStationList.stream().filter(l -> l.getRainfall() != null).map(LzzRainfallStation::getRainfall).collect(Collectors.toList());
                    for(BigDecimal b:collect){
                        tempValue += b.doubleValue();
                    }
                    t.setV(tempValue);
                }
            }
            if(paramName.equals("八一林场")){
                List<LzzRainfallStation> rainfallStationList = lzzRainfallStations.stream().filter(s -> s.getStationName().equals("八一林场自动雨量站")).collect(Collectors.toList());
                if(null!= rainfallStationList && rainfallStationList.size()>0){
                    Double tempValue = 0.0;
                    List<BigDecimal> collect = rainfallStationList.stream().filter(l -> l.getRainfall() != null).map(LzzRainfallStation::getRainfall).collect(Collectors.toList());
                    for(BigDecimal b:collect){
                        tempValue += b.doubleValue();
                    }
                    t.setV(tempValue);
                }
            }
            if(paramName.equals("黑沟")){
                List<LzzRainfallStation> rainfallStationList = lzzRainfallStations.stream().filter(s -> s.getStationName().equals("黑沟自动雨量站")).collect(Collectors.toList());
                if(null!= rainfallStationList && rainfallStationList.size()>0){
                    Double tempValue = 0.0;
                    List<BigDecimal> collect = rainfallStationList.stream().filter(l -> l.getRainfall() != null).map(LzzRainfallStation::getRainfall).collect(Collectors.toList());
                    for(BigDecimal b:collect){
                        tempValue += b.doubleValue();
                    }
                    t.setV(tempValue);
                }
            }
            if(paramName.equals("无名沟")){
                List<LzzRainfallStation> rainfallStationList = lzzRainfallStations.stream().filter(s -> s.getStationName().equals("无名沟自动雨量站")).collect(Collectors.toList());
                if(null!= rainfallStationList && rainfallStationList.size()>0){
                    Double tempValue = 0.0;
                    List<BigDecimal> collect = rainfallStationList.stream().filter(l -> l.getRainfall() != null).map(LzzRainfallStation::getRainfall).collect(Collectors.toList());
                    for(BigDecimal b:collect){
                        tempValue += b.doubleValue();
                    }
                    t.setV(tempValue);
                }
            }
            if(paramName.equals("团结一队")){
                List<IrrigatedPlatformDataInfo> platformDataInfoList = irrigatedPlatformDataInfo.stream().filter(s -> s.getMonitorName().equals("团结一队雨量站")).collect(Collectors.toList());
                if(null!= platformDataInfoList && platformDataInfoList.size()>0){
                    Double aDouble = platformDataInfoList.stream().filter(r -> r.getYqRainFallOne() != null).map(IrrigatedPlatformDataInfo::getYqRainFallTwentyFour).findFirst().get();
                    t.setV(aDouble==null?null:aDouble);
                }
            }
            if(paramName.equals("头屯河进库")){
                List<IrrigatedPlatformDataInfo> platformDataInfoList = irrigatedPlatformDataInfo.stream().filter(s -> s.getMonitorName().equals("头屯河水库雨量站")).collect(Collectors.toList());
                if(null!= platformDataInfoList && platformDataInfoList.size()>0){
                    Double aDouble = platformDataInfoList.stream().filter(r -> r.getYqRainFallOne() != null).map(IrrigatedPlatformDataInfo::getYqRainFallTwentyFour).findFirst().get();
                    t.setV(aDouble==null?null:aDouble);
                }
            }

            if(paramName.equals("小渠子")){
                List<IrrigatedPlatformDataInfo> platformDataInfoList = irrigatedPlatformDataInfo.stream().filter(s -> s.getMonitorName().equals("小渠子雨量站")).collect(Collectors.toList());
                if(null!= platformDataInfoList && platformDataInfoList.size()>0){
                    Double aDouble = platformDataInfoList.stream().filter(r -> r.getYqRainFallOne() != null).map(IrrigatedPlatformDataInfo::getYqRainFallTwentyFour).findFirst().get();
                    t.setV(aDouble==null?null:aDouble);
                }
            }
            if(paramName.equals("甘沟")){
                List<IrrigatedPlatformDataInfo> platformDataInfoList = irrigatedPlatformDataInfo.stream().filter(s -> s.getMonitorName().equals("小渠子雨量站")).collect(Collectors.toList());
                if(null!= platformDataInfoList && platformDataInfoList.size()>0){
                    Double aDouble = platformDataInfoList.stream().filter(r -> r.getYqRainFallOne() != null).map(IrrigatedPlatformDataInfo::getYqRainFallTwentyFour).findFirst().get();
                    t.setV(aDouble==null?null:aDouble);
                }
            }
            if(paramName.equals("三工滩")){
                List<IrrigatedPlatformDataInfo> platformDataInfoList = irrigatedPlatformDataInfo.stream().filter(s -> s.getMonitorName().equals("小渠子雨量站")).collect(Collectors.toList());
                if(null!= platformDataInfoList && platformDataInfoList.size()>0){
                    Double aDouble = platformDataInfoList.stream().filter(r -> r.getYqRainFallOne() != null).map(IrrigatedPlatformDataInfo::getYqRainFallTwentyFour).findFirst().get();
                    t.setV(aDouble==null?null:aDouble);
                }
            }
            if(paramName.equals("喀什沟")){
                List<LzzRainfallStation> rainfallStationList = lzzRainfallStations.stream().filter(s -> s.getStationName().equals("喀什沟自动雨量站")).collect(Collectors.toList());
                if(null!= rainfallStationList && rainfallStationList.size()>0){
                    Double tempValue = 0.0;
                    List<BigDecimal> collect = rainfallStationList.stream().filter(l -> l.getRainfall() != null).map(LzzRainfallStation::getRainfall).collect(Collectors.toList());
                    for(BigDecimal b:collect){
                        tempValue += b.doubleValue();
                    }
                    t.setV(tempValue);
                }
            }
            if(paramName.equals("制材厂")){
                List<LzzRainfallStation> rainfallStationList = lzzRainfallStations.stream().filter(s -> s.getStationName().equals("制材厂自动雨量站")).collect(Collectors.toList());
                if(null!= rainfallStationList && rainfallStationList.size()>0){
                    Double tempValue = 0.0;
                    List<BigDecimal> collect = rainfallStationList.stream().filter(l -> l.getRainfall() != null).map(LzzRainfallStation::getRainfall).collect(Collectors.toList());
                    for(BigDecimal b:collect){
                        tempValue += b.doubleValue();
                    }
                    t.setV(tempValue);
                }
            }
            if(paramName.equals("煤矿沟")){
                List<LzzRainfallStation> rainfallStationList = lzzRainfallStations.stream().filter(s -> s.getStationName().equals("煤矿沟自动雨量站")).collect(Collectors.toList());
                if(null!= rainfallStationList && rainfallStationList.size()>0){
                    Double tempValue = 0.0;
                    List<BigDecimal> collect = rainfallStationList.stream().filter(l -> l.getRainfall() != null).map(LzzRainfallStation::getRainfall).collect(Collectors.toList());
                    for(BigDecimal b:collect){
                        tempValue += b.doubleValue();
                    }
                    t.setV(tempValue);
                }
            }
            if(paramName.equals("宰尔德")){
                List<LzzRainfallStation> rainfallStationList = lzzRainfallStations.stream().filter(s -> s.getStationName().equals("宰尔德自动雨量站")).collect(Collectors.toList());
                if(null!= rainfallStationList && rainfallStationList.size()>0){
                    Double tempValue = 0.0;
                    List<BigDecimal> collect = rainfallStationList.stream().filter(l -> l.getRainfall() != null).map(LzzRainfallStation::getRainfall).collect(Collectors.toList());
                    for(BigDecimal b:collect){
                        tempValue += b.doubleValue();
                    }
                    t.setV(tempValue);
                }
            }
            if(paramName.equals("东南沟")){
                List<LzzRainfallStation> rainfallStationList = lzzRainfallStations.stream().filter(s -> s.getStationName().equals("东南沟自动雨量站")).collect(Collectors.toList());
                if(null!= rainfallStationList && rainfallStationList.size()>0){
                    Double tempValue = 0.0;
                    List<BigDecimal> collect = rainfallStationList.stream().filter(l -> l.getRainfall() != null).map(LzzRainfallStation::getRainfall).collect(Collectors.toList());
                    for(BigDecimal b:collect){
                        tempValue += b.doubleValue();
                    }
                    t.setV(tempValue);
                }
            }
            if(paramName.equals("加普沙")){
                List<LzzRainfallStation> rainfallStationList = lzzRainfallStations.stream().filter(s -> s.getStationName().equals("加普沙自动雨量站")).collect(Collectors.toList());
                if(null!= rainfallStationList && rainfallStationList.size()>0){
                    Double tempValue = 0.0;
                    List<BigDecimal> collect = rainfallStationList.stream().filter(l -> l.getRainfall() != null).map(LzzRainfallStation::getRainfall).collect(Collectors.toList());
                    for(BigDecimal b:collect){
                        tempValue += b.doubleValue();
                    }
                    t.setV(tempValue);
                }
            }

        }
        dayWaterSituationStatisticsTableSyylList.forEach(t->t.setId(UUIDUtils.getUUID()));
        boolean b = this.saveBatch(dayWaterSituationStatisticsTableSyylList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse delete(String ids) {
        List<String> collect = Arrays.stream(ids.split(",")).collect(Collectors.toList());
        boolean remove = this.lambdaUpdate().in(DayWaterSituationStatisticsTableSyyl::getId, collect).remove();
        if (remove) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(List<DayWaterSituationStatisticsTableSyyl> dayWaterSituationStatisticsTableSyylList) {
        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableSyylList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    private Date calculateTime(Date time,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.DATE,day);
        Date date = calendar.getTime();
        return date;
    }
}

