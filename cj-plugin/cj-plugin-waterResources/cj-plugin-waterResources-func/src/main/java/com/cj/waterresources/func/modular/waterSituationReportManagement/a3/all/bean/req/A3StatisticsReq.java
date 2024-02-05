package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class A3StatisticsReq implements Serializable {

    @ApiModelProperty(value = "站点")
    private String station;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "层级下的所有id")
    private List<String> ids;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
