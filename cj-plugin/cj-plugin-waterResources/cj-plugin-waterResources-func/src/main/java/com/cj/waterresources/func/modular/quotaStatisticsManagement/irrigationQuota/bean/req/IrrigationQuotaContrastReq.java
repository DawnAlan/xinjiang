package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.req;

import io.swagger.annotations.ApiModelProperty;

public class IrrigationQuotaContrastReq {
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "管理站",required = true)
    private String station;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "作物类型")
    private String cropType;
}
