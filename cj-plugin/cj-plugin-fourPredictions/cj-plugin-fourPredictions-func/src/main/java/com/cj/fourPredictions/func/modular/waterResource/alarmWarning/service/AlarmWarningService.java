package com.cj.fourPredictions.func.modular.waterResource.alarmWarning.service;

import com.cj.common.model.RestResponse;

public interface AlarmWarningService {

    RestResponse getReservoirWaterConditionAlarm(String reservoir,String time);

    RestResponse getTurbidityAlarm(String time);
}
