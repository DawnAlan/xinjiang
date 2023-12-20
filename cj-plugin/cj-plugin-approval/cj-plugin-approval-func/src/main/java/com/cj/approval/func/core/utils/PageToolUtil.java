package com.cj.approval.func.core.utils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageToolUtil {

    @ApiModelProperty(value = "当前页")
    private Integer pageNum;

    @ApiModelProperty(value = "页面大小")
    private Integer pageSize;
}
