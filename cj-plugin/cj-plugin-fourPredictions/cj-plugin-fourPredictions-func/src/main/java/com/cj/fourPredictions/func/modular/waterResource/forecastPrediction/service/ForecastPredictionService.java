package com.cj.fourPredictions.func.modular.waterResource.forecastPrediction.service;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.forecastPrediction.bean.req.PredictionListByNameReq;

public interface ForecastPredictionService {

    RestResponse getPredictionListByTimeType(Integer timeType);

    RestResponse getPredictionListByName(PredictionListByNameReq req);

    RestResponse getNeedWaterValueList(String area,Integer timeType);

    RestResponse getYearWaterPlan(String area,Integer year);

    RestResponse getYearWaterPlanCrop(String area,String unit,Integer year);

    RestResponse getMonthWaterPlan(String area,Integer year,Integer month);

    RestResponse getMonthWaterPlanCrop(String area,String unit,Integer year,Integer month);

    RestResponse getTenDaysWaterPlan(String area,Integer year,Integer month,String tenDays);

    RestResponse getTenDaysWaterPlanCrop(String area,String unit,Integer year,Integer month,String tenDays);

    RestResponse getDayWaterPlan(String area,Integer year,Integer month,Integer day);

    RestResponse getUseWaterUser(String useWaterPlan,String area);
}
