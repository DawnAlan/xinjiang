package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.bean.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StatisticsByLocationReq implements Serializable {

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "月份")
    private Integer month;

    @ApiModelProperty(value = "旬")
    private String tenDays;

    private String startTime;

    private String endTime;
}
