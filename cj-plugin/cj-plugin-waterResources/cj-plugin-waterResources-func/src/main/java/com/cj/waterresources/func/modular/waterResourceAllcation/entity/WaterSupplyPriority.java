package com.cj.waterresources.func.modular.waterResourceAllcation.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WaterSupplyPriority {
    @ApiModelProperty(value = "供水类型")
    private String priorityType;
    @ApiModelProperty(value = "供水优先级,从小到大")
    private Integer priorityIndex;
    @ApiModelProperty(value = "优先级值")
    private Integer priorityValue;
    @ApiModelProperty(value = "锁定,1不允许修改优先级")
    private Integer isBlock;
}
