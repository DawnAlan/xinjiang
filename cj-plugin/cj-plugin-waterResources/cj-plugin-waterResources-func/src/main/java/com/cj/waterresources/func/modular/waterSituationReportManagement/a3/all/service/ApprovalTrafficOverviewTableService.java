package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.ApprovalTrafficOverviewTableAddReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity.ApprovalTrafficOverviewTable;

import java.util.Date;

/**
 * 流量概览表(ApprovalTrafficOverviewTable)表服务接口
 *
 * @author makejava
 * @since 2024-04-09 16:34:48
 */
public interface ApprovalTrafficOverviewTableService extends IService<ApprovalTrafficOverviewTable> {

    RestResponse add(ApprovalTrafficOverviewTableAddReq req);

    RestResponse delete(String id);

    RestResponse selectList(String time,String name);

    RestResponse synchronizationEightData(String id);

}

