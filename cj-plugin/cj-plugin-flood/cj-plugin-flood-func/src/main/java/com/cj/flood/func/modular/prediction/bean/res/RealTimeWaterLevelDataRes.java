package com.cj.flood.func.modular.prediction.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RealTimeWaterLevelDataRes implements Serializable {

    @ApiModelProperty("站点名称")
    private String stationName;

    @ApiModelProperty("当前流量")
    private Double flow;

    @ApiModelProperty("水位")
    private Double relativeWaterLevel;
}
