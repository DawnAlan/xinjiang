package com.cj.flood.func.modular.prediction.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConfluenceTimeDto {
    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "值")
    private String value;
}
