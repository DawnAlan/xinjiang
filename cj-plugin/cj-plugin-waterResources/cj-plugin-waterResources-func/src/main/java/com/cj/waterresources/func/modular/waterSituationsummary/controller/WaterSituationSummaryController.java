package com.cj.waterresources.func.modular.waterSituationsummary.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationsummary.service.WaterSituationSummaryService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * (WaterSituationSummary)表控制层
 *
 * @author makejava
 * @since 2024-03-19 17:25:29
 */
@RestController
@RequestMapping("waterSituationSummary")
public class WaterSituationSummaryController {

    @Autowired
    private WaterSituationSummaryService waterSituationSummaryService;

//    @ApiOperationSupport(order = 3)
//    @ApiOperation("头屯河水库日水情汇月")
//    @PostMapping("/selectListForIndustrialWaterFee")
//    public RestResponse selectListForIndustrialWaterFee(@RequestBody SelectListForIndustrialWaterFeeReq req) {
//        return waterSituationSummaryService.selectListForIndustrialWaterFee(req);
//    }
}

