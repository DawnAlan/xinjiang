package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.model.func.modular.FloodPredict.entity.DateIndex;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间处理方法
 * @author leileilei
 *
 */
public class TimeUtils {
	
	/**
	 * 计算开始时间和结束时间之间的相隔天数
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int getForePeriod(Date startTime,Date endTime) {
		Long spi = endTime.getTime() - startTime.getTime();
		int step = (int)(spi / (24 * 60 * 60 * 1000))+1;// 相隔天数
		return step;
	}
	
	/**以天为单位，将分段的洪水预报时间合在一起
	 * 
	 * @param times 字符串数组，记录每次洪水的起始时间
	 * @param datas 任一与场次洪水相关的雨量、流量、产流量等数据
	 * @return
	 * @throws ParseException 率定期或检验期每一天的具体数据
	 */
	public static Date[] getDays(String[][] times,double[][] datas,int warmup) throws ParseException {
		Date[][] dates = new Date[datas.length][];
		int sum =0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < datas.length; i++) {
			Date startTime = sdf.parse(times[i][0]);
			Calendar cal = Calendar.getInstance();
			cal.setTime(startTime);
			cal.add(Calendar.DAY_OF_YEAR, warmup);
			Date startTime1 = cal.getTime();
			dates[i] = getDayDateList(startTime1, datas[i].length);
			sum+=datas[i].length;
		}
		Date[] totaldates = new Date[sum];
		int len =0;
		for (int i = 0; i < dates.length; i++) {
			for (int j = 0; j < dates[i].length; j++) {
				totaldates[j+len] = dates[i][j];
			}
			len+=dates[i].length;
		}
		
		return totaldates;
	}
	
	/** 预报时段为天的日期出来
	 * 
	 * @param startDate
	 * @param len
	 * @return
	 */
	public static Date[] getDayDateList(Date startDate,int len) {
		Date[] dates = new Date[len];
		for (int i = 0; i < dates.length; i++) {
			if (i==0) {
				dates[i]=startDate;
			} else {
				 Calendar cal = Calendar.getInstance();
				 cal.setTime(dates[i-1]);
				 cal.add(Calendar.DATE,1);
				 dates[i]=cal.getTime();
			}
		}
		return dates;
	}
	
	/**中长期的月日期处理
	 * 
	 * @param startDate 开始时间（延长至基础数据）
	 * @param len 预见期的长度
	 * @param outputNum
	 * @return 从预报开始日期月初开始返回
	 */
	public static Date[][] getMonthDateList(Date startDate,int len,int outputNum) {
		Date[][] dates = new Date[len][outputNum];
		int day = DataUtils.getSpecificDate(startDate).get("日");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_MONTH,-day+1);
		startDate=cal.getTime();
		for(int i = 0; i < len; i++){
			for(int j = 0; j < outputNum; j++){
				if(i == 0){
					if(j == 0){
						cal.setTime(startDate);
						dates[i][j]=cal.getTime();
					}
				}else{

					cal.setTime(dates[i-1][j]);
					cal.add(Calendar.MONTH,1);
					dates[i][j]=cal.getTime();
				}
			}
		}
		return dates;
	}
	
	/**
	 *  得到从开始时间开始的预见期内的所有日期
	 * @param startDate  开始时间
	 * @param len 预见期的长度
	 * @param outputNum
	 * @return
	 */
	public static Date[][] getMonthDateList1(Date startDate,int len,int outputNum) {
		Date[][] dates = new Date[len][outputNum];
		for(int i = 0; i < len; i++){
			for(int j = 0; j < outputNum; j++){
				if(i == 0){
					if(j == 0){
						
						 dates[i][j]=startDate;
					}
					}else{
						 Calendar cal = Calendar.getInstance();
						 cal.setTime(dates[i-1][j]);
						 cal.add(Calendar.MONTH,1);
						 dates[i][j]=cal.getTime();
					}
			}
		}
		return dates;
	}
	
	public static Date[][] getDateList(Date startDate,int len, int day, int hours, int outptuNum){
		Date[][] dates = new Date[len][outptuNum];
		Calendar now = Calendar.getInstance();
		now.setTime(startDate);
		startDate = now.getTime();
		if(day == 10){
			DateIndex index = TimeUtils.getDateIndex(startDate);
			DateIndex outputIndex = index;
			for(int i = 0; i < len; i++){
				for(int j = 0; j < outptuNum; j++){	
					if(j == 0){
						dates[i][j] = TimeUtils.getDateByIndexTenDay(outputIndex);
					}else{
						dates[i][j] = TimeUtils.getDateByIndexTenDay(TimeUtils.getDateIndex(dates[i][j - 1]).getNextDateIndex(36));		
					}
				}
				outputIndex = outputIndex.getNextDateIndex(36);
			}

		}else{	
			for(int i = 0; i < len; i++){
				for(int j = 0; j < outptuNum; j++){
					if(i == 0){
						if(j == 0){
							dates[i][j] = startDate;
						}else{
							Calendar rightNow = Calendar.getInstance();
							rightNow.setTime(dates[i][j - 1]);
							rightNow.add(Calendar.DAY_OF_YEAR, day);
							rightNow.add(Calendar.HOUR_OF_DAY, hours);
							dates[i][j] = rightNow.getTime();
						}
					}else{
						if(j == 0){
							Calendar rightNow = Calendar.getInstance();
							rightNow.setTime(dates[i - 1][j]);
							rightNow.add(Calendar.DAY_OF_YEAR, day);
							rightNow.add(Calendar.HOUR_OF_DAY, hours);
							dates[i][j] = rightNow.getTime();
						}else{
							Calendar rightNow = Calendar.getInstance();
							rightNow.setTime(dates[i][j - 1]);
							rightNow.add(Calendar.DAY_OF_YEAR, day);
							rightNow.add(Calendar.HOUR_OF_DAY, hours);
							dates[i][j] = rightNow.getTime();
						}
					}
					
				}
			}
		}
		return dates;
	}
	

	
	
	public static Date timeFormat(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date newDate = null;
		try {
			String dateStr = sdf.format(date);
			newDate = sdf.parse(dateStr);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		return newDate;
	}
	
	public static int getHours(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		String dateStr = sdf.format(date);
		int time = Integer.parseInt(dateStr);
		
		if(time > 23 || time <= 5){
			time = 2;
		}else if(time > 5 && time <= 11){
			time = 8;
		}else if(time > 11 && time <= 17){
			time = 14;
		}else{
			time = 20;
		}
		
		return time;
	}
	
	public static int indexOFtheYear(Date date){
		
	    Calendar c = Calendar.getInstance();
		c.setTime(date);
		int index = c.get(Calendar.DAY_OF_YEAR);
	
		return index;
		
	}
	
	public static boolean isSameDate(Date date1, Date date2) {
        try {
         Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                    .get(Calendar.YEAR);
            boolean isSameMonth = isSameYear
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
            boolean isSameDate = isSameMonth
                    && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                            .get(Calendar.DAY_OF_MONTH);

            return isSameDate;
     } catch (Exception e) {
         e.printStackTrace();
     }
     return false;
    }
	
	
	public static DateIndex getDateIndex(Date date){
		
		DateIndex dateIndex = new DateIndex();
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM");
		SimpleDateFormat sdf2= new SimpleDateFormat("dd");
		String year = sdf0.format(date);
		String month = sdf1.format(date);
		String day = sdf2.format(date);
		int y = Integer.parseInt(year);
		int m = Integer.parseInt(month);
		int d = Integer.parseInt(day);
		dateIndex.setYear(y);
		if(d<=10){
			dateIndex.setIndex(3 * (m - 1) + 1);
		}else if(d <= 20){
			dateIndex.setIndex(3 * (m - 1) + 2);
		}else{
			dateIndex.setIndex(3 * (m - 1) + 3);
		}
		return dateIndex;
	}
	
	
	public static Date timestampToDate(Timestamp timestamp){
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf0.format(timestamp);
		Date date = null;
		try {
			date = sdf0.parse(time);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date getFirstDateOfYear(int year){
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date startTime = calendar.getTime();
		return startTime;
	}
	
	public static Date getDateByIndexTenDay(DateIndex index){
		Calendar calendar = Calendar.getInstance();
		int month = (index.getIndex() - 1) / 3;
		int day = ((index.getIndex() + 2) % 3) * 10 + 1;
		calendar.clear();
		calendar.set(Calendar.YEAR, index.getYear());
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar.getTime();
		
	}

}
