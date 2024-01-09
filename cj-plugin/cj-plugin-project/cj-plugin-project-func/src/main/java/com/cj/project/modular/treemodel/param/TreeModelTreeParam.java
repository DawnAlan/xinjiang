
package com.cj.project.modular.treemodel.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 测点树参数
 *
 * @author lb
 * @date 2023/9/14 19:49
 */
@Getter
@Setter
public class TreeModelTreeParam {

    /**
     * 项目Code
     */
    @ApiModelProperty(value = "项目Code", position = 2)
    private String projectCode;

    /**
     * 测点树分类
     */
    @ApiModelProperty(value = "测点树分类", position = 3)
    private String category;

    /**
     * 绑定的测点id
     */
    @ApiModelProperty(value = "绑定的测点id", position = 4)
    private String pointId;
}
