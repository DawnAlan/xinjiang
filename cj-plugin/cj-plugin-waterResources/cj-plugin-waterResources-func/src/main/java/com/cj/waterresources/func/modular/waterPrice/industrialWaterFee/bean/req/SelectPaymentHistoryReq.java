package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SelectPaymentHistoryReq {
    @NotNull
    @ApiModelProperty("站点编号")
    private String siteCode;
    @NotNull
    @ApiModelProperty("站点名称")
    private String siteName;
}
