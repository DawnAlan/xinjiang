package com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DayWaterBalanceSelectListReq implements Serializable {

    //管理站
    @ApiModelProperty(value = "管理站")
    private String station;

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;

    //月份
    @ApiModelProperty(value = "月份")
    private Integer month;

    //旬
    @ApiModelProperty(value = "日")
    private String day;
}
