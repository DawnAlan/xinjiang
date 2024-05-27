package com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UseWaterManagementAddReq implements Serializable {

    private String id;

    //用水计划(1-年 2-旬 3-日)
    @ApiModelProperty(value = "用水计划")
    private String useWaterPlan;

    //使用区域
    @ApiModelProperty(value = "使用区域")
    private String area;

    //父节点
    @ApiModelProperty(value = "父节点(初始父节点id默认0)")
    private String pId;

    //单位名称
    @ApiModelProperty(value = "单位名称")
    private String unitName;

    //单位Id
    @ApiModelProperty(value = "单位Id")
    private String unitId;

    //单位类型
    @ApiModelProperty(value = "单位类型")
    private String unitType;

    //绑定A3ID
    @ApiModelProperty(value = "绑定A3ID")
    private String bindId;

    //地区(1-十二师 2-乌鲁木齐经开区 3-昌吉)
    @ApiModelProperty(value = "地区(1-十二师 2-乌鲁木齐经开区 3-昌吉)")
    private Integer location;
}
