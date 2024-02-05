package com.cj.flood.func.modular.homePage.controller;


import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.homePage.bean.res.OverviewRes;
import com.cj.flood.func.modular.homePage.bean.res.WaterRainRes;
import com.cj.flood.func.modular.homePage.bean.res.WaterStorageOverviewRes;
import com.cj.flood.func.modular.homePage.service.FloodHomePageService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("flood/homaPage")
@Api(tags = "防洪首页")
@Validated
@RequiredArgsConstructor
public class FloodHomePageController {
    private final FloodHomePageService floodHomePageService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("今日概览")
    @GetMapping("/overview")
    public RestResponse<OverviewRes> overview(@RequestParam("dateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime) {
        return floodHomePageService.overview(dateTime);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("今日雨量")
    @PostMapping("/rainfall")
    public RestResponse<List<WaterRainRes>> rainfall(@RequestParam("dateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime) {
        return floodHomePageService.rainfall(dateTime);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("今日水情")
    @PostMapping("/waterSituation")
    public RestResponse<List<WaterRainRes>> waterSituation(@RequestParam("dateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime,
                                                           @RequestParam("unitId") String unitId) {
        return floodHomePageService.waterSituation(dateTime, unitId);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("水库概览")
    @GetMapping("/waterStorageOverview")
    public RestResponse<List<WaterStorageOverviewRes>> waterStorageOverview(@RequestParam("dateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime) {
        return floodHomePageService.waterStorageOverview(dateTime);
    }

}

