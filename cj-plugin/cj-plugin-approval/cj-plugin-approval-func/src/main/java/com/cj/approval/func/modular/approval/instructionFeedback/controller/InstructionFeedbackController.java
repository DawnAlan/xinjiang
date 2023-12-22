package com.cj.approval.func.modular.approval.instructionFeedback.controller;

import com.cj.approval.func.modular.approval.approvalManagement.entity.ApprovalManagement;
import com.cj.approval.func.modular.approval.instructionFeedback.entity.InstructionFeedback;
import com.cj.approval.func.modular.approval.instructionFeedback.service.InstructionFeedbackService;
import com.cj.common.model.RestResponse;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 指令反馈表(InstructionFeedback)表控制层
 *
 * @author makejava
 * @since 2023-12-19 19:41:29
 */
@Api(tags = "指令反馈表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("instructionFeedback")
public class InstructionFeedbackController {

    @Autowired
    private InstructionFeedbackService instructionFeedbackService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("查询详情")
    @GetMapping("/selectListByInstructionViewId")
    public RestResponse<List<InstructionFeedback>> selectListByInstructionViewId(@RequestParam("id") String id) {
        return instructionFeedbackService.selectListByInstructionViewId(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody InstructionFeedback instructionFeedback) {
        return instructionFeedbackService.add(instructionFeedback);
    }

}

