package com.cj.waterresources.func.modular.benchmarkTraffic.controller;


import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.benchmarkTraffic.bean.req.ApprovalReq;
import com.cj.waterresources.func.modular.benchmarkTraffic.bean.req.BenchmarkTrafficListReq;
import com.cj.waterresources.func.modular.benchmarkTraffic.entity.BenchmarkTraffic;
import com.cj.waterresources.func.modular.benchmarkTraffic.service.BenchmarkTrafficService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 基准流量表(BenchmarkTraffic)表控制层
 *
 * @author makejava
 * @since 2024-07-17 11:23:38
 */
@Api(tags = "基准流量")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("benchmarkTraffic")
public class BenchmarkTrafficController{
    /**
     * 服务对象
     */
    @Resource
    private BenchmarkTrafficService benchmarkTrafficService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("基准流量模块新增")
    @CommonLog(value = "基准流量模块新增")
    @PostMapping("/insert")
    public RestResponse insert(@RequestBody BenchmarkTraffic benchmarkTraffic) {
        return benchmarkTrafficService.add(benchmarkTraffic);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("基准流量模块删除")
    @CommonLog(value = "基准流量模块删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        return benchmarkTrafficService.deleteById(id);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("基准流量模块查询")
    @CommonLog(value = "基准流量模块查询")
    @PostMapping("/selectList")
    public RestResponse selectList(@RequestBody BenchmarkTrafficListReq req) {
        return benchmarkTrafficService.selectList(req);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("基准流量模块站审批")
    @CommonLog(value = "基准流量模块站审批")
    @PostMapping("/approvalForSite")
    public RestResponse approvalForSite(@RequestBody ApprovalReq req) {
        return benchmarkTrafficService.approvalForSite(req);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("基准流量模块局审批")
    @CommonLog(value = "基准流量模块局审批")
    @PostMapping("/approvalForBureau")
    public RestResponse approvalForBureau(@RequestBody ApprovalReq req) {
        return benchmarkTrafficService.approvalForBureau(req);
    }
}

