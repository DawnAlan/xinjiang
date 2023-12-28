package com.cj.flood.func.modular.dispatch.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FloodControlOperationListRes implements Serializable {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "方案名称")
    private String schemeName;

    @ApiModelProperty(value = "归属洪水预报方案")
    private String programmeName;

    @ApiModelProperty(value = "制作人")
    private String createBy;

    @ApiModelProperty(value = "预报时间")
    private Date predictionTime;

    @ApiModelProperty(value = "状态")
    private Integer status;

}
