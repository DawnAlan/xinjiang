package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 头屯河水库蓄水调度计划表(WaterStorageSchedulingTth)表实体类
 *
 * @author makejava
 * @since 2023-12-12 10:20:45
 */
@Data
public class WaterStorageSchedulingTth extends Model<WaterStorageSchedulingTth> {
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

    //调蓄水量-多余
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "调蓄水量-多余")
    private Double regulatingWaterStorageCapacitySurplus;

    //调蓄水量-不足
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "调蓄水量-不足")
    private Double regulatingWaterStorageCapacityInsuffcient;

    //蓄水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "蓄水量")
    private Double waterStorage;

    //供水量-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "供水量-合计")
    private Double waterSupplyVolumeTotal;

    //工业需水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "工业需水量")
    private Double industryWaterDemand;

    //灌溉需水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "灌溉需水量")
    private Double irrigationWaterDemand;

    //生态基流
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "生态基流")
    private Double ecologicalBaseFlow;

    //蓄水位
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "蓄水位")
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

}

