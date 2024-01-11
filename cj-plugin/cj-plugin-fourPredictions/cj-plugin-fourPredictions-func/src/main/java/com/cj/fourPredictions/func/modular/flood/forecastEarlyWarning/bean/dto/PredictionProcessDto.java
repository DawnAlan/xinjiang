package com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PredictionProcessDto implements Serializable {

    @ApiModelProperty(value = "时间")
    private Date time;

    @ApiModelProperty(value = "来水过程\\入库流量")
    private Double preQ;

    @ApiModelProperty(value = "水位")
    private Double waterLevel;

    @ApiModelProperty(value = "库容")
    private Double capacity;

}
