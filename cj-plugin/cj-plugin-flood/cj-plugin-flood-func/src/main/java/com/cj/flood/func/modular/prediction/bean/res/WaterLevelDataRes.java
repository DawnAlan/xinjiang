package com.cj.flood.func.modular.prediction.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WaterLevelDataRes implements Serializable {


    @ApiModelProperty("流量")
    private Double flow;

    @ApiModelProperty("水位")
    private Double waterLevel;

    @ApiModelProperty("时间")
    private String time;
}
