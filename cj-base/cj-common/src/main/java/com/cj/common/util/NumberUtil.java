package com.cj.common.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NumberUtil {

    /**
     * 针对2位小数的四舍五入
     * @param integer
     * @param decimal
     * @return
     */
    public static List<String> roundDecimal(String integer, String decimal){
        try {
            List<String> list = new ArrayList<>();
            if(decimal.length()==1){
                list.add(integer);
                list.add("0."+decimal);
            }
            if(decimal.length()==2 ||decimal.length()==3) {
                byte[] arr = decimal.getBytes("UTF-8");
                char c = (char) arr[0];
                char c1 = (char) arr[1];
                if (c1 == 48) {
                    list.add(integer);
                    list.add(Double.parseDouble("0." + c + "0") + "");
                }
                if (49 <= c1 && 53 >= c1) {
                    list.add(integer);
                    list.add(Double.parseDouble("0." + c + "5") + "");
                }
                if (53 < c1 && 57 >= c1) {
                    if (Double.parseDouble("0." + c) + 0.1 == 1) {
                        list.add(Integer.parseInt(integer) + 1 + "");
                        list.add("0");
                    } else {
                        list.add(integer);
                        list.add(Double.parseDouble("0." + c) + 0.1 + "");
                    }

                }
            }

            return list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String test = new BigDecimal("123.992").setScale(2, BigDecimal.ROUND_UP).toString();
        System.out.println(test);
        String[] split = test.split("\\.");
        List<String> strings = roundDecimal(split[0], split[1]);
        System.out.println(strings.toString());
    }
}
