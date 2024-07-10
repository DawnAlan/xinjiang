package com.cj.dev.feign;

import com.cj.common.consts.FeignConstant;
import com.cj.common.pojo.CommonDevLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name= FeignConstant.WEB_APP, contextId = "DevLogFegin")
public interface DevLogFegin {


    @PostMapping("/feign/dev/log/saveLog")
    void saveLog(@RequestBody CommonDevLog commonDevLog);
}
