
package com.cj.sys.modular.index.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 站内信Id参数
 *
 * @author xuyuxiang
 * @date 2022/9/2 11:06
 */
@Getter
@Setter
public class SysIndexMessageIdParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotBlank(message = "id不能为空")
    private String id;
}
