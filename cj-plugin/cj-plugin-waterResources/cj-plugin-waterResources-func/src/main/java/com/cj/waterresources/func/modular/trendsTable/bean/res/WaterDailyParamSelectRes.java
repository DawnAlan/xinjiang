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

    @ApiModelProperty(value = "区域（1、昌吉 2、十二师 3、乌鲁木齐）")
    private Integer area;

    @ApiModelProperty(value = "类别（1、流量  2、水位 3、库容  4、浊度）")
    private Integer category;

    private List<WaterDailyParamSelectRes> children;
}
