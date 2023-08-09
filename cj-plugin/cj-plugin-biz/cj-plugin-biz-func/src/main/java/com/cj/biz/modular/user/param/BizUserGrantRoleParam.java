
package com.cj.biz.modular.user.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 人员授权角色参数
 *
 * @author xuyuxiang
 * @date 2022/4/21 16:13
 **/
@Getter
@Setter
public class BizUserGrantRoleParam {

    /** id */
    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 角色id集合 */
    @ApiModelProperty(value = "角色id集合", required = true, position = 2)
    @NotNull(message = "roleIdList不能为空")
    private List<String> roleIdList;
}
