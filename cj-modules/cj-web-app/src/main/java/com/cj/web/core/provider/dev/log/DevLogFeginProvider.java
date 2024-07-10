package com.cj.web.core.provider.dev.log;

import com.cj.common.pojo.CommonDevLog;
import com.cj.dev.feign.DevLogFegin;
import com.cj.dev.modular.log.entity.DevLog;
import com.cj.dev.modular.log.provider.DevLogApiProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DevLogFeginProvider  implements DevLogFegin {

    private final DevLogApiProvider devLogApiProvider;

    @Override
    @RequestMapping("/feign/dev/log/saveLog")
    public void saveLog(@RequestBody CommonDevLog commonDevLog) {
        DevLog devLog = new DevLog();
        BeanUtils.copyProperties(commonDevLog, devLog);
        devLogApiProvider.saveLog(devLog);
    }
}
