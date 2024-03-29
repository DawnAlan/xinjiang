package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tjc.controller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tjc.entity.DayWaterSituationStatisticsTableTjc;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tjc.service.DayWaterSituationStatisticsTableTjcService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 调节池日水情统计表(DayWaterSituationStatisticsTableTjc)表控制层
 *
 * @author makejava
 * @since 2023-12-23 16:00:33
 */
@Api(tags = "调节池日水情统计表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("dayWaterSituationStatisticsTableTjc")
public class DayWaterSituationStatisticsTableTjcController {

    @Autowired
    private DayWaterSituationStatisticsTableTjcService dayWaterSituationStatisticsTableTjcService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("调节池日水情统计表新增")
    @CommonLog("调节池日水情统计表新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody List<DayWaterSituationStatisticsTableTjc> list) {
        return dayWaterSituationStatisticsTableTjcService.add(list);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("调节池日水情统计表查询列表")
    @CommonLog("调节池日水情统计表查询列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "date")String date) {
        return dayWaterSituationStatisticsTableTjcService.selectList(date);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("调节池日水情统计表删除")
    @CommonLog("调节池日水情统计表删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        return dayWaterSituationStatisticsTableTjcService.delete(ids);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("调节池日水情统计表修改")
    @CommonLog("调节池日水情统计表修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<DayWaterSituationStatisticsTableTjc> list) {
        return dayWaterSituationStatisticsTableTjcService.update(list);
    }
}

