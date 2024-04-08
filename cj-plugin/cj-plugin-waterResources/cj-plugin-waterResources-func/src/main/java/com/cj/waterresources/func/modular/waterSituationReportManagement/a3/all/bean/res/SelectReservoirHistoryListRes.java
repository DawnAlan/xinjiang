package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SelectReservoirHistoryListRes implements Serializable {

    @ApiModelProperty(value = "水库名称")
    private String name;

    @ApiModelProperty(value = "时间")
    private Date time;

    @ApiModelProperty(value = "结束时间")
    private Double value;
}
