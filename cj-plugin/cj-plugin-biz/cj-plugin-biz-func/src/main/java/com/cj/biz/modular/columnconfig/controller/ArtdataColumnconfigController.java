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
package com.cj.biz.modular.columnconfig.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.common.pojo.CommonValidList;
import com.cj.biz.modular.columnconfig.entity.ArtdataColumnconfig;
import com.cj.biz.modular.columnconfig.param.ArtdataColumnconfigAddParam;
import com.cj.biz.modular.columnconfig.param.ArtdataColumnconfigEditParam;
import com.cj.biz.modular.columnconfig.param.ArtdataColumnconfigIdParam;
import com.cj.biz.modular.columnconfig.param.ArtdataColumnconfigPageParam;
import com.cj.biz.modular.columnconfig.service.ArtdataColumnconfigService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 格式配置表控制器
 *
 * @author dengdi
 * @date  2023/08/22 10:10
 */
@Api(tags = "格式配置表控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class ArtdataColumnconfigController {

    @Resource
    private ArtdataColumnconfigService artdataColumnconfigService;

    /**
     * 获取格式配置表分页
     *
     * @author dengdi
     * @date  2023/08/22 10:10
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取格式配置表分页")
    @GetMapping("/columnconfig/page")
    public CommonResult<Page<ArtdataColumnconfig>> page(ArtdataColumnconfigPageParam artdataColumnconfigPageParam) {
        return CommonResult.data(artdataColumnconfigService.page(artdataColumnconfigPageParam));
    }

    /**
     * 添加格式配置表
     *
     * @author dengdi
     * @date  2023/08/22 10:10
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加格式配置表")
    @CommonLog("添加格式配置表")
    @PostMapping("/columnconfig/add")
    public CommonResult<String> add(@RequestBody @Valid ArtdataColumnconfigAddParam artdataColumnconfigAddParam) {
        artdataColumnconfigService.add(artdataColumnconfigAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑格式配置表
     *
     * @author dengdi
     * @date  2023/08/22 10:10
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑格式配置表")
    @CommonLog("编辑格式配置表")
    @PostMapping("/columnconfig/edit")
    public CommonResult<String> edit(@RequestBody @Valid ArtdataColumnconfigEditParam artdataColumnconfigEditParam) {
        artdataColumnconfigService.edit(artdataColumnconfigEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除格式配置表
     *
     * @author dengdi
     * @date  2023/08/22 10:10
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除格式配置表")
    @CommonLog("删除格式配置表")
    @PostMapping("/biz/columnconfig/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<ArtdataColumnconfigIdParam> artdataColumnconfigIdParamList) {
        artdataColumnconfigService.delete(artdataColumnconfigIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取格式配置表详情
     *
     * @author dengdi
     * @date  2023/08/22 10:10
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取格式配置表详情")
    @GetMapping("/columnconfig/detail")
    public CommonResult<ArtdataColumnconfig> detail(@Valid ArtdataColumnconfigIdParam artdataColumnconfigIdParam) {
        return CommonResult.data(artdataColumnconfigService.detail(artdataColumnconfigIdParam));
    }
}
