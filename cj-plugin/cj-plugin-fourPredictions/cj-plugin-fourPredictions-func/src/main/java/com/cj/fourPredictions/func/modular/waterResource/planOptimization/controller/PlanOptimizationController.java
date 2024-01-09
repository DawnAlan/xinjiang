package com.cj.fourPredictions.func.modular.waterResource.planOptimization.controller;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.planOptimization.service.PlanOptimizationService;
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

@Api(tags = "供水保障-预案优选")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("planOptimization")
public class PlanOptimizationController {

    @Autowired
    private PlanOptimizationService planOptimizationService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("根据模型类型查询模型列表")
    @GetMapping("/getWaterResourceAllocationList")
    public RestResponse getWaterResourceAllocationList(@RequestParam(value = "waterDistributionType")Integer waterDistributionType) {
        return planOptimizationService.getWaterResourceAllocationList(waterDistributionType);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("方案对比")
    @GetMapping("/contrast")
    public RestResponse contrast(@RequestParam(value = "idA")String idA,
                                 @RequestParam(value = "idB")String idB) {
        return planOptimizationService.contrast(idA,idB);
    }
}
