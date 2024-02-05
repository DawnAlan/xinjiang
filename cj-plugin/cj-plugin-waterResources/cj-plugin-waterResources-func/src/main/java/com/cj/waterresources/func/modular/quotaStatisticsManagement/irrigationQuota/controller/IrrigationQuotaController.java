package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.req.IrrigationQuotaListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.entity.IrrigationQuota;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.service.IrrigationQuotaService;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.bean.req.StatisticsReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.service.IrrigationQuotaDetailsService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 灌溉额度表(IrrigationQuota)表控制层
 *
 * @author makejava
 * @since 2023-12-22 12:49:37
 */
@Api(tags = "灌溉额度表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("irrigationQuota")
public class IrrigationQuotaController {

    @Autowired
    private IrrigationQuotaService irrigationQuotaService;

    @Autowired
    private IrrigationQuotaDetailsService irrigationQuotaDetailsService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody IrrigationQuota irrigationQuota) {
        return irrigationQuotaService.add(irrigationQuota);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("查询列表")
    @PostMapping("/selectList")
    public RestResponse selectList(@RequestBody IrrigationQuotaListReq req) {
        return irrigationQuotaService.selectList(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "id") String id) {
        return irrigationQuotaService.delete(id);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody IrrigationQuota irrigationQuota) {
        return irrigationQuotaService.update(irrigationQuota);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("统计")
    @PostMapping("/statistics")
    public RestResponse statistics(@RequestBody StatisticsReq req) {
        return irrigationQuotaDetailsService.statistics(req);
    }

}

