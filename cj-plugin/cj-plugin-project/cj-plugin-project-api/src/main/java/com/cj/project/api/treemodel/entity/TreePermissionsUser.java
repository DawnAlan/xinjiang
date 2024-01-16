package com.cj.project.api.treemodel.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cj.common.pojo.CommonEntity;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 树权限组与用户关系表对象 TREE_PERMISSIONS_USER
 *
 * @author zsy
 * @date 2024-01-15
 */
@Data
@TableName(value = "TREE_PERMISSIONS_USER", excludeProperty = {"updateUser", "updateTime"})
public class TreePermissionsUser extends CommonEntity implements TransPojo {

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;


    /**
     * $column.columnComment
     */
    @ApiModelProperty(value = "$column.columnComment")
    private String createUser;


    /**
     * 权限组id
     */
    @ApiModelProperty(value = "权限组id")
    private String pId;

    /**
     * 绑定用户数据类型：1：帐号id，2：角色id
     */
    @ApiModelProperty(value = "绑定用户数据类型：1：帐号id，2：角色id")
    private Integer bindType;

}