package com.cj.flood.func.core.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "cj-water-condition-web", contextId = "waterSituationClient")
public interface WaterSituationClient {
    @GetMapping(value = "toutunhe/basic/queryRRs")
    String queryRRs(@RequestParam("id") String id);

    @GetMapping("toutunhe/wpdCurved/dropDown")
    String dropDown(@RequestParam("ndcdId") String ndcdId);

    @GetMapping("toutunhe/wpdCurved/queryQuXT")
    String queryQuXT(@RequestParam("id") String id);
}
