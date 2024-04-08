package com.cj.fourPredictions.func.modular.waterResource.preparationRehearsal.controller;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.preparationRehearsal.service.PreparationRehearsalService;
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

@Api(tags = "供水保障-调配预演")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("preparationRehearsal")
public class PreparationRehearsalController {

    @Autowired
    private PreparationRehearsalService preparationRehearsalService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("根据模型类型查询模型列表")
    @GetMapping("/getWaterResourceAllocationList")
    public RestResponse getWaterResourceAllocationList(@RequestParam(value = "waterDistributionType")Integer waterDistributionType,
                                                       @RequestParam(value = "inflowDataName")String inflowDataName) {
        return preparationRehearsalService.getWaterResourceAllocationList(waterDistributionType,inflowDataName);
    }


    @ApiOperationSupport(order = 2)
    @ApiOperation("根据模型id查询模型四预详情")
    @GetMapping("/getWaterResourceAllocationDetails")
    public RestResponse getWaterResourceAllocationDetails(@RequestParam(value = "id")String id) {
        return preparationRehearsalService.getWaterResourceAllocationDetails(id);
    }
}
