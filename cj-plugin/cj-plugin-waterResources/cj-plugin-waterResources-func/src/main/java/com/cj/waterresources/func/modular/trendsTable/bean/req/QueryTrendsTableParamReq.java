package com.cj.waterresources.func.modular.trendsTable.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryTrendsTableParamReq {

    @ApiModelProperty(value = "使用类型(1-水情日报 2-水费管理 3-灌溉额度)")
    private Integer useType;

    @ApiModelProperty(value = "使用水库或站点")
    private String useStation;
}
