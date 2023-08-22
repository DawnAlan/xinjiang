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
package com.cj.textua.textua.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.common.pojo.CommonValidList;
import com.cj.textua.textua.entity.FiducialParam;
import com.cj.textua.textua.param.FiducialParamAddParam;
import com.cj.textua.textua.param.FiducialParamEditParam;
import com.cj.textua.textua.param.FiducialParamIdParam;
import com.cj.textua.textua.param.FiducialParamPageParam;
import com.cj.textua.textua.service.FiducialParamService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 考证参数信息表(跟字段配置表关联)控制器
 *
 * @author yancheng
 * @date  2023/08/21 20:50
 */
@Api(tags = "考证参数信息表(跟字段配置表关联)控制器")
@ApiSupport(author = "CJ_TEAM", order = 1)
@RestController
@Validated
public class FiducialParamController {

    @Resource
    private FiducialParamService fiducialParamService;

    /**
     * 获取考证参数信息表(跟字段配置表关联)分页
     *
     * @author yancheng
     * @date  2023/08/21 20:50
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取考证参数信息表(跟字段配置表关联)分页")
    @SaCheckPermission("/fiducialparam/page")
    @GetMapping("/fiducialparam/page")
    public CommonResult<Page<FiducialParam>> page(FiducialParamPageParam fiducialParamPageParam) {
        return CommonResult.data(fiducialParamService.page(fiducialParamPageParam));
    }

    /**
     * 添加考证参数信息表(跟字段配置表关联)
     *
     * @author yancheng
     * @date  2023/08/21 20:50
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加考证参数信息表(跟字段配置表关联)")
    @CommonLog("添加考证参数信息表(跟字段配置表关联)")
    @SaCheckPermission("/fiducialparam/add")
    @PostMapping("/fiducialparam/add")
    public CommonResult<String> add(@RequestBody @Valid FiducialParamAddParam fiducialParamAddParam) {
        fiducialParamService.add(fiducialParamAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑考证参数信息表(跟字段配置表关联)
     *
     * @author yancheng
     * @date  2023/08/21 20:50
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑考证参数信息表(跟字段配置表关联)")
    @CommonLog("编辑考证参数信息表(跟字段配置表关联)")
    @SaCheckPermission("/fiducialparam/edit")
    @PostMapping("/fiducialparam/edit")
    public CommonResult<String> edit(@RequestBody @Valid FiducialParamEditParam fiducialParamEditParam) {
        fiducialParamService.edit(fiducialParamEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除考证参数信息表(跟字段配置表关联)
     *
     * @author yancheng
     * @date  2023/08/21 20:50
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除考证参数信息表(跟字段配置表关联)")
    @CommonLog("删除考证参数信息表(跟字段配置表关联)")
    @SaCheckPermission("/fiducialparam/delete")
    @PostMapping("/fiducialparam/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<FiducialParamIdParam> fiducialParamIdParamList) {
        fiducialParamService.delete(fiducialParamIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取考证参数信息表(跟字段配置表关联)详情
     *
     * @author yancheng
     * @date  2023/08/21 20:50
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取考证参数信息表(跟字段配置表关联)详情")
    @SaCheckPermission("/fiducialparam/detail")
    @GetMapping("/fiducialparam/detail")
    public CommonResult<FiducialParam> detail(@Valid FiducialParamIdParam fiducialParamIdParam) {
        return CommonResult.data(fiducialParamService.detail(fiducialParamIdParam));
    }
}
