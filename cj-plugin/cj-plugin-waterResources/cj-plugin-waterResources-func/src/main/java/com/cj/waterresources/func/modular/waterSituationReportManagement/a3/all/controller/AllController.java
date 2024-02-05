package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.A3StatisticsReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.AllService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "A3表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("all")
public class AllController {

    @Autowired
    private AllService allService;

    @ApiOperationSupport(order = 2)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "date")String date) {
        return allService.deleteAll(date);
    }

    @ApiOperationSupport(order = 1)
    @ApiOperation("统计")
    @PostMapping("/statistics")
    public RestResponse statistics(@RequestBody A3StatisticsReq req) {
        return allService.statistics(req);
    }
}
