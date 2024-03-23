package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class YearCropImportTableReq {

    //灌溉作物
    @Excel(name = "作物名称", width = 15)
    private String irrigatedCrop;

    //作物类型
    @Excel(name = "作物种类", width = 15)
    private String cropType;

    //灌溉面积
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "灌溉面积", width = 15)
    private Double irrigatedArea;

    //灌溉定额
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "灌溉定额", width = 15)
    private Double irrigatedQuota;

    //灌溉次数
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "灌溉次数", width = 15)
    private Double yearIrrigatedCount;

    //总需水量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "总需水量", width = 15)
    private Double waterDemand;

    //灌水定额
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "灌水定额", width = 15)
    private Double irrigationQuota;


    //4月上旬次数
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "4月上旬次数", width = 15)
    private Double aprilEarlyOctoberCount;

    //4月上旬需水
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "4月上旬需水", width = 15)
    private Double aprilEarlyOctoberWaterDemand;

    //4月中旬次数
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "4月中旬次数", width = 15)
    private Double aprilMidDayCount;

    //4月中旬需水
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "4月中旬需水", width = 15)
    private Double aprilMidDayWaterDemand;

    //4月下旬次数
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "4月下旬次数", width = 15)
    private Double aprilLaterOctoberCount;

    //4月下旬需水
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "4月下旬需水", width = 15)
    private Double aprilLaterOctoberWaterDemand;

    //4月合计次数
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "4月合计次数", width = 15)
    private Double aprilTotalCount;

    //4月合计需水
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "4月合计需水", width = 15)
    private Double aprilTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "5月上旬次数", width = 15)
    private Double mayEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "5月上旬需水", width = 15)
    private Double mayEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "5月中旬次数", width = 15)
    private Double mayMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "5月中旬需水", width = 15)
    private Double mayMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "5月下旬次数", width = 15)
    private Double mayLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "5月下旬需水", width = 15)
    private Double mayLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "5月合计次数", width = 15)
    private Double mayTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "5月合计需水", width = 15)
    private Double mayTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "6月上旬次数", width = 15)
    private Double juneEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "6月上旬需水", width = 15)
    private Double juneEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "6月中旬次数", width = 15)
    private Double juneMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "6月中旬需水", width = 15)
    private Double juneMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "6月下旬次数", width = 15)
    private Double juneLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "6月下旬需水", width = 15)
    private Double juneLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "6月合计次数", width = 15)
    private Double juneTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "6月合计需水", width = 15)
    private Double juneTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "7月上旬次数", width = 15)
    private Double julyEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "7月上旬需水", width = 15)
    private Double julyEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "7月中旬次数", width = 15)
    private Double julyMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "7月中旬需水", width = 15)
    private Double julyMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "7月下旬次数", width = 15)
    private Double julyLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "7月下旬需水", width = 15)
    private Double julyLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "7月合计次数", width = 15)
    private Double julyTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "7月合计需水", width = 15)
    private Double julyTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "8月上旬次数", width = 15)
    private Double augustEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "8月上旬需水", width = 15)
    private Double augustEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "8月中旬次数", width = 15)
    private Double augustMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "8月中旬需水", width = 15)
    private Double augustMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "8月下旬次数", width = 15)
    private Double augustLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "8月下旬需水", width = 15)
    private Double augustLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "8月合计次数", width = 15)
    private Double augustTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "8月合计需水", width = 15)
    private Double augustTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "9月上旬次数", width = 15)
    private Double septemberEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "9月上旬需水", width = 15)
    private Double septemberEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "9月中旬次数", width = 15)
    private Double septemberMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "9月中旬需水", width = 15)
    private Double septemberMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "9月下旬次数", width = 15)
    private Double septemberLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "9月下旬需水", width = 15)
    private Double septemberLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "9月合计次数", width = 15)
    private Double septemberTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "9月合计需水", width = 15)
    private Double septemberTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "10月上旬次数", width = 15)
    private Double octoberEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "10月上旬需水", width = 15)
    private Double octoberEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "10月中旬次数", width = 15)
    private Double octoberMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "10月中旬需水", width = 15)
    private Double octoberMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "10月下旬次数", width = 15)
    private Double octoberLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "10月下旬需水", width = 15)
    private Double octoberLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "10月合计次数", width = 15)
    private Double octoberTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "10月合计需水", width = 15)
    private Double octoberTotalWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "11月上旬次数", width = 15)
    private Double novemberEarlyOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "11月上旬需水", width = 15)
    private Double novemberEarlyOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "11月中旬次数", width = 15)
    private Double novemberMidDayCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "11月中旬需水", width = 15)
    private Double novemberMidDayWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "11月下旬次数", width = 15)
    private Double novemberLaterOctoberCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "11月下旬需水", width = 15)
    private Double novemberLaterOctoberWaterDemand;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "11月合计次数", width = 15)
    private Double novemberTotalCount;


    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "11月合计需水", width = 15)
    private Double novemberTotalWaterDemand;
}
