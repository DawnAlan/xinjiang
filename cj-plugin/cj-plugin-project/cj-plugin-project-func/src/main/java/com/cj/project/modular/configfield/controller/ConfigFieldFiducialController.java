package com.cj.project.modular.configfield.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialDto;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialPageDto;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialQueryDto;
import com.cj.project.api.configfield.entity.ConfigFieldFiducial;
import com.cj.project.modular.configfield.result.ConfigFieldFiducialResult;
import com.cj.project.modular.configfield.service.ConfigFieldFiducialGreatService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.project.modular.configfield.service.ConfigFieldFiducialService;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @PostMapping("/project/configfield/fiducial/get")
    public CommonResult<List<ConfigFieldFiducialResult>> getList(ConfigFieldFiducialQueryDto configFieldFiducialQueryDto) {
        return CommonResult.data(configFieldFiducialService.getList(configFieldFiducialQueryDto));
    }


    /**
     * 获取考证字段配置分页
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取数据字段配置分页")
    @PostMapping("/project/configfield/page")
    public CommonResult<Page<ConfigFieldFiducial>> page(ConfigFieldFiducialPageDto configFieldFiducialPageDto) {
        return CommonResult.data(configFieldFiducialService.page(configFieldFiducialPageDto));
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
    public CommonResult<String> add(@RequestBody @Valid ConfigFieldFiducial configFieldFiducial) {
        configFieldFiducialService.add(configFieldFiducial);
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
    public CommonResult<String> edit(@RequestBody @Valid ConfigFieldFiducial configFieldFiducial) {
        configFieldFiducialService.edit(configFieldFiducial);
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
                                                   List<String> idList) {
        configFieldFiducialService.delete(idList);
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
    @GetMapping("/project/configfield/fiducial/detail/{id}")
    public CommonResult<ConfigFieldFiducial> detail(@PathVariable("id") String id) {
        return CommonResult.data(configFieldFiducialService.detail(id));
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
    @PostMapping("/project/configfield/fiducial/creat")
    public CommonResult<String> Create(@RequestBody ConfigFieldFiducialDto configFieldFiducialExportDto) {
        configFieldFiducialGreatService.Create(configFieldFiducialExportDto);
        return CommonResult.ok();
    }


    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "项目仪器模板导出" , tags = "文件名需要前端解码")
    @PostMapping("/project/configfield/fiducial/templateExport")
    public void templateExport(@RequestBody ConfigFieldFiducialDto configFieldFiducialExportDto,
                               HttpServletRequest request , HttpServletResponse response) {
        configFieldFiducialService.templateExport(configFieldFiducialExportDto , request , response);
    }


    @ApiOperationSupport(order = 13)
    @ApiOperation("项目仪器数据导入")
    @PostMapping("/project/configfield/fiducial/dataImport")
    public CommonResult dataImport(@RequestParam(value = "file")  MultipartFile file ) {

        return configFieldFiducialService.dataImport(file);
    }

    @ApiOperationSupport(order = 14)
    @ApiOperation("项目仪器数据导出")
    @PostMapping("/project/configfield/fiducial/dataExport")
    public void dataExport(@RequestBody ConfigFieldFiducialDto configFieldFiducialExportDto,
                           HttpServletRequest request , HttpServletResponse response) {
        configFieldFiducialService.templateExport(configFieldFiducialExportDto , request , response);
    }

    
}
