package com.cj.waterresources.func.modular.waterResourceAllcation.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.pojo.CommonResult;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.api.PredictionApi;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.model.func.core.util.MultipartFileUtil;
import com.cj.model.func.modular.curve.service.CurveService;
import com.cj.model.func.modular.entity.Flood;
import com.cj.model.func.modular.watertransfer.entity.DataInflowPrevent;
import com.cj.model.func.modular.watertransfer.entity.Excel2;
import com.cj.model.func.modular.watertransfer.entity.Waterdemand;
import com.cj.model.func.modular.watertransfer.function.OutResult;
import com.cj.model.func.modular.watertransfer.function.WaterResourceAssessment;
import com.cj.model.func.modular.watertransfer.req.AppraiseReq;
import com.cj.model.func.modular.watertransfer.req.WaterTransferReq;
import com.cj.model.func.modular.watertransfer.res.ResOption;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.service.DayWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.TenDayWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalService;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.dto.IncomingWaterForecastDto;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.ViewModelReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.WaterResourceAllocationAddReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.WaterResourceAllocationQueryReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.res.ViewModelRes;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.res.WaterAllocationComparisonSelectionRes;
import com.cj.waterresources.func.modular.waterResourceAllcation.entity.AllocationDisplayData;
import com.cj.waterresources.func.modular.waterResourceAllcation.entity.IncomingWaterForecast;
import com.cj.waterresources.func.modular.waterResourceAllcation.mapper.WaterResourceAllocationMapper;
import com.cj.waterresources.func.modular.waterResourceAllcation.entity.WaterResourceAllocation;
import com.cj.waterresources.func.modular.waterResourceAllcation.service.WaterResourceAllocationService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 水资源调配模型表(WaterResourceAllocation)表服务实现类
 *
 * @author makejava
 * @since 2023-11-14 17:34:50
 */
@Service("waterResourceAllocationService")
@AllArgsConstructor
public class WaterResourceAllocationServiceImpl extends ServiceImpl<WaterResourceAllocationMapper, WaterResourceAllocation> implements WaterResourceAllocationService {

    private final PredictionApi predictionApi;
    private final MinioUtils minioUtils;
    private final CurveService curveService;
    private final YearWaterUsePlanTrunkCanalService yearWaterUsePlanTrunkCanalService;
    private final MonthWaterUsePlanService monthWaterUsePlanService;
    private final TenDayWaterUsePlanService tenDayWaterUsePlanService;
    private final DayWaterUsePlanService dayWaterUsePlanService;

    @Override
    public RestResponse<List<IncomingWaterForecastDto>> getIncomingWaterForecastListByTime(String startTime, String endTime, Integer bucketType) {
        try {
            String programmeListByTime = predictionApi.getProgrammeListByTime(startTime, endTime);
            CommonResult commonResult = JSONObject.parseObject(programmeListByTime, CommonResult.class);
            if (StringUtils.hasText(commonResult.getMsg())) {
                return RestResponse.no(commonResult.getMsg());
            }
            // todo 来水预报开始时间/结束时间应该跟调配起始时间一致
            //programmeListByTime = filterForecast(JSONObject.parseArray(programmeListByTime, IncomingWaterForecast.class), startTime, endTime, bucketType);
            List<IncomingWaterForecastDto> incomingWaterForecastDtos = JSONObject.parseArray(programmeListByTime, IncomingWaterForecastDto.class);

            if (null != incomingWaterForecastDtos && incomingWaterForecastDtos.size() > 0) {
                return RestResponse.ok(incomingWaterForecastDtos);
            } else {
                return RestResponse.no("暂无相关数据，请前往防洪业务系统的来水预报新建符合条件的模型结果");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("查询错误");
        }
    }

    private String filterForecast(List<IncomingWaterForecast> forecasts, String startTime, String endTime, Integer bucketType) {
        String format;
        DateField dateField;
        //月
        if (bucketType == 1) {
            format = "yyyy-MM";
            dateField = DateField.MONTH;
            //旬
        } else if (bucketType == 2) {
            // todo 旬的判断逻辑
            format = "yyyy-MM-dd";
            dateField = DateField.MONTH;
            //日
        } else {
            format = "yyyy-MM-dd";
            dateField = DateField.DAY_OF_MONTH;
        }

        return JSONObject.toJSONString(forecasts.stream().filter(n ->
                n.getPeriodTimeType() == bucketType
                        && DateUtil.format(n.getPredictionTime(), format).equals(DateUtil.format(DateUtil.parse(startTime), format))
                        && DateUtil.format(DateUtil.parse(startTime).offset(dateField, n.getPeriodTimeStep() * n.getPeriodTimeNum()), format)
                        .equals(DateUtil.format(DateUtil.parse(endTime), format))).collect(Collectors.toList()));
    }

    @Override
    public RestResponse generativeModel(WaterResourceAllocationAddReq req) {
        WaterResourceAllocation waterResourceAllocation = new WaterResourceAllocation();
        BeanUtils.copyProperties(req, waterResourceAllocation);
        Date now = new Date();
        waterResourceAllocation.setId(UUIDUtils.getUUID());
        waterResourceAllocation.setDel(0);
        // todo 登陆用户
        waterResourceAllocation.setCreateBy(null);
        waterResourceAllocation.setCreateTime(now);

        WaterTransferReq waterTransferReq = new WaterTransferReq();
        List<Flood> floods = getListFromMinio(req.getInflowDataAddress(), Flood.class);
        floods = floods.stream().filter(f -> f.getTime().getTime() <= req.getWaterDistributionEndTime().getTime()
                && f.getTime().getTime() >= req.getWaterDistributionStartTime().getTime()).collect(Collectors.toList());
        List<DataInflowPrevent> dataInflowPrevents = JSONObject.parseArray(JSONObject.toJSONString(floods), DataInflowPrevent.class);
        List<DataInflowPrevent> lzzEntryStation = dataInflowPrevents.stream().filter(t -> t.getLocation().equals("楼庄子")).collect(Collectors.toList());
        List<DataInflowPrevent> interval = dataInflowPrevents.stream().filter(t -> t.getLocation().equals("楼头区间")).collect(Collectors.toList());
        Map<String, List<DataInflowPrevent>> data = new HashMap<>();
        data.put("lzz", lzzEntryStation);
        data.put("tth", interval);
        waterTransferReq.setStartTime(req.getWaterDistributionStartTime());
        waterTransferReq.setEndTime(req.getWaterDistributionEndTime());
        waterTransferReq.setName(req.getWaterDistributionType());
        waterTransferReq.setFloodWaterLevelLzz(req.getFloodWaterLevelLzz());
        waterTransferReq.setFloodWaterLevelTth(req.getFloodWaterLevelTth());
        waterTransferReq.setLevelBeginLzz(req.getLevelBeginLzz());
        waterTransferReq.setLevelBeginTth(req.getLevelBeginTth());
        waterTransferReq.setLevelEndLzz(req.getLevelEndLzz());
        waterTransferReq.setLevelEndTth(req.getLevelEndTth());
        waterTransferReq.setTimeCalStep(req.getBucketType());
        waterTransferReq.setData(data);
        waterTransferReq.setWaterDemandData(waterNeed(req.getWaterDistributionStartTime(), req.getWaterDistributionEndTime()));
        waterTransferReq.setCurve(curveService.selectList());
        List<ResOption> calculator;
        try {
            calculator = OutResult.calculator(waterTransferReq);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String displayDataPath = calculator.stream().filter(n -> n.getName().equals("表1")).findFirst().get().getPath();
        String displayDataPathMinio = DateUtil.format(now, "yyyyMMdd/HH/mm/ss/") + displayDataPath.substring(displayDataPath.lastIndexOf(File.separator) + 1);
        String customDataPath = calculator.stream().filter(n -> n.getName().equals("配水详情")).findFirst().get().getPath();
        String customDataPathMinio = DateUtil.format(now, "yyyyMMdd/HH/mm/ss/") + customDataPath.substring(customDataPath.lastIndexOf(File.separator) + 1);
        minioUtils.putObject("tth", displayDataPathMinio, displayDataPath);
        minioUtils.putObject("tth", customDataPathMinio, customDataPath);
        waterResourceAllocation.setAllocationDataDisplayAddress(displayDataPathMinio);
        waterResourceAllocation.setAllocationDataCustomAddress(customDataPathMinio);

        boolean save = this.save(waterResourceAllocation);
        if (save) {
            return RestResponse.ok(waterResourceAllocation);
        } else {
            return RestResponse.no("水资源调配生成失败");
        }
    }

    @Override
    public RestResponse<IPage<WaterResourceAllocation>> getAllocationPage(WaterResourceAllocationQueryReq req) {
        Page<WaterResourceAllocation> page = new Page<>(req.getPageNo(), req.getPageSize());
        LambdaQueryWrapper<WaterResourceAllocation> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WaterResourceAllocation::getDel, 0);
        if (req.getBucketType() != null) {
            wrapper.eq(WaterResourceAllocation::getBucketType, req.getBucketType());
        }
        if (StringUtils.hasText(req.getPlanName())) {
            wrapper.like(WaterResourceAllocation::getSchemeName, req.getPlanName());
        }
        if (req.getDateTime() != null) {
            wrapper.le(WaterResourceAllocation::getWaterDistributionStartTime, req.getDateTime())
                    .ge(WaterResourceAllocation::getWaterDistributionEndTime, req.getDateTime());
        }
        return RestResponse.ok(baseMapper.selectPage(page, wrapper));
    }

    @Override
    public RestResponse delById(String id) {
        WaterResourceAllocation waterResourceAllocation = baseMapper.selectById(id);
        minioUtils.deleteObjectInfo("tth", waterResourceAllocation.getAllocationDataDisplayAddress());
        minioUtils.deleteObjectInfo("tth", waterResourceAllocation.getAllocationDataCustomAddress());
        waterResourceAllocation.setDel(1);
        // todo 登陆用户
        waterResourceAllocation.setUpdateBy(null);
        waterResourceAllocation.setUpdateTime(new Date());
        baseMapper.updateById(waterResourceAllocation);
        return RestResponse.ok();
    }

    @SneakyThrows
    @Override
    public RestResponse<WaterAllocationComparisonSelectionRes> compare(String idA, String idB) {
        WaterAllocationComparisonSelectionRes waterAllocationComparisonSelectionRes = new WaterAllocationComparisonSelectionRes();
        WaterResourceAllocation waterResourceAllocationA = baseMapper.selectById(idA);
        WaterResourceAllocation waterResourceAllocationB = baseMapper.selectById(idB);
        AppraiseReq appraiseReqA = getCompareAppraise(waterResourceAllocationA);
        AppraiseReq appraiseReqB = getCompareAppraise(waterResourceAllocationB);
        waterAllocationComparisonSelectionRes.setAppraise(new WaterResourceAssessment().WaterResourceAssessment(appraiseReqA, appraiseReqB));
        waterAllocationComparisonSelectionRes.setWaterRatio(getWaterRatio(appraiseReqA.getExcel2Data(), appraiseReqB.getExcel2Data()));
        waterAllocationComparisonSelectionRes.setWaterStatistics(getWaterStatistics(waterResourceAllocationA, waterResourceAllocationB));
        return RestResponse.ok(waterAllocationComparisonSelectionRes);
    }

    private static Map<String, List<String>> areaMap = new HashMap() {{
        put("lzz", Arrays.asList("楼庄子生活"));
        put("hongYan", Arrays.asList("红岩生活"));
        put("baGang", Arrays.asList("八钢工业"));
        put("quShou", Arrays.asList("渠首灌溉", "渠首绿化", "渠首工业"));
        put("heDong", Arrays.asList("河东绿化", "东干渠灌溉"));
        put("heXi", Arrays.asList("河西绿化", "西干渠灌溉"));
    }};

    private static Map<String, Map<String, List<String>>> unitMap = new HashMap() {{
        put("irrigate", new HashMap() {{
            put("quShou", Arrays.asList("渠首灌溉"));
            put("heDong", Arrays.asList("东干渠灌溉"));
            put("heXi", Arrays.asList("西干渠灌溉"));
        }});
        put("industry", new HashMap() {{
            put("quShou", Arrays.asList("渠首工业"));
        }});
        put("green", new HashMap() {{
            put("quShou", Arrays.asList("渠首绿化"));
            put("heDong", Arrays.asList("河东绿化"));
            put("heXi", Arrays.asList("河西绿化"));
        }});
    }};

    @Override
    public RestResponse<List<ViewModelRes>> viewModel(ViewModelReq req) {
        List<ViewModelRes> viewModelResList = new ArrayList<>();
        List<Excel2> excelList = getListFromMinio(req.getAllocationDataCustomAddress(), Excel2.class);
        Map<String, Double> collect = excelList.stream().filter(n ->
                        n.getTime().getTime() <= req.getWaterDistributionEndTime().getTime()
                                && n.getTime().getTime() >= req.getWaterDistributionStartTime().getTime()
                                && !n.getStationType().equals("总东干渠")
                                && !n.getStationType().equals("总西干渠"))
                .collect(Collectors.groupingBy(n -> n.getStationType() + ":" + n.getStationName(),
                        Collectors.summingDouble(Excel2::getWater)));
        areaMap.forEach((k, v) -> {
            ViewModelRes viewModelRes = new ViewModelRes();
            viewModelRes.setArea(k);
            ViewModelRes.AreaDTO areaDTO = new ViewModelRes.AreaDTO();
            areaDTO.setWater(collect.entrySet().stream().filter(n -> v.contains(n.getKey().split(":")[0])).mapToDouble(n -> n.getValue()).sum());

            unitMap.forEach((unitK, unitV) -> {
                if (unitV.containsKey(k)) {
                    List<ViewModelRes.AreaDTO.UnitsDTO> unitsDTOList = new ArrayList<>();
                    collect.entrySet().stream().filter(n -> v.contains(n.getKey().split(":")[0]))
                            .filter(n -> unitV.get(k).contains(n.getKey().split(":")[0]))
                            .forEach(n -> {
                                ViewModelRes.AreaDTO.UnitsDTO unitsDTO = new ViewModelRes.AreaDTO.UnitsDTO();
                                unitsDTO.setWater(n.getValue());
                                unitsDTO.setUnit(n.getKey().split(":")[1]);
                                unitsDTOList.add(unitsDTO);
                            });
                    if (areaDTO.getData() != null) {
                        areaDTO.getData().put(unitK, unitsDTOList);
                    } else {
                        areaDTO.setData(new HashMap() {{
                            put(unitK, unitsDTOList);
                        }});
                    }
                }
            });
            viewModelRes.setInfo(areaDTO);
            viewModelResList.add(viewModelRes);
        });

        List<AllocationDisplayData> displayDataList = getListFromMinio(req.getAllocationDataDisplayAddress(), AllocationDisplayData.class);
        Map<String, Double> collect1 = displayDataList.stream().filter(n ->
                        n.getTime().getTime() <= req.getWaterDistributionEndTime().getTime()
                                && n.getTime().getTime() >= req.getWaterDistributionStartTime().getTime())
                .collect(Collectors.groupingBy(n -> n.getStationName(),
                        Collectors.summingDouble(AllocationDisplayData::getOutFlowWater)));
        ViewModelRes viewModelLzzOut = new ViewModelRes();
        ViewModelRes viewModelTth = new ViewModelRes();

        viewModelLzzOut.setArea("lzzOut");
        ViewModelRes.AreaDTO lzzOut = new ViewModelRes.AreaDTO();
        lzzOut.setWater(collect1.get("楼庄子"));
        viewModelLzzOut.setInfo(lzzOut);

        ViewModelRes.AreaDTO tth = new ViewModelRes.AreaDTO();
        tth.setWater(collect1.get("头屯河"));
        viewModelTth.setArea("tth");
        viewModelTth.setInfo(tth);

        viewModelResList.add(viewModelLzzOut);
        viewModelResList.add(viewModelTth);
        return RestResponse.ok(viewModelResList);
    }

    private List<WaterAllocationComparisonSelectionRes.WaterRatioDTO> getWaterRatio(List<Excel2> dataA, List<Excel2> dataB) {
        List<String> stationsA = dataA.stream().map(n -> n.getStationType() + ":" + n.getStationName()).distinct().collect(Collectors.toList());
        List<String> stationsB = dataB.stream().map(n -> n.getStationType() + ":" + n.getStationName()).distinct().collect(Collectors.toList());
        stationsA.addAll(stationsB);
        stationsA = stationsA.stream().distinct().collect(Collectors.toList());
        List<WaterAllocationComparisonSelectionRes.WaterRatioDTO> waterRatioDTOS = new ArrayList<>();
        for (String station : stationsA) {
            WaterAllocationComparisonSelectionRes.WaterRatioDTO waterRatioDTO = new WaterAllocationComparisonSelectionRes.WaterRatioDTO();
            waterRatioDTO.setArea(station.split(":")[0]);
            waterRatioDTO.setUnit(station.split(":")[1]);
            List<Double> proportions = new ArrayList<>();
            List<Double> waterLacks = new ArrayList<>();
            proportions.add(dataA.stream().filter(n -> (n.getStationType() + ":" + n.getStationName()).equals(station)).max(Comparator.comparing(Excel2::getProportion)).orElse(new Excel2()).getProportion());
            proportions.add(dataB.stream().filter(n -> (n.getStationType() + ":" + n.getStationName()).equals(station)).max(Comparator.comparing(Excel2::getProportion)).orElse(new Excel2()).getProportion());
            waterLacks.add(dataA.stream().filter(n -> (n.getStationType() + ":" + n.getStationName()).equals(station)).min(Comparator.comparing(Excel2::getWaterLack)).orElse(new Excel2()).getWaterLack());
            waterLacks.add(dataB.stream().filter(n -> (n.getStationType() + ":" + n.getStationName()).equals(station)).min(Comparator.comparing(Excel2::getWaterLack)).orElse(new Excel2()).getWaterLack());
            waterRatioDTO.setProportion(proportions);
            waterRatioDTO.setWaterLack(waterLacks);
            waterRatioDTOS.add(waterRatioDTO);
        }
        return waterRatioDTOS;
    }

    private WaterAllocationComparisonSelectionRes.WaterStatisticsDTO getWaterStatistics(WaterResourceAllocation waterResourceAllocationA, WaterResourceAllocation waterResourceAllocationB) {
        List<AllocationDisplayData> allocationDisplayDataA = getListFromMinio(waterResourceAllocationA.getAllocationDataDisplayAddress(), AllocationDisplayData.class);
        List<AllocationDisplayData> allocationDisplayDataB = getListFromMinio(waterResourceAllocationB.getAllocationDataDisplayAddress(), AllocationDisplayData.class);
        WaterAllocationComparisonSelectionRes.WaterStatisticsDTO waterStatisticsDTO = new WaterAllocationComparisonSelectionRes.WaterStatisticsDTO();
        List<Double> ecologyProportion = new ArrayList<>();
        ecologyProportion.add(allocationDisplayDataA.stream().mapToDouble(n -> n.getEcologyProportion() * n.getAllWater()).sum() /
                allocationDisplayDataA.stream().mapToDouble(AllocationDisplayData::getAllWater).sum());
        ecologyProportion.add(allocationDisplayDataB.stream().mapToDouble(n -> n.getEcologyProportion() * n.getAllWater()).sum() /
                allocationDisplayDataB.stream().mapToDouble(AllocationDisplayData::getAllWater).sum());

        List<Double> cityProportion = new ArrayList<>();
        cityProportion.add(allocationDisplayDataA.stream().mapToDouble(n -> n.getCityProportion() * n.getAllWater()).sum() /
                allocationDisplayDataA.stream().mapToDouble(AllocationDisplayData::getAllWater).sum());
        cityProportion.add(allocationDisplayDataB.stream().mapToDouble(n -> n.getCityProportion() * n.getAllWater()).sum() /
                allocationDisplayDataB.stream().mapToDouble(AllocationDisplayData::getAllWater).sum());

        List<Double> industryProportion = new ArrayList<>();
        industryProportion.add(allocationDisplayDataA.stream().mapToDouble(n -> n.getIndustryProportion() * n.getAllWater()).sum() /
                allocationDisplayDataA.stream().mapToDouble(AllocationDisplayData::getAllWater).sum());
        industryProportion.add(allocationDisplayDataB.stream().mapToDouble(n -> n.getIndustryProportion() * n.getAllWater()).sum() /
                allocationDisplayDataB.stream().mapToDouble(AllocationDisplayData::getAllWater).sum());

        List<Double> irrigateProportion = new ArrayList<>();
        irrigateProportion.add(allocationDisplayDataA.stream().mapToDouble(n -> n.getIrrigateProportion() * n.getAllWater()).sum() /
                allocationDisplayDataA.stream().mapToDouble(AllocationDisplayData::getAllWater).sum());
        irrigateProportion.add(allocationDisplayDataB.stream().mapToDouble(n -> n.getIrrigateProportion() * n.getAllWater()).sum() /
                allocationDisplayDataB.stream().mapToDouble(AllocationDisplayData::getAllWater).sum());

        List<Double> greeningProportion = new ArrayList<>();
        greeningProportion.add(allocationDisplayDataA.stream().mapToDouble(n -> n.getGreeningProportion() * n.getAllWater()).sum() /
                allocationDisplayDataA.stream().mapToDouble(AllocationDisplayData::getAllWater).sum());
        greeningProportion.add(allocationDisplayDataB.stream().mapToDouble(n -> n.getGreeningProportion() * n.getAllWater()).sum() /
                allocationDisplayDataB.stream().mapToDouble(AllocationDisplayData::getAllWater).sum());

        waterStatisticsDTO.setEcologyProportion(ecologyProportion);
        waterStatisticsDTO.setCityProportion(cityProportion);
        waterStatisticsDTO.setIndustryProportion(industryProportion);
        waterStatisticsDTO.setIrrigateProportion(irrigateProportion);
        waterStatisticsDTO.setGreeningProportion(greeningProportion);
        return waterStatisticsDTO;
    }

    private AppraiseReq getCompareAppraise(WaterResourceAllocation waterResourceAllocation) {
        AppraiseReq appraiseReq = new AppraiseReq();
        appraiseReq.setPeriod(waterResourceAllocation.getBucketType().toString());
        appraiseReq.setId(waterResourceAllocation.getWaterDistributionType());
        appraiseReq.setStartTime(waterResourceAllocation.getWaterDistributionStartTime());
        appraiseReq.setEndTime(waterResourceAllocation.getWaterDistributionEndTime());
        appraiseReq.setExcel2Data(getListFromMinio(waterResourceAllocation.getAllocationDataCustomAddress(), Excel2.class));
        return appraiseReq;
    }

    @SneakyThrows
    private List getListFromMinio(String minioPath, Class clazz) {
        String[] split = minioPath.split("/");
        String[] split1 = split[split.length - 1].split("\\.");
        InputStream is = minioUtils.getObject("tth", minioPath);
        MultipartFile multipartFile = MultipartFileUtil.inputStreamToMultipartFile(is, split1[0]);
        return ExcelUtils.importExcel(multipartFile, clazz);
    }

    //year  month  tenDays day
    // 1-年逐月 2-月逐旬 3-旬逐日
    private List<Waterdemand> waterNeed(Date startTime, Date endTime) {
        List<Waterdemand> demands = new ArrayList<>();
        demands.addAll(waterNeedYear(startTime));
        demands.addAll(waterNeedMonth(startTime));
        demands.addAll(waterNeedTenDays(startTime));
        demands.addAll(waterNeedDay(startTime, endTime));
        return demands;
    }

    private final Map<String, String> yearDict = new HashMap<String, String>() {{
        put("JANUARY", "一月");
        put("FEBRUARY", "二月");
        put("MARCH", "三月");
        put("APRIL", "四月");
        put("MAY", "五月");
        put("JUNE", "六月");
        put("JULY", "七月");
        put("AUGUST", "八月");
        put("SEPTEMBER", "九月");
        put("OCTOBER", "十月");
        put("NOVEMBER", "十一月");
        put("DECEMBER", "十二月");
    }};

    private final Map<String, String> monthDict = new HashMap<String, String>() {{
        put("EARLYOCTOBER", "上旬");
        put("MIDDAY", "中旬");
        put("LATEROCTOBER", "下旬");
        put("TOTAL", "合计");
    }};

    private List<Waterdemand> waterNeedYear(Date date) {
        List<Waterdemand> demands = new ArrayList<>();
        List<YearWaterUsePlanTrunkCanal> yearWaterUsePlanTrunkCanals = yearWaterUsePlanTrunkCanalService.lambdaQuery()
                .eq(YearWaterUsePlanTrunkCanal::getDel, 0)
                .eq(YearWaterUsePlanTrunkCanal::getYear, DateUtil.year(date))
                .list();
        for (int i = 0; i < yearWaterUsePlanTrunkCanals.size(); i++) {
            YearWaterUsePlanTrunkCanal yearWaterUse = yearWaterUsePlanTrunkCanals.get(i);
            demands.addAll(rowToCol(yearWaterUse, "year"));
        }
        return demands;
    }

    private List<Waterdemand> waterNeedMonth(Date date) {
        List<Waterdemand> demands = new ArrayList<>();
        List<MonthWaterUsePlan> monthWaterUsePlans = monthWaterUsePlanService.lambdaQuery()
                .eq(MonthWaterUsePlan::getDel, 0)
                .eq(MonthWaterUsePlan::getYear, DateUtil.year(date))
                .eq(MonthWaterUsePlan::getMonth, DateUtil.month(date) + 1)
                .list();
        for (int i = 0; i < monthWaterUsePlans.size(); i++) {
            MonthWaterUsePlan monthWaterUsePlan = monthWaterUsePlans.get(i);
            demands.addAll(rowToCol(monthWaterUsePlan, "month"));
        }
        return demands;
    }

    private List<Waterdemand> waterNeedTenDays(Date date) {
        List<Waterdemand> demands = new ArrayList<>();
        QueryWrapper<TenDayWaterUsePlan> lqw = new QueryWrapper<>();
        lqw.eq("del", 0).eq("year", DateUtil.year(date)).eq("month", DateUtil.month(date) + 1);
        lqw.select("sum(WATER_DEMAND_FOR_THIS_MONTH) as DEMAND, IRRIGATED_AREA, USE_WATER_USER, YEAR, MONTH, TEN_DAYS");
        lqw.groupBy("IRRIGATED_AREA, USE_WATER_USER, YEAR, MONTH, TEN_DAYS");
        List<Map<String, Object>> maps = tenDayWaterUsePlanService.getBaseMapper().selectMaps(lqw);
        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> tenDays = maps.get(i);
            Waterdemand waterdemand = new Waterdemand();
            waterdemand.setUseWaterPlan("tenDays");
            waterdemand.setWaterDemendData(((BigDecimal) tenDays.get("DEMAND")).doubleValue());
            waterdemand.setArea(tenDays.get("IRRIGATED_AREA").toString());
            waterdemand.setUnit(tenDays.get("USE_WATER_USER").toString());
            waterdemand.setColName(tenDays.get("TEN_DAYS").toString());
            demands.add(waterdemand);
        }
        return demands;
    }

    private List<Waterdemand> waterNeedDay(Date startTime, Date endTime) {
        List<Waterdemand> demands = new ArrayList<>();
        List<Map> waterNeedDetailList = dayWaterUsePlanService.getWaterNeedDetail(startTime, endTime);
        for (int i = 0; i < waterNeedDetailList.size(); i++) {
            Map<String, String> waterNeedDetail = waterNeedDetailList.get(i);
            for (Map water : JSONObject.parseArray(waterNeedDetail.get("V"), Map.class)) {
                Waterdemand waterdemand = new Waterdemand();
                waterdemand.setUseWaterPlan("day");
                double flow = 0d, plan = 0d;
                if (StringUtils.hasText(water.get("flow").toString())) {
                    flow = Double.parseDouble(water.get("flow").toString());
                }
                if (StringUtils.hasText(water.get("waterPlan").toString())) {
                    plan = Double.parseDouble(water.get("waterPlan").toString());
                }
                waterdemand.setWaterDemendData(flow + plan);
                waterdemand.setArea(waterNeedDetail.get("AREA"));
                waterdemand.setUnit(waterNeedDetail.get("UNIT"));
                waterdemand.setSubArea(water.get("unitName").toString());
                waterdemand.setColName("flow");
                demands.add(waterdemand);
            }
        }
        return demands;
    }

    @SneakyThrows
    private List<Waterdemand> rowToCol(Object o, String planType) {
        List<Waterdemand> demands = new ArrayList<>();
        Map<String, String> dict = yearDict;
        if (planType.equalsIgnoreCase("month")) {
            dict = monthDict;
        }
        Class<?> aClass = o.getClass();
        Field[] fields = aClass.getDeclaredFields();
        Field area = Arrays.stream(fields).filter(f -> f.getName().equalsIgnoreCase("area")).findAny().get();
        area.setAccessible(true);
        Field unit = Arrays.stream(fields).filter(f -> f.getName().equalsIgnoreCase("unit")).findAny().get();
        unit.setAccessible(true);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (dict.containsKey(field.getName().toUpperCase())) {
                field.setAccessible(true);
                Waterdemand waterdemand = new Waterdemand();
                waterdemand.setUseWaterPlan(planType);
                Object data = field.get(o);
                if (data != null && !data.toString().equalsIgnoreCase("null")) {
                    waterdemand.setWaterDemendData(Double.parseDouble(data.toString()));
                }
                waterdemand.setArea(area.get(o).toString());
                waterdemand.setUnit(unit.get(o).toString());
                waterdemand.setColName(dict.get(field.getName().toUpperCase()));
                demands.add(waterdemand);
            }
        }
        return demands;
    }
}

