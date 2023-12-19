
package com.cj.inspection.core.listener;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.Log;
import com.cj.common.cache.CommonCacheOperator;
import com.cj.common.consts.CacheConstant;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 资源搜集器，将项目中所有接口（带@RequestMapping的）都搜集起来
 *  <p>
 *  搜集到的接口会被缓存
 *
 * @author dongxiayu
 * @date 2023/1/29 23:24
 **/
@Component
public class ResourceCollectListener implements CommandLineRunner {

    private static final Log log = Log.get();

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Override
    public void run(String... args) {

        //1.获取所有后端接口
        List<String> permissionResult = CollectionUtil.newArrayList();
        SpringUtil.getApplicationContext().getBeansOfType(RequestMappingHandlerMapping.class).values()
                .forEach(requestMappingHandlerMapping -> requestMappingHandlerMapping.getHandlerMethods()
                        .forEach((key, value) -> {
                            SaCheckPermission saCheckPermission = value.getMethod().getAnnotation(SaCheckPermission.class);
                            if(ObjectUtil.isNotEmpty(saCheckPermission)) {
                                PatternsRequestCondition patternsCondition = key.getPatternsCondition();
                                if (patternsCondition != null) {
                                    String apiName = "未定义接口名称";
                                    ApiOperation apiOperation = value.getMethod().getAnnotation(ApiOperation.class);
                                    if(ObjectUtil.isNotEmpty(apiOperation)) {
                                        String annotationValue = apiOperation.value();
                                        if(ObjectUtil.isNotEmpty(annotationValue)) {
                                            apiName = annotationValue;
                                        }
                                    }
                                    permissionResult.add(patternsCondition.getPatterns().iterator().next() + StrUtil.BRACKET_START + apiName + StrUtil.BRACKET_END);
                                }
                            }
                        }));

        //2.汇总添加到缓存
        Object permissionResourceObject = commonCacheOperator.get(CacheConstant.PERMISSION_RESOURCE_CACHE_KEY);
        List<String> permissionResource = null;
        if(Objects.isNull(permissionResourceObject)){
            permissionResource = CollUtil.newArrayList();
        }else{
            permissionResource = Convert.toList(String.class,permissionResourceObject);
        }
        if(CollUtil.isNotEmpty(permissionResult)){
            for(String permission : permissionResult){
                if(!permissionResource.contains(permission)){
                    permissionResource.add(permission);
                }
            }

            // 刷新缓存
            commonCacheOperator.put(CacheConstant.PERMISSION_RESOURCE_CACHE_KEY, permissionResource);
        }

        log.info(">>> 缓存资源URL集合完成!资源数量：{}", permissionResult.size());
    }
}
