package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req.TenDayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlanOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.TenDayWaterUsePlanOwnerService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (TenDayWaterUsePlanOwner)表控制层
 *
 * @author makejava
 * @since 2024-03-25 16:55:14
 */
@Api(tags = "旬用水计划模块(供水科专看)")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("tenDayWaterUsePlanOwner")
public class TenDayWaterUsePlanOwnerController{

    @Autowired
    private TenDayWaterUsePlanOwnerService tenDayWaterUsePlanOwnerService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<List<TenDayWaterUsePlanOwner>> select(@RequestBody TenDayWaterUsePlanSelectReq req) {
        return tenDayWaterUsePlanOwnerService.selectList(req);
    }

}

