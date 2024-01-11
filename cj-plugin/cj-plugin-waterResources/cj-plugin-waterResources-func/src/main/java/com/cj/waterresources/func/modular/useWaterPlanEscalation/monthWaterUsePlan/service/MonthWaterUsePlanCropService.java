package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanCrop;

import java.util.List;

/**
 * 月用水计划作物表(MonthWaterUsePlanCrop)表服务接口
 *
 * @author makejava
 * @since 2024-01-04 18:10:46
 */
public interface MonthWaterUsePlanCropService extends IService<MonthWaterUsePlanCrop> {

    RestResponse<List<MonthWaterUsePlanCrop>> selectList(MonthCropSelectListReq req);

    RestResponse update(MonthWaterUsePlanCrop yearWaterUsePlanCrop);

    RestResponse add(MonthWaterUsePlanCrop yearWaterUsePlanCrop);

    RestResponse delete(String id);

}

