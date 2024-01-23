package com.cj.waterresources.func.modular.homePage.service;

import cn.hutool.core.date.DateUtil;
import com.cj.common.model.RestResponse;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity.IrrigatedPlatformTree;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.service.IrrigatedPlatformTreeService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.waterresources.func.modular.homePage.bean.res.OverviewRes;
import com.cj.waterresources.func.modular.homePage.bean.res.WaterSituationRes;
import com.cj.waterresources.func.modular.homePage.bean.res.WaterStorageOverviewRes;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsTotalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WaterResourceHomePageService {

    private final IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;
    private final IrrigatedPlatformTreeService irrigatedPlatformTreeService;
    private final LzzGaugingStationService lzzGaugingStationService;
    private final WaterFeeStatisticsTotalService waterFeeStatisticsTotalService;

    private static final String PATTERN_DAY = "yyyy-MM-dd";
    private static final String PATTERN_MINUTE_OF_DAY = "yyyy-MM-dd HH:mm";
    private static final String PATTERN_HOUR = "HH";
    private static final String FLOOD_RETENTION_HOUR = "08";

    public RestResponse<OverviewRes> overview(Date dateTime) {
        Long unpaidCount = waterFeeStatisticsTotalService.lambdaQuery()
                .eq(WaterFeeStatisticsTotal::getYear, DateUtil.year(dateTime))
                .eq(WaterFeeStatisticsTotal::getMonth, DateUtil.month(dateTime) + 1)
                .eq(WaterFeeStatisticsTotal::getTenDays, getTenDays(dateTime))
                .gt(WaterFeeStatisticsTotal::getUnpaidWaterFees, 0)
                .count();
        return RestResponse.ok(new OverviewRes(null, null, unpaidCount.intValue(), null, null, null));
    }

    public RestResponse<List<WaterSituationRes>> waterSituation(Date dateTime) {
        List<WaterSituationRes> waterSituationResList = new ArrayList<>();
        List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.lambdaQuery()
                .between(IrrigatedPlatformDataInfo::getMonitorTime, DateUtil.beginOfDay(dateTime), dateTime)
                .in(IrrigatedPlatformDataInfo::getMonitorId, getLeafStation().stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()))
                .list();
        list.stream()
                .collect(Collectors.groupingBy(IrrigatedPlatformDataInfo::getMonitorName,
                        Collectors.maxBy((n1, n2) ->
                                DateUtil.compare(DateUtil.parse(n1.getMonitorTime(), PATTERN_MINUTE_OF_DAY), DateUtil.parse(n2.getMonitorTime(), PATTERN_MINUTE_OF_DAY)))))
                .forEach((k, v) -> {
                    String flow = "无数据", waterLevel = "无数据";
                    if (null != v.get().getSqMonitorFlow()) {
                        flow = v.get().getSqMonitorFlow().toString();
                    }
                    if (null != v.get().getAvgWaterLevel()) {
                        waterLevel = v.get().getAvgWaterLevel().toString();
                    }
                    waterSituationResList.add(new WaterSituationRes(k, DateUtil.parse(v.get().getMonitorTime(), PATTERN_MINUTE_OF_DAY), waterLevel, flow));
                });

        return RestResponse.ok(waterSituationResList);
    }

    public RestResponse<List<WaterStorageOverviewRes>> waterStorageOverview(Date dateTime) {
        List<WaterStorageOverviewRes> waterStorageOverviewResList = new ArrayList<>();
        waterStorageOverviewResList.add(getLzz(dateTime));
        waterStorageOverviewResList.add(getTth(dateTime));
        return RestResponse.ok(waterStorageOverviewResList);
    }

    private List<IrrigatedPlatformTree> getLeafStation() {
        List<IrrigatedPlatformTree> list0 = irrigatedPlatformTreeService.lambdaQuery()
                .eq(IrrigatedPlatformTree::getParentId, "0").list();
        List<IrrigatedPlatformTree> list1 = irrigatedPlatformTreeService.lambdaQuery()
                .in(IrrigatedPlatformTree::getParentId,
                        list0.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()))
                .list();
        List<IrrigatedPlatformTree> list2 = irrigatedPlatformTreeService.lambdaQuery()
                .in(IrrigatedPlatformTree::getParentId,
                        list1.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()))
                .ne(IrrigatedPlatformTree::getName, "水库站")
                .list();
        List<IrrigatedPlatformTree> list3 = irrigatedPlatformTreeService.lambdaQuery()
                .in(IrrigatedPlatformTree::getParentId,
                        list2.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()))
                .list();
        return list3;
    }

    private String getTenDays(Date date) {
        int offsetTenDays = (DateUtil.dayOfMonth(date) - 1) / 10;
        if (offsetTenDays == 0) {
            return "上旬";
        }
        if (offsetTenDays == 1) {
            return "中旬";
        }
        return "下旬";
    }

    private WaterStorageOverviewRes getLzz(Date dateTime) {
        List<LzzGaugingStation> lzzYear = lzzGaugingStationService.lambdaQuery()
                .between(LzzGaugingStation::getGatherTime, DateUtil.offsetDay(DateUtil.beginOfYear(dateTime), -1), dateTime)
                .eq(LzzGaugingStation::getStationName, "楼庄子库水位站")
                .list();
        LzzGaugingStation lzzCurrent;
        if (lzzYear.size() == 0) {
            lzzCurrent = null;
        } else {
            lzzCurrent = lzzYear.stream().max(Comparator.comparing(LzzGaugingStation::getGatherTime)).get();
            if (!DateUtil.format(lzzCurrent.getGatherTime(), PATTERN_DAY).equals(DateUtil.format(dateTime, PATTERN_DAY))) {
                lzzCurrent = null;
            }
        }
        Map<String, Double> storageWaterLevelDaily = lzzYear.stream()
                .filter(n -> DateUtil.format(n.getGatherTime(), PATTERN_HOUR).equals(FLOOD_RETENTION_HOUR))
                .collect(Collectors.groupingBy(n -> DateUtil.format(n.getGatherTime(), PATTERN_DAY),
                        Collectors.averagingDouble(LzzGaugingStation::getRelativeWaterLevel)));
        List<Double> floodRetentionCapacityList = getFloodRetentionCapacityList(storageWaterLevelDaily);

        WaterStorageOverviewRes waterStorageOverviewResLzz = new WaterStorageOverviewRes();
        waterStorageOverviewResLzz.setWaterStorageName("楼庄子水库");
        waterStorageOverviewResLzz.setWaterLevel(lzzCurrent == null ? null : lzzCurrent.getRelativeWaterLevel() < 0 ? "数据异常" : lzzCurrent.getRelativeWaterLevel().toString());
        waterStorageOverviewResLzz.setInFlow(null);//
        waterStorageOverviewResLzz.setOutFlow(null);//
        waterStorageOverviewResLzz.setStorageCapacity(lzzCurrent == null ? null : lzzCurrent.getStorageCapacity());
        waterStorageOverviewResLzz.setYesterdayFloodRetentionCapacity(getLastFloodRetention(dateTime, storageWaterLevelDaily, floodRetentionCapacityList));
        waterStorageOverviewResLzz.setYearFloodRetentionCapacity(floodRetentionCapacityList.stream().mapToDouble(n -> n == null ? 0 : n).sum());
        return waterStorageOverviewResLzz;
    }

    private WaterStorageOverviewRes getTth(Date dateTime) {
        List<IrrigatedPlatformDataInfo> tthList = irrigatedPlatformDataInfoService.lambdaQuery()
                .between(IrrigatedPlatformDataInfo::getMonitorTime, DateUtil.offsetDay(DateUtil.beginOfYear(dateTime), -1), dateTime)
                .in(IrrigatedPlatformDataInfo::getMonitorName, "头屯河水库水位", "入库流量", "出库流量")
                .list();
        Map<String, Optional<IrrigatedPlatformDataInfo>> tth = tthList.stream().
                collect(Collectors.groupingBy(IrrigatedPlatformDataInfo::getMonitorName,
                        Collectors.maxBy((n1, n2) ->
                                DateUtil.compare(DateUtil.parse(n1.getMonitorTime(), PATTERN_MINUTE_OF_DAY),
                                        DateUtil.parse(n2.getMonitorTime(), PATTERN_MINUTE_OF_DAY)))));

        Map<String, Double> storageWaterLevelDaily = tthList.stream()
                .filter(n -> n.getMonitorName().equals("头屯河水库水位")
                        && DateUtil.format(DateUtil.parse(n.getMonitorTime(), PATTERN_MINUTE_OF_DAY), PATTERN_HOUR).equals(FLOOD_RETENTION_HOUR))
                .collect(Collectors.groupingBy(n -> DateUtil.format(DateUtil.parse(n.getMonitorTime(), PATTERN_MINUTE_OF_DAY), PATTERN_DAY),
                        Collectors.averagingDouble(n -> n.getSqWaterLevel() == null ? 0 : n.getSqWaterLevel())));
        List<Double> floodRetentionCapacityList = getFloodRetentionCapacityList(storageWaterLevelDaily);

        WaterStorageOverviewRes waterStorageOverviewResTth = new WaterStorageOverviewRes();
        waterStorageOverviewResTth.setWaterStorageName("头屯河水库");
        waterStorageOverviewResTth.setWaterLevel(tth.get("头屯河水库水位") == null ? null : tth.get("头屯河水库水位").orElse(new IrrigatedPlatformDataInfo()).getAvgWaterLevel().toString());
        waterStorageOverviewResTth.setInFlow(tth.get("入库流量") == null ? null : tth.get("入库流量").orElse(new IrrigatedPlatformDataInfo()).getSqMonitorFlow());
        waterStorageOverviewResTth.setOutFlow(tth.get("出库流量") == null ? null : tth.get("出库流量").orElse(new IrrigatedPlatformDataInfo()).getSqMonitorFlow());
        waterStorageOverviewResTth.setStorageCapacity(tth.get("头屯河水库水位") == null ? null : tth.get("头屯河水库水位").orElse(new IrrigatedPlatformDataInfo()).getSqCapacity());
        waterStorageOverviewResTth.setYesterdayFloodRetentionCapacity(getLastFloodRetention(dateTime, storageWaterLevelDaily, floodRetentionCapacityList));
        waterStorageOverviewResTth.setYearFloodRetentionCapacity(floodRetentionCapacityList.size() == 0 ? null : floodRetentionCapacityList.stream().mapToDouble(n -> n == null ? 0 : n).sum());
        return waterStorageOverviewResTth;
    }

    private List<Double> getFloodRetentionCapacityList(Map<String, Double> storageWaterLevelDaily) {
        LinkedHashMap<String, Double> storageWaterLevelDailySorted = storageWaterLevelDaily.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal, LinkedHashMap::new));
        AtomicBoolean start = new AtomicBoolean(true);
        AtomicReference<Double> last = new AtomicReference<>(0.0);
        List<Double> floodRetentionCapacityList = new ArrayList<>();
        storageWaterLevelDailySorted.forEach((k, v) -> {
            if (start.get()) {
                start.set(false);
                last.set(v);
                return;
            }
            Double floodRetentionCapacity = v - last.get();
            last.set(v);
            if (floodRetentionCapacity > 0) {
                floodRetentionCapacityList.add(floodRetentionCapacity);
            } else {
                floodRetentionCapacityList.add(null);
            }
        });
        return floodRetentionCapacityList;
    }

    private Double getLastFloodRetention(Date dateTime, Map<String, Double> storageWaterLevelDaily, List<Double> floodRetentionCapacityList) {
        Double yesterdayFloodRetention = null;
        if (floodRetentionCapacityList.size() > 0) {
            if (DateUtil.hour(dateTime, true) < Integer.parseInt(FLOOD_RETENTION_HOUR)) {
                if (storageWaterLevelDaily.containsKey(DateUtil.format(DateUtil.offsetDay(dateTime, -1), PATTERN_DAY))) {
                    yesterdayFloodRetention = floodRetentionCapacityList.get(floodRetentionCapacityList.size() - 1);
                }
            } else {
                if (storageWaterLevelDaily.containsKey(DateUtil.format(dateTime, PATTERN_DAY))) {
                    yesterdayFloodRetention = floodRetentionCapacityList.get(floodRetentionCapacityList.size() - 1);
                }
            }
        }
        return yesterdayFloodRetention;
    }

}
