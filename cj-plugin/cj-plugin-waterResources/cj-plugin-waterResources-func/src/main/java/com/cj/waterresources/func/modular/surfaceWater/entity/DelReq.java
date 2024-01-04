package com.cj.waterresources.func.modular.surfaceWater.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DelReq {
    @ApiModelProperty(value = "ID列表")
    private List<String> ids;
}
