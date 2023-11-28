package com.cj.waterresources.func.modular;


import com.alibaba.fastjson.JSONObject;
import com.cj.waterresources.func.core.utils.DynamicClassUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class TestD {
    public static void main(String[] args) throws Exception {
        /*ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass("DynamicEntity");
        CtField name = new CtField(pool.get(String.class.getCanonicalName()), "name", ctClass);
        name.setModifiers(Modifier.PRIVATE);
        ctClass.addField(name);
        CtField age = new CtField(pool.get(String.class.getCanonicalName()), "age", ctClass);
        age.setModifiers(Modifier.PRIVATE);
        ctClass.addField(age);
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
        age.getFieldInfo().addAttribute(annotationsAttribute);

        //添加getter setter方法
        ctClass.addMethod(CtNewMethod.setter("setName", name));
        ctClass.addMethod(CtNewMethod.setter("setAge", age));
        ctClass.addMethod(CtNewMethod.getter("getName", name));
        ctClass.addMethod(CtNewMethod.getter("getAge", age));

        //添加toString方法
        StringBuilder builder = new StringBuilder();
        builder.append("return \"Person{\" +\n" +
                "                \"name='\" + name + '\\'' +\n" +
                "                \", age='\" + age + '\\'' +\n" +
                "                '}';");
        CtMethod toStringMethod = new CtMethod(pool.get("java.lang.String"), "toString", null, ctClass);
        toStringMethod.setBody(builder.toString());
        ctClass.addMethod(toStringMethod);
        Class<?> aClass = ctClass.toClass();*/
        Class aClass = DynamicClassUtil.getDynamicEntity(Arrays.asList("age", "name"));
        List<JSONObject> temp = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("age","123");
        jsonObject.put("name","456");
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("age","789");
        jsonObject1.put("name","147");
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("age","852");
        jsonObject2.put("name","963");
        temp.add(jsonObject);
        temp.add(jsonObject1);
        temp.add(jsonObject2);
        String jsonString = temp.toString();
        List<?> objects = JSONObject.parseArray(jsonString, aClass);
        System.out.println(objects);
        List<?> collect = objects.stream().map(t-> Arrays.stream(t.getClass().getFields()).map(t1->t1.getName().equals("name"))).collect(Collectors.toList());
        System.out.println(JSONObject.parseArray(collect.toString(),aClass));
        objects.forEach(t->{
            System.out.println(t.toString());
        });

    }


}
