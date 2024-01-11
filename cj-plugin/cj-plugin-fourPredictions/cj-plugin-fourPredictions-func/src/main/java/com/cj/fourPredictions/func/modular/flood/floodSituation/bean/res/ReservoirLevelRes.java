package com.cj.fourPredictions.func.modular.flood.floodSituation.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReservoirLevelRes implements Serializable {

    @ApiModelProperty("水位")
    private Double waterLevel;

    @ApiModelProperty("时间")
    private String time;
}
