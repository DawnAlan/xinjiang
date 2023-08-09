
package com.cj.biz.modular.user.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色选择器结果
 *
 * @author xuyuxiang
 * @date 2022/7/22 14:29
 **/
@Getter
@Setter
public class BizUserRoleResult {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 组织id */
    @ApiModelProperty(value = "机构id", position = 2)
    private String orgId;

    /** 名称 */
    @ApiModelProperty(value = "名称", position = 3)
    private String name;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 4)
    private String category;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 5)
    private Integer sortCode;
}
