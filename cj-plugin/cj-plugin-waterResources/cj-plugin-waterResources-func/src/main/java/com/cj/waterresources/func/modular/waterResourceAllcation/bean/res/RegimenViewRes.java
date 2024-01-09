package com.cj.waterresources.func.modular.waterResourceAllcation.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RegimenViewRes implements Serializable {

    @ApiModelProperty(value = "水库名称")
    private String stationName;

    @ApiModelProperty(value = "库容")
    private double capacity;

    @ApiModelProperty(value = "水位")
    private double level;

    @ApiModelProperty(value = "时间")
    private Date time;

    @ApiModelProperty(value = "入库流量")
    private double inflow;

    @ApiModelProperty(value = "出库流量")
    private double outflow;
}
