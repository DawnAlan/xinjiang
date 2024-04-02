package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthWaterUsePlanSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 月用水计划(MonthWaterUsePlan)表控制层
 *
 * @author makejava
 * @since 2023-12-07 16:48:26
 */
@Api(tags = "月用水计划管理模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("monthWaterUsePlan")
public class MonthWaterUsePlanController {

    @Autowired
    private MonthWaterUsePlanService monthWaterUsePlanService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("月用水计划管理模块删除")
    @CommonLog(value = "月用水计划管理模块删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        return monthWaterUsePlanService.delete(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("月用水计划管理模块新增")
    @CommonLog(value = "月用水计划管理模块新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody MonthWaterUsePlan monthWaterUsePlan) {
        return monthWaterUsePlanService.add(monthWaterUsePlan);
    }
    @ApiOperationSupport(order = 3)
    @ApiOperation("月用水计划管理模块修改")
    @CommonLog(value = "月用水计划管理模块修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody MonthWaterUsePlan monthWaterUsePlan) {
        return monthWaterUsePlanService.update(monthWaterUsePlan);
    }


    @ApiOperationSupport(order = 4)
    @ApiOperation("月用水计划管理模块查询列表")
    @CommonLog(value = "月用水计划管理模块查询列表")
    @PostMapping("/select")
    public RestResponse<List<MonthWaterUsePlan>> select(@RequestBody MonthWaterUsePlanSelectListReq req) {
        return monthWaterUsePlanService.selectList(req);
    }

}

