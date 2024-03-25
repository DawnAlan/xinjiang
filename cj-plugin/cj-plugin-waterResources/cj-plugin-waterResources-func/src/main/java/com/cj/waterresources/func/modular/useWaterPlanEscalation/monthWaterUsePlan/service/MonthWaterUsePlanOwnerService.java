package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthWaterUsePlanSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanOwner;

import java.util.List;

/**
 * (MonthWaterUsePlanOwner)表服务接口
 *
 * @author makejava
 * @since 2024-03-23 17:57:24
 */
public interface MonthWaterUsePlanOwnerService extends IService<MonthWaterUsePlanOwner> {

    RestResponse<List<MonthWaterUsePlanOwner>> selectList(MonthWaterUsePlanSelectListReq req);

    RestResponse add(MonthWaterUsePlanOwner monthWaterUsePlanOwner);

    RestResponse delete(String id);

}

