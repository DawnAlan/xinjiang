package com.cj.model.func.modular.FloodPredict.entity;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RainFallDto {

    @ApiModelProperty(value = "时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String date;

    @ApiModelProperty(value = "降雨量")
    private Double rainFall;

    @ApiModelProperty(value = "气温")
    private Double temperature;

    @ApiModelProperty(value = "降雨地区")
    private String area;
}
