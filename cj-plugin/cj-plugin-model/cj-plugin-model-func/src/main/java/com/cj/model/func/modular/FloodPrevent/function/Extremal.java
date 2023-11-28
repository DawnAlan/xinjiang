package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.model.func.modular.FloodPrevent.entity.Option;

import java.util.*;

public class Extremal {

    /**
     * 计算不同方案的最大水位、最小水位、最大流量、最小流量及出现时间
     */
    public static Map<String,Map<String, Map<String,Object>>> ExtremalCalculator(Map<String, List<Option>> options){
        Map<String,Map<String,Map<String,Object>>> FinalResult = new LinkedHashMap<>();

        Map<String,Map<String,Object>> lzz = new LinkedHashMap<>();
        Map<String,Map<String,Object>> tth = new LinkedHashMap<>();

        Map<String,Object> MaxLevel_lzz = new LinkedHashMap<>();
        Map<String,Object> MaxLevelTime_lzz = new LinkedHashMap<>();
        Map<String,Object> MinLevel_lzz = new LinkedHashMap<>();
        Map<String,Object> MinLevelTime_lzz = new LinkedHashMap<>();
        Map<String,Object> maxQOut_lzz = new LinkedHashMap<>();
        Map<String,Object> maxQOutTime_lzz = new LinkedHashMap<>();
        Map<String,Object> minQOut_lzz = new LinkedHashMap<>();
        Map<String,Object> minQOutTime_lzz = new LinkedHashMap<>();

        Map<String,Object> MaxLevel_tth = new LinkedHashMap<>();
        Map<String,Object> MaxLevelTime_tth = new LinkedHashMap<>();
        Map<String,Object> MinLevel_tth = new LinkedHashMap<>();
        Map<String,Object> MinLevelTime_tth = new LinkedHashMap<>();
        Map<String,Object> maxQOut_tth = new LinkedHashMap<>();
        Map<String,Object> maxQOutTime_tth = new LinkedHashMap<>();
        Map<String,Object> minQOut_tth = new LinkedHashMap<>();
        Map<String,Object> minQOutTime_tth = new LinkedHashMap<>();

        Set<String> keySet = options.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()){
            String key = keys.next();
            List<Option> option = options.get(key);

            List<Date> time_lzz = new ArrayList<>();
            List<Date> time_tth = new ArrayList<>();
            List<Double> H_lzz = new ArrayList<>();
            List<Double> H_tth = new ArrayList<>();
            List<Double> Qout_lzz = new ArrayList<>();
            List<Double> Qout_tth = new ArrayList<>();

            for (int i = 0; i < option.size(); i++) {
                String name = option.get(i).getName();
                if(name.equals("楼庄子")){
                    time_lzz.add(option.get(i).getTime());
                    H_lzz.add(option.get(i).getH2());
                    Qout_lzz.add(option.get(i).getQOut());
                }
                else if(name.equals("头屯河")){
                    time_tth.add(option.get(i).getTime());
                    H_tth.add(option.get(i).getH2());
                    Qout_tth.add(option.get(i).getQOut());
                }
                else{
                    throw new RuntimeException("方案有误");
                }
            }

            MaxLevel_lzz.put(key,GetMax(H_lzz,time_lzz)[0]);
            MaxLevelTime_lzz.put(key,GetMax(H_lzz,time_lzz)[1]);
            MinLevel_lzz.put(key,GetMin(H_lzz,time_lzz)[0]);
            MinLevelTime_lzz.put(key,GetMin(H_lzz,time_lzz)[1]);
            maxQOut_lzz.put(key,GetMax(Qout_lzz,time_lzz)[0]);
            maxQOutTime_lzz.put(key,GetMax(Qout_lzz,time_lzz)[1]);
            minQOut_lzz.put(key,GetMin(Qout_lzz,time_lzz)[0]);
            minQOutTime_lzz.put(key,GetMin(Qout_lzz,time_lzz)[1]);

            MaxLevel_tth.put(key,GetMax(H_tth,time_tth)[0]);
            MaxLevelTime_tth.put(key,GetMax(H_tth,time_tth)[1]);
            MinLevel_tth.put(key,GetMin(H_tth,time_tth)[0]);
            MinLevelTime_tth.put(key,GetMin(H_tth,time_tth)[1]);
            maxQOut_tth.put(key,GetMax(Qout_tth,time_tth)[0]);
            maxQOutTime_tth.put(key,GetMax(Qout_tth,time_tth)[1]);
            minQOut_tth.put(key,GetMin(Qout_tth,time_tth)[0]);
            minQOutTime_tth.put(key,GetMin(Qout_tth,time_tth)[1]);
        }

        lzz.put("最大水位",MaxLevel_lzz);
        lzz.put("最大水位时间",MaxLevelTime_lzz);
        lzz.put("最小水位",MinLevel_lzz);
        lzz.put("最小水位时间",MinLevelTime_lzz);
        lzz.put("最大流量",maxQOut_lzz);
        lzz.put("最大流量时间",maxQOutTime_lzz);
        lzz.put("最小流量",minQOut_lzz);
        lzz.put("最小流量时间",minQOutTime_lzz);

        tth.put("最大水位",MaxLevel_tth);
        tth.put("最大水位时间",MaxLevelTime_tth);
        tth.put("最小水位",MinLevel_tth);
        tth.put("最小水位时间",MinLevelTime_tth);
        tth.put("最大流量",maxQOut_tth);
        tth.put("最大流量时间",maxQOutTime_tth);
        tth.put("最小流量",minQOut_tth);
        tth.put("最小流量时间",minQOutTime_tth);

        FinalResult.put("楼庄子",lzz);
        FinalResult.put("头屯河",tth);

        return FinalResult;

    }

    public static Object[] GetMax(List<Double> Value, List<Date> Time){
        Object[] result = new Object[2];
        if(Value.isEmpty()||Time.isEmpty()||(Value.size()!=Time.size())){
            throw new RuntimeException("方案有误");
        }
        else{
            double max =Value.get(0);
            int num = 0;
            for (int i = 1; i < Value.size(); i++) {
                if(Value.get(i)>=max){
                    max = Value.get(i);
                    num =i;
                }
            }

            result[0]=max;
            result[1]=Time.get(num);
            return result;
        }
    }
    public static Object[] GetMin(List<Double> Value, List<Date> Time){
        Object[] result = new Object[2];
        if(Value.isEmpty()||Time.isEmpty()||(Value.size()!=Time.size())){
            throw new RuntimeException("方案有误");
        }
        else{
            double min =Value.get(0);
            int num = 0;
            for (int i = 1; i < Value.size(); i++) {
                if(Value.get(i)<=min){
                    min = Value.get(i);
                    num =i;
                }
            }

            result[0]=min;
            result[1]=Time.get(num);
            return result;
        }
    }




}
