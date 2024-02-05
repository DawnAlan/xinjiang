package com.cj.waterresources.func.modular.surfaceWater.generator.domain;

import java.io.Serializable;

import java.time.LocalDate;
import java.util.Date;
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
* 地表水水情数据子表
* @TableName surface_water_flow_detail
*/
@TableName(value ="surface_water_flow_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "地表水水情数据子表", description = "地表水水情数据子表")
public class SurfaceWaterFlowDetail implements Serializable {

    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "父级ID")
    private String parentId;
    @ApiModelProperty(value = "时间")
    private Date sampleTime;
    @ApiModelProperty(value = "年")
    private Integer year;
    @ApiModelProperty(value = "月")
    private Integer month;
    @ApiModelProperty(value = "日")
    private Integer day;
    @ApiModelProperty(value = "流量")
    private BigDecimal flow;
    @ApiModelProperty(value = "站点编码")
    private String siteCode;
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
