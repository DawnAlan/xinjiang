package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IrrigationQuotaListReq {

    @ApiModelProperty(value = "年度")
    private Integer year;

    //用水户
    @ApiModelProperty(value = "用水户")
    private String waterUser;

    //作物类型
    @ApiModelProperty(value = "作物类型")
    private String cropType;

    @ApiModelProperty(value = "管理站")
    private String station;

}
