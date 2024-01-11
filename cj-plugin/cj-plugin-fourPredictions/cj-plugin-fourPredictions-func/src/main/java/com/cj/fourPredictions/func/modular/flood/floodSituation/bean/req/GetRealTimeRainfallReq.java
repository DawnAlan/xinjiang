package com.cj.fourPredictions.func.modular.flood.floodSituation.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetRealTimeRainfallReq implements Serializable {

    @ApiModelProperty(value = "小时")
    private Integer hour;

    @ApiModelProperty(value = "时间")
    private String date;
}
