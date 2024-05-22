package com.cj.waterresources.func.modular.surfaceWater.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TenDayVo {
    @ApiModelProperty(value = "月份")
    private Integer month;
    @ApiModelProperty(value = "名称")
    private Integer name;
    @ApiModelProperty(value = "值")
    private BigDecimal value;
}
