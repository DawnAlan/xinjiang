package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.controller;




import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req.TenDayWaterUsePlanImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req.TenDayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.TenDayWaterUsePlanService;
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
 * 旬用水计划(TenDayWaterUsePlan)表控制层
 *
 * @author makejava
 * @since 2023-12-01 19:41:07
 */
@Api(tags = "旬用水计划模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("tenDayWaterUsePlan")
public class TenDayWaterUsePlanController {
    /**
     * 服务对象
     */
    @Autowired
    private TenDayWaterUsePlanService tenDayWaterUsePlanService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("删除")
    @PostMapping("/delete")
    public RestResponse delete(TenDayWaterUsePlanImportParamReq req) {
        return tenDayWaterUsePlanService.delete(req);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestParam(value = "area",required = true) String area,
                            @RequestParam(value = "year",required = true) Integer year,
                            @RequestParam(value = "month",required = true) Integer month,
                            @RequestParam(value = "useWaterUser",required = true) String useWaterUser,
                            @RequestParam(value = "tenDays",required = true) String tenDays,
                            @RequestParam(value = "file",required = true) MultipartFile file) {
        TenDayWaterUsePlanImportParamReq req = new TenDayWaterUsePlanImportParamReq();
        req.setTenDays(tenDays);
        req.setYear(year);
        req.setArea(area);
        req.setMonth(month);
        req.setUseWaterUser(useWaterUser);
        return tenDayWaterUsePlanService.add(req,file);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<List<TenDayWaterUsePlan>> select(@RequestBody TenDayWaterUsePlanSelectReq req) {
        return tenDayWaterUsePlanService.selectList(req);
    }
}

