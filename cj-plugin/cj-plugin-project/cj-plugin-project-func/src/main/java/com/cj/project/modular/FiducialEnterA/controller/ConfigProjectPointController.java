package com.cj.project.modular.FiducialEnterA.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.project.modular.FiducialEnterA.entity.ConfigProjectPoint;
import com.cj.project.modular.FiducialEnterA.param.ConfigProjectPointAddParam;
import com.cj.project.modular.FiducialEnterA.param.ConfigProjectPointEditParam;
import com.cj.project.modular.FiducialEnterA.param.ConfigProjectPointIdParam;
import com.cj.project.modular.FiducialEnterA.param.ConfigProjectPointPageParam;
import com.cj.project.modular.FiducialEnterA.service.ConfigProjectPointService;
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

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 集成测点配置控制器
 *
 * @author Lb
 * @date  2023/11/23 10:20
 */
@Api(tags = "集成测点配置控制器")
@ApiSupport(author = "lb", order = 1)
@RestController
@Validated
public class ConfigProjectPointController {

    @Resource
    private ConfigProjectPointService configProjectPointService;


    /**
     * 添加
     *
     * @author Lb
     * @date  2023/11/23 10:20
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加ConfigProjectPoint")
    @CommonLog("添加ConfigProjectPoint")
    @PostMapping("/transport/ConfigProjectPoint/add")
    public CommonResult<String> add(@RequestBody @Valid ConfigProjectPointAddParam configProjectPointAddParam) {
        configProjectPointService.add(configProjectPointAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑
     *
     * @author Lb
     * @date  2023/11/23 10:20
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑ConfigProjectPoint")
    @CommonLog("编辑ConfigProjectPoint")
    @PostMapping("/transport/ConfigProjectPoint/edit")
    public CommonResult<String> edit(@RequestBody @Valid ConfigProjectPointEditParam configProjectPointEditParam) {
        configProjectPointService.edit(configProjectPointEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除ConfigProjectPoint
     *
     * @author Lb
     * @date  2023/11/23 10:20
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除ConfigProjectPoint")
    @CommonLog("删除ConfigProjectPoint")
    @PostMapping("/transport/ConfigProjectPoint/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<ConfigProjectPointIdParam> configProjectPointIdParamList) {
        configProjectPointService.delete(configProjectPointIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取FiducialEnterA详情
     *
     * @author Lb
     * @date  2023/11/23 10:20
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取ConfigProjectPoint详情")
    @GetMapping("/transport/ConfigProjectPoint/detail")
    public CommonResult<ConfigProjectPoint> detail(@Valid ConfigProjectPointIdParam configProjectPointIdParam) {
        return CommonResult.data(configProjectPointService.detail(configProjectPointIdParam));
    }
}
