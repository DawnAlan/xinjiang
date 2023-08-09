package com.cj.biz.core;

import com.cj.common.runner.AppStartupListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * SpringBoot方式启动类
 *
 * @author xuyuxiang
 * @date 2021/12/18 16:57
 */
@Slf4j
@RestController
@EnableSwagger2WebMvc
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cj")
@SpringBootApplication(scanBasePackages = {"com.cj"})
public class CjCloudBizApp {

    /* 解决druid 日志报错：discard long time none received connection:xxx */
    static {
        System.setProperty("druid.mysql.usePingMethod","false");
    }

    /**
     * 主启动函数
     *
     * @author xuyuxiang
     * @date 2022/7/30 21:42
     */
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(CjCloudBizApp.class);
        springApplication.run(args);
        log.info(">>> {}", CjCloudBizApp.class.getSimpleName().toUpperCase() + " STARTING SUCCESS");
    }

    /**
     * 首页
     *
     * @author xuyuxiang
     * @date 2022/7/8 14:22
     **/
    @GetMapping("/")
    public String index() {
        return "WELCOME";
    }

    @Bean
    public AppStartupListener appStartupListener(){
        return new AppStartupListener();
    }
}
