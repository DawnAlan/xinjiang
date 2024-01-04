package com.cj.flood.func.modular.homePage.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverviewRes {
    @ApiModelProperty(value = "水情预警")
    private Integer waterWarning;
    @ApiModelProperty(value = "浊度预警")
    private Integer turbidityWarning;
    @ApiModelProperty(value = "巡查反馈")
    private Integer inspectionFeedback;
}
