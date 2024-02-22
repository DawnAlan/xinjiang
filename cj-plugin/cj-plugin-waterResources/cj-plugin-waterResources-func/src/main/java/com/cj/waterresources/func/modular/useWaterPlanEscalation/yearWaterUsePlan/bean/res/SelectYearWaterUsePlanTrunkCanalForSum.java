package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 干渠年用水计划(YearWaterUsePlanTrunkCanal)表实体类
 *
 * @author makejava
 * @since 2023-12-01 18:26:47
 */
@Data
public class SelectYearWaterUsePlanTrunkCanalForSum{

    //一月
    @ApiModelProperty(value = "一月")
    private Double january;

    //二月
    @ApiModelProperty(value = "二月")
    private Double february;

    //三月
    @ApiModelProperty(value = "三月")
    private Double march;

    //四月
    @ApiModelProperty(value = "四月")
    private Double april;

    //五月
    @ApiModelProperty(value = "五月")
    private Double may;

    //六月
    @ApiModelProperty(value = "六月")
    private Double june;

    //七月
    @ApiModelProperty(value = "七月")
    private Double july;

    //八月
    @ApiModelProperty(value = "八月")
    private Double august;

    //九月
    @ApiModelProperty(value = "九月")
    private Double september;

    //十月
    @ApiModelProperty(value = "十月")
    private Double october;

    //十一月
    @ApiModelProperty(value = "十一月")
    private Double november;

    //十二月
    @ApiModelProperty(value = "十二月")
    private Double december;
}

