package com.cj.dataSynchronization;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.cj.common.runner.AppStartupListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Slf4j
@RestController
@EnableSwagger2WebMvc
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cj")
@SpringBootApplication(scanBasePackages = {"com.cj"})
public class CjDataSynchronizationApp {

	static {
		System.setProperty("druid.mysql.usePingMethod","false");
	}

	public static void main(String[] args) {
		SpringApplication.run(CjDataSynchronizationApp.class, args);
	}

	@Bean
	public AppStartupListener appStartupListener(){
		return new AppStartupListener();
	}

	@GetMapping("/")
	public String index() {
		return "WELCOME";
	}

}
