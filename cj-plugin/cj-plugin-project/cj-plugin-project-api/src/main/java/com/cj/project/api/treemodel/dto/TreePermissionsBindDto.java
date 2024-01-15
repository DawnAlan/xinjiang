package com.cj.project.api.treemodel.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TreePermissionsBindDto {

    /**
     * 权限组id
     */
    @ApiModelProperty(value = "权限组id", position = 1)
    private String id;

    /**
     * idList类别：1用户帐号；2节点树目录id
     */
    @ApiModelProperty(value = "idList类别：1用户帐号id；2节点树目录id；3用户角色id", position = 2)
    private Integer type;

    /**
     * idList
     */
    @ApiModelProperty(value = "idList", position = 3)
    private List<String> idList;

}
