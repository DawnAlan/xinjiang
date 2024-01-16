package com.cj.fourPredictions.func.modular.waterResource.index.service;

import com.cj.common.model.RestResponse;

public interface IndexService {

    RestResponse getTodayWaterDiversionInstruction();

    RestResponse getRealTimeWaterSituationOfTheReservoir(String reservoir);

    RestResponse getRealTimeWaterLevel(String station);

    RestResponse getWaterSupplyStatistics(String station);

    RestResponse getWaterFeeStatistics();

    RestResponse getTodayInspectionStatistics();
}
