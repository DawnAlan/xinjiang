package com.cj.flood.func.modular.homePage.service;

import cn.hutool.core.date.DateUtil;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.homePage.bean.res.OverviewRes;
import com.cj.flood.func.modular.homePage.bean.res.WaterRainRes;
import com.cj.flood.func.modular.homePage.bean.res.WaterStorageOverviewRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity.IrrigatedPlatformTree;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.service.IrrigatedPlatformTreeService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzPlatformTree.entity.LzzPlatformTree;
import com.cj.middleDatabase.func.modular.lzz.lzzPlatformTree.service.LzzPlatformTreeService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.impl.LzzRainfallStationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FloodHomePageService {
    private final LzzRainfallStationServiceImpl lzzRainfallStationService;
    private final LzzPlatformTreeService lzzPlatformTreeService;
    private final IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;
    private final IrrigatedPlatformTreeService irrigatedPlatformTreeService;
    private final LzzGaugingStationService lzzGaugingStationService;

    private static final String PATTERN_DAY = "yyyy-MM-dd";
    private static final String PATTERN_MINUTE_OF_DAY = "yyyy-MM-dd HH:mm";

    public RestResponse<OverviewRes> overview(Date dateTime) {
        return RestResponse.ok(new OverviewRes(null, null, null));
    }

    public RestResponse<List<WaterRainRes>> rainfall(Date dateTime) {
        List<LzzPlatformTree> lzzPlatformTrees = lzzPlatformTreeService.lambdaQuery().ne(LzzPlatformTree::getPId, 0).list();
        List<LzzRainfallStation> rainfalls = lzzRainfallStationService.lambdaQuery()
                .between(LzzRainfallStation::getTime, DateUtil.beginOfDay(dateTime), dateTime)
                .list();
        List<WaterRainRes> waterRainResList = new ArrayList<>();
        Map<String, Double> collect = rainfalls.stream()
                .collect(Collectors.groupingBy(LzzRainfallStation::getStationName,
                        Collectors.summingDouble(n -> {
                            if (n.getRainfall() == null) {
                                return 0.000;
                            } else {
                                return n.getRainfall().setScale(3, RoundingMode.HALF_UP).doubleValue();
                            }
                        })));
        lzzPlatformTrees.forEach(tree -> {
            WaterRainRes waterRainRes = new WaterRainRes();
            waterRainRes.setStation(tree.getName());
            Double rainfall = collect.getOrDefault(tree.getName(), -1.0);
            waterRainRes.setValue(rainfall == -1.0 ? "无数据" : rainfall.toString());
            waterRainResList.add(waterRainRes);
        });
        return RestResponse.ok(waterRainResList);
    }

    public RestResponse<List<WaterRainRes>> waterSituation(Date dateTime) {
        List<WaterRainRes> waterRainResList = new ArrayList<>();
        List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.lambdaQuery()
                .between(IrrigatedPlatformDataInfo::getMonitorTime, DateUtil.beginOfDay(dateTime), dateTime)
                .in(IrrigatedPlatformDataInfo::getMonitorId, getLeafStation().stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()))
                .list();
        list.stream()
                .collect(Collectors.groupingBy(IrrigatedPlatformDataInfo::getMonitorName,
                        Collectors.maxBy((n1, n2) ->
                                DateUtil.compare(DateUtil.parse(n1.getMonitorTime(), PATTERN_MINUTE_OF_DAY), DateUtil.parse(n2.getMonitorTime(), PATTERN_MINUTE_OF_DAY)))))
                .forEach((k, v) -> {
                    String value = "0.000";
                    if (null != v.get().getSqMonitorFlow()) {
                        value = v.get().getSqMonitorFlow().toString();
                    }
                    waterRainResList.add(new WaterRainRes(k, value));
                });

        return RestResponse.ok(waterRainResList);
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

    public RestResponse<List<WaterStorageOverviewRes>> waterStorageOverview(Date dateTime) {
        List<WaterStorageOverviewRes> waterStorageOverviewResList = new ArrayList<>();
        waterStorageOverviewResList.add(getLzz(dateTime));
        waterStorageOverviewResList.add(getTth(dateTime));
        return RestResponse.ok(waterStorageOverviewResList);
    }

    private WaterStorageOverviewRes getLzz(Date dateTime) {
        List<LzzGaugingStation> lzzYear = lzzGaugingStationService.lambdaQuery()
                .between(LzzGaugingStation::getGatherTime, DateUtil.beginOfYear(dateTime), dateTime)
                .eq(LzzGaugingStation::getStationName, "楼庄子库水位站")
                .list();
        LzzGaugingStation lzzCurrent = lzzYear.stream().max(Comparator.comparing(LzzGaugingStation::getGatherTime)).get();
        if (!DateUtil.format(lzzCurrent.getGatherTime(), PATTERN_DAY).equals(DateUtil.format(dateTime, PATTERN_DAY))) {
            lzzCurrent = null;
        }
        Map<String, Double> storageCapacityDaily = lzzYear.stream()
                .collect(Collectors.groupingBy(n -> DateUtil.format(n.getGatherTime(), PATTERN_DAY),
                        Collectors.averagingDouble(LzzGaugingStation::getStorageCapacity)));
        List<Double> floodRetentionCapacityList = getFloodRetentionCapacityList(storageCapacityDaily);

        WaterStorageOverviewRes waterStorageOverviewResLzz = new WaterStorageOverviewRes();
        waterStorageOverviewResLzz.setWaterStorageName("楼庄子水库");
        waterStorageOverviewResLzz.setWaterLevel(lzzCurrent == null ? null : lzzCurrent.getRelativeWaterLevel());
        waterStorageOverviewResLzz.setInFlow(0.0);//
        waterStorageOverviewResLzz.setOutFlow(0.0);//
        waterStorageOverviewResLzz.setStorageCapacity(lzzCurrent == null ? null : lzzCurrent.getStorageCapacity());
        waterStorageOverviewResLzz.setYesterdayFloodRetentionCapacity(lzzCurrent == null ? null : floodRetentionCapacityList.get(floodRetentionCapacityList.size() - 1));
        waterStorageOverviewResLzz.setYearFloodRetentionCapacity(floodRetentionCapacityList.stream().mapToDouble(n -> n == null ? 0 : n).sum());
        return waterStorageOverviewResLzz;
    }

    private WaterStorageOverviewRes getTth(Date dateTime) {
        List<IrrigatedPlatformDataInfo> tthList = irrigatedPlatformDataInfoService.lambdaQuery()
                .between(IrrigatedPlatformDataInfo::getMonitorTime, DateUtil.beginOfYear(dateTime), dateTime)
                .in(IrrigatedPlatformDataInfo::getMonitorName, "头屯河水库水位", "入库流量", "出库流量")
                .list();
        Map<String, Optional<IrrigatedPlatformDataInfo>> tth = tthList.stream().
                collect(Collectors.groupingBy(IrrigatedPlatformDataInfo::getMonitorName,
                        Collectors.maxBy((n1, n2) ->
                                DateUtil.compare(DateUtil.parse(n1.getMonitorTime(), PATTERN_MINUTE_OF_DAY),
                                        DateUtil.parse(n2.getMonitorTime(), PATTERN_MINUTE_OF_DAY)))));

        Map<String, Double> storageCapacityDaily = tthList.stream()
                .filter(n -> n.getMonitorName().equals("头屯河水库水位"))
                .collect(Collectors.groupingBy(n -> DateUtil.format(DateUtil.parse(n.getMonitorTime(), PATTERN_MINUTE_OF_DAY), PATTERN_DAY),
                        Collectors.averagingDouble(n -> n.getSqCapacity() == null ? 0 : n.getSqCapacity())));
        List<Double> floodRetentionCapacityList = getFloodRetentionCapacityList(storageCapacityDaily);

        WaterStorageOverviewRes waterStorageOverviewResTth = new WaterStorageOverviewRes();
        waterStorageOverviewResTth.setWaterStorageName("头屯河水库");
        waterStorageOverviewResTth.setWaterLevel(tth.get("头屯河水库水位").get().getAvgWaterLevel());
        waterStorageOverviewResTth.setInFlow(tth.get("入库流量").get().getSqMonitorFlow());
        waterStorageOverviewResTth.setOutFlow(tth.get("出库流量").get().getSqMonitorFlow());
        waterStorageOverviewResTth.setStorageCapacity(tth.get("头屯河水库水位").get().getSqCapacity());//
        waterStorageOverviewResTth.setYesterdayFloodRetentionCapacity(floodRetentionCapacityList.get(floodRetentionCapacityList.size() - 1));
        waterStorageOverviewResTth.setYearFloodRetentionCapacity(floodRetentionCapacityList.stream().mapToDouble(n -> n == null ? 0 : n).sum());
        return waterStorageOverviewResTth;
    }

    private List<Double> getFloodRetentionCapacityList(Map<String, Double> storageCapacityDaily) {
        LinkedHashMap<String, Double> storageCapacityDailySorted = storageCapacityDaily.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal, LinkedHashMap::new));
        AtomicBoolean start = new AtomicBoolean(true);
        AtomicReference<Double> last = new AtomicReference<>(0.0);
        List<Double> floodRetentionCapacityList = new ArrayList<>();
        storageCapacityDailySorted.forEach((k, v) -> {
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

}
