package com.cj.project.modular.treemodel.controller;

import cn.hutool.core.lang.tree.Tree;
import com.cj.project.modular.treemodel.param.*;
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
import com.cj.project.modular.treemodel.entity.TreeModel;
import com.cj.project.modular.treemodel.service.TreeModelService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 测点树控制器
 *
 * @author Lb
 * @date  2023/09/14 16:41
 */
@Api(tags = "测点树控制器")
@ApiSupport(author = "LB", order = 1)
@RestController
@Validated
public class TreeModelController {

    @Resource
    private TreeModelService treeModelService;

    /**
     * 获取测点树
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取测点树")
    @CommonLog("获取测点树")
    @GetMapping("/project/treemodel/tree")
    public CommonResult<List<Tree<String>>> Tree(TreeModelTreeParam treeModelTreeParam) {
        return CommonResult.data(treeModelService.tree(treeModelTreeParam));
    }

    /**
     * 添加测点树节点
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加测点树")
    @CommonLog("添加测点树")
    @PostMapping("/project/treemodel/add")
    public CommonResult<String> add(@RequestBody @Valid TreeModelDto treeModelAddParam) {
        treeModelService.add(treeModelAddParam);
        return CommonResult.ok();
    }

    /**
     * 绑定测点到测点树
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("绑定测点到测点树")
    @CommonLog("绑定测点到测点树")
    @PostMapping("/project/treemodel/addpointnode")
    public CommonResult<String> addPointNode(@RequestBody @Valid TreePointNodeAddParam pointNodeAddParam) {
        treeModelService.addPointNode(pointNodeAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑测点树
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑测点树")
    @CommonLog("编辑测点树")
    @PostMapping("/project/treemodel/edit")
    public CommonResult<String> edit(@RequestBody @Valid TreeModelDto treeModelEditParam) {
        treeModelService.edit(treeModelEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除测点树
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除测点树")
    @CommonLog("删除测点树")
    @PostMapping("/project/treemodel/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<TreeModelTreeParam> treeModelIdParamList) {
        treeModelService.delete(treeModelIdParamList);
        return CommonResult.ok();
    }

    /**
     * 删除绑定的测点
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除绑定的测点")
    @CommonLog("删除绑定的测点")
    @PostMapping("/project/treemodel/deletepoint")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                           List<String> pointIdList, String category) {
        treeModelService.deletePointNode(pointIdList, category);
        return CommonResult.ok();
    }

    /**
     * 获取测点树详情
     *
     * @author Lb
     * @date  2023/09/14 16:41
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取测点树详情")
    @GetMapping("/project/treemodel/detail")
    public CommonResult<TreeModel> detail(@Valid TreeModelTreeParam treeModelIdParam) {
        return CommonResult.data(treeModelService.detail(treeModelIdParam));
    }
}
