package com.cj.waterresources.func.modular.waterResourceAllcation.bean.req;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ViewModelReq implements Serializable {

    //方案名称
    @ApiModelProperty(value = "方案名称")
    private String allocationDataCustomAddress;

    //配水开始时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "配水开始时间")
    private Date waterDistributionStartTime;

    //配水结束时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "配水结束时间")
    private Date waterDistributionEndTime;

}
