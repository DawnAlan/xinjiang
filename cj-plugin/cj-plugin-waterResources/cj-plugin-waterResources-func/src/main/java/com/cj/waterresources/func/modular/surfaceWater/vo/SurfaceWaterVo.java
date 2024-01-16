package com.cj.waterresources.func.modular.surfaceWater.vo;

import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWaterActualflowDetail;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWaterHydrologyDetail;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWaterWaterregimenDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SurfaceWaterVo {
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
    @ApiModelProperty(value = "日汇总表")
    private SurfaceWaterFlowVo flowDetailVos;
    @ApiModelProperty(value = "洪水摘录表")
    private List<SurfaceWaterHydrologyDetail>hydrologyDetailVos;
    @ApiModelProperty(value = "实测流量成果表")
    private List<SurfaceWaterActualflowDetail> actualflowDetailVos;
    @ApiModelProperty(value = "水库水情统计表")
    private List<SurfaceWaterWaterregimenDetail> waterregimenDetailVos;
}
