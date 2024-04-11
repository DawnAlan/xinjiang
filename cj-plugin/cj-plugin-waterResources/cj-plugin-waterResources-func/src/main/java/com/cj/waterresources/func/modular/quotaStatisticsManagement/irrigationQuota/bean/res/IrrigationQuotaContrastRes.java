package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IrrigationQuotaContrastRes {
    @ApiModelProperty("年")
    private Integer year;
    @ApiModelProperty("月")
    private Integer month;
    @ApiModelProperty("旬")
    private String tenDays;
    @ApiModelProperty("管理站")
    private String station;
    @ApiModelProperty("用水户")
    private String waterUser;
    @ApiModelProperty("作物类型")
    private String cropType;
    @ApiModelProperty("作物")
    private String irrigationCrop;
    @ApiModelProperty("实际灌溉水量")
    private Double sjVolume;
    @ApiModelProperty("计划灌溉水量")
    private Double jhVolume;
    @ApiModelProperty("实际灌溉面积")
    private Double sjArea;
    @ApiModelProperty("计划灌溉面积")
    private Double jhArea;
}
