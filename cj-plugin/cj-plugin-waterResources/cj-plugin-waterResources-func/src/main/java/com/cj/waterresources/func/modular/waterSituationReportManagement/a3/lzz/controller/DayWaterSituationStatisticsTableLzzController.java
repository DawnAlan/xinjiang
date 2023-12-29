package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.controller;


import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * 楼庄子水库日水情统计表(DayWaterSituationStatisticsTableLzz)表控制层
 *
 * @author makejava
 * @since 2023-12-23 15:59:32
 */
@Api(tags = "楼庄子水库日水情统计表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("dayWaterSituationStatisticsTableLzz")
public class DayWaterSituationStatisticsTableLzzController {

    @Autowired
    private DayWaterSituationStatisticsTableLzzService dayWaterSituationStatisticsTableLzzService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody List<DayWaterSituationStatisticsTableLzz> list) {
        return dayWaterSituationStatisticsTableLzzService.add(list);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("查询列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "date")String date) {
        return dayWaterSituationStatisticsTableLzzService.selectList(date);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        return dayWaterSituationStatisticsTableLzzService.delete(ids);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<DayWaterSituationStatisticsTableLzz> list) {
        return dayWaterSituationStatisticsTableLzzService.update(list);
    }
}

