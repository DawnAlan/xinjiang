package com.cj.flood.func.modular.homePage.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.flood.func.modular.homePage.bean.res.OverviewRes;
import com.cj.flood.func.modular.homePage.bean.res.WaterRainRes;
import com.cj.flood.func.modular.homePage.bean.res.WaterStorageOverviewRes;
import com.cj.middleDatabase.func.modular.a3.entity.DayWaterSituationStatisticsTable;
import com.cj.middleDatabase.func.modular.a3.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.middleDatabase.func.modular.a3.entity.DayWaterSituationStatisticsTableTth;
import com.cj.middleDatabase.func.modular.a3.service.DayWaterSituationStatisticsTableLzzService;
import com.cj.middleDatabase.func.modular.a3.service.DayWaterSituationStatisticsTableTthService;
import com.cj.middleDatabase.func.modular.a3.dto.AThreeHeader;
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
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    private final RedisUtil redisUtil;
    @Resource(name = "DayWaterSituationStatisticsTableLzzServiceHomePage")
    private DayWaterSituationStatisticsTableLzzService dayWaterSituationStatisticsTableLzzService;
    @Resource(name = "DayWaterSituationStatisticsTableTthServiceHomePage")
    private final DayWaterSituationStatisticsTableTthService dayWaterSituationStatisticsTableTthService;

    private static final String PATTERN_DAY = "yyyy-MM-dd";
    private static final String PATTERN_MINUTE_OF_DAY = "yyyy-MM-dd HH:mm";
    private static final String PATTERN_SECOND_OF_DAY = "yyyy-MM-dd HH:mm:ss";
    private static final String PATTERN_HOUR = "HH";
    private static final String FLOOD_RETENTION_HOUR = "08";
    private static final String FLOOD_RETENTION_HOUR_A3 = "08:00";
    private static final String FLOOD_HOME_PAGE_STORAGE_OVERVIEW_REDIS_KEY = "FLOOD_HOME_PAGE_STORAGE_OVERVIEW_REDIS_KEY";

    public RestResponse<OverviewRes> overview(Date dateTime) {
        return RestResponse.ok(new OverviewRes(null, null, null));
    }

    public RestResponse<List<WaterRainRes>> rainfall(Date dateTime) {
        List<WaterRainRes> waterRainResList = new ArrayList<>();
        waterRainResList.addAll(lzzRainfall(dateTime));
        waterRainResList.addAll(tthRainfall(dateTime));
        waterRainResList.addAll(lzzStorageRainfall(dateTime));
        return RestResponse.ok(waterRainResList);
    }

    private List<WaterRainRes> lzzRainfall(Date dateTime) {
        List<LzzPlatformTree> lzzPlatformTrees = lzzPlatformTreeService.lambdaQuery().like(LzzPlatformTree::getName, "雨量").list();
        List<LzzRainfallStation> rainfalls = lzzRainfallStationService.lambdaQuery()
                .in(LzzRainfallStation::getTreeId, lzzPlatformTrees.stream().map(LzzPlatformTree::getId).collect(Collectors.toList()))
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
        return waterRainResList;
    }

    private List<WaterRainRes> tthRainfall(Date dateTime) {
        List<IrrigatedPlatformTree> irrigatedPlatformTrees = irrigatedPlatformTreeService.lambdaQuery().like(IrrigatedPlatformTree::getName, "雨量").list();
        List<IrrigatedPlatformDataInfo> rainfalls = irrigatedPlatformDataInfoService.lambdaQuery()
                .in(IrrigatedPlatformDataInfo::getMonitorId, irrigatedPlatformTrees.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()))
//                .apply("to_date(MONITOR_TIME,'yyyy-mm-dd hh24:mi') >= to_date('{0}','yyyy-mm-dd hh24:mi:ss') and to_date(MONITOR_TIME,'yyyy-mm-dd hh24:mi') <= to_date('{1}','yyyy-mm-dd hh24:mi:ss')",
//                        DateUtil.format(DateUtil.beginOfDay(dateTime), PATTERN_SECOND_OF_DAY),
//                        DateUtil.format(dateTime, PATTERN_SECOND_OF_DAY))
                .list()
                .stream().filter(n -> DateUtil.parse(n.getMonitorTime(), PATTERN_MINUTE_OF_DAY).before(dateTime) &&
                        DateUtil.parse(n.getMonitorTime(), PATTERN_MINUTE_OF_DAY).after(DateUtil.beginOfDay(dateTime)))
                .collect(Collectors.toList());
        List<WaterRainRes> waterRainResList = new ArrayList<>();
        Map<String, Double> collect = rainfalls.stream()
                .collect(Collectors.groupingBy(IrrigatedPlatformDataInfo::getMonitorName,
                        Collectors.summingDouble(n -> {
                            if (n.getYqRainFallOne() == null) {
                                return 0.000;
                            } else {
                                return n.getYqRainFallOne();
                            }
                        })));
        irrigatedPlatformTrees.forEach(tree -> {
            WaterRainRes waterRainRes = new WaterRainRes();
            waterRainRes.setStation(tree.getName());
            Double rainfall = collect.getOrDefault(tree.getName(), -1.0);
            waterRainRes.setValue(rainfall == -1.0 ? "无数据" : rainfall.toString());
            waterRainResList.add(waterRainRes);
        });
        return waterRainResList;
    }

    private List<WaterRainRes> lzzStorageRainfall(Date dateTime) {
        return new ArrayList<>();
    }

    public RestResponse<List<WaterRainRes>> waterSituation(Date dateTime, String unitId) {
        List<WaterRainRes> waterRainResList = new ArrayList<>();
        List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.lambdaQuery()
                .between(IrrigatedPlatformDataInfo::getMonitorTime, DateUtil.beginOfDay(dateTime), dateTime)
                .in(IrrigatedPlatformDataInfo::getMonitorId, getLeafStation(unitId).stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()))
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

    public void waterStorageOverviewSchedule(Date dateTime) {
        List<WaterStorageOverviewRes> waterStorageOverviewResList = new ArrayList<>();
        WaterStorageOverviewRes lzz = getLzz(dateTime);
        if (lzz.getWaterLevel() == null || lzz.getWaterLevel().equals("数据异常")) {
            lzz = getLzzA3(dateTime);
        }
        waterStorageOverviewResList.add(lzz);
        waterStorageOverviewResList.add(getTth(dateTime));
        redisUtil.set(FLOOD_HOME_PAGE_STORAGE_OVERVIEW_REDIS_KEY, waterStorageOverviewResList, 1000 * 60 * 60 * 2);
    }

    public List<WaterStorageOverviewRes> waterStorageOverview(Date dateTime) {
        if (!redisUtil.hasKey(FLOOD_HOME_PAGE_STORAGE_OVERVIEW_REDIS_KEY)) {
            waterStorageOverviewSchedule(new Date());
        }
        return (List<WaterStorageOverviewRes>) redisUtil.get(FLOOD_HOME_PAGE_STORAGE_OVERVIEW_REDIS_KEY);
    }

    private WaterStorageOverviewRes getLzz(Date dateTime) {
        List<LzzGaugingStation> lzzYear = lzzGaugingStationService.lambdaQuery()
                .between(LzzGaugingStation::getGatherTime, DateUtil.beginOfYear(dateTime), dateTime)
                .eq(LzzGaugingStation::getStationName, "楼庄子库水位站")
                .list();
        LzzGaugingStation in = lzzGaugingStationService.lambdaQuery()
                .eq(LzzGaugingStation::getStationName, "楼庄子出库水位站")
                .apply("to_char(gather_time, {0}) = {1}", PATTERN_DAY, DateUtil.format(dateTime, PATTERN_DAY))
                .orderByDesc(LzzGaugingStation::getGatherTime)
                .last("limit 1")
                .one();
        LzzGaugingStation out = lzzGaugingStationService.lambdaQuery()
                .eq(LzzGaugingStation::getStationName, "楼庄子入库水位站")
                .apply("to_char(gather_time, {0}) = {1}", PATTERN_DAY, DateUtil.format(dateTime, PATTERN_DAY))
                .orderByDesc(LzzGaugingStation::getGatherTime)
                .last("limit 1")
                .one();

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
        waterStorageOverviewResLzz.setInFlow(in == null ? null : in.getFlow());
        waterStorageOverviewResLzz.setOutFlow(out == null ? null : out.getFlow());
        waterStorageOverviewResLzz.setStorageCapacity(lzzCurrent == null ? null : lzzCurrent.getStorageCapacity());
        waterStorageOverviewResLzz.setYesterdayFloodRetentionCapacity(getLastFloodRetention(dateTime, storageWaterLevelDaily, floodRetentionCapacityList));
        waterStorageOverviewResLzz.setYearFloodRetentionCapacity(floodRetentionCapacityList.stream().mapToDouble(n -> n == null ? 0 : n).sum());
        return waterStorageOverviewResLzz;
    }

    private WaterStorageOverviewRes getLzzA3(Date dateTime) {
        WaterStorageOverviewRes waterStorageOverviewResLzz = new WaterStorageOverviewRes();
        waterStorageOverviewResLzz.setWaterStorageName("楼庄子水库");
        String sql = "to_char(record_time, '" + PATTERN_DAY + "') = '" + DateUtil.format(dateTime, PATTERN_DAY) + "'";
        List<DayWaterSituationStatisticsTable> lzzToday = dayWaterSituationStatisticsTableLzzService.lambdaQuery()
                .ne(DayWaterSituationStatisticsTableLzz::getTime, "昨日均")
                .ne(DayWaterSituationStatisticsTableLzz::getTime, "今日均")
                .apply(sql)
                .orderByDesc(DayWaterSituationStatisticsTableLzz::getRecordTime, DayWaterSituationStatisticsTableLzz::getTime)
                .last("limit 9")
                .list().stream().collect(Collectors.toList());
        if (lzzToday.size() == 0) {
            lzzToday = dayWaterSituationStatisticsTableLzzService.lambdaQuery()
                    .eq(DayWaterSituationStatisticsTableLzz::getTime, "昨日均")
                    .apply(sql)
                    .list().stream().collect(Collectors.toList());
        }
        List<DayWaterSituationStatisticsTableLzz> lzzYear = dayWaterSituationStatisticsTableLzzService.lambdaQuery()
                .ge(DayWaterSituationStatisticsTableLzz::getRecordTime, DateUtil.beginOfYear(dateTime))
                .le(DayWaterSituationStatisticsTableLzz::getRecordTime, dateTime)
                .eq(DayWaterSituationStatisticsTableLzz::getTime, "08:00")
                .list();
        DayWaterSituationStatisticsTable any = dayWaterSituationStatisticsTableLzzService.lambdaQuery().last("limit 1").one();
        List<AThreeHeader> aThreeHeaders = JSON.parseArray(any.getFrontTableList(), AThreeHeader.class);
        String inFlowHead = findIdLoop(aThreeHeaders, Arrays.asList("进库", "进库流量"));
        String outFlowHead = findIdLoop(aThreeHeaders, Arrays.asList("出库", "流量", "河道"));
        String wlHead = findIdLoop(aThreeHeaders, Arrays.asList("库水位"), Arrays.asList("水位"));
        String capacityHead = findIdLoop(aThreeHeaders, Arrays.asList("库容"));

//        String todayMaxTm = lzzToday.stream().max(Comparator.comparingInt(t -> Integer.parseInt(t.getTime().substring(0, 2)))).get().getTime();
//        List<DayWaterSituationStatisticsTable> collect = lzzToday.stream().filter(n -> n.getTime().equals(todayMaxTm)).collect(Collectors.toList());

        Map<String, Double> storageWaterLevelDaily = lzzYear.stream()
                .filter(n -> n.getTableHeadId().equals(capacityHead))
                .collect(Collectors.groupingBy(n -> DateUtil.format(n.getRecordTime(), PATTERN_DAY),
                        Collectors.averagingDouble(n -> n.getV() == null ? 0.00 : n.getV().doubleValue())));
        List<Double> floodRetentionCapacityList = getFloodRetentionCapacityList(storageWaterLevelDaily);

        waterStorageOverviewResLzz.setWaterLevel(getV(lzzToday, wlHead) + "");
        waterStorageOverviewResLzz.setInFlow(getV(lzzToday, inFlowHead));
        waterStorageOverviewResLzz.setOutFlow(getV(lzzToday, outFlowHead));
        waterStorageOverviewResLzz.setStorageCapacity(getV(lzzToday, capacityHead));
        waterStorageOverviewResLzz.setYesterdayFloodRetentionCapacity(getLastFloodRetention(dateTime, storageWaterLevelDaily, floodRetentionCapacityList));
        waterStorageOverviewResLzz.setYearFloodRetentionCapacity(floodRetentionCapacityList.stream().mapToDouble(n -> n == null ? 0 : n).sum());
        return waterStorageOverviewResLzz;
    }

    private String findIdLoop(List<AThreeHeader> aThreeHeaders, List<String>... filters) {
        for (List<String> filter : filters) {
            String id = findId(aThreeHeaders, filter);
            if (StringUtils.hasText(id)){
                return id;
            }
        }
        return null;
    }

    private String findId(List<AThreeHeader> aThreeHeaders, List<String> filters) {
        List<AThreeHeader> childHeaders = aThreeHeaders;
        for (int i = 0; i < filters.size(); i++) {
            String filter = filters.get(i);
            AThreeHeader header = childHeaders.stream().filter(a -> a.getParamName().equals(filter)).findAny().orElse(null);
            if (header == null) {
                return null;
            }
            if (i == filters.size() - 1 || (childHeaders = header.getChildren()) == null) {
                return header.getId();
            }
        }
        return null;
    }

    private Double getV(List<DayWaterSituationStatisticsTable> collect, String headId) {
        BigDecimal v = collect.stream().filter(n -> n.getTableHeadId().equals(headId)).findAny().orElse(new DayWaterSituationStatisticsTableLzz()).getV();
        return v == null ? null : v.doubleValue();
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
        //waterStorageOverviewResTth.setOutFlow(tth.get("出库流量") == null ? null : tth.get("出库流量").orElse(new IrrigatedPlatformDataInfo()).getSqMonitorFlow());
        waterStorageOverviewResTth.setOutFlow(getTthA3OutFlow(dateTime));
        waterStorageOverviewResTth.setStorageCapacity(tth.get("头屯河水库水位") == null ? null : tth.get("头屯河水库水位").orElse(new IrrigatedPlatformDataInfo()).getSqCapacity());
        waterStorageOverviewResTth.setYesterdayFloodRetentionCapacity(getLastFloodRetention(dateTime, storageWaterLevelDaily, floodRetentionCapacityList));
        waterStorageOverviewResTth.setYearFloodRetentionCapacity(floodRetentionCapacityList.size() == 0 ? null : floodRetentionCapacityList.stream().mapToDouble(n -> n == null ? 0 : n).sum());
        return waterStorageOverviewResTth;
    }

    private Double getTthA3OutFlow(Date dateTime) {
        List<DayWaterSituationStatisticsTableTth> tthToday = dayWaterSituationStatisticsTableTthService.lambdaQuery()
                .eq(DayWaterSituationStatisticsTableTth::getRecordTime, DateUtil.beginOfDay(dateTime))
                .notLike(DayWaterSituationStatisticsTableTth::getTime, "日均")
                .list();
        DayWaterSituationStatisticsTable any = dayWaterSituationStatisticsTableTthService.lambdaQuery().last("limit 1").one();
        List<AThreeHeader> aThreeHeaders = JSON.parseArray(any.getFrontTableList(), AThreeHeader.class);

        String todayMaxTm = tthToday.stream().max(Comparator.comparingInt(t -> Integer.parseInt(t.getTime().substring(0, 2)))).orElse(new DayWaterSituationStatisticsTableTth()).getTime();
        List<DayWaterSituationStatisticsTable> collect = tthToday.stream().filter(n -> n.getTime().equals(todayMaxTm)).collect(Collectors.toList());
        String outFlow = findIdLoop(aThreeHeaders, Arrays.asList("出库流量", "河道流量"), Arrays.asList("出库", "流量", "河道流量"));
        return getV(collect, outFlow);
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
