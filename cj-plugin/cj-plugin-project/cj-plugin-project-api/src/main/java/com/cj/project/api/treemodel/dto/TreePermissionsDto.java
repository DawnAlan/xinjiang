package com.cj.project.api.treemodel.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 树目录权限组基本信息表对象 TREE_PERMISSIONS
 *
 * @author zsy
 * @date 2024-01-15
 */
@Data
public class TreePermissionsDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 权限名称
     */
    @ApiModelProperty(value = "权限名称")
    @NotBlank(message = "权限组名称不能为空！")
    private String name;

    /**
     * 作用介绍
     */
    @ApiModelProperty(value = "作用介绍")
    private String descr;
}