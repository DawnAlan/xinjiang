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

    @ApiModelProperty(value = "制作时间")
    private Date createTime;

    @ApiModelProperty(value = "预报时间")
    private Date predictionTime;

    @ApiModelProperty(value = "预报结束时间")
    private Date endTime;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

}
