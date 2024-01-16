package com.cj.data.core.config;

import com.cj.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;

/**
 * 数据管理插件相关配置
 *
 * @author LB
 * @date 2023/9/21 19:18
 **/
@Configuration
public class DataConfigure {

    @Resource
    private OpenApiExtensionResolver openApiExtensionResolver;

    /**
     * API文档分组配置
     *
     * @author LB
     * @date 2023/9/21 19:18
     **/
    @Bean(value = "DataDocApi")
    public Docket DataDocApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("数据管理功能Data")
                        .description("数据管理功能Data")
                        .termsOfServiceUrl("")
                        .contact(new Contact("CJ_MCloud_TEAM","", ""))
                        .version("2.0.0")
                        .build())
                .globalResponseMessage(RequestMethod.GET, CommonResult.responseList())
                .globalResponseMessage(RequestMethod.POST, CommonResult.responseList())
                .groupName("数据管理功能Data")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.cj.data"))
                .paths(PathSelectors.any())
                .build().extensions(openApiExtensionResolver.buildExtensions("数据管理功能Data"));
    }
}
