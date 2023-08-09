
package com.cj.auth.modular.sso.service.impl;

import com.cj.auth.modular.sso.service.AuthSsoService;
import org.springframework.stereotype.Service;
import com.cj.auth.modular.login.service.AuthService;
import com.cj.auth.modular.sso.param.AuthSsoTicketLoginParam;

import javax.annotation.Resource;

/**
 * 单点登录Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/8/30 9:21
 **/
@Service
public class AuthSsoServiceImpl implements AuthSsoService {

    @Resource
    private AuthService authService;

    @Override
    public String doLogin(AuthSsoTicketLoginParam authAccountPasswordLoginParam, String device) {
        return null;
    }
}
