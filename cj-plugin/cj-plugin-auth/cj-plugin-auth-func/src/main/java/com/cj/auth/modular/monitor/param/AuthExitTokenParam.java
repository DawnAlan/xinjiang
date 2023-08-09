package com.cj.auth.modular.monitor.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Token退出参数
 *
 * @author xuyuxiang
 * @date 2022/6/24 22:28
 */
@Getter
@Setter
public class AuthExitTokenParam {

    /** token值 */
    @ApiModelProperty(value = "token值", required = true)
    @NotBlank(message = "tokenValue不能为空")
    private String tokenValue;
}
