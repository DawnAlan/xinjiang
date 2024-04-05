package com.cj.fourPredictions.func.modular.waterResource.planOptimization.service;

import com.cj.common.model.RestResponse;

import java.util.List;

public interface PlanOptimizationService {

    RestResponse getWaterResourceAllocationList(Integer bucketType,String inflowDataName);

    RestResponse contrast(String idA,String idB);
    RestResponse contrastNew(List<String> ids);
}
