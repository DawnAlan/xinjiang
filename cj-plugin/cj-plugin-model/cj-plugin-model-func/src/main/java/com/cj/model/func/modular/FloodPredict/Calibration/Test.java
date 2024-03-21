package com.cj.model.func.modular.FloodPredict.model;

import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.duration;

public class test {

    public static void main(String[] args) throws IOException, ParseException, InvalidFormatException {
        Object[][] inputObject = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\雨量站整理数据.xlsx","初始数据");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = sdf.parse("2022-07-31 00:00:00");
        Date endTime = sdf.parse("2022-08-04 00:00:00");
        int l = duration(startTime,endTime,"小时");
        List<Object[]> BYLC = zhuduandian(inputObject,"八一林场自动雨量站",startTime,endTime);
        List<Object[]> DN = zhuduandian(inputObject,"东南沟自动雨量站",startTime,endTime);
        List<Object[]> HG = zhuduandian(inputObject,"黑沟自动雨量站",startTime,endTime);
        List<Object[]> JPS = zhuduandian(inputObject,"加普沙自动雨量站",startTime,endTime);
        List<Object[]> KSG = zhuduandian(inputObject,"喀什沟自动雨量站",startTime,endTime);
        List<Object[]> MKG = zhuduandian(inputObject,"煤矿沟自动雨量站",startTime,endTime);
        List<Object[]> SEDW = zhuduandian(inputObject,"萨尔达万自动雨量站",startTime,endTime);
        List<Object[]> WMG = zhuduandian(inputObject,"无名沟自动雨量站",startTime,endTime);
        List<Object[]> ZED = zhuduandian(inputObject,"宰尔德自动雨量站",startTime,endTime);
        List<Object[]> ZCC = zhuduandian(inputObject,"制材厂自动雨量站",startTime,endTime);
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
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\雨量站整理数据.xlsx","7.31~8.4逐小时整理数据",result);
        Object[][] result1 = new Object[l+1][2];
        result1[0][0] = "时间";
        result1[0][1] = "面雨量";
        for (int i = 1; i < result.length; i++) {
            result1[i][0] = BYLC.get(i-1)[0];
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
            result1[i][1] = by*0.454164+dn*0.205747+jp*0.194604+mk*0.039224+se*0.024912+wm*0.025386+ze*0.194604;
        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\雨量站整理数据.xlsx","7.31~8.4面雨量整理数据",result1);
    }

    public static List<Object[]> zhuduandian(Object[][] input, String location, Date startTime, Date endTime)  {
        List<Object[]> result = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (input[i][1].equals(location)){
                result.add(input[i]);
            }
        }
        result = zhuxiaoshi(result,startTime,endTime);
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
}
