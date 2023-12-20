package com.cj.approval.func.modular.approval.approvalManagement.bean.req;

import com.cj.approval.func.core.utils.PageToolUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SelectListReq extends PageToolUtil implements Serializable {

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "指令类型")
    private String instructionType;
}
