package com.cj.middleDatabase.func.modular.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class RealTimeRainfallRes implements Serializable {

    @ApiModelProperty("雨量站名称")
    private String stationName;

    @ApiModelProperty("降雨量")
    private Double rainfall;

    private String id;

    private String overallId;

    private Date dateTime;
}
