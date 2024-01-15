package com.cj.data.modular.dataResult.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 数据基础表Id参数
 *
 * @author Lb
 * @date  2023/10/13 08:59
 **/
@Getter
@Setter
public class DataRecordIdParam {

    /** ID */
    @ApiModelProperty(value = "ID", required = true)
    @NotBlank(message = "id不能为空")
    private String id;
}
