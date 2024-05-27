package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StatisticsByLocationRes {

    @ApiModelProperty(value = "作物名称")
    private String crop;

    @ApiModelProperty(value = "种植面积")
    private Double area;

    @ApiModelProperty(value = "累计灌溉")
    private Double irrigation;

    @ApiModelProperty(value = "实际用水量")
    private Double waterConsumption;
}
