package com.cj.waterresources.func.modular.waterSupplyPlan.bean.req;

import com.cj.waterresources.func.core.utils.PageToolUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WaterSupplyPlanSelectListReq extends PageToolUtil implements Serializable {


    //供水计划类型(1-蓄水调度计划表)
    @ApiModelProperty(value = "供水计划类型(1-蓄水调度计划表)")
    private Integer waterSupplyPlanType;

    //水库(1-头屯河水库 2-楼庄子水库)
    @ApiModelProperty(value = "水库(1-头屯河水库 2-楼庄子水库)")
    private Integer reservoir;

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;
}
