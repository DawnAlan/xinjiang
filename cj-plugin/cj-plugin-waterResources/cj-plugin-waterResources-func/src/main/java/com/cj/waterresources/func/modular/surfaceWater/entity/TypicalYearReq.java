package com.cj.waterresources.func.modular.surfaceWater.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TypicalYearReq {
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
//    @ApiModelProperty(value = "比去年")
//    private BigDecimal comparedToLastYear;
    @ApiModelProperty(value = "来水频率")
    private BigDecimal frequency;
    @ApiModelProperty(value = "平均流量")
    private Double avgFlow;
    @ApiModelProperty(value = "平均水量")
    private Double avgWater;
    @ApiModelProperty(value = "历年流量情况")
    private List<TypicalYearDetailReq> typicalYearDetailReqList;

}
