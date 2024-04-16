package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.controller;


import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.ApprovalTrafficOverviewTableAddReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity.ApprovalTrafficOverview;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.ApprovalTrafficOverviewService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 审批管理流量概览表(ApprovalTrafficOverview)表控制层
 *
 * @author makejava
 * @since 2024-04-09 16:35:43
 */
@Api(tags = "审批管理流量概览表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("approvalTrafficOverview")
public class ApprovalTrafficOverviewController{
    /**
     * 服务对象
     */
    @Resource
    private ApprovalTrafficOverviewService approvalTrafficOverviewService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("流量概览表查询详情列表")
    @CommonLog(value = "流量概览表查询详情列表")
    @GetMapping("/selectListById")
    public RestResponse selectListById(@RequestParam(value = "id", required = false) String id) {
        return approvalTrafficOverviewService.selectListById(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("审批管理流量概览表修改")
    @CommonLog(value = "审批管理流量概览表修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody ApprovalTrafficOverview approvalTrafficOverview) {
        return approvalTrafficOverviewService.update(approvalTrafficOverview);
    }

}

