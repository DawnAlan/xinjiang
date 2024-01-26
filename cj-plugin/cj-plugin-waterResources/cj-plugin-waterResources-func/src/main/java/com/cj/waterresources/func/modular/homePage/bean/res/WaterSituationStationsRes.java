package com.cj.waterresources.func.modular.homePage.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterSituationStationsRes {
    @ApiModelProperty(value = "单位id")
    private String unitId;
    @ApiModelProperty(value = "单位名称")
    private String unitName;
}
