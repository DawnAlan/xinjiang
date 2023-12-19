package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TrunkCanalSelectListReq implements Serializable {

    @ApiModelProperty(value = "年度")
    private Integer year;

    @ApiModelProperty(value = "区域")
    private String area;

    //用水计划
    @ApiModelProperty(value = "用水计划")
    private String useWaterPlan;
}
