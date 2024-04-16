package com.cj.model.func.modular.FloodPredict.model.test;

import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.DateCompare;
import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.duration;

public class rongXueTest {
    public static void main(String[] args) throws ParseException, IOException, InvalidFormatException {
        /**
         * 融雪径流预报效果
         */
//        ForcastInputParam param = new ForcastInputParam();
//        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date startTime = sFormat.parse("2023-06-30 00:00:00");
//        Date endTime = sFormat.parse("2023-08-01 00:00:00");
//        int l = duration(startTime,endTime,"日");
//        param.setIsSimulation(false);
//        param.setIsRealtime(true);
//        param.setIsSnowMeltModel(true);
//        param.setIsShortForecast(true);
//        param.setModel("Elman神经网络");
//        param.setLocation("楼庄子");
//        param.setPeriodStepNumber(1);
//        param.setPeriodStepSize(1);
//        param.setPeriod("日");
//        Object[][] inputData = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx", "楼庄子日");
//        Object[][] snowFlood = new Object[l][2];
//        for (int i = 0; i < l; i++) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(startTime);
//            param.setPreStartTime(startTime);
//            Object[][] machineInput = getObject(inputData,startTime);
//            Object[][] snowMeltInput= DataUtils.snowMeltDate(machineInput, param.getLocation());
//            //训练模型获得参数以及其储存路径
//            SnowMeltModel model = new SnowMeltModel();
//            param = getMachineParams(param);
//            //中长期预报预报融雪效果
//            Object[][] flood=model.snowForecast(snowMeltInput, param);
//            snowFlood[i]=flood[0];
//            calendar.add(Calendar.DAY_OF_MONTH,1);
//            startTime=calendar.getTime();
//        }
//        ExcelTool.writeObjectExcel("D:\\融雪基流预报.xlsx", "楼庄子日",snowFlood);

        List<Object[]> result = new ArrayList<>();
        Object[][] inputData = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游23年流量.xlsx", "3号桥+楼庄子");
        int n = 0;
        double flow = 0.0;
        for (int i = 0; i < inputData.length - 1; i++) {
            if (DateCompare((Date) inputData[i][1],(Date) inputData[i+1][1],"日")){
                n++;
                flow += (double) inputData[i][2];
            }else {
                Object[] empty = new Object[3];
                empty[0]=inputData[i][0];
                empty[1]=inputData[i][1];
                empty[2]=flow/n;
                n = 0;
                flow = 0.0;
                result.add(empty);
            }
        }
        Object[][] resultObject = new Object[result.size()][3];
        for (int i = 0; i < result.size(); i++) {
            resultObject[i][0]=result.get(i)[0];
            resultObject[i][1]=result.get(i)[1];
            resultObject[i][2]=result.get(i)[2];
        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游23年流量.xlsx", "日",resultObject);
    }
    public static Object[][] getObject(Object[][] input, Date date){
        int l = duration(date,(Date) input[input.length-1][0],"日");
        Object[][] result = new Object[input.length-l][4];
        for (int i = 0; i < input.length - l; i++) {
            result[i]=input[i];
        }
        return result;
    }
}
