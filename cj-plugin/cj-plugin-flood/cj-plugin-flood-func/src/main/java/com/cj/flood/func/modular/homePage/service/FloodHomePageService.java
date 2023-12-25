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
        return RestResponse.ok(new OverviewRes(1, 2, 3));
    }

    public RestResponse<List<WaterRainRes>> rainfall(Date dateTime) {
        List<LzzPlatformTree> lzzPlatformTrees = lzzPlatformTreeService.lambdaQuery().ne(LzzPlatformTree::getPId, 0).list();
        List<LzzRainfallStation> rainfalls = lzzRainfallStationService.lambdaQuery()
                .eq(LzzRainfallStation::getYear, DateUtil.year(dateTime))
                .le(LzzRainfallStation::getTime, dateTime)
                .list();
        List<WaterRainRes> waterRainResList = new ArrayList<>();
        Map<String, Double> collect = rainfalls.stream().filter(n -> DateUtil.format(n.getTime(), PATTERN_DAY).equals(DateUtil.format(dateTime, PATTERN_DAY)))
                .collect(Collectors.groupingBy(n -> n.getStationName() + "|" + n.getYear(),
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
            Double rainfall = collect.getOrDefault(tree.getName() + "|" + DateUtil.year(dateTime), -1.0);
            waterRainRes.setValue(rainfall == -1.0 ? "无数据" : rainfall.toString());
            waterRainResList.add(waterRainRes);
        });
        return RestResponse.ok(waterRainResList);
    }

    public RestResponse<List<WaterRainRes>> waterSituation(Date dateTime) {
        List<WaterRainRes> waterRainResList = new ArrayList<>();
        List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.lambdaQuery()
                .le(IrrigatedPlatformDataInfo::getMonitorTime, dateTime)
                .in(IrrigatedPlatformDataInfo::getMonitorId, getLeafStation().stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()))
                .list();
        list.stream()
                .filter(n ->
                        DateUtil.format(DateUtil.parse(n.getMonitorTime(), PATTERN_MINUTE_OF_DAY), PATTERN_DAY)
                                .equals(DateUtil.format(dateTime, PATTERN_DAY)))
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
        LzzGaugingStation lzz = lzzGaugingStationService.lambdaQuery()
                .le(LzzGaugingStation::getGatherTime, dateTime)
                .eq(LzzGaugingStation::getStationName, "楼庄子库水位站")
                .list()
                .stream().max(Comparator.comparing(LzzGaugingStation::getGatherTime))
                .get();
        WaterStorageOverviewRes waterStorageOverviewResLzz = new WaterStorageOverviewRes();
        waterStorageOverviewResLzz.setWaterStorageName("楼庄子水库");
        waterStorageOverviewResLzz.setWaterLevel(lzz.getRelativeWaterLevel());
        waterStorageOverviewResLzz.setInFlow(0.0);//
        waterStorageOverviewResLzz.setOutFlow(0.0);//
        waterStorageOverviewResLzz.setStorageCapacity(lzz.getStorageCapacity());
        waterStorageOverviewResLzz.setWaterRetentionCapacity(0.0);//
        waterStorageOverviewResList.add(waterStorageOverviewResLzz);

        Map<String, Optional<IrrigatedPlatformDataInfo>> tth = irrigatedPlatformDataInfoService.lambdaQuery()
                .le(IrrigatedPlatformDataInfo::getMonitorTime, dateTime)
                .in(IrrigatedPlatformDataInfo::getMonitorName, "头屯河水库水位", "入库流量", "出库流量")
                .list()
                .stream().collect(Collectors.groupingBy(IrrigatedPlatformDataInfo::getMonitorName,
                        Collectors.maxBy((n1, n2) ->
                                DateUtil.compare(DateUtil.parse(n1.getMonitorTime(), PATTERN_MINUTE_OF_DAY), DateUtil.parse(n2.getMonitorTime(), PATTERN_MINUTE_OF_DAY)))));
        WaterStorageOverviewRes waterStorageOverviewResTth = new WaterStorageOverviewRes();
        waterStorageOverviewResTth.setWaterStorageName("头屯河水库");
        waterStorageOverviewResTth.setWaterLevel(getDoubleNonNull(tth.get("头屯河水库水位").get().getAvgWaterLevel()));
        waterStorageOverviewResTth.setInFlow(getDoubleNonNull(tth.get("入库流量").get().getSqMonitorFlow()));
        waterStorageOverviewResTth.setOutFlow(getDoubleNonNull(tth.get("出库流量").get().getSqMonitorFlow()));
        waterStorageOverviewResTth.setStorageCapacity(getDoubleNonNull(tth.get("头屯河水库水位").get().getSqCapacity()));
        waterStorageOverviewResTth.setWaterRetentionCapacity(getDoubleNonNull(tth.get("头屯河水库水位").get().getWaterDaily()));
        waterStorageOverviewResList.add(waterStorageOverviewResTth);

        return RestResponse.ok(waterStorageOverviewResList);
    }

    private double getDoubleNonNull(Double d) {
        if (d == null) {
            return 0.000;
        }
        return d.doubleValue();
    }
}
