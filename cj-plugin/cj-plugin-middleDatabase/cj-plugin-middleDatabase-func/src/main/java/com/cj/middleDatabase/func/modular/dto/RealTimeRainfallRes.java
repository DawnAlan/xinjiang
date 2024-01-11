package com.cj.middleDatabase.func.modular.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RealTimeRainfallRes implements Serializable {

    @ApiModelProperty("雨量站名称")
    private String stationName;

    @ApiModelProperty("降雨量")
    private BigDecimal rainfall;
}
