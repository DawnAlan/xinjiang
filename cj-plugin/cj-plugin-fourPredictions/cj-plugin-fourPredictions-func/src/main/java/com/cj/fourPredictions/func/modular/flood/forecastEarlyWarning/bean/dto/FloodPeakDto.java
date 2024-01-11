package com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FloodPeakDto implements Serializable {

    @ApiModelProperty("洪峰时刻")
    private Date peakTime;

    @ApiModelProperty("洪水等级")
    private String floodLevel;

    @ApiModelProperty("洪峰持续时间")
    private String peakDuration;
}
