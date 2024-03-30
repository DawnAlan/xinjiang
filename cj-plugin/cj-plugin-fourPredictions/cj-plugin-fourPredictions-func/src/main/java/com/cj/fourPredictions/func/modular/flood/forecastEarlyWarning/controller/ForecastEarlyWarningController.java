package com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.controller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.res.IncomingWaterForecast;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.res.IncomingWaterForecastDetailsRes;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.service.ForecastEarlyWarningService;
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

import java.util.List;

@Api(tags = "防洪兴利-预报预警")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("forecastEarlyWarning")
public class ForecastEarlyWarningController {

    @Autowired
    private ForecastEarlyWarningService forecastEarlyWarningService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("防洪兴利-预报预警查询方案列表")
    @CommonLog(value = "防洪兴利-预报预警查询方案列表")
    @GetMapping("/getList")
    public RestResponse<List<IncomingWaterForecast>> getList() {
        return forecastEarlyWarningService.getProgrammeList();
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("防洪兴利-预报预警查询方案详情")
    @CommonLog(value = "防洪兴利-预报预警查询方案详情")
    @GetMapping("/getDetails")
    public RestResponse getDetails(@RequestParam(value = "id")String id) {
        return forecastEarlyWarningService.getDetails(id);
    }
}
