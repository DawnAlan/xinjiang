package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res.SelectYearWaterUsePlanCropForSum;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.vo.NeedWaterVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.vo.PlanComparedToActualByYearVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作物年用水计划(YearWaterUsePlanCrop)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-01 18:26:27
 */
public interface YearWaterUsePlanCropMapper extends BaseMapper<YearWaterUsePlanCrop> {

    SelectYearWaterUsePlanCropForSum selectListForSum(@Param("year") Integer year, @Param("area") String area);

    List<PlanComparedToActualByYearVo> planComparedToActual(@Param("year") Integer year, @Param("month") String month);
    NeedWaterVo needWaterByPlan(@Param("year") Integer year);

}

