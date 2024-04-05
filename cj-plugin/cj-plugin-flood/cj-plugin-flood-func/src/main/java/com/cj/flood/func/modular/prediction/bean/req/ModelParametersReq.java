package com.cj.flood.func.modular.prediction.bean.req;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ModelParametersReq {

    @ApiModelProperty(value = "配水开始时间")
    private Date startTime;

    @ApiModelProperty(value = "配水结束时间")
    private Date endTime;

    @ApiModelProperty(value = "断面名称")
    private String siteName;
}
