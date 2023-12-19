package com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.bean.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class DayWaterUsePlanSelectReq implements Serializable {
    //记录日期
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "记录日期")
    private Date recordTime;

    //区域
    @ApiModelProperty(value = "区域")
    private String area;
}
