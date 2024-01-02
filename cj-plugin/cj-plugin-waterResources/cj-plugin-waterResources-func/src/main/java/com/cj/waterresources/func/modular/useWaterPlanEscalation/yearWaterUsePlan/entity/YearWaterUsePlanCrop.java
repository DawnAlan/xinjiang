package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 作物年用水计划(YearWaterUsePlanCrop)表实体类
 *
 * @author makejava
 * @since 2023-12-01 18:26:28
 */
@Data
public class YearWaterUsePlanCrop extends Model<YearWaterUsePlanCrop> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;

    //区域
    @ApiModelProperty(value = "区域")
    private String area;

    //单位
    @ApiModelProperty(value = "单位")
    private String unit;

    //灌溉作物
    @ApiModelProperty(value = "灌溉作物")
    private String irrigatedCrop;

    //作物类型
    @ApiModelProperty(value = "作物类型")
    private String cropType;

    //灌溉面积
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "灌溉面积")
    private Double irrigatedArea;

    //灌溉定额
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "灌溉定额")
    private Double irrigatedQuota;

    //年灌溉次数
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "年灌溉次数")
    private Double yearIrrigatedCount;

    //需水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "需水量")
    private Double waterDemand;

    //四月-上旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "四月-上旬")
    private Double aprilEarlyOctober;

    //四月-中旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "四月-中旬")
    private Double aprilMidDay;

    //四月-下旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "四月-下旬")
    private Double aprilLaterOctober;

    //四月-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "四月-合计")
    private Double aprilTotal;

    //五月-上旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "五月-上旬")
    private Double mayEarlyOctober;

    //五月-中旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "五月-中旬")
    private Double mayMidDay;

    //五月-下旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "五月-下旬")
    private Double mayLaterOctober;

    //五月-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "月-合计")
    private Double mayTotal;

    //六月-上旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "六月-上旬")
    private Double juneEarlyOctober;

    //六月-中旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "六月-中旬")
    private Double juneMidDay;

    //六月-下旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "六月-下旬")
    private Double juneLaterOctober;

    //六月-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "六月-合计")
    private Double juneTotal;

    //七月-上旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "七月-上旬")
    private Double julyEarlyOctober;

    //七月-中旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "七月-中旬")
    private Double julyMidDay;

    //七月-下旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "七月-下旬")
    private Double julyLaterOctober;

    //七月-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "七月-合计")
    private Double julyTotal;

    //八月-上旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "八月-上旬")
    private Double augustEarlyOctober;

    //八月-中旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "八月-中旬")
    private Double augustMidDay;

    //八月-下旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "八月-下旬")
    private Double augustLaterOctober;

    //八月-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "八月-合计")
    private Double augustTotal;

    //九月-上旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "九月-上旬")
    private Double septemberEarlyOctober;

    //九月-中旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "九月-中旬")
    private Double septemberMidDay;

    //九月-下旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "九月-下旬")
    private Double septemberLaterOctober;

    //九月-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "九月-合计")
    private Double septemberTotal;

    //十月-上旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十月-上旬")
    private Double octoberEarlyOctober;

    //十月-中旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十月-中旬")
    private Double octoberMidDay;

    //十月-下旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十月-下旬")
    private Double octoberLaterOctober;

    //十月-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十月-合计")
    private Double octoberTotal;

    //十一月-上旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十一月-上旬")
    private Double novemberEarlyOctober;

    //十一月-中旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十一月-中旬")
    private Double novemberMidDay;

    //十一月-下旬
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十一月-下旬")
    private Double novemberLaterOctober;

    //十一月-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十一月-合计")
    private Double novemberTotal;

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

    //灌水定额
    @ApiModelProperty(value = "灌水定额")
    private Double irrigationQuota;

    //单位ID
    @ApiModelProperty(value = "单位ID")
    private String unitId;
}

