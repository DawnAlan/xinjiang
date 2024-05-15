package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.model.TouTunHe;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;


public class InputUtils {

    TimeUtils timeUtils = new TimeUtils();

    public static int beforeDays = 30;

    public static int beforeHours = 10;

    public static Map<String,Object[][]> historyData;

    public static Map<String,Object[][]> hortonParam;

    public static Map<String,Object[][]> machineParam;

    public static Map<String,Object[][]> machineMaxMin;

    /**
     * 判断需要从数据库获取哪些数据
     *
     * @param
     * @return
     * @throws IOException
     */
    public List<Date> judgeDate(String path,Date predictTime, int n) throws IOException {
        List<Date> result = new ArrayList<>();
        historyData = ExcelTool.readExcel(path, "HISTORY-DATA");
        hortonParam = ExcelTool.readExcel(path, "HORTON-PARAM");
        machineParam = ExcelTool.readExcel(path,"MACHINE-PARAM");
        machineMaxMin = ExcelTool.readExcel(path,"MACHINE-MAXMIN");
        Object[][] historyInput = historyData.get("楼庄子日");
        Date historyTime = (Date) historyInput[historyInput.length - 1][0];
        int number = timeUtils.duration(historyTime, predictTime, "日");
        if (number > beforeDays) {
            result.add(historyTime);
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
