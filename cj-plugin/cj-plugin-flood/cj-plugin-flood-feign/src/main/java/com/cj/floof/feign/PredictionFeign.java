package com.cj.floof.feign;

import com.cj.common.consts.FeignConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = FeignConstant.FLOOD_APP,contextId="predictionFeign")
public interface PredictionFeign {

    @RequestMapping("/feign/provider/prediction/getProgrammeListByTime")
    String getProgrammeListByTime(@RequestParam(value = "startTime", required =true) String startTime,
                                  @RequestParam(value = "endTime", required =true) String endTime);
}
