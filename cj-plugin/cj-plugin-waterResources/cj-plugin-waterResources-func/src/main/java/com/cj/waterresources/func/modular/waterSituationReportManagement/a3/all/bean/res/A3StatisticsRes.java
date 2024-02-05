package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class A3StatisticsRes implements Serializable {

    @ApiModelProperty(value = "流量值")
    private Double v;

    @ApiModelProperty(value = "名称")
    private String paramName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "时间")
    private Date recordTime;
}
