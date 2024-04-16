package com.cj.model.func.modular.FloodPredict.Calibration.test;

import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.DateCompare;
import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.duration;


public class DataProcessing {

    public static void main(String[] args) throws IOException, ParseException, InvalidFormatException {
        Object[][] inputObject = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游雨量站23年.xlsx","初始数据");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = "06-01";
        String end = "08-31";
        Date startTime = sdf.parse("2023-"+start+" 00:00:00");
        Date endTime = sdf.parse("2023-"+end+" 00:00:00");
        int l = duration(startTime,endTime,"小时");
//        逐小时
        List<Object[]> BYLC = zhuduandian(inputObject,"八一林场自动雨量站",startTime,endTime).get(0);
        List<Object[]> DN = zhuduandian(inputObject,"东南沟自动雨量站",startTime,endTime).get(0);
        List<Object[]> HG = zhuduandian(inputObject,"黑沟自动雨量站",startTime,endTime).get(0);
        List<Object[]> JPS = zhuduandian(inputObject,"加普沙自动雨量站",startTime,endTime).get(0);
        List<Object[]> KSG = zhuduandian(inputObject,"喀什沟自动雨量站",startTime,endTime).get(0);
        List<Object[]> MKG = zhuduandian(inputObject,"煤矿沟自动雨量站",startTime,endTime).get(0);
        List<Object[]> SEDW = zhuduandian(inputObject,"萨尔达万自动雨量站",startTime,endTime).get(0);
        List<Object[]> WMG = zhuduandian(inputObject,"无名沟自动雨量站",startTime,endTime).get(0);
        List<Object[]> ZED = zhuduandian(inputObject,"宰尔德自动雨量站",startTime,endTime).get(0);
        List<Object[]> ZCC = zhuduandian(inputObject,"制材厂自动雨量站",startTime,endTime).get(0);
        Object[][] result = new Object[l+1][11];
        result[0][0] = "时间";
        result[0][1] = "八一林场自动雨量站";
        result[0][2] = "东南沟自动雨量站";
        result[0][3] = "黑沟自动雨量站";
        result[0][4] = "加普沙自动雨量站";
        result[0][5] = "喀什沟自动雨量站";
        result[0][6] = "煤矿沟自动雨量站";
        result[0][7] = "萨尔达万自动雨量站";
        result[0][8] = "无名沟自动雨量站";
        result[0][9] = "宰尔德自动雨量站";
        result[0][10] = "制材厂自动雨量站";
        for (int i = 1; i < result.length; i++) {
            result[i][0] = BYLC.get(i-1)[0];
            result[i][1] = BYLC.get(i-1)[2];
            result[i][2] = DN.get(i-1)[2];
            result[i][3] = HG.get(i-1)[2];
            result[i][4] = JPS.get(i-1)[2];
            result[i][5] = KSG.get(i-1)[2];
            result[i][6] = MKG.get(i-1)[2];
            result[i][7] = SEDW.get(i-1)[2];
            result[i][8] = WMG.get(i-1)[2];
            result[i][9] = ZED.get(i-1)[2];
            result[i][10] = ZCC.get(i-1)[2];
        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游雨量站23年.xlsx",start+"~"+end+"逐小时整理数据",result);
        //逐日
        List<Object[]> BYLC1 = zhuduandian(inputObject,"八一林场自动雨量站",startTime,endTime).get(1);
        List<Object[]> DN1 = zhuduandian(inputObject,"东南沟自动雨量站",startTime,endTime).get(1);
        List<Object[]> HG1 = zhuduandian(inputObject,"黑沟自动雨量站",startTime,endTime).get(1);
        List<Object[]> JPS1 = zhuduandian(inputObject,"加普沙自动雨量站",startTime,endTime).get(1);
        List<Object[]> KSG1 = zhuduandian(inputObject,"喀什沟自动雨量站",startTime,endTime).get(1);
        List<Object[]> MKG1 = zhuduandian(inputObject,"煤矿沟自动雨量站",startTime,endTime).get(1);
        List<Object[]> SEDW1 = zhuduandian(inputObject,"萨尔达万自动雨量站",startTime,endTime).get(1);
        List<Object[]> WMG1 = zhuduandian(inputObject,"无名沟自动雨量站",startTime,endTime).get(1);
        List<Object[]> ZED1 = zhuduandian(inputObject,"宰尔德自动雨量站",startTime,endTime).get(1);
        List<Object[]> ZCC1 = zhuduandian(inputObject,"制材厂自动雨量站",startTime,endTime).get(1);
        Object[][] result1 = new Object[BYLC1.size()+1][2];
        result1[0][0] = "时间";
        result1[0][1] = "日面雨量";
        for (int i = 1; i < result1.length; i++) {
            result1[i][0] = BYLC1.get(i-1)[0];
            double by = (Double)BYLC1.get(i-1)[1];
            double dn = (Double) DN1.get(i-1)[1];
            double hg = (Double) HG1.get(i-1)[1];
            double jp = (Double) JPS1.get(i-1)[1];
            double ks = (Double) KSG1.get(i-1)[1];
            double mk = (Double) MKG1.get(i-1)[1];
            double se = (Double) SEDW1.get(i-1)[1];
            double wm = (Double) WMG1.get(i-1)[1];
            double ze = (Double) ZED1.get(i-1)[1];
            double zc = (Double) ZCC1.get(i-1)[1];
            result1[i][1] = by*0.344401+dn*0.156022+hg*0.044157+jp*0.147571+ks*0.082419+mk*0.029744+se*0.018891+wm*0.019251+ze*0.042438+zc*0.115105;
        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游雨量站23年.xlsx",start+"~"+end+"逐日整理数据",result1);

        Object[][] result3 = new Object[result1.length][11];
        result3[0][0] = "时间";
        result3[0][1] = "八一林场自动雨量站";
        result3[0][2] = "东南沟自动雨量站";
        result3[0][3] = "黑沟自动雨量站";
        result3[0][4] = "加普沙自动雨量站";
        result3[0][5] = "喀什沟自动雨量站";
        result3[0][6] = "煤矿沟自动雨量站";
        result3[0][7] = "萨尔达万自动雨量站";
        result3[0][8] = "无名沟自动雨量站";
        result3[0][9] = "宰尔德自动雨量站";
        result3[0][10] = "制材厂自动雨量站";
        for (int i = 1; i < result3.length; i++) {
            result3[i][0] = BYLC1.get(i-1)[0];
            result3[i][1] = BYLC1.get(i-1)[1];
            result3[i][2] = DN1.get(i-1)[1];
            result3[i][3] = HG1.get(i-1)[1];
            result3[i][4] = JPS1.get(i-1)[1];
            result3[i][5] = KSG1.get(i-1)[1];
            result3[i][6] = MKG1.get(i-1)[1];
            result3[i][7] = SEDW1.get(i-1)[1];
            result3[i][8] = WMG1.get(i-1)[1];
            result3[i][9] = ZED1.get(i-1)[1];
            result3[i][10] = ZCC1.get(i-1)[1];
        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游雨量站23年.xlsx",start+"~"+end+"逐日逐断面整理数据",result3);

//        转换为面雨量
        Object[][] result2 = new Object[l+1][2];
        result2[0][0] = "时间";
        result2[0][1] = "面雨量";
        for (int i = 1; i < result.length; i++) {
            result2[i][0] = BYLC.get(i-1)[0];
            double by = (Double)BYLC.get(i-1)[2];
            double dn = (Double) DN.get(i-1)[2];
            double hg = (Double) HG.get(i-1)[2];
            double jp = (Double) JPS.get(i-1)[2];
            double ks = (Double) KSG.get(i-1)[2];
            double mk = (Double) MKG.get(i-1)[2];
            double se = (Double) SEDW.get(i-1)[2];
            double wm = (Double) WMG.get(i-1)[2];
            double ze = (Double) ZED.get(i-1)[2];
            double zc = (Double) ZCC.get(i-1)[2];
            result2[i][1] = by*0.344401+dn*0.156022+hg*0.044157+jp*0.147571+ks*0.082419+mk*0.029744+se*0.018891+wm*0.019251+ze*0.042438+zc*0.115105;
        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游雨量站23年.xlsx",start+"~"+end+"面雨量整理数据",result2);
    }

    public static List<List<Object[]>> zhuduandian(Object[][] input, String location, Date startTime, Date endTime)  {
        List<List<Object[]>> result = new ArrayList<>();
        List<Object[]> resultHour = new ArrayList<>();
        List<Object[]> resultDay = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (input[i][1].equals(location)){
                resultHour.add(input[i]);
            }
        }
        resultHour = zhuxiaoshi(resultHour,startTime,endTime);
        resultDay = zhuri(resultHour,startTime,endTime);
        result.add(resultHour);
        result.add(resultDay);
        return result;
    }
    public static List<Object[]> zhuxiaoshi(List<Object[]> input, Date startTime,Date endTime){
        List<Object[]> inputList = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            Date time = (Date) input.get(i)[0];
            if (time.compareTo(startTime)==0||time.compareTo(endTime)==0||time.after(startTime)&& time.before(endTime)){
                inputList.add(input.get(i));
            }
        }
        int l = duration(startTime,endTime,"小时");
        List<Object[]> result = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTime);
            cal.add(Calendar.HOUR_OF_DAY,i);
            Date date = cal.getTime();
            Boolean judge = false;
            for (int j = 0; j < inputList.size(); j++) {
                Date list = (Date) inputList.get(j)[0];
                if (date.compareTo(list)==0){
                    judge = true;
                    result.add(inputList.get(j));
                }
            }
            if (!judge){
                Object[] a = new Object[3];
                a[0] = date;
                a[1] = inputList.get(0)[1];
                a[2] = 0.0;
                result.add(a);
            }
        }
        return result;
    }

    public static List<Object[]> zhuri(List<Object[]> input, Date startTime,Date endTime){
        int l = duration(startTime,endTime,"日");
        List<Object[]> result = new ArrayList<>();
        Date date = startTime;
        for (int i = 0; i < l; i++) {
            Object[] temp = new Object[2];
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            double sum = 0.0;
            for (int j = 0; j < input.size(); j++) {
                Boolean judge = DateCompare(date,(Date) input.get(j)[0],"日");
                if (judge){
                    sum += (Double)input.get(j)[2];
                }
            }
            temp[0] = date;
            temp[1] = sum;
            result.add(temp);
            cal.add(Calendar.DAY_OF_MONTH,1);
            date = cal.getTime();
        }
        return result;
    }
}
