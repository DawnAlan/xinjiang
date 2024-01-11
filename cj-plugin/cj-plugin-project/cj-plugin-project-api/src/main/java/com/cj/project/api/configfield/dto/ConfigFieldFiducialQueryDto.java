package com.cj.project.api.configfield.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 考证字段配置查询参数
 *
 * @author Lb
 * @date  2023/08/31 19:28
 **/
@Getter
@Setter
public class ConfigFieldFiducialQueryDto {

    /** 项目编号 */
    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    /** 仪器类型 */
    @ApiModelProperty(value = "仪器类型")
    private String instrumentType;

}
