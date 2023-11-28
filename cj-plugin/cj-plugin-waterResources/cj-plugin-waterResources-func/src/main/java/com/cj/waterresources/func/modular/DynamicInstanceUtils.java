package com.cj.waterresources.func.modular;

import com.alibaba.fastjson.JSONObject;
import javassist.*;
import jdk.nashorn.api.scripting.JSObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * java动态创建实体类工具类
 */
public class DynamicInstanceUtils {
    public static Object getInstance(String className) throws CannotCompileException, IllegalAccessException, InstantiationException {
        //创建一个类模板
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("DynamicEntity");

        //添加类的字段
        CtField field = new CtField(CtClass.charType, "key", cc);
        CtField field1 = new CtField(CtClass.charType, "name", cc);
        cc.addField(field);
        cc.addField(field1);
        //设置字段的访问修饰符
        field.setModifiers(Modifier.PUBLIC);
        field1.setModifiers(Modifier.PUBLIC);
        //设置字段的数据类型
        field.setType(CtClass.charType);
        field1.setType(CtClass.charType);
        //设置字段的名称
        field.setName("key");
        field1.setName("name");

        //创建类模板的实例
        Object obj = cc.toClass().newInstance();
        return obj;
    }

    public static void main(String[] args) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key","123");
            jsonObject.put("name","456");
            String jsonString = jsonObject.toJSONString();
            Object instance = DynamicInstanceUtils.getInstance("");
            Class<?> aClass = instance.getClass();
            List<?> objects = JSONObject.parseArray(jsonString, aClass);
            objects.forEach(t->{
                System.out.println(t);
            });
            for (Field declaredField : instance.getClass().getDeclaredFields()) {
                System.out.println(declaredField.getName());
                System.out.println(declaredField.getType());
            }

        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
