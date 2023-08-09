package com.cj.auth.modular.monitor.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Session退出参数
 *
 * @author xuyuxiang
 * @date 2022/6/24 22:28
 */
@Getter
@Setter
public class AuthExitSessionParam {

    /** 用户id */
    @ApiModelProperty(value = "用户id", required = true)
    @NotBlank(message = "userId不能为空")
    private String userId;
}
