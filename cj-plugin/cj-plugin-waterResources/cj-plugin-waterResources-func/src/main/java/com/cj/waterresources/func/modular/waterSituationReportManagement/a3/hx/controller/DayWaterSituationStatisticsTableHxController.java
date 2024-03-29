package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.controller;


import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.service.DayWaterSituationStatisticsTableHxService;
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
 * 河西管理站日水情统计表(DayWaterSituationStatisticsTableHx)表控制层
 *
 * @author makejava
 * @since 2023-12-23 15:59:11
 */
@Api(tags = "河西管理站日水情统计表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("dayWaterSituationStatisticsTableHx")
public class DayWaterSituationStatisticsTableHxController{

    @Autowired
    private DayWaterSituationStatisticsTableHxService dayWaterSituationStatisticsTableHxService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("河西管理站日水情统计表新增")
    @CommonLog("河西管理站日水情统计表新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody List<DayWaterSituationStatisticsTableHx> list) {
        return dayWaterSituationStatisticsTableHxService.add(list);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("河西管理站日水情统计表查询列表")
    @CommonLog("河西管理站日水情统计表查询列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "date")String date) {
        return dayWaterSituationStatisticsTableHxService.selectList(date);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("河西管理站日水情统计表删除")
    @CommonLog("河西管理站日水情统计表删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        return dayWaterSituationStatisticsTableHxService.delete(ids);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("河西管理站日水情统计表修改")
    @CommonLog("河西管理站日水情统计表修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<DayWaterSituationStatisticsTableHx> list) {
        return dayWaterSituationStatisticsTableHxService.update(list);
    }

}

