package com.cj.waterresources.func.modular.waterResourceAllcation.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WaterResourceAllocationQueryReq implements Serializable {

    @ApiModelProperty(value = "模型类型")
    private Integer bucketType;

    @ApiModelProperty(value = "方案名称")
    private String planName;

    @ApiModelProperty(value = "时间")
    private Date dateTime;

    @ApiModelProperty(value = "页数")
    private Integer pageNo;

    @ApiModelProperty(value = "页大小")
    private Integer pageSize;
}
