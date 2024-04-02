package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class FloodRetentionCapacityRes implements Serializable {

    @ApiModelProperty(value ="水库名称")
    private String reservoirName;

    @ApiModelProperty(value ="累计拦蓄水量")
    private Double yearFloodRetentionCapacity;

    @ApiModelProperty(value ="昨日拦蓄水量")
    private Double yesterdayFloodRetentionCapacity;

    @ApiModelProperty(value = "入库流量")
    private Double inputFlow;

    @ApiModelProperty(value = "出库流量")
    private Double outputFlow;

    private String overallId;
}
