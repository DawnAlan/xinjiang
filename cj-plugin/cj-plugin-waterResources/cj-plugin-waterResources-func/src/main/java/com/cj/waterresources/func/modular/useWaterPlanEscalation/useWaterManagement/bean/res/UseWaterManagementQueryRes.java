package com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UseWaterManagementQueryRes implements Serializable {

    @ApiModelProperty(value = "id")
    private String id;

    //用水计划(1-年 2-旬 3-日)
    @ApiModelProperty(value = "用水计划")
    private String useWaterPlan;

    //使用区域
    @ApiModelProperty(value = "使用区域")
    private String area;

    //父节点
    @ApiModelProperty(value = "父节点")
    private String pId;

    //单位名称
    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "单位Id")
    private String unitId;

    @ApiModelProperty(value = "子节点")
    private List<UseWaterManagementQueryRes> children;
}
