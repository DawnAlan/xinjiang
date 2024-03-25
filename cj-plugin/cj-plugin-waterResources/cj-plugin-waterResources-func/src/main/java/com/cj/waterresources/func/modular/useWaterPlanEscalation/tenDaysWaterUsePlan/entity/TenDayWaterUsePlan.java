package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 旬用水计划(TenDayWaterUsePlan)表实体类
 *
 * @author makejava
 * @since 2024-03-25 16:54:21
 */
@Data
public class TenDayWaterUsePlan extends Model<TenDayWaterUsePlan> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //灌区
    @ApiModelProperty(value = "灌区")
    private String area;

    //用水户
    @ApiModelProperty(value = "用水户")
    private String useWaterUser;

    //作物种类
    @ApiModelProperty(value = "作物种类")
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

    //作物名称
    @ApiModelProperty(value = "作物名称")
    private String irrigatedCrop;

    //灌溉面积
    @ApiModelProperty(value = "灌溉面积")
    private Double irrigatedArea;

    //灌溉定额
    @ApiModelProperty(value = "灌溉定额")
    private Double irrigatedQuota;

    //灌溉次数
    @ApiModelProperty(value = "灌溉次数")
    private Double irrigationCount;

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

    //灌水定额
    @ApiModelProperty(value = "灌水定额")
    private Double irrigationQuota;

    //需水量
    @ApiModelProperty(value = "需水量")
    private Double waterDemand;
}

