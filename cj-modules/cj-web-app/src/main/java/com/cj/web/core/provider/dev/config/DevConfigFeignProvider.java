package com.cj.web.core.provider.dev.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cj.dev.feign.DevConfigFeign;
import com.cj.dev.modular.config.provider.DevConfigApiProvider;

/**
 * 配置API Feign接口提供者
 *
 * @author dongxiayu
 * @date 2022/11/22 11:42
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class DevConfigFeignProvider implements DevConfigFeign {

    private final DevConfigApiProvider devConfigApiProvider;

    /**
     * 根据键获取值
     *
     * @param key
     * @author dongxiayu
     * @date 2022/11/12 11:11
     */
    @Override
    @PostMapping("/feign/dev/config/getValueByKey")
    public String getValueByKey(@RequestParam("key") String key) {
        return devConfigApiProvider.getValueByKey(key);
    }




}