package com.cj.common.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

/***
 * @Author: yly
 * Map转换工具类
 * Create time 2024/1/10 19:03
*/
@Slf4j
@Component
public class MapTransformUtil {


    //Map转换Class
    @SneakyThrows
    public Object mapTransformClass(Map<String, Object> map , Class<?> targetClass){

        // 获取要转换的目标Class类型
        //Class<?> targetClass = MyClass.class;

        // 使用反射机制动态生成新的对象并设置属性值
        Constructor<?> constructor = targetClass.getConstructor();
        Object object = constructor.newInstance();

        for (Field field : targetClass.getDeclaredFields()) {
            String key = field.getName();

            if (map.containsKey(key)) {
                field.setAccessible(true);

                try {
                    field.set(object, map.get(key));
                } catch (Exception e) {
                    //抛出异常
                    System.out.println("无法将" + map.get(key).toString() + "赋值给字段" + field.getName());
                }
            } else {
                //打印日志
                log.info("未找到与字段" + field.getName() + "相关联的键");
            }
        }
        return object;
    }
}
