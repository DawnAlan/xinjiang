package com.cj.model.func.modular.FloodPredict.Calibration.test;

import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class FlowProcessing {
    TimeUtils timeUtils =new TimeUtils();
    public void main(String[] args) throws IOException, ParseException, InvalidFormatException {
        Object[][] inputObject = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游22年流量.xlsx","Sheet2");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = "06-01";
        String end = "08-31";
        Date startTime = sdf.parse("2022-"+start+" 00:00:00");
        Date endTime = sdf.parse("2022-"+end+" 00:00:00");
        int l = timeUtils.duration(startTime,endTime,"日");
        Object[][] result = new Object[l][2];
        for (int i = 0; i < l; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            int n = 0;
            double flow = 0.0;
            for (int j = 0; j < inputObject.length; j++) {
                if (timeUtils.DateCompare(startTime,(Date) inputObject[j][1],"日")){
                    flow +=(double) inputObject[j][2];
                    n++;
                }
            }
            flow = flow/n;
            result[i][0]=startTime;
            result[i][1]=flow;
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            startTime = calendar.getTime();

        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游22年流量.xlsx","flow",result);
    }
}
