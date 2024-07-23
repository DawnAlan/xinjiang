package com.cj.waterresources.api;

import java.util.List;

public interface WaterResourceApi {

    String getYearWaterPlan(String area,Integer year);

    String getYearWaterPlanCrop(String area,String unit,Integer year);

    String getMonthWaterPlan(String area,Integer year,Integer month);

    String getMonthWaterPlanCrop(String area,String unit,Integer year,Integer month);

    String getTenDaysWaterPlan(String area,Integer year,Integer month,String tenDays);

    String getTenDaysWaterPlanCrop(String area,String unit,Integer year,Integer month,String tenDays);

    String getDayWaterPlan(String area,Integer year,Integer month,Integer day);

    String getUseWaterUser(String useWaterPlan,String area);

    String getNeedWaterValueList(String area,Integer timeType);

    String getWaterResourceAllocationList(Integer bucketType,String inflowDataName);

    String getWaterResourceAllocationDetails(String id);

    String contrast(String idA,String idB);
    String contrastNew(List<String> ids);

    String waterQuantityCalculation(String id);

    String getRealTimeReservoirLevel(String reservoir);

    String getSupplyDemandBalance();

    String getReservoirWaterConditionAlarm(String reservoir,String time);

    String getTurbidityAlarm(String time);

    String getWaterAlarm();

    String getRealTimeWaterSituationOfTheReservoir(String reservoir,String time);

    String getRealTimeWaterLevel(String station,String time);

    String getWaterSupplyStatistics(String time);

    String getWaterFeeStatistics(String time);

    String getTodayInspectionStatistics(String time);

    String getFormList();

    String getSupplyDemandBalanceByFormId(String id);

    String getOverallSituationUnitMgrList();

    String getRealTimeWaterLevelData(String time);

    void sendMsg(String msg);


}
