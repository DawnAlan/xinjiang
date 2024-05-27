package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 干渠年用水计划(YearWaterUsePlanTrunkCanal)表实体类
 *
 * @author makejava
 * @since 2023-12-01 18:26:47
 */
@Data
public class YearWaterUsePlanTrunkCanal extends Model<YearWaterUsePlanTrunkCanal> {
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

    //二月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "二月")
    private Double february;

    //三月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "三月")
    private Double march;

    //四月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "四月")
    private Double april;

    //五月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "五月")
    private Double may;

    //六月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "六月")
    private Double june;

    //七月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "七月")
    private Double july;

    //八月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "八月")
    private Double august;

    //九月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "九月")
    private Double september;

    //十月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十月")
    private Double october;

    //十一月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十一月")
    private Double november;

    //合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "合计")
    private Double amountCount;

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


    //一月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "一月")
    private Double january;

    //十二月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十二月")
    private Double december;

    //用水计划
    @ApiModelProperty(value = "用水计划")
    private String useWaterPlan;

    //单位ID
    @ApiModelProperty(value = "单位ID")
    private String unitId;

    //绑定A3ID
    @ApiModelProperty(value = "绑定A3ID")
    private String bindId;
}

