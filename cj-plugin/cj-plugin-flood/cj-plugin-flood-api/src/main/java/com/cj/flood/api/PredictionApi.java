package com.cj.flood.api;


public interface PredictionApi {

    //查询来水预报列表
    String getProgrammeListByTime(String startTime, String endTime);
    String getProgrammeList();

    String getProgrammeDetails(String id);

    //查询实时雨量数据
    String getRealTimeRainfall(String startTime, String endTime);

    //查询实时工情
    String getRealTimeWaterLevelData(String date);

    //查询实时水情
    String getRealTimeReservoirLevelData(String date);

    //查询雨量历史数据
    String getRainfallStationsHistoricalData(String name, String startTime, String endTime);

    //查询水库水位历史数据
    String getReservoirLevel(String name, String startTime, String endTime);

    //查询水位站历史数据
    String getWaterLevelData(String name, String startTime, String endTime);

    String getFloodControlOperationListById(String id);

    String getFloodControlOperationFrontViewById(String id);

    String getFloodControlOperationDetails(String id);

    String getPlansComparison(String ids);

    String getProgrammeListForFloodControlOperation();

    //根据时段类型查询当年、当月、当旬、今日的来水预报列表
    String getPredictionListByTimeType(Integer timeType);
    String getPredictionListByName(String id,String reservoir);

    default String getWaterStorageOverview(String dateTime) {
        return null;
    }

}
