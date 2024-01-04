package com.cj.project.modular.FiducialEnterA.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("config_projectfield")
public class ConfigProjectField {
    /** Id */
    @TableId
    @ApiModelProperty(value = "Id", position = 1)
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

    /** 字段 */
    @ApiModelProperty(value = "字段", position = 5)
    private String fieldKey;

    /** 源字段 */
    @ApiModelProperty(value = "源字段", position = 5)
    private String sourceFieldName;

    /** 源字段分类 */
    @ApiModelProperty(value = "源字段分类", position = 5)
    private String sourceFieldType;


}
