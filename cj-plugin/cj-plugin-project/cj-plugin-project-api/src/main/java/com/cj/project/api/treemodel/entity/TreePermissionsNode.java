package com.cj.project.api.treemodel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cj.common.pojo.CommonEntity;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 权限组与树节点绑定关系表对象 TREE_PERMISSIONS_NODE
 *
 * @author zsy
 * @date 2024-01-15
 */
@Data
@TableName("TREE_PERMISSIONS_NODE")
public class TreePermissionsNode implements TransPojo {

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    private String id;


    /**
     * 权限组id
     */
    @ApiModelProperty(value = "权限组id")
    private String pId;


    /**
     * 树节点id
     */
    @ApiModelProperty(value = "树节点id")
    private String nodeId;


}