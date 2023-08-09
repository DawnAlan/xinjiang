package com.cj.biz.core.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cj.biz.core.interceptor.FeignRequestInterceptor;

/**
 * Feign配置注册（全局）
 *
 * @author dongxiayu
 * @date : 2022/11/29 15:04
 **/
@Configuration
public class FeignSupportConfigure {

    /**
     * feign请求拦截器
     *
     * @return
     */
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new FeignRequestInterceptor();
    }

}
