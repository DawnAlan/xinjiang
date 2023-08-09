package com.cj.biz.core.context.dev;

import com.cj.dev.api.DevConfigApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.cj.dev.feign.DevConfigFeign;

/**
 * 配置APi上下文Bean
 *
 * @author dongxiayu
 * @date 2022/11/22 14:12
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DevConfigApiContenxtBean implements DevConfigApi {

    private final DevConfigFeign devConfigFeign;

    /**
     * 根据键获取值
     *
     * @param key
     * @author dongxiayu
     * @date 2022/6/17 11:11
     */
    @Override
    public String getValueByKey(String key) {
        return devConfigFeign.getValueByKey(key);
    }
}