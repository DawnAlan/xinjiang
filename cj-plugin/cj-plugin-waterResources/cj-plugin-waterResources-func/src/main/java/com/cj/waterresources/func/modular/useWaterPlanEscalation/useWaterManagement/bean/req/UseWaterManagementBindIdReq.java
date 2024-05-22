package com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UseWaterManagementBindIdReq implements Serializable {

    private String id;
    
    //绑定A3ID
    @ApiModelProperty(value = "绑定A3ID")
    private String bindId;
}
