package com.cj.waterresources.func.modular.homePage.controller;


import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.homePage.bean.res.OverviewRes;
import com.cj.waterresources.func.modular.homePage.bean.res.WaterSituationRes;
import com.cj.waterresources.func.modular.homePage.bean.res.WaterSituationStationsRes;
import com.cj.waterresources.func.modular.homePage.bean.res.WaterStorageOverviewRes;
import com.cj.waterresources.func.modular.homePage.service.WaterResourceHomePageService;
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
@RequestMapping("waterResource/homaPage")
@Api(tags = "水资源首页")
@Validated
@RequiredArgsConstructor
public class WaterResourceHomePageController {
    private final WaterResourceHomePageService waterResourceHomePageService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("今日概览")
    @GetMapping("/overview")
    public RestResponse<OverviewRes> overview(@RequestParam("dateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime) {
        return waterResourceHomePageService.overview(dateTime);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("获取水情站分类")
    @PostMapping("/getWaterSituationStations")
    public RestResponse<List<WaterSituationStationsRes>> getWaterSituationStations() {
        return waterResourceHomePageService.getWaterSituationStations();
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("今日水情")
    @PostMapping("/waterSituation")
    public RestResponse<List<WaterSituationRes>> waterSituation(@RequestParam("dateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime,
                                                                @RequestParam("unitId") String unitId) {
        return waterResourceHomePageService.waterSituation(dateTime, unitId);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("水库概览")
    @GetMapping("/waterStorageOverview")
    public RestResponse<List<WaterStorageOverviewRes>> waterStorageOverview(@RequestParam("dateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime) {
        return waterResourceHomePageService.waterStorageOverview(dateTime);
    }

}

