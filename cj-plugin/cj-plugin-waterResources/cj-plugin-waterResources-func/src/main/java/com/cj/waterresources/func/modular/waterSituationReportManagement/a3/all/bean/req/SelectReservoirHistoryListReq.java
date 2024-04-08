package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SelectReservoirHistoryListReq implements Serializable {

    @ApiModelProperty(value = "水库名称")
    private String name;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
