package com.cj.waterresources.func.modular.trendsTable.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WaterDailyParamSelectRes implements Serializable {

    private String id;

    private String pId;

    private String paramName;

    private String paramCode;

    private String isParent;

    private Integer orderNum;

    @ApiModelProperty(value = "用水类型")
    private String useWaterType;

    private List<WaterDailyParamSelectRes> children;
}
