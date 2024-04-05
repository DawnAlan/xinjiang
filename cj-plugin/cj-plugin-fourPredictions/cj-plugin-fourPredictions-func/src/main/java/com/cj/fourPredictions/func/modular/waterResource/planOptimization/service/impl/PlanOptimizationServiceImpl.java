package com.cj.fourPredictions.func.modular.waterResource.planOptimization.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.planOptimization.service.PlanOptimizationService;
import com.cj.waterresources.api.WaterResourceApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PlanOptimizationServiceImpl implements PlanOptimizationService {

    @Resource
    private WaterResourceApi waterResourceApi;

    @Override
    public RestResponse getWaterResourceAllocationList(Integer bucketType,String inflowDataName) {
        String data = waterResourceApi.getWaterResourceAllocationList(bucketType,inflowDataName);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse contrast(String idA, String idB) {
        String data = waterResourceApi.contrast(idA, idB);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }
}
