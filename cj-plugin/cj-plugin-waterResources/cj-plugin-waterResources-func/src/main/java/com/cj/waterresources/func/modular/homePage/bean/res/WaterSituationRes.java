package com.cj.waterresources.func.modular.homePage.bean.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterSituationRes {
    @ApiModelProperty(value = "站点")
    private String station;
    @ApiModelProperty(value = "时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    @ApiModelProperty(value = "水位")
    private String waterLevel;
    @ApiModelProperty(value = "流量")
    private String flow;
}
