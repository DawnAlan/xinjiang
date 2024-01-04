package com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.req;

import com.cj.waterresources.func.core.utils.PageToolUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class PaymentWaterFeesSelectListReq extends PageToolUtil implements Serializable {


    //管理站
    @ApiModelProperty(value = "管理站")
    private String station;

    //用水户名称
    @ApiModelProperty(value = "用水户名称")
    private String waterUserName;

    //缴费开始时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "缴费开始时间")
    private Date paymentStartTime;

    //缴费结束时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "缴费结束时间")
    private Date paymentEndTime;
}
