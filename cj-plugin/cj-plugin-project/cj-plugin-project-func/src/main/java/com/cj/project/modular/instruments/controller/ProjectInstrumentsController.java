package com.cj.project.modular.instruments.controller;

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
import com.cj.project.modular.instruments.entity.ProjectInstruments;
import com.cj.project.modular.instruments.param.ProjectInstrumentsAddParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsEditParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsIdParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsPageParam;
import com.cj.project.modular.instruments.service.ProjectInstrumentsService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 项目仪器表控制器
 *
 * @author Lb
 * @date  2023/09/02 18:12
 */
@Api(tags = "项目仪器表控制器")
@ApiSupport(author = "cj", order = 1)
@RestController
@Validated
public class ProjectInstrumentsController {

    @Resource
    private ProjectInstrumentsService projectInstrumentsService;

    /**
     * 获取项目仪器表分页
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取项目仪器表分页")
    @GetMapping("/project/instruments/page")
    public CommonResult<Page<ProjectInstruments>> page(ProjectInstrumentsPageParam projectInstrumentsPageParam) {
        return CommonResult.data(projectInstrumentsService.page(projectInstrumentsPageParam));
    }

    /**
     * 添加项目仪器表
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加项目仪器表")
    @CommonLog("添加项目仪器表")
    @PostMapping("/project/instruments/add")
    public CommonResult<String> add(@RequestBody @Valid ProjectInstrumentsAddParam projectInstrumentsAddParam) {
        projectInstrumentsService.add(projectInstrumentsAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑项目仪器表
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑项目仪器表")
    @CommonLog("编辑项目仪器表")
    @PostMapping("/project/instruments/edit")
    public CommonResult<String> edit(@RequestBody @Valid ProjectInstrumentsEditParam projectInstrumentsEditParam) {
        projectInstrumentsService.edit(projectInstrumentsEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除项目仪器表
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除项目仪器表")
    @CommonLog("删除项目仪器表")
    @PostMapping("/project/instruments/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<ProjectInstrumentsIdParam> projectInstrumentsIdParamList) {
        projectInstrumentsService.delete(projectInstrumentsIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取项目仪器表详情
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取项目仪器表详情")
    @GetMapping("/project/instruments/detail")
    public CommonResult<ProjectInstruments> detail(@Valid ProjectInstrumentsIdParam projectInstrumentsIdParam) {
        return CommonResult.data(projectInstrumentsService.detail(projectInstrumentsIdParam));
    }
}
