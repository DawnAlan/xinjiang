package com.cj.fourPredictions.func.modular.waterResource.preparationRehearsal.service;

import com.cj.common.model.RestResponse;

public interface PreparationRehearsalService {

    RestResponse getWaterResourceAllocationList(Integer waterDistributionType);

    RestResponse getWaterResourceAllocationDetails(String id);
}
