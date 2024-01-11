package com.cj.project.api.fiducial.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 测点考证表查询参数
 *
 * @author Lb
 * @date  2023/09/04 12:25
 **/
@Getter
@Setter
public class FiducialQueryParam {

    /** 项目编号 */
    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    /** 项目仪器类型 */
    @ApiModelProperty(value = "项目仪器类型")
    private String instrumentType;

    /** 测点序列 */
    @ApiModelProperty(value = "测点ID序列")
    private List<String> points;
}
