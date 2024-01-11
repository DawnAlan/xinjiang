package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MonthCropSelectListReq implements Serializable {

    //单位
    @ApiModelProperty(value = "单位")
    private String unit;

    //作物类型
    @ApiModelProperty(value = "作物类型")
    private String cropType;

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;

    //月份
    @ApiModelProperty(value = "月份")
    private Integer month;

    //区域
    @ApiModelProperty(value = "区域")
    private String area;
}
