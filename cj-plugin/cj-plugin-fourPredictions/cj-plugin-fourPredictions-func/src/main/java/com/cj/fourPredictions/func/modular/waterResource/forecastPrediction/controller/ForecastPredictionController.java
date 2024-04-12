package com.cj.fourPredictions.func.modular.waterResource.forecastPrediction.controller;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.forecastPrediction.bean.req.PredictionListByNameReq;
import com.cj.fourPredictions.func.modular.waterResource.forecastPrediction.service.ForecastPredictionService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "供水保障-预报预测")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("forecastPrediction")
public class ForecastPredictionController {

    @Autowired
    private ForecastPredictionService forecastPredictionService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("水库来水预报根据时间尺度查询来水预报列表")
    @GetMapping("/getPredictionListByTimeType")
    public RestResponse getPredictionListByTimeType(@RequestParam(value = "timeType")Integer timeType) {
        return forecastPredictionService.getPredictionListByTimeType(timeType);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("水库来水预报视图")
    @PostMapping("/getPredictionListByName")
    public RestResponse getPredictionListByName(@RequestBody PredictionListByNameReq req) {
        return forecastPredictionService.getPredictionListByName(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("需水计划-年-用水户")
    @GetMapping("/getYearWaterPlan")
    public RestResponse getYearWaterPlan(@RequestParam(value = "area")String area,
                                         @RequestParam(value = "year")Integer year) {
        return forecastPredictionService.getYearWaterPlan(area,year);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("需水计划-年-作物")
    @GetMapping("/getYearWaterPlanCrop")
    public RestResponse getYearWaterPlanCrop(@RequestParam(value = "area")String area,
                                             @RequestParam(value = "unit")String unit,
                                             @RequestParam(value = "year")Integer year) {
        return forecastPredictionService.getYearWaterPlanCrop(area, unit,year);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("需水计划-月-用水户")
    @GetMapping("/getMonthWaterPlan")
    public RestResponse getMonthWaterPlan(@RequestParam(value = "area")String area,
                                          @RequestParam(value = "year")Integer year,
                                          @RequestParam(value = "month")Integer month) {
        return forecastPredictionService.getMonthWaterPlan(area,year,month);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("需水计划-月-作物")
    @GetMapping("/getMonthWaterPlanCrop")
    public RestResponse getMonthWaterPlanCrop(@RequestParam(value = "area")String area,
                                              @RequestParam(value = "unit")String unit,
                                              @RequestParam(value = "year")Integer year,
                                              @RequestParam(value = "month")Integer month) {
        return forecastPredictionService.getMonthWaterPlanCrop(area, unit,year,month);
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("需水计划-旬-用水户")
    @GetMapping("/getTenDaysWaterPlan")
    public RestResponse getTenDaysWaterPlan(@RequestParam(value = "area")String area,
                                            @RequestParam(value = "year")Integer year,
                                            @RequestParam(value = "month")Integer month,
                                            @RequestParam(value = "tenDays")String tenDays) {
        return forecastPredictionService.getTenDaysWaterPlan(area,year,month,tenDays);
    }

    @ApiOperationSupport(order = 8)
    @ApiOperation("需水计划-旬-作物")
    @GetMapping("/getTenDaysWaterPlanCrop")
    public RestResponse getTenDaysWaterPlanCrop(@RequestParam(value = "area")String area,
                                                @RequestParam(value = "unit")String unit,
                                                @RequestParam(value = "year")Integer year,
                                                @RequestParam(value = "month")Integer month,
                                                @RequestParam(value = "tenDays")String tenDays) {
        return forecastPredictionService.getTenDaysWaterPlanCrop(area, unit,year,month,tenDays);
    }

    @ApiOperationSupport(order = 9)
    @ApiOperation("需水计划-日-取水口数据")
    @GetMapping("/getDayWaterPlan")
    public RestResponse getDayWaterPlan(@RequestParam(value = "area")String area,
                                        @RequestParam(value = "year")Integer year,
                                        @RequestParam(value = "month")Integer month,
                                        @RequestParam(value = "day")Integer day) {
        return forecastPredictionService.getDayWaterPlan(area,year,month,day);
    }

    @ApiOperationSupport(order = 10)
    @ApiOperation("获取层级关系用水户")
    @GetMapping("/getUseWaterUser")
    public RestResponse getUseWaterUser(@RequestParam(value = "useWaterPlan")String useWaterPlan,
                                        @RequestParam(value = "area")String area) {
        return forecastPredictionService.getUseWaterUser(useWaterPlan, area);
    }

    @ApiOperationSupport(order = 11)
    @ApiOperation("根据时间尺度获取2个水库的需水值")
    @GetMapping("/getNeedWaterValueList")
    public RestResponse getNeedWaterValueList(@RequestParam(value = "area")String area,
                                              @RequestParam(value = "timeType")Integer timeType) {
        return forecastPredictionService.getNeedWaterValueList(area,timeType);
    }

}
