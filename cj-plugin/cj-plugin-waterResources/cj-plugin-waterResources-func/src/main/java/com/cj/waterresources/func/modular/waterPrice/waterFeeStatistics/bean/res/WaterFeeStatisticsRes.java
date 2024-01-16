package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WaterFeeStatisticsRes implements Serializable {

    //应交水费
    @ApiModelProperty(value = "应交水费")
    private Double payableWaterFee;

    //预交水费
    @ApiModelProperty(value = "预交水费")
    private Double advancePaymentWaterFee;
}
