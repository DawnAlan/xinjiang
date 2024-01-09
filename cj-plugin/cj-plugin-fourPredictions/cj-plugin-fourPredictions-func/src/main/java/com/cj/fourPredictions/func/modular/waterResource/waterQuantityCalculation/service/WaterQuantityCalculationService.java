package com.cj.fourPredictions.func.modular.waterResource.waterQuantityCalculation.service;

import com.cj.common.model.RestResponse;

public interface WaterQuantityCalculationService {

    RestResponse waterQuantityCalculation(String id);

    RestResponse getRealTimeReservoirLevel(String reservoir);
}
