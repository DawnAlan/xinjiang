package com.cj.fourPredictions.func.modular.waterResource.supplyDemandBalance.controller;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.waterResource.supplyDemandBalance.service.SupplyDemandBalanceService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "供水保障-供需平衡")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("supplyDemandBalance")
public class SupplyDemandBalanceController {
    
    @Autowired
    private SupplyDemandBalanceService supplyDemandBalanceService;

  /*  @ApiOperationSupport(order = 1)
    @ApiOperation("查询水量平衡")
    @GetMapping("/getSupplyDemandBalance")
    public RestResponse getSupplyDemandBalance() {
        return supplyDemandBalanceService.getSupplyDemandBalance();
    }*/
}
