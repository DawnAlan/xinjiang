package com.cj.flood.func.modular.prediction.bean.req;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class WaterResourceAllocationTimeReq {
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "配水开始时间")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "配水结束时间")
    private Date endTime;
}
