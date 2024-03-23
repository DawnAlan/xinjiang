package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanCropOwner;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * (MonthWaterUsePlanCropOwner)表服务接口
 *
 * @author makejava
 * @since 2024-03-23 17:57:00
 */
public interface MonthWaterUsePlanCropOwnerService extends IService<MonthWaterUsePlanCropOwner> {

    RestResponse<List<MonthWaterUsePlanCropOwner>> selectList(MonthCropSelectListReq req);

    RestResponse add(List<MonthWaterUsePlanCropOwner> monthWaterUsePlanCropOwnerList);

    RestResponse delete(MonthCropImportParamReq req);

}

