package com.cj.floof.feign;

import com.cj.common.consts.FeignConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@FeignClient(name = FeignConstant.FLOOD_APP,contextId="predictionFeign")
public interface PredictionFeign {

    @RequestMapping("/feign/provider/prediction/getProgrammeListByTime")
    String getProgrammeListByTime(@RequestParam(value = "startTime", required =true) String startTime,
                                  @RequestParam(value = "endTime", required =true) String endTime);

    @RequestMapping("/feign/provider/prediction/getProgrammeList")
    String getProgrammeList();

    @RequestMapping("/feign/provider/prediction/getProgrammeDetails")
    String getProgrammeDetails(@RequestParam(value = "id", required =true)String id);

    @RequestMapping("/feign/provider/prediction/getRealTimeRainfall")
    String getRealTimeRainfall(@RequestParam(value = "startTime", required =true) String startTime,
                                  @RequestParam(value = "endTime", required =true) String endTime,
                               @RequestParam(value = "lzz", required =true)Integer lzz,
                               @RequestParam(value = "tth", required =true)Integer tth,
                               @RequestParam(value = "lzzIdList", required =true)List<String> lzzIdList,
                               @RequestParam(value = "tthIdList", required =true)List<String> tthIdList);

  /*  @RequestMapping("/feign/provider/prediction/getRealTimeWaterLevelData")
    String getRealTimeWaterLevelData(@RequestParam(value = "date", required =true)String date);*/

    @RequestMapping("/feign/provider/prediction/getRealTimeReservoirLevelData")
    String getRealTimeReservoirLevelData(@RequestParam(value = "date", required =true)String date);

    @RequestMapping("/feign/provider/prediction/getRainfallStationsHistoricalData")
    String getRainfallStationsHistoricalData(@RequestParam(value = "name", required =true)String name,
                                             @RequestParam(value = "startTime", required =true)String startTime,
                                             @RequestParam(value = "endTime", required =true)String endTime);

    @RequestMapping("/feign/provider/prediction/getReservoirLevel")
    String getReservoirLevel(@RequestParam(value = "name", required =true)String name,
                             @RequestParam(value = "startTime", required =true)String startTime,
                             @RequestParam(value = "endTime", required =true)String endTime);

    @RequestMapping("/feign/provider/prediction/getWaterLevelData")
    String getWaterLevelData(@RequestParam(value = "name", required =true)String name,
                             @RequestParam(value = "startTime", required =true)String startTime,
                             @RequestParam(value = "endTime", required =true)String endTime);

    @RequestMapping("/feign/provider/prediction/getFloodControlOperationListById")
    String getFloodControlOperationListById(@RequestParam(value = "id", required =true)String id);

    @RequestMapping("/feign/provider/prediction/getFloodControlOperationFrontViewById")
    String getFloodControlOperationFrontViewById(@RequestParam(value = "id", required =true)String id);

    @RequestMapping("/feign/provider/prediction/getFloodControlOperationDetails")
    String getFloodControlOperationDetails(@RequestParam(value = "id", required =true)String id);

    @RequestMapping("/feign/provider/prediction/getPlansComparison")
    String getPlansComparison(@RequestParam(value = "ids", required =true)String ids);

    @RequestMapping("/feign/provider/prediction/getProgrammeListForFloodControlOperation")
    String getProgrammeListForFloodControlOperation();

    @RequestMapping("/feign/provider/prediction/getPredictionListByTimeType")
    String getPredictionListByTimeType(@RequestParam(value = "timeType", required =true)Integer timeType);

    @RequestMapping("/feign/provider/prediction/getPredictionListByName")
    String getPredictionListByName(@RequestParam(value = "id", required =true)String id,
                                   @RequestParam(value = "reservoir", required =true)String reservoir);

    @RequestMapping("/feign/provider/flood/getWaterStorageOverview")
    String getWaterStorageOverview(@RequestParam(value = "dateTime", required =true)String dateTime);

    @RequestMapping("/feign/provider/flood/refreshWaterStorageOverview")
    void refreshWaterStorageOverview();


    @RequestMapping("/feign/provider/flood/getRealTimeRainfallByDate")
    String getRealTimeRainfallByDate(@RequestParam(value = "date", required =true)String date,
                                     @RequestParam(value = "lzz", required =true)Integer lzz,
                                     @RequestParam(value = "tth", required =true)Integer tth,
                                     @RequestParam(value = "lzzIdList", required =true)List<String> lzzIdList,
                                     @RequestParam(value = "tthIdList", required =true)List<String> tthIdList);
    @RequestMapping("/feign/provider/flood/autoGenerate")
    String autoGenerate(@RequestParam(value = "time", required =true)String time);

    @RequestMapping("/feign/provider/flood/selectModelAddressById")
    String selectModelAddressById(@RequestParam(value = "id", required =true)String id);
}
