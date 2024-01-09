package com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 旬水量平衡(TenDaysWaterBalance)表实体类
 *
 * @author makejava
 * @since 2023-12-23 14:04:02
 */
@Data
public class TenDaysWaterBalance extends Model<TenDaysWaterBalance> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //管理站
    @ApiModelProperty(value = "管理站")
    private String station;

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;

    //月份
    @ApiModelProperty(value = "月份")
    private Integer month;

    //旬
    @ApiModelProperty(value = "旬")
    private String tenDays;

    //表头ID
    @ApiModelProperty(value = "表头ID")
    private String tableHeadId;

    //实收水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "实收水量")
    private Double actualWaterReceived;

    //按比例水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "按比例水量")
    private Double proportionalWaterQuantity;

    //实际水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "实际水量")
    private Double actualWaterVolume;

    //实际比例
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "实际比例")
    private Double actualProportion;

    //盈亏水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "盈亏水量")
    private Double profitAndLossWaterVolume;

    //盈亏比例
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "盈亏比例")
    private Double profitAndLossRatio;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    //创建人
    @ApiModelProperty(value = "创建人")
    private String createBy;

    //更新时间
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    //更新人
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    //逻辑删除(0-否 1-是)
    @ApiModelProperty(value = "逻辑删除(0-否 1-是)")
    private Integer del;
}

