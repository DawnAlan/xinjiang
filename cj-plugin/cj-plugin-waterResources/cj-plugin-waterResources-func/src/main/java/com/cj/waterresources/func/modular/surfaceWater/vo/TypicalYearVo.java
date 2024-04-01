package com.cj.waterresources.func.modular.surfaceWater.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TypicalYearVo {
    @ApiModelProperty(value = "年")
    private Integer Year;
    @ApiModelProperty(value = "年预报")
    private BigDecimal predictionYear;
    @ApiModelProperty(value = "3月预报")
    private BigDecimal prediction3;
    @ApiModelProperty(value = "4月预报")
    private BigDecimal prediction4;
    @ApiModelProperty(value = "5月预报")
    private BigDecimal prediction5;
    @ApiModelProperty(value = "比去年")
    private BigDecimal comparedToLastYear;
    @ApiModelProperty(value = "来水频率")
    private BigDecimal frequency;

    @ApiModelProperty(value = "典型年")
    private Integer typicalYear;
    @ApiModelProperty(value = "平均流量")
    private Double typicalFlow;
    @ApiModelProperty(value = "平均水量")
    private Double typicalWater;

    @ApiModelProperty(value = "平均年")
    private Integer avgYear;
    @ApiModelProperty(value = "平均流量")
    private Double avgFlow;
    @ApiModelProperty(value = "平均水量")
    private Double avgWater;

    @ApiModelProperty(value = "最大年")
    private Integer maxYear;
    @ApiModelProperty(value = "平均流量")
    private Double maxFlow;
    @ApiModelProperty(value = "平均水量")
    private Double maxWater;
    @ApiModelProperty(value = "最小年")
    private Integer minYear;
    @ApiModelProperty(value = "平均流量")
    private Double minFlow;
    @ApiModelProperty(value = "平均水量")
    private Double minWater;
}
