package com.cj.flood.core.provider.prediction;

import com.cj.flood.func.modular.prediction.provider.PredictionApiProvider;
import com.cj.floof.feign.PredictionFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PredictionFeignProvider implements PredictionFeign {

    private final PredictionApiProvider predictionApiProvider;

    @Override
    @RequestMapping("/feign/provider/prediction/getProgrammeListByTime")
    public String getProgrammeListByTime(String startTime, String endTime) {
        return predictionApiProvider.getProgrammeListByTime(startTime, endTime);
    }
}
