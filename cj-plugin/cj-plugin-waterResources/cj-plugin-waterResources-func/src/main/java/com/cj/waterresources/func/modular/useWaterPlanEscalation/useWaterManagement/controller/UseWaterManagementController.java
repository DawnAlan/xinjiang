package com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.controller;


import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementAddReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementQueryReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.res.UseWaterManagementQueryRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.service.UseWaterManagementService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用水单位管理(UseWaterManagement)表控制层
 *
 * @author makejava
 * @since 2023-11-28 17:14:40
 */
@Api(tags = "用水单位管理模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("useWaterManagement")
public class UseWaterManagementController {

    @Autowired
    private UseWaterManagementService useWaterManagementService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id,@RequestParam("useWaterPlan") String useWaterPlan) {
        return useWaterManagementService.delete(id,useWaterPlan);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("新增")
    @PostMapping("/insert")
    public RestResponse insert(@RequestBody UseWaterManagementAddReq req) {
        return useWaterManagementService.insert(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<List<UseWaterManagementQueryRes>> select(@RequestBody UseWaterManagementQueryReq req) {
        return useWaterManagementService.select(req);
    }

}
