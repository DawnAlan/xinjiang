package com.cj.waterresources.func.modular.surfaceWater.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryListReq {
    @ApiModelProperty(value = "类型")
    private String type;
    @ApiModelProperty(value = "年份")
    private Integer year;
    @ApiModelProperty(value = "表名")
    private String tableName;
    @ApiModelProperty(value = "站点名称")
    private String siteName;
    @ApiModelProperty(value = "管理站名称")
    private String managerName;
    @ApiModelProperty(value = "分页大小,不分页传0")
    private Integer pageSize;
    @ApiModelProperty(value = "页码")
    private Integer pageNo;
}
