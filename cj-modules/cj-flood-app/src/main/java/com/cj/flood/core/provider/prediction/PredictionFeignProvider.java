package com.cj.flood.core.provider.prediction;

import com.cj.flood.func.modular.prediction.provider.PredictionApiProvider;
import com.cj.floof.feign.PredictionFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PredictionFeignProvider implements PredictionFeign {

    private final PredictionApiProvider predictionApiProvider;

    @Override
    @RequestMapping("/feign/provider/prediction/getProgrammeListByTime")
    public String getProgrammeListByTime(String startTime, String endTime) {
        return predictionApiProvider.getProgrammeListByTime(startTime, endTime);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getProgrammeList")
    public String getProgrammeList() {
        return predictionApiProvider.getProgrammeList();
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getProgrammeDetails")
    public String getProgrammeDetails(String id) {
        return predictionApiProvider.getProgrammeDetails(id);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getRealTimeRainfall")
    public String getRealTimeRainfall(String startTime, String endTime) {
        return predictionApiProvider.getRealTimeRainfall(startTime, endTime);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getRealTimeWaterLevelData")
    public String getRealTimeWaterLevelData(String date) {
        return predictionApiProvider.getRealTimeWaterLevelData(date);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getRealTimeReservoirLevelData")
    public String getRealTimeReservoirLevelData(String date) {
        return predictionApiProvider.getRealTimeReservoirLevelData(date);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getRainfallStationsHistoricalData")
    public String getRainfallStationsHistoricalData(String name, String startTime, String endTime) {
        return predictionApiProvider.getRainfallStationsHistoricalData(name, startTime, endTime);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getReservoirLevel")
    public String getReservoirLevel(String name, String startTime, String endTime) {
        return predictionApiProvider.getReservoirLevel(name, startTime, endTime);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getWaterLevelData")
    public String getWaterLevelData(String name, String startTime, String endTime) {
        return predictionApiProvider.getWaterLevelData(name, startTime, endTime);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getFloodControlOperationListById")
    public String getFloodControlOperationListById(String id) {
        return predictionApiProvider.getFloodControlOperationListById(id);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getFloodControlOperationFrontViewById")
    public String getFloodControlOperationFrontViewById(String id) {
        return predictionApiProvider.getFloodControlOperationFrontViewById(id);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getFloodControlOperationDetails")
    public String getFloodControlOperationDetails(String id) {
        return predictionApiProvider.getFloodControlOperationDetails(id);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getPlansComparison")
    public String getPlansComparison(String ids) {
        return predictionApiProvider.getPlansComparison(ids);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getProgrammeListForFloodControlOperation")
    public String getProgrammeListForFloodControlOperation() {
        return predictionApiProvider.getProgrammeListForFloodControlOperation();
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getPredictionListByTimeType")
    public String getPredictionListByTimeType(Integer timeType) {
        return predictionApiProvider.getPredictionListByTimeType(timeType);
    }

    @Override
    @RequestMapping("/feign/provider/prediction/getPredictionListByName")
    public String getPredictionListByName(String id, String reservoir) {
        return predictionApiProvider.getPredictionListByName(id, reservoir);
    }

    @RequestMapping("/feign/provider/flood/getWaterStorageOverview")
    @Override
    public String getWaterStorageOverview(String dateTime) {
        return predictionApiProvider.getWaterStorageOverview(dateTime);
    }

    @RequestMapping("/feign/provider/flood/refreshWaterStorageOverview")
    @Override
    public void refreshWaterStorageOverview() {
        predictionApiProvider.refreshWaterStorageOverview();
    }

    @RequestMapping("/feign/provider/flood/getRealTimeRainfallByDate")
    @Override
    public String getRealTimeRainfallByDate(String date) {
        return predictionApiProvider.getRealTimeRainfallByDate(date);
    }
}
