package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SelectListForIndustrialWaterFeeRes {

    @ApiModelProperty(value = "日期")
    private Date recordTime;

    @ApiModelProperty(value = "值")
    private Double flow;
}
