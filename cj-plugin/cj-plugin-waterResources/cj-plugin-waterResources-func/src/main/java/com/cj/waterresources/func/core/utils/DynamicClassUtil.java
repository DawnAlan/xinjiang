package com.cj.waterresources.func.core.utils;

import com.alibaba.fastjson.annotation.JSONField;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;

import java.util.List;

public class DynamicClassUtil {
    public static Class getDynamicEntity(List<String> elementList){
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.makeClass("DynamicEntity");
            for(String element : elementList){
                CtField name = new CtField(pool.get(String.class.getCanonicalName()), element, ctClass);
                name.setModifiers(Modifier.PRIVATE);
                ctClass.addField(name);
                //关于这段操作我也不太懂，都是零零散散地收集网上看到的，有大佬懂的话希望帮我解答解答~
                ClassFile classFile = ctClass.getClassFile();
                ConstPool constPool = classFile.getConstPool();
                AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                //创建要添加的注解
                Annotation jsonFileAnnotation = new Annotation(JSONField.class.getCanonicalName(), constPool);
                //设置注解中的属性和值
                jsonFileAnnotation.addMemberValue("serialize", new BooleanMemberValue(false, constPool));
                //把这个注解放到一个AnnotationsAttribute对象里面
                annotationsAttribute.addAnnotation(jsonFileAnnotation);
                //把这个对象怼到要打上这个注解的字段/类上面
                name.getFieldInfo().addAttribute(annotationsAttribute);
                ctClass.addMethod(CtNewMethod.setter("set"+element.substring(0,1).toUpperCase()+element.substring(1).toLowerCase(), name));
                ctClass.addMethod(CtNewMethod.getter("get"+element.substring(0,1).toUpperCase()+element.substring(1).toLowerCase(), name));
            }
            StringBuilder builder = new StringBuilder();
            builder.append("return \"Person{\" +\n" +
                    "                \"name='\" + name + '\\'' +\n" +
                    "                \", age='\" + age + '\\'' +\n" +
                    "                '}';");
            CtMethod toStringMethod = new CtMethod(pool.get("java.lang.String"), "toString", null, ctClass);
            toStringMethod.setBody(builder.toString());
            ctClass.addMethod(toStringMethod);
            Class<?> aClass = ctClass.toClass();
            return aClass;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
