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

        List<String> reservoirNames=getReservoirNames(options);
        Set<String> optionNames = options.keySet();

        //计算各库各方案拦蓄洪量、剩余库容、削减洪峰
        Map<String,Map<String,Map<String,Double>>> View = new LinkedHashMap<>();
        for (String reservoirName:reservoirNames){
            Map<String,Map<String,Double>> oneReservoir = new LinkedHashMap<>();

            Map<String,Double> retain_oneReservoir = new LinkedHashMap<>();
            Map<String,Double> remain_oneReservoir = new LinkedHashMap<>();
            Map<String,Double> peakShave_oneReservoir = new LinkedHashMap<>();

            for (String optionName : optionNames) {

                List<Double> Qin_oneReservoir = new ArrayList<>();
                List<Double> Qout_oneReservoir = new ArrayList<>();
                List<Double> V_oneReservoir = new ArrayList<>();
                List<Double> Retain_oneReservoir = new ArrayList<>();
                double retain = 0;
                double remain = 0;
                double peakShave = 0;

                List<Option> option = options.get(optionName);
                for (Option value : option) {
                    String name = value.getName();
                    if (name.equals(reservoirName)) {
                        Qin_oneReservoir.add(value.getQIn());
                        Qout_oneReservoir.add(value.getQOut());
                        V_oneReservoir.add(value.getV());
                        Retain_oneReservoir.add(value.getRetain());
                    }
                }

                if(reservoirName.equals("楼庄子")){
                    retain = BigDecimal.valueOf(Math.max(0,FindMax(Retain_oneReservoir))).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    remain = BigDecimal.valueOf(Math.max(0,7259.33 - FindMax(V_oneReservoir))).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    peakShave = BigDecimal.valueOf(Math.max(0,FindMax(Qin_oneReservoir) - FindMax(Qout_oneReservoir))).setScale(2, RoundingMode.HALF_UP).doubleValue();
                }
                else if(reservoirName.equals("头屯河")){
                    retain = BigDecimal.valueOf(Math.max(0,FindMax(Retain_oneReservoir))).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    remain = BigDecimal.valueOf(Math.max(0,1520.85 - FindMax(V_oneReservoir))).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    peakShave = BigDecimal.valueOf(Math.max(0,FindMax(Qin_oneReservoir) - FindMax(Qout_oneReservoir))).setScale(2, RoundingMode.HALF_UP).doubleValue();
                }

                retain_oneReservoir.put(optionName,retain);
                remain_oneReservoir.put(optionName,remain);
                peakShave_oneReservoir.put(optionName,peakShave);
            }
            oneReservoir.put("拦蓄洪量",retain_oneReservoir);
            oneReservoir.put("剩余库容",remain_oneReservoir);
            oneReservoir.put("削减洪峰",peakShave_oneReservoir);

            View.put(reservoirName,oneReservoir);
        }

        //生成评价
        Map<String,String> Opinion = new LinkedHashMap<>();
        Map<String,Double> Values = new LinkedHashMap<>();
        for (String optionName:optionNames){
            List<Option> options1 = options.get(optionName);
            Map<String,Map<String,Double>> HAQs = new LinkedHashMap<>();
            StringBuilder str = new StringBuilder();

            for (String reservoirName:reservoirNames){
                List<Double> in = new ArrayList<>();
                List<Double> out = new ArrayList<>();
                List<Double> level = new ArrayList<>();
                Map<String,Double> HAQ = new LinkedHashMap<>();

                for (Option option : options1) {
                    if (Objects.equals(option.getName(), reservoirName)) {
                        in.add(option.getQIn());
                        out.add(option.getQOut());
                        level.add(option.getH2());
                    }
                }

                double maxIn = FindMax(in);
                double maxH = FindMax(level);
                double maxOut = FindMax(out);
                str.append(reservoirName).append("最大入库流量为").append(maxIn).append("立方米/秒,最大出库流量为").append(maxOut).append("立方米/秒,最大坝前水位为").append(maxH).append("米；");

                HAQ.put("最高水位",maxH);
                HAQ.put("最大流量",maxOut);
                HAQs.put(reservoirName,HAQ);

            }

            Opinion.put(optionName, str.toString());
            Values.put(optionName,GetValue(HAQs));
        }

        //生成推荐方案
        Set<String> choices = Values.keySet();
        String decision="";
        double best = 0;
        for (String choice:choices){
            if(decision.equals("")){
                decision=choice;
                best=Values.get(decision);
            }

            double value = Values.get(choice);
            if(value<best){
                decision=choice;
                best=value;
            }
        }
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

    public static double GetValue(Map<String,Map<String,Double>> HAQs ){
        double value=0;
        Set<String> reservoirNames =HAQs.keySet();
        for (String reservoirName :reservoirNames){
            if(reservoirName.equals("楼庄子")){
                Map<String,Double> HAQ = HAQs.get("楼庄子");
                double maxH = HAQ.get("最高水位");
                double maxQ = HAQ.get("最大流量");

                //超过特征水位的惩罚
                if(maxH>=1397.63){
                    value=value+100000*(maxH-1397.63);
                }
                else if(maxH>=1397.41){
                    value=value+1000*(maxH-1397.41);
                }
                else if(maxH>=1397.21){
                    value=value+10*(maxH-1397.21);
                }

                //超过预警流量惩罚
                if(maxQ>=125){
                    value+=maxQ-125;
                }

            }
            else if(reservoirName.equals("头屯河")){
                Map<String,Double> HAQ = HAQs.get("头屯河");
                double maxH = HAQ.get("最高水位");
                double maxQ = HAQ.get("最大流量");

                //超过特征水位的惩罚
                if(maxH>=992.54){
                    value=value+100000*(maxH-992.54);
                }
                else if(maxH>=991.2){
                    value=value+1000*(maxH-991.2);
                }
                else if(maxH>=989.6){
                    value=value+10*(maxH-989.6);
                }

                //超过预警流量惩罚
                if(maxQ>=120){
                    value+=maxQ-120;
                }


            }
        }
        return value;
    }

    public static List<String> getReservoirNames(Map<String, List<Option>> options){
        List<String> result = new ArrayList<>();

        Set<String> optionNames = options.keySet();
        for (String optionName:optionNames){
            List<Option> option1 = options.get(optionName);
            for (Option option : option1) {
                String reservoirName = option.getName();
                if (!result.contains(reservoirName)) {
                    result.add(reservoirName);
                }
            }
        }

        return result;
    }

}
