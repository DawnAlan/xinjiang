package com.cj.dataSynchronization.func.modular.lzz.service;

import com.cj.common.model.RestResponse;
import com.cj.dataSynchronization.func.modular.lzz.bean.ParamDto;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LzzPlatformService {

    RestResponse insertRainfallStationInfo(Date time);

    RestResponse insertReservoirLevel(Date time);

    RestResponse insertGaugingStation(Date time);

    RestResponse insertLzzInfo(Date time);

    RestResponse insertLzzBetweenTime(Date startTime, Date endTime);
    RestResponse insertLzzKqRailBetweenTime(Date startTime, Date endTime);
    RestResponse insertLzzKqRailBetweenTimeByMyself(Date startTime, Date endTime);

    RestResponse insertRainfallStationRainfallBetweenTime(Date startTime, Date endTime,String name);
    RestResponse insertRainfallStationTemperatureBetweenTime(Date startTime, Date endTime,String name);
    RestResponse insertRainfallStationInfoBetweenTime(Date startTime, Date endTime,String name);

    RestResponse insertReservoirLevelWaterLevelBetweenTime(Date startTime, Date endTime);
    RestResponse<List<LzzGaugingStation>> getReservoirLevelWaterLevelBetweenTime(Date startTime, Date endTime);

    RestResponse<LzzGaugingStation> getReservoirLevelWaterLevelByTime(Date time);
    RestResponse<Map<String, ParamDto>> getLzzInfoByTime(Date time);

    RestResponse<List<LzzRainfallStation>> selectRainfallStationDataByTime(Date time);

    RestResponse<LzzGaugingStation> getLevelWaterLevelByTime(Date time);
    RestResponse insertReservoirLevelTemperatureBetweenTime(Date startTime, Date endTime);
    RestResponse insertReservoirLevelBetweenTime(Date startTime, Date endTime);

    RestResponse insertGaugingStationWaterLevelBetweenTime(Date startTime, Date endTime);
    RestResponse insertGaugingStationFlowBetweenTime(Date startTime, Date endTime);
    RestResponse insertGaugingStationTemperatureBetweenTime(Date startTime, Date endTime);
    RestResponse insertGaugingStationBetweenTime(Date startTime, Date endTime);

    RestResponse insertLzzInputFlow();
    RestResponse insertLzzInputFlowBetweenTime(String startTime, String endTime);

    RestResponse insertTree();
    RestResponse updateTree();
}
