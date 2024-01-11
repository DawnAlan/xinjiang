package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanCropService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 月用水计划作物表(MonthWaterUsePlanCrop)表控制层
 *
 * @author makejava
 * @since 2024-01-04 18:10:41
 */
@Api(tags = "月用水计划作物表模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("monthWaterUsePlanCrop")
public class MonthWaterUsePlanCropController {

    @Autowired
    private MonthWaterUsePlanCropService monthWaterUsePlanCropService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        return monthWaterUsePlanCropService.delete(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody MonthWaterUsePlanCrop monthWaterUsePlanCrop) {
        return monthWaterUsePlanCropService.add(monthWaterUsePlanCrop);
    }
    @ApiOperationSupport(order = 3)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody MonthWaterUsePlanCrop monthWaterUsePlanCrop) {
        return monthWaterUsePlanCropService.update(monthWaterUsePlanCrop);
    }


    @ApiOperationSupport(order = 4)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<List<MonthWaterUsePlanCrop>> select(@RequestBody MonthCropSelectListReq req) {
        return monthWaterUsePlanCropService.selectList(req);
    }
}

