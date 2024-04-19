package com.cj.fourPredictions.func.modular.waterResource.index.service;

import com.cj.common.model.RestResponse;

public interface IndexService {

    RestResponse getTodayWaterDiversionInstruction(String time);

    RestResponse getRealTimeWaterSituationOfTheReservoir(String reservoir,String time);

    RestResponse getRealTimeWaterLevel(String station,String time);

    RestResponse getWaterSupplyStatistics(String time);

    RestResponse getWaterFeeStatistics(String time);

    RestResponse getTodayInspectionStatistics(String time);
}
