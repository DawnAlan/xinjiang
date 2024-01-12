
package com.cj.project.api.treemodel.dto;

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
public class TreeModelTreeDto {

    /**
     * 节点ID
     */
    @ApiModelProperty(value = "节点ID", position = 1)
    private String id;

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

    /**
     * 测点名称
     */
    @ApiModelProperty(value = "测点名称", position = 5)
    private String nodeName;

}
