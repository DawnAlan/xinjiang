package com.cj.fourPredictions.func.modular.flood.floodSituation.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RealTimeEngineeringSituationDataRes implements Serializable {

    @ApiModelProperty("水库名称")
    public String reservoirName;

    @ApiModelProperty("汛限水位（m）")
    public Double floodControlLevel;

    @ApiModelProperty("实时水位（m）")
    public Double realTimeWaterLevel;

    @ApiModelProperty("已用库容（m³）")
    public Double usedStorageCapacity;

    @ApiModelProperty("剩余库容（m³）")
    public Double remainingStorageCapacity;
}
