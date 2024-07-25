package com.cj.flood.func.modular.prediction.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ModelParameterDetailReq {
//    @ApiModelProperty(value = "页码")
//    private Integer pageNo;
//
//    @ApiModelProperty(value = "分页大小")
//    private Integer pageSize;

    @ApiModelProperty(value = "modelId")
    private String modelId;
}
