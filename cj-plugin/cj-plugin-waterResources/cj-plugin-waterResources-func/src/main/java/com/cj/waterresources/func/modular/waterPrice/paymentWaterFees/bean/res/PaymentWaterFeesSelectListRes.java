package com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class PaymentWaterFeesSelectListRes implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private String id;

    //管理站
    @ApiModelProperty(value = "管理站")
    private String station;

    //用水户名称
    @ApiModelProperty(value = "用水户名称")
    private String waterUserName;

    //缴费金额
    @ApiModelProperty(value = "缴费金额")
    private Double paymentAmount;
    
    //缴费时间
    @ApiModelProperty(value = "缴费时间")
    private Date paymentTime;

    //水费类型
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "水费类型")
    private String type;
}
