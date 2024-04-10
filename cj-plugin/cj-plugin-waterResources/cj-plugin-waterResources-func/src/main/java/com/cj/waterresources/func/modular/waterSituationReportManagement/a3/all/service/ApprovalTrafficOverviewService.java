package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity.ApprovalTrafficOverview;

/**
 * 审批管理流量概览表(ApprovalTrafficOverview)表服务接口
 *
 * @author makejava
 * @since 2024-04-09 16:35:43
 */
public interface ApprovalTrafficOverviewService extends IService<ApprovalTrafficOverview> {

    RestResponse selectListById(String id);

    RestResponse update(ApprovalTrafficOverview approvalTrafficOverview);


}

