package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SelectListForIndustrialWaterFeeReq implements Serializable {

    @ApiModelProperty(value = "管理站名称",required = true)
    private String name;

    @ApiModelProperty(value = "开始时间",required = true)
    private String startTime;

    @ApiModelProperty(value = "结束时间",required = true)
    private String endTime;

    @ApiModelProperty(value = "此字段前端不传")
    private String headId;

    @ApiModelProperty(value = "此字段前端不传")
    private String headIds;
}
