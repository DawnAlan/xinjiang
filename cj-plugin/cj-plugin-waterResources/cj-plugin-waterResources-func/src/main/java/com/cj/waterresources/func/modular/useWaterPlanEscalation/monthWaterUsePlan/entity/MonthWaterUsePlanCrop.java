package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 月用水计划作物表(MonthWaterUsePlanCrop)表实体类
 *
 * @author makejava
 * @since 2024-03-23 17:05:34
 */
@Data
public class MonthWaterUsePlanCrop extends Model<MonthWaterUsePlanCrop> {

    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

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
    @ApiModelProperty(value = "灌溉面积")
    private Double irrigatedArea;

    //灌溉定额
    @ApiModelProperty(value = "灌溉定额")
    private Double irrigatedQuota;

    //月灌溉次数
    @ApiModelProperty(value = "月灌溉次数")
    private Double monthIrrigatedCount;

    //需水量
    @ApiModelProperty(value = "需水量")
    private Double waterDemand;

    //上旬次数
    @ApiModelProperty(value = "上旬次数")
    private Double earlyOctoberCount;

    //中旬次数
    @ApiModelProperty(value = "中旬次数")
    private Double midDayCount;

    //下旬次数
    @ApiModelProperty(value = "下旬次数")
    private Double laterOctoberCount;

    //本旬合计次数
    @ApiModelProperty(value = "本旬合计次数")
    private Double totalCount;

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

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;

    //区域
    @ApiModelProperty(value = "区域")
    private String area;

    //灌水定额
    @ApiModelProperty(value = "灌水定额")
    private Double irrigationQuota;

    //单位ID
    @ApiModelProperty(value = "单位ID")
    private String unitId;

    //月份
    @ApiModelProperty(value = "月份")
    private Integer month;

    //上旬需水
    @ApiModelProperty(value = "上旬需水")
    private Double earlyOctoberWaterDemand;

    //中旬需水
    @ApiModelProperty(value = "中旬需水")
    private Double midDayWaterDemand;

    //下旬需水
    @ApiModelProperty(value = "下旬需水")
    private Double laterOctoberWaterDemand;

    //本旬合计需水
    @ApiModelProperty(value = "本旬合计需水")
    private Double totalCountWaterDemand;
}

