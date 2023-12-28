package com.cj.model.func.modular.FloodPredict.Calibration;

import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParamNew;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.entity.RainFallDto;
import com.cj.model.func.modular.FloodPredict.model.PhysicalForcast;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.cj.model.func.modular.FloodPredict.model.PhysicalForcast.floodLevel;
import static com.cj.model.func.modular.FloodPredict.model.TouTunHe.rainPredict;
import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.emptyProcessing;
import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.preRainHourToDay;

public class Test {


    public static void main(String[] args) throws ParseException, IOException, InvalidFormatException {
        //模型参数输入设置
        ForcastInputParamNew paramForcastInputParamNew = new ForcastInputParamNew();
        paramForcastInputParamNew.setModelType(3);//3为场次
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        paramForcastInputParamNew.setPredictionTime(sFormat.parse("2023-12-31 19:00:00"));
        Date startDate = paramForcastInputParamNew.getPredictionTime();
        Date[][] dates;
        dates = TimeUtils.getMonthDateList(startDate,10, 1);
        int a =0;
    }
}


//public class Test {
//        public static void main(String[] args) throws IOException, ParseException, InvalidFormatException {
//        Object[][] pointRain= ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\雨量站.xlsx","LZZ_RAINFALL_STATION_2023121317");
//        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = sFormat.parse("2022-07-30 00:00:00");
//        int month = 0;
//        int day = 0;
//        int hour = 0;
//        int month2 = 0;
//        int day2 = 0;
//        int hour2 = 0;
//        Object[][] result =new Object[290][11];
//        result[0][0]="时间";
//        result[0][1]="八一林场自动雨量站";
//        result[0][2]="加普沙自动雨量站";
//        result[0][3]="东南沟自动雨量站";
//        result[0][4]="宰尔德自动雨量站";
//        result[0][5]="无名沟自动雨量站";
//        result[0][6]="煤矿沟自动雨量站";
//        result[0][7]="萨尔达万自动雨量站";
//        result[0][8]="喀什沟自动雨量站";
//        result[0][9]="黑沟自动雨量站";
//        result[0][10]="制材厂自动雨量站";
//        for (int i = 1; i < 290; i++) {
//            month = DataUtils.getSpecificDate(date).get("月");
//            day = DataUtils.getSpecificDate(date).get("日");
//            hour = DataUtils.getSpecificDate(date).get("小时");
//            double rainFall = 0;
//            for (int j = 1; j < pointRain.length; j++) {
//                month2 = DataUtils.getSpecificDate((Date) pointRain[j][3]).get("月");
//                day2 = DataUtils.getSpecificDate((Date) pointRain[j][3]).get("日");
//                hour2 = DataUtils.getSpecificDate((Date) pointRain[j][3]).get("小时");
//                if (month2==month && day2==day&& hour2==hour){
//                    if(pointRain[j][1].equals("八一林场自动雨量站")){
//                        System.out.println("言");
//                        result[i][1]=pointRain[j][2];
//                    }
//                    if(pointRain[j][1].equals("加普沙自动雨量站")){
//                        System.out.println("念");
//                        result[i][2]=pointRain[j][2];
//                    }
//                    if(pointRain[j][1].equals("东南沟自动雨量站")){
//                        System.out.println("君");
//                        result[i][3]=pointRain[j][2];
//                    }
//                    if(pointRain[j][1].equals("宰尔德自动雨量站")){
//                        System.out.println("子");
//                        result[i][4]=pointRain[j][2];
//                    }
//                    if(pointRain[j][1].equals("无名沟自动雨量站")){
//                        System.out.println("，");
//                        result[i][5]=pointRain[j][2];
//                    }
//                    if(pointRain[j][1].equals("萨尔达万自动雨量站")){
//                        System.out.println("其");
//                        result[i][7]=pointRain[j][2];
//                    }
//                    if(pointRain[j][1].equals("煤矿沟自动雨量站")){
//                        System.out.println("如");
//                        result[i][6]=pointRain[j][2];
//                    }
//                    if(pointRain[j][1].equals("喀什沟自动雨量站")){
//                        System.out.println("温");
//                        result[i][8]=pointRain[j][2];
//                    }
//                    if(pointRain[j][1].equals("黑沟自动雨量站")){
//                        System.out.println("玉");
//                        result[i][9]=pointRain[j][2];
//                    }
//                    if(pointRain[j][1].equals("制材厂自动雨量站")){
//                        System.out.println("。");
//                        result[i][10]=pointRain[j][2];
//                    }
//                }
//            }
//            result[i][0]=date;
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            calendar.add(Calendar.HOUR_OF_DAY, 1);
//            date = calendar.getTime();
//        }
//            for (int i = 0; i <result.length ; i++) {
//                for (int j = 0; j <result[0].length ; j++) {
//                    if (result[i][j]==null){
//                        result[i][j]=0;
//                    }
//                }
//            }
//        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\小时点雨量.xlsx","7.30~8.10",result);
//    }
//    /**
//     * 点雨量转为面雨量
//     * @param args
//     * @throws IOException
//     * @throws ParseException
//     * @throws InvalidFormatException
//     */
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
    /**
     * 天内温度分配
     */
//public static void main(String[] args) throws IOException, InvalidFormatException, ParseException {
//    Object[][] pointRain= ExcelTool.readExcel("D:\\204\\2.头屯河\\参数率定.xlsx","温度");
//    SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    Date date = sFormat.parse("2022-07-30 00:00:00");
//    int hour = 0;
//    int hour2 = 0;
//    Object[][] result = new Object[24][2];
//    double temperature = 0;
//    int tempureNumber = 0;
//    for (int i = 0; i < result.length; i++) {
//        hour = DataUtils.getSpecificDate(date).get("小时");
//        for (int j = 0; j < pointRain.length; j++) {
//            hour2 = DataUtils.getSpecificDate((Date) pointRain[j][0]).get("小时");
//            if (hour2==hour){
//                temperature += (double) pointRain[j][1];
//                tempureNumber++;
//            }
//        }
//
//        result[i][0]=date;
//        result[i][1]=temperature/tempureNumber;
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.add(Calendar.HOUR_OF_DAY, 1);
//        date = calendar.getTime();
//        temperature = 0;
//        tempureNumber = 0;
//    }
//    temperature = 0;
//    for (int i = 0; i < result.length; i++) {
//        temperature+=(double) result[i][1];
//    }
//    for (int i = 0; i < result.length; i++) {
//        result[i][1]=(double)result[i][1]/temperature;
//    }
//    ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\温度比值.xlsx","天内温度分配",result);
//}
//}



