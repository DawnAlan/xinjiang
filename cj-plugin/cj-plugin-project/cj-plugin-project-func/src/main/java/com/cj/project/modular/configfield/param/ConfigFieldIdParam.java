package com.cj.project.modular.configfield.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 字段配置Id参数
 *
 * @author Lb
 * @date  2023/08/31 19:28
 **/
@Getter
@Setter
public class ConfigFieldIdParam {

    /** Id */
    @ApiModelProperty(value = "Id", required = true)
    @NotBlank(message = "id不能为空")
    private String id;
}
