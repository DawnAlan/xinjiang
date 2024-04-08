package com.cj.flood.func.modular.dispatch.bean.req;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cj.flood.func.core.common.PageTool;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FloodControlOperationListReq extends PageTool implements Serializable {

    @ApiModelProperty(value = "方案名称")
    private String schemeName;

    @ApiModelProperty(value = "制作人")
    private String createBy;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "状态")
    private Integer status = null;

    @ApiModelProperty(value = "来水id")
    private String forecastingSchemeId;
}
