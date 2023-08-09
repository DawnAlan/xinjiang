
package com.cj.gen.modular.basic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.gen.modular.basic.entity.GenBasic;
import com.cj.gen.modular.basic.result.GenBasicMobileModuleSelectorResult;
import com.cj.gen.modular.basic.result.GenBasicPreviewResult;
import com.cj.gen.modular.basic.result.GenBasicTableColumnResult;
import com.cj.gen.modular.basic.result.GenBasicTableResult;
import com.cj.gen.modular.basic.service.GenBasicService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.common.pojo.CommonValidList;
import com.cj.gen.modular.basic.param.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 代码生成基础控制器
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Api(tags = "代码生成基础控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class GenBasicController {

    @Resource
    private GenBasicService genBasicService;

    /**
     * 获取代码生成基础分页
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取代码生成基础分页")
    @GetMapping("/gen/basic/page")
    public CommonResult<Page<GenBasic>> page(GenBasicPageParam genBasicPageParam) {
        return CommonResult.data(genBasicService.page(genBasicPageParam));
    }

    /**
     * 添加代码生成基础
     *
     * @author yubaoshan
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加代码生成基础")
    @CommonLog("添加代码生成基础")
    @PostMapping("/gen/basic/add")
    public CommonResult<GenBasic> add(@RequestBody @Valid GenBasicAddParam genBasicAddParam) {
        return CommonResult.data(genBasicService.add(genBasicAddParam));
    }

    /**
     * 编辑代码生成基础
     *
     * @author yubaoshan
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑代码生成基础")
    @CommonLog("编辑代码生成基础")
    @PostMapping("/gen/basic/edit")
    public CommonResult<GenBasic> edit(@RequestBody @Valid GenBasicEditParam genBasicEditParam) {
        return CommonResult.data(genBasicService.edit(genBasicEditParam));
    }

    /**
     * 删除代码生成基础
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除代码生成基础")
    @CommonLog("删除代码生成基础")
    @PostMapping("/gen/basic/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<GenBasicIdParam> genBasicIdParamList) {
        genBasicService.delete(genBasicIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取代码生成基础详情
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取代码生成基础详情")
    @GetMapping("/gen/basic/detail")
    public CommonResult<GenBasic> detail(@Valid GenBasicIdParam genBasicIdParam) {
        return CommonResult.data(genBasicService.detail(genBasicIdParam));
    }

    /**
     * 获取所有表信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("获取所有表信息")
    @GetMapping("/gen/basic/tables")
    public CommonResult<List<GenBasicTableResult>> dbsTable() {
        return CommonResult.data(genBasicService.tables());
    }

    /**
     * 获取表内所有字段信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("获取表内所有字段信息")
    @GetMapping("/gen/basic/tableColumns")
    public CommonResult<List<GenBasicTableColumnResult>> tableColumns(GenBasicTableColumnParam genBasicTableColumnParam) {
        return CommonResult.data(genBasicService.tableColumns(genBasicTableColumnParam));
    }

    /**
     * 执行代码生成
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    @ApiOperationSupport(order = 8)
    @ApiOperation("执行代码生成（压缩包）")
    @CommonLog("执行代码生成（压缩包）")
    @GetMapping(value = "/gen/basic/execGenZip", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void execGenZip(@Valid GenBasicIdParam genBasicIdParam, HttpServletResponse response) throws IOException {
        genBasicService.execGenZip(genBasicIdParam, response);
    }

    /**
     * 执行代码生成
     *
     * @author yubaoshan
     * @date 2022/10/31 02:17
     **/
    @ApiOperationSupport(order = 9)
    @ApiOperation("执行代码生成（项目内）")
    @CommonLog("执行代码生成（项目内）")
    @PostMapping(value = "/gen/basic/execGenPro")
    public CommonResult<String> execGenPro(@RequestBody @Valid GenBasicIdParam genBasicIdParam, HttpServletResponse response) throws IOException {
        genBasicService.execGenPro(genBasicIdParam, response);
        return CommonResult.ok();
    }

    /**
     * 预览代码生成
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    @ApiOperationSupport(order = 10)
    @ApiOperation("预览代码生成")
    @CommonLog("预览代码生成")
    @GetMapping(value = "/gen/basic/previewGen")
    public CommonResult<GenBasicPreviewResult> previewGen(@Valid GenBasicIdParam genBasicIdParam) {
        return CommonResult.data(genBasicService.previewGen(genBasicIdParam));
    }

}

