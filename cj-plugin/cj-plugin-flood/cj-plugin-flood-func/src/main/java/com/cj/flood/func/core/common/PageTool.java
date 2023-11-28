package com.cj.flood.func.core.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageTool {

    @ApiModelProperty(value = "当前页")
    private Integer pageNum;

    @ApiModelProperty(value = "页面大小")
    private Integer pageSize;
}
