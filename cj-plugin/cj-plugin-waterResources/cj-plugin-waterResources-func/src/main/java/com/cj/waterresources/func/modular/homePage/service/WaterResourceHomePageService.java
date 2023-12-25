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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WaterResourceHomePageService {

    private final IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;
    private final IrrigatedPlatformTreeService irrigatedPlatformTreeService;
    private final LzzGaugingStationService lzzGaugingStationService;

    private static final String PATTERN_DAY = "yyyy-MM-dd";
    private static final String PATTERN_MINUTE_OF_DAY = "yyyy-MM-dd HH:mm";

    public RestResponse<OverviewRes> overview(Date dateTime) {
        return RestResponse.ok(new OverviewRes(1, 2, 3, 4, 5, 6));
    }

    public RestResponse<List<WaterSituationRes>> waterSituation(Date dateTime) {
        List<WaterSituationRes> waterSituationResList = new ArrayList<>();
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
        waterStorageOverviewResTth.setWaterLevel(tth.get("头屯河水库水位").get().getAvgWaterLevel());
        waterStorageOverviewResTth.setInFlow(tth.get("入库流量").get().getSqMonitorFlow());
        waterStorageOverviewResTth.setOutFlow(tth.get("出库流量").get().getSqMonitorFlow());
        waterStorageOverviewResTth.setStorageCapacity(0.0);//
        waterStorageOverviewResTth.setWaterRetentionCapacity(tth.get("头屯河水库水位").get().getWaterDaily());
        waterStorageOverviewResList.add(waterStorageOverviewResTth);

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
}
