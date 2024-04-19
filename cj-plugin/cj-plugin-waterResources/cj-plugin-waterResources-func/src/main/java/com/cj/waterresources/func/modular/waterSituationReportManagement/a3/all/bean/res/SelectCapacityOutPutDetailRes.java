package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SelectCapacityOutPutDetailRes implements Serializable {

    @ApiModelProperty(value = "站点名称")
    private String stationName;

    @ApiModelProperty(value = "流量")
    private Double flow;
}
