package com.cj.model.func.modular.FloodPredict.utils;

import java.io.IOException;

import java.util.*;


public class InputUtils {

    static TimeUtils timeUtils = new TimeUtils();

    public static int beforeDays = 20;

    public static int beforeHours = 20;

    public static Date historyDate;

    public static Double lzzWaterLevel = 1394.5;
    public static Double tthWaterLevel = 988.0;

    public static Map<String,Object[][]> historyData;

    public static Map<String,Object[][]> hortonParam;

    public static Map<String,Object[][]> machineParam;

    public static Map<String,Object[][]> machineMaxMin;

    public static void getData(String path) throws IOException {
        historyData = ExcelTool.readExcel(path, "HISTORY-DATA");
        hortonParam = ExcelTool.readExcel(path, "HORTON-PARAM");
        machineParam = ExcelTool.readExcel(path,"MACHINE-PARAM");
        machineMaxMin = ExcelTool.readExcel(path,"MACHINE-MAXMIN");
        historyDate = (Date) historyData.get("楼庄子日")[historyData.get("楼庄子日").length-1][0];
    }
    public static void getData2(String path) throws IOException {
        historyData = ExcelTool.readExcel2(path, "HISTORY-DATA");
        hortonParam = ExcelTool.readExcel2(path, "HORTON-PARAM");
        machineParam = ExcelTool.readExcel2(path,"MACHINE-PARAM");
        machineMaxMin = ExcelTool.readExcel2(path,"MACHINE-MAXMIN");
        historyDate = (Date) historyData.get("楼庄子日")[historyData.get("楼庄子日").length-1][0];
    }

    /**
     * 判断需要从数据库获取哪些数据
     *
     * @param
     * @return
     */
    public static List<Date> judgeDate(Date predictTime, int n) {
        List<Date> result = new ArrayList<>();
        Date hisDate = timeUtils.addCalendar(InputUtils.historyDate,"日",1);
        int number = timeUtils.duration(hisDate, predictTime, "日");
        if (number > beforeDays) {
            result.add(hisDate);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(predictTime);
            calendar.add(Calendar.DAY_OF_MONTH, -beforeDays);
            Date startTime = calendar.getTime();
            result.add(startTime);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(predictTime);
        calendar.add(Calendar.DAY_OF_MONTH, (n / 24) + 1);
        predictTime = calendar.getTime();
        result.add(predictTime);
        return result;
    }

}
