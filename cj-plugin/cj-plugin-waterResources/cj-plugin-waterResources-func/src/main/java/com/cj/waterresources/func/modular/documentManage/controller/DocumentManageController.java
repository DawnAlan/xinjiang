package com.cj.waterresources.func.modular.documentManage.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.documentManage.req.QueryReq;
import com.cj.waterresources.func.modular.documentManage.service.DocumentManageService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("documentManage")
@Api(tags = "文档管理模块")
@Validated
@RequiredArgsConstructor
public class DocumentManageController {

    private final DocumentManageService documentManageService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("文件管理模块上传文件")
    @CommonLog(value = "文件管理模块上传文件")
    @ApiImplicitParam(name = "info", value = "{\"documentName\": \"\", \"documentType\": \"\"},documentType类型可为空", dataType = "String")
    @PostMapping("/upload")
    public RestResponse upload(@RequestPart("file") MultipartFile file,
                               @RequestParam("info") String info) {
        return documentManageService.upload(file, info);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("文件管理模块查询列表")
    @CommonLog(value = "文件管理模块查询列表")
    @PostMapping("/queryList")
    public RestResponse queryList(@RequestBody QueryReq req) {
        return documentManageService.queryList(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("文件管理模块删除")
    @CommonLog(value = "文件管理模块删除")
    @PostMapping("/del")
    public RestResponse del(@RequestBody List<String> ids) {
        return documentManageService.del(ids);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("文件管理模块预览")
    @CommonLog(value = "文件管理模块预览")
    @PostMapping("/view")
    public void view(@RequestParam("id") String id, HttpServletResponse response) {
        documentManageService.view(id, response);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("文件管理模块下载文件")
    @CommonLog(value = "文件管理模块下载文件")
    @PostMapping("/download")
    public void download(@RequestParam("id") String id, HttpServletResponse response) {
        documentManageService.download(id, response);
    }
}
