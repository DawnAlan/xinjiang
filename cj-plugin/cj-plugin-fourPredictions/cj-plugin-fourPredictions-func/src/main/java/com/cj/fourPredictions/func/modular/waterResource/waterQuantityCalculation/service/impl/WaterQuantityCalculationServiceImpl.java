package com.cj.fourPredictions.func.modular.waterResource.waterQuantityCalculation.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.waterQuantityCalculation.service.WaterQuantityCalculationService;
import com.cj.waterresources.api.WaterResourceApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WaterQuantityCalculationServiceImpl implements WaterQuantityCalculationService {

    @Resource
    private WaterResourceApi waterResourceApi;
    @Override
    public RestResponse waterQuantityCalculation(String id) {
        String data = waterResourceApi.waterQuantityCalculation(id);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getRealTimeReservoirLevel(String reservoir) {
        String data = waterResourceApi.getRealTimeReservoirLevel(reservoir);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data));
        }else {
            return RestResponse.no("blank");
        }
    }
}
