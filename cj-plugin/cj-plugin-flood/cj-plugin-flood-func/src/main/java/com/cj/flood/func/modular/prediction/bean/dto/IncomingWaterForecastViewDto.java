package com.cj.flood.func.modular.prediction.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class IncomingWaterForecastViewDto implements Serializable {
    @ApiModelProperty(value = "洪峰")
    private Map<Integer, FloodPeakDto> floodPeak;

    @ApiModelProperty(value = "预报过程")
    private List<PredictionDto> predictionProcess;

    @ApiModelProperty(value = "洪水来源")
    private List<IncomingWaterForecastKVDto> qCause;

    @ApiModelProperty(value = "洪量组成")
    private List<IncomingWaterForecastKVDto> qComposition;

    @ApiModelProperty("超警时刻")
    private List<Date> overAlarmProcess;

    @ApiModelProperty(value = "洪峰流量")
    private Double peakFlood;

    @ApiModelProperty(value = "洪量")
    private Double peakVolume;

    private Integer sort;

    private String name;
}
