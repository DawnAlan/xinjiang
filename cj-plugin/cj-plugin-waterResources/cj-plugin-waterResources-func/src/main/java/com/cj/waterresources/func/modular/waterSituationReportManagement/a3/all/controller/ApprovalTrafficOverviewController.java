package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.controller;


import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.ApprovalTrafficOverviewService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 审批管理流量概览表(ApprovalTrafficOverview)表控制层
 *
 * @author makejava
 * @since 2024-04-09 16:35:43
 */
@RestController
@RequestMapping("approvalTrafficOverview")
public class ApprovalTrafficOverviewController{
    /**
     * 服务对象
     */
    @Resource
    private ApprovalTrafficOverviewService approvalTrafficOverviewService;


}

