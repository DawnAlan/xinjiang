package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ApprovalTrafficOverviewTableAddReq implements Serializable {

    //方案名称
    @ApiModelProperty(value = "方案名称")
    private String name;
    //创建方案时间
    @ApiModelProperty(value = "创建方案时间")
    private String time;
    //模型id
    @ApiModelProperty(value = "模型id")
    private String modelId;

    @ApiModelProperty(value = "模型名称")
    private String modelName;
}
