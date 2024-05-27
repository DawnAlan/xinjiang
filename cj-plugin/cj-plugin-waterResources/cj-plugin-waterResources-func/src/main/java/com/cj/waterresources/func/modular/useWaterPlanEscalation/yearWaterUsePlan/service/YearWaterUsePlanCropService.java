package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res.PlanComparedToActualByYearRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res.SelectYearWaterUsePlanCropForSum;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 作物年用水计划(YearWaterUsePlanCrop)表服务接口
 *
 * @author makejava
 * @since 2023-12-01 18:26:28
 */
public interface YearWaterUsePlanCropService extends IService<YearWaterUsePlanCrop> {

    RestResponse<List<YearWaterUsePlanCrop>> selectList(YearCropSelectListReq req);

    RestResponse add(YearCropImportParamReq req, MultipartFile file);

    RestResponse delete(YearCropImportParamReq req);

    SelectYearWaterUsePlanCropForSum selectListForSum(Integer year, String area);

    RestResponse<List<PlanComparedToActualByYearRes>> planComparedToActual(Integer planYear, Integer actualYear, Integer month);

}

