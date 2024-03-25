package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TenDayWaterUsePlanImportTableReq {

    //作物种类
    @ApiModelProperty(value = "作物种类")
    @Excel(name = "作物种类", width = 15)
    private String cropType;

    //作物名称
    @ApiModelProperty(value = "作物名称")
    @Excel(name = "作物名称", width = 15)
    private String irrigatedCrop;

    //灌溉面积
    @ApiModelProperty(value = "灌溉面积")
    @Excel(name = "灌溉面积", width = 15)
    private Double irrigatedArea;

    //灌溉定额
    @ApiModelProperty(value = "灌溉定额")
    @Excel(name = "灌溉定额", width = 15)
    private Double irrigatedQuota;

    //灌溉次数
    @ApiModelProperty(value = "灌溉次数")
    @Excel(name = "灌溉次数", width = 15)
    private Double irrigationCount;

    //灌水定额
    @ApiModelProperty(value = "灌水定额")
    @Excel(name = "灌水定额", width = 15)
    private Double irrigationQuota;

    //需水量
    @ApiModelProperty(value = "需水量")
    @Excel(name = "需水量", width = 15)
    private Double waterDemand;
}
