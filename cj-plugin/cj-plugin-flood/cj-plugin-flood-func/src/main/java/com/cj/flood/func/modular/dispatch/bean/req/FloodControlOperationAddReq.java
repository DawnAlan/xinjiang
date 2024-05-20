package com.cj.flood.func.modular.dispatch.bean.req;

import com.cj.flood.func.modular.prediction.entity.BasinParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class FloodControlOperationAddReq implements Serializable {

    @ApiModelProperty(value = "预报方案id")
    private String IncomingWaterForecastId;

    @ApiModelProperty(value = "楼庄子起调水位")
    private double H1_begin;

    @ApiModelProperty(value = "楼庄子期末控制水位")
    private double H1_end;

    @ApiModelProperty(value = "头屯河起调水位")
    private double H2_begin;

    @ApiModelProperty(value = "头屯河期末控制水位")
    private double H2_end;

    @ApiModelProperty(value = "楼庄子模型精度(0.05~0.2)")
    private double Step1;

    @ApiModelProperty(value = "头屯河模型精度(0.05~0.2)")
    private double Step2;

    @ApiModelProperty(value = "楼庄子各月份汛限水位，共12个")
    private double[] limitLevelsLzz;

    @ApiModelProperty(value = "头屯河各月份汛限水位，共12个")
    private double[] limitLevelsTth;

    @ApiModelProperty(value = "楼庄子各月份生态流量,供12个")
    private double[] ecosLzz;

    @ApiModelProperty(value = "头屯河各月份生态流量,供12个")
    private double[] ecosTth;

    @ApiModelProperty(value = "头屯河各月份生态流量,供12个")
    private BasinParam basinParam;
}
