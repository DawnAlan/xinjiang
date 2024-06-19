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

    @ApiModelProperty(value = "是否一键生成模型")
    private Boolean isAuto;

    //楼庄子起调水位
    @ApiModelProperty(value = "楼庄子起调水位")
    private Double levelBeginLzz;

    //头屯河起调水位
    @ApiModelProperty(value = "头屯河起调水位")
    private Double levelBeginTth;

    //楼庄子期末水位
    @ApiModelProperty(value = "楼庄子期末水位")
    private Double levelEndLzz;

    //头屯河期末水位
    @ApiModelProperty(value = "头屯河期末水位")
    private Double levelEndTth;

}
