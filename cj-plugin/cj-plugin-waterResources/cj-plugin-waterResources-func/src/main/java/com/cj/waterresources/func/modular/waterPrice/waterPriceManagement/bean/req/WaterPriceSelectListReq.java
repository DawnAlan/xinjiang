package com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WaterPriceSelectListReq implements Serializable {

    //用水类型
    @ApiModelProperty(value = "用水类型")
    private String useWaterType;

    //管理站
    @ApiModelProperty(value = "管理站")
    private String station;

    @ApiModelProperty(value = "是否是农业(1-是 2-不是)")
    private Integer isAgriculture;
}
