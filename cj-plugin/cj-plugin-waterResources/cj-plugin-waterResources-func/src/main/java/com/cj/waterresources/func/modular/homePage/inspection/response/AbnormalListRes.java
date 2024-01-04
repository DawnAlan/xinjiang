package com.cj.waterresources.func.modular.homePage.inspection.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AbnormalListRes {
    @ApiModelProperty(value = "站点")
    private String station;
    @ApiModelProperty(value = "时间")
    private Date time;
    @ApiModelProperty(value = "水位")
    private double waterLevel;
    @ApiModelProperty(value = "流量")
    private double flow;
}
