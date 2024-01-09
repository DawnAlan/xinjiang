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
	 * @return 从预报开始日期月初开始返回相应枯水期或者丰水期的预报时间
	 */
	public static Date[][] getSelectMonthDateList(Date startDate, int len) {
		Date[][] dates = new Date[len][1];
		int day = DataUtils.getSpecificDate(startDate).get("日");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_MONTH,-day+1);
		startDate=cal.getTime();
		int month = DataUtils.getSpecificDate(startDate).get("月");
		if (month<=9&&month>=5){
			int judgeMonth = 0;
			for(int i = 0; i < len; i++){
				judgeMonth = DataUtils.getSpecificDate(startDate).get("月");
				cal.setTime(startDate);
				dates[i][0]=cal.getTime();
				cal.add(Calendar.MONTH,1);
				startDate=cal.getTime();
				if (judgeMonth==9){
					cal.add(Calendar.MONTH,7);//后续更改，注意丰水期的时间
					startDate = cal.getTime();
				}
			}
		}
		if (month<=4||month>=10){
			int judgeMonth = 0;
			for(int i = 0; i < len; i++){
				judgeMonth = DataUtils.getSpecificDate(startDate).get("月");
				cal.setTime(startDate);
				dates[i][0]=cal.getTime();
				cal.add(Calendar.MONTH,1);
				startDate=cal.getTime();
				if (judgeMonth==4){
					cal.add(Calendar.MONTH,5);//后续更改，注意枯水期的时间
					startDate = cal.getTime();
				}
			}
		}
		return dates;
	}

	
	public static Date[][] getSelectDateList(Date startDate, int len, int day, int hours){
		Date[][] dates = new Date[len][1];
		Calendar now = Calendar.getInstance();
		now.setTime(startDate);
		startDate = now.getTime();
		if(day == 10){
			int month = DataUtils.getSpecificDate(startDate).get("月");
			DateIndex outputIndex = TimeUtils.getDateIndex(startDate);
			if (month<=9&&month>=5){
				int judgeIndex = 0;
				for(int i = 0; i < len; i++){
					dates[i][0] = TimeUtils.getDateByIndexTenDay(outputIndex);
					int year = DataUtils.getSpecificDate(dates[i][0]).get("年");
					outputIndex = outputIndex.getNextDateIndex(36);
					judgeIndex =outputIndex.getIndex();
					if (judgeIndex==28){
						outputIndex.setYear(year+1);
						outputIndex.setIndex(13);
					}
				}
			}
			if (month<=4||month>=10){
				int judgeIndex = 0;
				for(int i = 0; i < len; i++){
					dates[i][0] = TimeUtils.getDateByIndexTenDay(outputIndex);
					outputIndex = outputIndex.getNextDateIndex(36);
					judgeIndex =outputIndex.getIndex();
					if (judgeIndex==13){
						outputIndex.setIndex(28);
					}
				}
			}
		}
		else{
			int month = DataUtils.getSpecificDate(startDate).get("月");
			if (month<=9&&month>=5){
				int judgeMonth = 0;
				for(int i = 0; i < len; i++){
					now.setTime(startDate);
					dates[i][0]=now.getTime();
					now.add(Calendar.DAY_OF_YEAR, day);
					now.add(Calendar.HOUR_OF_DAY, hours);
					startDate=now.getTime();
					judgeMonth = DataUtils.getSpecificDate(startDate).get("月");
					if (judgeMonth==10){
						now.add(Calendar.MONTH,7);//后续更改，注意丰水期的时间
						int dayOfMonth = DataUtils.getSpecificDate(startDate).get("日");
						now.add(Calendar.DAY_OF_MONTH,-dayOfMonth+1);
						startDate = now.getTime();
					}
				}
			}
			if (month<=4||month>=10){
				int judgeMonth = 0;
				for(int i = 0; i < len; i++){
					now.setTime(startDate);
					dates[i][0]=now.getTime();
					now.add(Calendar.DAY_OF_YEAR, day);
					now.add(Calendar.HOUR_OF_DAY, hours);
					startDate=now.getTime();
					judgeMonth = DataUtils.getSpecificDate(startDate).get("月");
					if (judgeMonth==5){
						now.add(Calendar.MONTH,5);//后续更改，注意丰水期的时间
						int dayOfMonth = DataUtils.getSpecificDate(startDate).get("日");
						now.add(Calendar.DAY_OF_MONTH,-dayOfMonth+1);
						startDate = now.getTime();
					}
				}
			}
		}
		return dates;
	}
	/**中长期的月日期处理
	 *
	 * @param startDate 开始时间（延长至基础数据）
	 * @param len 预见期的长度
	 * @return 除去基础数据日期的所有日期
	 */
	public static Date[][] getMonthDateList(Date startDate,int len) {
		Date[][] dates = new Date[len][1];
		int day = DataUtils.getSpecificDate(startDate).get("日");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_MONTH,-day+1);
		startDate=cal.getTime();
		for(int i = 0; i < len; i++){
			cal.setTime(startDate);
			dates[i][0]=cal.getTime();
			cal.add(Calendar.MONTH,1);
			startDate = cal.getTime();
		}
		return dates;
	}


	public static Date[][] getDateList(Date startDate,int len, int day, int hours){
		Date[][] dates = new Date[len][1];
		Calendar now = Calendar.getInstance();
		now.setTime(startDate);
		startDate = now.getTime();
		if(day == 10){
			DateIndex index = TimeUtils.getDateIndex(startDate);
			DateIndex outputIndex = index;
			for(int i = 0; i < len; i++){
				dates[i][0] = TimeUtils.getDateByIndexTenDay(outputIndex);
				outputIndex = outputIndex.getNextDateIndex(36);
			}

		}else{
			for(int i = 0; i < len; i++){
				if(i == 0){
					dates[i][0] = startDate;
				}else{
					Calendar rightNow = Calendar.getInstance();
					rightNow.setTime(dates[i - 1][0]);
					rightNow.add(Calendar.DAY_OF_YEAR, day);
					rightNow.add(Calendar.HOUR_OF_DAY, hours);
					dates[i][0] = rightNow.getTime();
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
