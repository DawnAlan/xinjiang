package com.cj.flood.func.modular.prediction.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PredictionFrontViewDto implements Serializable {

    @ApiModelProperty(value = "时间")
    private String time;

    @ApiModelProperty(value = "水位")
    private Double value;
}
