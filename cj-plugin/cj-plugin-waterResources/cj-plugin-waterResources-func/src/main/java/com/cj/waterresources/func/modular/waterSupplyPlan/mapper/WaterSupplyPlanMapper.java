package com.cj.waterresources.func.modular.waterSupplyPlan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.waterresources.func.modular.waterSupplyPlan.bean.req.WaterSupplyPlanSelectListReq;
import com.cj.waterresources.func.modular.waterSupplyPlan.bean.res.WaterSupplyPlanSelectListRes;
import com.cj.waterresources.func.modular.waterSupplyPlan.entity.WaterSupplyPlan;
import org.apache.ibatis.annotations.Param;

/**
 * 供水计划管理(WaterSupplyPlan)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-21 09:51:23
 */
public interface WaterSupplyPlanMapper extends BaseMapper<WaterSupplyPlan> {

    IPage<WaterSupplyPlanSelectListRes> getWaterSupplyPlanList(@Param(value = "req") WaterSupplyPlanSelectListReq req,IPage<WaterSupplyPlanSelectListRes> page);

}

