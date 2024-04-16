package com.cj.waterresources.func.modular.surfaceWater.generator.domain;

import java.io.Serializable;

import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
* 
* @TableName surface_water_actualflow_detail
*/
@TableName(value ="surface_water_actualflow_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "", description = "")
public class SurfaceWaterActualflowDetail implements Serializable {

    @ApiModelProperty(value = "")
    private String id;
    @ApiModelProperty(value = "")
    private String parentId;
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    @ApiModelProperty(value = "类型")
    private String type;
    @ApiModelProperty(value = "测验方法")
    private String testingMethod;
    @ApiModelProperty(value = "水位基本水尺")
    private String waterGauge;
    @ApiModelProperty(value = "流量")
    private BigDecimal flow;
    @ApiModelProperty(value = "断面面积")
    private BigDecimal sectionalArea;
    @ApiModelProperty(value = "平均流速")
    private BigDecimal meanVelocity;
    @ApiModelProperty(value = "最大流速")
    private BigDecimal maxVelocity;
    @ApiModelProperty(value = "水面宽")
    private BigDecimal tpwd;
    @ApiModelProperty(value = "平均水深")
    private BigDecimal meanDepth;
    @ApiModelProperty(value = "最大水深")
    private BigDecimal maxDepth;
    @ApiModelProperty(value = "站点编码")
    private String siteCode;
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
