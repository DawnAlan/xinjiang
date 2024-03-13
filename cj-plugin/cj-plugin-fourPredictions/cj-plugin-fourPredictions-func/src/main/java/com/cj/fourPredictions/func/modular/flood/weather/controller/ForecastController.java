package com.cj.fourPredictions.func.modular.flood.weather.controller;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.flood.weather.bean.vo.ForecastVO;
import com.cj.fourPredictions.func.modular.flood.weather.service.ForecastService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "天气预报")
@Slf4j
@RestController
@RequestMapping("/weather")
public class ForecastController {
    @Resource
    private ForecastService forecastService;

    @ApiOperation(value = "获取天气预报二十四小时", notes = "获取天气预报二十四小时")
    @GetMapping(value = "/getForecast")
    public RestResponse<List<ForecastVO>> getForecast() {
        try {
            List<ForecastVO> value = forecastService.getForecast();
            return RestResponse.ok(value);
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }
}
