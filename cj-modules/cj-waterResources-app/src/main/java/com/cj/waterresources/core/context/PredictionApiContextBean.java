package com.cj.waterresources.core.context;

import com.cj.flood.api.PredictionApi;
import com.cj.floof.feign.PredictionFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PredictionApiContextBean implements PredictionApi {

    private final PredictionFeign predictionFeign;
    @Override
    public String getProgrammeListByTime(String startTime, String endTime) {
        String programmeListByTime = predictionFeign.getProgrammeListByTime(startTime, endTime);
        return programmeListByTime;
    }

    @Override
    public String getProgrammeList() {
        String programmeListByTime = predictionFeign.getProgrammeList();
        return programmeListByTime;
    }

    @Override
    public String getProgrammeDetails(String id) {
        String data = predictionFeign.getProgrammeDetails(id);
        return data;
    }

    @Override
    public String getRealTimeRainfall(String startTime, String endTime,Integer lzz,Integer tth,List<String> lzzIdList,List<String> tthIdList) {
        String realTimeRainfall = predictionFeign.getRealTimeRainfall(startTime, endTime,lzz,tth,lzzIdList,tthIdList);
        return realTimeRainfall;
    }

    @Override
    public String getRealTimeRainfallByDate(String date, Integer lzz, Integer tth, List<String> lzzIdList, List<String> tthIdList) {
        String realTimeRainfall = predictionFeign.getRealTimeRainfallByDate(date,lzz,tth,lzzIdList,tthIdList);
        return realTimeRainfall;
    }

    /*@Override
    public String getRealTimeWaterLevelData(String date) {
        String realTimeWaterLevelData = predictionFeign.getRealTimeWaterLevelData(date);
        return realTimeWaterLevelData;
    }*/

    @Override
    public String getRealTimeReservoirLevelData(String date) {
        String realTimeReservoirLevelData = predictionFeign.getRealTimeReservoirLevelData(date);
        return realTimeReservoirLevelData;
    }

    @Override
    public String getRainfallStationsHistoricalData(String name, String startTime, String endTime) {
        String data = predictionFeign.getRainfallStationsHistoricalData(name,startTime,endTime);
        return data;
    }

    @Override
    public String getReservoirLevel(String name, String startTime, String endTime) {
        String data = predictionFeign.getReservoirLevel(name,startTime,endTime);
        return data;
    }

    @Override
    public String getWaterLevelData(String name, String startTime, String endTime) {
        String data = predictionFeign.getWaterLevelData(name,startTime,endTime);
        return data;
    }

    @Override
    public String getFloodControlOperationListById(String id) {
        String data = predictionFeign.getFloodControlOperationListById(id);
        return data;
    }

    @Override
    public String getFloodControlOperationFrontViewById(String id) {
        String data = predictionFeign.getFloodControlOperationFrontViewById(id);
        return data;
    }

    @Override
    public String getFloodControlOperationDetails(String id) {
        String data = predictionFeign.getFloodControlOperationDetails(id);
        return data;
    }

    @Override
    public String getPlansComparison(String ids) {
        String data = predictionFeign.getPlansComparison(ids);
        return data;
    }

    @Override
    public String getProgrammeListForFloodControlOperation() {
        String data = predictionFeign.getProgrammeListForFloodControlOperation();
        return data;
    }

    @Override
    public String getPredictionListByTimeType(Integer timeType) {
        String data = predictionFeign.getProgrammeListForFloodControlOperation();
        return data;
    }

    @Override
    public String getPredictionListByName(String id, String reservoir) {
        String data = predictionFeign.getPredictionListByName(id,reservoir);
        return data;
    }

    @Override
    public String autoGenerate(String time) {
        String data = predictionFeign.autoGenerate(time);
        return data;
    }

    @Override
    public String selectModelAddressById(String id) {
        String data = predictionFeign.selectModelAddressById(id);
        return data;
    }

    @Override
    public String getWaterStorageOverview(String dateTime) {
         return predictionFeign.getWaterStorageOverview(dateTime);
    }

    @Override
    public void refreshWaterStorageOverview() {
        predictionFeign.refreshWaterStorageOverview();
    }
}
