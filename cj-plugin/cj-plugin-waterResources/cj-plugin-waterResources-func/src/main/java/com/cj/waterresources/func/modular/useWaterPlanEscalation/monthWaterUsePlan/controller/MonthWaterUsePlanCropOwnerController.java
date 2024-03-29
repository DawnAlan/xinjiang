package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.controller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanCropOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanCropOwnerService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (MonthWaterUsePlanCropOwner)表控制层
 *
 * @author makejava
 * @since 2024-03-23 17:56:59
 */
@Api(tags = "月用水计划作物表模块(供水科)")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("monthWaterUsePlanCropOwner")
public class MonthWaterUsePlanCropOwnerController{
    /**
     * 服务对象
     */
    @Resource
    private MonthWaterUsePlanCropOwnerService monthWaterUsePlanCropOwnerService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("月用水计划作物表模块(供水科)查询列表")
    @CommonLog(value = "月用水计划作物表模块(供水科)查询列表")
    @PostMapping("/select")
    public RestResponse<List<MonthWaterUsePlanCropOwner>> select(@RequestBody MonthCropSelectListReq req) {
        return monthWaterUsePlanCropOwnerService.selectList(req);
    }

}

