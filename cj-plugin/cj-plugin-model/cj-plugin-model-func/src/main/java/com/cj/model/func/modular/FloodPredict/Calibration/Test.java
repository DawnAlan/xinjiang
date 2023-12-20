package com.cj.model.func.modular.FloodPredict.Calibration;

import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test {
//    public static void main(String[] args) throws IOException, ParseException, InvalidFormatException {
//        Object[][] pointRain= ExcelTool.readExcel("D:\\204\\2.头屯河\\参数率定.xlsx","2022.7.10-8.4雨量");
//        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = sFormat.parse("2022-07-30 00:00:00");
//        int month = 0;
//        int day = 0;
//        int hour = 0;
//        int month2 = 0;
//        int day2 = 0;
//        int hour2 = 0;
//        Object[][] result =new Object[120][2];
//        for (int i = 0; i < 120; i++) {
//            month = DataUtils.getSpecificDate(date).get("月");
//            day = DataUtils.getSpecificDate(date).get("日");
//            hour = DataUtils.getSpecificDate(date).get("小时");
//            double rainFall = 0;
//            for (int j = 1; j < pointRain.length; j++) {
//                month2 = DataUtils.getSpecificDate((Date) pointRain[j][2]).get("月");
//                day2 = DataUtils.getSpecificDate((Date) pointRain[j][2]).get("日");
//                hour2 = DataUtils.getSpecificDate((Date) pointRain[j][2]).get("小时");
//                if (month2==month && day2==day&& hour2==hour){
//                    rainFall += (double) pointRain[j][1];
//                }
//            }
//            result[i][0]=date;
//            result[i][1]=rainFall/10;
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            calendar.add(Calendar.HOUR_OF_DAY, 1);
//            date = calendar.getTime();
//        }
//        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\面雨量.xlsx","7.30~8.3",result);
//    }
public static void main(String[] args) throws IOException, InvalidFormatException, ParseException {
    Object[][] pointRain= ExcelTool.readExcel("D:\\204\\2.头屯河\\参数率定.xlsx","温度");
    SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = sFormat.parse("2022-07-30 00:00:00");
    int hour = 0;
    int hour2 = 0;
    Object[][] result = new Object[24][2];
    double temperature = 0;
    int tempureNumber = 0;
    for (int i = 0; i < result.length; i++) {
        hour = DataUtils.getSpecificDate(date).get("小时");
        for (int j = 0; j < pointRain.length; j++) {
            hour2 = DataUtils.getSpecificDate((Date) pointRain[j][0]).get("小时");
            if (hour2==hour){
                temperature += (double) pointRain[j][1];
                tempureNumber++;
            }
        }

        result[i][0]=date;
        result[i][1]=temperature/tempureNumber;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        date = calendar.getTime();
        temperature = 0;
        tempureNumber = 0;
    }
    temperature = 0;
    for (int i = 0; i < result.length; i++) {
        temperature+=(double) result[i][1];
    }
    for (int i = 0; i < result.length; i++) {
        result[i][1]=(double)result[i][1]/temperature;
    }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\温度比值.xlsx","天内温度分配",result);
}
}
