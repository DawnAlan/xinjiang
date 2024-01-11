package com.cj.waterresources.feign;

import com.cj.common.consts.FeignConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = FeignConstant.WATER_RESOURCES_APP,contextId="waterResourceFeign")
public interface WaterResourceFeign {

    @RequestMapping("/feign/provider/waterResource/getYearWaterPlan")
    String getYearWaterPlan(@RequestParam(value = "area", required =true)String area);

    @RequestMapping("/feign/provider/waterResource/getYearWaterPlanCrop")
    String getYearWaterPlanCrop(@RequestParam(value = "area", required =true)String area,
                                @RequestParam(value = "unit", required =true)String unit);

    @RequestMapping("/feign/provider/waterResource/getMonthWaterPlan")
    String getMonthWaterPlan(@RequestParam(value = "area", required =true)String area);

    @RequestMapping("/feign/provider/waterResource/getMonthWaterPlanCrop")
    String getMonthWaterPlanCrop(@RequestParam(value = "area", required =true)String area,
                                 @RequestParam(value = "unit", required =true)String unit);

    @RequestMapping("/feign/provider/waterResource/getTenDaysWaterPlan")
    String getTenDaysWaterPlan(@RequestParam(value = "area", required =true)String area);

    @RequestMapping("/feign/provider/waterResource/getTenDaysWaterPlanCrop")
    String getTenDaysWaterPlanCrop(@RequestParam(value = "area", required =true)String area,
                                   @RequestParam(value = "unit", required =true)String unit);

    @RequestMapping("/feign/provider/waterResource/getDayWaterPlan")
    String getDayWaterPlan(@RequestParam(value = "area", required =true)String area);

    @RequestMapping("/feign/provider/waterResource/getUseWaterUser")
    String getUseWaterUser(@RequestParam(value = "useWaterPlan", required =true)String useWaterPlan,
                           @RequestParam(value = "area", required =true)String area);

    @RequestMapping("/feign/provider/waterResource/getNeedWaterValueList")
    String getNeedWaterValueList(@RequestParam(value = "area", required =true)String area,
                           @RequestParam(value = "timeType", required =true)Integer timeType);

    @RequestMapping("/feign/provider/waterResource/getWaterResourceAllocationList")
    String getWaterResourceAllocationList(@RequestParam(value = "bucketType", required =true)Integer bucketType);

    @RequestMapping("/feign/provider/waterResource/getWaterResourceAllocationDetails")
    String getWaterResourceAllocationDetails(@RequestParam(value = "id", required =true)String id);

    @RequestMapping("/feign/provider/waterResource/contrast")
    String contrast(@RequestParam(value = "idA", required =true)String idA,
                    @RequestParam(value = "idB", required =true)String idB);

    @RequestMapping("/feign/provider/waterResource/waterQuantityCalculation")
    String waterQuantityCalculation(@RequestParam(value = "id", required =true)String id);

    @RequestMapping("/feign/provider/waterResource/getRealTimeReservoirLevel")
    String getRealTimeReservoirLevel(@RequestParam(value = "reservoir", required =true)String reservoir);

    @RequestMapping("/feign/provider/waterResource/getSupplyDemandBalance")
    String getSupplyDemandBalance();

    @RequestMapping("/feign/provider/waterResource/getReservoirWaterConditionAlarm")
    String getReservoirWaterConditionAlarm(@RequestParam(value = "reservoir", required =true)String reservoir,
                                           @RequestParam(value = "time", required =true)String time);

    @RequestMapping("/feign/provider/waterResource/getTurbidityAlarm")
    String getTurbidityAlarm(@RequestParam(value = "time", required =true)String time);

    @RequestMapping("/feign/provider/waterResource/getWaterAlarm")
    String getWaterAlarm();
}
