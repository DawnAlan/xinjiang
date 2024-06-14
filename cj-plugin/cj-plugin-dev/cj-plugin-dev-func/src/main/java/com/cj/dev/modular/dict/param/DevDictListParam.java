
package com.cj.dev.modular.dict.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典列表参数
 *
 * @author xuyuxiang
 * @date 2022/7/30 21:49
 */
@Getter
@Setter
public class DevDictListParam {

    /** 父id */
    @ApiModelProperty(value = "父id")
    private String parentId;

    /** 字典分类 */
    @ApiModelProperty(value = "字典分类")
    private String category;

    /** 字典组 */
    @ApiModelProperty(value = "字典组")
    private String dictGroup;

    /** 是否可用 */
    @ApiModelProperty(value = "是否可用")
    private String enable;

    /** 是否可用 */
    @ApiModelProperty(value = "是否带父节点:0不带，默认带")
    private String hasParent = "1";
}
