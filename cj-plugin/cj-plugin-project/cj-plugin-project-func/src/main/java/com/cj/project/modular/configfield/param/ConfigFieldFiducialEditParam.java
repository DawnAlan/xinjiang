package com.cj.project.modular.configfield.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 考证字段配置编辑参数
 *
 * @author Lb
 * @date  2023/08/31 19:28
 **/
@Getter
@Setter
public class ConfigFieldFiducialEditParam {

    /** Id */
    @ApiModelProperty(value = "Id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 项目编号 */
    @ApiModelProperty(value = "项目编号", position = 2)
    private String projectCode;

    /** 仪器类型 */
    @ApiModelProperty(value = "仪器类型", position = 3)
    private String instrumentType;

    /** 平台仪器类型 */
    @ApiModelProperty(value = "平台仪器类型", position = 4)
    private String instrumentMetaType;

    /** 字段释义 */
    @ApiModelProperty(value = "字段释义", position = 5)
    private String fieldText;

    /** 字段标识 */
    @ApiModelProperty(value = "字段标识", position = 6)
    private String fieldKey;

    /** 字段分类 */
    @ApiModelProperty(value = "字段分类", position = 7)
    private String fieldConfig;

    /** 是否显示 */
    @ApiModelProperty(value = "是否显示", position = 8)
    private String isDisplay;

    /** 排序 */
    @ApiModelProperty(value = "排序", position = 9)
    private Integer sortCode;

    /** 字段类型 */
    @ApiModelProperty(value = "字段类型", position = 10)
    private String systemType;

    /** 配置 */
    @ApiModelProperty(value = "配置", position = 11)
    private String extJson;

}
