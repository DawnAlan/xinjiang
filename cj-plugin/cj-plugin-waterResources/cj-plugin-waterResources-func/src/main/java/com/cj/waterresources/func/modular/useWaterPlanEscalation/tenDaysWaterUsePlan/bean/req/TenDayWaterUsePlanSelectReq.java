package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TenDayWaterUsePlanSelectReq implements Serializable {

    //灌区
    @ApiModelProperty(value = "灌区")
    private String irrigatedArea;

    //用水户
    @ApiModelProperty(value = "用水户")
    private String useWaterUser;

    //作物类型
    @ApiModelProperty(value = "作物类型")
    private String cropType;

    //年
    @ApiModelProperty(value = "年")
    private Integer year;

    //月
    @ApiModelProperty(value = "月")
    private Integer month;

    //旬
    @ApiModelProperty(value = "旬")
    private String tenDays;
}
