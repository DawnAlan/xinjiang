package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlanComparedToActualByMonthVo {

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "月份")
    private Integer month;

    @ApiModelProperty(value = "区域")
    private String area;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "value")
    private Double v;

    @ApiModelProperty(value = "A3单位id")
    private String unitId;

    @ApiModelProperty(value = "对比A3绑定id")
    private String bindId;

}
