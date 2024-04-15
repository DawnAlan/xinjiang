package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.ApprovalTrafficOverviewTableAddReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.SelectListForIndustrialWaterFeeReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity.ApprovalTrafficOverviewTable;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.ApprovalTrafficOverviewTableService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 流量概览表(ApprovalTrafficOverviewTable)表控制层
 *
 * @author makejava
 * @since 2024-04-09 16:34:47
 */
@Api(tags = "流量概览表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("approvalTrafficOverviewTable")
public class ApprovalTrafficOverviewTableController {
    /**
     * 服务对象
     */
    @Resource
    private ApprovalTrafficOverviewTableService approvalTrafficOverviewTableService;



    @ApiOperationSupport(order = 1)
    @ApiOperation("流量概览表查询方案列表")
    @CommonLog(value = "流量概览表查询方案列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "time", required = false) String time,
                                   @RequestParam(value = "name", required = false) String name) {
        return approvalTrafficOverviewTableService.selectList(time, name);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("流量概览表新增方案列表")
    @CommonLog(value = "流量概览表新增方案列表")
    @PostMapping("/add")
    public RestResponse add(@RequestBody ApprovalTrafficOverviewTableAddReq req) {
        return approvalTrafficOverviewTableService.add(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("流量概览表删除方案列表")
    @CommonLog(value = "流量概览表删除方案列表")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "id") String id) {
        return approvalTrafficOverviewTableService.delete(id);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("查询8点的流量")
    @CommonLog(value = "查询8点的流量")
    @GetMapping("/synchronizationEightData")
    public RestResponse synchronizationEightData(@RequestParam(value = "id", required = true) String id) {
        return approvalTrafficOverviewTableService.synchronizationEightData(id);
    }
}

