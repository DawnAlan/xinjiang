package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 楼庄子水库蓄水调度计划表(WaterStorageSchedulingLzz)表实体类
 *
 * @author makejava
 * @since 2024-02-18 09:35:47
 */
@Data
public class WaterStorageSchedulingLzz extends Model<WaterStorageSchedulingLzz> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //年份
    @ApiModelProperty(value = "年份")
    private Integer year;

    //月份
    @ApiModelProperty(value = "月份")
    private Integer month;

    //旬
    @ApiModelProperty(value = "旬")
    private String tenDays;

    //入库水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "入库水量")
    private Double reservoirInflow;

    //调蓄水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "调蓄水量")
    private Double regulatingWaterStorageCapacity;

    //蓄水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "蓄水量")
    private Double waterStorage;

    //供水量-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "供水量-合计")
    private Double waterSupplyVolumeTotal;

    //头屯河水库需水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河水库需水量")
    private Double reservoirWaterDemand;

    //楼庄子水厂需水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子水厂需水量")
    private Double waterPlantDemand;

    //楼庄子蓄水位
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子蓄水位")
    private Double waterStorageLevel;

    //备注
    @ApiModelProperty(value = "备注")
    private String remark;

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

    //逻辑删除(0-正常 1-删除)
    @ApiModelProperty(value = "逻辑删除(0-正常 1-删除)")
    private Integer del;

    //微调
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "微调")
    private Double fineTuning;

    //微调后入库水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "微调后入库水量")
    private Double fineTuningReservoirInflow;

    //总表id
    @ApiModelProperty(value = "总表id")
    private String formId;

    @ApiModelProperty(value = "排序字段")
    private Integer sortNum;

    //损失水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "损失水量")
    private Double waterLoss;
}

