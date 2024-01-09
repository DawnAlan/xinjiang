package com.cj.waterresources.func.modular.waterResourceAllcation.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WaterQuantityCalculationRes implements Serializable {

    @ApiModelProperty(value = "来水预测")
    private double inflowWater;

    @ApiModelProperty(value = "水库可用水量")
    private double waterAvailability;

    @ApiModelProperty(value = "供水计划")
    private double waterSupply;

    @ApiModelProperty(value = "需水计划")
    private double waterDemand;

    @ApiModelProperty(value = "时间")
    private String time;
}
