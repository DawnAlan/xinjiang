package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SelectInfoByIrrigationNameListRes implements Serializable {

    @ApiModelProperty(value = "监测点")
    private String monitorName;

    @ApiModelProperty(value = "瞬时流量")
    private Double sqMonitorFlow;
}
