
package com.cj.auth.modular.sso.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * ticket单点登录登录参数
 *
 * @author xuyuxiang
 * @date 2022/7/7 16:46
 **/
@Getter
@Setter
public class AuthSsoTicketLoginParam {

    /** ticket */
    @ApiModelProperty(value = "ticket", required = true, position = 1)
    @NotBlank(message = "ticket不能为空")
    private String ticket;

    /** 设备 */
    @ApiModelProperty(value = "设备", position = 2)
    private String device;
}
