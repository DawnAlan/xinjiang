package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.controller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.bean.req.DutyRecordsSelectListReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.entity.DutyRecords;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.service.DutyRecordsService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 值班记录(DutyRecords)表控制层
 *
 * @author makejava
 * @since 2023-12-25 16:59:49
 */
@Api(tags = "值班记录")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("dutyRecords")
public class DutyRecordsController {

    @Autowired
    private DutyRecordsService dutyRecordsService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("值班记录新增")
    @CommonLog("值班记录新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody DutyRecords dutyRecords) {
        return dutyRecordsService.add(dutyRecords);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("值班记录查询列表")
    @CommonLog("值班记录查询列表")
    @PostMapping("/selectList")
    public RestResponse selectList(@RequestBody DutyRecordsSelectListReq req) {
        return dutyRecordsService.selectList(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("值班记录删除")
    @CommonLog("值班记录删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "id") String id) {
        return dutyRecordsService.delete(id);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("值班记录修改")
    @CommonLog("值班记录修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody DutyRecords dutyRecords) {
        return dutyRecordsService.update(dutyRecords);
    }
}

