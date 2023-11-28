package com.cj.waterresources.func.modular.waterSupplyPlan.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WaterSupplyPlanUpdateReq implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "表格行数据")
    private String tableValue;

    @ApiModelProperty(value = "表格头数据")
    private String tableHead;
}
