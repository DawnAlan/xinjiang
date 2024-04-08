package com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.bean.req.TenDaysWaterBalanceSelectListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.entity.TenDaysWaterBalance;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.service.TenDaysWaterBalanceService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 旬水量平衡(TenDaysWaterBalance)表控制层
 *
 * @author makejava
 * @since 2023-12-22 18:40:01
 */
@Api(tags = "旬水量平衡")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("tenDaysWaterBalance")
public class TenDaysWaterBalanceController{


    @Autowired
    private TenDaysWaterBalanceService tenDaysWaterBalanceService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("旬水量平衡查询列表")
    @CommonLog(value = "旬水量平衡查询列表")
    @PostMapping("/selectList")
    public RestResponse<List<TenDaysWaterBalance>> selectList(@RequestBody TenDaysWaterBalanceSelectListReq req) {
        return tenDaysWaterBalanceService.selectList(req);
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("四预水资源预告预警供水统计图表")
    @CommonLog(value = "四预水资源预告预警供水统计图表")
    @GetMapping("/selectTotalForIndexWarning")
    public RestResponse selectTotalForIndexWarning(@RequestParam("stationName") String stationName) {
        return tenDaysWaterBalanceService.selectTotalForIndexWarning(stationName);
    }
}

