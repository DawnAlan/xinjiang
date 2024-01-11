package com.cj.project.api.instruments.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 项目仪器表实体
 *
 * @author Lb
 * @date  2023/09/02 18:12
 **/
@Data
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

    /** 排序值 */
    @ApiModelProperty(value = "排序值", position = 7)
    private Integer sortCode;

    /** 设计埋设量 */
    @ApiModelProperty(value = "设计埋设量", position = 8)
    private Integer designCount;

    /** 别名关键词 */
    @ApiModelProperty(value = "别名关键词", position = 9)
    private String nameKeys;
}
