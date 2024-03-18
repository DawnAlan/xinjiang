package com.cj.waterresources.func.modular.homePage.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity.IrrigatedPlatformTree;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity.IrrigatedSysOrgMapping;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.service.IrrigatedPlatformTreeService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.service.IrrigatedSysOrgMappingService;
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
    private final IrrigatedSysOrgMappingService irrigatedSysOrgMappingService;
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

    public RestResponse<List<WaterSituationRes>> waterSituation(Date dateTime) {
        List<IrrigatedPlatformTree> irrigatedPlatformTreeList = irrigatedPlatformTreeService.list();

        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        IrrigatedSysOrgMapping one = irrigatedSysOrgMappingService.lambdaQuery().eq(IrrigatedSysOrgMapping::getSysId, saBaseLoginUser.getOrgId()).one();
        if (one == null) {
            return RestResponse.no("组织id未对应");
        }
        String irrigatedId = one.getIrrigatedId();
//        String sysId = "123";
//        String irrigatedId = irrigatedSysOrgMappingService.lambdaQuery().eq(IrrigatedSysOrgMapping::getSysId, sysId).one().getIrrigatedId();

        List<String> superiorUnit = new ArrayList<>();
        List<String> unitId = new ArrayList<>();

        if (!irrigatedPlatformTreeList.stream().anyMatch(n -> n.getId().equals(irrigatedId))) {
            return RestResponse.no("组织对应平台id有误");
        }

        if (getChildStation(irrigatedPlatformTreeList, getChildStation(irrigatedPlatformTreeList, getRootStation(irrigatedPlatformTreeList))).stream().anyMatch(n -> n.getId().equals(irrigatedId))) {
            superiorUnit.add(irrigatedId);
        } else if (irrigatedPlatformTreeList.stream().filter(n -> n.getId().equals(irrigatedId)).anyMatch(n -> n.getName().contains("供水科"))){
            superiorUnit.addAll(getChildStation(irrigatedPlatformTreeList,
                    irrigatedPlatformTreeList.stream().filter(n -> n.getId().equals(irrigatedId)).collect(Collectors.toList()))
                    .stream().filter(n -> n.getName().contains("河东") || n.getName().contains("河西") || n.getName().contains("渠首")).map(IrrigatedPlatformTree::getId).collect(Collectors.toList()));
        } else {
            return RestResponse.no("组织对应平台id无权限");
        }
        for (String unit : superiorUnit) {
            unitId.addAll(getLeafStation(irrigatedPlatformTreeList, unit).stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()));
        }

        List<WaterSituationRes> waterSituationResList = new ArrayList<>();
        List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.lambdaQuery()
                .between(IrrigatedPlatformDataInfo::getMonitorTime, DateUtil.beginOfDay(dateTime), dateTime)
                .in(IrrigatedPlatformDataInfo::getMonitorId, unitId)
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

    private List<IrrigatedPlatformTree> getLeafStation(List<IrrigatedPlatformTree> irrigatedPlatformTreeList, String unitId) {
        List<IrrigatedPlatformTree> stations = getChildStation(irrigatedPlatformTreeList,
                getChildStation(irrigatedPlatformTreeList,
                        getChildStation(irrigatedPlatformTreeList,
                                getRootStation(irrigatedPlatformTreeList)))
                        .stream().filter(station -> station.getId().equals(unitId)).collect(Collectors.toList()));
        if (stations.stream().anyMatch(station -> storageWaterStationList.contains(station.getName()))) {
            stations = stations.stream().filter(station -> storageWaterStationList.contains(station.getName())).collect(Collectors.toList());
        }
        return stations;
    }

    private List<IrrigatedPlatformTree> getRootStation(List<IrrigatedPlatformTree> irrigatedPlatformTreeList) {
        return irrigatedPlatformTreeList.stream().filter(n -> n.getParentId().equals("0")).collect(Collectors.toList());
    }

    private List<IrrigatedPlatformTree> getChildStation(List<IrrigatedPlatformTree> allStations, List<IrrigatedPlatformTree> parentStations) {
        return allStations.stream().filter(n -> parentStations.stream().anyMatch(p -> p.getId().equals(n.getParentId()))).collect(Collectors.toList());
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
        List<IrrigatedPlatformTree> irrigatedPlatformTreeList = irrigatedPlatformTreeService.list();
        List<WaterSituationStationsRes> result = new ArrayList<>();
        getChildStation(irrigatedPlatformTreeList,
                getChildStation(irrigatedPlatformTreeList,
                        getRootStation(irrigatedPlatformTreeList)))
                .forEach(unit -> result.add(new WaterSituationStationsRes(unit.getId(), unit.getName())));
        return RestResponse.ok(result);
    }
}
