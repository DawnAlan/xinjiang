
package com.cj.biz.modular.user.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位选择器参数
 *
 * @author xuyuxiang
 * @date 2022/7/26 15:58
 **/
@Getter
@Setter
public class BizUserSelectorPositionParam {

    /** 当前页 */
    @ApiModelProperty(value = "当前页码")
    private Integer current;

    /** 每页条数 */
    @ApiModelProperty(value = "每页条数")
    private Integer size;

    /** 机构id */
    @ApiModelProperty(value = "机构id")
    private String orgId;

    /** 名称关键词 */
    @ApiModelProperty(value = "名称关键词")
    private String searchKey;
}
