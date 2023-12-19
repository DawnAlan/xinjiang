package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.TrunkCanalSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;

import java.util.List;

/**
 * 干渠年用水计划(YearWaterUsePlanTrunkCanal)表服务接口
 *
 * @author makejava
 * @since 2023-12-01 18:26:47
 */
public interface YearWaterUsePlanTrunkCanalService extends IService<YearWaterUsePlanTrunkCanal> {

    RestResponse<List<YearWaterUsePlanTrunkCanal>> selectList(TrunkCanalSelectListReq req);

    RestResponse updateTrunkCanal(YearWaterUsePlanTrunkCanal yearWaterUsePlanTrunkCanal);

    RestResponse addTrunkCanal(YearWaterUsePlanTrunkCanal yearWaterUsePlanTrunkCanal);
}

