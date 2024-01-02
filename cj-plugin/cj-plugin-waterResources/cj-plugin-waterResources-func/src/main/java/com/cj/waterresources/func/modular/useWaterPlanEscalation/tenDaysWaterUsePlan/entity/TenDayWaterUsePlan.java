package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 旬用水计划(TenDayWaterUsePlan)表实体类
 *
 * @author makejava
 * @since 2023-12-01 19:41:08
 */
@Data
public class TenDayWaterUsePlan extends Model<TenDayWaterUsePlan> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //灌区
    @ApiModelProperty(value = "灌区")
    private String irrigatedArea;

    //用水户
    @ApiModelProperty(value = "用水户")
    private String useWaterUser;

    //作物类型
    @ApiModelProperty(value = "作物类型")
    private String cropType;

    //年
    @ApiModelProperty(value = "年")
    private Integer year;

    //月
    @ApiModelProperty(value = "月")
    private Integer month;

    //旬
    @ApiModelProperty(value = "旬")
    private String tenDays;

    //灌溉作物
    @ApiModelProperty(value = "灌溉作物")
    private String irrigatedCrop;

    //计划灌溉总面积
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "计划灌溉总面积")
    private Double totalPlannedIrrigatedArea;

    //已播种面积
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "已播种面积")
    private Double sowedArea;

    //本旬计划灌溉面积
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "本旬计划灌溉面积")
    private Double plannedIrrigationAreaForThisMonth;

    //灌溉定额
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "灌溉定额")
    private Double irrigationQuota;

    //本旬计划灌溉
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "本旬计划灌溉")
    private Double irrigationPlanForThisWeek;

    //总灌溉次数
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "总灌溉次数")
    private Double totalIrrigationFrequency;

    //本旬计划灌水次
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "本旬计划灌水次")
    private Double plannedWateringTimesForThisMonth;

    //本旬需水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "本旬需水量")
    private Double waterDemandForThisMonth;

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

