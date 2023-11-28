
package com.cj.dataSynchronization.func.core.config;

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
 * @author xuyuxiang
 * @date 2022/7/7 16:18
 **/
@Configuration
public class DataSynchronizationConfigure {

    @Resource
    private OpenApiExtensionResolver openApiExtensionResolver;

    /**
     * API文档分组配置
     *
     * @author xuyuxiang
     * @date 2022/7/7 16:18
     **/
    @Bean(value = "dataSynchronizationDocApi")
    public Docket dataSynchronizationDocApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("数据同步功能")
                        .description("数据同步功能")
                        .termsOfServiceUrl("")
                        .contact(new Contact("LEO-LUOXU","", ""))
                        .version("2.0.0")
                        .build())
                .globalResponseMessage(RequestMethod.GET, CommonResult.responseList())
                .globalResponseMessage(RequestMethod.POST, CommonResult.responseList())
                .groupName("数据同步功能")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.cj.dataSynchronization"))
                .paths(PathSelectors.any())
                .build().extensions(openApiExtensionResolver.buildExtensions("数据同步功能"));
    }

}
