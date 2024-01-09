package com.cj.waterresources.func.modular.provider;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.api.WaterResourceApi;
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
import com.cj.waterresources.func.modular.waterResourceAllcation.entity.WaterResourceAllocation;
import com.cj.waterresources.func.modular.waterResourceAllcation.service.WaterResourceAllocationService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.entity.WaterStorageSchedulingLzz;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.service.WaterStorageSchedulingLzzService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.entity.WaterStorageSchedulingTth;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.service.WaterStorageSchedulingTthService;
import lombok.AllArgsConstructor;
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
                        (crop.getAprilTotal()==null?0.0:crop.getAprilTotal())+
                        (crop.getMayTotal()==null?0.0:crop.getMayTotal())+
                        (crop.getJuneTotal()==null?0.0:crop.getJuneTotal())+
                        (crop.getJulyTotal()==null?0.0:crop.getJulyTotal())+
                        (crop.getAugustTotal()==null?0.0:crop.getAugustTotal())+
                        (crop.getSeptemberTotal()==null?0.0:crop.getSeptemberTotal())+
                        (crop.getOctoberTotal()==null?0.0:crop.getOctoberTotal())+
                        (crop.getNovemberTotal()==null?0.0:crop.getNovemberTotal())
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
                res.setTotalAmount(crop.getTotal()==null?0.0: crop.getTotal());
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
                eq(TenDayWaterUsePlan::getDel,0).
                list();
        if(null!= list && list.size()>0){
            List<WaterUsePlanForViewRes> resList = new ArrayList<>();
            Map<String, List<TenDayWaterUsePlan>> collect = list.stream().collect(Collectors.groupingBy(TenDayWaterUsePlan::getUseWaterUser));
            Set<String> strings = collect.keySet();
            for(String s1:strings){
                WaterUsePlanForViewRes res = new WaterUsePlanForViewRes();
                res.setUnit(s1);
                Double aDouble = collect.get(s1).stream().filter(t->t.getWaterDemandForThisMonth()!=null).map(TenDayWaterUsePlan::getWaterDemandForThisMonth).reduce(Double::sum).orElse(0.00);
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
                eq(TenDayWaterUsePlan::getDel,0).
                list();
        if(null!= list && list.size()>0){
            List<WaterUsePlanForViewRes> resList = new ArrayList<>();
            for(TenDayWaterUsePlan plan:list){
                WaterUsePlanForViewRes res = new WaterUsePlanForViewRes();
                res.setUnit(plan.getUseWaterUser());
                res.setTotalAmount(plan.getWaterDemandForThisMonth()==null?0.0:plan.getWaterDemandForThisMonth());
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
    public String getWaterResourceAllocationList(Integer waterDistributionType) {
        List<WaterResourceAllocation> list = waterResourceAllocationService.lambdaQuery().eq(WaterResourceAllocation::getWaterDistributionType, waterDistributionType).list();
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
}
