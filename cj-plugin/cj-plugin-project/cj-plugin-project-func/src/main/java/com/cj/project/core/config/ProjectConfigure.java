
package com.cj.project.core.config;

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
 * 业务相关配置
 *
 * @author Lb
 * @date 2023年8月29日19:02:27
 **/
@Configuration
public class ProjectConfigure {

    @Resource
    private OpenApiExtensionResolver openApiExtensionResolver;

    /**
     * API文档分组配置
     *
     * @author xuyuxiang
     * @date 2022/7/7 16:18
     **/
    @Bean(value = "ProjectDocApi")
    public Docket ProjectDocApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("项目基础功能Project")
                        .description("项目基础管理功能Project")
                        .termsOfServiceUrl("")
                        .contact(new Contact("CJ_MCloud_TEAM","", ""))
                        .version("1.0.0")
                        .build())
                .globalResponseMessage(RequestMethod.GET, CommonResult.responseList())
                .globalResponseMessage(RequestMethod.POST, CommonResult.responseList())
                .groupName("项目基础功能Project")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.cj.project"))
                .paths(PathSelectors.any())
                .build().extensions(openApiExtensionResolver.buildExtensions("项目基础功能Project"));
    }
}
