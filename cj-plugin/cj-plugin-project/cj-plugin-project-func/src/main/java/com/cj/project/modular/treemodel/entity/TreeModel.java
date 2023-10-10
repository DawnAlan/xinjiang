package com.cj.project.modular.treemodel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cj.common.pojo.CommonEntity;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 测点树实体
 *
 * @author Lb
 * @date  2023/09/14 16:41
 **/
@Getter
@Setter
@TableName("tree_model")
public class TreeModel extends CommonEntity implements TransPojo {

    /** 节点ID */
    @TableId
    @ApiModelProperty(value = "节点ID", position = 1)
    private String id;

    /** 项目Code */
    @ApiModelProperty(value = "项目Code", position = 2)
    private String projectCode;

    /** 父节点ID */
    @ApiModelProperty(value = "节点父ID", position = 3)
    private String parentId;

    /** 节点名称 */
    @ApiModelProperty(value = "节点名称", position = 4)
    private String nodeName;

    /** 绑定的测点ID */
    @ApiModelProperty(value = "绑定的测点ID", position = 5)
    private String pointId;

    /** 节点备注 */
    @ApiModelProperty(value = "节点备注", position = 6)
    private String nodeDescr;

    /** 是否为最末级节点 */
    @ApiModelProperty(value = "是否为最末级节点", position = 7)
    private Integer isEnd;

    /** 节点描述 */
    @ApiModelProperty(value = "节点描述", position = 8)
    private String nodeInfo;

    /** 树目录类型 */
    @ApiModelProperty(value = "树目录类型", position = 9)
    private String category;

    /** 节点排序 */
    @ApiModelProperty(value = "节点排序", position = 10)
    private Integer sortCode;

}
