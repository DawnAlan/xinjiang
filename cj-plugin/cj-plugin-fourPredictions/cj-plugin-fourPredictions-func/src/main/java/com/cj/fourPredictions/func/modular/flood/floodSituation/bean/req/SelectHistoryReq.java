package com.cj.fourPredictions.func.modular.flood.floodSituation.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SelectHistoryReq implements Serializable {
    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;
}
