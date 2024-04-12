package com.cj.fourPredictions.func.modular.flood.floodSituation.service;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.flood.floodSituation.bean.res.*;
import com.cj.fourPredictions.func.modular.flood.floodSituation.bean.req.SelectHistoryReq;

import java.util.List;

public interface FloodSituationService {

    RestResponse<List<RealTimeRainfallRes>> getRealTimeRainfall(String date , Integer hour);

    /*RestResponse<List<RealTimeEngineeringSituationDataRes>>  getRealTimeWaterLevelData(String date);*/

    RestResponse<List<RealTimeWaterLevelDataRes>> getRealTimeReservoirLevelData(String date);

    RestResponse<List<RainfallStationsHistoricalDataRes>> getRainfallStationsHistoricalData(SelectHistoryReq req);

    RestResponse<List<ReservoirLevelRes>> getReservoirLevel(SelectHistoryReq req);

    RestResponse<List<WaterLevelDataRes>> getWaterLevelData(SelectHistoryReq req);
}
