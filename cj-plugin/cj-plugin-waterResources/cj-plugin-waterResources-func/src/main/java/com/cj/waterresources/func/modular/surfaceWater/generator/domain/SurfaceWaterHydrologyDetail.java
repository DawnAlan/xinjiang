package com.cj.waterresources.func.modular.surfaceWater.generator.domain;

import java.io.Serializable;

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
* 洪水水文
* @TableName surface_water_hydrology_detail
*/
@TableName(value ="surface_water_hydrology_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "洪水水文", description = "洪水水文")
public class SurfaceWaterHydrologyDetail implements Serializable {

    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "父级ID")
    private String parentId;
    @ApiModelProperty(value = "日期")
    private Date sampleTime;
    @ApiModelProperty(value = "流量")
    private BigDecimal flow;
    @ApiModelProperty(value = "水位")
    private BigDecimal level;
    @ApiModelProperty(value = "站点编码")
    private String siteCode;
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
