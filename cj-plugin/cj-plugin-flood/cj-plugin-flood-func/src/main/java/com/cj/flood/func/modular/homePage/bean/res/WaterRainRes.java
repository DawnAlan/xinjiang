package com.cj.flood.func.modular.homePage.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterRainRes {
    @ApiModelProperty(value = "站点")
    private String station;
    @ApiModelProperty(value = "数据")
    private String value;
}
