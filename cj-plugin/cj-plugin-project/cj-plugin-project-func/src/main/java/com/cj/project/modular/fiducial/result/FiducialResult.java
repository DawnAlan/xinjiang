package com.cj.project.modular.fiducial.result;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 考证查询结果
 *
 * @author lb
 * @date 2023/9/4 9:28
 **/
@Getter
@Setter
public class FiducialResult {

    /** ID */
    @ApiModelProperty(value = "ID", required = true, position = 1)
    private String id;

    /** 项目编号 */
    @ApiModelProperty(value = "项目编号", required = true, position = 2)
    private String projectCode;

    /** 项目仪器类型 */
    @ApiModelProperty(value = "项目仪器类型", required = true, position = 3)
    private String instrumentType;

    /** 测点名称 */
    @ApiModelProperty(value = "测点名称", required = true, position = 5)
    private String pointName;

    /** 测点别名 */
    @ApiModelProperty(value = "测点别名", position = 6)
    private String pointAlias;

    /** 测点考证详情 */
    @ApiModelProperty(value = "测点考证详情", position = 7)
    private Map<String, Object> detail;

}
