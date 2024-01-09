package com.cj.fourPredictions.func.modular.waterResource.forecastPrediction.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PredictionListByNameReq implements Serializable {

    @ApiModelProperty(value = "来水预报列表id")
    private String id;

    @ApiModelProperty(value = "预报断面")
    private String reservoir;
}
