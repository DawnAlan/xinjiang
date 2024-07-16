package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.model.func.modular.FloodPredict.model.entity.DateIndex;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 时间处理方法
 * @author leileilei
 *
 */
public class TimeUtils {
	//基础方法
	public DateIndex getDateIndex(Date date){

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
	public Date getDateByIndexTenDay(DateIndex index){
		Calendar calendar = Calendar.getInstance();
		int month = (index.getIndex() - 1) / 3;
		int day = ((index.getIndex() + 2) % 3) * 10 + 1;
		calendar.clear();
		calendar.set(Calendar.YEAR, index.getYear());
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar.getTime();

	}

	/**
	 * 获得预报时间序列（天、小时）
	 */
	public Date[][] getDateList(Date startDate,int len, int day, int hours){
		Date[][] dates = new Date[len][1];
		Calendar now = Calendar.getInstance();
		now.setTime(startDate);
		startDate = now.getTime();
		if(day == 10){
			DateIndex outputIndex = getDateIndex(startDate);
			for(int i = 0; i < len; i++){
				dates[i][0] = getDateByIndexTenDay(outputIndex);
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

	/**
	 * 获得融雪期预报时间序列（天、小时）
	 */
	public Date[][] getSelectDateList(Date startDate, int len, int day, int hours){
		Date[][] dates = new Date[len][1];
		Calendar now = Calendar.getInstance();
		now.setTime(startDate);
		startDate = now.getTime();
		if(day == 10){
			int month = getSpecificDate(startDate).get("月");
			DateIndex outputIndex = getDateIndex(startDate);
			if (month<=9&&month>=5){
				int judgeIndex;
				for(int i = 0; i < len; i++){
					dates[i][0] = getDateByIndexTenDay(outputIndex);
					int year = getSpecificDate(dates[i][0]).get("年");
					outputIndex = outputIndex.getNextDateIndex(36);
					judgeIndex =outputIndex.getIndex();
					if (judgeIndex==28){
						outputIndex.setYear(year+1);
						outputIndex.setIndex(13);
					}
				}
			}
			if (month<=4||month>=10){
				int judgeIndex;
				for(int i = 0; i < len; i++){
					dates[i][0] = getDateByIndexTenDay(outputIndex);
					outputIndex = outputIndex.getNextDateIndex(36);
					judgeIndex =outputIndex.getIndex();
					if (judgeIndex==13){
						outputIndex.setIndex(28);
					}
				}
			}
		}
		else{
			int month = getSpecificDate(startDate).get("月");
			if (month<=9&&month>=5){
				int judgeMonth;
				for(int i = 0; i < len; i++){
					now.setTime(startDate);
					dates[i][0]=now.getTime();
					now.add(Calendar.DAY_OF_YEAR, day);
					now.add(Calendar.HOUR_OF_DAY, hours);
					startDate=now.getTime();
					judgeMonth = getSpecificDate(startDate).get("月");
					if (judgeMonth==10){
						now.add(Calendar.MONTH,7);//后续更改，注意丰水期的时间
						int dayOfMonth = getSpecificDate(startDate).get("日");
						now.add(Calendar.DAY_OF_MONTH,-dayOfMonth+1);
						startDate = now.getTime();
					}
				}
			}
			if (month<=4||month>=10){
				int judgeMonth;
				for(int i = 0; i < len; i++){
					now.setTime(startDate);
					dates[i][0]=now.getTime();
					now.add(Calendar.DAY_OF_YEAR, day);
					now.add(Calendar.HOUR_OF_DAY, hours);
					startDate=now.getTime();
					judgeMonth = getSpecificDate(startDate).get("月");
					if (judgeMonth==5){
						now.add(Calendar.MONTH,5);//后续更改，注意丰水期的时间
						int dayOfMonth = getSpecificDate(startDate).get("日");
						now.add(Calendar.DAY_OF_MONTH,-dayOfMonth+1);
						startDate = now.getTime();
					}
				}
			}
		}
		return dates;
	}

	/**
	 * 中长期的月日期处理
	 * @param startDate 开始时间
	 * @param len 预见期的长度
	 * @return 除去基础数据日期的所有日期
	 */
	public Date[][] getMonthDateList(Date startDate,int len,int out) {
		Date[][] dates = new Date[len][out];
		int day = getSpecificDate(startDate).get("日");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_MONTH,-day+1);
		startDate=cal.getTime();
		for(int i = 0; i < len; i++){
			cal.setTime(startDate);
			for (int j = 0; j < out; j++) {
				dates[i][j]=cal.getTime();
				cal.add(Calendar.MONTH,1);
			}
			startDate = addCalendar(startDate,"月",1);
		}
		return dates;
	}

	/**
	 * 返回日期相差的数量（分：小时，日，月）
	 * 后面减去前面
	 */
	public int duration(Date dateStart,Date dateEnd,String period){
		int result = 0;
		LocalDate localDate1 = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate localDate2 = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if (period.equals("年")){
			Period duration = Period.between(localDate1, localDate2);
			result = duration.getYears();
		}
		if (period.equals("月")){
			long months = ChronoUnit.MONTHS.between(localDate1, localDate2);
			result =Math.toIntExact(months);
		}
		if (period.equals("旬")){
			long months = ChronoUnit.MONTHS.between(localDate1, localDate2);
			int month =Math.toIntExact(months);
			int day0 = getSpecificDate(dateStart).get("日");
			int xun0 = day0<=10?1:(day0<=20?2:3);
			int day1 = getSpecificDate(dateEnd).get("日");
			int xun1 = day1<=10?1:(day1<=20?2:3);
			result =  month * 3 + xun1-xun0 ;
			return result;
		}
		if (period.equals("日")){
			long duration =  Duration.between(localDate1.atStartOfDay(), localDate2.atStartOfDay()).toDays();
			result = Math.toIntExact(duration);
		}
		if (period.equals("小时")){
			LocalDateTime localDateTime1 = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			LocalDateTime localDateTime2 = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			Duration duration = Duration.between(localDateTime1, localDateTime2);
			long hours = duration.toHours();
			result = Math.toIntExact(hours);
		}
		return result;
	}



	/**
	 * 日期比较
	 * @param date1 最终返回date2=date1
	 * @param date2 最终返回date2=date1
	 * @return 如果两个日期在规定尺度上相等，则返回true
	 */
	public Boolean DateCompare(Date date1,Date date2,String period){
		boolean result = false;
		int year = getSpecificDate(date1).get("年");
		int month = getSpecificDate(date1).get("月");
		int day = getSpecificDate(date1).get("日");
		int hour = getSpecificDate(date1).get("小时");
		int year1 = getSpecificDate(date2).get("年");
		int month1 = getSpecificDate(date2).get("月");
		int day1 = getSpecificDate(date2).get("日");
		int hour1 = getSpecificDate(date2).get("小时");
		if (period.equals("小时")){
			if (year1==year & month1==month & day1==day & hour1==hour){
				result=true;
			}
		}
		if (period.equals("日")){
			if (year1==year & month1==month & day1==day ){
				result=true;
			}
		}
		if (period.equals("月")){
			if (year1==year & month1==month){
				result=true;
			}
		}
		if (period.equals("年")){
			if (year1==year ){
				result=true;
			}
		}
		return result;
	}

	/**
	 * 返回的是这个时间或者他后面离他最近的值
	 * @param timeSeries 按升序排列的时间list
	 * @param inputTime 需要寻找的时间
	 */
	public int findNearestTime(List<Date> timeSeries, Date inputTime) {
		Collections.sort(timeSeries);

		int index = Collections.binarySearch(timeSeries, inputTime);
		if (index >= 0) {
			return index; // 输入时间点恰好存在于时间序列中
		}

		index = -index - 1; // 找到输入时间点应该插入的位置

		if (index == 0) {
			return 0; // 输入时间点比时间序列中最小的时间还小
		}

		if (index == timeSeries.size()) {
			return timeSeries.size() - 1; // 输入时间点比时间序列中最大的时间还大
		}

//		Date before = timeSeries.get(index - 1);
//		Date after = timeSeries.get(index);
//		LocalDate localDate1 = before.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		LocalDate localDate2 = inputTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		LocalDate localDate3 = after.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		// 计算相差天数并返回
//		long duration =  Duration.between(localDate1.atStartOfDay(), localDate2.atStartOfDay()).toHours();
//		int n = Math.toIntExact(duration);
//		m是后面的时间与需要寻找的时间之间的差值
//		long duration2 =  Duration.between(localDate2.atStartOfDay(), localDate3.atStartOfDay()).toHours();
//		int m = Math.toIntExact(duration2);
//		if (m < n) {
//			return index;
//		} else {
//			return index-1;
//		}
		return index;
	}
	/**
	 * 获取时间区间内的Object
	 */
	public List<Object[]> getTimeIntervalList(Object[][] input,Date startTime,Date endTime){
		List<Object[]> result = new ArrayList<>();
		for (Object[] objects : input) {
			if (DateCompare((Date) objects[0], startTime, "日")) {
				result.add(objects);
			} else if (((Date) objects[0]).after(startTime) && ((Date) objects[0]).before(endTime)) {
				result.add(objects);
			} else if (DateCompare((Date) objects[0], endTime, "日")) {
				result.add(objects);
			}
		}
		return result;
	}

	/**
	 * 根据period将日尺度数据转换为相应尺度
	 */
	public List<PredictInputData> ChangeDate(List<PredictInputData> data, String period){
		List<PredictInputData>  result = new ArrayList<>();

		double flowSum = 0;
		double temperatureSum = 0;
		double rainfallSum = 0;
		int flowNum = 0;
		int temperatureNum = 0;
		int rainfallNum = 0;

		if(period.equals("旬")){
			for (int i = 0; i < data.size(); i++) {
				Date time = data.get(i).getDates();
				int day = getSpecificDate(time).get("日");
				int dayBefore = 0;
				Date time2 = new Date();
				if(i!=0){
					time2 = data.get(i-1).getDates();
					dayBefore = getSpecificDate(time2).get("日");
				}
				if(day==11||day==21||((day-dayBefore)<0)||i==data.size()-1){
					if (flowNum==0){
						flowNum=1;
					}
					if (temperatureNum==0){
						temperatureNum=1;
					}
					if (rainfallNum==0){
						rainfallNum=1;
					}
					double flowY =flowSum / flowNum;
					double temperatureY =temperatureSum / temperatureNum;
					double rainfallY =rainfallSum / rainfallNum;
					PredictInputData piece = new PredictInputData();
					piece.setDates(time2);
					piece.setFlow(flowY);
					piece.setTemperature(temperatureY);
					piece.setRainfall(rainfallY);
					result.add(piece);
					flowSum=0;
					temperatureSum=0;
					rainfallSum=0;
					flowNum=0;
					temperatureNum=0;
					rainfallNum=0;
				}
				if (data.get(i).getFlow()!=null){
					flowSum=flowSum + data.get(i).getFlow();
					flowNum=flowNum + 1;
				}
				if(data.get(i).getTemperature()!=null){
					temperatureSum=temperatureSum + data.get(i).getTemperature();
					temperatureNum=temperatureNum+1;
				}
				if (data.get(i).getRainfall()!=null){
					rainfallSum=rainfallSum + data.get(i).getRainfall();
					rainfallNum=rainfallNum+1;
				}
			}
		}
		else if(period.equals("月")){
			for (int i = 0; i < data.size(); i++) {
				Date time = data.get(i).getDates();
				int day = getSpecificDate(time).get("日");
				int dayBefore = 0;
				Date time2 = new Date();
				if(i!=0){
					time2 = data.get(i-1).getDates();
					dayBefore = getSpecificDate(time2).get("日");
				}
				if(((day-dayBefore)<0)||i==data.size()-1){
					if (flowNum==0){
						flowNum=1;
					}
					if (temperatureNum==0){
						temperatureNum=1;
					}
					if (rainfallNum==0){
						rainfallNum=1;
					}
					double flowY =flowSum / flowNum;
					double temperatureY =temperatureSum / temperatureNum;
					double rainfallY =rainfallSum / rainfallNum;
					PredictInputData piece = new PredictInputData();
					piece.setDates(time2);
					piece.setFlow(flowY);
					piece.setTemperature(temperatureY);
					piece.setRainfall(rainfallY);
					result.add(piece);
					flowSum=0;
					temperatureSum=0;
					rainfallSum=0;
					flowNum=0;
					temperatureNum=0;
					rainfallNum=0;
				}
				if (data.get(i).getFlow()!=null){
					flowSum=flowSum + data.get(i).getFlow();
					flowNum=flowNum + 1;
				}
				if(data.get(i).getTemperature()!=null){
					temperatureSum=temperatureSum + data.get(i).getTemperature();
					temperatureNum=temperatureNum+1;
				}
				if (data.get(i).getRainfall()!=null){
					rainfallSum=rainfallSum + data.get(i).getRainfall();
					rainfallNum=rainfallNum+1;
				}
			}
		}
		else if(period.equals("日")){
			for (int i = 0; i < data.size(); i++) {
				Date time = data.get(i).getDates();
				int hour = getSpecificDate(time).get("小时");
				int hourBefore = 0;
				Date time2 = new Date();
				if(i!=0){
					time2 = data.get(i-1).getDates();
					hourBefore = getSpecificDate(time2).get("小时");
				}
				if(((hour-hourBefore)<0)||i==data.size()-1){
					if (flowNum==0){
						flowNum=1;
					}
					if (temperatureNum==0){
						temperatureNum=1;
					}
					if (rainfallNum==0){
						rainfallNum=1;
					}
					double flowY =flowSum / flowNum;
					double temperatureY =temperatureSum / temperatureNum;
					double rainfallY =rainfallSum / rainfallNum;
					PredictInputData piece = new PredictInputData();
					piece.setDates(time2);
					piece.setFlow(flowY);
					piece.setTemperature(temperatureY);
					piece.setRainfall(rainfallY);
					result.add(piece);
					flowSum=0;
					temperatureSum=0;
					rainfallSum=0;
					flowNum=0;
					temperatureNum=0;
					rainfallNum=0;
				}
				if (data.get(i).getFlow()!=null){
					flowSum=flowSum + data.get(i).getFlow();
					flowNum=flowNum + 1;
				}
				if(data.get(i).getTemperature()!=null){
					temperatureSum=temperatureSum + data.get(i).getTemperature();
					temperatureNum=temperatureNum+1;
				}
				if (data.get(i).getRainfall()!=null){
					rainfallSum=rainfallSum + data.get(i).getRainfall();
					rainfallNum=rainfallNum+1;
				}
			}
		}
		//小时尺度据不做处理直接输出
		else result = data;

		return result;
	}

	/**
	 * 获得数据中的年、月、日、小时
	 */
	public Map<String, Integer> getSpecificDate(Date date){
		Map<String, Integer> result = new HashMap<>();
		int year;
		int month;
		int day;
		int hour;
		int minute;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH) + 1;
		day = cal.get(Calendar.DAY_OF_MONTH);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
		result.put("年",year);
		result.put("月",month);
		result.put("日",day);
		result.put("小时",hour);
		result.put("分钟",minute);
		return result;
	}

	/**
	 * 添加时间
	 */
	public Date addCalendar(Date startDate,String period,int l){
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		switch (period){
			case "月":
				cal.add(Calendar.MONTH,l);
				break;
			case "日":
				cal.add(Calendar.DAY_OF_MONTH,l);
				break;
			case "小时":
				cal.add(Calendar.HOUR_OF_DAY,l);
				break;
		}
		return cal.getTime();
	}

}
