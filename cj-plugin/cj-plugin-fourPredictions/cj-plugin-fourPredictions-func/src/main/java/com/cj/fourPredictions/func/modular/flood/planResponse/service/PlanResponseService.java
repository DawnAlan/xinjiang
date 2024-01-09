package com.cj.fourPredictions.func.modular.flood.planResponse.service;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.res.IncomingWaterForecast;
import com.cj.fourPredictions.func.modular.flood.planResponse.bean.res.FloodControlOperation;

import java.util.List;

public interface PlanResponseService {

    RestResponse<List<FloodControlOperation>> getFloodControlOperationListById(String id);
    RestResponse<List<IncomingWaterForecast>> getProgrammeListForFloodControlOperation();

    RestResponse getFloodControlOperationFrontViewById(String id);

    RestResponse getFloodControlOperationDetails(String id);
}
