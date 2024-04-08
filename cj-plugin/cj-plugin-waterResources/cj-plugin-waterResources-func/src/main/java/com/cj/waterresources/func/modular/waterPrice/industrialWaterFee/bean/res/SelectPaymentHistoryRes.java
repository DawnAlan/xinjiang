package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SelectPaymentHistoryRes {

    @ApiModelProperty("农业水费")
    private Double agriculturalWaterPrice;
    //非农业水费

    @ApiModelProperty("非农业水费")
    private Double notAgriculturalWaterPrice;

    @ApiModelProperty("工业水价")
    private Double industrialWaterPrice;

    @ApiModelProperty("水资源征收标准")
    private Double waterResourceTaxes;
    @ApiModelProperty("农业水量")
    private Integer agriculturalProportion = 30;
    @ApiModelProperty("工业水量")
    private Integer industrialProportion = 70;
    //非农业水量
    @ApiModelProperty("非农业水量")
    private Integer notAgriculturalProportion = 0;
}
