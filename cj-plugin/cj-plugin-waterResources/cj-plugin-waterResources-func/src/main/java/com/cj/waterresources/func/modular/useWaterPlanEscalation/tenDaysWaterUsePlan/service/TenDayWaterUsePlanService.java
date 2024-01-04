package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req.TenDayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlan;

import java.util.List;

/**
 * 旬用水计划(TenDayWaterUsePlan)表服务接口
 *
 * @author makejava
 * @since 2023-12-01 19:41:08
 */
public interface TenDayWaterUsePlanService extends IService<TenDayWaterUsePlan> {

    RestResponse<List<TenDayWaterUsePlan>> selectList(TenDayWaterUsePlanSelectReq req);

    RestResponse add(TenDayWaterUsePlan tenDayWaterUsePlan);

    RestResponse update(TenDayWaterUsePlan tenDayWaterUsePlan);

    RestResponse delete(String id);

}

