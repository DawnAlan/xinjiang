package com.cj.model.func.modular.FloodPredict.model.test;

import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.*;
import static com.cj.model.func.modular.FloodPredict.utils.Tools.AddObject;

public class FlowProcessing {
    public static void main(String[] args) throws ParseException, IOException, InvalidFormatException {

//        int year = 2012;
//        List<Object[][]> resultList = new ArrayList<>();
//        for (int i = 0; i < 12; i++) {
//            Object[][] input = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\日进库\\"+year+"-头屯河水库-头屯河进库-日汇总表-日平均流量表.xlsx","日平均流量表");
//            Object[][] result = getDays(input,year);
//            resultList.add(result);
//            year++;
//        }
//        Object[][] result = AddObject(resultList);
//        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\日尺度数据.xlsx","日尺度" ,result);
        Object[][] input = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx","头屯河日总数据");
        List<PredictInputData> inputData = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            PredictInputData inputData1 = new PredictInputData();
            inputData1.setDates((Date) input[i][0]);
            inputData1.setFlow((Double) input[i][1]);
            inputData.add(inputData1);
        }
        List<PredictInputData> resultList = ChangeDate(inputData,"月");
        Object[][] result = new Object[resultList.size()][2];
        for (int i = 0; i < result.length; i++) {
            result[i][0]=resultList.get(i).getDates();
            result[i][1]=resultList.get(i).getFlow();
        }
        ExcelTool.writeObjectExcel("D:\\头屯河历史数据1.xlsx","头屯河旬统计数据",result);
    }


    public static Object[][] getDays(Object[][] input,int year) throws ParseException {
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = sFormat.parse(year+"-01-01 00:00:00");
        Date endTime = sFormat.parse(year+"-12-31 00:00:00");
        int l = duration(startTime,endTime,"日");
        Object[][] result = new Object[l][2];
        for (int i = 0; i < l; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            int month = getSpecificDate(startTime).get("月");
            int day = getSpecificDate(startTime).get("日");
            result[i][0]=startTime;
            if (day<=10){
                result[i][1]=input[day][month];
            } else if (10<day && day<=20) {
                result[i][1]=input[day+3][month];
            }else {
                result[i][1]=input[day+6][month];
            }
            calendar.add(Calendar.DAY_OF_MONTH,1);
            startTime=calendar.getTime();
        }
        return result;
    }
}
