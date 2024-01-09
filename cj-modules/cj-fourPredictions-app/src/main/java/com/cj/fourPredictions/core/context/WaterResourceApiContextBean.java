package com.cj.fourPredictions.core.context;

import com.cj.waterresources.api.WaterResourceApi;
import com.cj.waterresources.feign.WaterResourceFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class WaterResourceApiContextBean implements WaterResourceApi {

    private final WaterResourceFeign waterResourceFeign;
    @Override
    public String getYearWaterPlan(String area) {
        String data = waterResourceFeign.getYearWaterPlan(area);
        return data;
    }

    @Override
    public String getYearWaterPlanCrop(String area, String unit) {
        String data = waterResourceFeign.getYearWaterPlanCrop(area,unit);
        return data;
    }

    @Override
    public String getMonthWaterPlan(String area) {
        String data = waterResourceFeign.getMonthWaterPlan(area);
        return data;
    }

    @Override
    public String getMonthWaterPlanCrop(String area, String unit) {
        String data = waterResourceFeign.getMonthWaterPlanCrop(area,unit);
        return data;
    }

    @Override
    public String getTenDaysWaterPlan(String area) {
        String data = waterResourceFeign.getTenDaysWaterPlan(area);
        return data;
    }

    @Override
    public String getTenDaysWaterPlanCrop(String area, String unit) {
        String data = waterResourceFeign.getTenDaysWaterPlanCrop(area,unit);
        return data;
    }

    @Override
    public String getDayWaterPlan(String area) {
        String data = waterResourceFeign.getDayWaterPlan(area);
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
    public String getWaterResourceAllocationList(Integer waterDistributionType) {
        String data = waterResourceFeign.getWaterResourceAllocationList(waterDistributionType);
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
    public String waterQuantityCalculation(String id) {
        String data = waterResourceFeign.waterQuantityCalculation(id);
        return data;
    }

    @Override
    public String getRealTimeReservoirLevel(String reservoir) {
        String data = waterResourceFeign.getRealTimeReservoirLevel(reservoir);
        return data;
    }
}
