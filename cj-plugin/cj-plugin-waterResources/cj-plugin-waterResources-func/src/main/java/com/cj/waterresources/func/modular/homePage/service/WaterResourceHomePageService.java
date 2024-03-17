package com.cj.waterresources.func.modular.homePage.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.cj.common.model.RestResponse;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity.IrrigatedPlatformTree;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.service.IrrigatedPlatformTreeService;
import com.cj.waterresources.func.modular.homePage.bean.res.OverviewRes;
import com.cj.waterresources.func.modular.homePage.bean.res.WaterSituationRes;
import com.cj.waterresources.func.modular.homePage.bean.res.WaterSituationStationsRes;
import com.cj.waterresources.func.modular.homePage.bean.res.WaterStorageOverviewRes;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsTotalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.cj.flood.api.PredictionApi;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WaterResourceHomePageService {

    private final IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;
    private final IrrigatedPlatformTreeService irrigatedPlatformTreeService;
    private final WaterFeeStatisticsTotalService waterFeeStatisticsTotalService;
    private final PredictionApi predictionApi;

    private static final String PATTERN_MINUTE_OF_DAY = "yyyy-MM-dd HH:mm";
    private static final String PATTERN_SECOND_OF_DAY = "yyyy-MM-dd HH:mm:ss";

    public RestResponse<OverviewRes> overview(Date dateTime) {
        Long unpaidCount = waterFeeStatisticsTotalService.lambdaQuery()
                .eq(WaterFeeStatisticsTotal::getYear, DateUtil.year(dateTime))
                .eq(WaterFeeStatisticsTotal::getMonth, DateUtil.month(dateTime) + 1)
                .eq(WaterFeeStatisticsTotal::getTenDays, getTenDays(dateTime))
                .gt(WaterFeeStatisticsTotal::getUnpaidWaterFees, 0)
                .count();
        return RestResponse.ok(new OverviewRes(null, null, unpaidCount.intValue(), null, null, null));
    }

    public RestResponse<List<WaterSituationRes>> waterSituation(Date dateTime, String unitId) {
        List<WaterSituationRes> waterSituationResList = new ArrayList<>();
        List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.lambdaQuery()
                .between(IrrigatedPlatformDataInfo::getMonitorTime, DateUtil.beginOfDay(dateTime), dateTime)
                .in(IrrigatedPlatformDataInfo::getMonitorId, getLeafStation(unitId).stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()))
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
        return RestResponse.ok(JSON.parseArray(predictionApi.getWaterStorageOverview(DateUtil.format(dateTime, PATTERN_SECOND_OF_DAY)), WaterStorageOverviewRes.class));
    }

    private final static List<String> storageWaterStationList = new ArrayList() {{
        add("通泽清淤场取水口");
        add("八钢工业用水取水口(渠道)");
        add("井房伸缩水尺水位");
    }};

    private List<IrrigatedPlatformTree> getLeafStation(String unitId) {
        List<IrrigatedPlatformTree> stations = getChildStation(getChildStation(getChildStation(getRootStation())).stream().filter(station -> station.getId().equals(unitId)).collect(Collectors.toList()));
        if (stations.stream().anyMatch(station -> storageWaterStationList.contains(station.getName()))) {
            stations = stations.stream().filter(station -> storageWaterStationList.contains(station.getName())).collect(Collectors.toList());
        }
        return stations;
    }

    private List<IrrigatedPlatformTree> getRootStation() {
        return irrigatedPlatformTreeService.lambdaQuery()
                .eq(IrrigatedPlatformTree::getParentId, "0").list();
    }

    private List<IrrigatedPlatformTree> getChildStation(List<IrrigatedPlatformTree> stations) {
        return irrigatedPlatformTreeService.lambdaQuery()
                .in(IrrigatedPlatformTree::getParentId,
                        stations.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()))
                .list();
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

    public RestResponse<List<WaterSituationStationsRes>> getWaterSituationStations() {
        List<WaterSituationStationsRes> result = new ArrayList<>();
        getChildStation(getChildStation(getRootStation())).forEach(unit -> result.add(new WaterSituationStationsRes(unit.getId(), unit.getName())));
        return RestResponse.ok(result);
    }
}
