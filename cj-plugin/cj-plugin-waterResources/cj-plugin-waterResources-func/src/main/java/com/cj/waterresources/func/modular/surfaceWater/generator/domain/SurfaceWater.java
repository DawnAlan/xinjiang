package com.cj.waterresources.func.modular.surfaceWater.generator.domain;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
* 地表水情数据
* @TableName surface_water
*/
@TableName(value ="surface_water")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "地表水情数据", description = "地表水情数据")
public class SurfaceWater implements Serializable {

    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "类型")
    private String type;
    @ApiModelProperty(value = "年份")
    private Integer year;
    @ApiModelProperty(value = "表名")
    private String tableName;
    @ApiModelProperty(value = "站点编号")
    private String siteCode;
    @ApiModelProperty(value = "站点名称")
    private String siteName;
    @ApiModelProperty(value = "管理站编号")
    private String managerCode;
    @ApiModelProperty(value = "管理站名称")
    private String managerName;
    @ApiModelProperty(value = "单位")
    private String unit;
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
