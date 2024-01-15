package com.cj.data.modular.dataResult.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 数据成果表编辑参数
 *
 * @author Lb
 * @date  2023/10/23 16:51
 **/
@Getter
@Setter
public class DataValueEditParam {

    /** ID */
    @ApiModelProperty(value = "ID", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

}
