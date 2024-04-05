package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SelectPaymentReq {
    @NotNull
    @ApiModelProperty("站点编号")
    private String siteCode;
    @NotNull
    @ApiModelProperty("站点名称")
    private String siteName;
    @NotNull
    @ApiModelProperty("年")
    private Integer year;
    @NotNull
    @ApiModelProperty("月")
    private Integer month;
    @NotNull
    @ApiModelProperty("农业水费")
    private Double agriculturalWaterPrice;
    @NotNull
    @ApiModelProperty("工业水价")
    private Double industrialWaterPrice;
    @NotNull
    @ApiModelProperty("水资源征收标准")
    private Double waterResourceTaxes;
    @NotNull
    @ApiModelProperty("农业水量")
    private Integer agriculturalProportion;
    @NotNull
    @ApiModelProperty("工业水量")
    private Integer industrialProportion;
}
