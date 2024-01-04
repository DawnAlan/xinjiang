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
    private String waterLevel;
    @ApiModelProperty(value = "入库流量")
    private Double inFlow;
    @ApiModelProperty(value = "出库流量")
    private Double outFlow;
    @ApiModelProperty(value = "库容")
    private Double storageCapacity;
    @ApiModelProperty(value = "昨日拦蓄洪量")
    private Double yesterdayFloodRetentionCapacity;
    @ApiModelProperty(value = "累计拦蓄洪量")
    private Double yearFloodRetentionCapacity;
}
