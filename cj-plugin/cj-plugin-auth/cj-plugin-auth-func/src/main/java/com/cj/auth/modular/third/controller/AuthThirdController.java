
package com.cj.auth.modular.third.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.zhyd.oauth.model.AuthCallback;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cj.auth.modular.third.entity.AuthThirdUser;
import com.cj.auth.modular.third.param.AuthThirdCallbackParam;
import com.cj.auth.modular.third.param.AuthThirdRenderParam;
import com.cj.auth.modular.third.param.AuthThirdUserPageParam;
import com.cj.auth.modular.third.result.AuthThirdRenderResult;
import com.cj.auth.modular.third.service.AuthThirdService;
import com.cj.common.pojo.CommonResult;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 第三方登录控制器
 *
 * @author xuyuxiang
 * @date 2022/7/8 16:18
 **/
@Api(tags = "三方登录控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 5)
@RestController
@Validated
public class AuthThirdController {

    @Resource
    private AuthThirdService authThirdService;

    /**
     * 第三方登录页面渲染
     *
     * @author xuyuxiang
     * @date 2022/7/8 16:19
     **/
    @ApiOperationSupport(order = 1)
    @ApiOperation("第三方登录页面渲染")
    @GetMapping("/auth/third/render")
    public CommonResult<AuthThirdRenderResult> render(@Valid AuthThirdRenderParam authThirdRenderParam) {
        return CommonResult.data(authThirdService.render(authThirdRenderParam));
    }

    /**
     * 第三方登录授权回调
     *
     * @author xuyuxiang
     * @date 2022/7/8 16:42
     **/
    @ApiOperationSupport(order = 2)
    @ApiOperation("第三方登录授权回调")
    @GetMapping("/auth/third/callback")
    public CommonResult<String> callback(@Valid AuthThirdCallbackParam authThirdCallbackParam, AuthCallback authCallback) {
        return CommonResult.data(authThirdService.callback(authThirdCallbackParam, authCallback));
    }

    /**
     * 获取三方用户分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("获取三方用户分页")
    @GetMapping("/auth/third/page")
    public CommonResult<Page<AuthThirdUser>> page(AuthThirdUserPageParam authThirdUserPageParam) {
        return CommonResult.data(authThirdService.page(authThirdUserPageParam));
    }
}
