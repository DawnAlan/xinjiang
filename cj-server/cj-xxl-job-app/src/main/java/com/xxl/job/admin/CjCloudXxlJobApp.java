
package com.xxl.job.admin;

import com.cj.common.runner.AppStartupListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @description 分布式任务调度平台
 * @author dongxiayu
 * @date 2022/10/21 2:01
 **/
@SpringBootApplication
public class CjCloudXxlJobApp {

	@Bean
	public AppStartupListener appStartupListener(){
		return new AppStartupListener();
	}

	public static void main(String[] args) {
        SpringApplication.run(CjCloudXxlJobApp.class, args);
	}

}