package dataExtraction.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;

@Service
public class DataShowService {

    public String dateType(String y,String m){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd 08:00:00");
        // 获取Calendar类的实例
        Calendar c = Calendar.getInstance();
        // 设置年份
        c.set(Calendar.YEAR, Integer.parseInt(y));
        // 设置月份，因为月份从0开始，所以用month - 1
        c.set(Calendar.MONTH, Integer.parseInt(m) -1);
        // 获取当前时间下，该月的最大日期的数字
        int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 将获取的最大日期数设置为Calendar实例的日期数
        c.set(Calendar.DAY_OF_MONTH, lastDay);
        return simpleDateFormat.format(c.getTime());
    }

    public List<String> dataTime(){
        List<String> data = new ArrayList<>();
        try {
            Calendar c = Calendar.getInstance();
            // 获取当前的年份
            int year = c.get(Calendar.YEAR);
            // 定义时间格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            // 开始日期为当前年拼接1月份
            Date startDate = sdf.parse(year + "-01");
            // 结束日期为当前年拼接12月份
            Date endDate = sdf.parse(year + "-12");
            // 设置calendar的开始日期
            c.setTime(startDate);
            // 当前时间小于等于设定的结束时间
            while(c.getTime().compareTo(endDate) <= 0){
                String times = sdf.format(c.getTime())+"-01";
                data.add(times);
                // 当前月份加1
                c.add(Calendar.MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }
    //两个时间相差多少月
    public Integer contrastTime(String timeHi,String timeNwe) throws ParseException {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date dHi = simpleDateFormat.parse(timeHi);
        Date dNew = simpleDateFormat.parse(timeNwe);
        Temporal temporal1 = LocalDate.parse(simpleDateFormat.format(dHi));
        Temporal temporal2 = LocalDate.parse(simpleDateFormat.format(dNew));
        // 方法返回为相差月份
        long l = ChronoUnit.MONTHS.between(temporal1, temporal2);
        Integer monthsDiff = Math.toIntExact(l);
        return monthsDiff;
    }

    //两个时间相差几小时
    public Integer hour(String beginTime, String endTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(endTime);
        Date monday = sdf.parse(beginTime);
        long dayM = 1000 * 24 * 60 * 60;
        long hourM = 1000 * 60 * 60;
        long differ = date.getTime() - monday.getTime();
        long hour = differ % dayM / hourM + 24 * (differ / dayM);
        return  Integer.parseInt(String.valueOf(hour));
    }

    //两个时间相差几天
    public Integer day(String beginTime,String endTime){
        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date star = dft.parse(beginTime);//开始时间
            Date endDay=dft.parse(endTime);//结束时间
            Date nextDay=star;
            int i=0;
            while(nextDay.before(endDay)){//当明天不在结束时间之前是终止循环
                Calendar cld = Calendar.getInstance();
                cld.setTime(star);
                cld.add(Calendar.DATE, 1);
                star = cld.getTime();
                //获得下一天日期字符串
                nextDay = star;
                i++;
            }
            return i;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //查询月多少天
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public Integer yearDay(String year) {
        LocalDate date = LocalDate.of(Integer.valueOf(year), 1, 1); // 创建指定日期的LocalDate对象
        int days = date.lengthOfYear(); // 获取该日期所在年份的天数
        return days;
    }

}
