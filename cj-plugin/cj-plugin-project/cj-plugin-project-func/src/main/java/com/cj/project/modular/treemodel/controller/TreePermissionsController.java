package com.cj.project.modular.treemodel.controller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.project.api.treemodel.dto.TreePermissionsDto;
import com.cj.project.modular.treemodel.service.TreePermissionsService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 树目录权限组基本信息表Controller
 *
 * @author zsy
 * @date 2024-01-15
 */
@Api(tags = "树目录权限组基本信息表")
@ApiSupport(author = "zsy", order = 1)
@RestController
@Validated
public class TreePermissionsController {

    @Resource
    private TreePermissionsService treePermissionsService;

    /**
     * 添加或修改树目录权限组
     *
     * @author zsy
     * @date 2024/01/15 16:41
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("添加或修改树目录权限组")
    @CommonLog("添加或修改树目录权限组")
    @PostMapping("/project/permissions/save")
    public CommonResult save(@Valid @RequestBody TreePermissionsDto treePermissionsDto) {
        boolean save = treePermissionsService.savePermissions(treePermissionsDto);
        return save ? CommonResult.ok() : CommonResult.error();
    }

    /**
     * 删除树目录权限组
     *
     * @author zsy
     * @date 2024/01/15 16:41
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("删除树目录权限组")
    @CommonLog("删除树目录权限组")
    @GetMapping("/project/permissions/deletePermissions")
    public CommonResult deletePermissions(@RequestParam("id") String id) {
        boolean editResult = treePermissionsService.deletePermissions(id);
        return editResult ? CommonResult.ok() : CommonResult.error("删除失败！");
    }

    /**
     * 绑定树目录权限组
     *
     * @author zsy
     * @date 2024/01/15 16:41
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("绑定树目录权限组")
    @CommonLog("绑定树目录权限组")
    @PostMapping("/project/permissions/bindPermissions")
    public CommonResult bindPermissions(@RequestBody TreePermissionsDto treePermissionsDto) {
        boolean save = treePermissionsService.savePermissions(treePermissionsDto);
        return save ? CommonResult.ok() : CommonResult.error();
    }

}