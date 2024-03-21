package com.cj.model.func.modular.FloodPredict.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RainFallDto {

    @ApiModelProperty(value = "时间")
    private String date;

    @ApiModelProperty(value = "降雨量")
    private Double rainFall;

    @ApiModelProperty(value = "气温")
    private Double temperature;

    @ApiModelProperty(value = "降雨地区")
    private String area;
}
