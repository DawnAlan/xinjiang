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
import com.cj.data.api.artdata.entity.ArtdataColumnconfig;
import com.cj.data.api.artdata.param.ArtdataColumnconfigAddParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigEditParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigIdParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigPageParam;
import com.cj.data.modular.artdata.service.ArtdataColumnconfigService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 格式配置表控制器
 *
 * @author dd
 * @date  2024/01/12 17:23
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
     * @author dd
     * @date  2024/01/12 17:23
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取格式配置表分页")
    @SaCheckPermission("/biz/artdatacolumnconfig/page")
    @GetMapping("/biz/artdatacolumnconfig/page")
    public CommonResult<Page<ArtdataColumnconfig>> page(ArtdataColumnconfigPageParam artdataColumnconfigPageParam) {
        return CommonResult.data(artdataColumnconfigService.page(artdataColumnconfigPageParam));
    }

    /**
     * 添加格式配置表
     *
     * @author dd
     * @date  2024/01/12 17:23
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加格式配置表")
    @CommonLog("添加格式配置表")
    @SaCheckPermission("/biz/artdatacolumnconfig/add")
    @PostMapping("/biz/artdatacolumnconfig/add")
    public CommonResult<String> add(@RequestBody @Valid ArtdataColumnconfigAddParam artdataColumnconfigAddParam) {
        artdataColumnconfigService.add(artdataColumnconfigAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑格式配置表
     *
     * @author dd
     * @date  2024/01/12 17:23
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑格式配置表")
    @CommonLog("编辑格式配置表")
    @SaCheckPermission("/biz/artdatacolumnconfig/edit")
    @PostMapping("/biz/artdatacolumnconfig/edit")
    public CommonResult<String> edit(@RequestBody @Valid ArtdataColumnconfigEditParam artdataColumnconfigEditParam) {
        artdataColumnconfigService.edit(artdataColumnconfigEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除格式配置表
     *
     * @author dd
     * @date  2024/01/12 17:23
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除格式配置表")
    @CommonLog("删除格式配置表")
    @SaCheckPermission("/biz/artdatacolumnconfig/delete")
    @PostMapping("/biz/artdatacolumnconfig/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<ArtdataColumnconfigIdParam> artdataColumnconfigIdParamList) {
        artdataColumnconfigService.delete(artdataColumnconfigIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取格式配置表详情
     *
     * @author dd
     * @date  2024/01/12 17:23
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取格式配置表详情")
    @SaCheckPermission("/biz/artdatacolumnconfig/detail")
    @GetMapping("/biz/artdatacolumnconfig/detail")
    public CommonResult<ArtdataColumnconfig> detail(@Valid ArtdataColumnconfigIdParam artdataColumnconfigIdParam) {
        return CommonResult.data(artdataColumnconfigService.detail(artdataColumnconfigIdParam));
    }
}
