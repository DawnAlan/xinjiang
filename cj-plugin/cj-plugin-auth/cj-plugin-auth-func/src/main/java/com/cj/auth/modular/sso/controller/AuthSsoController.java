
package com.cj.auth.modular.sso.controller;

import com.cj.auth.modular.sso.param.AuthSsoTicketLoginParam;
import com.cj.auth.modular.sso.service.AuthSsoService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cj.auth.core.enums.SaClientTypeEnum;
import com.cj.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 单点登录控制器
 *
 * @author xuyuxiang
 * @date 2022/8/30 9:20
 **/
@Api(tags = "单点登录控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 4)
@RestController
@Validated
public class AuthSsoController {

    @Resource
    private AuthSsoService authSsoService;

    /**
     * 根据ticket执行单点登录
     *
     * @author xuyuxiang
     * @date 2021/10/15 13:12
     **/
    @ApiOperationSupport(order = 1)
    @ApiOperation("根据ticket执行单点登录")
    @PostMapping("/auth/sso/doLogin")
    public CommonResult<String> doLogin(@RequestBody @Valid AuthSsoTicketLoginParam authAccountPasswordLoginParam) {
        return CommonResult.data(authSsoService.doLogin(authAccountPasswordLoginParam, SaClientTypeEnum.B.getValue()));
    }
}
