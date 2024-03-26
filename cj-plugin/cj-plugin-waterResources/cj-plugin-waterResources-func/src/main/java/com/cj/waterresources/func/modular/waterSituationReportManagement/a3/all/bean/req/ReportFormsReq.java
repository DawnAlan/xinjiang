package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReportFormsReq implements Serializable {

    @ApiModelProperty(value = "水库")
    private String reservoir;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
