package com.cj.project.modular.fiducial.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.common.pojo.CommonValidList;
import com.cj.project.api.fiducial.entity.FiducialBase;
import com.cj.project.api.fiducial.param.*;
import com.cj.project.modular.fiducial.service.FiducialBaseService;
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
 * 测点考证_基础数据控制器
 *
 * @author Lb
 * @date  2023/09/04 12:25
 */
@Api(tags = "测点考证_基础数据控制器")
@ApiSupport(author = "CJ_TEAM", order = 1)
@RestController
@Validated
public class FiducialBaseController {

    @Resource
    private FiducialBaseService fiducialBaseService;

    /**
     * 获取测点考证分页
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取测点考证分页")
    @GetMapping("/project/fiducialbase/page")
    public CommonResult<Page<FiducialBase>> page(FiducialPageParam fiducialPageParam) {
        return CommonResult.data(fiducialBaseService.page(fiducialPageParam));
    }

    /**
     * 添加测点考证表
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加测点考证")
    @CommonLog("添加测点考证")
    @PostMapping("/project/fiducialbase/add")
    public CommonResult<String> add(@RequestBody @Valid FiducialBaseAddParam fiducialBaseAddParam) {
        FiducialBase fiducialBase = BeanUtil.toBean(fiducialBaseAddParam, FiducialBase.class);
        fiducialBaseService.add(fiducialBase);
        return CommonResult.ok();
    }

    /**
     * 编辑测点考证表
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑测点考证")
    @CommonLog("编辑测点考证")
    @PostMapping("/project/fiducialbase/edit")
    public CommonResult<String> edit(@RequestBody @Valid FiducialBaseEditParam fiducialBaseEditParam) {
        fiducialBaseService.edit(fiducialBaseEditParam);
        return CommonResult.ok();
    }


    /**
     * 删除测点考证表
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除测点考证")
    @CommonLog("删除测点考证")
    @PostMapping("/project/fiducial/base/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                       CommonValidList<String> fiducialIdList) {
        fiducialBaseService.delete(fiducialIdList);
        return CommonResult.ok();
    }

    /**
     * 获取测点考证表详情
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取测点考证详情")
    @GetMapping("/project/fiducialbase/detail")
    public CommonResult<FiducialBase> detail(@Valid FiducialIdParam fiducialIdParam) {
        return CommonResult.data(fiducialBaseService.detail(fiducialIdParam));
    }
}
