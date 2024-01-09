package com.cj.fourPredictions.func.modular.waterResource.forecastPrediction.service;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.forecastPrediction.bean.req.PredictionListByNameReq;

public interface ForecastPredictionService {

    RestResponse getPredictionListByTimeType(Integer timeType);

    RestResponse getPredictionListByName(PredictionListByNameReq req);

    RestResponse getNeedWaterValueList(String area,Integer timeType);

    RestResponse getYearWaterPlan(String area);

    RestResponse getYearWaterPlanCrop(String area,String unit);

    RestResponse getMonthWaterPlan(String area);

    RestResponse getMonthWaterPlanCrop(String area,String unit);

    RestResponse getTenDaysWaterPlan(String area);

    RestResponse getTenDaysWaterPlanCrop(String area,String unit);

    RestResponse getDayWaterPlan(String area);

    RestResponse getUseWaterUser(String useWaterPlan,String area);
}
