package com.cj.project.api.treemodel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cj.common.pojo.CommonEntity;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 树目录权限组基本信息表对象 TREE_PERMISSIONS
 *
 * @author zsy
 * @date 2024-01-15
 */
@Data
@TableName("TREE_PERMISSIONS")
public class TreePermissions extends CommonEntity implements TransPojo {

    /**
     * 主键id
     */
    @TableId
    @ApiModelProperty(value = "主键id")
    private String id;


    /**
     * 权限名称
     */
    @ApiModelProperty(value = "权限名称")
    private String name;

    /**
     * 作用介绍
     */
    @ApiModelProperty(value = "作用介绍")
    private String descr;


}