package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.controller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.entity.DayWaterSituationStatisticsTableSyyl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.service.DayWaterSituationStatisticsTableSyylService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity.DayWaterSituationStatisticsTableZcc;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 上游雨量日水情统计表(DayWaterSituationStatisticsTableSyyl)表控制层
 *
 * @author makejava
 * @since 2023-12-23 16:00:14
 */
@Api(tags = "上游雨量日水情统计表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("dayWaterSituationStatisticsTableSyyl")
public class DayWaterSituationStatisticsTableSyylController {

    @Autowired
    private DayWaterSituationStatisticsTableSyylService dayWaterSituationStatisticsTableSyylService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("上游雨量日水情统计表新增")
    @CommonLog("上游雨量日水情统计表新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody List<DayWaterSituationStatisticsTableSyyl> list) {
        return dayWaterSituationStatisticsTableSyylService.add(list);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("上游雨量日水情统计表查询列表")
    @CommonLog("上游雨量日水情统计表查询列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "date")String date) {
        return dayWaterSituationStatisticsTableSyylService.selectList(date);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("上游雨量日水情统计表删除")
    @CommonLog("上游雨量日水情统计表删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        return dayWaterSituationStatisticsTableSyylService.delete(ids);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("上游雨量日水情统计表修改")
    @CommonLog("上游雨量日水情统计表修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<DayWaterSituationStatisticsTableSyyl> list) {
        return dayWaterSituationStatisticsTableSyylService.update(list);
    }

}

