package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.model.func.modular.FloodPrevent.entity.Option;

import java.util.*;

public class OverTimes {
    /**
     * 计算不同方案的超过特征水位的时段长度
     */
    public static Map<String,Map<String, Map<String,String>>> OverTimesCalculator(Map<String, List<Option>> options){
        Map<String,Map<String,Map<String,String>>> FinalResult = new LinkedHashMap<>();

        Map<String,Map<String,String>> lzz = new LinkedHashMap<>();
        Map<String,Map<String,String>> tth = new LinkedHashMap<>();

        Map<String,String> beyondLimit_lzz = new LinkedHashMap<>();
        Map<String,String> beyondHeight_lzz = new LinkedHashMap<>();
        Map<String,String> beyondDesign_lzz = new LinkedHashMap<>();
        Map<String,String> beyondLimit_tth = new LinkedHashMap<>();
        Map<String,String> beyondHeight_tth = new LinkedHashMap<>();
        Map<String,String> beyondDesign_tth = new LinkedHashMap<>();

        Set<String> keySet = options.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()){
            String key = keys.next();
            List<Option> option = options.get(key);

            List<Double> H_lzz = new ArrayList<>();
            List<Double> H_tth = new ArrayList<>();
            List<Date> Time_lzz = new ArrayList<>();
            List<Date> Time_tth = new ArrayList<>();
            List<Double> limits_lzz = null;
            List<Double> limits_tth = null;

            for (int i = 0; i < option.size(); i++) {
                String name = option.get(i).getName();
                if(name.equals("楼庄子")){
                    H_lzz.add(option.get(i).getH2());
                    Time_lzz.add(option.get(i).getTime());
                    limits_lzz = option.get(i).getLimits();
//                    limits_lzz = JSONObject.parseArray(option.get(i).getLimits(), Double.class);
                }
                else if(name.equals("头屯河")){
                    H_tth.add(option.get(i).getH2());
                    Time_tth.add(option.get(i).getTime());
                    limits_tth=option.get(i).getLimits();
                }
                else{
                    throw new RuntimeException("方案有误");
                }
            }

            Date date1 = Time_lzz.get(0);
            Date date2 = Time_lzz.get(1);
            int hours = Math.round((float) (date2.getTime() - date1.getTime()) /1000/60/60);



            int a_lzz = hours*getBeyond("楼庄子",H_lzz,Time_lzz,limits_lzz)[0];
            int b_lzz = hours*getBeyond("楼庄子",H_lzz,Time_lzz,limits_lzz)[1];
            int c_lzz = hours*getBeyond("楼庄子",H_lzz,Time_lzz,limits_lzz)[2];

            int a_tth = hours*getBeyond("头屯河",H_tth,Time_tth,limits_tth)[0];
            int b_tth = hours*getBeyond("头屯河",H_tth,Time_tth,limits_tth)[1];
            int c_tth = hours*getBeyond("头屯河",H_tth,Time_tth,limits_tth)[2];

            beyondLimit_lzz.put(key,a_lzz+"小时");
            beyondHeight_lzz.put(key,b_lzz+"小时");
            beyondDesign_lzz.put(key,c_lzz+"小时");

            beyondLimit_tth.put(key,a_tth+"小时");
            beyondHeight_tth.put(key,b_tth+"小时");
            beyondDesign_tth.put(key,c_tth+"小时");
        }

        lzz.put("超汛限水位时段数",beyondLimit_lzz);
        lzz.put("超防洪高水位时段数",beyondHeight_lzz);
        lzz.put("超设计洪水位时段数",beyondDesign_lzz);

        tth.put("超汛限水位时段数",beyondLimit_tth);
        tth.put("超防洪高水位时段数",beyondHeight_tth);
        tth.put("超设计洪水位时段数",beyondDesign_tth);

        FinalResult.put("楼庄子",lzz);
        FinalResult.put("头屯河",tth);

        return FinalResult;
    }

    public static int[] getBeyond(String reservoirName,List<Double> H_end,List<Date> times,List<Double> limits){
        int[] result = new int[3];

        double limitLevel;
        double heightLevel;
        double designLevel;

        int beyondLimitLevel=0;
        int beyondHeightLevel=0;
        int beyondDesignLevel=0;


        if(reservoirName.equals("楼庄子")) {
            heightLevel=1397.21;
            designLevel=1397.41;
        } else {
            heightLevel=989.6;
            designLevel=991.2;
        }

        for (int i = 0; i < H_end.size(); i++) {
            limitLevel=getLimitLevel(times.get(i),limits);

            if (H_end.get(i)>limitLevel) beyondLimitLevel++;
            if (H_end.get(i)>heightLevel) beyondHeightLevel++;
            if (H_end.get(i)>designLevel) beyondDesignLevel++;
        }

        result[0]=beyondLimitLevel;
        result[1]=beyondHeightLevel;
        result[2]=beyondDesignLevel;

        return result;

    }

    public static double getLimitLevel(Date time, List<Double> limits){
        double level;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int month = calendar.get(Calendar.MONTH);
        level=limits.get(month);

        return level;
    }


}
