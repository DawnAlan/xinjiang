package com.cj.project.modular.configfield.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.common.pojo.CommonValidList;
import com.cj.project.modular.configfield.entity.ConfigFieldData;
import com.cj.project.modular.configfield.param.*;
import com.cj.project.modular.configfield.result.ConfigFieldDataResult;
import com.cj.project.modular.configfield.service.ConfigFieldDataGreatService;
import com.cj.project.modular.configfield.service.ConfigFieldDataService;
import com.cj.project.modular.configfield.service.ConfigFieldDataGreatService;
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
import java.util.List;

/**
 * 数据字段配置控制器
 *
 * @author Lb
 * @date  2023/11/08 15:28
 */
@Api(tags = "数据字段配置控制器")
@ApiSupport(author = "CJ_TEAM", order = 1)
@RestController
@Validated
public class ConfigFieldDataController {

    @Resource
    private ConfigFieldDataService configFieldDataService;

    @Resource
    private ConfigFieldDataGreatService configFieldDataGreatService;

    /**
     * 获取数据字段配置
     *
     * @author Lb
     * @date 2023/11/08 15:28
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取数据字段配置")
    @CommonLog("添加数据字段配置")
    @GetMapping("/project/configField/data/get")
    public CommonResult<List<ConfigFieldDataResult>> getList(ConfigFieldQueryParam configFieldQueryParam) {
        return CommonResult.data(configFieldDataService.getList(configFieldQueryParam));
    }

    /**
     * 添加数据字段配置
     *
     * @author Lb
     * @date  2023/11/08 15:28
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加数据字段配置")
    @CommonLog("添加数据字段配置")
    @PostMapping("/project/configField/data/add")
    public CommonResult<String> add(@RequestBody @Valid ConfigFieldDataAddParam configFieldDataAddParam) {
        configFieldDataService.add(configFieldDataAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑数据字段配置
     *
     * @author Lb
     * @date  2023/11/08 15:28
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑数据字段配置")
    @CommonLog("编辑数据字段配置")
    @PostMapping("/project/configField/data/edit")
    public CommonResult<String> edit(@RequestBody @Valid ConfigFieldDataEditParam configFieldDataEditParam) {
        configFieldDataService.edit(configFieldDataEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除数据字段配置
     *
     * @author Lb
     * @date  2023/11/08 15:28
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除数据字段配置")
    @CommonLog("删除数据字段配置")
    @PostMapping("/project/configField/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<ConfigFieldIdParam> configFieldIdParamList) {
        configFieldDataService.delete(configFieldIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取数据字段配置详情
     *
     * @author Lb
     * @date  2023/11/08 15:28
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取数据字段配置详情")
    @GetMapping("/project/configField/data/detail")
    public CommonResult<ConfigFieldData> detail(@Valid ConfigFieldIdParam configFieldIdParam) {
        return CommonResult.data(configFieldDataService.detail(configFieldIdParam));
    }

    /**
     * 创建数据字段配置
     *
     * @author Lb
     * @date 2023/11/08 17:28
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("创建数据字段配置")
    @CommonLog("创建数据字段配置")
    @SaCheckPermission("/project/configField/data/creat")
    @GetMapping("/project/configField/data/creat")
    public CommonResult<String> Create(String projectCode, String instrumentMetaType, String instrumentType) {
        configFieldDataGreatService.Create(projectCode, instrumentMetaType, instrumentType);
        return CommonResult.ok();
    }

    /**
     * 批量更新数据字段配置
     *
     * @author : lb
     * @date : 2023/11/08 17:28
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("批量更新数据字段配置")
    @CommonLog("批量更新数据字段配置")
    @GetMapping("/project/configField/data/updateFields")
    public CommonResult<String> UpdateFields(String projectCode, String instrumentType, String[] Fields,String isDisplay) {
        configFieldDataGreatService.UpdateFieldDisplay(projectCode,instrumentType,Fields,isDisplay);
        return CommonResult.ok();
    }
}
