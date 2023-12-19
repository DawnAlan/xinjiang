package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MonthWaterUsePlanSelectListReq implements Serializable {
    @ApiModelProperty(value = "年度")
    private Integer year;

    //月度
    @ApiModelProperty(value = "月度")
    private Integer month;

    @ApiModelProperty(value = "区域")
    private String area;
}
