package com.cj.flood.func.modular.homePage.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.func.modular.homePage.bean.res.OverviewRes;
import com.cj.flood.func.modular.homePage.bean.res.WaterRainRes;
import com.cj.flood.func.modular.homePage.bean.res.WaterStorageOverviewRes;
import com.cj.middleDatabase.func.modular.a3.entity.DailyFloodRetentionCapacity;
import com.cj.middleDatabase.func.modular.a3.entity.DayWaterSituationStatisticsTable;
import com.cj.middleDatabase.func.modular.a3.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.middleDatabase.func.modular.a3.entity.DayWaterSituationStatisticsTableTth;
import com.cj.middleDatabase.func.modular.a3.service.DailyFloodRetentionCapacityService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    private final DailyFloodRetentionCapacityService dailyFloodRetentionCapacityService;
    private final RedisUtil redisUtil;
    @Resource(name = "DayWaterSituationStatisticsTableLzzServiceHomePage")
    private DayWaterSituationStatisticsTableLzzService dayWaterSituationStatisticsTableLzzService;
    @Resource(name = "DayWaterSituationStatisticsTableTthServiceHomePage")
    private final DayWaterSituationStatisticsTableTthService dayWaterSituationStatisticsTableTthService;

    private static final String PATTERN_DAY = "yyyy-MM-dd";
    private static final String PATTERN_HOUR_OF_DAY = "yyyy-MM-dd HH";
    private static final String PATTERN_MINUTE_OF_DAY = "yyyy-MM-dd HH:mm";
    private static final String PATTERN_SECOND_OF_DAY = "yyyy-MM-dd HH:mm:ss";
    private static final String PATTERN_HOUR = "HH";
    private static final String FLOOD_RETENTION_HOUR = "08";
    private static final String FLOOD_RETENTION_HOUR_A3 = "08:00";
    private static final String FLOOD_HOME_PAGE_STORAGE_OVERVIEW_REDIS_KEY = "FLOOD_HOME_PAGE:STORAGE_OVERVIEW:";
    private static final String FLOOD_HOME_PAGE_RAINFALL_REDIS_KEY = "FLOOD_HOME_PAGE:RAINFALL:";

    public RestResponse<OverviewRes> overview(Date dateTime) {
        return RestResponse.ok(new OverviewRes(null, null, null));
    }

    public RestResponse<List<WaterRainRes>> rainfall(Date dateTime) {
        if (!redisUtil.hasKey(FLOOD_HOME_PAGE_RAINFALL_REDIS_KEY + DateUtil.format(dateTime, PATTERN_HOUR_OF_DAY))) {
            rainfallSchedule(dateTime);
        }
        return RestResponse.ok((List<WaterRainRes>) redisUtil.get(FLOOD_HOME_PAGE_RAINFALL_REDIS_KEY + DateUtil.format(dateTime, PATTERN_HOUR_OF_DAY)));
    }

    public void rainfallSchedule(Date dateTime) {
        List<WaterRainRes> waterRainResList = new ArrayList<>();
        waterRainResList.addAll(lzzRainfall(dateTime));
        waterRainResList.addAll(tthRainfall(dateTime));
        redisUtil.removeAll(FLOOD_HOME_PAGE_RAINFALL_REDIS_KEY);
        redisUtil.set(FLOOD_HOME_PAGE_RAINFALL_REDIS_KEY + DateUtil.format(dateTime, PATTERN_HOUR_OF_DAY), waterRainResList, 60 * 60 * 2);
    }

    private List<WaterRainRes> lzzRainfall(Date dateTime) {
//        List<LzzRainfallStation> rainfalls = lzzRainfallStationService.getRecentlyRainfalls(DateUtil.format(dateTime, PATTERN_SECOND_OF_DAY));
//        List<WaterRainRes> waterRainResList = new ArrayList<>();
//        rainfalls.forEach(rain -> {
//            WaterRainRes waterRainRes = new WaterRainRes();
//            waterRainRes.setStation(rain.getStationName());
//            waterRainRes.setValue(rain.getRainfall().toString());
//            waterRainResList.add(waterRainRes);
//        });
//        return waterRainResList;
        List<LzzPlatformTree> lzzPlatformTrees = lzzPlatformTreeService.lambdaQuery().like(LzzPlatformTree::getName, "雨量").list();
        List<LzzRainfallStation> rainfalls = lzzRainfallStationService.getBaseMapper().selectList(
                new QueryWrapper<LzzRainfallStation>().select("station_name, sum(rainfall) rainfall").lambda()
                        .in(LzzRainfallStation::getTreeId, lzzPlatformTrees.stream().map(LzzPlatformTree::getId).collect(Collectors.toList()))
                        .between(LzzRainfallStation::getTime, DateUtil.beginOfDay(dateTime), dateTime)
                        .groupBy(LzzRainfallStation::getStationName));
        List<WaterRainRes> waterRainResList = new ArrayList<>();
        lzzPlatformTrees.forEach(tree -> {
            WaterRainRes waterRainRes = new WaterRainRes();
            waterRainRes.setStation(tree.getName());
            Double rainfall = rainfalls.stream().filter(n -> n.getStationName().equals(tree.getName())).findAny().orElse(new LzzRainfallStation(){{setRainfall(new BigDecimal(-1));}}).getRainfall().doubleValue();
            waterRainRes.setValue(rainfall == -1 ? "无数据" : rainfall.toString());
            waterRainResList.add(waterRainRes);
        });
        return waterRainResList;
    }

    private List<WaterRainRes> tthRainfall(Date dateTime) {
//        List<IrrigatedPlatformDataInfo> rainfalls = irrigatedPlatformDataInfoService.getRecentlyRainfalls(DateUtil.format(dateTime, PATTERN_SECOND_OF_DAY));
//        List<WaterRainRes> waterRainResList = new ArrayList<>();
//        rainfalls.forEach(rain -> {
//            WaterRainRes waterRainRes = new WaterRainRes();
//            waterRainRes.setStation(rain.getMonitorName());
//            waterRainRes.setValue(rain.getYqRainFallOne().toString());
//            waterRainResList.add(waterRainRes);
//        });
//        return waterRainResList;

        List<IrrigatedPlatformTree> irrigatedPlatformTrees = irrigatedPlatformTreeService.lambdaQuery().like(IrrigatedPlatformTree::getName, "雨量").list();
        List<IrrigatedPlatformDataInfo> rainfalls = irrigatedPlatformDataInfoService.getBaseMapper().selectList(
                new QueryWrapper<IrrigatedPlatformDataInfo>().select("monitor_name, sum(yq_rain_fall_one) yq_rain_fall_one").lambda()
                        .in(IrrigatedPlatformDataInfo::getMonitorId, irrigatedPlatformTrees.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList()))
                        .apply("to_date(MONITOR_TIME,{2}) >= to_date({0},{2}) and to_date(MONITOR_TIME,{2}) <= to_date({1},{2})",
                                DateUtil.format(DateUtil.beginOfDay(dateTime), PATTERN_SECOND_OF_DAY),
                                DateUtil.format(dateTime, PATTERN_SECOND_OF_DAY),
                                "yyyy-mm-dd hh24:mi:ss")
                        .groupBy(IrrigatedPlatformDataInfo::getMonitorName));
        List<WaterRainRes> waterRainResList = new ArrayList<>();
        irrigatedPlatformTrees.forEach(tree -> {
            WaterRainRes waterRainRes = new WaterRainRes();
            waterRainRes.setStation(tree.getName());
            Double rainfall = rainfalls.stream().filter(n -> n.getMonitorName().equals(tree.getName())).findAny().orElse(new IrrigatedPlatformDataInfo() {{setYqRainFallOne(-1d);}}).getYqRainFallOne();
            waterRainRes.setValue(rainfall == -1 ? "无数据" : rainfall.toString());
            waterRainResList.add(waterRainRes);
        });
        return waterRainResList;
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
                                DateUtil.compare(n1.getMonitorTime(), n2.getMonitorTime()))))
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
        setFloodRetention(lzz, dateTime, "0");
        waterStorageOverviewResList.add(lzz);
        waterStorageOverviewResList.add(getTth(dateTime));
        redisUtil.removeAll(FLOOD_HOME_PAGE_STORAGE_OVERVIEW_REDIS_KEY);
        redisUtil.set(FLOOD_HOME_PAGE_STORAGE_OVERVIEW_REDIS_KEY + DateUtil.format(dateTime, PATTERN_HOUR_OF_DAY), waterStorageOverviewResList, 60 * 60 * 2);
    }

    private void setFloodRetention(WaterStorageOverviewRes waterStorageOverviewRes, Date dateTime, String type) {
        List<DailyFloodRetentionCapacity> yesterdayFloodRetention = dailyFloodRetentionCapacityService.lambdaQuery()
                .eq(DailyFloodRetentionCapacity::getId, DateUtil.format(dateTime, PATTERN_DAY) + "-" + type).list();
        List<DailyFloodRetentionCapacity> yearFloodRetention = dailyFloodRetentionCapacityService.getBaseMapper().selectList(
                new QueryWrapper<DailyFloodRetentionCapacity>().select("sum(capacity) capacity").lambda()
                        .eq(DailyFloodRetentionCapacity::getStationType, type)
                        .apply("to_date(tm,{2}) >= to_date({0},{2}) and to_date(tm,{2}) <= to_date({1},{2})",
                                DateUtil.format(DateUtil.beginOfYear(dateTime), PATTERN_DAY),
                                DateUtil.format(dateTime, PATTERN_DAY),
                                "yyyy-mm-dd"));
        waterStorageOverviewRes.setYearFloodRetentionCapacity(yesterdayFloodRetention.size() == 0 ? null : yesterdayFloodRetention.get(0).getCapacity().doubleValue());
        waterStorageOverviewRes.setYearFloodRetentionCapacity(yearFloodRetention.get(0) == null ? 0 : yearFloodRetention.get(0).getCapacity().doubleValue());
    }

    public List<WaterStorageOverviewRes> waterStorageOverview(Date dateTime) {
        if (!redisUtil.hasKey(FLOOD_HOME_PAGE_STORAGE_OVERVIEW_REDIS_KEY + DateUtil.format(dateTime, PATTERN_HOUR_OF_DAY))) {
            waterStorageOverviewSchedule(dateTime);
        }
        return (List<WaterStorageOverviewRes>) redisUtil.get(FLOOD_HOME_PAGE_STORAGE_OVERVIEW_REDIS_KEY + DateUtil.format(dateTime, PATTERN_HOUR_OF_DAY));
    }

    private WaterStorageOverviewRes getLzz(Date dateTime) {
        Map<String, List<LzzGaugingStation>> current = lzzGaugingStationService.getCurrent(DateUtil.format(dateTime, PATTERN_DAY))
                .stream().collect(Collectors.groupingBy(LzzGaugingStation::getStationName));
        WaterStorageOverviewRes waterStorageOverviewResLzz = new WaterStorageOverviewRes();
        waterStorageOverviewResLzz.setWaterStorageName("楼庄子水库");
        waterStorageOverviewResLzz.setWaterLevel(current.get("楼庄子库水位站") == null ? null : current.get("楼庄子库水位站").get(0).getRelativeWaterLevel() < 0 ? "数据异常" : current.get("楼庄子库水位站").get(0).getRelativeWaterLevel().toString());
        waterStorageOverviewResLzz.setInFlow(current.get("楼庄子入库水位站") == null ? null : current.get("楼庄子入库水位站").get(0).getFlow());
        waterStorageOverviewResLzz.setOutFlow(current.get("楼庄子出库水位站") == null ? null : current.get("楼庄子出库水位站").get(0).getFlow());
        waterStorageOverviewResLzz.setStorageCapacity(current.get("楼庄子库水位站") == null ? null : current.get("楼庄子库水位站").get(0).getStorageCapacity());
        return waterStorageOverviewResLzz;
    }

    private WaterStorageOverviewRes getLzzA3(Date dateTime) {
        WaterStorageOverviewRes waterStorageOverviewResLzz = new WaterStorageOverviewRes();
        waterStorageOverviewResLzz.setWaterStorageName("楼庄子水库");

        boolean isDailyAvg = false;
        String sql = String.format("to_char(record_time, '%s') = '%s'", PATTERN_DAY, DateUtil.format(dateTime, PATTERN_DAY));
        String sql1 = String.format("SUBSTR(time, 0,2) <= '%s'", DateUtil.format(dateTime, PATTERN_HOUR));
        List<DayWaterSituationStatisticsTable> lzzToday = dayWaterSituationStatisticsTableLzzService.lambdaQuery()
                .ne(DayWaterSituationStatisticsTableLzz::getTime, "昨日均")
                .ne(DayWaterSituationStatisticsTableLzz::getTime, "今日均")
                .apply(sql)
                .apply(sql1)
                .list().stream().collect(Collectors.toList());
        if (lzzToday.size() == 0) {
            lzzToday = dayWaterSituationStatisticsTableLzzService.lambdaQuery()
                    .eq(DayWaterSituationStatisticsTableLzz::getTime, "昨日均")
                    .apply(sql)
                    .list().stream().collect(Collectors.toList());
            isDailyAvg = true;
        }
        if (lzzToday.size() == 0) {
            return waterStorageOverviewResLzz;
        }

        if (!isDailyAvg) {
            String todayMaxTm = lzzToday.stream().max(Comparator.comparingInt(t -> Integer.parseInt(t.getTime().substring(0, 2)))).orElse(new DayWaterSituationStatisticsTableLzz()).getTime();
            lzzToday = lzzToday.stream().filter(n -> n.getTime().equals(todayMaxTm)).collect(Collectors.toList());
        }

        DayWaterSituationStatisticsTable any = lzzToday.get(0);
        List<AThreeHeader> aThreeHeaders = JSON.parseArray(any.getFrontTableList(), AThreeHeader.class);
        String inFlowHead = findIdLoop(aThreeHeaders, Arrays.asList("进库", "进库流量"));
        String outFlowHead = findIdLoop(aThreeHeaders, Arrays.asList("出库", "流量", "河道"));
        String wlHead = findIdLoop(aThreeHeaders, Arrays.asList("库水位"), Arrays.asList("水位"));
        String capacityHead = findIdLoop(aThreeHeaders, Arrays.asList("库容"));

        Double wl = getV(lzzToday, wlHead);
        waterStorageOverviewResLzz.setWaterLevel(wl == null ? null : wl.toString());
        waterStorageOverviewResLzz.setInFlow(getV(lzzToday, inFlowHead));
        waterStorageOverviewResLzz.setOutFlow(getV(lzzToday, outFlowHead));
        waterStorageOverviewResLzz.setStorageCapacity(getV(lzzToday, capacityHead));
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
        Map<String, List<IrrigatedPlatformDataInfo>> tth = irrigatedPlatformDataInfoService.getCurrentDate(
                        DateUtil.format(DateUtil.beginOfDay(dateTime), PATTERN_SECOND_OF_DAY + ".0")
                        , DateUtil.format(dateTime, PATTERN_SECOND_OF_DAY + ".0"))
                .stream().collect(Collectors.groupingBy(IrrigatedPlatformDataInfo::getMonitorName));

        WaterStorageOverviewRes waterStorageOverviewResTth = new WaterStorageOverviewRes();
        waterStorageOverviewResTth.setWaterStorageName("头屯河水库");
        waterStorageOverviewResTth.setWaterLevel(tth.get("头屯河水库水位") == null ? null : Optional.of(tth.get("头屯河水库水位").get(0).getAvgWaterLevel()).orElse(0d).toString());
        waterStorageOverviewResTth.setInFlow(tth.get("入库流量") == null ? null : tth.get("入库流量").get(0).getSqMonitorFlow());
        //waterStorageOverviewResTth.setOutFlow(tth.get("出库流量") == null ? null : tth.get("出库流量").orElse(new IrrigatedPlatformDataInfo()).getSqMonitorFlow());
        waterStorageOverviewResTth.setOutFlow(getTthA3OutFlow(dateTime));
        waterStorageOverviewResTth.setStorageCapacity(tth.get("头屯河水库水位") == null ? null : tth.get("头屯河水库水位").get(0).getSqCapacity());
        setFloodRetention(waterStorageOverviewResTth, dateTime, "1");
        return waterStorageOverviewResTth;
    }

    private Double getTthA3OutFlow(Date dateTime) {
        String sql = String.format("to_char(record_time, '%s') = '%s'", PATTERN_DAY, DateUtil.format(dateTime, PATTERN_DAY));
        String sql1 = String.format("SUBSTR(time, 0,2) <= '%s'", DateUtil.format(dateTime, PATTERN_HOUR));

        boolean isDailyAvg = false;
        List<DayWaterSituationStatisticsTableTth> tthToday = dayWaterSituationStatisticsTableTthService.lambdaQuery()
                .ne(DayWaterSituationStatisticsTableTth::getTime, "今日均")
                .ne(DayWaterSituationStatisticsTableTth::getTime, "昨日均")
                .apply(sql)
                .apply(sql1)
                .list();
        if (tthToday.size() == 0) {
            tthToday = dayWaterSituationStatisticsTableTthService.lambdaQuery()
                    .eq(DayWaterSituationStatisticsTableTth::getTime, "昨日均")
                    .apply(sql)
                    .list().stream().collect(Collectors.toList());
            isDailyAvg = true;
        }
        if (tthToday.size() == 0) {
            return 0d;
        }

        List<DayWaterSituationStatisticsTable> collect;
        if (!isDailyAvg) {
            String todayMaxTm = tthToday.stream().max(Comparator.comparingInt(t -> Integer.parseInt(t.getTime().substring(0, 2)))).orElse(new DayWaterSituationStatisticsTableTth()).getTime();
            collect = tthToday.stream().filter(n -> n.getTime().equals(todayMaxTm)).collect(Collectors.toList());
        } else {
            collect = tthToday.stream().collect(Collectors.toList());
        }

        DayWaterSituationStatisticsTable any = collect.get(0);
        List<AThreeHeader> aThreeHeaders = JSON.parseArray(any.getFrontTableList(), AThreeHeader.class);

        String outFlow = findIdLoop(aThreeHeaders, Arrays.asList("出库流量", "河道流量"), Arrays.asList("出库", "流量", "河道流量"));
        return getV(collect, outFlow);
    }

    private Map<String, Double> getFloodRetentionCapacityListAndDate(Map<String, Double> storageWaterLevelDaily) {
        LinkedHashMap<String, Double> storageWaterLevelDailySorted = storageWaterLevelDaily.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal, LinkedHashMap::new));
        AtomicBoolean start = new AtomicBoolean(true);
        AtomicReference<Double> last = new AtomicReference<>(0.0);
        Map<String, Double> floodRetentionCapacityMap = new LinkedHashMap<>();
        storageWaterLevelDailySorted.forEach((k, v) -> {
            if (start.get()) {
                start.set(false);
                last.set(v);
                return;
            }
            Double floodRetentionCapacity = v - last.get();
            last.set(v);
            if (floodRetentionCapacity > 0) {
                floodRetentionCapacityMap.put(k, floodRetentionCapacity);
            } else {
                floodRetentionCapacityMap.put(k, 0.0);
            }
        });
        return floodRetentionCapacityMap;
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

    @Transactional
    public void calcLzzFloodRetention() {
        Date startTime = null;
        List<DailyFloodRetentionCapacity> toSave = new ArrayList<>();
        DailyFloodRetentionCapacity startRetention = dailyFloodRetentionCapacityService.lambdaQuery().eq(DailyFloodRetentionCapacity::getStationType, "3").one();
        if (startRetention != null) {
            startTime = DateUtil.parse(startRetention.getTm(), PATTERN_DAY);
        }

        List<LzzGaugingStation> lzzLast = lzzGaugingStationService.lambdaQuery()
                .eq(LzzGaugingStation::getStationName, "楼庄子库水位站")
                .apply(startTime != null, "GATHER_TIME >= to_date({0}, 'yyyy-mm-dd hh24:mi:ss')", DateUtil.format(startTime, PATTERN_SECOND_OF_DAY))
                .apply("to_char(GATHER_TIME, 'hh24') = '08'")
                .list();

        Map<String, Double> storageWaterLevelDaily = new HashMap<>();
        for (LzzGaugingStation lzz : lzzLast) {
            if (lzz.getRelativeWaterLevel() >= 0) {
                storageWaterLevelDaily.put(DateUtil.format(lzz.getGatherTime(), PATTERN_DAY), lzz.getStorageCapacity() == null ? 0 : lzz.getStorageCapacity());
            }
        }

        List<DayWaterSituationStatisticsTableLzz> lzzLastA3 = dayWaterSituationStatisticsTableLzzService.lambdaQuery()
                .eq(DayWaterSituationStatisticsTableLzz::getTime, "08:00")
                //.eq(DayWaterSituationStatisticsTableLzz::getTableHeadId, capacityHead)
                .apply(startTime != null, "RECORD_TIME >= to_date({0}, 'yyyy-mm-dd hh24:mi:ss')", DateUtil.format(startTime, PATTERN_SECOND_OF_DAY))
                .list();
        Map<Date, List<DayWaterSituationStatisticsTableLzz>> collect = lzzLastA3.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableLzz::getRecordTime));
        List<DayWaterSituationStatisticsTableLzz> lzzA3ToCalc = new ArrayList<>();
        collect.forEach((k, v) -> {
            DayWaterSituationStatisticsTable any = v.get(0);
            List<AThreeHeader> aThreeHeaders = JSON.parseArray(any.getFrontTableList(), AThreeHeader.class);
            String capacityHead = findIdLoop(aThreeHeaders, Arrays.asList("库容"));
            lzzA3ToCalc.add(v.stream().filter(n -> n.getTableHeadId().equals(capacityHead)).findFirst().orElse(new DayWaterSituationStatisticsTableLzz()));
        });

        for (DayWaterSituationStatisticsTableLzz lzzA3 : lzzA3ToCalc) {
            String time = DateUtil.format(lzzA3.getRecordTime(), PATTERN_DAY);
            if (!storageWaterLevelDaily.containsKey(time) || (storageWaterLevelDaily.containsKey(time) && storageWaterLevelDaily.get(time) == 0)) {
                storageWaterLevelDaily.put(time, lzzA3.getV() == null ? 0 : lzzA3.getV().doubleValue());
            }
        }
        getFloodRetentionCapacityListAndDate(storageWaterLevelDaily).forEach((k, v) -> {
            toSave.add(new DailyFloodRetentionCapacity(k + "-0", "0", k, BigDecimal.valueOf(v)));
        });

        if (toSave.size() == 0) {
            return;
        }
        if (startRetention == null) {
            startRetention = new DailyFloodRetentionCapacity(UUIDUtils.getUUID(), "3", toSave.get(toSave.size() - 1).getTm(), null);
            dailyFloodRetentionCapacityService.save(startRetention);
        } else {
            startRetention.setTm(toSave.get(toSave.size() - 1).getTm());
            dailyFloodRetentionCapacityService.updateById(startRetention);
        }
        dailyFloodRetentionCapacityService.saveBatch(toSave);
    }

    @Transactional
    public void calcTthFloodRetention() {
        Date startTime = null;
        List<DailyFloodRetentionCapacity> toSave = new ArrayList<>();
        DailyFloodRetentionCapacity startRetention = dailyFloodRetentionCapacityService.lambdaQuery().eq(DailyFloodRetentionCapacity::getStationType, "4").one();
        if (startRetention != null) {
            startTime = DateUtil.parse(startRetention.getTm(), PATTERN_DAY);
        }

        Map<String, Double> storageWaterLevelDaily = irrigatedPlatformDataInfoService.lambdaQuery()
                .eq(IrrigatedPlatformDataInfo::getMonitorName, "头屯河水库水位")
                .apply(startTime != null, "monitor_time >= to_date({0}, 'yyyy-mm-dd hh24:mi:ss')", DateUtil.format(startTime, PATTERN_SECOND_OF_DAY))
                .apply("SUBSTR(MONITOR_TIME, 12, 2) = '08'")
                .list().stream()
                .collect(Collectors.groupingBy(n -> DateUtil.format(n.getMonitorTime(),PATTERN_SECOND_OF_DAY).substring(0, 10),
                        Collectors.averagingDouble(n -> n.getSqCapacity() == null ? 0 : n.getSqCapacity())));

        getFloodRetentionCapacityListAndDate(storageWaterLevelDaily).forEach((k, v) -> {
            toSave.add(new DailyFloodRetentionCapacity(k + "-1", "1", k, BigDecimal.valueOf(v)));
        });

        if (toSave.size() == 0) {
            return;
        }
        String lastTime = toSave.get(toSave.size() - 1).getTm();
        Date now = new Date();
        if (lastTime.equals(DateUtil.format(now, PATTERN_DAY)) && (DateUtil.format(now, PATTERN_HOUR).equals("08") || DateUtil.format(now, PATTERN_HOUR).equals("09"))) {
            lastTime = DateUtil.format(DateUtil.offsetDay(DateUtil.parse(lastTime, PATTERN_DAY), -1), PATTERN_DAY);
        }
        if (startRetention == null) {
            startRetention = new DailyFloodRetentionCapacity(UUIDUtils.getUUID(), "4", lastTime, null);
        } else {
            startRetention.setTm(lastTime);
        }
        dailyFloodRetentionCapacityService.saveOrUpdate(startRetention);
        dailyFloodRetentionCapacityService.saveOrUpdateBatch(toSave);
    }
}
