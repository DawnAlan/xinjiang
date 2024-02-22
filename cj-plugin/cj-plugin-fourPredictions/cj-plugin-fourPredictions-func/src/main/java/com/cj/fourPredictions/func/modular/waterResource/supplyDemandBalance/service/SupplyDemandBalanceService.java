package com.cj.fourPredictions.func.modular.waterResource.supplyDemandBalance.service;

import com.cj.common.model.RestResponse;

public interface SupplyDemandBalanceService {

    RestResponse getSupplyDemandBalance();

    RestResponse getFormList();

    RestResponse getSupplyDemandBalanceByFormId(String id);
}
