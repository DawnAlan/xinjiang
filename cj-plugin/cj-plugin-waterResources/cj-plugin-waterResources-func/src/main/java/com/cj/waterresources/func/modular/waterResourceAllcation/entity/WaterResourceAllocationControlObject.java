package com.cj.waterresources.func.modular.waterResourceAllcation.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
* 
* @TableName WATER_RESOURCE_ALLOCATION_CONTROL_OBJECT
*/
@TableName(value ="WATER_RESOURCE_ALLOCATION_CONTROL_OBJECT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "水资源调配控制对象", description = "水资源调配控制对象")
public class WaterResourceAllocationControlObject implements Serializable {

    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "调配方案id")
    private String allocationId;
    @ApiModelProperty(value = "对象类型,0-楼庄子生态流量,1-头屯河生态流量,2-楼庄子动态汛限水位,3-楼庄子最低调度水位,4-头屯河动态汛限水位,5-头屯河最低调度水位")
    private String objectType;
    @ApiModelProperty(value = "月份")
    private String monthNum;
    @ApiModelProperty(value = "控制值")
    private Double monthVal;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
