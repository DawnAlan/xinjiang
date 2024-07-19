package com.cj.model.func.modular.FloodPredict.model;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class getHoursRainTest {
    @SneakyThrows
    public static void main(String[] args) {
//        List<String> level = Arrays.asList("十年一遇","二十年一遇","五十年一遇","百年一遇","千年一遇","典型洪水");
        List<String> level = Arrays.asList("十年一遇","二十年一遇","五十年一遇","百年一遇","千年一遇");
        List<String> rainStation = Arrays.asList("加普沙自动雨量站","东南沟自动雨量站","宰尔德自动雨量站","无名沟自动雨量站","八一林场自动雨量站","萨尔达万自动雨量站","煤矿沟自动雨量站",
                "黑沟自动雨量站", "喀什沟自动雨量站","制材厂自动雨量站","甘沟雨量站","小渠子雨量站","团结一队雨量站","头屯河水库雨量站");
        for (String s : level) {
            Object[][] rainObject = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\模拟降雨.xlsx", s);
            Object[][] result = new Object[rainObject.length][(rainObject[0].length - 1) * 2 + 1];
            for (int j = 0; j < result.length; j++) {
                result[j][0] = rainObject[j][0];
                for (int i = 1; i < rainObject[0].length; i++) {
                    result[j][i*2] = rainObject[j][i];
                    result[0][i*2] = "\"" + rainStation.get(i-1) + "\"";
                }
            }
            for (int j = 1; j < result.length; j++) {
                for (int i = 1; i < rainObject[0].length; i++) {
                    result[j][i*2-1] = "\"" + rainStation.get(i-1) + "\"";
                }
            }
            ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\模拟降雨-前端格式.xlsx",s,result);
        }
    }
}
