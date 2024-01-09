package com.cj.waterresources.func.modular.useWaterPlanEscalation.index.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WaterUsePlanForViewRes implements Serializable {

    @ApiModelProperty(value = "区域")
    private String unit;

    @ApiModelProperty(value = "总值")
    private Double totalAmount;
}
