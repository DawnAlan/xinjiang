package com.cj.model.func.modular.entity;

import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 洪水过程(Flood)表实体类
 *
 * @author leoluoxu
 * @since 2023-08-25 18:51:57
 */
@Data
@ApiModel("Flood")
public class Flood implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("断面位置")
    @TableField(value = "LOCATION")
    @Excel(name = "LOCATION",width = 15,orderNum = "1")
    private String location;
   
    @ApiModelProperty("预报尺度")
    @TableField(value = "SCALE")
    @Excel(name = "SCALE",width = 15,orderNum = "2")
    private String scale;
   
    @ApiModelProperty("洪号")
    @TableField(value = "PEAK_INDEX")
    @Excel(name = "PEAK_INDEX",width = 15,orderNum = "3")
    private Integer peakIndex;
   
    @ApiModelProperty("时间")
    @TableField(value = "TIME")
    @Excel(name = "TIME",width = 15,orderNum = "4")
    private Date time;
   
    @ApiModelProperty("预报流量")
    @TableField(value = "PRE_Q")
    @Excel(name = "PRE_Q",width = 15,orderNum = "5")
    private Double preQ;
   
    @ApiModelProperty("水位")
    @TableField(value = "WATER_LEVEL")
    @Excel(name = "WATER_LEVEL",width = 15,orderNum = "6")
    private Double waterLevel;
   
    @ApiModelProperty("洪峰")
    @TableField(value = "PEAK_FLOOD")
    @Excel(name = "PEAK_FLOOD",width = 15,orderNum = "7")
    private Double peakFlood;
   
    @ApiModelProperty("峰现时间")
    @TableField(value = "PEAK_TIME")
    @Excel(name = "PEAK_TIME",width = 15,orderNum = "8")
    private Date peakTime;
   
    @ApiModelProperty("洪峰持续时间")
    @TableField(value = "PEAK_DURATION")
    @Excel(name = "PEAK_DURATION",width = 15,orderNum = "9")
    private String peakDuration;

    @ApiModelProperty("洪量")
    @TableField(value = "FLOOD_VOLUME")
    @Excel(name = "FLOOD_VOLUME",width = 15,orderNum = "10")
    private Double floodVolume;

    @ApiModelProperty("洪水来源")
    @TableField(value = "Q_COMPOSITION")
    @Excel(name = "Q_COMPOSITION",width = 15,orderNum = "11")
    private String qComposition;

    @ApiModelProperty("洪水成因")
    @TableField(value = "Q_CAUSE")
    @Excel(name = "Q_CAUSE",width = 15,orderNum = "12")
    private String qCause;

    @ApiModelProperty("洪水等级")
    @TableField(value = "FLOOD_LEVEL")
    @Excel(name = "FLOOD_LEVEL",width = 15,orderNum = "13")
    private String floodLevel;

    @ApiModelProperty("出库流量")
    @TableField(value = "OUT_Q")
    @Excel(name = "OUT_Q",width = 15,orderNum = "10")
    private Double outQ;

    @ApiModelProperty("预警时段")
    @TableField(value = "WARNING_TIME")
    @Excel(name = "WARNING_TIME",width = 15,orderNum = "10")
    private Integer warningTime;

}

