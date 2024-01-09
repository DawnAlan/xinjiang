package com.cj.waterresources.func.modular.waterResourceAllcation.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@Data
public class WaterSupplyBalanceRes implements Serializable {

    @ApiModelProperty(value = "计划供水总量")
    private Double waterDemand;

    @ApiModelProperty(value = "实际供水总量")
    private Double waterSupply;
}
