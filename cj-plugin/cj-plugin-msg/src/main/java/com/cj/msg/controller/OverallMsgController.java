package com.cj.msg.controller;

import com.cj.common.model.RestResponse;
import com.cj.msg.entity.OverallMsgInsertReq;
import com.cj.msg.entity.OverallMsgQueryReq;
import com.cj.msg.service.OverallMsgService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 全局消息管理(OverallMsg)表控制层
 *
 * @author makejava
 * @since 2024-04-19 16:30:37
 */
@Api(tags = "全局消息管理")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("overallMsg")
public class OverallMsgController {
    /**
     * 服务对象
     */
    @Resource
    private OverallMsgService overallMsgService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("查询当前用户未读消息数量")
    @GetMapping("/selectCount")
    public RestResponse selectCount() {
        return overallMsgService.selectCount();
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("查询当前用户未读消息列表")
    @GetMapping("/selectInfoSubjectList")
    public RestResponse selectInfoSubjectList() {
        return overallMsgService.selectInfoSubjectList();
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("查询当前用户消息列表详情")
    @GetMapping("/selectDetailsList")
    public RestResponse selectDetailsList(@RequestParam("date")String date) {
        return overallMsgService.selectDetailsList(date);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("阅读消息")
    @GetMapping("/editReadStatus")
    public RestResponse editReadStatus(@RequestParam(value = "id",required = true)String id) {
        return overallMsgService.editReadStatus(id);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("插入数据")
    @PostMapping("/insert")
    public RestResponse insert(@RequestBody OverallMsgInsertReq req) {
        return overallMsgService.insert(req);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("查询数据")
    @PostMapping("/query")
    public RestResponse query(@RequestBody OverallMsgQueryReq req) {
        return overallMsgService.query(req);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("删除数据")
    @PostMapping("/del")
    public RestResponse query(@RequestBody List<String> ids) {
        return RestResponse.ok(overallMsgService.removeBatchByIds(ids));
    }
}

