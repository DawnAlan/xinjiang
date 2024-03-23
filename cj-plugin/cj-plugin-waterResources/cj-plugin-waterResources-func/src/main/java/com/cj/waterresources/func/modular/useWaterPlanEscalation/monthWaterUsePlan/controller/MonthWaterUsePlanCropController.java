package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropImportParamReq;
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
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/delete")
    public RestResponse delete(@RequestBody MonthCropImportParamReq req) {
        return monthWaterUsePlanCropService.delete(req);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestParam(value = "area",required = true) String area,
                            @RequestParam(value = "unit",required = true) String unit,
                            @RequestParam(value = "unitId",required = true) String unitId,
                            @RequestParam(value = "year",required = true) Integer year,
                            @RequestParam(value = "month",required = true) Integer month,
                            @RequestParam(value = "file",required = true) MultipartFile file) {
        MonthCropImportParamReq req = new MonthCropImportParamReq();
        req.setYear(year);
        req.setArea(area);
        req.setUnit(unit);
        req.setUnitId(unitId);
        req.setMonth(month);
        return monthWaterUsePlanCropService.add(req,file);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<List<MonthWaterUsePlanCrop>> select(@RequestBody MonthCropSelectListReq req) {
        return monthWaterUsePlanCropService.selectList(req);
    }
}

