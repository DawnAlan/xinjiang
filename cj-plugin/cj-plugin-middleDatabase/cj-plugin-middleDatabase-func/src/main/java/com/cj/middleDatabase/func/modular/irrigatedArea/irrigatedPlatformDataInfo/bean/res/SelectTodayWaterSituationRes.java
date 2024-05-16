package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectTodayWaterSituationRes {

    //监测点
    @ApiModelProperty(value = "监测点")
    private String name;

    //瞬时流量
    @ApiModelProperty(value = "瞬时流量")
    private Double value;

    //监测点id
    @ApiModelProperty(value = "监测点id")
    private String id;
}
