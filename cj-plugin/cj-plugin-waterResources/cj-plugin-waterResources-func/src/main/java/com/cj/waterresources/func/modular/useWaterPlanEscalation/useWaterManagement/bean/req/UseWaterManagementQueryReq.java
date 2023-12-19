package com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UseWaterManagementQueryReq implements Serializable {

    //用水计划(1-年 2-旬 3-日)
    @ApiModelProperty(value = "用水计划")
    private String useWaterPlan;

    //使用区域
    @ApiModelProperty(value = "使用区域")
    private String area;

}
