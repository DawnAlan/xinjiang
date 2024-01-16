/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cj.data.modular.artdata.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.common.pojo.CommonValidList;
import com.cj.data.api.artdata.entity.ArtdataColumnconfigParameter;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterAddParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterEditParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterIdParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterPageParam;
import com.cj.data.modular.artdata.service.ArtdataColumnconfigParameterService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 模板列参数表控制器
 *
 * @author dd
 * @date  2024/01/12 17:25
 */
@Api(tags = "模板列参数表控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class ArtdataColumnconfigParameterController {

    @Resource
    private ArtdataColumnconfigParameterService artdataColumnconfigParameterService;

    /**
     * 获取模板列参数表分页
     *
     * @author dd
     * @date  2024/01/12 17:25
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取模板列参数表分页")
    @SaCheckPermission("/biz/artdatacolumnconfigparameter/page")
    @GetMapping("/biz/artdatacolumnconfigparameter/page")
    public CommonResult<Page<ArtdataColumnconfigParameter>> page(ArtdataColumnconfigParameterPageParam artdataColumnconfigParameterPageParam) {
        return CommonResult.data(artdataColumnconfigParameterService.page(artdataColumnconfigParameterPageParam));
    }

    /**
     * 添加模板列参数表
     *
     * @author dd
     * @date  2024/01/12 17:25
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加模板列参数表")
    @CommonLog("添加模板列参数表")
    @SaCheckPermission("/biz/artdatacolumnconfigparameter/add")
    @PostMapping("/biz/artdatacolumnconfigparameter/add")
    public CommonResult<String> add(@RequestBody @Valid ArtdataColumnconfigParameterAddParam artdataColumnconfigParameterAddParam) {
        artdataColumnconfigParameterService.add(artdataColumnconfigParameterAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑模板列参数表
     *
     * @author dd
     * @date  2024/01/12 17:25
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑模板列参数表")
    @CommonLog("编辑模板列参数表")
    @SaCheckPermission("/biz/artdatacolumnconfigparameter/edit")
    @PostMapping("/biz/artdatacolumnconfigparameter/edit")
    public CommonResult<String> edit(@RequestBody @Valid ArtdataColumnconfigParameterEditParam artdataColumnconfigParameterEditParam) {
        artdataColumnconfigParameterService.edit(artdataColumnconfigParameterEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除模板列参数表
     *
     * @author dd
     * @date  2024/01/12 17:25
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除模板列参数表")
    @CommonLog("删除模板列参数表")
    @SaCheckPermission("/biz/artdatacolumnconfigparameter/delete")
    @PostMapping("/biz/artdatacolumnconfigparameter/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<ArtdataColumnconfigParameterIdParam> artdataColumnconfigParameterIdParamList) {
        artdataColumnconfigParameterService.delete(artdataColumnconfigParameterIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取模板列参数表详情
     *
     * @author dd
     * @date  2024/01/12 17:25
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取模板列参数表详情")
    @SaCheckPermission("/biz/artdatacolumnconfigparameter/detail")
    @GetMapping("/biz/artdatacolumnconfigparameter/detail")
    public CommonResult<ArtdataColumnconfigParameter> detail(@Valid ArtdataColumnconfigParameterIdParam artdataColumnconfigParameterIdParam) {
        return CommonResult.data(artdataColumnconfigParameterService.detail(artdataColumnconfigParameterIdParam));
    }
}
