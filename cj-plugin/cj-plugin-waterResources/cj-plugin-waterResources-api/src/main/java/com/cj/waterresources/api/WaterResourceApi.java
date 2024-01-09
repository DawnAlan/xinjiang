package com.cj.waterresources.api;

public interface WaterResourceApi {

    String getYearWaterPlan(String area);

    String getYearWaterPlanCrop(String area,String unit);

    String getMonthWaterPlan(String area);

    String getMonthWaterPlanCrop(String area,String unit);

    String getTenDaysWaterPlan(String area);

    String getTenDaysWaterPlanCrop(String area,String unit);

    String getDayWaterPlan(String area);

    String getUseWaterUser(String useWaterPlan,String area);

    String getNeedWaterValueList(String area,Integer timeType);

    String getWaterResourceAllocationList(Integer waterDistributionType);

    String getWaterResourceAllocationDetails(String id);

    String contrast(String idA,String idB);

    String waterQuantityCalculation(String id);

    String getRealTimeReservoirLevel(String reservoir);

}
