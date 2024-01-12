package com.cj.project.api.treemodel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cj.common.pojo.CommonEntity;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 测点树实体
 *
 * @author Lb
 * @date 2023/09/14 16:41
 **/
@Getter
@Setter
@TableName("tree_model")
public class TreeModel extends CommonEntity implements TransPojo {

    /**
     * 节点ID
     */
    @TableId
    @ApiModelProperty(value = "节点ID", position = 1)
    private String id;

    /**
     * 项目Code
     */
    @ApiModelProperty(value = "项目Code", position = 2)
    private String projectCode;

    /**
     * 父节点ID
     */
    @ApiModelProperty(value = "节点父ID", position = 3)
    private String parentId;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称", position = 4)
    private String nodeName;

    /**
     * 绑定的测点ID
     */
    @ApiModelProperty(value = "绑定的测点ID", position = 5)
    private String pointId;

    /**
     * 节点备注
     */
    @ApiModelProperty(value = "节点备注", position = 6)
    private String nodeDescr;

    /**
     * 是否为最末级节点
     */
    @ApiModelProperty(value = "是否为最末级节点：0：否；1：是", position = 7)
    private Integer isEnd;

    /**
     * 节点描述
     */
    @ApiModelProperty(value = "节点描述", position = 8)
    private String nodeInfo;

    /**
     * 树目录类型： 1 工程结构树 2 仪器类型树 3 MCU采集树之类
     */
    @ApiModelProperty(value = "树目录类型： 1 工程结构树 2 仪器类型树 3 MCU采集树之类", position = 9)
    private String category;

    /**
     * 节点排序
     */
    @ApiModelProperty(value = "节点排序", position = 10)
    private Integer sortCode;

    /**
     * 节点分类：1:自定义分类、2:监测类型、3:仪器类型，4:测点编号
     */
    @ApiModelProperty(value = "节点分类：1:自定义分类、2:监测类型、3:仪器类型，4:测点编号", position = 11)
    private Integer nodeType;

    /**
     * 是否是重点节点：0：是、1：否
     */
    @ApiModelProperty(value = "是否是重点节点：0：否；1：是", position = 12)
    private Integer isImportantNode;

    /**
     * 仪器名称
     */
    @ApiModelProperty(value = "仪器名称", position = 13)
    private String instrumentName;

    /**
     * 检测类型
     */
    @ApiModelProperty(value = "检测类型", position = 14)
    private String monitorName;

}
