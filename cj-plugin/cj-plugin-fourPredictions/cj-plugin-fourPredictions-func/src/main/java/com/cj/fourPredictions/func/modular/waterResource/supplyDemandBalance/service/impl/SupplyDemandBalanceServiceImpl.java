package com.cj.fourPredictions.func.modular.waterResource.supplyDemandBalance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.supplyDemandBalance.service.SupplyDemandBalanceService;
import com.cj.waterresources.api.WaterResourceApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SupplyDemandBalanceServiceImpl implements SupplyDemandBalanceService {

    @Resource
    private WaterResourceApi waterResourceApi;
    @Override
    public RestResponse getSupplyDemandBalance() {
        String data = waterResourceApi.getSupplyDemandBalance();
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getFormList() {
        String data = waterResourceApi.getFormList();
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getSupplyDemandBalanceByFormId(String id) {
        String data = waterResourceApi.getSupplyDemandBalanceByFormId(id);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }
}
