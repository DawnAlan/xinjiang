package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TodayWaterSituationForFloodRes implements Serializable {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "流量")
    private Double flow;

    @ApiModelProperty(value = "水位")
    private Double waterLevel;
}
