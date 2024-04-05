package com.cj.waterresources.core.provider;

import com.cj.waterresources.feign.WaterResourceFeign;
import com.cj.waterresources.func.modular.provider.WaterResourceApiProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class WaterResourceFeignProvider implements WaterResourceFeign {

    private final WaterResourceApiProvider waterResourceApiProvider;
    @Override
    public String getYearWaterPlan(String area) {
        return waterResourceApiProvider.getYearWaterPlan(area);
    }

    @Override
    public String getYearWaterPlanCrop(String area, String unit) {
        return waterResourceApiProvider.getYearWaterPlanCrop(area,unit);
    }

    @Override
    public String getMonthWaterPlan(String area) {
        return waterResourceApiProvider.getMonthWaterPlan(area);
    }

    @Override
    public String getMonthWaterPlanCrop(String area, String unit) {
        return waterResourceApiProvider.getMonthWaterPlanCrop(area,unit);
    }

    @Override
    public String getTenDaysWaterPlan(String area) {
        return waterResourceApiProvider.getTenDaysWaterPlan(area);
    }

    @Override
    public String getTenDaysWaterPlanCrop(String area, String unit) {
        return waterResourceApiProvider.getTenDaysWaterPlanCrop(area,unit);
    }

    @Override
    public String getDayWaterPlan(String area) {
        return waterResourceApiProvider.getDayWaterPlan(area);
    }

    @Override
    public String getUseWaterUser(String useWaterPlan, String area) {
        return waterResourceApiProvider.getUseWaterUser(useWaterPlan,area);
    }

    @Override
    public String getNeedWaterValueList(String area, Integer timeType) {
        return waterResourceApiProvider.getNeedWaterValueList(area,timeType);
    }

    @Override
    public String getWaterResourceAllocationList(Integer bucketType,String inflowDataName) {
        return waterResourceApiProvider.getWaterResourceAllocationList(bucketType,inflowDataName);
    }

    @Override
    public String getWaterResourceAllocationDetails(String id) {
        return waterResourceApiProvider.getWaterResourceAllocationDetails(id);
    }

    @Override
    public String contrast(String idA, String idB) {
        return waterResourceApiProvider.contrast(idA,idB);
    }

    @Override
    public String waterQuantityCalculation(String id) {
        return waterResourceApiProvider.waterQuantityCalculation(id);
    }

    @Override
    public String getRealTimeReservoirLevel(String reservoir) {
        return waterResourceApiProvider.getRealTimeReservoirLevel(reservoir);
    }

    @Override
    public String getSupplyDemandBalance() {
        return waterResourceApiProvider.getSupplyDemandBalance();
    }

    @Override
    public String getReservoirWaterConditionAlarm(String reservoir, String time) {
        return waterResourceApiProvider.getReservoirWaterConditionAlarm(reservoir,time);
    }

    @Override
    public String getTurbidityAlarm(String time) {
        return waterResourceApiProvider.getTurbidityAlarm(time);
    }

    @Override
    public String getWaterAlarm() {
        return waterResourceApiProvider.getWaterAlarm();
    }

    @Override
    public String getRealTimeWaterSituationOfTheReservoir(String reservoir) {
        return waterResourceApiProvider.getRealTimeWaterSituationOfTheReservoir(reservoir);
    }

    @Override
    public String getRealTimeWaterLevel(String station) {
        return waterResourceApiProvider.getRealTimeWaterLevel(station);
    }

    @Override
    public String getWaterSupplyStatistics(String station) {
        return waterResourceApiProvider.getWaterSupplyStatistics(station);
    }

    @Override
    public String getWaterFeeStatistics() {
        return waterResourceApiProvider.getWaterFeeStatistics();
    }

    @Override
    public String getTodayInspectionStatistics() {
        return waterResourceApiProvider.getTodayInspectionStatistics();
    }

    @Override
    public String getFormList() {
        return waterResourceApiProvider.getFormList();
    }

    @Override
    public String getSupplyDemandBalanceByFormId(String id) {
        return waterResourceApiProvider.getSupplyDemandBalanceByFormId(id);
    }

    @Override
    public String getOverallSituationUnitMgrList() {
        return waterResourceApiProvider.getOverallSituationUnitMgrList();
    }
}
