package com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SporadicWaterFeeSelectListReq implements Serializable {

    //类型
    @ApiModelProperty(value = "类型")
    private Integer waterFeeType;

    //年份
    @ApiModelProperty(value = "年份")
    private Integer year;

    //月份
    @ApiModelProperty(value = "月份")
    private Integer month;
}
