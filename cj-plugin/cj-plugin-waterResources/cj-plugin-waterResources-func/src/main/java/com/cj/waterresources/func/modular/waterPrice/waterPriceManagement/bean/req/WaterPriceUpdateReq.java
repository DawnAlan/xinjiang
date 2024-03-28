package com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.req;

import com.cj.waterresources.func.core.utils.PageToolUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WaterPriceUpdateReq implements Serializable {

    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String ids;

    //用水类型
    @ApiModelProperty(value = "用水类型")
    private String useWaterType;

    //水价
    @ApiModelProperty(value = "水价")
    private Double waterPrice;

    @ApiModelProperty(value = "定额水量")
    private Double quotaWaterQuantity;

    @ApiModelProperty(value = "定额水价")
    private Double fixedWaterPrice;

    @ApiModelProperty(value = "第一阶段标准")
    private Double firstLevelStandard;

    @ApiModelProperty(value = "第一阶段价格")
    private Double firstTierPrice;

    @ApiModelProperty(value = "第二阶段标准")
    private Double secondLevelStandard;

    @ApiModelProperty(value = "第二阶段价格")
    private Double secondTierPrice;

    @ApiModelProperty(value = "第三阶段标准")
    private Double thirdLevelStandard;

    @ApiModelProperty(value = "第三阶段价格")
    private Double thirdTierPrice;

    @ApiModelProperty(value = "水资源费")
    private Double waterResourcePrice;

}
