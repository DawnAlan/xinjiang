package com.cj.dataSynchronization.core.context;

import com.cj.waterresources.api.WaterResourceApi;
import com.cj.waterresources.feign.WaterResourceFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class WaterResourceApiContextBean implements WaterResourceApi {

    private final WaterResourceFeign waterResourceFeign;
    @Override
    public String getYearWaterPlan(String area,Integer year) {
        String data = waterResourceFeign.getYearWaterPlan(area,year);
        return data;
    }

    @Override
    public String getYearWaterPlanCrop(String area, String unit,Integer year) {
        String data = waterResourceFeign.getYearWaterPlanCrop(area,unit,year);
        return data;
    }

    @Override
    public String getMonthWaterPlan(String area,Integer year,Integer month) {
        String data = waterResourceFeign.getMonthWaterPlan(area,year,month);
        return data;
    }

    @Override
    public String getMonthWaterPlanCrop(String area, String unit,Integer year,Integer month) {
        String data = waterResourceFeign.getMonthWaterPlanCrop(area,unit,year,month);
        return data;
    }

    @Override
    public String getTenDaysWaterPlan(String area,Integer year,Integer month,String tenDays) {
        String data = waterResourceFeign.getTenDaysWaterPlan(area,year,month,tenDays);
        return data;
    }

    @Override
    public String getTenDaysWaterPlanCrop(String area, String unit,Integer year,Integer month,String tenDays) {
        String data = waterResourceFeign.getTenDaysWaterPlanCrop(area,unit,year,month,tenDays);
        return data;
    }

    @Override
    public String getDayWaterPlan(String area,Integer year,Integer month,Integer day) {
        String data = waterResourceFeign.getDayWaterPlan(area,year,month,day);
        return data;
    }

    @Override
    public String getUseWaterUser(String useWaterPlan, String area) {
        String data = waterResourceFeign.getUseWaterUser(useWaterPlan,area);
        return data;
    }

    @Override
    public String getNeedWaterValueList(String area, Integer timeType) {
        String data = waterResourceFeign.getNeedWaterValueList(area,timeType);
        return data;
    }

    @Override
    public String getWaterResourceAllocationList(Integer bucketType,String inflowDataName) {
        String data = waterResourceFeign.getWaterResourceAllocationList(bucketType,inflowDataName);
        return data;
    }

    @Override
    public String getWaterResourceAllocationDetails(String id) {
        String data = waterResourceFeign.getWaterResourceAllocationDetails(id);
        return data;
    }

    @Override
    public String contrast(String idA, String idB) {
        String data = waterResourceFeign.contrast(idA,idB);
        return data;
    }

    @Override
    public String contrastNew(List<String> ids) {
        String data = waterResourceFeign.contrastNew(ids);
        return data;
    }

    @Override
    public String waterQuantityCalculation(String id) {
        String data = waterResourceFeign.waterQuantityCalculation(id);
        return data;
    }

    @Override
    public String getRealTimeReservoirLevel(String reservoir) {
        String data = waterResourceFeign.getRealTimeReservoirLevel(reservoir);
        return data;
    }

    @Override
    public String getSupplyDemandBalance() {
        String data = waterResourceFeign.getSupplyDemandBalance();
        return data;
    }

    @Override
    public String getReservoirWaterConditionAlarm(String reservoir, String time) {
        String data = waterResourceFeign.getReservoirWaterConditionAlarm(reservoir,time);
        return data;
    }

    @Override
    public String getTurbidityAlarm(String time) {
        String data = waterResourceFeign.getTurbidityAlarm(time);
        return data;
    }

    @Override
    public String getWaterAlarm() {
        String data = waterResourceFeign.getWaterAlarm();
        return data;
    }

    @Override
    public String getRealTimeWaterSituationOfTheReservoir(String reservoir,String time) {
        String data = waterResourceFeign.getRealTimeWaterSituationOfTheReservoir(reservoir,time);
        return data;
    }

    @Override
    public String getRealTimeWaterLevel(String station,String time) {
        String data = waterResourceFeign.getRealTimeWaterLevel(station,time);
        return data;
    }

    @Override
    public String getWaterSupplyStatistics(String time) {
        String data = waterResourceFeign.getWaterSupplyStatistics(time);
        return data;
    }

    @Override
    public String getWaterFeeStatistics(String time) {
        String data = waterResourceFeign.getWaterFeeStatistics(time);
        return data;
    }

    @Override
    public String getTodayInspectionStatistics(String time) {
        String data = waterResourceFeign.getTodayInspectionStatistics(time);
        return data;
    }

    @Override
    public String getFormList() {
        String data = waterResourceFeign.getFormList();
        return data;
    }

    @Override
    public String getSupplyDemandBalanceByFormId(String id) {
        String data = waterResourceFeign.getSupplyDemandBalanceByFormId(id);
        return data;
    }

    @Override
    public String getOverallSituationUnitMgrList() {
        String data = waterResourceFeign.getOverallSituationUnitMgrList();
        return data;
    }

    @Override
    public String getRealTimeWaterLevelData(String time) {
        String data = waterResourceFeign.getRealTimeWaterLevelData(time);
        return data;
    }

    @Override
    public void sendMsg(String msg) {
        waterResourceFeign.sendMsg(msg);
    }
}
