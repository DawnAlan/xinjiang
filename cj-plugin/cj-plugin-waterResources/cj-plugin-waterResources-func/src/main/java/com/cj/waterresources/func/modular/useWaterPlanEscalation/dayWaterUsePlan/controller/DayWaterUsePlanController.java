package com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectInfoByIrrigationNameListRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.bean.req.DayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.entity.DayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.service.DayWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthWaterUsePlanSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 日用水计划(DayWaterUsePlan)表控制层
 *
 * @author makejava
 * @since 2023-12-07 17:27:07
 */
@Api(tags = "日用水计划模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("dayWaterUsePlan")
public class DayWaterUsePlanController {

    @Autowired
    private DayWaterUsePlanService dayWaterUsePlanService;

    @ApiOperationSupport(order = 2)
    @ApiOperation("日用水计划模块新增")
    @CommonLog(value = "日用水计划模块新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody DayWaterUsePlan dayWaterUsePlan) {
        return dayWaterUsePlanService.add(dayWaterUsePlan);
    }
    @ApiOperationSupport(order = 3)
    @ApiOperation("日用水计划模块修改")
    @CommonLog(value = "日用水计划模块修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody DayWaterUsePlan dayWaterUsePlan) {
        return dayWaterUsePlanService.update(dayWaterUsePlan);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("日用水计划模块查询列表")
    @CommonLog(value = "日用水计划模块查询列表")
    @PostMapping("/select")
    public RestResponse<DayWaterUsePlan> select(@RequestBody DayWaterUsePlanSelectReq req) {
        return dayWaterUsePlanService.select(req);
    }


    @ApiOperationSupport(order = 5)
    @ApiOperation("日用水计划模块查询实时流量")
    @CommonLog(value = "日用水计划模块查询实时流量")
    @GetMapping("/selectValue")
    public RestResponse<List<SelectInfoByIrrigationNameListRes>> selectValue(@RequestParam("names") String names,@RequestParam("station") String station) {
        return dayWaterUsePlanService.selectValue(names,station);
    }

}

