package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.*;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.AllService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "A3表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("all")
public class AllController {

    @Autowired
    private AllService allService;

    @ApiOperationSupport(order = 2)
    @ApiOperation("A3表删除")
    @CommonLog("A3表删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "date")String date) {
        return allService.deleteAll(date);
    }

    @ApiOperationSupport(order = 1)
    @ApiOperation("A3表水情查询统计")
    @CommonLog(value = "A3表水情查询统计")
    @PostMapping("/statistics")
    public RestResponse statistics(@RequestBody A3StatisticsReq req) {
        return allService.statistics(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("A3表工业水费根据站点查询时间区间流量")
    @CommonLog(value = "A3表工业水费根据站点查询时间区间流量")
    @PostMapping("/selectListForIndustrialWaterFee")
    public RestResponse selectListForIndustrialWaterFee(@RequestBody SelectListForIndustrialWaterFeeReq req) {
        return allService.selectListForIndustrialWaterFee(req);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("A3表水情报表管理-水库报表")
    @CommonLog(value = "A3表水情报表管理-水库报表")
    @PostMapping("/selectReportForms")
    public RestResponse selectReportForms(@RequestBody ReportFormsReq req) {
        return allService.selectReportForms(req);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("A3表查询2库的拦蓄水量")
    @CommonLog(value = "A3表查询2库的拦蓄水量")
    @GetMapping("/selectFloodRetentionCapacity")
    public RestResponse selectFloodRetentionCapacity(@RequestParam("date") String date) {
        return allService.selectFloodRetentionCapacity(date);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("A3表查询水资源首页今日水情")
    @CommonLog(value = "A3表查询水资源首页今日水情")
    @GetMapping("/selectTodayWaterSituation")
    public RestResponse selectTodayWaterSituation(@RequestParam("date") String date) {
        return allService.selectTodayWaterSituation(date);
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("A3表更新全部昨日均")
    @CommonLog(value = "A3表更新全部昨日均")
    @GetMapping("/updateInfoDate")
    public RestResponse updateInfoDate() {
        return allService.updateInfoDate();
    }

    @ApiOperationSupport(order = 8)
    @ApiOperation("A3表查询2库的拦蓄水量(new)")
    @CommonLog(value = "A3表查询2库的拦蓄水量(new)")
    @GetMapping("/selectFloodRetentionCapacityNew")
    public RestResponse selectFloodRetentionCapacityNew(@RequestParam("date") String date,@RequestParam("ids") String ids) {
        return allService.selectFloodRetentionCapacityNew(date,ids);
    }

    @ApiOperationSupport(order = 8)
    @ApiOperation("A3表查询2库的出入库流量详情")
    @CommonLog(value = "A3表查询2库的出入库流量详情")
    @GetMapping("/selectCapacityOutPutDetail")
    public RestResponse selectCapacityOutPutDetail(@RequestParam("date") String date,@RequestParam("ids") String ids) {
        return allService.selectCapacityOutPutDetail(date,ids);
    }

    @ApiOperationSupport(order = 9)
    @ApiOperation("防洪-查询今日水情和实时水情")
    @CommonLog(value = "防洪-查询今日水情和实时水情")
    @GetMapping("/selectTodayWaterSituationForFlood")
    public RestResponse selectTodayWaterSituationForFlood(@RequestParam("date") String date,@RequestParam("ids") String ids) {
        return allService.selectTodayWaterSituationForFlood(date,ids);
    }

    @ApiOperationSupport(order = 9)
    @ApiOperation("防洪-查询实时雨量")
    @CommonLog(value = "防洪-查询实时雨量")
    @GetMapping("/selectTodayRainfall")
    public RestResponse selectTodayRainfall(@RequestParam("date") String date,@RequestParam(value = "hour",required = false) Integer hour) {
        return allService.selectTodayRainfall(date,hour);
    }

    @ApiOperationSupport(order = 10)
    @ApiOperation("A3表查询水资源首页今日水情过程线")
    @CommonLog(value = "A3表查询水资源首页今日水情过程线")
    @PostMapping("/selectTodayWaterSituationSelectById")
    public RestResponse selectTodayWaterSituationSelectById(@RequestBody SelectTodayWaterSituationSelectByIdReq req) {
        return allService.selectTodayWaterSituationSelectById(req);
    }

    @ApiOperationSupport(order = 10)
    @ApiOperation("A3表查询查询2库水位和库容曲线")
    @CommonLog(value = "A3表查询查询2库水位和库容曲线")
    @PostMapping("/selectReservoirHistoryList")
    public RestResponse selectReservoirHistoryList(@RequestBody SelectReservoirHistoryListReq req) {
        return allService.selectReservoirHistoryList(req);
    }

    @ApiOperationSupport(order = 11)
    @ApiOperation("查询2库水位库容")
    @CommonLog(value = "查询2库水位库容")
    @PostMapping("/getRealTimeWaterLevelData")
    public RestResponse getRealTimeWaterLevelData(@RequestBody GetRealTimeWaterLevelDataReq req) {
        return allService.getRealTimeWaterLevelData(req.getDate());
    }
}
