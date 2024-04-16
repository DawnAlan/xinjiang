package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MonthCropImportTableReq {

    //灌溉作物
    @ApiModelProperty(value = "灌溉作物")
    @Excel(name = "作物名称", width = 15)
    private String irrigatedCrop;

    //作物类型
    @ApiModelProperty(value = "作物类型")
    @Excel(name = "作物种类", width = 15)
    private String cropType;

    //灌溉面积
    @ApiModelProperty(value = "灌溉面积")
    @Excel(name = "灌溉面积", width = 15)
    private Double irrigatedArea;

    //灌溉定额
    @ApiModelProperty(value = "灌溉定额")
    @Excel(name = "灌溉定额", width = 15)
    private Double irrigatedQuota;

    //月灌溉次数
    @ApiModelProperty(value = "灌溉次数")
    @Excel(name = "灌溉次数", width = 15)
    private Double monthIrrigatedCount;

    //灌水定额
    @ApiModelProperty(value = "灌水定额")
    @Excel(name = "灌水定额", width = 15)
    private Double irrigationQuota;

    //需水量
    @ApiModelProperty(value = "总需水量")
    @Excel(name = "总需水量", width = 15)
    private Double waterDemand;

    //上旬次数
    @ApiModelProperty(value = "上旬次数")
    @Excel(name = "上旬次数", width = 15)
    private Double earlyOctoberCount;

    //中旬次数
    @ApiModelProperty(value = "中旬次数")
    @Excel(name = "中旬次数", width = 15)
    private Double midDayCount;

    //下旬次数
    @ApiModelProperty(value = "下旬次数")
    @Excel(name = "下旬次数", width = 15)
    private Double laterOctoberCount;

    //上旬需水
    @ApiModelProperty(value = "上旬需水")
    @Excel(name = "上旬需水", width = 15)
    private Double earlyOctoberWaterDemand;

    //中旬需水
    @ApiModelProperty(value = "中旬需水")
    @Excel(name = "中旬需水", width = 15)
    private Double midDayWaterDemand;

    //下旬需水
    @ApiModelProperty(value = "下旬需水")
    @Excel(name = "下旬需水", width = 15)
    private Double laterOctoberWaterDemand;
}
