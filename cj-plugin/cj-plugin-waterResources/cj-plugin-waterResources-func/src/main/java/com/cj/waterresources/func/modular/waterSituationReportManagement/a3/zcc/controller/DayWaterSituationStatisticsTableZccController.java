package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity.DayWaterSituationStatisticsTableZcc;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.service.DayWaterSituationStatisticsTableZccService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 制材厂日水情统计表(DayWaterSituationStatisticsTableZcc)表控制层
 *
 * @author makejava
 * @since 2023-12-23 16:01:30
 */
@Api(tags = "制材厂日水情统计表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("dayWaterSituationStatisticsTableZcc")
public class DayWaterSituationStatisticsTableZccController{

    @Autowired
    private DayWaterSituationStatisticsTableZccService dayWaterSituationStatisticsTableZccService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody List<DayWaterSituationStatisticsTableZcc> list) {
        return dayWaterSituationStatisticsTableZccService.add(list);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("查询列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "date")String date) {
        return dayWaterSituationStatisticsTableZccService.selectList(date);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        return dayWaterSituationStatisticsTableZccService.delete(ids);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<DayWaterSituationStatisticsTableZcc> list) {
        return dayWaterSituationStatisticsTableZccService.update(list);
    }
}

