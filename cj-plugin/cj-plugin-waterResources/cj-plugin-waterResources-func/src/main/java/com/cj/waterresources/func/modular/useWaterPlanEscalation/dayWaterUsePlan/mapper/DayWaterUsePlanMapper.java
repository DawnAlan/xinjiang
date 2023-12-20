package com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.model.func.modular.watertransfer.entity.Waterdemand;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.bean.req.DayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.entity.DayWaterUsePlan;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 日用水计划(DayWaterUsePlan)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-07 17:27:08
 */
public interface DayWaterUsePlanMapper extends BaseMapper<DayWaterUsePlan> {

    DayWaterUsePlan selectOne(@Param("req") DayWaterUsePlanSelectReq req);

    List getWaterNeedDetail(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}

