package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.model.func.modular.FloodPrevent.entity.Option;

import java.util.*;

public class ProcessDetail {
    public static Map<String,Map<String, Map<String, List<Object>>>> ProcessDetailCalculator(Map<String, List<Option>> options){
        Map<String,Map<String, Map<String, List<Object>>>> result = new LinkedHashMap<>();

        Map<String, Map<String, List<Object>>> lzz = new LinkedHashMap<>();
        Map<String, Map<String, List<Object>>> tth = new LinkedHashMap<>();


        Map<String, List<Object>> Time_lzz = new LinkedHashMap<>();
        Map<String, List<Object>> H_lzz = new LinkedHashMap<>();
        Map<String, List<Object>> Q_lzz = new LinkedHashMap<>();

        Map<String, List<Object>> Time_tth = new LinkedHashMap<>();
        Map<String, List<Object>> H_tth = new LinkedHashMap<>();
        Map<String, List<Object>> Q_tth = new LinkedHashMap<>();

        Set<String> keySet = options.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()){
            String key = keys.next();
            List<Option> option = options.get(key);

            List<Object> time_lzz = new ArrayList<>();
            List<Object> Height_lzz = new ArrayList<>();
            List<Object> Out_lzz = new ArrayList<>();

            List<Object> time_tth = new ArrayList<>();
            List<Object> Height_tth = new ArrayList<>();
            List<Object> Out_tth = new ArrayList<>();

            for (int i = 0; i < option.size(); i++) {
                String name = option.get(i).getName();
                if(name.equals("楼庄子")){
                    time_lzz.add(option.get(i).getTime());
                    Height_lzz.add(option.get(i).getH2());
                    Out_lzz.add(option.get(i).getQOut());
                }
                else if(name.equals("头屯河")){
                    time_tth.add(option.get(i).getTime());
                    Height_tth.add(option.get(i).getH2());
                    Out_tth.add(option.get(i).getQOut());
                }
                else{
                    throw new RuntimeException("方案有误");
                }
            }

            Time_lzz.put(key,time_lzz);
            H_lzz.put(key,Height_lzz);
            Q_lzz.put(key,Out_lzz);
            Time_tth.put(key,time_tth);
            H_tth.put(key,Height_tth);
            Q_tth.put(key,Out_tth);
        }
        lzz.put("时间",Time_lzz);
        lzz.put("水位过程",H_lzz);
        lzz.put("流量过程",Q_lzz);

        tth.put("时间",Time_tth);
        tth.put("水位过程",H_tth);
        tth.put("流量过程",Q_tth);

        result.put("楼庄子",lzz);
        result.put("头屯河",tth);

        return result;
    }

}
