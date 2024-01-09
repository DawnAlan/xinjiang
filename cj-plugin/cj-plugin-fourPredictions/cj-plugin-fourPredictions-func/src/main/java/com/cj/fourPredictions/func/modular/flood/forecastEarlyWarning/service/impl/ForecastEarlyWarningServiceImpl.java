package com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.flood.api.PredictionApi;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.res.IncomingWaterForecast;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.res.IncomingWaterForecastDetailsRes;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.service.ForecastEarlyWarningService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ForecastEarlyWarningServiceImpl implements ForecastEarlyWarningService {

    @Resource
    private PredictionApi predictionApi;


    @Override
    public RestResponse<List<IncomingWaterForecast>> getProgrammeList() {
        String data = predictionApi.getProgrammeList();
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data, IncomingWaterForecast.class));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getDetails(String id) {
        String data = predictionApi.getProgrammeDetails(id);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }
}
