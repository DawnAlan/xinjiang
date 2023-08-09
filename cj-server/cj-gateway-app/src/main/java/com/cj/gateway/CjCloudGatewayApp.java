package com.cj.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;
import com.cj.common.runner.AppStartupListener;

/**
 * SpringBoot方式 CjCloudGatewayApp 启动类
 * @author dongxiayu
 * @date 2020/12/11 12:06
 */
@EnableSwagger2WebMvc
@SpringBootApplication
public class CjCloudGatewayApp {
	public static void main(String[] args) {
		SpringApplication.run(CjCloudGatewayApp.class, args);
	}

	@Bean
	public AppStartupListener appStartupListener(){
		return new AppStartupListener();
	}

}
