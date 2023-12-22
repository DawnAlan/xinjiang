package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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

    //作物类型
    @ApiModelProperty(value = "作物类型")
    private String cropType;

    //灌溉作物
    @ApiModelProperty(value = "灌溉作物")
    private String irrigationCrop;

    //计划灌溉总面积
    @ApiModelProperty(value = "计划灌溉总面积")
    private Double totalPlannedIrrigationArea;

    //四月上旬灌溉面积
    @ApiModelProperty(value = "四月上旬灌溉面积")
    private Double aprilEarlyOctoberIrrigationArea;

    //四月上旬灌溉水量
    @ApiModelProperty(value = "四月上旬灌溉水量")
    private Double aprilEarlyOctoberIrrigationWaterVolume;

    //四月中旬灌溉面积
    @ApiModelProperty(value = "四月中旬灌溉面积")
    private Double aprilMidDayIrrigationArea;

    //四月中旬灌溉水量
    @ApiModelProperty(value = "四月中旬灌溉水量")
    private Double aprilMidDayIrrigationWaterVolume;

    //四月下旬灌溉面积
    @ApiModelProperty(value = "四月下旬灌溉面积")
    private Double aprilLateOctoberIrrigationArea;

    //四月下旬灌溉水量
    @ApiModelProperty(value = "四月下旬灌溉水量")
    private Double aprilLateOctoberIrrigationWaterVolume;

    
    private Double mayEarlyOctoberIrrigationArea;

    
    private Double mayEarlyOctoberIrrigationWaterVolume;

    
    private Double mayMidDayIrrigationArea;

    
    private Double mayMidDayIrrigationWaterVolume;

    
    private Double mayLateOctoberIrrigationArea;

    
    private Double mayLateOctoberIrrigationWaterVolume;

    
    private Double juneEarlyOctoberIrrigationArea;

    
    private Double juneEarlyOctoberIrrigationWaterVolume;

    
    private Double juneMidDayIrrigationArea;

    
    private Double juneMidDayIrrigationWaterVolume;

    
    private Double juneLateOctoberIrrigationArea;

    
    private Double juneLateOctoberIrrigationWaterVolume;

    
    private Double julyEarlyOctoberIrrigationArea;

    
    private Double julyEarlyOctoberIrrigationWaterVolume;

    
    private Double julyMidDayIrrigationArea;

    
    private Double julyMidDayIrrigationWaterVolume;

    
    private Double julyLateOctoberIrrigationArea;

    
    private Double julyLateOctoberIrrigationWaterVolume;

    
    private Double augustEarlyOctoberIrrigationArea;

    
    private Double augustEarlyOctoberIrrigationWaterVolume;

    
    private Double augustMidDayIrrigationArea;

    
    private Double augustMidDayIrrigationWaterVolume;

    
    private Double augustLateOctoberIrrigationArea;

    
    private Double augustLateOctoberIrrigationWaterVolume;

    
    private Double septemberEarlyOctoberIrrigationArea;


    private Double septemberEarlyOctoberIrrigationWaterVolume;

    
    private Double septemberMidDayIrrigationArea;

    
    private Double septemberMidDayIrrigationWaterVolume;

    
    private Double septemberLateOctoberIrrigationArea;

    
    private Double septemberLateOctoberIrrigationWaterVolume;

    
    private Double octoberEarlyOctoberIrrigationArea;

    
    private Double octoberEarlyOctoberIrrigationWaterVolume;

    
    private Double octoberMidDayIrrigationArea;

    
    private Double octoberMidDayIrrigationWaterVolume;

    
    private Double octoberLateOctoberIrrigationArea;

    
    private Double octoberLateOctoberIrrigationWaterVolume;

    
    private Double novemberEarlyOctoberIrrigationArea;

    
    private Double novemberEarlyOctoberIrrigationWaterVolume;

    
    private Double novemberMidDayIrrigationArea;

    
    private Double novemberMidDayIrrigationWaterVolume;

    
    private Double novemberLateOctoberIrrigationArea;

    
    private Double novemberLateOctoberIrrigationWaterVolume;

    //累计灌溉面积
    @ApiModelProperty(value = "累计灌溉面积")
    private Double accumulatedIrrigationArea;

    //累计灌溉总量
    @ApiModelProperty(value = "累计灌溉总量")
    private Double accumulatedTotalIrrigationAmount;

    //灌水定额
    @ApiModelProperty(value = "灌水定额")
    private Double irrigationQuota;

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

