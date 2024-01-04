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
* 
* @TableName surface_water_waterregimen_detail
*/
@TableName(value ="surface_water_waterregimen_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "", description = "")
public class SurfaceWaterWaterregimenDetail implements Serializable {

    @ApiModelProperty(value = "ID")
    private String id;
    @ApiModelProperty(value = "父级ID")
    private String parentId;
    @ApiModelProperty(value = "日期")
    private Date sampleTime;
    @ApiModelProperty(value = "进库早8")
    private BigDecimal inReservoirAm;
    @ApiModelProperty(value = "进库日均")
    private BigDecimal inReservoirMean;
    @ApiModelProperty(value = "出库早8")
    private BigDecimal outReservoirAm;
    @ApiModelProperty(value = "出库日均")
    private BigDecimal outReservoirMean;
    @ApiModelProperty(value = "出库河道早8")
    private BigDecimal outRiverAm;
    @ApiModelProperty(value = "出库河道日均")
    private BigDecimal outRiverMean;
    @ApiModelProperty(value = "出库暗管早8")
    private BigDecimal outConcealedAm;
    @ApiModelProperty(value = "出库暗管日均")
    private BigDecimal outConcealedMean;
    @ApiModelProperty(value = "水位早8")
    private BigDecimal waterLevelAm;
    @ApiModelProperty(value = "水位晚8")
    private BigDecimal waterLevelPm;
    @ApiModelProperty(value = "库容早8")
    private BigDecimal capacityAm;
    @ApiModelProperty(value = "库容晚8")
    private BigDecimal capacityPm;
    @ApiModelProperty(value = "坝后渗流点流量(m3/s)")
    private String seepageFlow;
    @ApiModelProperty(value = "八钢浊度早8")
    private Integer bagangTurbidity;
    @ApiModelProperty(value = "出库浊度早8")
    private Integer outTurbidityAm;
    @ApiModelProperty(value = "出库浊度晚8")
    private Integer outTurbidityPm;
    @ApiModelProperty(value = "龙口浊度早8")
    private Integer longkouTurbidityAm;
    @ApiModelProperty(value = "龙口浊度晚8")
    private Integer longkouTurbidityPm;
    @ApiModelProperty(value = "年")
    private Integer year;
    @ApiModelProperty(value = "月")
    private Integer month;
    @ApiModelProperty(value = "日")
    private Integer day;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
