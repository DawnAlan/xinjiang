package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlanComparedToActualByYearRes {

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "所选年计划值")
    private Double planValue;

    @ApiModelProperty(value = "所选年实际值")
    private Double actualValue;

    @ApiModelProperty(value = "所选年计划与实际差值")
    private Double planSubtractActualValue;

    @ApiModelProperty(value = "对比年同期")
    private Double actualValueForContrastYear;

    @ApiModelProperty(value = "同期差值")//所选年实际值-对比年同期
    private Double planSubtractActualValueForContrastYear;

    @ApiModelProperty(value = "同期比")//同期差值/对比年同期
    private Double contrast;
}
