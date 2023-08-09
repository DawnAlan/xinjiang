
package com.alibaba.csp.sentinel.dashboard;

import com.alibaba.csp.sentinel.init.InitExecutor;
import com.cj.common.runner.AppStartupListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @description 流量保护模块 熔断降级限流
 * @author dongxiayu
 * @date 2022/10/22 22:32
 * @return
 **/
@SpringBootApplication
public class CjCloudSentinelApp {

    public static void main(String[] args) {
        triggerSentinelInit();
        SpringApplication.run(CjCloudSentinelApp.class, args);
    }

    @Bean
    public AppStartupListener appStartupListener(){
        return new AppStartupListener();
    }

    private static void triggerSentinelInit() {
        new Thread(() -> InitExecutor.doInit()).start();
    }
}
