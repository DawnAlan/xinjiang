package com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class IncomingWaterForecastKVDto implements Serializable {
    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "值")
    private Double value;
}
