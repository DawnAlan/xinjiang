package com.cj.approval.func.modular.approval.instructionViewing.controller;

import com.cj.approval.func.modular.approval.instructionViewing.entity.InstructionViewing;
import com.cj.approval.func.modular.approval.instructionViewing.service.InstructionViewingService;
import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * 指令查看表(InstructionViewing)表控制层
 *
 * @author makejava
 * @since 2023-12-19 19:41:47
 */
@Api(tags = "指令查看表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("instructionViewing")
public class InstructionViewingController{

    @Autowired
    private InstructionViewingService instructionViewingService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("指令查看表通过指令id查询列表")
    @CommonLog(value = "指令查看表通过指令id查询列表")
    @GetMapping("/selectListByInstructionId")
    public RestResponse<List<InstructionViewing>> selectListByInstructionId(@RequestParam("id") String id) {
        return instructionViewingService.selectListByInstructionId(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("指令查看表修改阅读状态")
    @CommonLog(value = "指令查看表修改阅读状态")
    @GetMapping("/updateRedsStatus")
    public RestResponse updateRedsStatus(@RequestParam("id") String id) {
        InstructionViewing byId = instructionViewingService.getById(id);
        if(byId.getReadTime() !=null){
            return RestResponse.ok();
        }
        boolean update = instructionViewingService.lambdaUpdate().set(InstructionViewing::getReadTime,new Date()).
                set(InstructionViewing::getViewStatus, 1).eq(InstructionViewing::getId, id).update();
        if(update){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

}

