package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.model.func.modular.FloodPrevent.entity.Option;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Containment {

    /**
     * 计算不同方案的拦蓄洪量、剩余库容、削减洪峰
     */
    public static Map<String,Object> ContainmentCalculator(Map<String, List<Option>> options){
        Map<String,Object> FinalResult = new HashMap<>();

        Map<String,Map<String,Map<String,Double>>> View = new LinkedHashMap<>();
        Map<String,String> Opinion = new HashMap<>();

        Map<String,Map<String,Double>> lzz = new LinkedHashMap<>();
        Map<String,Map<String,Double>> tth = new LinkedHashMap<>();

        Map<String,Double> retain_lzz = new LinkedHashMap<>();
        Map<String,Double> remain_lzz = new LinkedHashMap<>();
        Map<String,Double> peakShave_lzz = new LinkedHashMap<>();
        Map<String,Double> retain_tth = new LinkedHashMap<>();
        Map<String,Double> remain_tth = new LinkedHashMap<>();
        Map<String,Double> peakShave_tth = new LinkedHashMap<>();

        List<String> Key_num= new ArrayList<>();
        List<Double[]> HAQ = new ArrayList<>();

        Set<String> keySet = options.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()){
            String key = keys.next();
            List<Option> option = options.get(key);

            List<Double> Qin_lzz = new ArrayList<>();
            List<Double> Qin_tth = new ArrayList<>();
            List<Double> Qout_lzz = new ArrayList<>();
            List<Double> Qout_tth = new ArrayList<>();
            List<Double> H_lzz = new ArrayList<>();
            List<Double> H_tth = new ArrayList<>();
            List<Double> V_lzz = new ArrayList<>();
            List<Double> V_tth = new ArrayList<>();

            for (int i = 0; i < option.size(); i++) {
                String name = option.get(i).getName();
                if(name.equals("楼庄子")){
                    Qin_lzz.add(option.get(i).getQIn());
                    Qout_lzz.add(option.get(i).getQOut());
                    V_lzz.add(option.get(i).getV());
                    H_lzz.add(option.get(i).getH2());
                }
                else if(name.equals("头屯河")){
                    Qin_tth.add(option.get(i).getQIn());
                    Qout_tth.add(option.get(i).getQOut());
                    V_tth.add(option.get(i).getV());
                    H_tth.add(option.get(i).getH2());
                }
                else{
                    throw new RuntimeException("方案有误");
                }
            }

            double a_lzz = BigDecimal.valueOf(FindMax(V_lzz)-V_lzz.get(0)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            double b_lzz = BigDecimal.valueOf(7259.33-FindMax(V_lzz)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            double c_lzz = BigDecimal.valueOf(FindMax(Qin_lzz)-FindMax(Qout_lzz)).setScale(2, RoundingMode.HALF_UP).doubleValue();

            double a_tth = BigDecimal.valueOf(FindMax(V_tth)-V_tth.get(0)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            double b_tth = BigDecimal.valueOf(1297.03-FindMax(V_tth)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            double c_tth = BigDecimal.valueOf(FindMax(Qin_tth)-FindMax(Qout_tth)).setScale(2, RoundingMode.HALF_UP).doubleValue();

            retain_lzz.put(key,a_lzz);
            remain_lzz.put(key,b_lzz);
            peakShave_lzz.put(key,c_lzz);

            retain_tth.put(key,a_tth);
            remain_tth.put(key,b_tth);
            peakShave_tth.put(key,c_tth);

            //计算方案评价
            double maxH_lzz = FindMax(Qout_lzz);
            double maxQ_lzz = FindMax(H_lzz);

            double maxQin = FindMax(Qin_tth);
            double maxH_tth = FindMax(Qout_tth);
            double maxQ_tth = FindMax(H_tth);
            String str="楼庄子最大出库流量为"+maxQ_lzz+"立方米/秒,最大坝前水位为"+maxH_lzz+"米；" +
                       "头屯河最大入库流量为"+maxQin+"立方米/秒，最大出库流量为"+maxQ_tth+"立方米/秒,最大坝前水位为"+maxH_tth+"米。";
            Opinion.put(key,str);

            Key_num.add(key);
            HAQ.add(new Double[]{maxH_lzz, maxQ_lzz,maxH_tth,maxQ_tth});
        }

        lzz.put("拦蓄洪量",retain_lzz);
        lzz.put("剩余库容",remain_lzz);
        lzz.put("削减洪峰",peakShave_lzz);
        tth.put("拦蓄洪量",retain_tth);
        tth.put("剩余库容",remain_tth);
        tth.put("削减洪峰",peakShave_tth);

        View.put("楼庄子",lzz);
        View.put("头屯河",tth);

        double best=HAQ.get(0)[1];
        int num =0;
        for (int i = 0; i < Key_num.size(); i++) {
            double va=HAQ.get(i)[1];
            if(va<=best){
                best=va;
                num=i;
            }
        }
        String decision = Key_num.get(num);

        Opinion.put("推荐方案",decision);

        FinalResult.put("总览",View);
        FinalResult.put("方案评价",Opinion);
        return FinalResult;
    }

    public static double FindMax(List<Double> list){
        double max = 0;
        if (list!=null){
            max =list.get(0);
            for (int i = 1; i < list.size(); i++) {
                double num = list.get(i);
                if(num>max) max=num;
            }
        }
        return max;
    }

}
