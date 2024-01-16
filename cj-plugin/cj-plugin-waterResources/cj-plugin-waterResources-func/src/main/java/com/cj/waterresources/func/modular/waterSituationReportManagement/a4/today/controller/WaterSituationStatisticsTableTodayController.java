package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.entity.WaterSituationStatisticsTableToday;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.service.WaterSituationStatisticsTableTodayService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 今日水情日报表(WaterSituationStatisticsTableToday)表控制层
 *
 * @author makejava
 * @since 2023-12-23 19:11:06
 */
@Api(tags = "今日水情日报表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterSituationStatisticsTableToday")
public class WaterSituationStatisticsTableTodayController {

    @Autowired
    private WaterSituationStatisticsTableTodayService waterSituationStatisticsTableTodayService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody WaterSituationStatisticsTableToday waterSituationStatisticsTableToday) {
        return waterSituationStatisticsTableTodayService.add(waterSituationStatisticsTableToday);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("查询列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "date")String date) {
        return waterSituationStatisticsTableTodayService.select(date);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "id") String id) {
        return waterSituationStatisticsTableTodayService.delete(id);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody WaterSituationStatisticsTableToday waterSituationStatisticsTableToday) {
        return waterSituationStatisticsTableTodayService.update(waterSituationStatisticsTableToday);
    }

}

