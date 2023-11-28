package com.cj.dataSynchronization.core.config;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.common.annotation.CommonNoRepeat;
import com.cj.common.annotation.CommonWrapper;
import com.cj.common.cache.CommonCacheOperator;
import com.cj.common.consts.FeignConstant;
import com.cj.common.enums.CommonDeleteFlagEnum;
import com.cj.common.enums.SysBuildInEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.listener.CommonDataChangeEventCenter;
import com.cj.common.listener.CommonDataChangeListener;
import com.cj.common.pojo.CommonResult;
import com.cj.common.pojo.CommonWrapperInterface;
import com.cj.common.util.CommonTimeFormatUtil;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Snowy配置
 *
 * @author xuyuxiang
 * @date 2021/10/9 14:24
 **/
@Configuration
@MapperScan(basePackages = {"com.cj.**.mapper,com.bstek.**.mapper"})
public class GlobalConfigure implements WebMvcConfigurer {

    private static final String COMMON_REPEAT_SUBMIT_CACHE_KEY = "common-repeatSubmit:";

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Resource
    private OpenApiExtensionResolver openApiExtensionResolver;


    /**
     * RedisTemplate序列化
     *
     * @author xuyuxiang
     * @date 2022/6/21 17:01
     **/
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 静态资源映射
     *
     * @author xuyuxiang
     * @date 2022/7/25 15:16
     **/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/ureport/res/**").addResourceLocations("classpath:/META-INF/resources/ureport-asserts/");
    }

    /**
     * 添加节流防抖拦截器
     *
     * @author xuyuxiang
     * @date 2022/6/20 15:18
     **/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                     @NonNull Object handler) throws Exception {
                if (handler instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod) handler;
                    Method method = handlerMethod.getMethod();
                    CommonNoRepeat annotation = method.getAnnotation(CommonNoRepeat.class);
                    if (ObjectUtil.isNotEmpty(annotation)) {
                        JSONObject repeatSubmitJsonObject = this.isRepeatSubmit(request, annotation);
                        if (repeatSubmitJsonObject.getBool("repeat")) {
                            response.setCharacterEncoding(CharsetUtil.UTF_8);
                            response.setContentType(ContentType.JSON.toString());
                            response.getWriter().write(JSONUtil.toJsonStr(CommonResult.error("请求过于频繁，请" + repeatSubmitJsonObject.getStr("time") + "后再试")));
                            return false;
                        }
                    }
                }
                return true;
            }

            public JSONObject isRepeatSubmit(HttpServletRequest request, CommonNoRepeat annotation) {
                JSONObject jsonObject = JSONUtil.createObj();
                jsonObject.set("repeatParam", JSONUtil.toJsonStr(request.getParameterMap()));
                jsonObject.set("repeatTime", DateUtil.current());
                String url = request.getRequestURI();
                // 获取该接口缓存的限流数据
                Object cacheObj = commonCacheOperator.get(COMMON_REPEAT_SUBMIT_CACHE_KEY + url);
                if (ObjectUtil.isNotEmpty(cacheObj)) {
                    JSONObject cacheJsonObject = JSONUtil.parseObj(cacheObj);
                    if(cacheJsonObject.containsKey(url)) {
                        JSONObject existRepeatJsonObject = cacheJsonObject.getJSONObject(url);
                        // 如果与上次参数一致，且时间间隔小于要求的限流时长，则判定为重复提交
                        if (jsonObject.getStr("repeatParam").equals(existRepeatJsonObject.getStr("repeatParam"))) {
                            long interval = jsonObject.getLong("repeatTime") - existRepeatJsonObject.getLong("repeatTime");
                            if(interval < annotation.interval()) {
                                long secondsParam = (annotation.interval() - interval) / 1000;
                                if(secondsParam == 0) {
                                    return JSONUtil.createObj().set("repeat", false);
                                } else {
                                    return JSONUtil.createObj().set("repeat", true).set("time", CommonTimeFormatUtil.formatSeconds(secondsParam));
                                }
                            }
                        }
                    }
                }
                // 缓存最新的该接口的限流数据，为防止缓存的数据过多，缓存时效为1小时
                commonCacheOperator.put(COMMON_REPEAT_SUBMIT_CACHE_KEY + url, JSONUtil.createObj().set(url, jsonObject), 60 * 60);
                return JSONUtil.createObj().set("repeat", false);
            }
        }).addPathPatterns("/**");
    }

    /**
     * 通用Wrapper的AOP
     *
     * @author xuyuxiang
     * @date 2022/9/15 21:24
     */
    @Component
    @Aspect
    public static class CommonWrapperAop {

        /**
         * 切入点
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:27
         */
        @Pointcut("@annotation(com.cj.common.annotation.CommonWrapper)")
        private void wrapperPointcut() {

        }

        /**
         * 执行包装
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:27
         */
        @Around("wrapperPointcut()")
        public Object doWrapper(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            // 直接执行原有业务逻辑
            Object proceedResult = proceedingJoinPoint.proceed();
            return processWrapping(proceedingJoinPoint, proceedResult);
        }

        /**
         * 具体包装过程
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:27
         */
        @SuppressWarnings("all")
        private Object processWrapping(ProceedingJoinPoint proceedingJoinPoint, Object originResult) throws IllegalAccessException, InstantiationException {
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method method = methodSignature.getMethod();
            CommonWrapper commonWrapper = method.getAnnotation(CommonWrapper.class);
            Class<? extends CommonWrapperInterface<?>>[] baseWrapperClasses = commonWrapper.value();
            if (ObjectUtil.isEmpty(baseWrapperClasses)) {
                return originResult;
            }
            if (!(originResult instanceof CommonResult)) {
                return originResult;
            }
            CommonResult commonResult = (CommonResult) originResult;
            Object beWrapped = commonResult.getData();
            if (ObjectUtil.isBasicType(beWrapped)) {
                throw new CommonException("被包装的值不能是基本类型");
            }
            if (beWrapped instanceof Page) {
                Page page = (Page) beWrapped;
                ArrayList<Map<String, Object>> maps = new ArrayList<>();
                for (Object wrappedItem : page.getRecords()) {
                    maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
                }
                page.setRecords(maps);
                commonResult.setData(page);
            } else if (beWrapped instanceof Collection) {
                Collection collection = (Collection) beWrapped;
                List<Map<String, Object>> maps = new ArrayList<>();
                for (Object wrappedItem : collection) {
                    maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
                }
                commonResult.setData(maps);
            } else if (ArrayUtil.isArray(beWrapped)) {
                Object[] objects = this.objToArray(beWrapped);
                ArrayList<Map<String, Object>> maps = new ArrayList<>();
                for (Object wrappedItem : objects) {
                    maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
                }
                commonResult.setData(maps);
            } else {
                commonResult.setData(this.wrapPureObject(beWrapped, baseWrapperClasses));
            }
            return commonResult;
        }

        /**
         * 原始对象包装JSONObject
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:36
         */
        @SuppressWarnings("all")
        private JSONObject wrapPureObject(Object originModel, Class<? extends CommonWrapperInterface<?>>[] baseWrapperClasses) {
            JSONObject jsonObject = JSONUtil.parseObj(originModel);
            try {
                for (Class<? extends CommonWrapperInterface<?>> commonWrapperClass : baseWrapperClasses) {
                    CommonWrapperInterface commonWrapperInterface = commonWrapperClass.newInstance();
                    Map<String, Object> incrementFieldsMap = commonWrapperInterface.doWrap(originModel);
                    jsonObject.putAll(incrementFieldsMap);
                }
            } catch (Exception e) {
                throw new CommonException("原始对象包装过程，字段转化异常：{}", e.getMessage());
            }
            return jsonObject;
        }

        /**
         * Object转array
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:34
         */
        private Object[] objToArray(Object object) {
            int length = Array.getLength(object);
            Object[] result = new Object[length];
            for (int i = 0; i < result.length; i++) {
                result[i] = Array.get(object, i);
            }
            return result;
        }
    }

    /**
     * 数据库id选择器，用于Mapper.xml中
     * MyBatis可以根据不同的数据库厂商执行不同的语句
     *
     * @author xuyuxiang
     * @date 2022/1/8 2:16
     */
    @Component
    public static class CustomDbIdProvider implements DatabaseIdProvider {

        @Override
        public String getDatabaseId(DataSource dataSource) throws SQLException {
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                String url = conn.getMetaData().getURL().toLowerCase();
                if (url.contains("jdbc:oracle")) {
                    return "oracle";
                } else if (url.contains("jdbc:postgresql")) {
                    return "pgsql";
                } else if (url.contains("jdbc:mysql")) {
                    return "mysql";
                } else if (url.contains("jdbc:dm")) {
                    return "dm";
                } else if (url.contains("jdbc:kingbase")) {
                    return "kingbase";
                }  else {
                    return "mysql";
                }
            } finally {
                JdbcUtils.closeConnection(conn);
            }
        }
    }


    /**
     * 应用API文档分组配置
     *
     * @author dongxiayu
     * @date 2022/7/7 16:18
     **/
    @Bean(value = "appApiDoc")
    public Docket appApiDoc() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title(FeignConstant.DATA_SYNCHRONIZATION_APP)
                        .description(FeignConstant.DATA_SYNCHRONIZATION_APP)
                        .termsOfServiceUrl("cj")
                        .contact(new Contact("LEO_LUOXU","cj", "-"))
                        .version("2.0.0")
                        .build())
                .globalResponseMessage(RequestMethod.GET, CommonResult.responseList())
                .globalResponseMessage(RequestMethod.POST, CommonResult.responseList())
                .groupName(FeignConstant.DATA_SYNCHRONIZATION_APP)
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.cj"))
                .paths(PathSelectors.any())
                .build().extensions(openApiExtensionResolver.buildExtensions(FeignConstant.DATA_SYNCHRONIZATION_APP));
    }

    /**
     * 启用分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

}
