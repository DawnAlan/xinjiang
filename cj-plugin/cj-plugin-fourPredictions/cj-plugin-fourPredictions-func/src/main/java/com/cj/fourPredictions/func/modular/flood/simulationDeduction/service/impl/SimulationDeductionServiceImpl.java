package com.cj.fourPredictions.func.modular.flood.simulationDeduction.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.flood.api.PredictionApi;
import com.cj.fourPredictions.func.modular.flood.simulationDeduction.service.SimulationDeductionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SimulationDeductionServiceImpl implements SimulationDeductionService {

    @Resource
    private PredictionApi predictionApi;

    @Override
    public RestResponse getPlansComparison(String ids) {
        String data = predictionApi.getPlansComparison(ids);
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseObject(data));
        }else {
            return RestResponse.no("blank");
        }
    }
}
