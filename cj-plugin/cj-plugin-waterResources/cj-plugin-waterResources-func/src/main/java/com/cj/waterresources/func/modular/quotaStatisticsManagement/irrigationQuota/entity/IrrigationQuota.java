package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 灌溉额度表(IrrigationQuota)表实体类
 *
 * @author makejava
 * @since 2023-12-22 12:49:39
 */
@Data
public class IrrigationQuota extends Model<IrrigationQuota> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "年度")
    private Integer year;

    //用水户
    @ApiModelProperty(value = "用水户")
    private String waterUser;

    //管理站
    @ApiModelProperty(value = "管理站")
    private String station;

    //作物类型
    @ApiModelProperty(value = "作物类型")
    private String cropType;

    //灌溉作物
    @ApiModelProperty(value = "灌溉作物")
    private String irrigationCrop;

    //实际灌溉总面积
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "实际灌溉总面积")
    private Double totalPlannedIrrigationArea;

    //四月上旬灌溉面积
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "四月上旬灌溉面积")
    private Double aprilEarlyOctoberIrrigationArea;

    //四月上旬灌溉水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "四月上旬灌溉水量")
    private Double aprilEarlyOctoberIrrigationWaterVolume;


    //四月中旬灌溉面积
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "四月中旬灌溉面积")
    private Double aprilMidDayIrrigationArea;

    //四月中旬灌溉水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "四月中旬灌溉水量")
    private Double aprilMidDayIrrigationWaterVolume;

    //四月下旬灌溉面积
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "四月下旬灌溉面积")
    private Double aprilLateOctoberIrrigationArea;

    //四月下旬灌溉水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "四月下旬灌溉水量")
    private Double aprilLateOctoberIrrigationWaterVolume;

    private String aprilEarlyOctoberId;

    private String aprilMidDayId;

    private String aprilLateOctoberId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayEarlyOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayEarlyOctoberIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayMidDayIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayMidDayIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayLateOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayLateOctoberIrrigationWaterVolume;

    private String mayEarlyOctoberId;

    private String mayMidDayId;

    private String mayLateOctoberId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneEarlyOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneEarlyOctoberIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneMidDayIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneMidDayIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneLateOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneLateOctoberIrrigationWaterVolume;

    private String juneEarlyOctoberId;

    private String juneMidDayId;

    private String juneLateOctoberId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyEarlyOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyEarlyOctoberIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyMidDayIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyMidDayIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyLateOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyLateOctoberIrrigationWaterVolume;

    private String julyEarlyOctoberId;

    private String julyMidDayId;

    private String julyLateOctoberId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustEarlyOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustEarlyOctoberIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustMidDayIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustMidDayIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustLateOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustLateOctoberIrrigationWaterVolume;

    private String augustEarlyOctoberId;

    private String augustMidDayId;

    private String augustLateOctoberId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberEarlyOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberEarlyOctoberIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberMidDayIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberMidDayIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberLateOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberLateOctoberIrrigationWaterVolume;

    private String septemberEarlyOctoberId;

    private String septemberMidDayId;

    private String septemberLateOctoberId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberEarlyOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberEarlyOctoberIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberMidDayIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberMidDayIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberLateOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberLateOctoberIrrigationWaterVolume;

    private String octoberEarlyOctoberId;

    private String octoberMidDayId;

    private String octoberLateOctoberId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberEarlyOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberEarlyOctoberIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberMidDayIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberMidDayIrrigationWaterVolume;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberLateOctoberIrrigationArea;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberLateOctoberIrrigationWaterVolume;

    private String novemberEarlyOctoberId;

    private String novemberMidDayId;

    private String novemberLateOctoberId;

    //累计灌溉面积
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "累计灌溉面积")
    private Double accumulatedIrrigationArea;

    //累计灌溉总量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "累计灌溉总量")
    private Double accumulatedTotalIrrigationAmount;

    //灌水定额
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "灌水定额")
    private Double irrigationQuota;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //创建人
    @ApiModelProperty(value = "创建人")
    private String createBy;

    //更新时间
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    //更新人
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    //逻辑删除(0-否 1-是)
    @ApiModelProperty(value = "逻辑删除(0-否 1-是)")
    private Integer del;

    //亩平均灌水额
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "亩平均灌水额")
    private Double averageIrrigationAmount;
}

