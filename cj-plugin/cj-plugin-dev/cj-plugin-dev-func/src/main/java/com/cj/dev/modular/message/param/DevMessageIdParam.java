
package com.cj.dev.modular.message.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 站内信Id参数
 *
 * @author xuyuxiang
 * @date 2022/6/21 15:34
 **/
@Getter
@Setter
public class DevMessageIdParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotBlank(message = "id不能为空")
    private String id;
}
