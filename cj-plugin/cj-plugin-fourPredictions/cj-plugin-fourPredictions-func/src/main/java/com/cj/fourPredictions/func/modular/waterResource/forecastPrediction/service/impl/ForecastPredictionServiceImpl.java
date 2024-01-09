package com.cj.fourPredictions.func.modular.waterResource.forecastPrediction.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.flood.api.PredictionApi;
import com.cj.fourPredictions.func.modular.waterResource.forecastPrediction.bean.req.PredictionListByNameReq;
import com.cj.fourPredictions.func.modular.waterResource.forecastPrediction.service.ForecastPredictionService;
import com.cj.waterresources.api.WaterResourceApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ForecastPredictionServiceImpl implements ForecastPredictionService {

    @Resource
    private PredictionApi predictionApi;

    @Resource
    private WaterResourceApi waterResourceApi;

    @Override
    public RestResponse getPredictionListByTimeType(Integer timeType) {
        String data = predictionApi.getPredictionListByTimeType(timeType);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getPredictionListByName(PredictionListByNameReq req) {
        String data = predictionApi.getPredictionListByName(req.getId(),req.getReservoir());
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getNeedWaterValueList(String area, Integer timeType) {
        String data = waterResourceApi.getNeedWaterValueList(area,timeType);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getYearWaterPlan(String area) {
        String data = waterResourceApi.getYearWaterPlan(area);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getYearWaterPlanCrop(String area, String unit) {
        String data = waterResourceApi.getYearWaterPlanCrop(area, unit);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getMonthWaterPlan(String area) {
        String data = waterResourceApi.getMonthWaterPlan(area);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getMonthWaterPlanCrop(String area, String unit) {
        String data = waterResourceApi.getMonthWaterPlanCrop(area,unit);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getTenDaysWaterPlan(String area) {
        String data = waterResourceApi.getTenDaysWaterPlan(area);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getTenDaysWaterPlanCrop(String area, String unit) {
        String data = waterResourceApi.getTenDaysWaterPlanCrop(area,unit);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getDayWaterPlan(String area) {
        String data = waterResourceApi.getDayWaterPlan(area);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getUseWaterUser(String useWaterPlan, String area) {
        String data = waterResourceApi.getUseWaterUser(useWaterPlan,area);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data));
        }else {
            return RestResponse.no("blank");
        }
    }
}
