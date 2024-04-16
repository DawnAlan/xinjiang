package com.cj.waterresources.func.modular.homePage.inspection.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    @ApiModelProperty(value = "水位")
    private double waterLevel;
    @ApiModelProperty(value = "流量")
    private double flow;
}
