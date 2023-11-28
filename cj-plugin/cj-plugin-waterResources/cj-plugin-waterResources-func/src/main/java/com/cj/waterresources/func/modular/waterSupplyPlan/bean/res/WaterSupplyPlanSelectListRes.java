package com.cj.waterresources.func.modular.waterSupplyPlan.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WaterSupplyPlanSelectListRes implements Serializable {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //供水计划类型(1-蓄水调度计划表)
    @ApiModelProperty(value = "供水计划类型(1-蓄水调度计划表)")
    private Integer waterSupplyPlanType;

    //水库(1-头屯河水库 2-楼庄子水库)
    @ApiModelProperty(value = "水库(1-头屯河水库 2-楼庄子水库)")
    private Integer reservoir;

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;

    //表格行数据
    @ApiModelProperty(value = "表格行数据")
    private String tableValue;

    //表格头数据
    @ApiModelProperty(value = "表格头数据")
    private String tableHead;
}
