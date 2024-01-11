package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.model.func.modular.FloodPredict.entity.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.getDateList;
import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.getMonthDateList;

/**
 * 数据处理方法
 * 
 * @author leileilei
 *
 */
public class DataUtils {

	public static String array2String(int[] array) {
		String str = "";
		for (int i = 0; i < array.length; i++) {
			if (i == array.length - 1) {
				str += array[i];
			} else {
				str += array[i] + ",";
			}
		}
		return str;
	}

	/**
	 * 将输入数据转化为制定的格式
	 * @param entity 楼庄子上游数据
	 * @return
	 * result.get(0)三号桥日尺度站点+时间+径流
	 * result.get(1)楼庄子入库日尺度站点+时间+径流
	 * result.get(2)楼庄子出库日尺度站点+时间+径流
	 * 后续更改（目前返回23年至今流量数据）
	 * result.get(3)喀什沟雨量站小时尺度站点+时间+降水+温度
	 * result.get(4)黑沟雨量站小时尺度站点+时间+降水+温度
	 * result.get(5)煤矿沟雨量站小时尺度站点+时间+降水+温度
	 * result.get(6)无名沟雨量站小时尺度站点+时间+降水+温度
	 * result.get(7)加普沙雨量站小时尺度站点+时间+降水+温度
	 * result.get(8)宰尔德雨量站小时尺度站点+时间+降水+温度
	 * result.get(9)东南沟雨量站小时尺度站点+时间+降水+温度
	 * result.get(10)八一林场雨量站小时尺度站点+时间+降水+温度
	 * result.get(11)萨尔达万雨量站小时尺度站点+时间+降水+温度
	 * result.get(12)制材厂雨量站小时尺度站点+时间+降水+温度
	 */
	public static List<List<PredictInputData>> lzzDataConversion(ForcastInputParamNew entity) throws ParseException, IOException, InvalidFormatException {
		List<List<PredictInputData>> result = new ArrayList<>();
		Date date = entity.getPredictionTime();
		//3号桥
		List<LzzGaugingStation> THS = entity.getLzzHydrologyParam().getThreeGaugingStation();
		List<PredictInputData> Three = lzzFlowConversion(date,THS);
		result.add(Three);
		//楼庄子入库
		List<LzzGaugingStation> LZZI = entity.getLzzHydrologyParam().getLzzInput();
		List<PredictInputData> LOUIN = lzzFlowConversion(date,LZZI);
		result.add(LOUIN);
		//楼庄子出库
		List<LzzGaugingStation> LZZO = entity.getLzzHydrologyParam().getLzzOutput();
		List<PredictInputData> LOUOUT = lzzFlowConversion(date,LZZO);
		result.add(LOUOUT);
		if (entity.getModelType()==3){
			//喀什沟雨量站
			List<LzzRainfallStation> KSG = entity.getLzzHydrologyParam().getKsgRainfallStation();
			List<PredictInputData> KASHI = lzzRainConversion(KSG);
			result.add(KASHI);
			//黑沟雨量站
			List<LzzRainfallStation> HG = entity.getLzzHydrologyParam().getHgRainfallStation();
			List<PredictInputData> HEIGOU = lzzRainConversion(HG);
			result.add(HEIGOU);
			//煤矿沟雨量站
			List<LzzRainfallStation> MKG = entity.getLzzHydrologyParam().getMkgRainfallStation();
			List<PredictInputData> MEI = lzzRainConversion(MKG);
			result.add(MEI);
			//无名沟雨量站
			List<LzzRainfallStation> WMG = entity.getLzzHydrologyParam().getWmgRainfallStation();
			List<PredictInputData> WUMING = lzzRainConversion(WMG);
			result.add(WUMING);
			//加普沙雨量站
			List<LzzRainfallStation> JPS = entity.getLzzHydrologyParam().getJpsRainfallStation();
			List<PredictInputData> JIA = lzzRainConversion(JPS);
			result.add(JIA);
			//宰尔德雨量站
			List<LzzRainfallStation> ZED = entity.getLzzHydrologyParam().getZrdRainfallStation();
			List<PredictInputData> ZAI = lzzRainConversion(ZED);
			result.add(ZAI);
			//东南沟雨量站
			List<LzzRainfallStation> DNG = entity.getLzzHydrologyParam().getDngRainfallStation();
			List<PredictInputData> DONG = lzzRainConversion(DNG);
			result.add(DONG);
			//八一林场雨量站
			List<LzzRainfallStation> BYLC = entity.getLzzHydrologyParam().getBylcRainfallStation();
			List<PredictInputData> BAYI = lzzRainConversion(BYLC);
			result.add(BAYI);
			//萨尔达万雨量站
			List<LzzRainfallStation> SEDW = entity.getLzzHydrologyParam().getSedwRainfallStation();
			List<PredictInputData> SAER = lzzRainConversion(SEDW);
			result.add(SAER);
			//制材厂雨量站
			List<LzzRainfallStation> ZCC = entity.getLzzHydrologyParam().getZccRainfallStation();
			List<PredictInputData> ZHI = lzzRainConversion(ZCC);
			result.add(ZHI);
		}
		return result;
	}

	/**
	 * 楼庄子上游水位站数据转化
	 * @param input
	 * @return 站点名称、日尺度时间、流量
	 * 后续更改（选择时间为2023年及以后则返回每一天的值，23年以前之间返回原始数据不做其他处理）
	 */
	public static List<PredictInputData> lzzFlowConversion(Date dateEnd,List<LzzGaugingStation> input) throws ParseException, IOException, InvalidFormatException {
		List<PredictInputData> result = new ArrayList<>();
		double flowSum = 0;
		int flowNum = 0;
		int yearEnd = getSpecificDate(dateEnd).get("年");
		for (int i = 0; i < input.size(); i++) {
			String id = input.get(i).getId();
			// 使用间隔符提取数字部分
			String[] parts = id.split(":");
			String bridgeNumber = parts[0];
			long numericValue= Long.parseLong(parts[1]);
			Date date = new Date(numericValue); // 根据时间戳创建日期对象
			int year = getSpecificDate(date).get("年");
			int day =getSpecificDate(date).get("日");
			int hour = getSpecificDate(date).get("小时");
			int hourBefore = 0;
			int dayBefore = day;
			if (yearEnd>=2023)//预报时间为23年以后则只读取数据库中23年后的数据
			{
				if (year >= 2023){
					if(i!=0){
						String id1 = input.get(i-1).getId();
						// 使用间隔符提取数字部分
						String[] parts1 = id1.split(":");
						long numericValue1= Long.parseLong(parts1[1]);
						Date date1 = new Date(numericValue1);
						hourBefore = getSpecificDate(date1).get("小时");
						dayBefore = getSpecificDate(date1).get("日");
					}
					if(((hour-hourBefore)<0)||day!=dayBefore){
						if (flowNum==0){
							flowNum=1;
						}
						double flowY =flowSum / flowNum;
						PredictInputData piece = new PredictInputData();
						piece.setRainStation(bridgeNumber);
						piece.setDates(date);
						piece.setFlow(flowY);
						result.add(piece);
						flowSum=0;
						flowNum=0;

					}
					if (input.get(i).getFlow()!=null){
						flowSum = flowSum + input.get(i).getFlow();
						flowNum = flowNum + 1;
					}
				}
			}
			else //预报时间为23年之前则读取历史数据
			{
					if(i!=0){
						String id1 = input.get(i-1).getId();
						// 使用间隔符提取数字部分
						String[] parts1 = id1.split(":");
						long numericValue1= Long.parseLong(parts1[1]);
						Date date1 = new Date(numericValue1);
						hourBefore = getSpecificDate(date1).get("小时");
						dayBefore = getSpecificDate(date1).get("日");
					}
					if(((hour-hourBefore)<0)||day!=dayBefore){
						if (flowNum==0){
							flowNum=1;
						}
						double flowY =flowSum / flowNum;
						PredictInputData piece = new PredictInputData();
						piece.setRainStation(bridgeNumber);
						piece.setDates(date);
						piece.setFlow(flowY);
						result.add(piece);
						flowSum=0;
						flowNum=0;

					}
					if (input.get(i).getFlow()!=null){
						flowSum = flowSum + input.get(i).getFlow();
						flowNum = flowNum + 1;
					}
			}
		}
		/**
		 * 2023年至今每一天都赋值，保证数据连续性
		 */
		List<PredictInputData> resultEnd = new ArrayList<>();
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateStart = sFormat.parse("2023-01-01 00:00:00");
		if (yearEnd >= 2023){
			// 计算相差天数并返回
			int n =duration(dateStart,dateEnd,"日");
			for (int i = 0; i < n; i++) {
				PredictInputData data = new PredictInputData();
				for (int j = 0; j < result.size(); j++) {
					Date date = result.get(j).getDates();
					Boolean dateCompare = DateCompare(date,dateStart,"日");
					if (dateCompare){
						data=result.get(j);
					}else {
						data.setRainStation(result.get(0).getRainStation());
						data.setDates(dateStart);
					}
				}
				// 将 Calendar 的日期加一天
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dateStart);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				dateStart = calendar.getTime();
				resultEnd.add(data);
			}
			//为空日期赋值，赋值为下一个有值的flow
            for (PredictInputData predictInputData : resultEnd) {
                if (predictInputData.getFlow() == null) {
                    predictInputData.setFlow(0.0);
                }
            }
			//对0值流量进行赋值
			resultEnd=lzzFlowError(resultEnd);
			return resultEnd;
		}
		else {
			return result;
		}
	}

	/**
	 * 后续更改
	 * 对于流量为0或其他的异常值进行处理
	 * @param input
	 * @return
	 */
	public static List<PredictInputData> lzzFlowError(List<PredictInputData> input){
		List<PredictInputData> result = new ArrayList<>();
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).getFlow()==0){
				Date date=input.get(i).getDates();
				int month = getSpecificDate(date).get("月");
				switch (month){
					case 1:input.get(i).setFlow(1.29);
						break;
					case 2:input.get(i).setFlow(1.19);
						break;
					case 3:input.get(i).setFlow(1.77);
						break;
					case 4:input.get(i).setFlow(2.78);
						break;
					case 5:input.get(i).setFlow(8.13);
						break;
					case 6:input.get(i).setFlow(17.92);
						break;
					case 7:input.get(i).setFlow(22.05);
						break;
					case 8:input.get(i).setFlow(16.13);
						break;
					case 9:input.get(i).setFlow(7.84);
						break;
					case 10:input.get(i).setFlow(3.85);
						break;
					case 11:input.get(i).setFlow(2.23);
						break;
					case 12:input.get(i).setFlow(1.52);
						break;
				}
			}
			//去除枯水月份过大流量
			Date date=input.get(i).getDates();
			int month = getSpecificDate(date).get("月");
			if (month<=4||month>=9){
				if (input.get(i).getFlow()>=10){
					switch (month){
						case 1:input.get(i).setFlow(1.29);
							break;
						case 2:input.get(i).setFlow(1.19);
							break;
						case 3:input.get(i).setFlow(1.77);
							break;
						case 4:input.get(i).setFlow(2.78);
							break;
						case 5:input.get(i).setFlow(8.13);
							break;
						case 6:input.get(i).setFlow(17.92);
							break;
						case 7:input.get(i).setFlow(22.05);
							break;
						case 8:input.get(i).setFlow(16.13);
							break;
						case 9:input.get(i).setFlow(7.84);
							break;
						case 10:input.get(i).setFlow(3.85);
							break;
						case 11:input.get(i).setFlow(2.23);
							break;
						case 12:input.get(i).setFlow(1.52);
							break;
					}
				}
			}
		}
		result=input;
		return result;
	}

	/**
	 * 楼庄子上游雨量站小时尺度转日尺度
	 * @param input
	 * @return 站点名称、日尺度时间、降水、温度
	 */
	public static List<PredictInputData> lzzRainHourToDay(List<LzzRainfallStation> input){
		List<PredictInputData> result = new ArrayList<>();
		BigDecimal temperatureSum =BigDecimal.valueOf(0);
		BigDecimal rainfallSum =BigDecimal.valueOf(0);
		BigDecimal temperatureNum=BigDecimal.valueOf(0);
		BigDecimal rainfallNum=BigDecimal.valueOf(0);
		for (int i = 0; i < input.size(); i++) {
			String id = input.get(i).getId();
			// 使用间隔符提取数字部分
			String[] parts = id.split(":");
			String bridgeNumber = parts[0];
			long numericValue= Long.parseLong(parts[1]);
			Date date = new Date(numericValue); // 根据时间戳创建日期对象
			int hour = getSpecificDate(date).get("小时");
			int day = getSpecificDate(date).get("日");
			int hourBefore = 0;
			int day1 = day;
			if(i!=0){
				String id1 = input.get(i-1).getId();
				// 使用间隔符提取数字部分
				String[] parts1 = id1.split(":");
				long numericValue1= Long.parseLong(parts1[1]);
				Date date1 = new Date(numericValue1);
				hourBefore = getSpecificDate(date1).get("小时");
				day1 = getSpecificDate(date1).get("日");
			}
			if(((hour-hourBefore)<0||day!=day1)){
				BigDecimal temperatureY = BigDecimal.valueOf(temperatureSum.doubleValue()/(temperatureNum.doubleValue()));
				BigDecimal rainfallY = BigDecimal.valueOf(rainfallSum.doubleValue()/(rainfallNum.doubleValue()));
				PredictInputData piece = new PredictInputData();
				piece.setRainStation(bridgeNumber);
				piece.setDates(date);
				piece.setTemperature(temperatureY.doubleValue());
				piece.setRainfall(rainfallY.doubleValue());
				result.add(piece);

				temperatureSum=BigDecimal.valueOf(0);
				rainfallSum=BigDecimal.valueOf(0);
				temperatureNum=BigDecimal.valueOf(0);
				rainfallNum=BigDecimal.valueOf(0);

			}
			if(input.get(i).getTemperature()!=null){
				temperatureSum=temperatureSum.add(input.get(i).getTemperature());
				temperatureNum=temperatureNum.add(BigDecimal.valueOf(1));
			}
			if (input.get(i).getRainfall()!=null){
				rainfallSum=rainfallSum.add(input.get(i).getRainfall());
				rainfallNum=rainfallNum.add(BigDecimal.valueOf(1));
			}
		}
		return result;
	}

	/**
	 * 预报降水小时转日
	 * @param input
	 * @return
	 */
	public static List<PredictInputData> preRainHourToDay(List<RainFallDto> input){
		List<PredictInputData> result = new ArrayList<>();
		double rainfallSum =0.0;
		int rainfallNum=0;
		for (int i = 0; i < input.size(); i++) {
			Date date = stringToDate(input.get(i).getDate()); // 小时尺度时间
			int hour = getSpecificDate(date).get("小时");
			int day = getSpecificDate(date).get("日");
			int hourBefore = 0;
			String station1 = input.get(0).getArea();
			int day1 = day;
			if(i!=0){
				Date date1 = stringToDate(input.get(i-1).getDate());
				hourBefore = getSpecificDate(date1).get("小时");
				day1 = getSpecificDate(date1).get("日");
				station1= input.get(i-1).getArea();
			}
			String station = input.get(i).getArea();
			if(((hour-hourBefore)<0||day!=day1)&&station.equals(station1)){

				double rainfallY = (rainfallSum/rainfallNum);
				PredictInputData piece = new PredictInputData();
				piece.setRainStation(input.get(i).getArea());
				piece.setDates(date);
				piece.setTemperature(12.0);
				piece.setRainfall(rainfallY);
				result.add(piece);

				rainfallSum=0.0;
				rainfallNum=0;

			}
			if (input.get(i).getRainFall()!=null){
				rainfallSum=rainfallSum+input.get(i).getRainFall();
				rainfallNum=rainfallNum+1;
			}
		}
		return result;
	}

	/**
	 * 将雨量站输入转化为模型需要的类型，没有去除空值
	 * (后续更改）目前返回21年至今所有小时的数据
	 * @param input
	 * @return 小时尺度站点名、时间、雨量、温度
	 */
	public static List<PredictInputData> lzzRainConversion(List<LzzRainfallStation> input){
		List<PredictInputData> resultMid = new ArrayList<>();
		for (int i = 0; i < input.size(); i++) {
			String id = input.get(i).getId();
			// 使用间隔符提取数字部分
			String[] parts = id.split(":");
			String bridgeNumber = parts[0];
			long numericValue= Long.parseLong(parts[1]);
			Date date = new Date(numericValue); // 根据时间戳创建日期对象
			//储存相应数据
			PredictInputData piece = new PredictInputData();
			piece.setRainStation(bridgeNumber);
			piece.setDates(date);
			piece.setRainfall(input.get(i).getRainfall().doubleValue());
			piece.setTemperature(input.get(i).getTemperature().doubleValue());
			resultMid.add(piece);
		}
		return resultMid;
	}

	/**
	 *
	 * @param entity
	 * @return
	 * result.get(0)头屯河入库日尺度站点+时间+径流
	 *result.get(1)小渠子站小时尺度站点+时间+降雨
	 *result.get(2)团结一队小时尺度站点+时间+降雨
	 *result.get(3)头屯河水库雨量站小时尺度站点+时间+降水
	 */
	public static List<List<PredictInputData>> irrigatedDataConversion(IrrigatedHydrologyParam entity){
		List<List<PredictInputData>> result = new ArrayList<>();
		//头屯河入库流量
		List<IrrigatedPlatformDataInfo> TTHI = entity.getTthInput();
		List<PredictInputData> TOIN = IrrigateFlowConversion(TTHI);
		result.add(TOIN);
		//小渠子雨量站
		List<IrrigatedPlatformDataInfo> XQZ = entity.getXqzGaugingStation();
		List<PredictInputData> XIAO = IrrigateRainConversion(XQZ);
		result.add(XIAO);
		//团结一队雨量站
		List<IrrigatedPlatformDataInfo> TJYD = entity.getTjydGaugingStation();
		List<PredictInputData> TUANJIE = IrrigateRainConversion(TJYD);
		result.add(TUANJIE);
		//头屯河水库雨量站
		List<IrrigatedPlatformDataInfo> TTHR = entity.getTthGaugingStation();
		List<PredictInputData> TORAIN = IrrigateRainConversion(TTHR);
		result.add(TORAIN);
		return result;
	}

	/**
	 * 头屯河入库流量数据转化
	 * @param input
	 * @return 返回日尺度的站点、时间、流量
	 */
	public static List<PredictInputData> IrrigateFlowConversion(List<IrrigatedPlatformDataInfo> input){
		List<PredictInputData> result = new ArrayList<>();

		for (int i = 0; i < input.size(); i++) {
			String id = input.get(i).getId();
			// 使用间隔符提取数字部分
			String[] parts = id.split("-");
			String bridgeNumber = parts[0];
			long numericValue= Long.parseLong(parts[1]);
			Date date = new Date(numericValue); // 根据时间戳创建日期对象
			int day = getSpecificDate(date).get("日");
			int dayBefore = 0;
			if(i!=0){
				String id1 = input.get(i-1).getId();
				// 使用间隔符提取数字部分
				String[] parts1 = id1.split("-");
				long numericValue1= Long.parseLong(parts1[1]);
				Date date1 = new Date(numericValue1);
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(date1);
				dayBefore = cal1.get(Calendar.DAY_OF_MONTH);
			}
			if((day!=dayBefore)){
				PredictInputData piece = new PredictInputData();
				piece.setRainStation(bridgeNumber);
				if (input.get(i).getYesterdayAvgFlow()!=null){
					piece.setDates(date);
					piece.setFlow(input.get(i).getYesterdayAvgFlow());
					result.add(piece);
				}
			}
		}
		return result;
	}

	/**
	 * 区间雨量站数据转化为日尺度
	 * @param input
	 * @return 雨量站日尺度站点、时间、降水
	 */
	public static List<PredictInputData> IrrigateRainHourToDay(List<IrrigatedPlatformDataInfo> input){
		List<PredictInputData> result = new ArrayList<>();
		double rainfallSum = 0.0;
		int rainfallNum=0;
		for (int i = 0; i < input.size(); i++) {
			String id = input.get(i).getId();
			// 使用间隔符提取数字部分
			String[] parts = id.split("-");
			String bridgeNumber = parts[0];
			long numericValue= Long.parseLong(parts[1]);
			Date date = new Date(numericValue); // 根据时间戳创建日期对象
			int hour = getSpecificDate(date).get("小时");
			int day = getSpecificDate(date).get("日");
			int hourBefore = 0;
			int dayBefore = day;
			if(i!=0){
				String id1 = input.get(i-1).getId();
				// 使用间隔符提取数字部分
				String[] parts1 = id1.split("-");
				long numericValue1= Long.parseLong(parts1[1]);
				Date date1 = new Date(numericValue1);
				hourBefore = getSpecificDate(date1).get("小时");
				dayBefore = getSpecificDate(date1).get("日");
			}
			if(((hour-hourBefore)<0)||day!=dayBefore){
				double rainfallY =rainfallSum/rainfallNum;
				PredictInputData piece = new PredictInputData();
				piece.setRainStation(bridgeNumber);
				piece.setDates(date);
				piece.setRainfall(rainfallY);
				result.add(piece);
				rainfallSum=0.0;
				rainfallNum=0;

			}
			if (input.get(i).getQxRainFall()!=null){
				rainfallSum=rainfallSum+input.get(i).getQxRainFall();
				rainfallNum=rainfallNum+1;
			}
		}
		return result;
	}

	/**
	 * 区间雨量站数据转化，没有去除空值
	 * @param input
	 * @return 小时尺度的站点名、时间、雨量
	 */
	public static List<PredictInputData> IrrigateRainConversion(List<IrrigatedPlatformDataInfo> input){
		List<PredictInputData> result = new ArrayList<>();

		for (int i = 0; i < input.size(); i++) {
			String id = input.get(i).getId();
			// 使用间隔符提取数字部分
			String[] parts = id.split("-");
			String bridgeNumber = parts[0];
			long numericValue= Long.parseLong(parts[1]);
			Date date = new Date(numericValue); // 根据时间戳创建日期对象
			int hour = getSpecificDate(date).get("小时");
			int hourBefore = 0;
			if(i!=0){
				String id1 = input.get(i-1).getId();
				// 使用间隔符提取数字部分
				String[] parts1 = id1.split("-");
				long numericValue1= Long.parseLong(parts1[1]);
				Date date1 = new Date(numericValue1);
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(date1);
				hourBefore = cal1.get(Calendar.HOUR_OF_DAY);
			}
			if((hour!=hourBefore)){
				PredictInputData piece = new PredictInputData();
				piece.setRainStation(bridgeNumber);
				piece.setDates(date);
				piece.setRainfall(input.get(i).getYqRainFallOne());
				result.add(piece);
			}
		}
		return result;
	}

	/**
	 *后续更改（目前只能实现前期的模拟，后续需要导入预报的雨量）
	 *雨量信息，包括了前10小时落地雨和后期预报雨量
	 * @return
	 */
	public static List<PredictInputData> hoursRain(ForcastInputParamNew param,List<PredictInputData> input) throws ParseException {
		List<PredictInputData> result = new ArrayList<>();
		PredictInputData data = new PredictInputData();

		//获得开始时间和结束时间，分情况判断
		Date dateStart = param.getPredictionTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateStart);
		calendar.add(Calendar.HOUR_OF_DAY, -10);
		dateStart = calendar.getTime();//找到落地雨前十小时
		int n = 10 + param.getPeriodTimeNum()* param.getPeriodTimeStep();//需要预报的时间长度
		calendar.add(Calendar.HOUR_OF_DAY, n);
		Date dataEnd = calendar.getTime();//预报结束时间

		//找到最贴近的时间
		List<Date> dateList = new ArrayList<>();
		for (PredictInputData predictInputData : input) {
			dateList.add(predictInputData.getDates());
		}
		int d = findNearestTime(dateList,dateStart);
		Date dateFind = input.get(d).getDates();

		int end_inputEnd = duration(dataEnd,input.get(input.size()-1).getDates(),"小时");
		if (end_inputEnd > 0)//预报结束时间在数据库中有，也就是全部读取历史数据
		{
			//此时的dateFind是历史数据中与开始预报时间最接近的
			for (int i = 0; i < n; i++) {
				for (int j = 0; d + j < input.size() && j < n; j++) {
					Boolean dateCompare = DateCompare(dateStart,input.get(d+j).getDates(),"小时");
					if (dateCompare){
						data=input.get(d+j);
						break;
					}else {
						data = new PredictInputData();
						data.setDates(dateStart);
						data.setTemperature(12.0);
						data.setRainStation(input.get(0).getRainStation());
						data.setRainfall(0.0);
					}
				}
				calendar.setTime(dateStart);
				calendar.add(Calendar.HOUR_OF_DAY, 1);
				dateStart = calendar.getTime();
				result.add(data);
			}
		}else //预报时间在数据库中没有，也就是需要读取预报雨量
		{
			int start_inputEnd = duration(dateStart,input.get(input.size()-1).getDates(),"小时");
			int length = param.getRainFallDtos().size();
			List<RainFallDto> rainPre =param.getRainFallDtos();
			//后续更改预报时间的类型
			String station = input.get(0).getRainStation();
			if (start_inputEnd < 0)//预报开始时间在数据库中没有，也就是全部读取预报值
			{
				for (int i = 0; i < n; i++) {
					if (length>0)//有预报值
					{
						for (int j = 0; j < length; j++) {
							Date date =stringToDate(rainPre.get(j).getDate());
							Boolean dateCompare = DateCompare(dateStart,date,"小时");
							if (dateCompare && station.equals(rainPre.get(j).getArea()))//日期相等并且地点相等才能赋值
							{
								data = new PredictInputData();
								data.setDates(dateStart);
								data.setTemperature(12.0);
								data.setRainStation(input.get(0).getRainStation());
								data.setRainfall(rainPre.get(j).getRainFall());
								break;
							}else {
								data = new PredictInputData();
								data.setDates(dateStart);
								data.setTemperature(12.0);
								data.setRainStation(input.get(0).getRainStation());
								data.setRainfall(0.0);
							}
						}
					}else //没有预报值，数据库中也没有数据
					{
						data = new PredictInputData();
						data.setDates(dateStart);
						data.setTemperature(12.0);
						data.setRainStation(input.get(0).getRainStation());
						data.setRainfall(0.0);
					}

					calendar.setTime(dateStart);
					calendar.add(Calendar.HOUR_OF_DAY, 1);
					dateStart = calendar.getTime();
					result.add(data);
				}
			}else //预报开始时间在数据库内，预报结束时间不在数据库内
			{
				for (int i = 0; i < start_inputEnd; i++) //从预报开始给其赋值到数据库末尾
				{
					for (int j = 0; d + j < input.size() && j < n; j++) {
						Boolean dateCompare = DateCompare(dateStart,input.get(d+j).getDates(),"小时");
						if (dateCompare){
							data=input.get(d+j);
							break;
						}else {
							data = new PredictInputData();
							data.setDates(dateStart);
							data.setTemperature(12.0);
							data.setRainStation(input.get(0).getRainStation());
							data.setRainfall(0.0);
						}
					}
					calendar.setTime(dateStart);
					calendar.add(Calendar.HOUR_OF_DAY, 1);
					dateStart = calendar.getTime();
					result.add(data);
				}
				//此时的dataStart==数据库末尾的时间
				int inputEnd_dateEnd = duration(input.get(input.size()-1).getDates(),dataEnd,"小时");
				for (int i = 0; i < inputEnd_dateEnd; i++) {
					if (length>0)
					{
						for (int j = 0; j < length; j++) {

							Date date =stringToDate(rainPre.get(j).getDate());
							Boolean dateCompare = DateCompare(dateStart,date,"小时");
							if (dateCompare && station.equals(rainPre.get(j).getArea()))//日期相等并且地点相等才能赋值
							{
								data = new PredictInputData();
								data.setDates(dateStart);
								data.setTemperature(12.0);
								data.setRainStation(input.get(0).getRainStation());
								data.setRainfall(rainPre.get(j).getRainFall());
								break;
							}else {
								data = new PredictInputData();
								data.setDates(dateStart);
								data.setTemperature(12.0);
								data.setRainStation(input.get(0).getRainStation());
								data.setRainfall(0.0);
							}
						}
					}else
					{
						data = new PredictInputData();
						data.setDates(dateStart);
						data.setTemperature(12.0);
						data.setRainStation(input.get(0).getRainStation());
						data.setRainfall(0.0);
					}

					calendar.setTime(dateStart);
					calendar.add(Calendar.HOUR_OF_DAY, 1);
					dateStart = calendar.getTime();
					result.add(data);
				}
			}
		}
		return result;
	}

	/**
	 * 保留前20天雨量
	 * @param input
	 * @return
	 */
	public static List<PredictInputData> TwentyDayRain(ForcastInputParamNew param,List<PredictInputData> input) {
		List<PredictInputData> result = new ArrayList<>();
		Date dateStart = param.getPredictionTime();
		PredictInputData data = new PredictInputData();
		List<Date> dateList = new ArrayList<>();
		for (PredictInputData predictInputData : input) {
			dateList.add(predictInputData.getDates());
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateStart);
		calendar.add(Calendar.DAY_OF_MONTH, -20);
		Date dateStart_20 = calendar.getTime();//找到前二十天
		Date inputDateEnd = input.get(input.size()-1).getDates();//数据库中最新时间
		int d = findNearestTime(dateList,dateStart_20);//找到最贴近的时间
		int start_End =duration(dateStart,inputDateEnd,"日");
		if (start_End > 0)//预报时间在数据库内全为历史值
		{
			for (int i = 0; i < 20; i++) {
				for (int j = 0; d + j < input.size() && j < 20; j++) {
					Boolean dateCompare = DateCompare(dateStart_20,input.get(d+j).getDates(),"日");
					if (dateCompare){
						data=input.get(d+j);
						break;
					}else {
						data = new PredictInputData();
						data.setDates(dateStart_20);
						data.setTemperature(12.0);
						data.setRainStation(input.get(0).getRainStation());
						data.setRainfall(0.0);
					}
				}
				calendar.setTime(dateStart_20);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				dateStart_20 = calendar.getTime();
				result.add(data);
			}
		}else //预报开始时间在数据库外，又可以分为前二十天都在数据库或者不都在数据库
		{	List<RainFallDto> rainFallDtoList = param.getRainFallDtos();
			List<PredictInputData> preRainDay =preRainHourToDay(rainFallDtoList);
			int start_20_End = duration(dateStart_20,inputDateEnd,"日");
			if (start_20_End < 0)//全部为预报值
			{
				for (int i = 0; i < 20; i++) {
					if (!preRainDay.isEmpty())
					{
                        for (PredictInputData predictInputData : preRainDay) {
                            Date date = predictInputData.getDates();
                            Boolean dateCompare = DateCompare(dateStart_20, date, "日");
                            if (dateCompare && predictInputData.getRainStation().equals(input.get(0).getRainStation()))//日期和站点都相等才能赋值
                            {
                                data = predictInputData;
                                break;
                            } else {
                                data = new PredictInputData();
                                data.setDates(dateStart_20);
                                data.setTemperature(12.0);
                                data.setRainStation(input.get(0).getRainStation());
                                data.setRainfall(0.0);
                            }
                        }
					}else
					{
						data = new PredictInputData();
						data.setDates(dateStart_20);
						data.setTemperature(12.0);
						data.setRainStation(input.get(0).getRainStation());
						data.setRainfall(0.0);
					}
					calendar.setTime(dateStart_20);
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					dateStart_20 = calendar.getTime();
					result.add(data);
				}
			}else //二十天一部分历史，一部分预报
			{
				int start_20_inputEnd = duration(dateStart_20,inputDateEnd,"日");
				for (int i = 0; i < start_20_inputEnd; i++) {
					for (int j = 0; d + j < input.size() && j < 20; j++) {
						Boolean dateCompare = DateCompare(dateStart_20,input.get(d+j).getDates(),"日");
						if (dateCompare){
							data=input.get(d+j);
							break;
						}else {
							data = new PredictInputData();
							data.setDates(dateStart_20);
							data.setTemperature(12.0);
							data.setRainStation(input.get(0).getRainStation());
							data.setRainfall(0.0);
						}
					}
					calendar.setTime(dateStart_20);
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					dateStart_20 = calendar.getTime();
					result.add(data);
				}
				int inputEnd_Start = duration(inputDateEnd,dateStart,"日");
				for (int i = 0; i < inputEnd_Start; i++) {
					if (!preRainDay.isEmpty()){
                        for (PredictInputData predictInputData : preRainDay) {
                            Date date = predictInputData.getDates();
                            Boolean dateCompare = DateCompare(dateStart_20, date, "日");
                            if (dateCompare && predictInputData.getRainStation().equals(input.get(0).getRainStation()))//日期相等才能赋值
                            {
                                data = predictInputData;
                                break;
                            } else {
                                data = new PredictInputData();
                                data.setDates(dateStart_20);
                                data.setTemperature(12.0);
                                data.setRainStation(input.get(0).getRainStation());
                                data.setRainfall(0.0);
                            }
                        }
					}else
					{
						data = new PredictInputData();
						data.setDates(dateStart_20);
						data.setTemperature(12.0);
						data.setRainStation(input.get(0).getRainStation());
						data.setRainfall(0.0);
					}

					calendar.setTime(dateStart_20);
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					dateStart_20 = calendar.getTime();
					result.add(data);
				}
			}
		}
		return result;
	}

	/**
	 * 返回相差的数量
	 * @param dateStart
	 * @param dateEnd
	 * @param period
	 * @return
	 */
	public static int duration(Date dateStart,Date dateEnd,String period){
		int result = 0;
		if (period.equals("年")){
			LocalDate localDate1 = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate localDate2 = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			Period duration = Period.between(localDate1, localDate2);
			int years = duration.getYears();
			result =years;
		}
		if (period.equals("月")){
			LocalDate localDate1 = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate localDate2 = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			long months = ChronoUnit.MONTHS.between(localDate1, localDate2);
			result =Math.toIntExact(months);
		}
		if (period.equals("日")){
			LocalDate localDate1 = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate localDate2 = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			// 计算相差天数并返回
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
	 * 楼庄子上游雨量站数据整合
	 * @param paramNew
	 * @return 前期雨量和小时尺度降水
	 */
	public static List<List<PredictInputData>> LzzRainIntegration(ForcastInputParamNew paramNew) throws ParseException, IOException, InvalidFormatException {
		List<List<PredictInputData>> result = new ArrayList<>();
		//雨量站整合
		List<PredictInputData> RainHour = new ArrayList<>();
		List<PredictInputData> RainDay = new ArrayList<>();
		//喀什沟
		List<PredictInputData> KSG = DataUtils.lzzDataConversion(paramNew).get(3);
		KSG=DataUtils.hoursRain(paramNew ,KSG);
		List<PredictInputData> KSGDAY = DataUtils.lzzRainHourToDay(paramNew.getLzzHydrologyParam().getKsgRainfallStation());
		KSGDAY=DataUtils.TwentyDayRain(paramNew,KSGDAY);
		RainDay.addAll(KSGDAY);
		RainHour.addAll(KSG);
		//黑沟
		List<PredictInputData> HG = DataUtils.lzzDataConversion(paramNew).get(4);
		HG=DataUtils.hoursRain(paramNew,HG);
		List<PredictInputData> HGDAY = DataUtils.lzzRainHourToDay(paramNew.getLzzHydrologyParam().getHgRainfallStation());
		HGDAY=DataUtils.TwentyDayRain(paramNew,HGDAY);
		RainDay.addAll(HGDAY);
		RainHour.addAll(HG);
		//煤矿沟
		List<PredictInputData> MKG = DataUtils.lzzDataConversion(paramNew).get(5);
		MKG=DataUtils.hoursRain(paramNew,MKG);
		List<PredictInputData> MKGDAY = DataUtils.lzzRainHourToDay(paramNew.getLzzHydrologyParam().getMkgRainfallStation());
		MKGDAY=DataUtils.TwentyDayRain(paramNew,MKGDAY);
		RainDay.addAll(MKGDAY);
		RainHour.addAll(MKG);
		//无名沟
		List<PredictInputData> WMG = DataUtils.lzzDataConversion(paramNew).get(6);
		WMG=DataUtils.hoursRain(paramNew,WMG);
		List<PredictInputData> WMGDAY = DataUtils.lzzRainHourToDay(paramNew.getLzzHydrologyParam().getWmgRainfallStation());
		WMGDAY=DataUtils.TwentyDayRain(paramNew,WMGDAY);
		RainDay.addAll(WMGDAY);
		RainHour.addAll(WMG);
		//加普沙
		List<PredictInputData> JPS = DataUtils.lzzDataConversion(paramNew).get(7);
		JPS=DataUtils.hoursRain(paramNew,JPS);
		List<PredictInputData> JPSDAY = DataUtils.lzzRainHourToDay(paramNew.getLzzHydrologyParam().getJpsRainfallStation());
		JPSDAY=DataUtils.TwentyDayRain(paramNew,JPSDAY);
		RainDay.addAll(JPSDAY);
		RainHour.addAll(JPS);
		//宰尔德
		List<PredictInputData> ZED = DataUtils.lzzDataConversion(paramNew).get(8);
		ZED=DataUtils.hoursRain(paramNew,ZED);
		List<PredictInputData> ZEDDAY = DataUtils.lzzRainHourToDay(paramNew.getLzzHydrologyParam().getZrdRainfallStation());
		ZEDDAY=DataUtils.TwentyDayRain(paramNew,ZEDDAY);
		RainDay.addAll(ZEDDAY);
		RainHour.addAll(ZED);
		//东南沟
		List<PredictInputData> DNG = DataUtils.lzzDataConversion(paramNew).get(9);
		DNG=DataUtils.hoursRain(paramNew,DNG);
		List<PredictInputData> DNGDAY = DataUtils.lzzRainHourToDay(paramNew.getLzzHydrologyParam().getDngRainfallStation());
		DNGDAY=DataUtils.TwentyDayRain(paramNew,DNGDAY);
		RainDay.addAll(DNGDAY);
		RainHour.addAll(DNG);
		//八一林场
		List<PredictInputData> BYLC = DataUtils.lzzDataConversion(paramNew).get(10);
		BYLC=DataUtils.hoursRain(paramNew,BYLC);
		List<PredictInputData> BYLCDAY = DataUtils.lzzRainHourToDay(paramNew.getLzzHydrologyParam().getBylcRainfallStation());
		BYLCDAY=DataUtils.TwentyDayRain(paramNew,BYLCDAY);
		RainDay.addAll(BYLCDAY);
		RainHour.addAll(BYLC);
		//萨尔达万
		List<PredictInputData> SEDW = DataUtils.lzzDataConversion(paramNew).get(11);
		SEDW=DataUtils.hoursRain(paramNew,SEDW);
		List<PredictInputData> SEDWDAY = DataUtils.lzzRainHourToDay(paramNew.getLzzHydrologyParam().getSedwRainfallStation());
		SEDWDAY=DataUtils.TwentyDayRain(paramNew,SEDWDAY);
		RainDay.addAll(SEDWDAY);
		RainHour.addAll(SEDW);
		//制材厂
		List<PredictInputData> ZCC = DataUtils.lzzDataConversion(paramNew).get(12);
		ZCC=DataUtils.hoursRain(paramNew,ZCC);
		List<PredictInputData> ZCCDAY = DataUtils.lzzRainHourToDay(paramNew.getLzzHydrologyParam().getZccRainfallStation());
		ZCCDAY=DataUtils.TwentyDayRain(paramNew,ZCCDAY);
		RainDay.addAll(ZCCDAY);
		RainHour.addAll(ZCC);
		//添加小时尺度雨量和日尺度雨量
		result.add(RainHour);
		result.add(RainDay);
		return result;
	}

	/**
	 * 区间雨量站整合
	 * @param paramNew
	 * @return 24小时雨量和20天雨量
	 */
	public static List<List<PredictInputData>> IrrigateRainIntegration(ForcastInputParamNew paramNew) throws ParseException {
		List<List<PredictInputData>> result = new ArrayList<>();
		//雨量站整合
		List<PredictInputData> RainHour = new ArrayList<>();
		List<PredictInputData> RainDay = new ArrayList<>();
		//小渠子
		List<PredictInputData> XQZ = DataUtils.irrigatedDataConversion(paramNew.getIrrigatedHydrologyParam()).get(1);
		XQZ=DataUtils.hoursRain(paramNew,XQZ);
		List<PredictInputData> XQZDAY = DataUtils.IrrigateRainHourToDay(paramNew.getIrrigatedHydrologyParam().getXqzGaugingStation());
		XQZDAY=DataUtils.TwentyDayRain(paramNew,XQZDAY);
		RainDay.addAll(XQZDAY);
		RainHour.addAll(XQZ);
		//团结一队
		List<PredictInputData> TJYD = DataUtils.irrigatedDataConversion(paramNew.getIrrigatedHydrologyParam()).get(2);
		TJYD=DataUtils.hoursRain(paramNew,TJYD);
		List<PredictInputData> TJYDDAY = DataUtils.IrrigateRainHourToDay(paramNew.getIrrigatedHydrologyParam().getTjydGaugingStation());
		TJYDDAY=DataUtils.TwentyDayRain(paramNew,TJYDDAY);
		RainDay.addAll(TJYDDAY);
		RainHour.addAll(TJYD);
		//头屯河水库
		List<PredictInputData> TTHR = DataUtils.irrigatedDataConversion(paramNew.getIrrigatedHydrologyParam()).get(3);
		TTHR=DataUtils.hoursRain(paramNew,TTHR);
		List<PredictInputData> TTHRDAY = DataUtils.IrrigateRainHourToDay(paramNew.getIrrigatedHydrologyParam().getTthGaugingStation());
		TTHRDAY=DataUtils.TwentyDayRain(paramNew,TTHRDAY);
		RainDay.addAll(TTHRDAY);
		RainHour.addAll(TTHR);
		//添加小时尺度雨量和日尺度雨量
		result.add(RainHour);
		result.add(RainDay);
		return result;
	}

	/**
	 * 楼庄子入库乘以系数得到区间
	 * @param LZZIN
	 * @return 区间来流量
	 */
	public static List<PredictInputData> Scaling(List<PredictInputData> LZZIN){
		List<PredictInputData> result = new ArrayList<>();
		for (int i = 0; i < LZZIN.size(); i++) {
			PredictInputData tthData = LZZIN.get(i);
			PredictInputData qjData = new PredictInputData();
			qjData.setDates(tthData.getDates());
			qjData.setRainStation("楼头区间");
			qjData.setFlow(tthData.getFlow()*0.058);//0.058为多年平均所得区间来水与头屯河入库的比值
			result.add(qjData);
		}
		return result;
	}


	/**
	 *
	 * @param pointData
	 * 各个雨量站点雨量
	 * 后续更改（各个雨量站权重）
	 * @return surfaceData
	 * 该流域的面雨量
	 */
	public static List<PredictInputData> pointToSurface(List<PredictInputData> pointData){
		List<PredictInputData> result=new ArrayList<>();
		String stationName = pointData.get(0).getRainStation();
		//number为时段数量
		int number = 0 ;
		for (int i = 0; i < pointData.size(); i++) {
			if (pointData.get(i).getRainStation().equals(stationName)){
				number++;
			}
		}
		PredictInputData hourData ;
		List<PredictInputData> hourDatalist = new ArrayList<>();
		List<List<PredictInputData>> hourDataList = new ArrayList<>();
		//按时间排序，划分为同一时段不同雨量站的List
		for (int j = 0; j < number; j++) {
			for (int i = 0; i < pointData.size(); i++) {
				Boolean dateCompare = DateCompare(pointData.get(j).getDates(),pointData.get(i).getDates(),"小时");
				if (dateCompare){
					hourData=pointData.get(i);
					hourDatalist.add(hourData);
				}
			}
			hourDataList.add(hourDatalist);
			hourDatalist = new ArrayList<>();
		}
		for (int i = 0; i < number; i++) {
			PredictInputData hourResult=new PredictInputData();
			double rainFall =0.0;
			double temperature=0.0;
			hourDatalist=hourDataList.get(i);
			hourResult.setDates(hourDatalist.get(0).getDates());
			//hourDatalist为同一时间段不同雨量站,hourDatalist.size()为雨量站数量
			for (int j = 0; j < hourDatalist.size(); j++) {
				if (hourDatalist.get(j).getRainStation().equals("八一林场自动雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.1;
					temperature += hourDatalist.get(j).getTemperature();
				}
				if (hourDatalist.get(j).getRainStation().equals("加普沙自动雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.1;
					temperature += hourDatalist.get(j).getTemperature();
				}
				if (hourDatalist.get(j).getRainStation().equals("东南沟自动雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.1;
					temperature += hourDatalist.get(j).getTemperature();
				}
				if (hourDatalist.get(j).getRainStation().equals("宰尔德自动雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.1;
					temperature += hourDatalist.get(j).getTemperature();
				}
				if (hourDatalist.get(j).getRainStation().equals("无名沟自动雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.1;
					temperature += hourDatalist.get(j).getTemperature();
				}
				if (hourDatalist.get(j).getRainStation().equals("萨尔达万自动雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.1;
					temperature += hourDatalist.get(j).getTemperature();
				}
				if (hourDatalist.get(j).getRainStation().equals("煤矿沟自动雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.1;
					temperature += hourDatalist.get(j).getTemperature();
				}
				if (hourDatalist.get(j).getRainStation().equals("黑沟自动雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.1;
					temperature += hourDatalist.get(j).getTemperature();
				}
				if (hourDatalist.get(j).getRainStation().equals("喀什沟自动雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.1;
					temperature += hourDatalist.get(j).getTemperature();
				}
				if (hourDatalist.get(j).getRainStation().equals("制材厂自动雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.1;
					temperature += hourDatalist.get(j).getTemperature();
				}
				//区间
				if (hourDatalist.get(j).getRainStation().equals("小渠子雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.3;
				}
				if (hourDatalist.get(j).getRainStation().equals("团结一队雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.3;
				}
				if (hourDatalist.get(j).getRainStation().equals("头屯河水库雨量站")){
					rainFall += hourDatalist.get(j).getRainfall()*0.3;
				}
				temperature = temperature/hourDatalist.size();
			}
			hourResult.setRainStation("面雨量");
			hourResult.setRainfall(rainFall);
			hourResult.setTemperature(temperature);
			result.add(hourResult);
		}
		return result;
	}


	/**
	 * 从数据库导入的数据进行处理，包括对三号桥、楼庄子进库站异常流量的处理，上游雨量站温度空值的处理
	 * @param paramForcastInputParamNew
	 * @return New
	 */
	public static ForcastInputParamNew emptyProcessing (ForcastInputParamNew paramForcastInputParamNew){
		ForcastInputParamNew result=new ForcastInputParamNew();
		//数据外参数赋值，保持不变
		result.setModelType(paramForcastInputParamNew.getModelType());
		result.setPeriodTimeNum(paramForcastInputParamNew.getPeriodTimeNum());
		result.setPeriodTimeStep(paramForcastInputParamNew.getPeriodTimeStep());
		result.setPeriodTimeType(paramForcastInputParamNew.getPeriodTimeType());
		result.setPredictionTime(paramForcastInputParamNew.getPredictionTime());
		//输入数据的转化
		LzzHydrologyParam lzzHydrologyParam = paramForcastInputParamNew.getLzzHydrologyParam();
		/**
		 * 水位站数据的前期处理
		 */
		//三号桥流量异常去除
		List<LzzGaugingStation> THQ = lzzHydrologyParam.getThreeGaugingStation();
		List<LzzGaugingStation> THQresult = new ArrayList<>();
		for (int i = 0; i < THQ.size(); i++) {
			LzzGaugingStation station = THQ.get(i);
			//去除空值数据
			String id = station.getId();
			String[] parts = id.split(":");
			String bridgeNumber = parts[0];
			long numericValue= Long.parseLong(parts[1]);
			Date date = new Date(numericValue); // 根据时间戳创建日期对象
			int month = getSpecificDate(date).get("月");
			if (parts.length > 1 &&bridgeNumber.length() > 1){
				if(month<=6||month>=9){
					if (station.getFlow() != null && station.getFlow() <= 100){
						THQresult.add(THQ.get(i));
					}
				}else {
					if (station.getFlow() != null && station.getFlow() <= 300){
						THQresult.add(THQ.get(i));
					}
				}
			}
		}
		lzzHydrologyParam.setThreeGaugingStation(THQresult);
		//楼庄子进库站流量去除异常
		List<LzzGaugingStation> LZZ = lzzHydrologyParam.getLzzInput();
		List<LzzGaugingStation> LZZresult = new ArrayList<>();
		for (int i = 0; i < LZZ.size(); i++) {
			LzzGaugingStation station = LZZ.get(i);
			String id = station.getId();
			String[] parts = id.split(":");
			String bridgeNumber = parts[0];
			long numericValue= Long.parseLong(parts[1]);
			Date date = new Date(numericValue); // 根据时间戳创建日期对象
			int month = getSpecificDate(date).get("月");
			if (parts.length > 1 &&bridgeNumber.length() > 1){
				if(month<=6||month>=9){
					if (station.getFlow() != null && station.getFlow() <= 100){
						LZZresult.add(LZZ.get(i));
					}
				}else {
					if (station.getFlow() != null && station.getFlow() <= 300){
						LZZresult.add(LZZ.get(i));
					}
				}
			}
		}
		lzzHydrologyParam.setLzzInput(LZZresult);
		List<LzzGaugingStation> LZZO = lzzHydrologyParam.getLzzOutput();
		List<LzzGaugingStation> LZZOresult = new ArrayList<>();
		for (int i = 0; i < LZZO.size(); i++) {
			LzzGaugingStation station = LZZO.get(i);
			//去除空值数据
			String id = station.getId();
			String[] parts = id.split(":");
			String bridgeNumber = parts[0];
			long numericValue= Long.parseLong(parts[1]);
			Date date = new Date(numericValue); // 根据时间戳创建日期对象
			int month = getSpecificDate(date).get("月");
			if (parts.length > 1 &&bridgeNumber.length() > 1){
				if(month<=6||month>=9){
					if (station.getFlow() != null && station.getFlow() <= 100){
						LZZOresult.add(LZZO.get(i));
					}
				}else {
					if (station.getFlow() != null && station.getFlow() <= 300){
						LZZOresult.add(LZZO.get(i));
					}
				}
			}
		}
		lzzHydrologyParam.setLzzOutput(LZZOresult);
		List<LzzGaugingStation> LZZW = lzzHydrologyParam.getLzzWaterLevel();
		List<LzzGaugingStation> LZZWresult = new ArrayList<>();
		for (int i = 0; i < LZZW.size(); i++) {
			LzzGaugingStation station = LZZW.get(i);
			//去除空值数据
			String id = station.getId();
			String[] parts = id.split(":");
			String bridgeNumber = parts[0];
			long numericValue= Long.parseLong(parts[1]);
			Date date = new Date(numericValue); // 根据时间戳创建日期对象
			int month = getSpecificDate(date).get("月");
			if (parts.length > 1 &&bridgeNumber.length() > 1){
				if(month<=6||month>=9){
					if (station.getFlow() != null && station.getFlow() <= 100){
						LZZWresult.add(LZZW.get(i));
					}
				}else {
					if (station.getFlow() != null && station.getFlow() <= 300){
						LZZWresult.add(LZZW.get(i));
					}
				}
			}
		}
		lzzHydrologyParam.setLzzWaterLevel(LZZWresult);
		/**
		 * 雨量站数据的前期处理
		 */
		//雨量站的处理温度数据为空则匹配下一个温度
		List<LzzRainfallStation> KSG = lzzHydrologyParam.getKsgRainfallStation();
		KSG = TemperatureProcessing(KSG);
		lzzHydrologyParam.setKsgRainfallStation(KSG);//喀什沟
		List<LzzRainfallStation> HG = lzzHydrologyParam.getHgRainfallStation();
		HG = TemperatureProcessing(HG);
		lzzHydrologyParam.setHgRainfallStation(HG);//黑沟
		List<LzzRainfallStation> MKG = lzzHydrologyParam.getMkgRainfallStation();
		MKG = TemperatureProcessing(MKG);
		lzzHydrologyParam.setMkgRainfallStation(MKG);//煤矿沟
		List<LzzRainfallStation> WMG = lzzHydrologyParam.getWmgRainfallStation();
		WMG = TemperatureProcessing(WMG);
		lzzHydrologyParam.setWmgRainfallStation(WMG);//无名沟
		List<LzzRainfallStation> JPS = lzzHydrologyParam.getJpsRainfallStation();
		JPS = TemperatureProcessing(JPS);
		lzzHydrologyParam.setJpsRainfallStation(JPS);//加普沙
		List<LzzRainfallStation> ZED = lzzHydrologyParam.getZrdRainfallStation();
		ZED = TemperatureProcessing(ZED);
		lzzHydrologyParam.setZrdRainfallStation(ZED);//宰尔德
		List<LzzRainfallStation> DNG = lzzHydrologyParam.getDngRainfallStation();
		DNG = TemperatureProcessing(DNG);
		lzzHydrologyParam.setDngRainfallStation(DNG);//东南沟
		List<LzzRainfallStation> BYLC = lzzHydrologyParam.getBylcRainfallStation();
		BYLC = TemperatureProcessing(BYLC);
		lzzHydrologyParam.setBylcRainfallStation(BYLC);//八一林场
		List<LzzRainfallStation> SEDW = lzzHydrologyParam.getSedwRainfallStation();
		SEDW = TemperatureProcessing(SEDW);
		lzzHydrologyParam.setSedwRainfallStation(SEDW);//萨尔达万
		List<LzzRainfallStation> ZCC = lzzHydrologyParam.getZccRainfallStation();
		ZCC = TemperatureProcessing(ZCC);
		lzzHydrologyParam.setZccRainfallStation(ZCC);//制材厂
		result.setLzzHydrologyParam(lzzHydrologyParam);
		/**
		 * 区间数据的前期处理
		 */
		//区间数据站点名空值处理
		IrrigatedHydrologyParam irrigatedHydrologyParam = paramForcastInputParamNew.getIrrigatedHydrologyParam();
		irrigatedHydrologyParam = NullStationProcessing(irrigatedHydrologyParam);
		result.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
		return result;
		}


	/**
	 * 温度值的处理，空值时等同于下一个有值的温度
	 * @param inputData
	 * @return
	 */
	public static List<LzzRainfallStation> TemperatureProcessing(List<LzzRainfallStation> inputData){
		List<LzzRainfallStation> result = new ArrayList<>();
		for (int i = 0; i < inputData.size() ; i++) {
			//去除空值异常
			String id = inputData.get(i).getId();
			String[] parts = id.split(":");
			String bridgeNumber = parts[0];
			if (parts.length > 1 && bridgeNumber.length() > 1){
				result.add(inputData.get(i));
			}
		}
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).getTemperature() == null){
				int n = 0 ;
				for (int j = 0; i+j<result.size(); j++) {
					if ((result.get(i+j).getTemperature() == null)){
						n++;
					}else break;
				}
				if (i + n < result.size()){
					BigDecimal T = result.get(i+n).getTemperature();
					result.get(i).setTemperature(T);
				}else {
					BigDecimal T = result.get(i-1).getTemperature();
					result.get(i).setTemperature(T);
				}
			}
		}
		return result;
	}

	/**
	 * 区间数据站点名为空的处理
	 * @param inputData
	 * @return
	 */
	public static IrrigatedHydrologyParam NullStationProcessing(IrrigatedHydrologyParam inputData){
		IrrigatedHydrologyParam result = new IrrigatedHydrologyParam();
		List<IrrigatedPlatformDataInfo> XQZ = inputData.getXqzGaugingStation();
		List<IrrigatedPlatformDataInfo> XQZresult = new ArrayList<>();
		for (int i = 0; i < XQZ.size(); i++) {
			IrrigatedPlatformDataInfo station = XQZ.get(i);
			//去除空值数据
			String id = station.getId();
			String[] parts = id.split("-");
			String bridgeNumber = parts[0];
			if (parts.length > 1 && bridgeNumber.length() > 1){
				XQZresult.add(XQZ.get(i));
			}
		}
		result.setXqzGaugingStation(XQZresult);
		//团结一队雨量站
		List<IrrigatedPlatformDataInfo> TJYD = inputData.getTjydGaugingStation();
		List<IrrigatedPlatformDataInfo> TJYDresult = new ArrayList<>();
		for (int i = 0; i < TJYD.size(); i++) {
			IrrigatedPlatformDataInfo station = TJYD.get(i);
			//去除空值数据
			String id = station.getId();
			String[] parts = id.split("-");
			String bridgeNumber = parts[0];
			if (parts.length > 1 &&bridgeNumber.length() > 1){
				TJYDresult.add(TJYD.get(i));
			}

		}
		result.setTjydGaugingStation(TJYDresult);
		//头屯河雨量站
		List<IrrigatedPlatformDataInfo> TTH = inputData.getTthGaugingStation();
		List<IrrigatedPlatformDataInfo> TTHresult = new ArrayList<>();
		for (int i = 0; i < TTH.size(); i++) {
			IrrigatedPlatformDataInfo station = TTH.get(i);
			//去除空值数据
			String id = station.getId();
			String[] parts = id.split("-");
			String bridgeNumber = parts[0];
			if (parts.length > 1 &&bridgeNumber.length() > 1){
				TTHresult.add(TTH.get(i));
			}
		}
		result.setTthGaugingStation(TTHresult);
		//头屯河入库流量
		List<IrrigatedPlatformDataInfo> TTHI = inputData.getTthInput();
		List<IrrigatedPlatformDataInfo> TTHIresult = new ArrayList<>();
		for (int i = 0; i < TTHI.size(); i++) {
			IrrigatedPlatformDataInfo station = TTHI.get(i);
			//去除空值数据
			String id = station.getId();
			String[] parts = id.split("-");
			String bridgeNumber = parts[0];
			if (parts.length > 1 &&bridgeNumber.length() > 1){
				TTHIresult.add(TTHI.get(i));
			}
		}
		result.setTthInput(TTHIresult);
		return result;
	}

	/**
	 *
	 * @param paramForcastInputParamNew 从数据库中获得的数据
	 * @return 日尺度温度与降水
	 */
	public static List<PredictInputData> RAT(ForcastInputParamNew paramForcastInputParamNew)
	{
		//雨量站整合
		List<PredictInputData> RainDay = new ArrayList<>();
		//喀什沟
		List<PredictInputData> KSGDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getKsgRainfallStation());
		RainDay.addAll(KSGDAY);
		//黑沟
		List<PredictInputData> HGDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getHgRainfallStation());
		RainDay.addAll(HGDAY);
		//煤矿沟
		List<PredictInputData> MKGDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getMkgRainfallStation());
		RainDay.addAll(MKGDAY);
		//无名沟
		List<PredictInputData> WMGDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getWmgRainfallStation());
		RainDay.addAll(WMGDAY);
		//加普沙
		List<PredictInputData> JPSDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getJpsRainfallStation());
		RainDay.addAll(JPSDAY);
		//宰尔德
		List<PredictInputData> ZEDDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getZrdRainfallStation());
		RainDay.addAll(ZEDDAY);
		//东南沟
		List<PredictInputData> DNGDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getDngRainfallStation());
		RainDay.addAll(DNGDAY);
		//八一林场
		List<PredictInputData> BYLCDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getBylcRainfallStation());
		RainDay.addAll(BYLCDAY);
		//萨尔达万
		List<PredictInputData> SEDWDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getSedwRainfallStation());
		RainDay.addAll(SEDWDAY);
		//制材厂
		List<PredictInputData> ZCCDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getZccRainfallStation());
		RainDay.addAll(ZCCDAY);
		//添加日尺度温度与降水
		List<PredictInputData> RAT = pointToSurface(RainDay);//转换为平均值

		return RAT;
	}

	/**
	 * 为水位站添加温度和降水
	 * @param WaterStation
	 * @param RAT
	 * @return
	 */
	public static List<PredictInputData> AddRAndT(List<PredictInputData> WaterStation, List<PredictInputData> RAT){
		List<PredictInputData> result = new ArrayList<>();
		for (int i = 0; i < WaterStation.size(); i++) {
			for (int j = 0; j < RAT.size(); j++) {
				Boolean dateCompare = DateCompare(RAT.get(j).getDates(),WaterStation.get(i).getDates(),"小时");
				if (dateCompare) {
					double rain = RAT.get(j).getRainfall();
					WaterStation.get(i).setRainfall(rain);
					double temperature = RAT.get(j).getTemperature();
					WaterStation.get(i).setTemperature(temperature);
				}
			}
			if (WaterStation.get(i).getRainfall()==null){
				WaterStation.get(i).setRainfall(0.0);
			}
		}
		//为空日期赋值，赋值为0
        for (PredictInputData predictInputData : WaterStation) {
            if (predictInputData.getTemperature() == null) {
                predictInputData.setTemperature(0.0);
            }
        }
		result = WaterStation;
		return result;
	}


	/**
	 * 日期比较
	 * @param date1
	 * @param date2 最终返回date2=date1
	 * @param period
	 * @return 如果两个日期在规定尺度上相等，则返回true
	 */
	public static Boolean DateCompare(Date date1,Date date2,String period){
		Boolean result = false;
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
	 * @return
	 */
	public static int findNearestTime(List<Date> timeSeries, Date inputTime) {
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

		Date before = timeSeries.get(index - 1);
		Date after = timeSeries.get(index);
		LocalDate localDate1 = before.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate localDate2 = inputTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate localDate3 = after.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		// 计算相差天数并返回
		long duration =  Duration.between(localDate1.atStartOfDay(), localDate2.atStartOfDay()).toHours();
		int n = Math.toIntExact(duration);
//		m是后面的时间与需要寻找的时间之间的差值
		long duration2 =  Duration.between(localDate2.atStartOfDay(), localDate3.atStartOfDay()).toHours();
		int m = Math.toIntExact(duration2);
//		if (m < n) {
//			return index;
//		} else {
//			return index-1;
//		}
		return index;
	}

	/**
	 * 根据period将日尺度数据转换为相应尺度
	 * @param data
	 * @param period
	 * @return
	 */
	public static List<PredictInputData> ChangeDate(List<PredictInputData> data, String period){
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
		//小时尺度和日尺度数据不做处理直接输出
		else result = data;

		return result;
	}


	/**
	 * 筛选枯水期丰水期,5~9月为丰水期
	 * @param input
	 * @param preStartTime
	 * @return
	 */
	public static Object[][] SelectDate(Object[][] input,Date preStartTime){
		List<Object[]> KuData =new ArrayList<>();
		Object[] kudata =new Object[input[0].length];
		List<Object[]> FengData =new ArrayList<>();
		Object[] fengdata =new Object[input[0].length];
		for (int i = 0; i < input.length; i++) {
			Date time = (Date) input[i][0];
			int month = getSpecificDate(time).get("月");
			kudata = new Object[input[0].length];
			fengdata = new Object[input[0].length];
			if (month<=4 || month>=10)
			{
				for (int j = 0; j < input[0].length; j++) {
					kudata[j]=input[i][j];
				}
				KuData.add(kudata);
			}
			else {
				for (int j = 0; j < input[0].length; j++) {
					fengdata[j]=input[i][j];
				}
				FengData.add(fengdata);
			}
		}
		Date time2 = preStartTime;
		int month2 = getSpecificDate(time2).get("月");
		Object[][] longForecastInput;
		if (month2<=4 || month2>=10){
			longForecastInput = new Object[KuData.size()][input[0].length];
			for (int i = 0; i < KuData.size(); i++) {
				longForecastInput[i]=KuData.get(i);
			}

		}else {
			longForecastInput = new Object[FengData.size()][input[0].length];
			for (int i = 0; i < FengData.size(); i++) {
				longForecastInput[i]=FengData.get(i);
			}
		}

		return longForecastInput;
	}

	/**
	 * 筛选融雪期,5~7月融雪
	 * @param input
	 * @return
	 */
	public static Object[][] snowMeltDate(Object[][] input){
		List<Object[]> Data =new ArrayList<>();
		Object[] data;
		for (int i = 0; i < input.length; i++) {
			Date time = (Date) input[i][0];
			int month = getSpecificDate(time).get("月");
			data = new Object[input[0].length];
			if (month>=5 && month<=7){
				for (int j = 0; j < input[0].length; j++) {
					data[j]=input[i][j];
				}
				Data.add(data);
			}
		}
		Object[][] rongXueInput = new Object[Data.size()][input[0].length];
		for (int i = 0; i < Data.size(); i++) {
			rongXueInput[i]=Data.get(i);
		}
		return rongXueInput;
	}


	public static MiniBatch separateByBatch(int batchNum, double[][] inputData, double[][] outputData) {
		List<double[][]> input = new ArrayList();
		List<double[][]> output = new ArrayList();
		List<double[]> inputList = new ArrayList();
		List<double[]> outputList = new ArrayList();
		for (int i = 0; i < inputData.length; i++) {
			inputList.add(inputData[i]);
			outputList.add(outputData[i]);
		}

		int totalNum = inputList.size() / batchNum;
		if (totalNum >= 1) {
			for (int n = 0; n < totalNum; n++) {
				int size = inputList.size();
				double[][] input_batch = new double[batchNum][];
				double[][] output_batch = new double[batchNum][];
				for (int i = 0; i < batchNum; i++) {
					Random ran = new Random();
					int index = ran.nextInt(size);
					input_batch[i] = inputList.remove(index);
					output_batch[i] = outputList.remove(index);
					size--;
					if (size == 0) {
						break;
					}
				}
				input.add(input_batch);
				output.add(output_batch);
			}
		}

		if (inputList.size() != 0) {
			double[][] input_batch = new double[inputList.size()][];
			double[][] output_batch = new double[inputList.size()][];
			for (int i = 0; i < inputList.size(); i++) {
				input_batch[i] = inputList.get(i);
				output_batch[i] = outputList.get(i);
			}
			input.add(input_batch);
			output.add(output_batch);
		}

		MiniBatch mb = new MiniBatch();
		mb.setInput(input);
		mb.setOutput(output);
		mb.setNum(input.size());
		return mb;
	}


	public static List<TthResultEntity> getHyfTrainResult(double[][] real, double[][] sim, double[][] data,
														  Date[][] dates, ParamsSetVO psvo, boolean isCascade) {
		String inputIndex = DataUtils.array2String(psvo.getInputIndex());
		String[] layerNum = psvo.getLayerCount().split(",");
		double outputNum = Double.parseDouble(layerNum[layerNum.length - 1]);
		List<TthResultEntity> resultList = new ArrayList();
		for (int i = 0; i < sim.length; i++) {
			for (int j = 0; j < sim[i].length; j++) {
				TthResultEntity simTrainResultEntity = new TthResultEntity();
				simTrainResultEntity.setForecastDuanmian(psvo.getForecastDuanmian());
				simTrainResultEntity.setInputIndex(inputIndex);
				simTrainResultEntity.setPeriod(psvo.getForecastPeriod());
				simTrainResultEntity.setModelName(psvo.getNetClass());
				simTrainResultEntity.setRealOutput(real[i][j]);
				simTrainResultEntity.setSimOutput(sim[i][j]);
				simTrainResultEntity.setResultDate(dates[i][j]);
				simTrainResultEntity.setDatasetStart(psvo.getDataSetStartTime());
				simTrainResultEntity.setDatasetEnd(psvo.getDateSetEndTime());
				simTrainResultEntity.setTestDatasetStart(psvo.getTestSetStartTime());
				simTrainResultEntity.setTestDatasetEnd(psvo.getTestSetEndTime());
				simTrainResultEntity.setOutputNum(outputNum);
				simTrainResultEntity.setOutputIndex((double) j);
				simTrainResultEntity.setRainfall(data[i][j + 3]);
				simTrainResultEntity.setUserName("hust");
				resultList.add(simTrainResultEntity);
			}
		}
		return resultList;
	}

	/**
	 * 模型预报时的输入
	 * @param dataList
	 * @param paramsSetVO
	 * @return
	 */
	public static double[][] inputData_Real(List<double[]> dataList, ParamsSetVO paramsSetVO){
		/**
		 * 前n旬流量+平均流量
		 */
		int a = dataList.get(0).length;
		int n = paramsSetVO.getInfluence_factor();
//		 一般训练期：检验期=3:1
		double[][] data = new double[a - n + 1][n + 2];
		for (int i = 0; i < a - n + 1; i++) {
			data[i][0] = dataList.get(0)[i + n - 1];//时间戳
			for (int j = 0; j < n - 1 ; j++) {
				data[i][j + 1] = dataList.get(1)[i + j];//前N旬流量
			}
			data[i][n] = dataList.get(2)[i];//平均流量
			data[i][n + 1] = dataList.get(1)[i + n - 1];//预报径流
		}
		return data;
	}

	/**
	 * 训练模型时的输入
	 * @param dataList
	 * @param paramsSetVO
	 * @param isTest
	 * @return
	 * @throws Exception
	 */
	public static double[][] inputData_Train(List<double[]> dataList, ParamsSetVO paramsSetVO, boolean isTest) throws Exception {

		int a = dataList.get(0).length;
		int b = a / 4 * 3;
		int c = a - b;
		int n = paramsSetVO.getInfluence_factor();
		/**
		 * 前n旬流量+平均流量
		 */
//		 一般训练期：检验期=3:1
		double[][] trainData = new double[b- n + 1][n + 2];// 第一维样本数据的长度，第二维输入节点输出节点的值,105,84
		double[][] testData = new double[c- n + 1][n + 2];

		for (int i = 0; i < b- n + 1; i++) {
			trainData[i][0] = dataList.get(0)[i+n-1];//时间戳
			for (int j = 0; j < n-1 ; j++) {
				trainData[i][j+1] = dataList.get(1)[i+j];//前N旬流量
			}
			trainData[i][n] = dataList.get(2)[i];//平均流量
			trainData[i][n+1] = dataList.get(1)[i+n-1];//预报径流
		}


		for (int i = b; i < a-n+1; i++) {
			testData[i - b][0] = dataList.get(0)[i+n-1];//时间戳
			for (int j = 0; j < n-1 ; j++) {
				testData[i - b][j+1] = dataList.get(1)[i+j];//前N旬流量
			}
			testData[i - b][n] = dataList.get(2)[i];//平均流量
			testData[i - b][n+1] = dataList.get(1)[i+n-1];//预报径流
		}

		if (isTest) {
			return testData;
		} else {
			return trainData;
		}

	}

	/**
	 * 融雪模型数据输入
	 * @param dataList
	 * @param paramsSetVO
	 * @return
	 */
	public static double[][] inputData_Real_Snow(List<double[]> dataList, ParamsSetVO paramsSetVO){
		/**
		 * 前3天流量+前3天温度+前3天降水
		 */
		int a = dataList.get(0).length;
		int n = paramsSetVO.getInfluence_factor();
		double[][] data = new double[a - n][n * 3 + 2];
		for (int i = 0; i < a - n ; i++) {
			data[i][0] = dataList.get(0)[i + n];//时间戳
			for (int j = 0; j < n  ; j++) {
				data[i][j + 1] = dataList.get(1)[i + j];//前N天流量
				data[i][j + 4] = dataList.get(3)[i + j];//温度
				data[i][j + 7] = dataList.get(4)[i + j];//降水
			}

			data[i][n * 3 + 1] = dataList.get(1)[i + n];//预报径流
		}
		return data;
	}

	/**
	 * 融雪模型训练数据输入
	 * @param dataList
	 * @param paramsSetVO
	 * @param isTest
	 * @return
	 * @throws Exception
	 */
	public static double[][] inputData_Train_Snow(List<double[]> dataList, ParamsSetVO paramsSetVO, boolean isTest) throws Exception {

		int a = dataList.get(0).length;
		int b = a / 4 * 3;
		int c = a - b;
		int n = paramsSetVO.getInfluence_factor();
		/**
		 * 前n旬流量+平均流量
		 */
//		 一般训练期：检验期=3:1
		double[][] trainData = new double[b- n][n*3 + 2];
		double[][] testData = new double[c- n][n*3 + 2];

		for (int i = 0; i < b - n; i++) {
			trainData[i][0] = dataList.get(0)[i+n];//时间戳
			for (int j = 0; j < n ; j++) {
				trainData[i][j + 1] = dataList.get(1)[i + j];//前N天流量
				trainData[i][j + 4] = dataList.get(3)[i + j];//温度
				trainData[i][j + 7] = dataList.get(4)[i + j];//降水
			}
			trainData[i][n * 3 + 1] = dataList.get(1)[i+n];//预报径流
		}

		for (int i = b; i < a-n; i++) {
			testData[i - b][0] = dataList.get(0)[i+n];//时间戳
			for (int j = 0; j < n ; j++) {
				testData[i - b][j + 1] = dataList.get(1)[i + j];//前N天流量
				testData[i - b][j + 4] = dataList.get(3)[i + j];//温度
				testData[i - b][j + 7] = dataList.get(4)[i + j];//降水
			}
			testData[i - b][n * 3 + 1] = dataList.get(1)[i+n];//预报径流
		}
		if (isTest) {
			return testData;
		} else {
			return trainData;
		}
	}
	/**
	 * 把输出的表格转为临时文件
	 * @param Flood
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public static TemporaryXlsx ObjectToXlsx(Object[][] Flood) throws IOException, InvalidFormatException {
		File tempFile = File.createTempFile("PRE_RESULT",".xlsx");
		String path= tempFile.getAbsolutePath();
		ExcelTool.writeFloodExcel(path, "预报结果", Flood);
		TemporaryXlsx result=new TemporaryXlsx();
		result.setPath(path);
		result.setSheetName("预报结果");
		return result;
	}

	/**
	 * 相同列的Object相加
	 * @param input
	 * @return
	 */
	public static Object[][] AddObject(List<Object[][]> input){
		int n = input.size();
		int rowNum = 0;
		int lineNum= 0;
		for (int i = 0; i < n; i++) {
			rowNum += input.get(i).length;
			if (lineNum<input.get(i)[0].length){
				lineNum=input.get(i)[0].length;
			}
		}
		Object[][] result = new Object[rowNum][lineNum];
		int row = 0;
		for (int i = 0; i < n; i++) {
			Object[][] inObject = input.get(i);
			for (int j = 0; j < inObject.length; j++) {
				for (int k = 0; k < inObject[0].length; k++) {
					result[j+row][k]=inObject[j][k];
				}
			}
			row +=inObject.length;
		}
		return result;
	}

	/**
	 * 获得数据中的年、月、日、小时
	 * @param date
	 * @return
	 */
	public static Map<String, Integer> getSpecificDate(Date date){
		Map<String, Integer> result = new HashMap<>();
		int year;
		int month;
		int day;
		int hour;
		Date time = date;
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH) + 1;
		day = cal.get(Calendar.DAY_OF_MONTH);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		result.put("年",year);
		result.put("月",month);
		result.put("日",day);
		result.put("小时",hour);
		return result;
	}

	/**
	 * 获得丰水期和枯水期的预报开始时间和预报数量
	 * @param param
	 * @return result.get(0)丰水期
	 * result.get(1)枯水期
	 */
	public static List<Object[]> getSelectedData(ForcastInputParam param){
		List<Object[]> result = new ArrayList<>();
		Object[] Feng = new Object[2];
		Object[] Ku = new Object[2];
		Date dateStart = param.getPreStartTime();
		int number = param.getPeriodStepNumber()* param.getPeriodStepSize();
		Date[][] date = new Date[number][1];
		int fengNumber=0;
		int kuNumber=0;
		int month = 0;
		switch (param.getPeriod()) {
			case "月":
				date = getMonthDateList(dateStart,number);
				break;
			case "旬":
				date = getDateList(dateStart, number, 10, 0);
				break;
			case "日":
				date = getDateList(dateStart, number, 1, 0);
				break;
		}
		for (int i = 0; i < number; i++) {
			month=getSpecificDate(date[i][0]).get("月");
			if (month<=9&&month>=5){
				fengNumber++;
			}else {
				kuNumber++;
			}
		}
		month=getSpecificDate(date[0][0]).get("月");
		if (month<=9&&month>=5){
			Feng[0]=date[0];
			for (int i = 0; i < number; i++) {
				month=getSpecificDate(date[i][0]).get("月");
				if (month==10){
					Ku[0]=date[i];
					break;
				}
			}
		}else {
			Ku[0]=date[0];
			for (int i = 0; i < number; i++) {
				month=getSpecificDate(date[i][0]).get("月");
				if (month==5){
					Feng[0]=date[i];
					break;
				}
			}
		}
		Feng[1]=fengNumber;
		Ku[1]=kuNumber;
		result.add(Feng);
		result.add(Ku);
		return result;
	}

	/**
	 * String转Date
	 * @param input
	 * @return
	 */
	public static Date stringToDate (String input){
		Date result =new Date();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		try {
			LocalDateTime dateTime = LocalDateTime.parse(input, formatter);
			ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("Asia/Shanghai"));
			result = Date.from(zonedDateTime.toInstant());
		}catch (DateTimeParseException e) {
			e.printStackTrace();
		}
		return result;
	}
}


