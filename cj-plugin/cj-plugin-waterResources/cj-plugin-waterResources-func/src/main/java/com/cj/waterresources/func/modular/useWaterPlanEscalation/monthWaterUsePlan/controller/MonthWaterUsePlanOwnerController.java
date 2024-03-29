package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.controller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthWaterUsePlanSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanOwnerService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (MonthWaterUsePlanOwner)表控制层
 *
 * @author makejava
 * @since 2024-03-23 17:57:24
 */
@Api(tags = "月用水计划管理模块(供水科)")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("monthWaterUsePlanOwner")
public class MonthWaterUsePlanOwnerController{
    /**
     * 服务对象
     */
    @Resource
    private MonthWaterUsePlanOwnerService monthWaterUsePlanOwnerService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("月用水计划管理模块(供水科)查询列表")
    @CommonLog(value = "月用水计划管理模块(供水科)查询列表")
    @PostMapping("/select")
    public RestResponse<List<MonthWaterUsePlanOwner>> select(@RequestBody MonthWaterUsePlanSelectListReq req) {
        return monthWaterUsePlanOwnerService.selectList(req);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("月用水计划管理模块(供水科)新增")
    @CommonLog(value = "月用水计划管理模块(供水科)新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody MonthWaterUsePlanOwner monthWaterUsePlanOwner) {
        return monthWaterUsePlanOwnerService.add(monthWaterUsePlanOwner);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("月用水计划管理模块(供水科)删除")
    @CommonLog(value = "月用水计划管理模块(供水科)删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        return monthWaterUsePlanOwnerService.delete(id);
    }

}

