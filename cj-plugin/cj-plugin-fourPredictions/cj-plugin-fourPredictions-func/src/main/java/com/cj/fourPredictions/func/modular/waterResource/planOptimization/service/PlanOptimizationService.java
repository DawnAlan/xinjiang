package com.cj.fourPredictions.func.modular.waterResource.planOptimization.service;

import com.cj.common.model.RestResponse;

public interface PlanOptimizationService {

    RestResponse getWaterResourceAllocationList(Integer waterDistributionType);

    RestResponse contrast(String idA,String idB);
}
