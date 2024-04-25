package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.bean.req.selectListFlowReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 渠首管理站日水情统计表(DayWaterSituationStatisticsTableQs)表控制层
 *
 * @author makejava
 * @since 2023-12-23 15:59:54
 */
@Api(tags = "渠首管理站日水情统计表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("dayWaterSituationStatisticsTableQs")
public class DayWaterSituationStatisticsTableQsController {

    @Autowired
    private DayWaterSituationStatisticsTableQsService dayWaterSituationStatisticsTableQsService;



    @ApiOperationSupport(order = 1)
    @ApiOperation("渠首管理站日水情统计表新增")
    @CommonLog("渠首管理站日水情统计表新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQsList) {
        return dayWaterSituationStatisticsTableQsService.add(dayWaterSituationStatisticsTableQsList);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("渠首管理站日水情统计表查询列表")
    @CommonLog("渠首管理站日水情统计表查询列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "date")String date) {
        return dayWaterSituationStatisticsTableQsService.selectList(date);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("渠首管理站日水情统计表删除")
    @CommonLog("渠首管理站日水情统计表删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        return dayWaterSituationStatisticsTableQsService.delete(ids);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("渠首管理站日水情统计表修改")
    @CommonLog("渠首管理站日水情统计表修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<DayWaterSituationStatisticsTableQs> list) {
        return dayWaterSituationStatisticsTableQsService.update(list);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("渠首管理站查询流量列表")
    @CommonLog("渠首管理站查询流量列表")
    @PostMapping("/selectListFlow")
    public RestResponse selectListFlow(@RequestBody selectListFlowReq req) {
        return dayWaterSituationStatisticsTableQsService.selectListFlow(req);
    }
}

