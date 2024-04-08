package com.cj.fourPredictions.func.modular.waterResource.preparationRehearsal.service;

import com.cj.common.model.RestResponse;

public interface PreparationRehearsalService {

    RestResponse getWaterResourceAllocationList(Integer waterDistributionType,String inflowDataName);

    RestResponse getWaterResourceAllocationDetails(String id);
}
