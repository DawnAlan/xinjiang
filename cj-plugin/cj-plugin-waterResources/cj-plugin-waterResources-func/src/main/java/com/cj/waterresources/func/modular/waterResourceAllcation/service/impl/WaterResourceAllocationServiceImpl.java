package com.cj.waterresources.func.modular.waterResourceAllcation.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.exception.CommonException;
import com.cj.common.model.RestResponse;
import com.cj.common.pojo.CommonResult;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.api.PredictionApi;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
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
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.dto.WaterDistributionDto;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.ViewModelReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.WaterResourceAllocationAddReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.WaterResourceAllocationQueryReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.res.*;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    private final LzzGaugingStationService lzzGaugingStationService;
    private final IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

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

        doAllocation(waterResourceAllocation, now);

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
    public RestResponse delById(List<String> ids) {
        List<WaterResourceAllocation> waterResourceAllocations = baseMapper.selectBatchIds(ids);
        for (WaterResourceAllocation waterResourceAllocation : waterResourceAllocations) {
            minioUtils.deleteObjectInfo("tth", waterResourceAllocation.getAllocationDataDisplayAddress());
            minioUtils.deleteObjectInfo("tth", waterResourceAllocation.getAllocationDataCustomAddress());
            waterResourceAllocation.setDel(1);
            // todo 登陆用户
            waterResourceAllocation.setUpdateBy(null);
            waterResourceAllocation.setUpdateTime(new Date());
            baseMapper.updateById(waterResourceAllocation);
        }
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
        extractAreaWater(req, viewModelResList);
        extractReservoirWater(req, viewModelResList);
        return RestResponse.ok(viewModelResList);
    }

    private void extractAreaWater(ViewModelReq req, List<ViewModelRes> viewModelResList) {
        List<Excel2> excelList = getListFromMinio(req.getAllocationDataCustomAddress(), Excel2.class);
        Map<String, Double> collect = excelList.stream().filter(n ->
                        n.getTime().getTime() <= req.getWaterDistributionEndTime().getTime()
                                && n.getTime().getTime() >= req.getWaterDistributionStartTime().getTime())
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
        addEcologyWater(viewModelResList, collect);
    }

    private void addEcologyWater(List<ViewModelRes> viewModelResList, Map<String, Double> collect) {
        viewModelResList.add(getSpecifyKeyWater(collect, "lzzEcology", "生态用水:楼庄子生态用水"));
        viewModelResList.add(getSpecifyKeyWater(collect, "tthEcology", "生态用水:头屯河生态用水"));
    }

    private void extractReservoirWater(ViewModelReq req, List<ViewModelRes> viewModelResList) {
        List<AllocationDisplayData> displayDataList = getListFromMinio(req.getAllocationDataDisplayAddress(), AllocationDisplayData.class);
        Map<String, Double> collect = displayDataList.stream().filter(n ->
                        n.getTime().getTime() <= req.getWaterDistributionEndTime().getTime()
                                && n.getTime().getTime() >= req.getWaterDistributionStartTime().getTime())
                .collect(Collectors.groupingBy(n -> n.getStationName(),
                        Collectors.summingDouble(AllocationDisplayData::getOutFlowWater)));

        viewModelResList.add(getSpecifyKeyWater(collect, "lzzOut", "楼庄子"));
        viewModelResList.add(getSpecifyKeyWater(collect, "tth", "头屯河"));
    }

    private ViewModelRes getSpecifyKeyWater(Map<String, Double> collect, String area, String key) {
        ViewModelRes viewModelRes = new ViewModelRes();
        viewModelRes.setArea(area);
        ViewModelRes.AreaDTO lzzOut = new ViewModelRes.AreaDTO();
        lzzOut.setWater(collect.get(key));
        viewModelRes.setInfo(lzzOut);
        return viewModelRes;
    }

    @Override
    public RestResponse updateAllocation(WaterResourceAllocation waterResourceAllocation) {
        Date now = new Date();
        waterResourceAllocation.setUpdateBy("");
        waterResourceAllocation.setUpdateTime(now);
        String customAddress = waterResourceAllocation.getAllocationDataCustomAddress();
        String displayAddress = waterResourceAllocation.getAllocationDataDisplayAddress();

        doAllocation(waterResourceAllocation, now);
        updateById(waterResourceAllocation);
        minioUtils.deleteObjectInfo("tth", customAddress);
        minioUtils.deleteObjectInfo("tth", displayAddress);
        return RestResponse.ok(waterResourceAllocation);
    }

    @Override
    @SneakyThrows
    public RestResponse getWaterResourceAllocationDetails(String id) {
        Map<String,Object> result =new HashMap<>();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        WaterResourceAllocation waterResourceAllocation = this.getById(id);
        String customAddress = waterResourceAllocation.getAllocationDataCustomAddress();
        String displayAddress = waterResourceAllocation.getAllocationDataDisplayAddress();
        //业务
        InputStream customAddressInputStream = minioUtils.getObject("tth", customAddress);
        String[] customAddressSplit = customAddress.split("/");
        String[] customAddressSplit1 = customAddressSplit[customAddressSplit.length - 1].split("\\.");
        MultipartFile customAddressFile = MultipartFileUtil.inputStreamToMultipartFile(customAddressInputStream, customAddressSplit1[0]);
        List<WaterDistributionDto> waterDistributionList = ExcelUtils.importExcel(customAddressFile, WaterDistributionDto.class);
        //配水详情
        Map<String,Object> waterDistributionDetails = new HashMap<>();
        Map<String, List<WaterDistributionDto>> collect1 = waterDistributionList.stream().collect(Collectors.groupingBy(WaterDistributionDto::getStationType));
        Set<String> strings1 = collect1.keySet();
        for(String s:strings1){
            List<WaterDistributionDto> waterDistributionDtos = collect1.get(s);
            Map<String, List<WaterDistributionDto>> collect = waterDistributionDtos.stream().collect(Collectors.groupingBy(WaterDistributionDto::getStationName));
            Set<String> strings = collect.keySet();
            Map<String,Object> waterDistributionDetailsTemp = new HashMap<>();

            for(String s1:strings){
                List<WaterDistributionDto> waterDistributionDtos1 = collect.get(s1);
                List<WaterDistributionDetailsRes> waterDistributionDetailsResList = new ArrayList<>();
                for(WaterDistributionDto dto:waterDistributionDtos1){
                    WaterDistributionDetailsRes res = new WaterDistributionDetailsRes();
                    res.setName(dto.getStationName());
                    res.setTime(sdf.format(dto.getTime()));
                    res.setValue(dto.getWater());
                    waterDistributionDetailsResList.add(res);
                }
                waterDistributionDetailsTemp.put(s1,waterDistributionDetailsResList);
            }
            waterDistributionDetails.put(s,waterDistributionDetailsTemp);
        }
        //四预
        InputStream displayAddressInputStream = minioUtils.getObject("tth", displayAddress);
        String[] displayAddressSplit = displayAddress.split("/");
        String[] displayAddressSplit1 = displayAddressSplit[displayAddressSplit.length - 1].split("\\.");
        MultipartFile displayAddressFile = MultipartFileUtil.inputStreamToMultipartFile(displayAddressInputStream, displayAddressSplit1[0]);
        List<AllocationDisplayData> displayDataList = ExcelUtils.importExcel(displayAddressFile, AllocationDisplayData.class);
        //水库水情
        Map<String,Object> regimen = new HashMap<>();
        //供水平衡
        Map<String,Object> waterSupplyBalance = new HashMap<>();
        //用水分布
        List<WaterDistributionRes> waterDistribution= new ArrayList<>();
        List<AllocationDisplayData> collect2 = displayDataList.stream().filter(t -> t.getStationName().equals("头屯河")).collect(Collectors.toList());
        for (AllocationDisplayData data:collect2){
            WaterDistributionRes res = new WaterDistributionRes();
            res.setTime(sdf.format(data.getTime()));
            res.setCityProportion(data.getCityProportion());
            res.setEcologyProportion(data.getEcologyProportion());
            res.setIrrigateProportion(data.getIrrigateProportion());
            res.setIndustryProportion(data.getIndustryProportion());
            res.setGreeningProportion(data.getGreeningProportion());
            waterDistribution.add(res);
        }
        Map<String, List<AllocationDisplayData>> collect = displayDataList.stream().collect(Collectors.groupingBy(AllocationDisplayData::getStationName));
        Set<String> strings = collect.keySet();
        for(String s:strings){
            List<AllocationDisplayData> displayDataList1 = collect.get(s);
            List<RegimenViewRes> regimenViewResList = new ArrayList<>();
            List<WaterSupplyBalanceRes> waterSupplyBalanceResList = new ArrayList<>();
            for(AllocationDisplayData data:displayDataList1){
                RegimenViewRes regimenViewRes = new RegimenViewRes();
                WaterSupplyBalanceRes waterSupplyBalanceRes = new WaterSupplyBalanceRes();
                regimenViewRes.setStationName(data.getStationName());
                regimenViewRes.setTime(data.getTime());
                regimenViewRes.setCapacity(data.getCapacity());
                regimenViewRes.setLevel(data.getLevelEnd());
                regimenViewRes.setInflow(data.getInflow());
                regimenViewRes.setOutflow(data.getOutflow());
                regimenViewResList.add(regimenViewRes);
                waterSupplyBalanceRes.setWaterDemand(data.getWaterDemand());
                waterSupplyBalanceRes.setWaterSupply(data.getWaterSupply());
                waterSupplyBalanceResList.add(waterSupplyBalanceRes);
            }
            regimen.put(s,regimenViewResList);
            waterSupplyBalance.put(s,waterSupplyBalanceResList);
        }
        result.put("水库水情",regimen);
        result.put("供水平衡",waterSupplyBalance);
        result.put("用水分布",waterDistribution);
        result.put("配水详情",waterDistributionDetails);
        return RestResponse.ok(result);
    }

    @Override
    @SneakyThrows
    public RestResponse contrast(String idA, String idB) {
        Map<String,Object> result = new HashMap<>();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        WaterResourceAllocation schemeA = this.getById(idA);
        String schemeAAddress = schemeA.getAllocationDataDisplayAddress();
        InputStream schemeAAddressInputStream = minioUtils.getObject("tth", schemeAAddress);
        String[] schemeAAddressSplit = schemeAAddress.split("/");
        String[] schemeAAddressSplit1 = schemeAAddressSplit[schemeAAddressSplit.length - 1].split("\\.");
        MultipartFile schemeAAddressFile = MultipartFileUtil.inputStreamToMultipartFile(schemeAAddressInputStream, schemeAAddressSplit1[0]);
        List<AllocationDisplayData> schemeADataList = ExcelUtils.importExcel(schemeAAddressFile, AllocationDisplayData.class);
        List<AllocationDisplayData> schemeALzz = schemeADataList.stream().filter(t -> t.getStationName().equals("楼庄子")).collect(Collectors.toList());
        List<AllocationDisplayData> schemeATth = schemeADataList.stream().filter(t -> t.getStationName().equals("头屯河")).collect(Collectors.toList());
        WaterResourceAllocation schemeB = this.getById(idB);
        String schemeBAddress = schemeB.getAllocationDataDisplayAddress();
        InputStream schemeBAddressInputStream = minioUtils.getObject("tth", schemeBAddress);
        String[] schemeBAddressSplit = schemeBAddress.split("/");
        String[] schemeBAddressSplit1 = schemeBAddressSplit[schemeBAddressSplit.length - 1].split("\\.");
        MultipartFile schemeBAddressFile = MultipartFileUtil.inputStreamToMultipartFile(schemeBAddressInputStream, schemeBAddressSplit1[0]);
        List<AllocationDisplayData> schemeBDataList = ExcelUtils.importExcel(schemeBAddressFile, AllocationDisplayData.class);
        List<AllocationDisplayData> schemeBLzz = schemeBDataList.stream().filter(t -> t.getStationName().equals("楼庄子")).collect(Collectors.toList());
        List<AllocationDisplayData> schemeBTth = schemeBDataList.stream().filter(t -> t.getStationName().equals("头屯河")).collect(Collectors.toList());
        //水库供蓄对比
        Map<String,Object> contrast = new HashMap<>();
        //楼庄子
        Map<String,Object> lzzResult = new HashMap<>();
        //头屯河
        Map<String,Object> tthResult = new HashMap<>();
        //lzz供水量
        List<ContrastRes> lzzWaterSupply = new ArrayList<>();
        List<ContrastRes> lzzWaterBalance = new ArrayList<>();
        schemeALzz.forEach(t->{
            ContrastRes contrastRes = new ContrastRes();
            ContrastRes waterBalanceRes = new ContrastRes();
            contrastRes.setA(t.getWaterSupply());
            contrastRes.setDate(sdf.format(t.getTime()));
            waterBalanceRes.setA(t.getWaterBalance());
            waterBalanceRes.setDate(sdf.format(t.getTime()));
            for(AllocationDisplayData data:schemeBLzz){
                if(data.getTime().compareTo(t.getTime())==0){
                    contrastRes.setB(data.getWaterSupply());
                    waterBalanceRes.setB(data.getWaterBalance());
                }
            }
            lzzWaterSupply.add(contrastRes);
            lzzWaterBalance.add(waterBalanceRes);
        });
        //lzz蓄水量
        List<ContrastRes> lzzDeltawater = new ArrayList<>();
        schemeALzz.forEach(t->{
            ContrastRes contrastRes = new ContrastRes();
            contrastRes.setA(t.getDeltaWater());
            contrastRes.setDate(sdf.format(t.getTime()));
            for(AllocationDisplayData data:schemeBLzz){
                if(data.getTime().compareTo(t.getTime())==0){
                    contrastRes.setB(data.getDeltaWater());
                }
            }
            lzzDeltawater.add(contrastRes);
        });
        lzzResult.put("供水量",lzzWaterSupply);
        lzzResult.put("蓄水量",lzzDeltawater);

        //tth供水量
        List<ContrastRes> tthWaterSupply = new ArrayList<>();
        List<ContrastRes> tthWaterBalance = new ArrayList<>();
        schemeATth.forEach(t->{
            ContrastRes contrastRes = new ContrastRes();
            ContrastRes waterBalanceRes = new ContrastRes();
            contrastRes.setA(t.getWaterSupply());
            contrastRes.setDate(sdf.format(t.getTime()));
            waterBalanceRes.setA(t.getWaterBalance());
            waterBalanceRes.setDate(sdf.format(t.getTime()));
            for(AllocationDisplayData data:schemeBTth){
                if(data.getTime().compareTo(t.getTime())==0){
                    contrastRes.setB(data.getWaterSupply());
                    waterBalanceRes.setB(data.getWaterBalance());
                }
            }
            tthWaterSupply.add(contrastRes);
            tthWaterBalance.add(waterBalanceRes);
        });
        //tth蓄水量
        List<ContrastRes> tthDeltawater = new ArrayList<>();
        schemeATth.forEach(t->{
            ContrastRes contrastRes = new ContrastRes();
            contrastRes.setA(t.getDeltaWater());
            contrastRes.setDate(sdf.format(t.getTime()));
            for(AllocationDisplayData data:schemeBTth){
                if(data.getTime().compareTo(t.getTime())==0){
                    contrastRes.setB(data.getDeltaWater());
                }
            }
            tthDeltawater.add(contrastRes);
        });
        tthResult.put("供水量",tthWaterSupply);
        tthResult.put("蓄水量",tthDeltawater);

        contrast.put("头屯河",tthResult);
        contrast.put("楼庄子",lzzResult);
        //水量平衡
        Map<String,Object> waterBalance = new HashMap<>();
        waterBalance.put("头屯河",tthWaterBalance);
        waterBalance.put("楼庄子",lzzWaterBalance);
        result.put("水库供蓄对比",contrast);
        result.put("水量平衡",waterBalance);
        RestResponse<WaterAllocationComparisonSelectionRes> compare = compare(idA, idB);
        if(compare.getCode()==200){
            String appraise = compare.getData().getAppraise();
            result.put("方案优选",appraise);
        }else {
            result.put("方案优选",null);
        }
        return RestResponse.ok(result);
    }

    @Override
    @SneakyThrows
    public RestResponse waterQuantityCalculation(String id) {
        Map<String,Object> result = new HashMap<>();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        WaterResourceAllocation resourceAllocation = this.getById(id);
        String dataDisplayAddress = resourceAllocation.getAllocationDataDisplayAddress();
        InputStream displayAddressInputStream = minioUtils.getObject("tth", dataDisplayAddress);
        String[] displayAddressSplit = dataDisplayAddress.split("/");
        String[] displayAddressSplit1 = displayAddressSplit[displayAddressSplit.length - 1].split("\\.");
        MultipartFile displayAddressFile = MultipartFileUtil.inputStreamToMultipartFile(displayAddressInputStream, displayAddressSplit1[0]);
        List<AllocationDisplayData> displayDataList = ExcelUtils.importExcel(displayAddressFile, AllocationDisplayData.class);
        if(null != displayDataList && displayDataList.size()>0){
            Map<String, List<AllocationDisplayData>> collect = displayDataList.stream().collect(Collectors.groupingBy(AllocationDisplayData::getStationName));
            Set<String> strings = collect.keySet();
            for(String s:strings){
                List<WaterQuantityCalculationRes> resList = new ArrayList<>();
                List<AllocationDisplayData> displayDataList1 = collect.get(s);
                for(AllocationDisplayData data:displayDataList1){
                    WaterQuantityCalculationRes res = new WaterQuantityCalculationRes();
                    BeanUtils.copyProperties(data,res);
                    res.setTime(sdf.format(data.getTime()));
                    resList.add(res);
                }
                result.put(s,resList);
            }
        }
        return RestResponse.ok(result);
    }

    @Override
    public RestResponse getRealTimeReservoirLevel(String reservoir) {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 创建 Calendar 对象
        Calendar calendar = Calendar.getInstance();
        // 将日期设置为当前时间
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 在当前时间上减去 24 小时（1天）
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        Date startTime = calendar.getTime();
        Date endTime = new Date();
        if(reservoir.equals("楼庄子")){
            List<LzzGaugingStation> list = lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getStationName,"楼庄子库水位站").
                    between(LzzGaugingStation::getGatherTime, startTime, endTime).list();
            if(null != list && list.size()>0){
                List<RealTimeReservoirLevelRes> resList = new ArrayList<>();
                for(LzzGaugingStation station:list){
                    RealTimeReservoirLevelRes res = new RealTimeReservoirLevelRes();
                    res.setDate(sdf.format(station.getGatherTime()));
                    res.setWaterAmount(station.getStorageCapacity()-591);
                    res.setCapacity(station.getStorageCapacity());
                    resList.add(res);
                }
                return RestResponse.ok(resList);
            }else {
                return RestResponse.no("no data");
            }
        }
        if(reservoir.equals("头屯河")){
            List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.lambdaQuery().eq(IrrigatedPlatformDataInfo::getMonitorName, "头屯河水库水位").
                    between(IrrigatedPlatformDataInfo::getMonitorTime, startTime, endTime).list();
            if(null != list && list.size()>0){
                List<RealTimeReservoirLevelRes> resList = new ArrayList<>();
                for(IrrigatedPlatformDataInfo dataInfo:list){
                    RealTimeReservoirLevelRes res = new RealTimeReservoirLevelRes();
                    res.setDate(dataInfo.getMonitorTime());
                    res.setWaterAmount(dataInfo.getSqCapacity()-211.79);
                    res.setCapacity(dataInfo.getSqCapacity());
                    resList.add(res);
                }
                return RestResponse.ok(resList);
            }else {
                return RestResponse.no("no data");
            }
        }
        return null;
    }

    private WaterResourceAllocation doAllocation(WaterResourceAllocation allocation, Date dateTime) {
        WaterTransferReq waterTransferReq = new WaterTransferReq();
        List<Flood> floods = getListFromMinio(allocation.getInflowDataAddress(), Flood.class);
        floods = floods.stream().filter(f -> f.getTime().getTime() <= allocation.getWaterDistributionEndTime().getTime()
                && f.getTime().getTime() >= allocation.getWaterDistributionStartTime().getTime()).collect(Collectors.toList());
        List<DataInflowPrevent> dataInflowPrevents = JSONObject.parseArray(JSONObject.toJSONString(floods), DataInflowPrevent.class);
        List<DataInflowPrevent> lzzEntryStation = dataInflowPrevents.stream().filter(t -> t.getLocation().equals("楼庄子")).collect(Collectors.toList());
        List<DataInflowPrevent> interval = dataInflowPrevents.stream().filter(t -> t.getLocation().equals("楼头区间")).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(lzzEntryStation) || CollectionUtil.isEmpty(interval)) {
            throw new CommonException(String.format("%s~%s来水预报数据异常",
                    DateUtil.format(allocation.getWaterDistributionStartTime(), DATE_FORMAT),
                    DateUtil.format(allocation.getWaterDistributionEndTime(), DATE_FORMAT)));
        }
        Map<String, List<DataInflowPrevent>> data = new HashMap<>();
        data.put("lzz", lzzEntryStation);
        data.put("tth", interval);
        waterTransferReq.setStartTime(allocation.getWaterDistributionStartTime());
        waterTransferReq.setEndTime(allocation.getWaterDistributionEndTime());
        waterTransferReq.setName(allocation.getWaterDistributionType());
        waterTransferReq.setFloodWaterLevelLzz(allocation.getFloodWaterLevelLzz());
        waterTransferReq.setFloodWaterLevelTth(allocation.getFloodWaterLevelTth());
        waterTransferReq.setLevelBeginLzz(allocation.getLevelBeginLzz());
        waterTransferReq.setLevelBeginTth(allocation.getLevelBeginTth());
        waterTransferReq.setLevelEndLzz(allocation.getLevelEndLzz());
        waterTransferReq.setLevelEndTth(allocation.getLevelEndTth());
        waterTransferReq.setTimeCalStep(allocation.getBucketType());
        waterTransferReq.setData(data);
        waterTransferReq.setWaterDemandData(waterNeed(allocation.getWaterDistributionStartTime(), allocation.getWaterDistributionEndTime()));
        waterTransferReq.setCurve(curveService.selectList());
        List<ResOption> calculator;
        try {
            calculator = OutResult.calculator(waterTransferReq);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
        String displayDataPath = calculator.stream().filter(n -> n.getName().equals("表1")).findFirst().get().getPath();
        String displayDataPathMinio = DateUtil.format(dateTime, "yyyyMMdd/HH/mm/ss/") + displayDataPath.substring(displayDataPath.lastIndexOf(File.separator) + 1);
        String customDataPath = calculator.stream().filter(n -> n.getName().equals("配水详情")).findFirst().get().getPath();
        String customDataPathMinio = DateUtil.format(dateTime, "yyyyMMdd/HH/mm/ss/") + customDataPath.substring(customDataPath.lastIndexOf(File.separator) + 1);
        minioUtils.putObject("tth", displayDataPathMinio, displayDataPath);
        minioUtils.putObject("tth", customDataPathMinio, customDataPath);
        allocation.setAllocationDataDisplayAddress(displayDataPathMinio);
        allocation.setAllocationDataCustomAddress(customDataPathMinio);
        return allocation;
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
        appraiseReq.setName(waterResourceAllocation.getSchemeName());
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
            if (tenDays == null || tenDays.get("DEMAND") == null) {
                throw new CommonException("旬需水计划数据异常");
            }
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

