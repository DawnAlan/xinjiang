package com.cj.project.modular.configfield.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialDto;
import com.cj.project.api.configfield.dto.ConfigFieldQueryDto;
import com.cj.project.api.configfield.entity.ConfigFieldData;
import com.cj.project.api.configfield.entity.ConfigFieldFiducial;
import com.cj.project.modular.configfield.result.ConfigFieldDataResult;
import com.cj.project.modular.configfield.service.ConfigFieldDataGreatService;
import com.cj.project.modular.configfield.service.impl.ConfigFieldDataServiceImpl;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 数据字段配置控制器
 *
 * @author Lb
 * @date  2023/08/31 19:28
 */
@Api(tags = "数据字段配置控制器")
@ApiSupport(author = "CJ_TEAM", order = 1)
@RestController
@Validated
public class ConfigFieldDataController {

    @Resource
    private ConfigFieldDataServiceImpl configFieldDataService;
    @Resource
    private ConfigFieldDataGreatService configFieldDataGreatService;

    /**
     * 获取数据字段配置
     *
     * @author Lb
     * @date 2023/08/31 19:28
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取数据字段配置")
    @CommonLog("获取数据字段配置")
    @PostMapping("/project/configfield/data/get")
    public CommonResult<List<ConfigFieldDataResult>> getList(@RequestBody ConfigFieldQueryDto configFieldQueryDto) {
        return CommonResult.data(configFieldDataService.getList(configFieldQueryDto));
    }


    /**
     * 添加考证字段配置
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加数据字段配置")
    @CommonLog("添加数据字段配置")
    @SaCheckPermission("/project/configfield/add")
    @PostMapping("/project/configfield/add")
    public CommonResult<String> add(@RequestBody @Valid ConfigFieldData configFieldData) {
        configFieldDataService.add(configFieldData);
        return CommonResult.ok();
    }

    /**
     * 编辑考证字段配置
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑数据字段配置")
    @CommonLog("编辑考证数据配置")
    @SaCheckPermission("/project/configfield/data/deletePermissions")
    @PostMapping("/project/configfield/data/edit")
    public CommonResult<String> edit(@RequestBody @Valid ConfigFieldData configFieldData) {
        configFieldDataService.edit(configFieldData);
        return CommonResult.ok();
    }

    /**
     * 删除考证字段配置
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除数据字段配置")
    @CommonLog("删除考证数据配置")
    @SaCheckPermission("/project/configfield/data/delete")
    @PostMapping("/project/configfield/data/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                           List<String> idList) {
        configFieldDataService.delete(idList);
        return CommonResult.ok();
    }

    /**
     * 获取考证字段配置详情
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取数据字段配置详情")
    @GetMapping("/project/configfield/data/detail")
    public CommonResult<ConfigFieldData> detail(@PathVariable("id") String id) {
        return CommonResult.data(configFieldDataService.detail(id));
    }
    /**
     * 创建数据字段配置
     *
     * @author Lb
     * @date 2023/08/31 19:28
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("创建数据字段配置")
    @CommonLog("创建数据字段配置")
    @PostMapping("/project/configfield/data/creat")
    public CommonResult<String> Create(String projectCode, String instrumentType,String instrumentMetaType) {
        configFieldDataGreatService.CopyCreate(projectCode,instrumentType,instrumentMetaType);
        return CommonResult.ok();
    }
}
