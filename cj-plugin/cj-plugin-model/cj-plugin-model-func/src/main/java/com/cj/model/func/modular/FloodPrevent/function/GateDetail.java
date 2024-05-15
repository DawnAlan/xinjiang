package com.cj.model.func.modular.FloodPrevent.function;

import com.alibaba.fastjson.JSONObject;
import com.cj.model.func.modular.FloodPrevent.entity.Option;

import java.util.*;

public class GateDetail {
    public static Map<String,Map<String, Map<String, List<Object>>>> GateDetailCalculator(Map<String, List<Option>> options){
        Map<String,Map<String, Map<String, List<Object>>>> result = new LinkedHashMap<>();

        Map<String, Map<String, List<Object>>> lzz = new LinkedHashMap<>();
        Map<String, Map<String, List<Object>>> tth = new LinkedHashMap<>();


        Map<String, List<Object>> Time_lzz = new LinkedHashMap<>();
        Map<String, List<Object>> Q1_lzz = new LinkedHashMap<>();
        Map<String, List<Object>> Q2_lzz = new LinkedHashMap<>();

        Map<String, List<Object>> Time_tth = new LinkedHashMap<>();
        Map<String, List<Object>> Q1_tth = new LinkedHashMap<>();
        Map<String, List<Object>> Q2_tth = new LinkedHashMap<>();
        Map<String, List<Object>> Q3_tth = new LinkedHashMap<>();

        Set<String> keySet = options.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()){
            String key = keys.next();
            List<Option> option = options.get(key);

            List<Object> time_lzz = new ArrayList<>();
            List<Object> q1_lzz = new ArrayList<>();
            List<Object> q2_lzz = new ArrayList<>();

            List<Object> time_tth = new ArrayList<>();
            List<Object> q1_tth = new ArrayList<>();
            List<Object> q2_tth = new ArrayList<>();
            List<Object> q3_tth = new ArrayList<>();

            for (int i = 0; i < option.size(); i++) {
                String name = option.get(i).getName();
                List<Double> singleList = JSONObject.parseArray(option.get(i).getQSingleString(), Double.class);
                if(name.equals("楼庄子")){
                    time_lzz.add(option.get(i).getTime());
                    q1_lzz.add(singleList.get(0));
                    q2_lzz.add(singleList.get(1));
                }
                else if(name.equals("头屯河")){
                    time_tth.add(option.get(i).getTime());
                    q1_tth.add(singleList.get(0));
                    q2_tth.add(singleList.get(1));
                    q3_tth.add(singleList.get(2));
                }
                else{
                    throw new RuntimeException("方案有误");
                }
            }

            Time_lzz.put(key,time_lzz);
            Q1_lzz.put(key,q1_lzz);
            Q2_lzz.put(key,q2_lzz);
            Time_tth.put(key,time_tth);
            Q1_tth.put(key,q1_tth);
            Q2_tth.put(key,q2_tth);
            Q3_tth.put(key,q3_tth);
        }
        lzz.put("时间",Time_lzz);
        lzz.put("溢洪洞",Q1_lzz);
        lzz.put("泄洪冲沙导流洞",Q2_lzz);


        tth.put("时间",Time_tth);
        tth.put("放水涵洞",Q1_tth);
        tth.put("泄水隧洞",Q2_tth);
        tth.put("溢洪道",Q3_tth);

        result.put("楼庄子",lzz);
        result.put("头屯河",tth);

        return result;
    }


}
