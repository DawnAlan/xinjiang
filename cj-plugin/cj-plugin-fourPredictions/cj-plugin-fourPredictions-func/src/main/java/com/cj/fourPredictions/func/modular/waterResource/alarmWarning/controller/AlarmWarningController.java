package com.cj.fourPredictions.func.modular.waterResource.alarmWarning.controller;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.alarmWarning.service.AlarmWarningService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "供水保障-告警预警")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("alarmWarning")
public class AlarmWarningController {

    @Autowired
    private AlarmWarningService warningService;
/*
    @ApiOperationSupport(order = 1)
    @ApiOperation("水库水情告警")
    @GetMapping("/getReservoirWaterConditionAlarm")
    public RestResponse getReservoirWaterConditionAlarm(@RequestParam(value = "reservoir")String reservoir,
                                 @RequestParam(value = "time")String time) {
        return warningService.getReservoirWaterConditionAlarm(reservoir,time);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("浊度告警")
    @GetMapping("/getTurbidityAlarm")
    public RestResponse getTurbidityAlarm(@RequestParam(value = "time")String time) {
        return warningService.getTurbidityAlarm(time);
    }*/
}
