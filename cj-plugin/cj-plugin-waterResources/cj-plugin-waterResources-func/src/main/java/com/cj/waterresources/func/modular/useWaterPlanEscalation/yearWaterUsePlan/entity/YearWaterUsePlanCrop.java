package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity;

import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 作物年用水计划(YearWaterUsePlanCrop)表实体类
 *
 * @author makejava
 * @since 2024-03-22 17:47:57
 */
@Data
public class YearWaterUsePlanCrop extends Model<YearWaterUsePlanCrop> {

    //主键ID
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
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double irrigatedArea;

    //灌溉定额
    @ApiModelProperty(value = "灌溉定额")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double irrigatedQuota;

    //灌溉次数
    @ApiModelProperty(value = "灌溉次数")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double yearIrrigatedCount;

    //总需水量
    @ApiModelProperty(value = "总需水量")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double waterDemand;

    //创建时间
    private Date createTime;

    //创建人
    private String createBy;

    //更新时间
    private Date updateTime;

    //更新人
    private String updateBy;

    //逻辑删除(0-正常 1-删除)
    private Integer del;

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;

    //区域
    @ApiModelProperty(value = "区域")
    private String area;

    //灌水定额
    @ApiModelProperty(value = "灌水定额")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double irrigationQuota;

    //单位ID
    @ApiModelProperty(value = "单位ID")
    private String unitId;


    //4月上旬次数
    @ApiModelProperty(value = "4月上旬次数")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double aprilEarlyOctoberCount;

    //4月上旬需水
    @ApiModelProperty(value = "4月上旬需水")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double aprilEarlyOctoberWaterDemand;

    //4月中旬次数
    @ApiModelProperty(value = "4月中旬次数")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double aprilMidDayCount;

    //4月中旬需水
    @ApiModelProperty(value = "4月中旬需水")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double aprilMidDayWaterDemand;

    //4月下旬次数
    @ApiModelProperty(value = "4月下旬次数")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double aprilLaterOctoberCount;

    //4月下旬需水
    @ApiModelProperty(value = "4月下旬需水")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double aprilLaterOctoberWaterDemand;

    //4月合计次数
    @ApiModelProperty(value = "4月合计次数")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double aprilTotalCount;

    //4月合计需水
    @ApiModelProperty(value = "4月合计需水")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double aprilTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double mayTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double juneTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double julyTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double augustTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double septemberTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double octoberTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double novemberTotalWaterDemand;

    //绑定A3ID
    @ApiModelProperty(value = "绑定A3ID")
    private String bindId;
}

