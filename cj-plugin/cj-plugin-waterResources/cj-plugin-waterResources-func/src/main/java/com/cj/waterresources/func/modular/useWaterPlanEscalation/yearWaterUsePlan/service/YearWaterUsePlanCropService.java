package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;

import java.util.List;

/**
 * 作物年用水计划(YearWaterUsePlanCrop)表服务接口
 *
 * @author makejava
 * @since 2023-12-01 18:26:28
 */
public interface YearWaterUsePlanCropService extends IService<YearWaterUsePlanCrop> {

    RestResponse<List<YearWaterUsePlanCrop>> selectList(YearCropSelectListReq req);

    RestResponse update(YearWaterUsePlanCrop yearWaterUsePlanCrop);

    RestResponse add(YearWaterUsePlanCrop yearWaterUsePlanCrop);

    RestResponse delete(String id);

}

