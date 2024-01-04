package com.cj.project.modular.instruments.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
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
import com.cj.project.modular.instruments.entity.ProjectInstruments;
import com.cj.project.modular.instruments.param.ProjectInstrumentsAddParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsEditParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsIdParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsPageParam;
import com.cj.project.modular.instruments.service.ProjectInstrumentsService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 项目仪器控制器
 *
 * @author Lb
 * @date  2023/09/02 18:12
 */
@Api(tags = "项目仪器控制器")
@ApiSupport(author = "cj", order = 1)
@RestController
@Validated
public class ProjectInstrumentsController {

    @Resource
    private ProjectInstrumentsService projectInstrumentsService;

    /**
     * 获取项目仪器分页
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取项目仪器分页")
    @GetMapping("/project/instruments/page")
    public CommonResult<Page<ProjectInstruments>> page(ProjectInstrumentsPageParam projectInstrumentsPageParam) {
        return CommonResult.data(projectInstrumentsService.page(projectInstrumentsPageParam));
    }

    /**
     * 获取项目仪器列表
     *
     * @author : lb
     * @date : 2023/10/31 18:10
    */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取项目仪器列表")
    @GetMapping("/project/instruments/list")
    public CommonResult<List<ProjectInstruments>> List(String projectCode,String monitorName,String instrumentType,String instrumentMetaType) {
        return CommonResult.data(projectInstrumentsService.getList(projectCode,monitorName,instrumentType,instrumentMetaType));
    }

    /**
     * 获取项目仪器树
     *
     * @author : lb
     * @date : 2023/10/31 09:11
    */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取项目仪器树")
    @GetMapping("/project/instruments/tree")
    public CommonResult<List<Tree<String>>> tree(@RequestParam @NotBlank(message = "项目编号不能为空")String projectCode ) {
        return CommonResult.data(projectInstrumentsService.tree(projectCode));
    }
    /**
     * 添加项目仪器
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加项目仪器")
    @CommonLog("添加项目仪器")
    @PostMapping("/project/instruments/add")
    public CommonResult<String> add(@RequestBody @Valid ProjectInstrumentsAddParam projectInstrumentsAddParam) {
        projectInstrumentsService.add(projectInstrumentsAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑项目仪器
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑项目仪器")
    @CommonLog("编辑项目仪器")
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
    @ApiOperation("删除项目仪器")
    @CommonLog("删除项目仪器")
    @PostMapping("/project/instruments/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<ProjectInstrumentsIdParam> projectInstrumentsIdParamList) {
        projectInstrumentsService.delete(projectInstrumentsIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取项目仪器详情
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取项目仪器详情")
    @GetMapping("/project/instruments/detail")
    public CommonResult<ProjectInstruments> detail(@Valid ProjectInstrumentsIdParam projectInstrumentsIdParam) {
        return CommonResult.data(projectInstrumentsService.detail(projectInstrumentsIdParam));
    }
}
