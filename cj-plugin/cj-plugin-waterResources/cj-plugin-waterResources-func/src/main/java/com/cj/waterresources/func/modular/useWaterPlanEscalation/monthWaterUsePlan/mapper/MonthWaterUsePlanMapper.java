package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.vo.PlanComparedToActualByMonthVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 月用水计划(MonthWaterUsePlan)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-07 16:48:27
 */
public interface MonthWaterUsePlanMapper extends BaseMapper<MonthWaterUsePlan> {


    List<PlanComparedToActualByMonthVo> planComparedToActual(@Param("year") Integer year, @Param("month") Integer month, @Param("tenDays") String tenDays);
}

