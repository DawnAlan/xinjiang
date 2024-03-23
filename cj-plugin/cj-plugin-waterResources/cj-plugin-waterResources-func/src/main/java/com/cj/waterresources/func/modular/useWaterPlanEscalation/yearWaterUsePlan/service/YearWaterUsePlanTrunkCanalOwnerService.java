package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.TrunkCanalSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanalOwner;

import java.util.List;

/**
 * (YearWaterUsePlanTrunkCanalOwner)表服务接口
 *
 * @author makejava
 * @since 2024-03-22 19:40:53
 */
public interface YearWaterUsePlanTrunkCanalOwnerService extends IService<YearWaterUsePlanTrunkCanalOwner> {

    RestResponse<List<YearWaterUsePlanTrunkCanalOwner>> selectList(TrunkCanalSelectListReq req);

    RestResponse addTrunkCanal(YearWaterUsePlanTrunkCanalOwner yearWaterUsePlanTrunkCanalOwner);

}

