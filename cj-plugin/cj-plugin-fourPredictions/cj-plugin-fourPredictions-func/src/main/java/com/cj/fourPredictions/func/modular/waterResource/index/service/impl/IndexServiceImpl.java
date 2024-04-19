package com.cj.fourPredictions.func.modular.waterResource.index.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.approval.api.ApprovalApi;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.index.service.IndexService;
import com.cj.waterresources.api.WaterResourceApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IndexServiceImpl implements IndexService {

    @Resource
    private WaterResourceApi waterResourceApi;

    @Resource
    private ApprovalApi approvalApi;
    @Override
    public RestResponse getTodayWaterDiversionInstruction(String time) {
        String data = approvalApi.getTotalCount(time);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getRealTimeWaterSituationOfTheReservoir(String reservoir,String time) {
        String data = waterResourceApi.getRealTimeWaterSituationOfTheReservoir(reservoir,time);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getRealTimeWaterLevel(String station,String time) {
        String data = waterResourceApi.getRealTimeWaterLevel(station,time);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getWaterSupplyStatistics(String time) {
        String data = waterResourceApi.getWaterSupplyStatistics(time);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getWaterFeeStatistics(String time) {
        String data = waterResourceApi.getWaterFeeStatistics(time);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getTodayInspectionStatistics(String time) {
        String data = waterResourceApi.getTodayInspectionStatistics(time);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }
}
