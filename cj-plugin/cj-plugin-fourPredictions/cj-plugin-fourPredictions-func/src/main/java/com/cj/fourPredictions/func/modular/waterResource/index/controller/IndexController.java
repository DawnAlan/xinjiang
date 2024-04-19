package com.cj.fourPredictions.func.modular.waterResource.index.controller;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.index.service.IndexService;
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

@Api(tags = "供水保障-首页")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("index")
public class IndexController {
    @Autowired
    private IndexService indexService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("水库实时水情")
    @GetMapping("/getRealTimeWaterSituationOfTheReservoir")
    public RestResponse getRealTimeWaterSituationOfTheReservoir(@RequestParam(value = "reservoir",required = true)String reservoir,
                                                                @RequestParam(value = "time",required = true)String time) {
        return indexService.getRealTimeWaterSituationOfTheReservoir(reservoir,time);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("实时水位")
    @GetMapping("/getRealTimeWaterLevel")
    public RestResponse getRealTimeWaterLevel(@RequestParam(value = "station",required = true)String station,
                                              @RequestParam(value = "time",required = true)String time) {
        return indexService.getRealTimeWaterLevel(station,time);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("今日调水指令")
    @GetMapping("/getTodayWaterDiversionInstruction")
    public RestResponse getTodayWaterDiversionInstruction(@RequestParam(value = "time",required = true)String time) {
        return indexService.getTodayWaterDiversionInstruction(time);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("供水统计")
    @GetMapping("/getWaterSupplyStatistics")
    public RestResponse getWaterSupplyStatistics(@RequestParam(value = "time",required = true)String time) {
        return indexService.getWaterSupplyStatistics(time);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("水费统计")
    @GetMapping("/getWaterFeeStatistics")
    public RestResponse getWaterFeeStatistics(@RequestParam(value = "time",required = true)String time) {
        return indexService.getWaterFeeStatistics(time);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("今日巡查统计")
    @GetMapping("/getTodayInspectionStatistics")
    public RestResponse getTodayInspectionStatistics(@RequestParam(value = "time",required = true)String time) {
        return indexService.getTodayInspectionStatistics(time);
    }
}
