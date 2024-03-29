package com.cj.waterresources.func.modular.surfaceWater.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TypicalYearDetailReq {
    @ApiModelProperty(value = "年")
    private Integer year;
    @ApiModelProperty(value = "流量合计")
    private Double sumFlow;
    @ApiModelProperty(value = "流量平均")
    private Double avgFlow;
    @ApiModelProperty(value = "水量合计")
    private Double sumWater;
    @ApiModelProperty(value = "水量平均")
    private Double avgWater;
}
