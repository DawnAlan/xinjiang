package com.cj.fourPredictions.func.modular.flood.planResponse.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.flood.api.PredictionApi;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.res.IncomingWaterForecast;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.res.IncomingWaterForecastDetailsRes;
import com.cj.fourPredictions.func.modular.flood.planResponse.bean.res.FloodControlOperation;
import com.cj.fourPredictions.func.modular.flood.planResponse.service.PlanResponseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PlanResponseServiceImpl implements PlanResponseService {

    @Resource
    private PredictionApi predictionApi;

    @Override
    public RestResponse<List<FloodControlOperation>> getFloodControlOperationListById(String id) {
        String data = predictionApi.getFloodControlOperationListById(id);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data, FloodControlOperation.class));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse<List<IncomingWaterForecast>> getProgrammeListForFloodControlOperation() {
        String data = predictionApi.getProgrammeListForFloodControlOperation();
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data, IncomingWaterForecast.class));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getFloodControlOperationFrontViewById(String id) {
        String data = predictionApi.getFloodControlOperationFrontViewById(id);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getFloodControlOperationDetails(String id) {
        String data = predictionApi.getFloodControlOperationDetails(id);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }
}
