package com.cj.fourPredictions.func.modular.waterResource.preparationRehearsal.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.preparationRehearsal.service.PreparationRehearsalService;
import com.cj.waterresources.api.WaterResourceApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PreparationRehearsalServiceImpl implements PreparationRehearsalService {

    @Resource
    private WaterResourceApi waterResourceApi;
    @Override
    public RestResponse getWaterResourceAllocationList(Integer waterDistributionType) {
        String data = waterResourceApi.getWaterResourceAllocationList(waterDistributionType);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getWaterResourceAllocationDetails(String id) {
        String data = waterResourceApi.getWaterResourceAllocationDetails(id);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }
}
