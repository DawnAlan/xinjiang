package com.cj.project.modular.instruments.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 项目仪器表实体
 *
 * @author Lb
 * @date  2023/09/02 18:12
 **/
@Getter
@Setter
@TableName("project_instruments")
public class ProjectInstruments {

    /** id */
    @TableId
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 项目编号 */
    @ApiModelProperty(value = "项目编号", position = 2)
    private String projectCode;

    /** 监测类型 */
    @ApiModelProperty(value = "监测类型", position = 3)
    private String monitorName;

    /** 项目仪器类型 */
    @ApiModelProperty(value = "项目仪器类型", position = 4)
    private String instrumentType;

    /** 平台仪器类型 */
    @ApiModelProperty(value = "平台仪器类型", position = 5)
    private String instrumentMetaType;

    /** 仪器编码 */
    @ApiModelProperty(value = "仪器编码", position = 6)
    private String instrumentCode;

    /** 设计埋设量 */
    @ApiModelProperty(value = "设计埋设量", position = 7)
    private Integer designCount;

    /** 别名关键词 */
    @ApiModelProperty(value = "别名关键词", position = 8)
    private String nameKeys;
}
