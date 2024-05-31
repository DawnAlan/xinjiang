package com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.bean.req.DayWaterBalanceSelectListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.entity.DayWaterBalance;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.service.DayWaterBalanceService;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.req.IrrigationQuotaListReq;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 日水量平衡表(DayWaterBalance)表控制层
 *
 * @author makejava
 * @since 2023-12-22 18:39:36
 */
@Api(tags = "日水量平衡表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("dayWaterBalance")
public class DayWaterBalanceController{

    @Autowired
    private DayWaterBalanceService dayWaterBalanceService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("日水量平衡表查询列表")
    @CommonLog(value = "日水量平衡表查询列表")
    @PostMapping("/selectList")
    public RestResponse<List<DayWaterBalance>> selectList(@RequestBody DayWaterBalanceSelectListReq req) {
        return dayWaterBalanceService.selectList(req);
    }

    @ApiOperationSupport(order = 8)
    @ApiOperation("查询管理站总水量")
    @CommonLog(value = "查询管理站总水量")
    @GetMapping("/getStationTotalValue")
    public Double getStationTotalValue(@RequestParam("station") String station,
                                       @RequestParam("time") String time) {
        return dayWaterBalanceService.getStationTotalValue(station,time);
    }
}

