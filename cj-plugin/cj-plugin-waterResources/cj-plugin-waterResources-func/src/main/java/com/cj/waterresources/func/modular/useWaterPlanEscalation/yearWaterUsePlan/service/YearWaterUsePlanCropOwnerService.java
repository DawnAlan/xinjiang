package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCropOwner;

import java.util.List;

/**
 * (YearWaterUsePlanCropOwner)表服务接口
 *
 * @author makejava
 * @since 2024-03-22 19:35:40
 */
public interface YearWaterUsePlanCropOwnerService extends IService<YearWaterUsePlanCropOwner> {

    RestResponse<List<YearWaterUsePlanCropOwner>> selectList(YearCropSelectListReq req);

    RestResponse addList(List<YearWaterUsePlanCropOwner> yearWaterUsePlanCropOwnerList);

    RestResponse delete(YearCropImportParamReq req);

}

