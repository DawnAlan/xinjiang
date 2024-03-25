package com.cj.waterresources.func.modular.provider;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.waterresources.api.WaterResourceApi;
import com.cj.waterresources.func.modular.homePage.bean.res.WaterStorageOverviewRes;
import com.cj.waterresources.func.modular.homePage.inspection.InspectionInterface;
import com.cj.waterresources.func.modular.homePage.inspection.response.AbnormalRes;
import com.cj.waterresources.func.modular.homePage.inspection.response.InspectionRes;
import com.cj.waterresources.func.modular.homePage.service.WaterResourceHomePageService;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.entity.OverallSituationUnitMgr;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.service.OverallSituationUnitMgrService;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.entity.IrrigationQuota;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.service.IrrigationQuotaService;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.entity.DayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.service.DayWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.index.res.WaterUsePlanForViewRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanCropService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.TenDayWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementQueryReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.res.UseWaterManagementQueryRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.service.UseWaterManagementService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.WaterFeeStatisticsRes;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsTotalService;
import com.cj.waterresources.func.modular.waterResourceAllcation.entity.WaterResourceAllocation;
import com.cj.waterresources.func.modular.waterResourceAllcation.service.WaterResourceAllocationService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.entity.WaterStorageSchedulingLzz;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.service.WaterStorageSchedulingLzzService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.entity.WaterStorageSchedulingTotalForm;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.service.WaterStorageSchedulingTotalFormService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.entity.WaterStorageSchedulingTth;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.service.WaterStorageSchedulingTthService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WaterResourceApiProvider implements WaterResourceApi {

    private final YearWaterUsePlanCropService yearWaterUsePlanCropService;
    private final YearWaterUsePlanTrunkCanalService yearWaterUsePlanTrunkCanalService;
    private final UseWaterManagementService useWaterManagementService;
    private final MonthWaterUsePlanService monthWaterUsePlanService;
    private final MonthWaterUsePlanCropService monthWaterUsePlanCropService;
    private final TenDayWaterUsePlanService tenDayWaterUsePlanService;
    private final DayWaterUsePlanService dayWaterUsePlanService;
    private final WaterStorageSchedulingLzzService waterStorageSchedulingLzzService;
    private final WaterStorageSchedulingTthService waterStorageSchedulingTthService;
    private final WaterResourceAllocationService waterResourceAllocationService;
    private final TrendsTableParamService trendsTableParamService;
    private final DayWaterSituationStatisticsTableLzzService dayWaterSituationStatisticsTableLzzService;
    private final DayWaterSituationStatisticsTableTthService dayWaterSituationStatisticsTableTthService;
    private final IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;
    private final WaterResourceHomePageService waterResourceHomePageService;
    private final IrrigationQuotaService irrigationQuotaService;
    private final WaterFeeStatisticsTotalService waterFeeStatisticsTotalService;
    private final InspectionInterface inspectionInterface;
    private final WaterStorageSchedulingTotalFormService waterStorageSchedulingTotalFormService;
    private final OverallSituationUnitMgrService overallSituationUnitMgrService;
    private final RedisUtil redisUtil;

    @Override
    public String getYearWaterPlan(String area) {
        Integer year = LocalDateTime.now().getYear();
        List<WaterUsePlanForViewRes> totalAmount = new ArrayList<>();
        List<YearWaterUsePlanTrunkCanal> list = yearWaterUsePlanTrunkCanalService.lambdaQuery().
                eq(YearWaterUsePlanTrunkCanal::getYear, year).
                eq(YearWaterUsePlanTrunkCanal::getArea, area).
                eq(YearWaterUsePlanTrunkCanal::getDel,0).
                list();
        for(YearWaterUsePlanTrunkCanal trunkCanal:list){
            WaterUsePlanForViewRes res = new WaterUsePlanForViewRes();
            res.setUnit(trunkCanal.getUnit());
            res.setTotalAmount(
                    (trunkCanal.getApril()==null?0.0:trunkCanal.getApril())+
                    (trunkCanal.getMay()==null?0.0:trunkCanal.getMay())+
                    (trunkCanal.getJune()==null?0.0:trunkCanal.getJune())+
                    (trunkCanal.getJuly()==null?0.0:trunkCanal.getJuly())+
                    (trunkCanal.getAugust()==null?0.0:trunkCanal.getAugust())+
                    (trunkCanal.getSeptember()==null?0.0:trunkCanal.getSeptember())+
                    (trunkCanal.getOctober()==null?0.0:trunkCanal.getOctober())+
                    (trunkCanal.getNovember()==null?0.0:trunkCanal.getNovember())
            );
            totalAmount.add(res);
        }
        return JSONObject.toJSONString(totalAmount);
    }

    @Override
    public String getYearWaterPlanCrop(String area,String unit) {
        Map<String, Object> result = new HashMap<>();
        YearCropSelectListReq req = new YearCropSelectListReq();
        req.setYear(LocalDateTime.now().getYear());
        req.setUnit(unit);
        req.setArea(area);
        RestResponse<List<YearWaterUsePlanCrop>> listRestResponse = yearWaterUsePlanCropService.selectList(req);
        if(listRestResponse.getCode()==200){
            List<WaterUsePlanForViewRes> resList = new ArrayList<>();
            List<YearWaterUsePlanCrop> data = listRestResponse.getData();
            for(YearWaterUsePlanCrop crop:data){
                WaterUsePlanForViewRes res = new WaterUsePlanForViewRes();
                res.setUnit(crop.getIrrigatedCrop());
                res.setTotalAmount(
                        (crop.getAprilTotalWaterDemand()==null?0.0:crop.getAprilTotalWaterDemand())+
                        (crop.getMayTotalWaterDemand()==null?0.0:crop.getMayTotalWaterDemand())+
                        (crop.getJuneTotalWaterDemand()==null?0.0:crop.getJuneTotalWaterDemand())+
                        (crop.getJulyTotalWaterDemand()==null?0.0:crop.getJulyTotalWaterDemand())+
                        (crop.getAugustTotalWaterDemand()==null?0.0:crop.getAugustTotalWaterDemand())+
                        (crop.getSeptemberTotalWaterDemand()==null?0.0:crop.getSeptemberTotalWaterDemand())+
                        (crop.getOctoberTotalWaterDemand()==null?0.0:crop.getOctoberTotalWaterDemand())+
                        (crop.getNovemberTotalWaterDemand()==null?0.0:crop.getNovemberTotalWaterDemand())
                );
                resList.add(res);
            }
            Double aDouble = resList.stream().filter(t->t.getTotalAmount()!=null).map(WaterUsePlanForViewRes::getTotalAmount).reduce(Double::sum).orElse(0.00);
            result.put("cropList", resList);
            result.put("amount", aDouble);
            return JSONObject.toJSONString(result);
        }
        return null;
    }

    @Override
    public String getMonthWaterPlan(String area) {
        Integer year = LocalDateTime.now().getYear();
        Integer month = LocalDateTime.now().getMonth().getValue();
        List<MonthWaterUsePlan> list = monthWaterUsePlanService.lambdaQuery().
                eq(MonthWaterUsePlan::getArea, area).
                eq(MonthWaterUsePlan::getYear, year).
                eq(MonthWaterUsePlan::getMonth, month).
                eq(MonthWaterUsePlan::getDel,0).
                list();
        if(null != list && list.size()>0){
            List<WaterUsePlanForViewRes> resList = new ArrayList<>();
            for(MonthWaterUsePlan plan :list){
                WaterUsePlanForViewRes res = new WaterUsePlanForViewRes();
                res.setUnit(plan.getUnit());
                res.setTotalAmount(plan.getTotal()==null?0.0:plan.getTotal());
                resList.add(res);
            }
            return JSONObject.toJSONString(resList);
        }
        return null;
    }

    @Override
    public String getMonthWaterPlanCrop(String area,String unit) {
        Integer year = LocalDateTime.now().getYear();
        Integer month = LocalDateTime.now().getMonth().getValue();
        Map<String, Object> result = new HashMap<>();
        List<MonthWaterUsePlanCrop> list = monthWaterUsePlanCropService.lambdaQuery().
                eq(MonthWaterUsePlanCrop::getArea, area).
                eq(MonthWaterUsePlanCrop::getYear, year).
                eq(MonthWaterUsePlanCrop::getMonth, month).
                eq(MonthWaterUsePlanCrop::getUnit, unit).
                eq(MonthWaterUsePlanCrop::getDel,0).
                list();
        if(null!= list && list.size()>0){
            List<WaterUsePlanForViewRes> resList = new ArrayList<>();
            for(MonthWaterUsePlanCrop crop:list){
                WaterUsePlanForViewRes res = new WaterUsePlanForViewRes();
                res.setUnit(crop.getUnit());
                res.setTotalAmount(crop.getTotalCountWaterDemand()==null?0.0: crop.getTotalCountWaterDemand());
                resList.add(res);
            }
            Double aDouble = resList.stream().filter(t->t.getTotalAmount()!=null).map(WaterUsePlanForViewRes::getTotalAmount).reduce(Double::sum).orElse(0.00);
            result.put("cropList", resList);
            result.put("amount", aDouble);
            return JSONObject.toJSONString(result);
        }
        return null;
    }

    @Override
    public String getTenDaysWaterPlan(String area) {
        Integer year = LocalDateTime.now().getYear();
        Integer month = LocalDateTime.now().getMonth().getValue();
        Integer day = LocalDateTime.now().getDayOfMonth();
        String s = determineTenDays(day);
        List<TenDayWaterUsePlan> list = tenDayWaterUsePlanService.lambdaQuery().
                eq(TenDayWaterUsePlan::getYear, year).
                eq(TenDayWaterUsePlan::getMonth, month).
                eq(TenDayWaterUsePlan::getTenDays, s).
                eq(TenDayWaterUsePlan::getIrrigatedArea, area).
                list();
        if(null!= list && list.size()>0){
            List<WaterUsePlanForViewRes> resList = new ArrayList<>();
            Map<String, List<TenDayWaterUsePlan>> collect = list.stream().collect(Collectors.groupingBy(TenDayWaterUsePlan::getUseWaterUser));
            Set<String> strings = collect.keySet();
            for(String s1:strings){
                WaterUsePlanForViewRes res = new WaterUsePlanForViewRes();
                res.setUnit(s1);
                Double aDouble = collect.get(s1).stream().filter(t->t.getWaterDemand()!=null).map(TenDayWaterUsePlan::getWaterDemand).reduce(Double::sum).orElse(0.00);
                res.setTotalAmount(aDouble==null?0.0:aDouble);
                resList.add(res);
            }
            return JSONObject.toJSONString(resList);
        }
        return null;
    }

    @Override
    public String getTenDaysWaterPlanCrop(String area,String unit) {
        Integer year = LocalDateTime.now().getYear();
        Integer month = LocalDateTime.now().getMonth().getValue();
        Integer day = LocalDateTime.now().getDayOfMonth();
        String s = determineTenDays(day);
        Map<String, Object> result = new HashMap<>();
        List<TenDayWaterUsePlan> list = tenDayWaterUsePlanService.lambdaQuery().
                eq(TenDayWaterUsePlan::getYear, year).
                eq(TenDayWaterUsePlan::getMonth, month).
                eq(TenDayWaterUsePlan::getTenDays, s).
                eq(TenDayWaterUsePlan::getUseWaterUser, unit).
                eq(TenDayWaterUsePlan::getIrrigatedArea, area).
                list();
        if(null!= list && list.size()>0){
            List<WaterUsePlanForViewRes> resList = new ArrayList<>();
            for(TenDayWaterUsePlan plan:list){
                WaterUsePlanForViewRes res = new WaterUsePlanForViewRes();
                res.setUnit(plan.getUseWaterUser());
                res.setTotalAmount(plan.getWaterDemand()==null?0.0:plan.getWaterDemand());
                resList.add(res);
            }
            Double aDouble = resList.stream().filter(t->t.getTotalAmount()!=null).map(WaterUsePlanForViewRes::getTotalAmount).reduce(Double::sum).orElse(0.00);
            result.put("cropList", resList);
            result.put("amount", aDouble);
            return JSONObject.toJSONString(result);
        }
        return null;
    }

    @Override
    public String getDayWaterPlan(String area) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterdayDate = dateFormat.format(calendar.getTime());
        List<DayWaterUsePlan> list = dayWaterUsePlanService.lambdaQuery().
                eq(DayWaterUsePlan::getArea, area).
                eq(DayWaterUsePlan::getRecordTime, yesterdayDate).
                eq(DayWaterUsePlan::getDel,0).
                list();
        if(null != list && list.size()>0){
            return JSONObject.toJSONString(list);
        }
        return null;
    }

    @Override
    public String getUseWaterUser(String useWaterPlan, String area) {
        UseWaterManagementQueryReq req = new UseWaterManagementQueryReq();
        req.setUseWaterPlan(useWaterPlan);
        req.setArea(area);
        RestResponse<List<UseWaterManagementQueryRes>> select = useWaterManagementService.select(req);
        if(select.getCode()==200){
            return JSONObject.toJSONString(select.getData());
        }
        return null;
    }

    @Override
    public String getNeedWaterValueList(String area, Integer timeType) {
        Integer year = LocalDateTime.now().getYear();
        Integer month = LocalDateTime.now().getMonth().getValue();
        Map<String,Object> result = new HashMap<>();
        if(area.equals("楼庄子水库")){
            if(timeType==1){
                List<WaterStorageSchedulingLzz> list = waterStorageSchedulingLzzService.lambdaQuery().
                        eq(WaterStorageSchedulingLzz::getYear, year).
                        eq(WaterStorageSchedulingLzz::getDel,0).
                        list();
                if(null!= list && list.size()>0){
                    Map<Integer, List<WaterStorageSchedulingLzz>> collect = list.stream().collect(Collectors.groupingBy(WaterStorageSchedulingLzz::getMonth));
                    Set<Integer> integers = collect.keySet();
                    for(Integer i:integers){
                        Double value = 0.0;
                        List<WaterStorageSchedulingLzz> waterStorageSchedulingLzz = collect.get(i);
                        for(WaterStorageSchedulingLzz lzz:waterStorageSchedulingLzz){
                            value +=lzz.getWaterSupplyVolumeTotal()==null?0.0:lzz.getWaterSupplyVolumeTotal();
                        }
                        result.put(i+"",value);
                    }
                    return JSONObject.toJSONString(result);
                }
            }
            if(timeType==2){
                List<WaterStorageSchedulingLzz> list = waterStorageSchedulingLzzService.lambdaQuery().
                        eq(WaterStorageSchedulingLzz::getYear, year).
                        eq(WaterStorageSchedulingLzz::getMonth, month).
                        eq(WaterStorageSchedulingLzz::getDel,0).
                        list();
                if(null!= list && list.size()>0){
                    for(WaterStorageSchedulingLzz lzz:list){
                        result.put(lzz.getTenDays(),lzz.getWaterSupplyVolumeTotal());
                    }
                    return JSONObject.toJSONString(result);
                }
            }
        }
        if(area.equals("头屯河水库")){
            if(timeType==1){
                List<WaterStorageSchedulingTth> list = waterStorageSchedulingTthService.lambdaQuery().
                        eq(WaterStorageSchedulingTth::getYear, year).
                        eq(WaterStorageSchedulingTth::getDel,0).
                        list();
                if(null!= list && list.size()>0){
                    Map<Integer, List<WaterStorageSchedulingTth>> collect = list.stream().collect(Collectors.groupingBy(WaterStorageSchedulingTth::getMonth));
                    Set<Integer> integers = collect.keySet();
                    for(Integer i:integers){
                        Double value = 0.0;
                        List<WaterStorageSchedulingTth> waterStorageSchedulingTth = collect.get(i);
                        for(WaterStorageSchedulingTth tth:waterStorageSchedulingTth){
                            value +=tth.getWaterSupplyVolumeTotal()==null?0.0:tth.getWaterSupplyVolumeTotal();
                        }
                        result.put(i+"",value);
                    }
                    return JSONObject.toJSONString(result);
                }
            }
            if(timeType==2){
                List<WaterStorageSchedulingTth> list = waterStorageSchedulingTthService.lambdaQuery().
                        eq(WaterStorageSchedulingTth::getYear, year).
                        eq(WaterStorageSchedulingTth::getMonth, month).
                        eq(WaterStorageSchedulingTth::getDel,0).
                        list();
                if(null!= list && list.size()>0){
                    for(WaterStorageSchedulingTth tth:list){
                        result.put(tth.getTenDays(),tth.getWaterSupplyVolumeTotal());
                    }
                    return JSONObject.toJSONString(result);
                }
            }
        }
        return null;
    }

    @Override
    public String getWaterResourceAllocationList(Integer bucketType) {
        List<WaterResourceAllocation> list = waterResourceAllocationService.lambdaQuery().
                eq(WaterResourceAllocation::getBucketType, bucketType).
                eq(WaterResourceAllocation::getDel,0).
                list();
        if(null!= list && list.size()>0){
            return JSONObject.toJSONString(list);
        }
        return null;
    }

    @Override
    public String getWaterResourceAllocationDetails(String id) {
        RestResponse waterResourceAllocationDetails = waterResourceAllocationService.getWaterResourceAllocationDetails(id);
        if(waterResourceAllocationDetails.getCode()==200){
            return JSONObject.toJSONString(waterResourceAllocationDetails.getData());
        }
        return null;
    }

    @Override
    public String contrast(String idA,String idB) {
        RestResponse contrast = waterResourceAllocationService.contrast(idA, idB);
        if(contrast.getCode()==200){
            return JSONObject.toJSONString(contrast.getData());
        }
        return null;
    }

    @Override
    public String waterQuantityCalculation(String id) {
        RestResponse restResponse = waterResourceAllocationService.waterQuantityCalculation(id);
        if(restResponse.getCode()==200){
            return JSONObject.toJSONString(restResponse.getData());
        }
        return null;
    }

    @Override
    public String getRealTimeReservoirLevel(String reservoir) {
        RestResponse restResponse = waterResourceAllocationService.getRealTimeReservoirLevel(reservoir);
        if(restResponse.getCode()==200){
            return JSONObject.toJSONString(restResponse.getData());
        }
        return null;
    }

    @Override
    public String getSupplyDemandBalance() {
        Map<String,Object> result = new HashMap<>();
        int year = LocalDateTime.now().getYear();
        List<WaterStorageSchedulingLzz> lzz = waterStorageSchedulingLzzService.lambdaQuery().eq(WaterStorageSchedulingLzz::getYear, year).list();
        List<WaterStorageSchedulingTth> tth = waterStorageSchedulingTthService.lambdaQuery().eq(WaterStorageSchedulingTth::getYear, year).list();
        result.put("楼庄子水库",lzz);
        result.put("头屯河水库",tth);
        return JSONObject.toJSONString(result);
    }

    @Override
    public String getReservoirWaterConditionAlarm(String reservoir,String time) {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        if(reservoir.equals("楼庄子水库")){
            List<TrendsTableParam> lzzTableParam = trendsTableParamListTemp.stream().filter(t->t.getUseType()==1 && t.getUseStation().equals("楼庄子水库")).collect(Collectors.toList());
            TrendsTableParam lzzJkllTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("进库流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzCkllTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("河道")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzSwTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzKrTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("库容")).collect(Collectors.toList()).get(0);
            RestResponse<Map<String, List<DayWaterSituationStatisticsTableLzz>>> mapRestResponse = dayWaterSituationStatisticsTableLzzService.selectList(time);
            Map<String,Object> lzzResult = new HashMap<>();
            if(mapRestResponse.getCode()==200){
                List<DayWaterSituationStatisticsTableLzz> dataList = new ArrayList<>();
                List<DayWaterSituationStatisticsTableLzz> morning = mapRestResponse.getData().get("08:00");
                if(morning.size()<0){
                    List<DayWaterSituationStatisticsTableLzz> yesterday = mapRestResponse.getData().get("昨日均");
                    dataList.addAll(yesterday);
                }else {
                    dataList.addAll(morning);
                }
                lzzResult.put("进库流量",dataList.stream().filter(t->t.getTableHeadId().equals(lzzJkllTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzJkllTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                lzzResult.put("出库流量",dataList.stream().filter(t->t.getTableHeadId().equals(lzzCkllTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzCkllTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                lzzResult.put("水位",dataList.stream().filter(t->t.getTableHeadId().equals(lzzSwTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzSwTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                lzzResult.put("库容",dataList.stream().filter(t->t.getTableHeadId().equals(lzzKrTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzKrTableParam.getId())).collect(Collectors.toList()).get(0).getV());
            }else {
                lzzResult.put("进库流量","1.052");
                lzzResult.put("出库流量","1.083");
                lzzResult.put("水位","1361.92");
                lzzResult.put("库容","1369.260");
            }
            return JSONObject.toJSONString(lzzResult);
        }
       if(reservoir.equals("头屯河水库")){
           List<TrendsTableParam> tthTableParam = trendsTableParamListTemp.stream().filter(t->t.getUseType()==1 && t.getUseStation().equals("头屯河水库")).collect(Collectors.toList());
           TrendsTableParam tthJkllTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("进库流量") && !t.getPId().equals("0")).collect(Collectors.toList()).get(0);
           TrendsTableParam tthCkllTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("河道流量")).collect(Collectors.toList()).get(0);
           TrendsTableParam tthSwTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
           TrendsTableParam tthKrTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("水库库容")).collect(Collectors.toList()).get(0);
           RestResponse<Map<String, List<DayWaterSituationStatisticsTableTth>>> mapRestResponse1 = dayWaterSituationStatisticsTableTthService.selectList(time);
           Map<String,Object> tthResult = new HashMap<>();
           if(mapRestResponse1.getCode()==200){
               List<DayWaterSituationStatisticsTableTth> dataList = new ArrayList<>();
               List<DayWaterSituationStatisticsTableTth> morning = mapRestResponse1.getData().get("08:00");
               if(morning.size()<0){
                   List<DayWaterSituationStatisticsTableTth> yesterday = mapRestResponse1.getData().get("昨日均");
                   dataList.addAll(yesterday);
               }else {
                   dataList.addAll(morning);
               }
               tthResult.put("进库流量",dataList.stream().filter(t->t.getTableHeadId().equals(tthJkllTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthJkllTableParam.getId())).collect(Collectors.toList()).get(0).getV());
               tthResult.put("出库流量",dataList.stream().filter(t->t.getTableHeadId().equals(tthCkllTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthCkllTableParam.getId())).collect(Collectors.toList()).get(0).getV());
               tthResult.put("水位",dataList.stream().filter(t->t.getTableHeadId().equals(tthSwTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthSwTableParam.getId())).collect(Collectors.toList()).get(0).getV());
               tthResult.put("库容",dataList.stream().filter(t->t.getTableHeadId().equals(tthKrTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthKrTableParam.getId())).collect(Collectors.toList()).get(0).getV());
           }else {
               tthResult.put("进库流量","1.2");
               tthResult.put("出库流量","0.640");
               tthResult.put("水位","981.58");
               tthResult.put("库容","509.24");
           }
           return JSONObject.toJSONString(tthResult);
       }
       return null;
    }

    @Override
    public String getTurbidityAlarm(String time) {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> reservoirTurbidity = new HashMap<>();
        List<TrendsTableParam> lzzTableParam = trendsTableParamListTemp.stream().filter(t->t.getUseType()==1 && t.getUseStation().equals("楼庄子水库")).collect(Collectors.toList());
        TrendsTableParam lzzJkzdTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("进库浊度")).collect(Collectors.toList()).get(0);
        TrendsTableParam lzzCkzdTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("出库河道浊度")).collect(Collectors.toList()).get(0);
        RestResponse<Map<String, List<DayWaterSituationStatisticsTableLzz>>> mapRestResponse = dayWaterSituationStatisticsTableLzzService.selectList(time);
        Map<String,Object> lzzResultTurbidity = new HashMap<>();
        if(mapRestResponse.getCode()==200){
            List<DayWaterSituationStatisticsTableLzz> dataList = new ArrayList<>();
            List<DayWaterSituationStatisticsTableLzz> morning = mapRestResponse.getData().get("08:00");
            if(morning.size()<0){
                List<DayWaterSituationStatisticsTableLzz> yesterday = mapRestResponse.getData().get("昨日均");
                dataList.addAll(yesterday);
            }else {
                dataList.addAll(morning);
            }
            lzzResultTurbidity.put("进库浊度",dataList.stream().filter(t->t.getTableHeadId().equals(lzzJkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzJkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV());
            lzzResultTurbidity.put("出库浊度",dataList.stream().filter(t->t.getTableHeadId().equals(lzzCkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzCkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV());

        }else {
            lzzResultTurbidity.put("进库浊度",null);
            lzzResultTurbidity.put("出库浊度",null);
        }
        List<TrendsTableParam> tthTableParam = trendsTableParamListTemp.stream().filter(t->t.getUseType()==1 && t.getUseStation().equals("头屯河水库")).collect(Collectors.toList());
        TrendsTableParam tthJkzdTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("进库浊度")).collect(Collectors.toList()).get(0);
        TrendsTableParam tthCkzdTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("河道浊度")).collect(Collectors.toList()).get(0);
        TrendsTableParam tthAgzdTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("暗渠浊度")).collect(Collectors.toList()).get(0);
        RestResponse<Map<String, List<DayWaterSituationStatisticsTableTth>>> mapRestResponse1 = dayWaterSituationStatisticsTableTthService.selectList(time);
        Map<String,Object> tthResultTurbidity = new HashMap<>();
        Map<String,Object> turbidity = new HashMap<>();
        if(mapRestResponse1.getCode()==200) {
            List<DayWaterSituationStatisticsTableTth> dataList = new ArrayList<>();
            List<DayWaterSituationStatisticsTableTth> morning = mapRestResponse1.getData().get("08:00");
            if(morning.size()<0){
                List<DayWaterSituationStatisticsTableTth> yesterday = mapRestResponse1.getData().get("昨日均");
                dataList.addAll(yesterday);
            }else {
                dataList.addAll(morning);
            }
            tthResultTurbidity.put("进库浊度",dataList.stream().filter(t->t.getTableHeadId().equals(tthJkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthJkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV());
            tthResultTurbidity.put("出库浊度",dataList.stream().filter(t->t.getTableHeadId().equals(tthCkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthCkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV());

            turbidity.put("暗管",dataList.stream().filter(t->t.getTableHeadId().equals(tthAgzdTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthAgzdTableParam.getId())).collect(Collectors.toList()).get(0).getV());
        }else {
            tthResultTurbidity.put("进库浊度",null);
            tthResultTurbidity.put("出库浊度",null);
            turbidity.put("暗管",null);
        }
        reservoirTurbidity.put("楼庄子水库",lzzResultTurbidity);
        reservoirTurbidity.put("头屯河水库",tthResultTurbidity);
        result.put("水库浊度",reservoirTurbidity);
        result.put("暗渠浊度",turbidity);
        return JSONObject.toJSONString(result);
    }

    @Override
    public String getWaterAlarm() {
        return null;
    }

    @Override
    public String getRealTimeWaterSituationOfTheReservoir(String reservoir) {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(reservoir.equals("楼庄子水库")){
            List<TrendsTableParam> lzzTableParam = trendsTableParamListTemp.stream().filter(t->t.getUseType()==1 && t.getUseStation().equals("楼庄子水库")).collect(Collectors.toList());
            TrendsTableParam lzzJkllTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("进库流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzCkllTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("河道")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzSwTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzKrTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("库容")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzJkzdTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("进库浊度")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzCkzdTableParam = lzzTableParam.stream().filter(t -> t.getParamName().equals("出库河道浊度")).collect(Collectors.toList()).get(0);
            RestResponse<Map<String, List<DayWaterSituationStatisticsTableLzz>>> mapRestResponse = dayWaterSituationStatisticsTableLzzService.selectList(sdf.format(new Date()));
            Map<String,Object> lzzResult = new HashMap<>();
            if(mapRestResponse.getCode()==200){
                List<DayWaterSituationStatisticsTableLzz> dataList = new ArrayList<>();
                List<DayWaterSituationStatisticsTableLzz> morning = mapRestResponse.getData().get("08:00");
                if(morning.size()<0){
                    List<DayWaterSituationStatisticsTableLzz> yesterday = mapRestResponse.getData().get("昨日均");
                    dataList.addAll(yesterday);
                }else {
                    dataList.addAll(morning);
                }
                lzzResult.put("进库流量",dataList.stream().filter(t->t.getTableHeadId().equals(lzzJkllTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzJkllTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                lzzResult.put("出库流量",dataList.stream().filter(t->t.getTableHeadId().equals(lzzCkllTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzCkllTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                lzzResult.put("水位",dataList.stream().filter(t->t.getTableHeadId().equals(lzzSwTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzSwTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                lzzResult.put("库容",dataList.stream().filter(t->t.getTableHeadId().equals(lzzKrTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzKrTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                lzzResult.put("进库浊度",dataList.stream().filter(t->t.getTableHeadId().equals(lzzJkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzJkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                lzzResult.put("出库浊度",dataList.stream().filter(t->t.getTableHeadId().equals(lzzCkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(lzzCkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                lzzResult.put("剩余库容",7374-((Double)lzzResult.get("库容")==null?0.0:(Double)lzzResult.get("库容")));
            }else {
                lzzResult.put("进库流量",null);
                lzzResult.put("出库流量",null);
                lzzResult.put("水位",null);
                lzzResult.put("库容",null);
                lzzResult.put("进库浊度",null);
                lzzResult.put("出库浊度",null);
                lzzResult.put("剩余库容",null);
            }
            return JSONObject.toJSONString(lzzResult);
        }
        if(reservoir.equals("头屯河水库")){
            List<TrendsTableParam> tthTableParam = trendsTableParamListTemp.stream().filter(t->t.getUseType()==1 && t.getUseStation().equals("头屯河水库")).collect(Collectors.toList());
            TrendsTableParam tthJkllTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("进库流量") && !t.getPId().equals("0")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthCkllTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("河道流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthSwTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthKrTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("水库库容")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthJkzdTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("进库浊度")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthCkzdTableParam = tthTableParam.stream().filter(t -> t.getParamName().equals("河道浊度")).collect(Collectors.toList()).get(0);
            RestResponse<Map<String, List<DayWaterSituationStatisticsTableTth>>> mapRestResponse1 = dayWaterSituationStatisticsTableTthService.selectList(sdf.format(new Date()));
            Map<String,Object> tthResult = new HashMap<>();
            if(mapRestResponse1.getCode()==200){
                List<DayWaterSituationStatisticsTableTth> dataList = new ArrayList<>();
                List<DayWaterSituationStatisticsTableTth> morning = mapRestResponse1.getData().get("08:00");
                if(morning.size()<0){
                    List<DayWaterSituationStatisticsTableTth> yesterday = mapRestResponse1.getData().get("昨日均");
                    dataList.addAll(yesterday);
                }else {
                    dataList.addAll(morning);
                }
                tthResult.put("进库流量",dataList.stream().filter(t->t.getTableHeadId().equals(tthJkllTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthJkllTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                tthResult.put("出库流量",dataList.stream().filter(t->t.getTableHeadId().equals(tthCkllTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthCkllTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                tthResult.put("水位",dataList.stream().filter(t->t.getTableHeadId().equals(tthSwTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthSwTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                tthResult.put("库容",dataList.stream().filter(t->t.getTableHeadId().equals(tthKrTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthKrTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                tthResult.put("进库浊度",dataList.stream().filter(t->t.getTableHeadId().equals(tthJkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthJkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                tthResult.put("出库浊度",dataList.stream().filter(t->t.getTableHeadId().equals(tthCkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV()==null?0.0:dataList.stream().filter(t->t.getTableHeadId().equals(tthCkzdTableParam.getId())).collect(Collectors.toList()).get(0).getV());
                tthResult.put("剩余库容",2030-((Double)tthResult.get("库容")==null?0.0:(Double)tthResult.get("库容")));
            }else {
                tthResult.put("进库流量",null);
                tthResult.put("出库流量",null);
                tthResult.put("水位",null);
                tthResult.put("库容",null);
                tthResult.put("进库浊度",null);
                tthResult.put("出库浊度",null);
                tthResult.put("剩余库容",null);
            }
            return JSONObject.toJSONString(tthResult);
        }
        return null;
    }

    @Override
    public String getRealTimeWaterLevel(String station) {
        List<IrrigatedPlatformDataInfo> realTimeWaterLevel = irrigatedPlatformDataInfoService.getRealTimeWaterLevel(station);
        if(null != realTimeWaterLevel && realTimeWaterLevel.size()>0){
            return JSONObject.toJSONString(realTimeWaterLevel);
        }
        return null;
    }

    @Override
    public String getWaterSupplyStatistics(String station) {
        Integer year = LocalDateTime.now().getYear();
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> waterUser = new HashMap<>();
        RestResponse<List<WaterStorageOverviewRes>> listRestResponse = waterResourceHomePageService.waterStorageOverview(new Date());
        if(listRestResponse.getCode()==200){
            List<WaterStorageOverviewRes> data = listRestResponse.getData();
            result.put("lzzSupply",data.stream().filter(t->t.getWaterStorageName().equals("楼庄子水库")).map(WaterStorageOverviewRes::getYearFloodRetentionCapacity).collect(Collectors.toList()).get(0));
            result.put("tthSupply",data.stream().filter(t->t.getWaterStorageName().equals("头屯河水库")).map(WaterStorageOverviewRes::getYearFloodRetentionCapacity).collect(Collectors.toList()).get(0));
        }else {
            result.put("lzzSupply",null);
            result.put("lzzSupply",null);
        }
        List<IrrigationQuota> list = irrigationQuotaService.lambdaQuery().eq(IrrigationQuota::getStation, station).eq(IrrigationQuota::getYear,year).
                eq(IrrigationQuota::getDel,0).list();
        Map<String, List<IrrigationQuota>> collect = list.stream().collect(Collectors.groupingBy(IrrigationQuota::getWaterUser));
        Set<String> strings = collect.keySet();
        for(String s:strings){
            Double aDouble = collect.get(s).stream().map(IrrigationQuota::getAccumulatedTotalIrrigationAmount).reduce(Double::sum).orElse(0.00);
            waterUser.put(s,aDouble);
        }
        if(waterUser.size()>0){
            result.put("waterUser",waterUser);
        }else {
            result.put("waterUser",null);
        }
        return JSONObject.toJSONString(result);
    }

    @Override
    public String getWaterFeeStatistics() {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        Map<String,WaterFeeStatisticsRes> result = new HashMap<>();
        Integer year = LocalDateTime.now().getYear();
        Integer month = LocalDateTime.now().getMonth().getValue();
        Integer day = LocalDateTime.now().getDayOfMonth();
        List<TrendsTableParam> totalId = trendsTableParamListTemp.stream().filter(t->t.getPId().equals("0") && t.getUseType()==2 && t.getParamName().equals("合计")).collect(Collectors.toList());

        List<WaterFeeStatisticsTotal> waterFeeStatisticsTotalList = waterFeeStatisticsTotalService.lambdaQuery().
                eq(WaterFeeStatisticsTotal::getYear, year).
                eq(WaterFeeStatisticsTotal::getMonth,month).
                eq(WaterFeeStatisticsTotal::getTenDays,determineTenDays(day)).list();
        if(null != waterFeeStatisticsTotalList && waterFeeStatisticsTotalList.size() > 0){
            Map<String, List<WaterFeeStatisticsTotal>> collect = waterFeeStatisticsTotalList.stream().collect(Collectors.groupingBy(WaterFeeStatisticsTotal::getStation));
            Set<String> strings = collect.keySet();
            for(String s:strings){
                List<WaterFeeStatisticsTotal> waterFeeStatisticsTotalList1 = collect.get(s);
                TrendsTableParam param = null;
                if(s.equals("渠首管理站")){
                    param = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType, 2).eq(TrendsTableParam::getParamName, "来水").one();
                }else {
                    param = totalId.stream().filter(t -> t.getUseStation().equals(s)).collect(Collectors.toList()).get(0);
                }
                String id = param.getId();
                List<WaterFeeStatisticsTotal> collect1 = waterFeeStatisticsTotalList1.stream().filter(t -> t.getTableHeadId().equals(id)).collect(Collectors.toList());
                WaterFeeStatisticsRes res = new WaterFeeStatisticsRes();
                res.setPayableWaterFee(collect1.stream().map(WaterFeeStatisticsTotal::getPayableWaterFee).reduce(Double::sum).orElse(0.00));
                res.setAdvancePaymentWaterFee(collect1.stream().map(WaterFeeStatisticsTotal::getAdvancePaymentWaterFee).reduce(Double::sum).orElse(0.00));
                result.put(s,res);
            }
            return JSONObject.toJSONString(result);
        }else {
            return null;
        }
    }

    @Override
    public String getTodayInspectionStatistics() {
        Map<String,Object> result = new HashMap<>();
        List<AbnormalRes> abnormalList1 = inspectionInterface.getAbnormalList1();
        if(null != abnormalList1 && abnormalList1.size()>0){
            result.put("findProblem",abnormalList1.size());
        }else {
            result.put("findProblem",null);
        }
        List<InspectionRes> inspectionList1 = inspectionInterface.getInspectionList1();
        if(null != inspectionList1 && inspectionList1.size()>0){
            Map<String,Object> inspectionMap = new HashMap<>();
            Map<String, List<InspectionRes>> collect = inspectionList1.stream().collect(Collectors.groupingBy(InspectionRes::getTaskFlag_dictText));
            Set<String> strings = collect.keySet();
            for(String s:strings){
                inspectionMap.put(s,collect.get(s).size());
            }
            Map<String, List<InspectionRes>> collect1 = inspectionList1.stream().collect(Collectors.groupingBy(InspectionRes::getTaskSchemeType_dictText));
            Set<String> strings1= collect1.keySet();
            for(String s:strings1){
                List<InspectionRes> inspectionRes = collect1.get(s);
                inspectionMap.put(s,inspectionRes.size());
            }
            result.put("inspectionMap",inspectionMap);
        }
        return JSONObject.toJSONString(result);
    }

    @Override
    public String getFormList() {
        List<WaterStorageSchedulingTotalForm> list = waterStorageSchedulingTotalFormService.list();
        if(!list.isEmpty()){
            return JSONObject.toJSONString(list);
        }
        return null;
    }

    @Override
    public String getSupplyDemandBalanceByFormId(String id) {
        Map<String,Object> result = new HashMap<>();
        List<WaterStorageSchedulingLzz> lzz = waterStorageSchedulingLzzService.lambdaQuery().eq(WaterStorageSchedulingLzz::getFormId, id).list();
        List<WaterStorageSchedulingTth> tth = waterStorageSchedulingTthService.lambdaQuery().eq(WaterStorageSchedulingTth::getFormId, id).list();
        if(lzz.isEmpty()){
            result.put("lzz",null);
        }else {
            result.put("lzz",lzz);
        }
        if(tth.isEmpty()){
            result.put("tth",null);
        }else {
            result.put("tth",tth);
        }
        if(result.size()>0){
            return JSONObject.toJSONString(result);
        }
        return null;
    }

    @Override
    public String getOverallSituationUnitMgrList() {
        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
        if(StringUtils.isEmpty(overall)){
            List<OverallSituationUnitMgr> list = overallSituationUnitMgrService.list();
            redisUtil.set("overallSituationUnitMgr:list", JSONObject.toJSONString(list));
            overall = JSONObject.toJSONString(list);
        }
        return overall;
    }

    public String determineTenDays(Integer day){
        if(day<=10){
            return "上旬";
        }
        if(day<=20){
            return "中旬";
        }
        if(day>20){
            return "下旬";
        }
        return "";
    }

    public void updateCache(){
        List<TrendsTableParam> listed = trendsTableParamService.list();
        redisUtil.set("trendsTableParam:list", JSONObject.toJSONString(listed));
        for (TrendsTableParam param:listed){
            redisUtil.set("trendsTableParam:name:"+param.getId(), param.getParamName());
            redisUtil.set("trendsTableParam:object:"+param.getId(), JSONObject.toJSONString(param));
        }
    }
}
