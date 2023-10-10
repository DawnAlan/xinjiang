package com.cj.project.modular.projects.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ProjectProjectsQueryParam {
    /** 编码 */
    @ApiModelProperty(value = "编码", required = true)
    private String code;

    /** 名称 */
    @ApiModelProperty(value = "名称")
    private String name;
}
