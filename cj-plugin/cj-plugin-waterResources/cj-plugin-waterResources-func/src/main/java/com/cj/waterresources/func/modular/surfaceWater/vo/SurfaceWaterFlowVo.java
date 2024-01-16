package com.cj.waterresources.func.modular.surfaceWater.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class SurfaceWaterFlowVo {
    @ApiModelProperty(value = "日汇总表")
    private List<SurfaceWaterFlowDetailVo> flowDetailVos;
    @ApiModelProperty(value = "年均流量")
    private BigDecimal annual_average_flow;
    @ApiModelProperty(value = "年径流")
    private BigDecimal annual_runoff;
    @ApiModelProperty(value = "年最大")
    private BigDecimal annual_max;
    @ApiModelProperty(value = "年最大日期")
    private Date annual_maxDay;
    @ApiModelProperty(value = "年最小")
    private BigDecimal annual_min;
    @ApiModelProperty(value = "年最小日期")
    private Date annual_minDay;
}
