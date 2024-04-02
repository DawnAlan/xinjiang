package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TodayWaterSituationRes implements Serializable {

    @ApiModelProperty(value = "站点名称")
    private String name;

    @ApiModelProperty(value = "值")
    private Double value;

    private String id;
}
