package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.model.func.modular.FloodPrevent.entity.Option;

import java.util.*;

public class OverLevels {

    /**
     * 计算不同方案的超过特征水位的时段个数
     */
    public static Map<String,Map<String,Map<String,Integer>>> OverLevelsCalculator(Map<String, List<Option>> options){
        Map<String,Map<String,Map<String,Integer>>> FinalResult = new LinkedHashMap<>();

        Map<String,Map<String,Integer>> lzz = new LinkedHashMap<>();
        Map<String,Map<String,Integer>> tth = new LinkedHashMap<>();

        Map<String,Integer> beyondLimit_lzz = new LinkedHashMap<>();
        Map<String,Integer> beyondHeight_lzz = new LinkedHashMap<>();
        Map<String,Integer> beyondDesign_lzz = new LinkedHashMap<>();
        Map<String,Integer> beyondLimit_tth = new LinkedHashMap<>();
        Map<String,Integer> beyondHeight_tth = new LinkedHashMap<>();
        Map<String,Integer> beyondDesign_tth = new LinkedHashMap<>();

        Set<String> keySet = options.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()){
            String key = keys.next();
            List<Option> option = options.get(key);

            List<Double> H_lzz = new ArrayList<>();
            List<Double> H_tth = new ArrayList<>();

            for (int i = 0; i < option.size(); i++) {
                String name = option.get(i).getName();
                if(name.equals("楼庄子")){
                    H_lzz.add(option.get(i).getH2());
                }
                else if(name.equals("头屯河")){
                    H_tth.add(option.get(i).getH2());
                }
                else{
                    throw new RuntimeException("方案有误");
                }
            }

            int a_lzz = getBeyond("楼庄子",H_lzz)[0];
            int b_lzz = getBeyond("楼庄子",H_lzz)[1];
            int c_lzz = getBeyond("楼庄子",H_lzz)[2];

            int a_tth = getBeyond("头屯河",H_tth)[0];
            int b_tth = getBeyond("头屯河",H_tth)[1];
            int c_tth = getBeyond("头屯河",H_tth)[2];

            beyondLimit_lzz.put(key,a_lzz);
            beyondHeight_lzz.put(key,b_lzz);
            beyondDesign_lzz.put(key,c_lzz);

            beyondLimit_tth.put(key,a_tth);
            beyondHeight_tth.put(key,b_tth);
            beyondDesign_tth.put(key,c_tth);
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

    public static int[] getBeyond(String reservoirName,List<Double> H_end){
        int[] result = new int[3];

        double limitLevel;
        double heightLevel;
        double designLevel;

        int beyondLimitLevel=0;
        int beyondHeightLevel=0;
        int beyondDesignLevel=0;


        if(reservoirName.equals("楼庄子")) {
            limitLevel =1394.5;
            heightLevel=1397.21;
            designLevel=1397.41;
        } else {
            limitLevel =987;
            heightLevel=989.6;
            designLevel=991.2;
        }

        for (int i = 0; i < H_end.size(); i++) {
            if (H_end.get(i)>limitLevel) beyondLimitLevel++;
            if (H_end.get(i)>heightLevel) beyondHeightLevel++;
            if (H_end.get(i)>designLevel) beyondDesignLevel++;
        }

        result[0]=beyondLimitLevel;
        result[1]=beyondHeightLevel;
        result[2]=beyondDesignLevel;

        return result;

    }


}
