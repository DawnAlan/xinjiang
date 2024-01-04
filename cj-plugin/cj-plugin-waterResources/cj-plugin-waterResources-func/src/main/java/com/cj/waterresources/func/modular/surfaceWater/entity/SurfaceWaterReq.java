package com.cj.waterresources.func.modular.surfaceWater.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SurfaceWaterReq {
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
}
