package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.service.DayWaterSituationStatisticsTableDklService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 对口率日水情统计表(DayWaterSituationStatisticsTableDkl)表控制层
 *
 * @author makejava
 * @since 2023-12-23 15:58:22
 */
@Api(tags = "对口率日水情统计表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("dayWaterSituationStatisticsTableDkl")
public class DayWaterSituationStatisticsTableDklController{

    @Autowired
    private DayWaterSituationStatisticsTableDklService dayWaterSituationStatisticsTableDklService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody List<DayWaterSituationStatisticsTableDkl> list) {
        return dayWaterSituationStatisticsTableDklService.add(list);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("查询列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "date")String date) {
        return dayWaterSituationStatisticsTableDklService.selectList(date);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        return dayWaterSituationStatisticsTableDklService.delete(ids);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<DayWaterSituationStatisticsTableDkl> list) {
        return dayWaterSituationStatisticsTableDklService.update(list);
    }
}

