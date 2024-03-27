package com.cj.flood.func.modular.prediction.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RealTimeEngineeringSituationDataRes implements Serializable {

    @ApiModelProperty("水库名称")
    private String reservoirName;

    @ApiModelProperty("汛限水位（m）")
    private Double floodControlLevel;

    @ApiModelProperty("实时水位（m）")
    private Double realTimeWaterLevel;

    @ApiModelProperty("已用库容（m³）")
    private Double usedStorageCapacity;

    @ApiModelProperty("剩余库容（m³）")
    private Double remainingStorageCapacity;

    @ApiModelProperty("累计拦蓄水量")
    private Double yearFloodRetentionCapacity;

    @ApiModelProperty("昨日拦蓄水量")
    private Double yesterdayFloodRetentionCapacity;
}
