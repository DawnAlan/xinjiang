package com.cj.waterresources.func.modular.trendsTable.bean.req;

import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TrendsTableParamUpdateReq implements Serializable {

    @ApiModelProperty(value = "表头")
    private TrendsTableParam param;

    @ApiModelProperty(value = "用水类型")
    private String useWaterType;


}
