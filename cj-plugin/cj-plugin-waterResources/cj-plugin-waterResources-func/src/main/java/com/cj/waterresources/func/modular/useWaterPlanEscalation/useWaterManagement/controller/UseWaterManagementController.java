package com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.controller;


import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementAddReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementBindIdReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementQueryReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.res.UseWaterManagementQueryRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.entity.UseWaterManagement;
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
    @ApiOperation("用水单位管理模块删除")
    @CommonLog(value = "用水单位管理模块删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id,@RequestParam("useWaterPlan") String useWaterPlan) {
        return useWaterManagementService.delete(id,useWaterPlan);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("用水单位管理模块新增")
    @CommonLog(value = "用水单位管理模块新增")
    @PostMapping("/insert")
    public RestResponse insert(@RequestBody UseWaterManagementAddReq req) {
        return useWaterManagementService.insert(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("用水单位管理模块查询列表")
    @CommonLog(value = "用水单位管理模块查询列表")
    @PostMapping("/select")
    public RestResponse<List<UseWaterManagementQueryRes>> select(@RequestBody UseWaterManagementQueryReq req) {
        return useWaterManagementService.select(req);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("用水单位管理模块绑定id")
    @CommonLog(value = "用水单位管理模块绑定id")
    @PostMapping("/bindId")
    public RestResponse bindId(@RequestBody UseWaterManagementBindIdReq req) {
        return useWaterManagementService.bindId(req);
    }

}
