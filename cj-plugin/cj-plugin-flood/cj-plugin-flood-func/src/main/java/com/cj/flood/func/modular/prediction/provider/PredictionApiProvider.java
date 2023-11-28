package com.cj.flood.func.modular.prediction.provider;

import com.alibaba.fastjson.JSONObject;
import com.cj.flood.api.PredictionApi;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.cj.flood.func.modular.prediction.service.IncomingWaterForecastService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PredictionApiProvider implements PredictionApi {

    @Resource
    private IncomingWaterForecastService incomingWaterForecastService;
    @Override
    public String getProgrammeListByTime(String startTime, String endTime) {
        List<IncomingWaterForecast> list = incomingWaterForecastService.lambdaQuery().between(IncomingWaterForecast::getPredictionTime, startTime, endTime).list();
        if(null != list && list.size() > 0) {
            return JSONObject.toJSONString(list);
        }
        return null;
    }
}
