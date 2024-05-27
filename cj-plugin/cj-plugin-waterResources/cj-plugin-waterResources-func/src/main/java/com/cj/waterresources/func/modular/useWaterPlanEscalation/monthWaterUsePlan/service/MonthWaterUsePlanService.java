package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthWaterUsePlanSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.res.PlanComparedToActualByMonthRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;

import java.util.List;

/**
 * 月用水计划(MonthWaterUsePlan)表服务接口
 *
 * @author makejava
 * @since 2023-12-07 16:48:27
 */
public interface MonthWaterUsePlanService extends IService<MonthWaterUsePlan> {

    RestResponse add(MonthWaterUsePlan monthWaterUsePlan);

    RestResponse delete(String id);

    RestResponse update(MonthWaterUsePlan monthWaterUsePlan);

    RestResponse<List<MonthWaterUsePlan>> selectList(MonthWaterUsePlanSelectListReq req);

    RestResponse<List<PlanComparedToActualByMonthRes>> planComparedToActual(String plan, String actual, String tenDays);
}

