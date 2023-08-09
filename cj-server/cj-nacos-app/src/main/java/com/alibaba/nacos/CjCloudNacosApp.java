package com.alibaba.nacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Nacos starter.
 * 开发环境调试使用
 *
 * @author nacos
 */
@SpringBootApplication(scanBasePackages = "com.alibaba.nacos")
@ServletComponentScan
@EnableScheduling
public class CjCloudNacosApp {
    
    public static void main(String[] args) {

        // 注意：工程目录不要使用中文
        // 环境变量设置 单机模式 启动
        System.setProperty("nacos.standalone", "true");
        System.setProperty("nacos.core.auth.enabled", "true");
        System.setProperty("server.tomcat.basedir", "logs_nacos");

        SpringApplication.run(CjCloudNacosApp.class, args);
    }
}

