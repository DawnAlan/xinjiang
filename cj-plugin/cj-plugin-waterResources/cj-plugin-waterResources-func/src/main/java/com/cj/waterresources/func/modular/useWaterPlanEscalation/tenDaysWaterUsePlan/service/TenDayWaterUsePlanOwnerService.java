package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req.TenDayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlanOwner;

import java.util.List;

/**
 * (TenDayWaterUsePlanOwner)表服务接口
 *
 * @author makejava
 * @since 2024-03-25 16:55:15
 */
public interface TenDayWaterUsePlanOwnerService extends IService<TenDayWaterUsePlanOwner> {
    RestResponse<List<TenDayWaterUsePlanOwner>> selectList(TenDayWaterUsePlanSelectReq req);

}

