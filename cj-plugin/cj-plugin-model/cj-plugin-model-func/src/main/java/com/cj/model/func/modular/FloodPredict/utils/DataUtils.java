package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.model.func.modular.FloodPredict.entity.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

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
	public static List<List<PredictInputData>> lzzDataConversion(LzzHydrologyParam entity){
		List<List<PredictInputData>> result = new ArrayList<>();
		//3号桥
		List<LzzGaugingStation> THS = entity.getThreeGaugingStation();
		List<PredictInputData> Three = lzzFlowConversion(THS);
		result.add(Three);
		//楼庄子入库
		List<LzzGaugingStation> LZZI = entity.getLzzInput();
		List<PredictInputData> LOUIN = lzzFlowConversion(LZZI);
		result.add(LOUIN);
		//楼庄子出库
		List<LzzGaugingStation> LZZO = entity.getLzzOutput();
		List<PredictInputData> LOUOUT = lzzFlowConversion(LZZO);
		result.add(LOUOUT);
		//喀什沟雨量站
		List<LzzRainfallStation> KSG = entity.getKsgRainfallStation();
		List<PredictInputData> KASHI = lzzRainConversion(KSG);
		result.add(KASHI);
		//黑沟雨量站
		List<LzzRainfallStation> HG = entity.getHgRainfallStation();
		List<PredictInputData> HEIGOU = lzzRainConversion(HG);
		result.add(HEIGOU);
		//煤矿沟雨量站
		List<LzzRainfallStation> MKG = entity.getMkgRainfallStation();
		List<PredictInputData> MEI = lzzRainConversion(MKG);
		result.add(MEI);
		//无名沟雨量站
		List<LzzRainfallStation> WMG = entity.getWmgRainfallStation();
		List<PredictInputData> WUMING = lzzRainConversion(WMG);
		result.add(WUMING);
		//加普沙雨量站
		List<LzzRainfallStation> JPS = entity.getJpsRainfallStation();
		List<PredictInputData> JIA = lzzRainConversion(JPS);
		result.add(JIA);
		//宰尔德雨量站
		List<LzzRainfallStation> ZED = entity.getZrdRainfallStation();
		List<PredictInputData> ZAI = lzzRainConversion(ZED);
		result.add(ZAI);
		//东南沟雨量站
		List<LzzRainfallStation> DNG = entity.getDngRainfallStation();
		List<PredictInputData> DONG = lzzRainConversion(DNG);
		result.add(DONG);
		//八一林场雨量站
		List<LzzRainfallStation> BYLC = entity.getBylcRainfallStation();
		List<PredictInputData> BAYI = lzzRainConversion(BYLC);
		result.add(BAYI);
		//萨尔达万雨量站
		List<LzzRainfallStation> SEDW = entity.getSedwRainfallStation();
		List<PredictInputData> SAER = lzzRainConversion(SEDW);
		result.add(SAER);
		//制材厂雨量站
		List<LzzRainfallStation> ZCC = entity.getZccRainfallStation();
		List<PredictInputData> ZHI = lzzRainConversion(ZCC);
		result.add(ZHI);
		return result;
	}

	/**
	 * 楼庄子上游水位站数据转化
	 * @param input
	 * @return 站点名称、日尺度时间、流量
	 */
	public static List<PredictInputData> lzzFlowConversion(List<LzzGaugingStation> input){
		List<PredictInputData> result = new ArrayList<>();
		double flowSum = 0;
		int flowNum = 0;
		for (int i = 0; i < input.size(); i++) {
			String id = input.get(i).getId();
			// 使用间隔符提取数字部分
			String[] parts = id.split(":");
			String bridgeNumber = parts[0];
			long numericValue= Long.parseLong(parts[1]);
			Date date = new Date(numericValue); // 根据时间戳创建日期对象

			int hour = getSpecificDate(date).get("小时");;
			int hourBefore = 0;
			if(i!=0){
				String id1 = input.get(i-1).getId();
				// 使用间隔符提取数字部分
				String[] parts1 = id1.split(":");
				long numericValue1= Long.parseLong(parts1[1]);
				Date date1 = new Date(numericValue1);
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(date1);
				hourBefore = cal1.get(Calendar.HOUR_OF_DAY);
			}
			if(((hour-hourBefore)<0)){
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
			int hourBefore = 0;
			if(i!=0){
				String id1 = input.get(i-1).getId();
				// 使用间隔符提取数字部分
				String[] parts1 = id1.split(":");
				long numericValue1= Long.parseLong(parts1[1]);
				Date date1 = new Date(numericValue1);
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(date1);
				hourBefore = cal1.get(Calendar.HOUR_OF_DAY);
			}
			if(((hour-hourBefore)<0)){
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
	 * 将雨量站输入转化为模型需要的类型，没有去除空值
	 * @param input
	 * @return 小时尺度站点名、时间、雨量、温度
	 */
	public static List<PredictInputData> lzzRainConversion(List<LzzRainfallStation> input){
		List<PredictInputData> result = new ArrayList<>();
		List<LzzRainfallStation> testL = new ArrayList<>();
		int n = 0;
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).getTemperature()==null){
				testL.add(input.get(i));
				n++;
			}
		}
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
			result.add(piece);
		}
		return result;
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
			if(((hour-hourBefore)<0)){
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
	 * 保留前72小时雨量
	 * @param param(前多少个小时），input原始数据
	 *
	 * @return
	 */
	public static List<PredictInputData> hoursRain(ForcastInputParamNew param,List<PredictInputData> input) {
		List<PredictInputData> result = new ArrayList<>();
		int n = param.getPeriodTimeNum();
		if (input.size()>n){
			for (int i = n; i > 0; i--) {
				result.add(input.get(input.size() - i));
			}
		}else {
			for (int i = 1; i < input.size(); i++) {
				result.add(input.get(input.size() - i));
			}
		}
		return result;
	}
	/**
	 * 保留前20天雨量
	 * @param input
	 * @return
	 */
	public static List<PredictInputData> TwentyDayRain(List<PredictInputData> input) {
		List<PredictInputData> result = new ArrayList<>();
		if (input.size()>20){
			for (int i = 20; i > 0; i--) {
				result.add(input.get(input.size() - i));
			}
		}else {
			for (int i = 1; i < input.size(); i++) {
				result.add(input.get(input.size() - i));
			}
		}

		return result;
	}

	/**
	 * 楼庄子上游雨量站数据整合
	 * @param paramForcastInputParamNew
	 * @return 前期雨量和小时尺度降水
	 */
	public static List<List<PredictInputData>> LzzRainIntegration(ForcastInputParamNew paramForcastInputParamNew){
		List<List<PredictInputData>> result = new ArrayList<>();
		//雨量站整合
		List<PredictInputData> RainHour = new ArrayList<>();
		List<PredictInputData> RainDay = new ArrayList<>();
		//喀什沟
		List<PredictInputData> KSG = DataUtils.lzzDataConversion(paramForcastInputParamNew.getLzzHydrologyParam()).get(3);
		KSG=DataUtils.hoursRain(paramForcastInputParamNew ,KSG);
		List<PredictInputData> KSGDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getKsgRainfallStation());
		KSGDAY=DataUtils.TwentyDayRain(KSGDAY);
		RainDay.addAll(KSGDAY);
		RainHour.addAll(KSG);
		//黑沟
		List<PredictInputData> HG = DataUtils.lzzDataConversion(paramForcastInputParamNew.getLzzHydrologyParam()).get(4);
		HG=DataUtils.hoursRain(paramForcastInputParamNew,HG);
		List<PredictInputData> HGDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getHgRainfallStation());
		HGDAY=DataUtils.TwentyDayRain(HGDAY);
		RainDay.addAll(HGDAY);
		RainHour.addAll(HG);
		//煤矿沟
		List<PredictInputData> MKG = DataUtils.lzzDataConversion(paramForcastInputParamNew.getLzzHydrologyParam()).get(5);
		MKG=DataUtils.hoursRain(paramForcastInputParamNew,MKG);
		List<PredictInputData> MKGDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getMkgRainfallStation());
		MKGDAY=DataUtils.TwentyDayRain(MKGDAY);
		RainDay.addAll(MKGDAY);
		RainHour.addAll(MKG);
		//无名沟
		List<PredictInputData> WMG = DataUtils.lzzDataConversion(paramForcastInputParamNew.getLzzHydrologyParam()).get(6);
		WMG=DataUtils.hoursRain(paramForcastInputParamNew,WMG);
		List<PredictInputData> WMGDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getWmgRainfallStation());
		WMGDAY=DataUtils.TwentyDayRain(WMGDAY);
		RainDay.addAll(WMGDAY);
		RainHour.addAll(WMG);
		//加普沙
		List<PredictInputData> JPS = DataUtils.lzzDataConversion(paramForcastInputParamNew.getLzzHydrologyParam()).get(7);
		JPS=DataUtils.hoursRain(paramForcastInputParamNew,JPS);
		List<PredictInputData> JPSDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getJpsRainfallStation());
		JPSDAY=DataUtils.TwentyDayRain(JPSDAY);
		RainDay.addAll(JPSDAY);
		RainHour.addAll(JPS);
		//宰尔德
		List<PredictInputData> ZED = DataUtils.lzzDataConversion(paramForcastInputParamNew.getLzzHydrologyParam()).get(8);
		ZED=DataUtils.hoursRain(paramForcastInputParamNew,ZED);
		List<PredictInputData> ZEDDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getZrdRainfallStation());
		ZEDDAY=DataUtils.TwentyDayRain(ZEDDAY);
		RainDay.addAll(ZEDDAY);
		RainHour.addAll(ZED);
		//东南沟
		List<PredictInputData> DNG = DataUtils.lzzDataConversion(paramForcastInputParamNew.getLzzHydrologyParam()).get(9);
		DNG=DataUtils.hoursRain(paramForcastInputParamNew,DNG);
		List<PredictInputData> DNGDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getDngRainfallStation());
		DNGDAY=DataUtils.TwentyDayRain(DNGDAY);
		RainDay.addAll(DNGDAY);
		RainHour.addAll(DNG);
		//八一林场
		List<PredictInputData> BYLC = DataUtils.lzzDataConversion(paramForcastInputParamNew.getLzzHydrologyParam()).get(10);
		BYLC=DataUtils.hoursRain(paramForcastInputParamNew,BYLC);
		List<PredictInputData> BYLCDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getBylcRainfallStation());
		BYLCDAY=DataUtils.TwentyDayRain(BYLCDAY);
		RainDay.addAll(BYLCDAY);
		RainHour.addAll(BYLC);
		//萨尔达万
		List<PredictInputData> SEDW = DataUtils.lzzDataConversion(paramForcastInputParamNew.getLzzHydrologyParam()).get(11);
		SEDW=DataUtils.hoursRain(paramForcastInputParamNew,SEDW);
		List<PredictInputData> SEDWDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getSedwRainfallStation());
		SEDWDAY=DataUtils.TwentyDayRain(SEDWDAY);
		RainDay.addAll(SEDWDAY);
		RainHour.addAll(SEDW);
		//制材厂
		List<PredictInputData> ZCC = DataUtils.lzzDataConversion(paramForcastInputParamNew.getLzzHydrologyParam()).get(12);
		ZCC=DataUtils.hoursRain(paramForcastInputParamNew,ZCC);
		List<PredictInputData> ZCCDAY = DataUtils.lzzRainHourToDay(paramForcastInputParamNew.getLzzHydrologyParam().getZccRainfallStation());
		ZCCDAY=DataUtils.TwentyDayRain(ZCCDAY);
		RainDay.addAll(ZCCDAY);
		RainHour.addAll(ZCC);
		//添加小时尺度雨量和日尺度雨量
		result.add(RainHour);
		result.add(RainDay);
		return result;
	}

	/**
	 * 楼庄子入库乘以系数得到区间
	 * @param LZZIN
	 * @return
	 */
	public static List<PredictInputData> Scaling(List<PredictInputData> LZZIN){
		List<PredictInputData> result = new ArrayList<>();
		for (int i = 0; i < LZZIN.size(); i++) {
			PredictInputData tthData = LZZIN.get(i);
			PredictInputData qjData = new PredictInputData();
			qjData.setDates(tthData.getDates());
			qjData.setRainStation("区间");
			qjData.setFlow(tthData.getFlow()*0.058);//0.058为多年平均所得区间来水与头屯河入库的比值
			result.add(qjData);
		}
		return result;
	}

	/**
	 * 区间雨量站整合
	 * @param paramForcastInputParamNew
	 * @return 24小时雨量和20天雨量
	 */
	public static List<List<PredictInputData>> IrrigateRainIntegration(ForcastInputParamNew paramForcastInputParamNew){
		List<List<PredictInputData>> result = new ArrayList<>();
		//雨量站整合
		List<PredictInputData> RainHour = new ArrayList<>();
		List<PredictInputData> RainDay = new ArrayList<>();
		//小渠子
		List<PredictInputData> XQZ = DataUtils.irrigatedDataConversion(paramForcastInputParamNew.getIrrigatedHydrologyParam()).get(1);
		XQZ=DataUtils.hoursRain(paramForcastInputParamNew,XQZ);
		List<PredictInputData> XQZDAY = DataUtils.IrrigateRainHourToDay(paramForcastInputParamNew.getIrrigatedHydrologyParam().getXqzGaugingStation());
		XQZDAY=DataUtils.TwentyDayRain(XQZDAY);
		RainDay.addAll(XQZDAY);
		RainHour.addAll(XQZ);
		//团结一队
		List<PredictInputData> TJYD = DataUtils.irrigatedDataConversion(paramForcastInputParamNew.getIrrigatedHydrologyParam()).get(2);
		TJYD=DataUtils.hoursRain(paramForcastInputParamNew,TJYD);
		List<PredictInputData> TJYDDAY = DataUtils.IrrigateRainHourToDay(paramForcastInputParamNew.getIrrigatedHydrologyParam().getTjydGaugingStation());
		TJYDDAY=DataUtils.TwentyDayRain(TJYDDAY);
		RainDay.addAll(TJYDDAY);
		RainHour.addAll(TJYD);
		//头屯河水库
		List<PredictInputData> TTHR = DataUtils.irrigatedDataConversion(paramForcastInputParamNew.getIrrigatedHydrologyParam()).get(3);
		TTHR=DataUtils.hoursRain(paramForcastInputParamNew,TTHR);
		List<PredictInputData> TTHRDAY = DataUtils.IrrigateRainHourToDay(paramForcastInputParamNew.getIrrigatedHydrologyParam().getTthGaugingStation());
		TTHRDAY=DataUtils.TwentyDayRain(TTHRDAY);
		RainDay.addAll(TTHRDAY);
		RainHour.addAll(TTHR);
		//添加小时尺度雨量和日尺度雨量
		result.add(RainHour);
		result.add(RainDay);
		return result;
	}
	/**
	 *
	 * @param pointData
	 * 各个雨量站点雨量
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
				if (pointData.get(i).getDates()==pointData.get(j).getDates()){
					hourData=pointData.get(i);
					hourDatalist.add(hourData);
				}
			}
			hourDataList.add(hourDatalist);
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
		//三号桥流量异常去除
		List<LzzGaugingStation> THQ = lzzHydrologyParam.getThreeGaugingStation();
		List<LzzGaugingStation> THQresult = new ArrayList<>();
		for (int i = 0; i < THQ.size(); i++) {
			LzzGaugingStation station = THQ.get(i);
			//去除空值数据
			String id = station.getId();
			String[] parts = id.split(":");
			String bridgeNumber = parts[0];
			if (parts.length > 1 &&bridgeNumber.length() > 1 && station.getFlow() != null && station.getFlow() <= 300){
				THQresult.add(THQ.get(i));
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
			if (parts.length > 1 &&bridgeNumber.length() > 1 && station.getFlow() != null && station.getFlow() <= 300){
				LZZresult.add(LZZ.get(i));
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
			if (parts.length > 1 && bridgeNumber.length() > 1 && station.getFlow() != null && station.getFlow() <= 300) {
				LZZOresult.add(LZZO.get(i));
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
			if (parts.length > 1 &&bridgeNumber.length() > 1 && station.getFlow() != null && station.getFlow() <= 300){
				LZZWresult.add(LZZW.get(i));
			}
		}
		lzzHydrologyParam.setLzzWaterLevel(LZZWresult);
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
		for (int i = 0; i < inputData.size(); i++) {
			if (inputData.get(i).getTemperature() == null){
				int n = 0 ;
				for (int j = 0; i+j<inputData.size(); j++) {
					if ((inputData.get(i+j).getTemperature() == null)){
						n++;
					}else break;
				}
				if (i + n < inputData.size()){
					BigDecimal T = inputData.get(i+n).getTemperature();
					inputData.get(i).setTemperature(T);
				}else {
					BigDecimal T = inputData.get(i-1).getTemperature();
					inputData.get(i).setTemperature(T);
				}
			}
		}
		for (int i = 0; i < inputData.size() ; i++) {
			//去除空值异常
			String id = inputData.get(i).getId();
			String[] parts = id.split(":");
			String bridgeNumber = parts[0];
			if (parts.length > 1 && bridgeNumber.length() > 1){
				result.add(inputData.get(i));
			}
		}
		return result;
	}

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
	 * @param WaterStation 水位站数据
	 * @param paramForcastInputParamNew 从数据库中获得的数据
	 * @return 日尺度的添加了温度和降水的水位站数据
	 */
	public static List<PredictInputData> AddRAndT(List<PredictInputData> WaterStation, ForcastInputParamNew paramForcastInputParamNew){
		List<PredictInputData> result = new ArrayList<>();
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
		for (int i = 0; i < WaterStation.size(); i++) {
			for (int j = 0; j < RAT.size(); j++) {
				if (WaterStation.get(i).getDates()==RAT.get(j).getDates()){
					double rain = RAT.get(j).getRainfall();
					WaterStation.get(i).setRainfall(rain);
					double temperature = RAT.get(j).getTemperature();
					WaterStation.get(i).setTemperature(temperature);
				}
			}
			result.add(WaterStation.get(i));
		}
		return result;
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
				if(i!=0){
					Date time2 = data.get(i-1).getDates();
					dayBefore = getSpecificDate(time2).get("日");
				}
				if(day==11||day==21||((day-dayBefore)<0)){
					double flowY =flowSum / flowNum;
					double temperatureY =temperatureSum / temperatureNum;
					double rainfallY =rainfallSum / rainfallNum;
					PredictInputData piece = new PredictInputData();
					piece.setDates(time);
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
				if(i!=0){
					Date time2 = data.get(i-1).getDates();
					dayBefore = getSpecificDate(time2).get("日");
				}

				if(((day-dayBefore)<0)){
					double flowY =flowSum / flowNum;
					double temperatureY =temperatureSum / temperatureNum;
					double rainfallY =rainfallSum / rainfallNum;
					PredictInputData piece = new PredictInputData();
					piece.setDates(time);
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
}


