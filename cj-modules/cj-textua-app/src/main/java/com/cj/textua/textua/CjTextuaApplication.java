package com.cj.textua.textua;

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

@Slf4j
@EnableSwagger2WebMvc
@RestController
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cj")
@SpringBootApplication(scanBasePackages = {"com.cj"})
public class CjTextuaApplication {

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
        SpringApplication springApplication = new SpringApplication(CjTextuaApplication.class);
        springApplication.run(args);
        log.info(">>> {}", CjTextuaApplication.class.getSimpleName().toUpperCase() + " STARTING SUCCESS");
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
