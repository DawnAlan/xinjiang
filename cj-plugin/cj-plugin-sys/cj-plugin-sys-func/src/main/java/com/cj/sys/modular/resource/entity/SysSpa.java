
package com.cj.sys.modular.resource.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.cj.common.pojo.CommonEntity;

/**
 * 单页面实体
 *
 * @author xuyuxiang
 * @date 2022/7/27 18:27
 **/
@Getter
@Setter
@TableName("SYS_RESOURCE")
public class SysSpa extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 标题 */
    @ApiModelProperty(value = "标题", position = 2)
    private String title;

    /** 别名 */
    @ApiModelProperty(value = "别名", position = 3)
    private String name;

    /** 编码 */
    @ApiModelProperty(value = "编码", position = 4)
    private String code;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 5)
    private String category;

    /** 菜单类型 */
    @ApiModelProperty(value = "菜单类型", position = 6)
    private String menuType;

    /** 路径 */
    @ApiModelProperty(value = "路径", position = 7)
    private String path;

    /** 组件 */
    @ApiModelProperty(value = "组件", position = 8)
    private String component;

    /** 图标 */
    @ApiModelProperty(value = "图标", position = 9)
    private String icon;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 10)
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 11)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String extJson;
}
