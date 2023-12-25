package com.cj.waterresources.func.modular.homePage.bean.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverviewRes {
    @ApiModelProperty(value = "水资源预警")
    private Integer waterResourceWarning;
    @ApiModelProperty(value = "设别异常")
    private Integer deviceAbnormality;
    @ApiModelProperty(value = "水费欠缴单位")
    private Integer unitUnpaidWaterFee;
    @ApiModelProperty(value = "巡查反馈")
    private Integer inspectionFeedback;
    @ApiModelProperty(value = "指令下达单位")
    private Integer instructionIssuingUnit;
    @ApiModelProperty(value = "指令未执行单位")
    private Integer instructionNotExecutedUnit;
}
