package com.cj.project.modular.configfield.result;

import com.cj.project.modular.configfield.entity.ConfigFieldData;
import com.cj.project.modular.configfield.entity.ConfigFieldFiducial;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * 数据字段配置结果
 *
 * @author Lb
 * @date  2023/11/08 11:28
 **/
@Getter
@Setter
public class ConfigFieldDataResult {

    /** 项目编号 */
    @ApiModelProperty(value = "项目编号", position = 1)
    private String projectCode;

    /** 仪器类型 */
    @ApiModelProperty(value = "仪器类型", position = 2)
    private String instrumentType;

    /** 平台仪器类型 */
    @ApiModelProperty(value = "平台仪器类型", position = 3)
    private String instrumentMetaType;

    /** 数据字段配置 */
    @ApiModelProperty(value = "数据字段配置", position = 4)
    List<ConfigFieldData> fieldConfigs;



}
