package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LzzReportFormsRes implements Serializable {

    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "进库流量-今年")
    private Double inputFlowThisYear;

    @ApiModelProperty(value = "进库流量-去年")
    private Double inputFlowLastYear;

    @ApiModelProperty(value = "出库流量-今年")
    private Double outputFlowThisYear;

    @ApiModelProperty(value = "出库流量-去年")
    private Double outputFlowLastYear;

    @ApiModelProperty(value = "库水位")
    private Double waterLevel;

    @ApiModelProperty(value = "库容")
    private Double storageCapacity;

    @ApiModelProperty(value = "水厂流量-今年")
    private Double scThisYear;

    @ApiModelProperty(value = "水厂流量-去年")
    private Double scLastYear;

}
