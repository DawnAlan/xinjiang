package com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementAddReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementBindIdReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementQueryReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.res.UseWaterManagementQueryRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.entity.UseWaterManagement;

import java.util.List;

/**
 * 用水单位管理(UseWaterManagement)表服务接口
 *
 * @author makejava
 * @since 2023-11-28 17:14:41
 */
public interface UseWaterManagementService extends IService<UseWaterManagement> {

    RestResponse insert(UseWaterManagementAddReq req);

    RestResponse delete(String id,String useWaterPlan);

    RestResponse<List<UseWaterManagementQueryRes>> select(UseWaterManagementQueryReq req);

    RestResponse bindId(UseWaterManagementBindIdReq req);
}

