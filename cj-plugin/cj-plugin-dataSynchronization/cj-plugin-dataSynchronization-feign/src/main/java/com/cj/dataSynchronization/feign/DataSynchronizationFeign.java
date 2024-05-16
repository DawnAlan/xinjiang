package com.cj.dataSynchronization.feign;

import com.cj.common.consts.FeignConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = FeignConstant.DATA_SYNCHRONIZATION_APP,contextId="dataSynchronizationFeign")
public interface DataSynchronizationFeign {

    @RequestMapping("/feign/provider/dataSynchronization/updateMonitor")
    String updateMonitor(@RequestParam(value = "treeType", required =true)Integer treeType);
}
