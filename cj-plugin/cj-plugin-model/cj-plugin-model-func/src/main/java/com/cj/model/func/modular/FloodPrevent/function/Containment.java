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
        Map<String,Object> FinalResult = new LinkedHashMap<>();

        Map<String,Map<String,Map<String,Double>>> View = new LinkedHashMap<>();
        Map<String,String> Opinion = new LinkedHashMap<>();

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
        for (String key : keySet) {
            List<Option> option = options.get(key);

            List<Double> Qin_lzz = new ArrayList<>();
            List<Double> Qin_tth = new ArrayList<>();
            List<Double> Qout_lzz = new ArrayList<>();
            List<Double> Qout_tth = new ArrayList<>();
            List<Double> H_lzz = new ArrayList<>();
            List<Double> H_tth = new ArrayList<>();
            List<Double> V_lzz = new ArrayList<>();
            List<Double> V_tth = new ArrayList<>();
            List<Double> Retain_lzz = new ArrayList<>();
            List<Double> Retain_tth = new ArrayList<>();


            for (Option value : option) {
                String name = value.getName();
                if (name.equals("楼庄子")) {
                    Qin_lzz.add(value.getQIn());
                    Qout_lzz.add(value.getQOut());
                    V_lzz.add(value.getV());
                    H_lzz.add(value.getH2());
                    Retain_lzz.add(value.getRetain());
                } else if (name.equals("头屯河")) {
                    Qin_tth.add(value.getQIn());
                    Qout_tth.add(value.getQOut());
                    V_tth.add(value.getV());
                    H_tth.add(value.getH2());
                    Retain_tth.add(value.getRetain());
                } else {
                    throw new RuntimeException("方案有误");
                }
            }

            double a_lzz = BigDecimal.valueOf(FindMax(Retain_lzz)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            double b_lzz = BigDecimal.valueOf(7259.33 - FindMax(V_lzz)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            double c_lzz = BigDecimal.valueOf(FindMax(Qin_lzz) - FindMax(Qout_lzz)).setScale(2, RoundingMode.HALF_UP).doubleValue();

            double a_tth = BigDecimal.valueOf(FindMax(Retain_tth)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            double b_tth = BigDecimal.valueOf(1297.03 - FindMax(V_tth)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            double c_tth = BigDecimal.valueOf(FindMax(Qin_tth) - FindMax(Qout_tth)).setScale(2, RoundingMode.HALF_UP).doubleValue();

            retain_lzz.put(key, Math.max(a_lzz, 0));
            remain_lzz.put(key, Math.max(b_lzz, 0));
            peakShave_lzz.put(key, Math.max(0, c_lzz));

            retain_tth.put(key, Math.max(0, a_tth));
            remain_tth.put(key, Math.max(0, b_tth));
            peakShave_tth.put(key, Math.max(0, c_tth));

            //计算方案评价
            double maxH_lzz = FindMax(H_lzz);
            double maxQ_lzz = FindMax(Qout_lzz);

            double maxQin = FindMax(Qin_tth);
            double maxH_tth = FindMax(H_tth);
            double maxQ_tth = FindMax(Qout_tth);
            String str = "楼庄子最大出库流量为" + maxQ_lzz + "立方米/秒,最大坝前水位为" + maxH_lzz + "米；" +
                    "头屯河最大入库流量为" + maxQin + "立方米/秒，最大出库流量为" + maxQ_tth + "立方米/秒,最大坝前水位为" + maxH_tth + "米。";
            Opinion.put(key, str);

            Key_num.add(key);
            HAQ.add(new Double[]{maxH_lzz, maxQ_lzz, maxH_tth, maxQ_tth});
        }
        lzz.put("削减洪峰",peakShave_lzz);
        lzz.put("剩余库容",remain_lzz);
        lzz.put("拦蓄洪量",retain_lzz);

        tth.put("削减洪峰",peakShave_tth);
        tth.put("剩余库容",remain_tth);
        tth.put("拦蓄洪量",retain_tth);

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
