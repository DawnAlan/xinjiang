package com.cj.waterresources.func.modular.surfaceWater.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SurfaceWaterFlowDetailVo {
    @ApiModelProperty(value = "日期")
    private String Day;
    @ApiModelProperty(value = "一月")
    private BigDecimal Jan;
    @ApiModelProperty(value = "二月")
    private BigDecimal Feb;
    @ApiModelProperty(value = "三月")
    private BigDecimal Mar;
    @ApiModelProperty(value = "四月")
    private BigDecimal Ari;
    @ApiModelProperty(value = "五月")
    private BigDecimal May;
    @ApiModelProperty(value = "六月")
    private BigDecimal Jun;
    @ApiModelProperty(value = "七月")
    private BigDecimal Jul;
    @ApiModelProperty(value = "八月")
    private BigDecimal Aut;
    @ApiModelProperty(value = "九月")
    private BigDecimal Sep;
    @ApiModelProperty(value = "十月")
    private BigDecimal Oct;
    @ApiModelProperty(value = "十一月")
    private BigDecimal Nov;
    @ApiModelProperty(value = "十二月")
    private BigDecimal Dec;
}
