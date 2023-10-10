package com.cj.project.modular.projects.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cj.common.pojo.CommonEntity;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目实体
 *
 * @author Lb
 * @date  2023/09/01 12:29
 **/
@Getter
@Setter
@TableName("project_projects")
public class ProjectProjects extends CommonEntity implements TransPojo {

    /** ID */
    @TableId
    @ApiModelProperty(value = "ID", position = 1)
    private String id;

    /** 父id */
    @ApiModelProperty(value = "父id", position = 2)
    private String parentId;

    /** 名称 */
    @ApiModelProperty(value = "名称", position = 3)
    private String name;

    /** 编码 */
    @ApiModelProperty(value = "编码", position = 4)
    private String code;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 5)
    private String category;

    /** 描述 */
    @ApiModelProperty(value = "描述", position = 6)
    private String description;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 7)
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 8)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String extJson;

}
