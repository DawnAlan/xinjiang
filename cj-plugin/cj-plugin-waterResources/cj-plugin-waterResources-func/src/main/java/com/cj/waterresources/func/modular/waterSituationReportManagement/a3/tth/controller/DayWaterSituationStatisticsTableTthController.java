package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.controller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 头屯河水库日水情统计表(DayWaterSituationStatisticsTableTth)表控制层
 *
 * @author makejava
 * @since 2023-12-23 16:01:11
 */
@Api(tags = "头屯河水库日水情统计表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("dayWaterSituationStatisticsTableTth")
public class DayWaterSituationStatisticsTableTthController {

    @Autowired
    private DayWaterSituationStatisticsTableTthService dayWaterSituationStatisticsTableTthService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("头屯河水库日水情统计表新增")
    @CommonLog("头屯河水库日水情统计表新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody List<DayWaterSituationStatisticsTableTth> list) {
        return dayWaterSituationStatisticsTableTthService.add(list);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("头屯河水库日水情统计表查询列表")
    @CommonLog("头屯河水库日水情统计表查询列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "date")String date) {
        return dayWaterSituationStatisticsTableTthService.selectList(date);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("头屯河水库日水情统计表删除")
    @CommonLog("头屯河水库日水情统计表删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        return dayWaterSituationStatisticsTableTthService.delete(ids);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("头屯河水库日水情统计表修改")
    @CommonLog("头屯河水库日水情统计表修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<DayWaterSituationStatisticsTableTth> list) {
        return dayWaterSituationStatisticsTableTthService.update(list);
    }
}

