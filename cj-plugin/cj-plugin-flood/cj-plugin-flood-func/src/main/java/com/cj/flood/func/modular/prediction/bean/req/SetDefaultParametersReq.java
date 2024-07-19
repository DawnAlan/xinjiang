package com.cj.flood.func.modular.prediction.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SetDefaultParametersReq {
    @ApiModelProperty(value = "断面名称")
    private String siteName;
    @ApiModelProperty(value = "modelId")
    private String modelId;
}
