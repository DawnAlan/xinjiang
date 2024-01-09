package com.cj.flood.func.modular.prediction.bean.dto;

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

    @ApiModelProperty(value = "拦蓄洪量")
    private Double retain;

    @ApiModelProperty(value = "入库流量")
    private Double qIn;

    @ApiModelProperty(value = "出库流量")
    private Double qOut;

    @ApiModelProperty(value = "防洪库容")
    private Double floodStorageCapacityPercent;

    @ApiModelProperty(value = "调洪库容")
    private double regulatingStorageCapacityPercent;

}
