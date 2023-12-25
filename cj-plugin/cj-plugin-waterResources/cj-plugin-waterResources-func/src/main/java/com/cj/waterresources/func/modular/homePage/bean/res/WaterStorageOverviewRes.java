package com.cj.waterresources.func.modular.homePage.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterStorageOverviewRes {
    @ApiModelProperty(value = "水库名")
    private String waterStorageName;
    @ApiModelProperty(value = "水位")
    private double waterLevel;
    @ApiModelProperty(value = "入库流量")
    private double inFlow;
    @ApiModelProperty(value = "出库流量")
    private double outFlow;
    @ApiModelProperty(value = "库容")
    private double storageCapacity;
    @ApiModelProperty(value = "拦蓄水量")
    private double waterRetentionCapacity;
}
