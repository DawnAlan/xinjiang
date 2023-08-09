package com.cj.auth.modular.login.service;

import com.cj.auth.modular.login.param.AuthAccountPasswordLoginParam;
import com.cj.auth.modular.login.param.AuthGetPhoneValidCodeParam;
import com.cj.auth.modular.login.param.AuthPhoneValidCodeLoginParam;
import com.cj.auth.modular.login.result.AuthPicValidCodeResult;
import com.cj.auth.core.pojo.SaBaseClientLoginUser;
import com.cj.auth.core.pojo.SaBaseLoginUser;

/**
 * 登录Service接口
 *
 * @author xuyuxiang
 * @date 2021/12/23 21:51
 */
public interface AuthService {

    /**
     * 获取图片验证码
     *
     * @author xuyuxiang
     * @date 2021/12/28 14:46
     **/
    AuthPicValidCodeResult getPicCaptcha(String type);

    /**
     * 获取手机验证码
     *
     * @author xuyuxiang
     * @date 2021/12/28 14:46
     **/
    String getPhoneValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type);

    /**
     * 账号密码登录
     *
     * @author xuyuxiang
     * @date 2021/12/28 14:46
     **/
    String doLogin(AuthAccountPasswordLoginParam authAccountPasswordLoginParam, String type);

    /**
     * 手机验证码登录
     *
     * @author xuyuxiang
     * @date 2021/12/28 14:46
     **/
    String doLoginByPhone(AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam, String type);

    /**
     * 获取B端登录用户信息
     *
     * @author xuyuxiang
     * @date 2021/10/12 15:59
     **/
    SaBaseLoginUser getLoginUser();

    /**
     * 获取C端登录用户信息
     *
     * @author xuyuxiang
     * @date 2021/10/12 15:59
     **/
    SaBaseClientLoginUser getClientLoginUser();

    /**
     * 根据用户id和客户端类型登录，用于第三方登录
     *
     * @author xuyuxiang
     * @date 2022/7/9 14:44
     */
    String doLoginById(String userId, String device, String type);
}
