package com.cj.fourPredictions.func.modular.waterResource.alarmWarning.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.alarmWarning.service.AlarmWarningService;
import com.cj.waterresources.api.WaterResourceApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AlarmWarningServiceImpl implements AlarmWarningService {

    @Resource
    private WaterResourceApi waterResourceApi;
    @Override
    public RestResponse getReservoirWaterConditionAlarm(String reservoir, String time) {
        String data = waterResourceApi.getReservoirWaterConditionAlarm(reservoir, time);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse getTurbidityAlarm(String time) {
        String data = waterResourceApi.getTurbidityAlarm(time);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }
}
