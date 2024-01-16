package com.cj.project.modular.FiducialEnterA.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PointEnterResult {

    @ApiModelProperty(value = "successCount", position = 1)
    private int successCount;

    @ApiModelProperty(value = "failCount", position = 2)
    private int failCount;

    @ApiModelProperty(value = "仪器名称", position = 3)
    private String instrumentType;

    @ApiModelProperty(value = "测点名称", position = 3)
    private List<ResultPointName> resultPointList;

}

@Getter
@Setter
class ResultPointName {

    @ApiModelProperty(value = "测点名称", position = 1)
    private String pointName;

    @ApiModelProperty(value = "记录", position = 2)
    private String Reason;

}
