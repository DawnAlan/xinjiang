package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 作物年用水计划(YearWaterUsePlanCrop)表控制层
 *
 * @author makejava
 * @since 2023-12-01 18:26:27
 */
@Api(tags = "年用水计划模块-作物")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("yearWaterUsePlanCrop")
public class YearWaterUsePlanCropController {

    @Autowired
    private YearWaterUsePlanCropService yearWaterUsePlanCropService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("删除")
    @PostMapping("/delete")
    public RestResponse delete(@RequestBody YearCropImportParamReq req) {
        return yearWaterUsePlanCropService.delete(req);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestParam(value = "area",required = true) String area,
                            @RequestParam(value = "unit",required = true) String unit,
                            @RequestParam(value = "unitId",required = true) String unitId,
                            @RequestParam(value = "year",required = true) Integer year,
                            @RequestParam(value = "file",required = true) MultipartFile file) {
        YearCropImportParamReq req = new YearCropImportParamReq();
        req.setYear(year);
        req.setArea(area);
        req.setUnit(unit);
        req.setUnitId(unitId);
        return yearWaterUsePlanCropService.add(req, file);
    }


    @ApiOperationSupport(order = 3)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<List<YearWaterUsePlanCrop>> select(@RequestBody YearCropSelectListReq req) {
        return yearWaterUsePlanCropService.selectList(req);
    }

}

