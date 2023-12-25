package com.cj.waterresources.func.modular.homePage.inspection.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AbnormalDetailRes {
    @ApiModelProperty(value = "水库名")
    private String waterStorageName;
    @ApiModelProperty(value = "当前水位")
    private double waterLevel;
    @ApiModelProperty(value = "入库流量")
    private double inFlow;
    @ApiModelProperty(value = "出库流量")
    private double outFlow;
    @ApiModelProperty(value = "防洪库容")
    private double floodStorageCapacity;
    @ApiModelProperty(value = "调节库容")
    private double regulatingStorageCapacity;
}
