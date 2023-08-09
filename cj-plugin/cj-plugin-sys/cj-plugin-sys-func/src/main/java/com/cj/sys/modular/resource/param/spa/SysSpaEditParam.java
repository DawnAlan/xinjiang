
package com.cj.sys.modular.resource.param.spa;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 单页面编辑参数
 *
 * @author xuyuxiang
 * @date 2022/7/27 18:40
 **/
@Getter
@Setter
public class SysSpaEditParam {

    /** id */
    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 标题 */
    @ApiModelProperty(value = "标题", required = true, position = 2)
    @NotBlank(message = "title不能为空")
    private String title;

    /** 菜单类型 */
    @ApiModelProperty(value = "菜单类型", required = true, position = 3)
    @NotBlank(message = "menuType不能为空")
    private String menuType;

    /** 别名 */
    @ApiModelProperty(value = "别名", required = true, position = 4)
    private String name;

    /** 路径 */
    @ApiModelProperty(value = "路径", required = true, position = 5)
    @NotBlank(message = "path不能为空")
    private String path;

    /** 组件 */
    @ApiModelProperty(value = "组件", required = true, position = 6)
    private String component;

    /** 图标 */
    @ApiModelProperty(value = "图标", required = true, position = 7)
    private String icon;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", required = true, position = 8)
    @NotNull(message = "sortCode不能为空")
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 9)
    private String extJson;
}
