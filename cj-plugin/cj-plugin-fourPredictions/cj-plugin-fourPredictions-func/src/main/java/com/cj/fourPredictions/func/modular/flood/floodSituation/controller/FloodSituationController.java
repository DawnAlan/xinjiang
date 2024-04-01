package com.cj.fourPredictions.func.modular.flood.floodSituation.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.flood.floodSituation.bean.req.GetRealTimeRainfallReq;
import com.cj.fourPredictions.func.modular.flood.floodSituation.bean.res.*;
import com.cj.fourPredictions.func.modular.flood.floodSituation.service.FloodSituationService;
import com.cj.fourPredictions.func.modular.flood.floodSituation.bean.req.GetRealTimeWaterLevelDataReq;
import com.cj.fourPredictions.func.modular.flood.floodSituation.bean.req.SelectHistoryReq;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "防洪兴利-防洪形式")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterResourceAllocation")
public class FloodSituationController {

    @Autowired
    private FloodSituationService floodSituationService;

    @ApiOperation(value="防洪兴利-防洪形式实时雨情", notes="实时雨情")
    @CommonLog(value = "防洪兴利-防洪形式实时雨情")
    @PostMapping(value = "/getRealTimeRainfall")
    public RestResponse<List<RealTimeRainfallRes>> getRealTimeRainfall(@RequestBody GetRealTimeRainfallReq req){
        try {
            return floodSituationService.getRealTimeRainfall(req.getDate(),req.getHour());
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value="防洪兴利-防洪形式实时水情", notes="实时水情")
    @CommonLog(value = "防洪兴利-防洪形式实时水情")
    @PostMapping(value = "/getRealTimeWaterLevelData")
    public RestResponse<List<RealTimeEngineeringSituationDataRes>>  getRealTimeWaterLevelData(@RequestBody GetRealTimeWaterLevelDataReq req){
        try {
            return floodSituationService.getRealTimeWaterLevelData(req.getDate());
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value="防洪兴利-防洪形式实时工情", notes="实时工情")
    @CommonLog(value = "防洪兴利-防洪形式实时工情")
    @PostMapping(value = "/getRealTimeReservoirLevelData")
    public RestResponse<List<RealTimeWaterLevelDataRes>>  getRealTimeReservoirLevelData(@RequestBody GetRealTimeWaterLevelDataReq req){
        try {
            return floodSituationService.getRealTimeReservoirLevelData(req.getDate());
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value="防洪兴利-防洪形式雨量历史数据", notes="雨量历史数据")
    @CommonLog(value = "防洪兴利-防洪形式雨量历史数据")
    @PostMapping(value = "/getRainfallStationsHistoricalData")
    public RestResponse<List<RainfallStationsHistoricalDataRes>> getRainfallStationsHistoricalData(@RequestBody SelectHistoryReq req){
        return floodSituationService.getRainfallStationsHistoricalData(req);
    }

    @ApiOperation(value="防洪兴利-防洪形式水库水位历史数据", notes="水库水位历史数据")
    @CommonLog(value = "防洪兴利-防洪形式水库水位历史数据")
    @PostMapping(value = "/getReservoirLevel")
    public RestResponse<List<ReservoirLevelRes>> getReservoirLevel(@RequestBody SelectHistoryReq req){
        return floodSituationService.getReservoirLevel(req);
    }

    @ApiOperation(value="防洪兴利-防洪形式水位站历史数据", notes="水位站历史数据")
    @CommonLog(value = "防洪兴利-防洪形式水位站历史数据")
    @PostMapping(value = "/getWaterLevelData")
    public RestResponse<List<WaterLevelDataRes>> getWaterLevelData(@RequestBody SelectHistoryReq req){
        return floodSituationService.getWaterLevelData(req);
    }
}
