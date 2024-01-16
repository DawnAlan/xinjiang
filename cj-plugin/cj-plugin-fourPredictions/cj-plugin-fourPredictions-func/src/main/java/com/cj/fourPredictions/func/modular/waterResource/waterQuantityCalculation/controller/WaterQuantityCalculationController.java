package com.cj.fourPredictions.func.modular.waterResource.waterQuantityCalculation.controller;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.waterQuantityCalculation.service.WaterQuantityCalculationService;
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

@Api(tags = "供水保障-水量计算")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterQuantityCalculation")
public class WaterQuantityCalculationController {
    @Autowired
    private WaterQuantityCalculationService waterQuantityCalculationService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("根据模型id查询水资源调配详情")
    @GetMapping("/waterQuantityCalculation")
    public RestResponse waterQuantityCalculation(@RequestParam(value = "id")String id) {
        return waterQuantityCalculationService.waterQuantityCalculation(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("根据水库名称查询水库实时水位信息")
    @GetMapping("/getRealTimeReservoirLevel")
    public RestResponse getRealTimeReservoirLevel(@RequestParam(value = "reservoir")String reservoir) {
        return waterQuantityCalculationService.getRealTimeReservoirLevel(reservoir);
    }
}
