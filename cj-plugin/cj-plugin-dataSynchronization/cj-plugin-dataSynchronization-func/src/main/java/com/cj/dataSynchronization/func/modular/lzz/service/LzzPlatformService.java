package com.cj.dataSynchronization.func.modular.lzz.service;

import com.cj.common.model.RestResponse;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;

import java.util.Date;
import java.util.List;

public interface LzzPlatformService {

    RestResponse insertRainfallStationInfo(Date time);

    RestResponse insertReservoirLevel(Date time);

    RestResponse insertGaugingStation(Date time);

    RestResponse insertRainfallStationRainfallBetweenTime(Date startTime, Date endTime);
    RestResponse insertRainfallStationTemperatureBetweenTime(Date startTime, Date endTime);
    RestResponse insertRainfallStationInfoBetweenTime(Date startTime, Date endTime);

    RestResponse insertReservoirLevelWaterLevelBetweenTime(Date startTime, Date endTime);
    RestResponse<List<LzzGaugingStation>> getReservoirLevelWaterLevelBetweenTime(Date startTime, Date endTime);
    RestResponse insertReservoirLevelTemperatureBetweenTime(Date startTime, Date endTime);
    RestResponse insertReservoirLevelBetweenTime(Date startTime, Date endTime);

    RestResponse insertGaugingStationWaterLevelBetweenTime(Date startTime, Date endTime);
    RestResponse insertGaugingStationFlowBetweenTime(Date startTime, Date endTime);
    RestResponse insertGaugingStationTemperatureBetweenTime(Date startTime, Date endTime);
    RestResponse insertGaugingStationBetweenTime(Date startTime, Date endTime);

    RestResponse insertTree();
}
