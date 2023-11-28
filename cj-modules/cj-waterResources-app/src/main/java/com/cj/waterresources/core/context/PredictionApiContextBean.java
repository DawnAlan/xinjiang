package com.cj.waterresources.core.context;

import com.cj.flood.api.PredictionApi;
import com.cj.floof.feign.PredictionFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PredictionApiContextBean implements PredictionApi {

    private final PredictionFeign predictionFeign;
    @Override
    public String getProgrammeListByTime(String startTime, String endTime) {
        String programmeListByTime = predictionFeign.getProgrammeListByTime(startTime, endTime);
        return programmeListByTime;
    }
}
