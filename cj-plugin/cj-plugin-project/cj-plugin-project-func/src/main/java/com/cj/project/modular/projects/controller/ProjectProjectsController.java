package com.cj.project.modular.projects.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.project.modular.projects.param.*;
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
import com.cj.project.modular.projects.entity.ProjectProjects;
import com.cj.project.modular.projects.service.ProjectProjectsService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 项目控制器
 *
 * @author Lb
 * @date  2023/09/01 12:29
 */
@Api(tags = "项目控制器")
@ApiSupport(author = "CJ_TEAM", order = 1)
@RestController
@Validated
public class ProjectProjectsController {

    @Resource
    private ProjectProjectsService projectProjectsService;

    /**
     * 获取项目分页
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取项目分页")
    // @SaCheckPermission("/project/projects/page")
    @GetMapping("/project/projects/page")
    public CommonResult<Page<ProjectProjects>> page(ProjectProjectsPageParam projectProjectsPageParam) {
        return CommonResult.data(projectProjectsService.page(projectProjectsPageParam));
    }

    /**
     * 获取项目树
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取项目树")
    @GetMapping("/project/projects/tree")
    public CommonResult<List<Tree<String>>> tree() {
        return CommonResult.data(projectProjectsService.tree());
    }

    /**
     * 查询项目
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("查询项目")
    // @SaCheckPermission("/project/projects/page")
    @GetMapping("/project/projects/query")
    public CommonResult<List<ProjectProjects>> Query(ProjectProjectsQueryParam projectProjectsQueryParam) {
        return CommonResult.data(projectProjectsService.getProjectsList(projectProjectsQueryParam.getCode(),
                projectProjectsQueryParam.getName()));
    }

    /**
     * 添加项目
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加项目")
    @CommonLog("添加项目")
    // @SaCheckPermission("/project/projects/add")
    @PostMapping("/project/projects/add")
    public CommonResult<String> add(@RequestBody @Valid ProjectProjectsAddParam projectProjectsAddParam) {
        projectProjectsService.add(projectProjectsAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑项目
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑项目")
    @CommonLog("编辑项目")
    // @SaCheckPermission("/project/projects/edit")
    @PostMapping("/project/projects/edit")
    public CommonResult<String> edit(@RequestBody @Valid ProjectProjectsEditParam projectProjectsEditParam) {
        projectProjectsService.edit(projectProjectsEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除项目
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除项目")
    @CommonLog("删除项目")
    // @SaCheckPermission("/project/projects/delete")
    @PostMapping("/project/projects/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<ProjectProjectsIdParam> projectProjectsIdParamList) {
        projectProjectsService.delete(projectProjectsIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取项目详情
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取项目详情")
    // @SaCheckPermission("/project/projects/detail")
    @GetMapping("/project/projects/detail")
    public CommonResult<ProjectProjects> detail(@Valid ProjectProjectsIdParam projectProjectsIdParam) {
        return CommonResult.data(projectProjectsService.detail(projectProjectsIdParam));
    }
}
