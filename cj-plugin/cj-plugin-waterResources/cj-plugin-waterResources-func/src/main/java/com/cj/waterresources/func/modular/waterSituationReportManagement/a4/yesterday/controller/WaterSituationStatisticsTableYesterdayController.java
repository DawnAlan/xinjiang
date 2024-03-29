package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.controller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.entity.WaterSituationStatisticsTableYesterday;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.service.WaterSituationStatisticsTableYesterdayService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 昨日水情日报表(WaterSituationStatisticsTableYesterday)表控制层
 *
 * @author makejava
 * @since 2023-12-23 19:10:45
 */
@Api(tags = "昨日水情日报表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterSituationStatisticsTableYesterday")
public class WaterSituationStatisticsTableYesterdayController{

    @Autowired
    private WaterSituationStatisticsTableYesterdayService waterSituationStatisticsTableYesterdayService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("昨日水情日报表新增")
    @CommonLog("昨日水情日报表新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody WaterSituationStatisticsTableYesterday waterSituationStatisticsTableYesterday) {
        return waterSituationStatisticsTableYesterdayService.add(waterSituationStatisticsTableYesterday);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("昨日水情日报表查询列表")
    @CommonLog("昨日水情日报表查询列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "date")String date) {
        return waterSituationStatisticsTableYesterdayService.select(date);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("昨日水情日报表删除")
    @CommonLog("昨日水情日报表删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "id") String id) {
        return waterSituationStatisticsTableYesterdayService.delete(id);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("昨日水情日报表修改")
    @CommonLog("昨日水情日报表修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody WaterSituationStatisticsTableYesterday waterSituationStatisticsTableYesterday) {
        return waterSituationStatisticsTableYesterdayService.update(waterSituationStatisticsTableYesterday);
    }
}

