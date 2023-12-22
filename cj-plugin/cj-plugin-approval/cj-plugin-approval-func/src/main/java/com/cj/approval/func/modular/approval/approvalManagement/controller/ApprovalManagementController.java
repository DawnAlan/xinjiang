package com.cj.approval.func.modular.approval.approvalManagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.approval.func.modular.approval.approvalManagement.bean.req.SelectListReq;
import com.cj.approval.func.modular.approval.approvalManagement.entity.ApprovalManagement;
import com.cj.approval.func.modular.approval.approvalManagement.service.ApprovalManagementService;
import com.cj.common.model.RestResponse;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 审批管理表(ApprovalManagement)表控制层
 *
 * @author makejava
 * @since 2023-12-19 19:41:00
 */

@Api(tags = "审批管理表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("approvalManagement")
public class ApprovalManagementController{

    @Autowired
    private ApprovalManagementService approvalManagementService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody ApprovalManagement approvalManagement) {
        return approvalManagementService.add(approvalManagement);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        return approvalManagementService.delete(id);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody ApprovalManagement approvalManagement) {
        return approvalManagementService.update(approvalManagement);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<IPage<ApprovalManagement>> select(@RequestBody SelectListReq req) {
        return approvalManagementService.selectList(req);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("查询详情")
    @GetMapping("/selectByIds")
    public RestResponse<List<ApprovalManagement>> selectByIds(@RequestParam("ids") String ids) {
        return approvalManagementService.selectByIds(ids);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("获取组织")
    @GetMapping("/getOrgList")
    public RestResponse getOrgList() {
        return approvalManagementService.getOrgList();
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("预览调度指令单")
    @GetMapping("/thymeleafExport")
    public void thymeleafExport(HttpServletResponse response,@RequestParam("id") String id) {
         approvalManagementService.thymeleafExport(response,id);
    }

    @ApiOperationSupport(order = 8)
    @ApiOperation("下载调度指令单")
    @GetMapping("/downFile")
    public void downFile(HttpServletResponse response,@RequestParam("id") String id) {
        approvalManagementService.downFile(response,id);
    }
    @ApiOperationSupport(order = 9)
    @ApiOperation("测试预览")
    @GetMapping("/testView")
    public void testView(HttpServletResponse response) {
        approvalManagementService.testView(response);
    }
}

