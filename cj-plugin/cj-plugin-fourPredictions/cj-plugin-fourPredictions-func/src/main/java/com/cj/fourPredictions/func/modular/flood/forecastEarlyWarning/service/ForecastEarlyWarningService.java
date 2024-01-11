package com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.service;

import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.res.IncomingWaterForecast;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.res.IncomingWaterForecastDetailsRes;

import java.util.List;

public interface ForecastEarlyWarningService {

    RestResponse<List<IncomingWaterForecast>> getProgrammeList();

    RestResponse getDetails(String id);
}
