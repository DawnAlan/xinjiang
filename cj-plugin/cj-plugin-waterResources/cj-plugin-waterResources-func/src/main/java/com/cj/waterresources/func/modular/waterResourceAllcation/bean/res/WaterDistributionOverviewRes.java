package com.cj.waterresources.func.modular.waterResourceAllcation.bean.res;

import com.cj.common.serializer.DoubleScale2Serializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Data
@ApiModel(value = "配水概览返回对象", description = "配水概览返回对象")
public class WaterDistributionOverviewRes {
    @ApiModelProperty(value = "来水预报总水量")
    @JsonSerialize(using = DoubleScale2Serializer.class)
    private Double incomingPreWaterAmount;
    @ApiModelProperty(value = "单位总需水量")
    @JsonSerialize(using = DoubleScale2Serializer.class)
    private Double unitNeedWaterAmount;
    @ApiModelProperty(value = "楼庄子生态流量")
    @JsonSerialize(using = DoubleScale2Serializer.class)
    private Double ecologyWaterAmountLzz;
    @ApiModelProperty(value = "头屯河生态流量")
    @JsonSerialize(using = DoubleScale2Serializer.class)
    private Double ecologyWaterAmountTth;
    @ApiModelProperty(value = "楼庄子需水量")
    @JsonSerialize(using = DoubleScale2Serializer.class)
    private Double yieldWaterAmountLzz;
    @ApiModelProperty(value = "头屯河需水量")
    @JsonSerialize(using = DoubleScale2Serializer.class)
    private Double yieldWaterAmountTth;
    @ApiModelProperty(value = "总弃水量")
    @JsonSerialize(using = DoubleScale2Serializer.class)
    private Double wasteWaterAmount;
    @ApiModelProperty(value = "可供水量利用率")
    @JsonSerialize(using = DoubleScale2Serializer.class)
    private Double waterAvailableRate;
    @ApiModelProperty(value = "供水")
    private List<WaterDto> proportionList;
    @ApiModelProperty(value = "缺额")
    private List<WaterDto> waterLackList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaterDto {
        @ApiModelProperty(value = "时间")
        private Date date;
        @ApiModelProperty(value = "水量")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double waterAmount;
    }
}
