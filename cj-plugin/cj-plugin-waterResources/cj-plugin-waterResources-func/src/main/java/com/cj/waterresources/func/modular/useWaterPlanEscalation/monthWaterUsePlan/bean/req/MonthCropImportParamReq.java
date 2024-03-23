package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MonthCropImportParamReq {

    @ApiModelProperty(value = "区域")
    private String area;

    //单位
    @ApiModelProperty(value = "单位")
    private String unit;

    //单位
    @ApiModelProperty(value = "单位ID")
    private String unitId;

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "月份")
    private Integer month;

}
