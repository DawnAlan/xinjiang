package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.bean.req;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DutyRecordsSelectListReq {

    //记录日期
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "记录日期")
    private Date recordTime;

    //所属站点
    @ApiModelProperty(value = "所属站点")
    private String station;
}
