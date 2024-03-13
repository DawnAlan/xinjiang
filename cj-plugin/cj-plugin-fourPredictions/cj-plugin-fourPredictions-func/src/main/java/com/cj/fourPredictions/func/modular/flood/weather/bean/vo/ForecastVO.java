package com.cj.fourPredictions.func.modular.flood.weather.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "预报天气返回对象", description = "预报天气返回对象")
public class ForecastVO {
    @ApiModelProperty(value = "时间")
    private Date time;
    @ApiModelProperty(value = "温度")
    private BigDecimal temperature;
    @ApiModelProperty(value = "降雨量")
    private BigDecimal rainfall;
    @ApiModelProperty(value = "天气")
    private String weather;

}
