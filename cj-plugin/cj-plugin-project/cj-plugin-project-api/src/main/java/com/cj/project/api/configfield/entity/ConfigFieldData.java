package com.cj.project.api.configfield.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据配置管理实体
 *
 * @author Lb
 * @date  2023/11/08 14:35
 **/
@Getter
@Setter
@TableName("config_fielddata")
public class ConfigFieldData {

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

    /** 显示名称 */
    @ApiModelProperty(value = "显示名称", position = 12)
    private String displayName;

    /** 数值单位 */
    @ApiModelProperty(value = "数值单位", position = 13)
    private String unit;

    /** 数值小数位 */
    @ApiModelProperty(value = "数值小数位", position = 14)
    private Integer decimals;

}
