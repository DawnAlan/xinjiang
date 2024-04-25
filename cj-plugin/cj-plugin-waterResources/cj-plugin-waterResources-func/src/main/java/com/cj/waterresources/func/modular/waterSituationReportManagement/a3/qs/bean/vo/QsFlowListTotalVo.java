package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class QsFlowListTotalVo implements Serializable {

    //表头id
    @ApiModelProperty(value = "表头id")
    private String tableHeaderId;

    //本旬水量
    @ApiModelProperty(value = "本旬水量")
    private Double currentWaterVolume;

    //本旬流量
    @ApiModelProperty(value = "本旬流量")
    private Double currentWaterFlow;

    //累计水量
    @ApiModelProperty(value = "累计水量")
    private Double accumulatedWaterVolume;

    //去年同期水量
    @ApiModelProperty(value = "去年同期水量")
    private Double waterVolumeDuringLastYear;
}
