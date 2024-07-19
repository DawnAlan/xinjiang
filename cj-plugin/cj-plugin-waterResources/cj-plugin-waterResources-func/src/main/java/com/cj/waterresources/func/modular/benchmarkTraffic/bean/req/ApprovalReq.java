package com.cj.waterresources.func.modular.benchmarkTraffic.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApprovalReq {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "审批状态（0-待审核  1-通过  2-拒绝 ）")
    private Integer status;
}
