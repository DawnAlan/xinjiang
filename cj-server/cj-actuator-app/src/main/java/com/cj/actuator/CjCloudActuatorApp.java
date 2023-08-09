package com.cj.actuator;

import com.cj.common.runner.AppStartupListener;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;


/**
 * SpringBoot方式 CjCloudActuatorApp 启动类
 * actuator监控服务端
 * @author dongxiayu
 * @date 2022/10/19 2:47
 */
@RefreshScope
@EnableAdminServer
@EnableDiscoveryClient
@SpringBootApplication
public class CjCloudActuatorApp {

	public static void main(String[] args) {
		SpringApplication.run(CjCloudActuatorApp.class, args);
	}

	@Bean
	public AppStartupListener appStartupListener(){
		return new AppStartupListener();
	}
}
