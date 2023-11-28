package com.cj.waterresources.func.modular.waterSupplyPlan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSupplyPlan.bean.req.WaterSupplyPlanSelectListReq;
import com.cj.waterresources.func.modular.waterSupplyPlan.bean.req.WaterSupplyPlanUpdateReq;
import com.cj.waterresources.func.modular.waterSupplyPlan.bean.res.WaterSupplyPlanSelectListRes;
import com.cj.waterresources.func.modular.waterSupplyPlan.entity.WaterSupplyPlan;

/**
 * 供水计划管理(WaterSupplyPlan)表服务接口
 *
 * @author makejava
 * @since 2023-11-21 09:51:24
 */
public interface WaterSupplyPlanService extends IService<WaterSupplyPlan> {

    RestResponse<IPage<WaterSupplyPlanSelectListRes>> getWaterSupplyPlanList(WaterSupplyPlanSelectListReq req);

    RestResponse addWaterSupplyPlan(WaterSupplyPlan waterSupplyPlan);

    RestResponse deleteWaterSupplyPlan(String id);

    RestResponse updateWaterSupplyPlan(WaterSupplyPlanUpdateReq req);

}

