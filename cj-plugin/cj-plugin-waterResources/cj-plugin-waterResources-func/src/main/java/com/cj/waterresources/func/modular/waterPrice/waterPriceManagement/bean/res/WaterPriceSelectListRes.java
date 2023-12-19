package com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WaterPriceSelectListRes implements Serializable {

    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //用水类型
    @ApiModelProperty(value = "用水类型")
    private String useWaterType;

    //管理站
    @ApiModelProperty(value = "管理站")
    private String station;

    //取水口/用水户
    @ApiModelProperty(value = "取水口/用水户")
    private String userName;

    //水价
    @ApiModelProperty(value = "水价")
    private Double waterPrice;

    @ApiModelProperty(value = "父ID")
    private String pId;

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

    @ApiModelProperty(value = "子节点")
    private List<WaterPriceSelectListRes> children;
}
