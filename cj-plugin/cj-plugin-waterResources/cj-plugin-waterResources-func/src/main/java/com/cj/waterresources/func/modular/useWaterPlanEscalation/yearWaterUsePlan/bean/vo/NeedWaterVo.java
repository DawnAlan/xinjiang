package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NeedWaterVo {

    //一月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "一月")
    private Double january;

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

    //十二月
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "十二月")
    private Double december;
}
