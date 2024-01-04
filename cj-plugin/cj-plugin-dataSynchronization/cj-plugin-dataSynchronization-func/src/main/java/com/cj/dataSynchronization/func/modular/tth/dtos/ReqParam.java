package com.cj.dataSynchronization.func.modular.tth.dtos;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ReqParam implements Serializable {

    @ApiModelProperty(value = "是否是当前时间(1-是 2-否)")
    private Integer flag;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "时间")
    private String time;
}
