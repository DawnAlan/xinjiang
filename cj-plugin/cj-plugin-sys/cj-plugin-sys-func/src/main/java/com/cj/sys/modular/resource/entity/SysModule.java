
package com.cj.sys.modular.resource.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.cj.common.pojo.CommonEntity;

/**
 * 模块实体
 *
 * @author xuyuxiang
 * @date 2022/7/27 18:20
 **/
@Getter
@Setter
@TableName("SYS_RESOURCE")
public class SysModule extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 标题 */
    @ApiModelProperty(value = "标题", position = 2)
    private String title;

    /** 编码 */
    @ApiModelProperty(value = "编码", position = 3)
    private String code;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 4)
    private String category;

    /** 图标 */
    @ApiModelProperty(value = "图标", position = 5)
    private String icon;

    /** 颜色 */
    @ApiModelProperty(value = "颜色", position = 6)
    private String color;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 7)
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 8)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String extJson;
}
