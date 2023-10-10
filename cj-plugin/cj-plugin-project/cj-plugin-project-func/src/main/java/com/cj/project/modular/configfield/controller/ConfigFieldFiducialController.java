package com.cj.project.modular.configfield.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.project.modular.configfield.param.*;
import com.cj.project.modular.configfield.result.ConfigFieldFiducialResult;
import com.cj.project.modular.configfield.service.ConfigFieldFiducialGreatService;
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
import com.cj.project.modular.configfield.entity.ConfigFieldFiducial;
import com.cj.project.modular.configfield.service.ConfigFieldFiducialService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 考证字段配置控制器
 *
 * @author Lb
 * @date  2023/08/31 19:28
 */
@Api(tags = "考证字段配置控制器")
@ApiSupport(author = "lb", order = 1)
@RestController
@Validated
public class ConfigFieldFiducialController {

    @Resource
    private ConfigFieldFiducialService configFieldFiducialService;
    @Resource
    private ConfigFieldFiducialGreatService configFieldFiducialGreatService;

    /**
     * 获取考证字段配置
     *
     * @author Lb
     * @date 2023/08/31 19:28
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取考证字段配置")
    @CommonLog("添加考证字段配置")
    @GetMapping("/project/configfield/fiducial/get")
    public CommonResult<List<ConfigFieldFiducialResult>> getList(ConfigFieldFiducialQueryParam configFieldFiducialQueryParam) {
        return CommonResult.data(configFieldFiducialService.getList(configFieldFiducialQueryParam));
    }

    /**
     * 添加考证字段配置
     *
     * @author Lb
     * @date 2023/08/31 19:28
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加考证字段配置")
    @CommonLog("添加考证字段配置")
    @PostMapping("/project/configfield/fiducial/add")
    public CommonResult<String> add(@RequestBody @Valid ConfigFieldFiducialAddParam configFieldFiducialAddParam) {
        configFieldFiducialService.add(configFieldFiducialAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑考证字段配置
     *
     * @author Lb
     * @date 2023/08/31 19:28
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑考证字段配置")
    @CommonLog("编辑考证字段配置")
    @PostMapping("/project/configfield/fiducial/edit")
    public CommonResult<String> edit(@RequestBody @Valid ConfigFieldFiducialEditParam configFieldFiducialEditParam) {
        configFieldFiducialService.edit(configFieldFiducialEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除考证字段配置
     *
     * @author Lb
     * @date 2023/08/31 19:28
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除考证字段配置")
    @CommonLog("删除考证字段配置")
    @PostMapping("/project/configfield/fiducial/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                           CommonValidList<ConfigFieldFiducialIdParam> configFieldFiducialIdParamList) {
        configFieldFiducialService.delete(configFieldFiducialIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取考证字段配置详情
     *
     * @author Lb
     * @date 2023/08/31 19:28
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取考证字段配置详情")
    @GetMapping("/project/configfield/fiducial/detail")
    public CommonResult<ConfigFieldFiducial> detail(@Valid ConfigFieldFiducialIdParam configFieldFiducialIdParam) {
        return CommonResult.data(configFieldFiducialService.detail(configFieldFiducialIdParam));
    }

    /**
     * 获取考证字段配置
     *
     * @author Lb
     * @date 2023/08/31 19:28
     */
    @ApiOperationSupport(order = 11)
    @ApiOperation("创建考证字段配置")
    @CommonLog("创建考证字段配置")
    @GetMapping("/project/configfield/fiducial/creat")
    public CommonResult<String> Create(String projectcode, String instrumentmetatype, String instrumenttype) {
        configFieldFiducialGreatService.Create(projectcode, instrumentmetatype, instrumenttype);
        return CommonResult.ok();
    }
}
