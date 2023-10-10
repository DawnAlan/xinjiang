
package com.cj.biz.modular.position.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
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
import com.cj.biz.modular.position.entity.BizPosition;
import com.cj.biz.modular.position.param.*;
import com.cj.biz.modular.position.service.BizPositionService;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.common.pojo.CommonValidList;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 岗位控制器
 *
 * @author xuyuxiang
 * @date 2022/4/25 20:40
 */
@Api(tags = "岗位控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 99)
@RestController
@Validated
public class BizPositionController {

    @Resource
    private BizPositionService bizPositionService;

    /**
     * 获取岗位分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取岗位分页")
    @SaCheckPermission("/biz/position/page")
    @GetMapping("/biz/position/page")
    public CommonResult<Page<BizPosition>> page(BizPositionPageParam bizPositionPageParam) {
        return CommonResult.data(bizPositionService.page(bizPositionPageParam));
    }

    /**
     * 添加岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加岗位")
    @CommonLog("添加岗位")
    @SaCheckPermission("/biz/position/add")
    @PostMapping("/biz/position/add")
    public CommonResult<String> add(@RequestBody @Valid BizPositionAddParam bizPositionAddParam) {
        bizPositionService.add(bizPositionAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑岗位")
    @CommonLog("编辑岗位")
    @SaCheckPermission("/biz/position/edit")
    @PostMapping("/biz/position/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizPositionEditParam bizPositionEditParam) {
        bizPositionService.edit(bizPositionEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除岗位")
    @CommonLog("删除岗位")
    @SaCheckPermission("/biz/position/delete")
    @PostMapping("/biz/position/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<BizPositionIdParam> bizPositionIdParamList) {
        bizPositionService.delete(bizPositionIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取岗位详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取岗位详情")
    @SaCheckPermission("/biz/position/detail")
    @GetMapping("/biz/position/detail")
    public CommonResult<BizPosition> detail(@Valid BizPositionIdParam bizPositionIdParam) {
        return CommonResult.data(bizPositionService.detail(bizPositionIdParam));
    }

    /* ====岗位部分所需要用到的选择器==== */

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("获取组织树选择器")
    @SaCheckPermission("/biz/position/orgTreeSelector")
    @GetMapping("/biz/position/orgTreeSelector")
    public CommonResult<List<Tree<String>>> orgTreeSelector() {
        return CommonResult.data(bizPositionService.orgTreeSelector());
    }

    /**
     * 获取岗位选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("获取岗位选择器")
    @SaCheckPermission("/biz/position/positionSelector")
    @GetMapping("/biz/position/positionSelector")
    public CommonResult<Page<BizPosition>> positionSelector(BizPositionSelectorPositionParam bizPositionSelectorPositionParam) {
        return CommonResult.data(bizPositionService.positionSelector(bizPositionSelectorPositionParam));
    }
}
