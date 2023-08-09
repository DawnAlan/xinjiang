
package com.cj.auth.modular.sso.service;

import com.cj.auth.modular.sso.param.AuthSsoTicketLoginParam;

/**
 * 单点登录Service接口
 *
 * @author xuyuxiang
 * @date 2022/8/30 9:20
 **/
public interface AuthSsoService {

    /**
     * 根据ticket执行单点登录
     *
     * @author xuyuxiang
     * @date 2022/8/30 9:36
     **/
    String doLogin(AuthSsoTicketLoginParam authAccountPasswordLoginParam, String value);
}
