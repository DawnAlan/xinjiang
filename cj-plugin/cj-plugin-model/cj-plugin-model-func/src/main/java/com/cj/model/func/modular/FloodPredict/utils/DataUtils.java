package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.model.func.modular.FloodPredict.entity.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 数据处理方法
 *
 * @author leileilei
 */
public class DataUtils {
    TimeUtils timeUtils = new TimeUtils();
    InputUtils inputUtils = new InputUtils();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 从数据库导入的数据进行处理，包括对三号桥、楼庄子进库站异常流量的处理，上游雨量站温度空值的处理
     */
    public ForecastInputParamNew emptyProcessing(ForecastInputParamNew result) {
        //输入数据的转化
        Date predictionTime = result.getPredictionTime();
        Date nowDate = new Date();
        Date endTime = timeUtils.addCalendar(predictionTime, "小时", result.getPeriodTimeNum() * result.getPeriodTimeStep());
        LzzHydrologyParam lzzHydrologyParam = result.getLzzHydrologyParam();
        /**
         * 水位站数据的前期处理
         */
        //三号桥流量异常去除
        List<LzzGaugingStation> THQ = lzzHydrologyParam.getThreeGaugingStation();
        if (THQ.isEmpty()) {
            throw new RuntimeException("未获取数据库中三号桥当前预报日期流量");
        }
        List<LzzGaugingStation> THQresult = new ArrayList<>();
        for (int i = 0; i < THQ.size(); i++) {
            LzzGaugingStation station = THQ.get(i);
            //去除空值数据
            String id = station.getId();
            String[] parts = id.split(":");
            String bridgeNumber = parts[0];
            long numericValue = Long.parseLong(parts[1]);
            Date date = new Date(numericValue); // 根据时间戳创建日期对象
            int month = timeUtils.getSpecificDate(date).get("月");
            if (parts.length > 1 && bridgeNumber.length() > 1) {
                if (month <= 6 || month >= 9) {
                    if (station.getFlow() != null && station.getFlow() <= 100) {
                        THQresult.add(THQ.get(i));
                    }
                } else {
                    if (station.getFlow() != null && station.getFlow() <= 300) {
                        THQresult.add(THQ.get(i));
                    }
                }
            }
        }
        lzzHydrologyParam.setThreeGaugingStation(THQresult);
        //楼庄子进库站流量去除异常
        List<LzzGaugingStation> LZZ = lzzHydrologyParam.getLzzInput();
        if (LZZ.isEmpty()) {
            Date time = result.getPredictionTime();
            for (int i = 0; i < 480; i++) {
                LzzGaugingStation empty = new LzzGaugingStation();
                empty.setFlow(0.0);
                empty.setStationName("楼庄子入库水位站");
                Calendar calendar = Calendar.getInstance();
                Date currentDate = time;
                calendar.setTime(currentDate);
                calendar.add(Calendar.HOUR_OF_DAY, (i - 480));
                empty.setGatherTime(calendar.getTime());
                long timestampInMilliseconds = calendar.getTime().toInstant().toEpochMilli();
                empty.setId("楼庄子入库水位站:" + timestampInMilliseconds);
                LZZ.add(empty);
            }
        }
        List<LzzGaugingStation> LZZresult = new ArrayList<>();
        for (int i = 0; i < LZZ.size(); i++) {
            LzzGaugingStation station = LZZ.get(i);
            String id = station.getId();
            String[] parts = id.split(":");
            String bridgeNumber = parts[0];
            long numericValue = Long.parseLong(parts[1]);
            Date date = new Date(numericValue); // 根据时间戳创建日期对象
            int month = timeUtils.getSpecificDate(date).get("月");
            if (parts.length > 1 && bridgeNumber.length() > 1) {
                if (month <= 6 || month >= 9) {
                    if (station.getFlow() != null && station.getFlow() <= 100) {
                        LZZresult.add(LZZ.get(i));
                    }
                } else {
                    if (station.getFlow() != null && station.getFlow() <= 300) {
                        LZZresult.add(LZZ.get(i));
                    }
                }
            }
        }
        lzzHydrologyParam.setLzzInput(LZZresult);
        /**
         * 雨量站数据的前期处理
         */
        //雨量站的处理温度数据为空则匹配下一个温度
        List<LzzRainfallStation> KSG = lzzHydrologyParam.getKsgRainfallStation();
        KSG = lzzTemProcessing(KSG);
        lzzHydrologyParam.setKsgRainfallStation(KSG);//喀什沟
        List<LzzRainfallStation> HG = lzzHydrologyParam.getHgRainfallStation();
        HG = lzzTemProcessing(HG);
        lzzHydrologyParam.setHgRainfallStation(HG);//黑沟
        List<LzzRainfallStation> MKG = lzzHydrologyParam.getMkgRainfallStation();
        MKG = lzzTemProcessing(MKG);
        lzzHydrologyParam.setMkgRainfallStation(MKG);//煤矿沟
        List<LzzRainfallStation> WMG = lzzHydrologyParam.getWmgRainfallStation();
        WMG = lzzTemProcessing(WMG);
        lzzHydrologyParam.setWmgRainfallStation(WMG);//无名沟
        List<LzzRainfallStation> JPS = lzzHydrologyParam.getJpsRainfallStation();
        JPS = lzzTemProcessing(JPS);
        lzzHydrologyParam.setJpsRainfallStation(JPS);//加普沙
        List<LzzRainfallStation> ZED = lzzHydrologyParam.getZrdRainfallStation();
        ZED = lzzTemProcessing(ZED);
        lzzHydrologyParam.setZrdRainfallStation(ZED);//宰尔德
        List<LzzRainfallStation> DNG = lzzHydrologyParam.getDngRainfallStation();
        DNG = lzzTemProcessing(DNG);
        lzzHydrologyParam.setDngRainfallStation(DNG);//东南沟
        List<LzzRainfallStation> BYLC = lzzHydrologyParam.getBylcRainfallStation();
        BYLC = lzzTemProcessing(BYLC);
        lzzHydrologyParam.setBylcRainfallStation(BYLC);//八一林场
        List<LzzRainfallStation> SEDW = lzzHydrologyParam.getSedwRainfallStation();
        SEDW = lzzTemProcessing(SEDW);
        lzzHydrologyParam.setSedwRainfallStation(SEDW);//萨尔达万
        List<LzzRainfallStation> ZCC = lzzHydrologyParam.getZccRainfallStation();
        ZCC = lzzTemProcessing(ZCC);
        lzzHydrologyParam.setZccRainfallStation(ZCC);//制材厂
        result.setLzzHydrologyParam(lzzHydrologyParam);
        //雨情预报数据处理
        if (result.getIsSimulation() == null) {
            result.setIsSimulation(false);
        }
        if (result.getModelType() == 3 && endTime.after(nowDate) && result.getRainFallDtos().isEmpty()) {
            throw new RuntimeException("未获得预报降雨数据");
        } else {
            result.setRainFallDtos(result.getRainFallDtos());
        }
        if (result.getInflowRunoffs() == null || result.getInflowRunoffs().isEmpty()) {
            throw new RuntimeException("未获得A3表中进库流量数据");
        }
        /**
         * 区间数据的前期处理
         */
        //区间数据站点名空值处理
        IrrigatedHydrologyParam irrigatedHydrologyParam = result.getIrrigatedHydrologyParam();
        irrigatedHydrologyParam = irrigateStationProcessing(irrigatedHydrologyParam);
        result.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
        return result;
    }

    /**
     * 预报降水小时转日
     *
     * @param input
     * @return
     */
    public List<PredictInputData> preRainHourToDay(List<RainFallDto> input) throws ParseException {
        List<PredictInputData> result = new ArrayList<>();
        double rainfallSum = 0.0;
        int rainfallNum = 0;
        for (int i = 0; i < input.size(); i++) {
            Date date = sdf.parse(input.get(i).getDate()); // 小时尺度时间
            int hour = timeUtils.getSpecificDate(date).get("小时");
            int day = timeUtils.getSpecificDate(date).get("日");
            int hourBefore = 0;
            String station1 = input.get(0).getArea();
            int day1 = day;
            if (i != 0) {
                Date date1 = sdf.parse(input.get(i - 1).getDate());
                hourBefore = timeUtils.getSpecificDate(date1).get("小时");
                day1 = timeUtils.getSpecificDate(date1).get("日");
                station1 = input.get(i - 1).getArea();
            }
            String station = input.get(i).getArea();
            if (((hour - hourBefore) < 0 || day != day1) && station.equals(station1)) {

                double rainfallY = (rainfallSum / rainfallNum);
                PredictInputData piece = new PredictInputData();
                piece.setLocation(input.get(i).getArea());
                piece.setDates(date);
                piece.setTemperature(12.0);
                piece.setRainfall(rainfallY);
                result.add(piece);

                rainfallSum = 0.0;
                rainfallNum = 0;

            }
            if (input.get(i).getRainFall() != null) {
                rainfallSum = rainfallSum + input.get(i).getRainFall();
                rainfallNum = rainfallNum + 1;
            }
        }
        return result;
    }

    /**
     * 雨量信息，包括了前n小时落地雨和后期预报雨量
     *
     * @return
     */
    public List<PredictInputData> getHoursRain(ForecastInputParamNew param, List<PredictInputData> input) throws ParseException {
        List<PredictInputData> result = new ArrayList<>();
        PredictInputData data = new PredictInputData();

        //获得开始时间和结束时间，分情况判断
        Date dateStart = param.getPredictionTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStart);
        calendar.add(Calendar.HOUR_OF_DAY, -inputUtils.beforeHours);
        dateStart = calendar.getTime();//找到落地雨前n小时
        int n = inputUtils.beforeHours + param.getPeriodTimeNum() * param.getPeriodTimeStep();//需要预报的时间长度
        calendar.add(Calendar.HOUR_OF_DAY, n);
        Date dataEnd = calendar.getTime();//预报结束时间
        String station = input.get(0).getLocation();
        List<RainFallDto> rainPre = param.getRainFallDtos();
        if (param.getIsSimulation() == null) {
            param.setIsSimulation(false);
        }
        if (param.getIsSimulation()) {
            List<RainFallDto> oneStationRain = new ArrayList<>();
            for (int i = 0; i < rainPre.size(); i++) {
                if (station.equals(rainPre.get(i).getArea())) {
                    oneStationRain.add(rainPre.get(i));
                }
            }
            for (int j = 0; j < n; j++) {
                for (int i = 0; i < oneStationRain.size(); i++) {
                    Date date = sdf.parse(oneStationRain.get(i).getDate());
                    Boolean dateCompare = timeUtils.DateCompare(dateStart, date, "小时");
                    if (dateCompare)//日期相等并且地点相等才能赋值
                    {
                        data.setLocation(input.get(0).getLocation());
                        data.setDates(dateStart);
                        data.setTemperature(24.0);
                        data.setRainfall(oneStationRain.get(i).getRainFall());
                        break;
                    } else {
                        data = assignmentNullRAndT(dateStart, input.get(0).getLocation());
                    }
                }
                calendar.setTime(dateStart);
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                dateStart = calendar.getTime();
                result.add(data);
            }
        } else {
            //找到最贴近的时间
            List<Date> dateList = new ArrayList<>();
            for (PredictInputData predictInputData : input) {
                dateList.add(predictInputData.getDates());
            }
            int d = timeUtils.findNearestTime(dateList, dateStart);
            Date dateFind = input.get(d).getDates();

            int end_inputEnd = timeUtils.duration(dataEnd, input.get(input.size() - 1).getDates(), "小时");
            if (end_inputEnd > 0)//预报结束时间在数据库中有，也就是全部读取历史数据
            {
                //此时的dateFind是历史数据中与开始预报时间最接近的
                for (int i = 0; i < n; i++) {
                    for (int j = 0; d + j < input.size() && j < n; j++) {
                        Boolean dateCompare = timeUtils.DateCompare(dateStart, input.get(d + j).getDates(), "小时");
                        if (dateCompare) {
                            data = input.get(d + j);
                            break;
                        } else {
                            data = assignmentNullRAndT(dateStart, input.get(0).getLocation());
                        }
                    }
                    calendar.setTime(dateStart);
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                    dateStart = calendar.getTime();
                    result.add(data);
                }
            } else //预报结束时间在数据库中没有，也就是需要读取预报雨量
            {
                int start_inputEnd = timeUtils.duration(dateStart, input.get(input.size() - 1).getDates(), "小时");
                int length = param.getRainFallDtos().size();
                rainPre = param.getRainFallDtos();
                station = input.get(0).getLocation();
                if (start_inputEnd < 0)//预报开始时间在数据库中没有，也就是全部读取预报值
                {
                    for (int i = 0; i < n; i++) {
                        if (length > 0)//有预报值
                        {
                            for (int j = 0; j < length; j++) {
                                Date date = sdf.parse(rainPre.get(j).getDate());
                                Boolean dateCompare = timeUtils.DateCompare(dateStart, date, "小时");
                                if (rainPre.get(0).getArea().equals("面雨量")) {
                                    if (dateCompare)//日期相等
                                    {
                                        data.setLocation("面雨量");
                                        data.setDates(dateStart);
                                        data.setTemperature(rainPre.get(j).getTemperature());
                                        data.setRainfall(rainPre.get(j).getRainFall());
                                        break;
                                    } else {
                                        data = assignmentNullRAndT(dateStart, "面雨量");
                                    }
                                } else {
                                    if (dateCompare && station.equals(rainPre.get(j).getArea()))//日期相等并且地点相等才能赋值
                                    {
                                        data.setLocation(input.get(0).getLocation());
                                        data.setDates(dateStart);
                                        data.setTemperature(rainPre.get(j).getTemperature());
                                        data.setRainfall(rainPre.get(j).getRainFall());
                                        break;
                                    } else {
                                        data = assignmentNullRAndT(dateStart, input.get(0).getLocation());
                                    }
                                }
                            }
                        } else //没有预报值，数据库中也没有数据
                        {
                            data = assignmentNullRAndT(dateStart, input.get(0).getLocation());
                        }
                        calendar.setTime(dateStart);
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        dateStart = calendar.getTime();
                        result.add(data);
                    }
                } else //预报开始时间在数据库内，预报结束时间不在数据库内
                {
                    for (int i = 0; i < start_inputEnd; i++) //从落地雨开始给其赋值到数据库末尾
                    {
                        for (int j = 0; d + j < input.size() && j < n; j++) {
                            Boolean dateCompare = timeUtils.DateCompare(dateStart, input.get(d + j).getDates(), "小时");
                            if (dateCompare) {
                                data = input.get(d + j);
                                break;
                            } else {
                                data = assignmentNullRAndT(dateStart, input.get(0).getLocation());
                            }
                        }
                        calendar.setTime(dateStart);
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        dateStart = calendar.getTime();
                        result.add(data);
                    }
                    //此时的dataStart==数据库末尾的时间
                    int inputEnd_dateEnd = timeUtils.duration(dateStart, dataEnd, "小时");//数据库末尾到预报结束时间的距离
                    for (int i = 0; i < inputEnd_dateEnd; i++) {
                        if (length > 0) {
                            for (int j = 0; j < length; j++) {
                                Date date = sdf.parse(rainPre.get(j).getDate());
                                Boolean dateCompare = timeUtils.DateCompare(dateStart, date, "小时");
                                if (rainPre.get(0).getArea().equals("面雨量")) {
                                    if (dateCompare)//日期相等
                                    {
                                        data.setLocation(station);
                                        data.setDates(dateStart);
                                        data.setTemperature(rainPre.get(j).getTemperature());
                                        data.setRainfall(rainPre.get(j).getRainFall());
                                        break;
                                    } else {
                                        data = assignmentNullRAndT(dateStart, station);
                                    }
                                } else {
                                    if (dateCompare && station.equals(rainPre.get(j).getArea()))//日期相等并且地点相等才能赋值
                                    {
                                        data.setLocation(station);
                                        data.setDates(dateStart);
                                        data.setTemperature(rainPre.get(j).getTemperature());
                                        data.setRainfall(rainPre.get(j).getRainFall());
                                        break;
                                    } else {
                                        data = assignmentNullRAndT(dateStart, input.get(0).getLocation());
                                    }
                                }

                            }
                        } else {
                            data = assignmentNullRAndT(dateStart, input.get(0).getLocation());
                        }
                        calendar.setTime(dateStart);
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        dateStart = calendar.getTime();
                        result.add(data);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 保留前20天雨量
     *
     * @param input
     * @return
     */
    public List<PredictInputData> getTwentyDaysRain(ForecastInputParamNew param, List<PredictInputData> input) throws ParseException {
        List<PredictInputData> result = new ArrayList<>();
        Date dateStart = param.getPredictionTime();
        PredictInputData data = new PredictInputData();
        List<Date> dateList = new ArrayList<>();
        for (PredictInputData predictInputData : input) {
            dateList.add(predictInputData.getDates());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStart);
        calendar.add(Calendar.DAY_OF_MONTH, -inputUtils.beforeDays);
        Date dateStart_20 = calendar.getTime();//找到前二十天
        Date inputDateEnd = input.get(input.size() - 1).getDates();//数据库中最新时间
        int d = timeUtils.findNearestTime(dateList, dateStart_20);//找到最贴近的时间
        int start_End = timeUtils.duration(dateStart, inputDateEnd, "日");
        if (start_End > 0)//预报时间在数据库内全为历史值
        {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; d + j < input.size() && j < 20; j++) {
                    Boolean dateCompare = timeUtils.DateCompare(dateStart_20, input.get(d + j).getDates(), "日");
                    if (dateCompare) {
                        data = input.get(d + j);
                        break;
                    } else {
                        data = assignmentNullRAndT(dateStart, input.get(0).getLocation());
                    }
                }
                calendar.setTime(dateStart_20);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                dateStart_20 = calendar.getTime();
                result.add(data);
            }
        } else //预报开始时间在数据库外，又可以分为前二十天都不在数据库或者不都在数据库
        {
            List<RainFallDto> rainFallDtoList = param.getRainFallDtos();
            List<PredictInputData> preRainDay = preRainHourToDay(rainFallDtoList);
            int start_20_End = timeUtils.duration(dateStart_20, inputDateEnd, "日");
            if (start_20_End < 0)//全部为预报值
            {
                for (int i = 0; i < 20; i++) {
                    if (!preRainDay.isEmpty()) {
                        for (PredictInputData predictInputData : preRainDay) {
                            Date date = predictInputData.getDates();
                            Boolean dateCompare = timeUtils.DateCompare(dateStart_20, date, "日");
                            if (dateCompare && predictInputData.getLocation().equals(input.get(0).getLocation()))//日期和站点都相等才能赋值
                            {
                                data = predictInputData;
                                break;
                            } else {
                                data = assignmentNullRAndT(dateStart_20, input.get(0).getLocation());
                            }
                        }
                    } else {
                        data = assignmentNullRAndT(dateStart_20, input.get(0).getLocation());
                    }
                    calendar.setTime(dateStart_20);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    dateStart_20 = calendar.getTime();
                    result.add(data);
                }
            } else //二十天一部分历史，一部分预报
            {
                int start_20_inputEnd = timeUtils.duration(dateStart_20, inputDateEnd, "日");
                for (int i = 0; i < start_20_inputEnd; i++) {
                    for (int j = 0; d + j < input.size() && j < 20; j++) {
                        Boolean dateCompare = timeUtils.DateCompare(dateStart_20, input.get(d + j).getDates(), "日");
                        if (dateCompare) {
                            data = input.get(d + j);
                            break;
                        } else {
                            data = assignmentNullRAndT(dateStart_20, input.get(0).getLocation());
                        }
                    }
                    calendar.setTime(dateStart_20);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    dateStart_20 = calendar.getTime();
                    result.add(data);
                }
                int inputEnd_Start = timeUtils.duration(inputDateEnd, dateStart, "日");
                for (int i = 0; i < inputEnd_Start; i++) {
                    if (!preRainDay.isEmpty()) {
                        for (PredictInputData predictInputData : preRainDay) {
                            Date date = predictInputData.getDates();
                            Boolean dateCompare = timeUtils.DateCompare(dateStart_20, date, "日");
                            if (dateCompare && predictInputData.getLocation().equals(input.get(0).getLocation()))//日期相等才能赋值
                            {
                                data = predictInputData;
                                break;
                            } else {
                                data = assignmentNullRAndT(dateStart_20, input.get(0).getLocation());
                            }
                        }
                    } else {
                        data = assignmentNullRAndT(dateStart_20, input.get(0).getLocation());
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
     * 各个雨量站点雨量
     *
     * @param pointData
     * @return surfaceData
     * 该流域的面雨量
     */
    public List<PredictInputData> pointToSurface(List<PredictInputData> pointData, String period, String location) {
        List<PredictInputData> result = new ArrayList<>();
        if (pointData.get(0).getLocation().equals("面雨量") && period.equals("小时"))//预报小时尺度转为面雨量
        {
            Date start = pointData.get(0).getDates();
            result.add(pointData.get(0));
            for (int i = 1; i < pointData.size(); i++) {//把十相同部分的面雨量化简为一个
                Date time = pointData.get(i).getDates();
                Boolean dateCompare = timeUtils.DateCompare(start, time, "小时");
                if (!dateCompare) {
                    result.add(pointData.get(i));
                } else {
                    break;
                }
            }
        } else {
            String stationName = pointData.get(0).getLocation();
            //number为时段数量
            int number = 0;
            for (int i = 0; i < pointData.size(); i++) {
                if (pointData.get(i).getLocation().equals(stationName)) {
                    number++;
                }
            }
            PredictInputData hourData;
            List<PredictInputData> hourDatalist = new ArrayList<>();
            List<List<PredictInputData>> hourDataList = new ArrayList<>();
            //按时间排序，划分为同一时段不同雨量站的List
            for (int j = 0; j < number; j++) {
                for (int i = 0; i < pointData.size(); i++) {
                    Boolean dateCompare = timeUtils.DateCompare(pointData.get(j).getDates(), pointData.get(i).getDates(), period);
                    if (dateCompare) {
                        hourData = pointData.get(i);
                        hourDatalist.add(hourData);
                    }
                }
                hourDataList.add(hourDatalist);
                hourDatalist = new ArrayList<>();
            }
            for (int i = 0; i < number; i++) {
                PredictInputData hourResult = new PredictInputData();
                double rainFall = 0.0;
                double temperature = 0.0;
                hourDatalist = hourDataList.get(i);
                hourResult.setDates(hourDatalist.get(0).getDates());
                //hourDatalist为同一时间段不同雨量站,hourDatalist.size()为雨量站数量
                for (int j = 0; j < hourDatalist.size(); j++) {
                    if (location.equals("3号桥")) {
                        if (hourDatalist.get(j).getLocation().equals("八一林场自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.344401;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("加普沙自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.147571;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("东南沟自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.156022;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("宰尔德自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.042438;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("无名沟自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.019251;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("萨尔达万自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.024912;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("煤矿沟自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.018891;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                    } else if (location.equals("楼庄子")) {
                        if (hourDatalist.get(j).getLocation().equals("八一林场自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.344401;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("加普沙自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.147571;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("东南沟自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.156022;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("宰尔德自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.042438;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("无名沟自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.019251;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("萨尔达万自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.018891;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("煤矿沟自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.029744;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("黑沟自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.044157;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("喀什沟自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.082419;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("制材厂自动雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.115105;
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                    } else {
                        //区间
                        if (hourDatalist.get(j).getLocation().equals("小渠子雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.284;
                            if (hourDatalist.get(j).getTemperature() == null) {
                                hourDatalist.get(j).setTemperature(0.0);
                            }
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("团结一队雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.1948;
                            if (hourDatalist.get(j).getTemperature() == null) {
                                hourDatalist.get(j).setTemperature(0.0);
                            }
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                        if (hourDatalist.get(j).getLocation().equals("头屯河水库雨量站")) {
                            rainFall += hourDatalist.get(j).getRainfall() * 0.51188;
                            if (hourDatalist.get(j).getTemperature() == null) {
                                hourDatalist.get(j).setTemperature(0.0);
                            }
                            temperature += hourDatalist.get(j).getTemperature();
                        }
                    }
                }
                temperature = temperature / hourDatalist.size();
                hourResult.setLocation("面雨量");
                hourResult.setRainfall(rainFall);
                hourResult.setTemperature(temperature);
                result.add(hourResult);
            }
        }
        return result;
    }

    /**
     * 楼庄子数据库数据处理
     */

    /**
     * 将楼庄子输入数据转化为PredictInputData格式
     *
     * @param entity 楼庄子上游数据
     * @return result.get(0)三号桥日尺度站点+时间+径流
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
    public List<List<PredictInputData>> lzzDataConversion(ForecastInputParamNew entity) {
        List<List<PredictInputData>> result = new ArrayList<>();
        Date dateStart = entity.getDataStartTime();
        Date dateEnd = entity.getPredictionTime();
        //3号桥
        List<LzzGaugingStation> THS = entity.getLzzHydrologyParam().getThreeGaugingStation();
        List<PredictInputData> Three = lzzFlowConversion(dateStart, dateEnd, THS);
        result.add(Three);
        //楼庄子入库
        List<LzzGaugingStation> LZZI = entity.getLzzHydrologyParam().getLzzInput();
        List<PredictInputData> LOUIN = lzzFlowConversion(dateStart, dateEnd, LZZI);
        result.add(LOUIN);
        //楼庄子出库
        List<LzzGaugingStation> LZZO = entity.getLzzHydrologyParam().getLzzOutput();
        List<PredictInputData> LOUOUT = lzzFlowConversion(dateStart, dateEnd, LZZO);
        result.add(LOUOUT);
        if (entity.getModelType() == 3) {
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
     * 楼庄子上游流量数据转化
     *
     * @param input
     * @return 站点名称、日尺度时间、流量
     * （选择时间为2023年及以后则返回每一天的值，23年以前之间返回原始数据不做其他处理）
     */
    public List<PredictInputData> lzzFlowConversion(Date dateStart, Date dateEnd, List<LzzGaugingStation> input) {
        List<PredictInputData> result = new ArrayList<>();
        double flowSum = 0;
        int flowNum = 0;
        int yearEnd = timeUtils.getSpecificDate(dateEnd).get("年");
        for (int i = 0; i < input.size(); i++) {
            String id = input.get(i).getId();
            // 使用间隔符提取数字部分
            String[] parts = id.split(":");
            String bridgeNumber = parts[0];
            long numericValue = Long.parseLong(parts[1]);
            Date date = new Date(numericValue); // 根据时间戳创建日期对象
            int year = timeUtils.getSpecificDate(date).get("年");
            int day = timeUtils.getSpecificDate(date).get("日");
            int hour = timeUtils.getSpecificDate(date).get("小时");
            int hourBefore = 0;
            int dayBefore = day;
            if (i != 0) {
                String id1 = input.get(i - 1).getId();
                // 使用间隔符提取数字部分
                String[] parts1 = id1.split(":");
                long numericValue1 = Long.parseLong(parts1[1]);
                Date date1 = new Date(numericValue1);
                hourBefore = timeUtils.getSpecificDate(date1).get("小时");
                dayBefore = timeUtils.getSpecificDate(date1).get("日");
            }
            if (((hour - hourBefore) < 0) || day != dayBefore) {
                if (flowNum == 0) {
                    flowNum = 1;
                }
                double flowY = flowSum / flowNum;
                PredictInputData piece = new PredictInputData();
                piece.setLocation(bridgeNumber);
                piece.setDates(date);
                piece.setFlow(flowY);
                result.add(piece);
                flowSum = 0;
                flowNum = 0;

            }
            if (input.get(i).getFlow() != null) {
                flowSum = flowSum + input.get(i).getFlow();
                flowNum = flowNum + 1;
            }

        }
        /**
         * 保证数据连续性
         */
        List<PredictInputData> resultEnd = new ArrayList<>();
        if (yearEnd >= 2023) {
            // 计算相差天数并返回
            int n = timeUtils.duration(dateStart, dateEnd, "日");
            for (int i = 0; i < n; i++) {
                PredictInputData data = new PredictInputData();
                for (int j = 0; j < result.size(); j++) {
                    Date date = result.get(j).getDates();
                    Boolean dateCompare = timeUtils.DateCompare(date, dateStart, "日");
                    if (dateCompare) {
                        data = result.get(j);
                    } else {
                        data.setLocation(result.get(0).getLocation());
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
            resultEnd = lzzFlowError(resultEnd);
            return resultEnd;
        } else {
            return result;
        }
    }

    /**
     * 对于流量为0或其他的异常值进行处理
     *
     * @param input
     * @return
     */
    public List<PredictInputData> lzzFlowError(List<PredictInputData> input) {
        List<PredictInputData> result = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).getFlow() == 0) {
                Date date = input.get(i).getDates();
                int month = timeUtils.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        input.get(i).setFlow(1.29);
                        break;
                    case 2:
                        input.get(i).setFlow(1.19);
                        break;
                    case 3:
                        input.get(i).setFlow(1.77);
                        break;
                    case 4:
                        input.get(i).setFlow(2.78);
                        break;
                    case 5:
                        input.get(i).setFlow(8.13);
                        break;
                    case 6:
                        input.get(i).setFlow(17.92);
                        break;
                    case 7:
                        input.get(i).setFlow(22.05);
                        break;
                    case 8:
                        input.get(i).setFlow(16.13);
                        break;
                    case 9:
                        input.get(i).setFlow(7.84);
                        break;
                    case 10:
                        input.get(i).setFlow(3.85);
                        break;
                    case 11:
                        input.get(i).setFlow(2.23);
                        break;
                    case 12:
                        input.get(i).setFlow(1.52);
                        break;
                }
            }
            //去除枯水月份过大流量
            Date date = input.get(i).getDates();
            int month = timeUtils.getSpecificDate(date).get("月");
            if (month <= 2 || month >= 10) {
                if (input.get(i).getFlow() >= 10) {
                    switch (month) {
                        case 1:
                            input.get(i).setFlow(1.29);
                            break;
                        case 2:
                            input.get(i).setFlow(1.19);
                            break;
                        case 3:
                            input.get(i).setFlow(1.77);
                            break;
                        case 4:
                            input.get(i).setFlow(2.78);
                            break;
                        case 5:
                            input.get(i).setFlow(8.13);
                            break;
                        case 6:
                            input.get(i).setFlow(17.92);
                            break;
                        case 7:
                            input.get(i).setFlow(22.05);
                            break;
                        case 8:
                            input.get(i).setFlow(16.13);
                            break;
                        case 9:
                            input.get(i).setFlow(7.84);
                            break;
                        case 10:
                            input.get(i).setFlow(3.85);
                            break;
                        case 11:
                            input.get(i).setFlow(2.23);
                            break;
                        case 12:
                            input.get(i).setFlow(1.52);
                            break;
                    }
                }
            }
        }
        result = input;
        return result;
    }

    /**
     * 将雨量站输入转化为模型需要的类型，没有去除空值
     * (后续更改）目前返回21年至今所有小时的数据
     *
     * @param input
     * @return 小时尺度站点名、时间、雨量、温度
     */
    public List<PredictInputData> lzzRainConversion(List<LzzRainfallStation> input) {
        List<PredictInputData> resultMid = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            String id = input.get(i).getId();
            // 使用间隔符提取数字部分
            String[] parts = id.split(":");
            String bridgeNumber = parts[0];
            long numericValue = Long.parseLong(parts[1]);
            Date date = new Date(numericValue); // 根据时间戳创建日期对象
            //储存相应数据
            PredictInputData piece = new PredictInputData();
            piece.setLocation(bridgeNumber);
            piece.setDates(date);
            piece.setRainfall(input.get(i).getRainfall().doubleValue());
            piece.setTemperature(input.get(i).getTemperature().doubleValue());
            resultMid.add(piece);
        }
        return resultMid;
    }

    /**
     * 楼庄子上游雨量站小时尺度转日尺度
     *
     * @param input
     * @return 站点名称、日尺度时间、降水、温度
     */
    public List<PredictInputData> lzzRainHourToDay(List<LzzRainfallStation> input) {
        List<PredictInputData> result = new ArrayList<>();
        BigDecimal temperatureSum = BigDecimal.valueOf(0);
        BigDecimal rainfallSum = BigDecimal.valueOf(0);
        BigDecimal temperatureNum = BigDecimal.valueOf(0);
        BigDecimal rainfallNum = BigDecimal.valueOf(0);
        for (int i = 0; i < input.size(); i++) {
            String id = input.get(i).getId();
            // 使用间隔符提取数字部分
            String[] parts = id.split(":");
            String bridgeNumber = parts[0];
            long numericValue = Long.parseLong(parts[1]);
            Date date = new Date(numericValue); // 根据时间戳创建日期对象
            int hour = timeUtils.getSpecificDate(date).get("小时");
            int day = timeUtils.getSpecificDate(date).get("日");
            int hourBefore = 0;
            int day1 = day;
            if (i != 0) {
                String id1 = input.get(i - 1).getId();
                // 使用间隔符提取数字部分
                String[] parts1 = id1.split(":");
                long numericValue1 = Long.parseLong(parts1[1]);
                Date date1 = new Date(numericValue1);
                hourBefore = timeUtils.getSpecificDate(date1).get("小时");
                day1 = timeUtils.getSpecificDate(date1).get("日");
            }
            if (((hour - hourBefore) < 0 || day != day1)) {
                BigDecimal temperatureY = BigDecimal.valueOf(temperatureSum.doubleValue() / (temperatureNum.doubleValue()));
                BigDecimal rainfallY = BigDecimal.valueOf(rainfallSum.doubleValue());
                String id1 = input.get(i - 1).getId();
                // 使用间隔符提取数字部分
                String[] parts1 = id1.split(":");
                long numericValue1 = Long.parseLong(parts1[1]);
                Date date1 = new Date(numericValue1);
                PredictInputData piece = new PredictInputData();
                piece.setLocation(bridgeNumber);
                piece.setDates(date1);
                piece.setTemperature(temperatureY.doubleValue());
                piece.setRainfall(rainfallY.doubleValue());
                result.add(piece);

                temperatureSum = BigDecimal.valueOf(0);
                rainfallSum = BigDecimal.valueOf(0);
                temperatureNum = BigDecimal.valueOf(0);
                rainfallNum = BigDecimal.valueOf(0);

            }
            if (input.get(i).getTemperature() != null) {
                temperatureSum = temperatureSum.add(input.get(i).getTemperature());
                temperatureNum = temperatureNum.add(BigDecimal.valueOf(1));
            }
            if (input.get(i).getRainfall() != null) {
                rainfallSum = rainfallSum.add(input.get(i).getRainfall());
                rainfallNum = rainfallNum.add(BigDecimal.valueOf(1));
            }
        }
        return result;
    }

    /**
     * 楼庄子上游雨量站数据整合
     *
     * @param paramNew
     * @return 前期雨量和小时尺度降水
     */
    public List<List<PredictInputData>> lzzRainIntegration(ForecastInputParamNew paramNew) throws ParseException {
        List<List<PredictInputData>> result = new ArrayList<>();
        //雨量站整合
        List<PredictInputData> RainHour = new ArrayList<>();
        List<PredictInputData> RainDay = new ArrayList<>();
        //喀什沟
        List<List<PredictInputData>> lzzData = lzzDataConversion(paramNew);
        List<PredictInputData> KSG = lzzData.get(3);
        KSG = getHoursRain(paramNew, KSG);
        List<PredictInputData> KSGDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getKsgRainfallStation());
        KSGDAY = getTwentyDaysRain(paramNew, KSGDAY);
        RainDay.addAll(KSGDAY);
        RainHour.addAll(KSG);
        //黑沟
        List<PredictInputData> HG = lzzData.get(4);
        HG = getHoursRain(paramNew, HG);
        List<PredictInputData> HGDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getHgRainfallStation());
        HGDAY = getTwentyDaysRain(paramNew, HGDAY);
        RainDay.addAll(HGDAY);
        RainHour.addAll(HG);
        //煤矿沟
        List<PredictInputData> MKG = lzzData.get(5);
        MKG = getHoursRain(paramNew, MKG);
        List<PredictInputData> MKGDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getMkgRainfallStation());
        MKGDAY = getTwentyDaysRain(paramNew, MKGDAY);
        RainDay.addAll(MKGDAY);
        RainHour.addAll(MKG);
        //无名沟
        List<PredictInputData> WMG = lzzData.get(6);
        WMG = getHoursRain(paramNew, WMG);
        List<PredictInputData> WMGDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getWmgRainfallStation());
        WMGDAY = getTwentyDaysRain(paramNew, WMGDAY);
        RainDay.addAll(WMGDAY);
        RainHour.addAll(WMG);
        //加普沙
        List<PredictInputData> JPS = lzzData.get(7);
        JPS = getHoursRain(paramNew, JPS);
        List<PredictInputData> JPSDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getJpsRainfallStation());
        JPSDAY = getTwentyDaysRain(paramNew, JPSDAY);
        RainDay.addAll(JPSDAY);
        RainHour.addAll(JPS);
        //宰尔德
        List<PredictInputData> ZED = lzzData.get(8);
        ZED = getHoursRain(paramNew, ZED);
        List<PredictInputData> ZEDDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getZrdRainfallStation());
        ZEDDAY = getTwentyDaysRain(paramNew, ZEDDAY);
        RainDay.addAll(ZEDDAY);
        RainHour.addAll(ZED);
        //东南沟
        List<PredictInputData> DNG = lzzData.get(9);
        DNG = getHoursRain(paramNew, DNG);
        List<PredictInputData> DNGDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getDngRainfallStation());
        DNGDAY = getTwentyDaysRain(paramNew, DNGDAY);
        RainDay.addAll(DNGDAY);
        RainHour.addAll(DNG);
        //八一林场
        List<PredictInputData> BYLC = lzzData.get(10);
        BYLC = getHoursRain(paramNew, BYLC);
        List<PredictInputData> BYLCDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getBylcRainfallStation());
        BYLCDAY = getTwentyDaysRain(paramNew, BYLCDAY);
        RainDay.addAll(BYLCDAY);
        RainHour.addAll(BYLC);
        //萨尔达万
        List<PredictInputData> SEDW = lzzData.get(11);
        SEDW = getHoursRain(paramNew, SEDW);
        List<PredictInputData> SEDWDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getSedwRainfallStation());
        SEDWDAY = getTwentyDaysRain(paramNew, SEDWDAY);
        RainDay.addAll(SEDWDAY);
        RainHour.addAll(SEDW);
        //制材厂
        List<PredictInputData> ZCC = lzzData.get(12);
        ZCC = getHoursRain(paramNew, ZCC);
        List<PredictInputData> ZCCDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getZccRainfallStation());
        ZCCDAY = getTwentyDaysRain(paramNew, ZCCDAY);
        RainDay.addAll(ZCCDAY);
        RainHour.addAll(ZCC);
        //添加小时尺度雨量和日尺度雨量
        result.add(RainHour);
        result.add(RainDay);
        return result;
    }

    /**
     * 温度值的处理，空值时等同于下一个有值的温度
     *
     * @param inputData
     * @return
     */

    public List<LzzRainfallStation> lzzTemProcessing(List<LzzRainfallStation> inputData) {
        List<LzzRainfallStation> result = new ArrayList<>();
        for (int i = 0; i < inputData.size(); i++) {
            //去除空值异常
            String id = inputData.get(i).getId();
            String[] parts = id.split(":");
            String bridgeNumber = parts[0];
            if (parts.length > 1 && bridgeNumber.length() > 1) {
                result.add(inputData.get(i));
            }
        }
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getTemperature() == null) {
                int n = 0;
                for (int j = 0; i + j < result.size(); j++) {
                    if ((result.get(i + j).getTemperature() == null)) {
                        n++;
                    } else break;
                }
                if (i + n < result.size()) {
                    BigDecimal T = result.get(i + n).getTemperature();
                    result.get(i).setTemperature(T);
                } else {
                    BigDecimal T = result.get(i - 1).getTemperature();
                    result.get(i).setTemperature(T);
                }
            }
        }
        return result;
    }

    /**
     * 头屯河数据库数据处理
     */

    /**
     * 区间数据转化为PredictInputData格式
     *
     * @param entity
     * @return result.get(0)头屯河入库日尺度站点+时间+径流
     * result.get(1)小渠子站小时尺度站点+时间+降雨
     * result.get(2)团结一队小时尺度站点+时间+降雨
     * result.get(3)头屯河水库雨量站小时尺度站点+时间+降水
     */
    public List<List<PredictInputData>> irrigatedDataConversion(IrrigatedHydrologyParam entity) {
        List<List<PredictInputData>> result = new ArrayList<>();
        //头屯河入库流量
        List<IrrigatedPlatformDataInfo> TTHI = entity.getTthInput();
        List<PredictInputData> TOIN = irrigateFlowConversion(TTHI);
        result.add(TOIN);
        //小渠子雨量站
        List<IrrigatedPlatformDataInfo> XQZ = entity.getXqzGaugingStation();
        List<PredictInputData> XIAO = irrigateRainConversion(XQZ);
        result.add(XIAO);
        //团结一队雨量站
        List<IrrigatedPlatformDataInfo> TJYD = entity.getTjydGaugingStation();
        List<PredictInputData> TUANJIE = irrigateRainConversion(TJYD);
        result.add(TUANJIE);
        //头屯河水库雨量站
        List<IrrigatedPlatformDataInfo> TTHR = entity.getTthGaugingStation();
        List<PredictInputData> TORAIN = irrigateRainConversion(TTHR);
        result.add(TORAIN);
        return result;
    }

    /**
     * 头屯河入库流量数据转化
     *
     * @param input
     * @return 返回日尺度的站点、时间、流量
     */
    public List<PredictInputData> irrigateFlowConversion(List<IrrigatedPlatformDataInfo> input) {
        List<PredictInputData> result = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            String id = input.get(i).getId();
            // 使用间隔符提取数字部分
            String[] parts = id.split("-");
            String bridgeNumber = parts[0];
            long numericValue = Long.parseLong(parts[1]);
            Date date = new Date(numericValue); // 根据时间戳创建日期对象
            int day = timeUtils.getSpecificDate(date).get("日");
            int dayBefore = 0;
            if (i != 0) {
                String id1 = input.get(i - 1).getId();
                // 使用间隔符提取数字部分
                String[] parts1 = id1.split("-");
                long numericValue1 = Long.parseLong(parts1[1]);
                Date date1 = new Date(numericValue1);
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date1);
                dayBefore = cal1.get(Calendar.DAY_OF_MONTH);
            }
            if ((day != dayBefore)) {
                PredictInputData piece = new PredictInputData();
                piece.setLocation(bridgeNumber);
                if (input.get(i).getYesterdayAvgFlow() != null) {
                    piece.setDates(date);
                    piece.setFlow(input.get(i).getYesterdayAvgFlow());//昨日平均流量？
                    result.add(piece);
                }
            }
        }
        return result;
    }

    /**
     * 区间雨量站数据转化，没有去除空值
     *
     * @param input
     * @return 小时尺度的站点名、时间、雨量
     */
    public List<PredictInputData> irrigateRainConversion(List<IrrigatedPlatformDataInfo> input) {
        List<PredictInputData> result = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            String id = input.get(i).getId();
            // 使用间隔符提取数字部分
            String[] parts = id.split("-");
            String bridgeNumber = parts[0];
            long numericValue = Long.parseLong(parts[1]);
            Date date = new Date(numericValue); // 根据时间戳创建日期对象
            int hour = timeUtils.getSpecificDate(date).get("小时");
            int hourBefore = 0;
            if (i != 0) {
                String id1 = input.get(i - 1).getId();
                // 使用间隔符提取数字部分
                String[] parts1 = id1.split("-");
                long numericValue1 = Long.parseLong(parts1[1]);
                Date date1 = new Date(numericValue1);
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date1);
                hourBefore = cal1.get(Calendar.HOUR_OF_DAY);
            }
            if ((hour != hourBefore)) {
                PredictInputData piece = new PredictInputData();
                piece.setLocation(bridgeNumber);
                piece.setDates(date);
                piece.setRainfall(input.get(i).getYqRainFallOne());
                result.add(piece);
            }
        }
        return result;
    }

    /**
     * 区间雨量站数据转化为日尺度
     *
     * @param input
     * @return 雨量站日尺度站点、时间、降水
     */
    public List<PredictInputData> irrigateRainHourToDay(List<IrrigatedPlatformDataInfo> input) {
        List<PredictInputData> result = new ArrayList<>();
        double rainfallSum = 0.0;
        int rainfallNum = 0;
        for (int i = 0; i < input.size(); i++) {
            String id = input.get(i).getId();
            // 使用间隔符提取数字部分
            String[] parts = id.split("-");
            String bridgeNumber = parts[0];
            long numericValue = Long.parseLong(parts[1]);
            Date date = new Date(numericValue); // 根据时间戳创建日期对象
            int hour = timeUtils.getSpecificDate(date).get("小时");
            int day = timeUtils.getSpecificDate(date).get("日");
            int hourBefore = 0;
            int dayBefore = day;
            if (i != 0) {
                String id1 = input.get(i - 1).getId();
                // 使用间隔符提取数字部分
                String[] parts1 = id1.split("-");
                long numericValue1 = Long.parseLong(parts1[1]);
                Date date1 = new Date(numericValue1);
                hourBefore = timeUtils.getSpecificDate(date1).get("小时");
                dayBefore = timeUtils.getSpecificDate(date1).get("日");
            }
            if (((hour - hourBefore) < 0) || day != dayBefore) {
                double rainfallY = rainfallSum;
                String id1 = input.get(i - 1).getId();
                // 使用间隔符提取数字部分
                String[] parts1 = id1.split("-");
                long numericValue1 = Long.parseLong(parts1[1]);
                Date date1 = new Date(numericValue1);
                PredictInputData piece = new PredictInputData();
                piece.setLocation(bridgeNumber);
                piece.setDates(date1);
                piece.setRainfall(rainfallY);
                result.add(piece);
                rainfallSum = 0.0;
                rainfallNum = 0;

            }
            if (input.get(i).getQxRainFall() != null) {
                rainfallSum = rainfallSum + input.get(i).getQxRainFall();
                rainfallNum = rainfallNum + 1;
            }
        }
        return result;
    }

    /**
     * 区间雨量站整合
     *
     * @param paramNew
     * @return 24小时雨量和20天雨量
     */
    public List<List<PredictInputData>> irrigateRainIntegration(ForecastInputParamNew paramNew)
            throws ParseException {
        List<List<PredictInputData>> result = new ArrayList<>();
        //雨量站整合
        List<PredictInputData> RainHour = new ArrayList<>();
        List<PredictInputData> RainDay = new ArrayList<>();
        List<List<PredictInputData>> rainStation = irrigatedDataConversion(paramNew.getIrrigatedHydrologyParam());
        //小渠子
        List<PredictInputData> XQZ = rainStation.get(1);
        XQZ = getHoursRain(paramNew, XQZ);
        List<PredictInputData> XQZDAY = irrigateRainHourToDay(paramNew.getIrrigatedHydrologyParam().getXqzGaugingStation());
        XQZDAY = getTwentyDaysRain(paramNew, XQZDAY);
        RainDay.addAll(XQZDAY);
        RainHour.addAll(XQZ);
        //团结一队
        List<PredictInputData> TJYD = rainStation.get(2);
        TJYD = getHoursRain(paramNew, TJYD);
        List<PredictInputData> TJYDDAY = irrigateRainHourToDay(paramNew.getIrrigatedHydrologyParam().getTjydGaugingStation());
        TJYDDAY = getTwentyDaysRain(paramNew, TJYDDAY);
        RainDay.addAll(TJYDDAY);
        RainHour.addAll(TJYD);
        //头屯河水库
        List<PredictInputData> TTHR = rainStation.get(3);
        TTHR = getHoursRain(paramNew, TTHR);
        List<PredictInputData> TTHRDAY = irrigateRainHourToDay(paramNew.getIrrigatedHydrologyParam().getTthGaugingStation());
        TTHRDAY = getTwentyDaysRain(paramNew, TTHRDAY);
        RainDay.addAll(TTHRDAY);
        RainHour.addAll(TTHR);
        //添加小时尺度雨量和日尺度雨量
        result.add(RainHour);
        result.add(RainDay);
        return result;
    }

    /**
     * 区间数据站点名为空的处理
     *
     * @param inputData
     * @return
     */
    public IrrigatedHydrologyParam irrigateStationProcessing(IrrigatedHydrologyParam inputData) {
        IrrigatedHydrologyParam result = new IrrigatedHydrologyParam();
        List<IrrigatedPlatformDataInfo> XQZ = inputData.getXqzGaugingStation();
        List<IrrigatedPlatformDataInfo> XQZresult = new ArrayList<>();
        for (int i = 0; i < XQZ.size(); i++) {
            IrrigatedPlatformDataInfo station = XQZ.get(i);
            //去除空值数据
            String id = station.getId();
            String[] parts = id.split("-");
            String bridgeNumber = parts[0];
            if (parts.length > 1 && bridgeNumber.length() > 1) {
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
            if (parts.length > 1 && bridgeNumber.length() > 1) {
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
            if (parts.length > 1 && bridgeNumber.length() > 1) {
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
            if (parts.length > 1 && bridgeNumber.length() > 1) {
                TTHIresult.add(TTHI.get(i));
            }
        }
        result.setTthInput(TTHIresult);
        return result;
    }

    /**
     * 区间数据尺度转化
     * @param inputData
     * @return
     */
    public IrrigatedHydrologyParam irrigateMinuteToHour(IrrigatedHydrologyParam inputData){
        IrrigatedHydrologyParam result = new IrrigatedHydrologyParam();
        List<IrrigatedPlatformDataInfo> xqzStation = inputData.getXqzGaugingStation();
        List<IrrigatedPlatformDataInfo> tjydStation = inputData.getTjydGaugingStation();
        List<IrrigatedPlatformDataInfo> tthStation = inputData.getTthGaugingStation();
        List<IrrigatedPlatformDataInfo> tthInput = inputData.getTthInput();
        result.setXqzGaugingStation(irrigateListMinuteToHour(xqzStation));
        result.setTjydGaugingStation(irrigateListMinuteToHour(tjydStation));
        result.setTthGaugingStation(irrigateListMinuteToHour(tthStation));
        result.setTthInput(irrigateListMinuteToHour(tthInput));
        return result;
    }

    /**
     * 区间雨量站和水文站数据从5min转为小时尺度
     * @param inputList
     * @return
     */
    public List<IrrigatedPlatformDataInfo> irrigateListMinuteToHour(List<IrrigatedPlatformDataInfo> inputList){
        Double rainFall = 0.0;
        Double monitorFlow =0.0;
        int number = 0;
        List<IrrigatedPlatformDataInfo> resultList = new ArrayList<>();
        for (int i = 0; i < inputList.size()-1; i++) {
            IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
            Boolean isSameHour = timeUtils.DateCompare(inputList.get(i).getMonitorTime(),inputList.get(i+1).getMonitorTime(),"小时");
            info.setId(inputList.get(i).getId());
            info.setYesterdayAvgFlow(inputList.get(i).getYesterdayAvgFlow());
            info.setMonitorName(inputList.get(i).getMonitorName());
            info.setMonitorId(inputList.get(i).getMonitorId());
            info.setMonitorTime(inputList.get(i).getMonitorTime());
            if (inputList.get(i).getSqMonitorFlow()==null){
                inputList.get(i).setSqMonitorFlow(0.0);
            }if (inputList.get(i).getYqRainFallOne()==null){
                inputList.get(i).setYqRainFallOne(0.0);
            }
            if (isSameHour){
                rainFall += inputList.get(i).getYqRainFallOne();
                monitorFlow += inputList.get(i).getSqMonitorFlow();
                number++;
            }else {
                if (number==0){//该小时只有一个时段
                    info.setSqMonitorFlow(inputList.get(i).getSqMonitorFlow());
                    info.setYqRainFallOne(inputList.get(i).getYqRainFallOne());
                }else {
                    info.setSqMonitorFlow(rainFall/number);
                    info.setYqRainFallOne(monitorFlow/number);
                }
                resultList.add(info);
            }
        }
        return resultList;
    }

    /**
     * 温度值和降雨量的处理
     */

    /**
     * 根据月份来提供空值的温度
     *
     * @param month
     * @return
     */
    public Double setNullTemperature(int month) {
        Double result = 0.0;
        switch (month) {
            case 1: {
                result = -17.5;
                break;
            }
            case 2: {
                result = -13.8;
                break;
            }
            case 3: {
                result = -1.8;
                break;
            }
            case 4: {
                result = 10.6;
                break;
            }
            case 5: {
                result = 17.6;
                break;
            }
            case 6: {
                result = 22.7;
                break;
            }
            case 7: {
                result = 24.6;
                break;
            }
            case 8: {
                result = 22.9;
                break;
            }
            case 9: {
                result = 16.9;
                break;
            }
            case 10: {
                result = 7.7;
                break;
            }
            case 11: {
                result = -3.3;
                break;
            }
            case 12: {
                result = -13.1;
                break;
            }
        }
        return result;
    }

    /**
     * 温度和降水空值赋值
     *
     * @param date
     * @param location
     * @return
     */
    public PredictInputData assignmentNullRAndT(Date date, String location) {
        PredictInputData result = new PredictInputData();
        result.setDates(date);
        Double tem = setNullTemperature(timeUtils.getSpecificDate(date).get("月"));
        result.setTemperature(tem);
        result.setLocation(location);
        result.setRainfall(0.0);
        return result;
    }

    /**
     * 从数据库中获得水位站的对应日尺度温度与降水
     *
     * @param paramForecastInputParamNew 从数据库中获得的数据
     * @return 日尺度温度与降水
     */
    public List<PredictInputData> getRAndT(ForecastInputParamNew paramForecastInputParamNew) {
        //雨量站整合
        List<PredictInputData> RainDay = new ArrayList<>();
        //喀什沟
        List<PredictInputData> KSGDAY = lzzRainHourToDay(paramForecastInputParamNew.getLzzHydrologyParam().getKsgRainfallStation());
        RainDay.addAll(KSGDAY);
        //黑沟
        List<PredictInputData> HGDAY = lzzRainHourToDay(paramForecastInputParamNew.getLzzHydrologyParam().getHgRainfallStation());
        RainDay.addAll(HGDAY);
        //煤矿沟
        List<PredictInputData> MKGDAY = lzzRainHourToDay(paramForecastInputParamNew.getLzzHydrologyParam().getMkgRainfallStation());
        RainDay.addAll(MKGDAY);
        //无名沟
        List<PredictInputData> WMGDAY = lzzRainHourToDay(paramForecastInputParamNew.getLzzHydrologyParam().getWmgRainfallStation());
        RainDay.addAll(WMGDAY);
        //加普沙
        List<PredictInputData> JPSDAY = lzzRainHourToDay(paramForecastInputParamNew.getLzzHydrologyParam().getJpsRainfallStation());
        RainDay.addAll(JPSDAY);
        //宰尔德
        List<PredictInputData> ZEDDAY = lzzRainHourToDay(paramForecastInputParamNew.getLzzHydrologyParam().getZrdRainfallStation());
        RainDay.addAll(ZEDDAY);
        //东南沟
        List<PredictInputData> DNGDAY = lzzRainHourToDay(paramForecastInputParamNew.getLzzHydrologyParam().getDngRainfallStation());
        RainDay.addAll(DNGDAY);
        //八一林场
        List<PredictInputData> BYLCDAY = lzzRainHourToDay(paramForecastInputParamNew.getLzzHydrologyParam().getBylcRainfallStation());
        RainDay.addAll(BYLCDAY);
        //萨尔达万
        List<PredictInputData> SEDWDAY = lzzRainHourToDay(paramForecastInputParamNew.getLzzHydrologyParam().getSedwRainfallStation());
        RainDay.addAll(SEDWDAY);
        //制材厂
        List<PredictInputData> ZCCDAY = lzzRainHourToDay(paramForecastInputParamNew.getLzzHydrologyParam().getZccRainfallStation());
        RainDay.addAll(ZCCDAY);
        //添加日尺度温度与降水
        List<PredictInputData> RAT = pointToSurface(RainDay, "日", "楼庄子");//转换为平均值

        return RAT;
    }

    /**
     * 为水位站添加温度和降水
     *
     * @param WaterStation
     * @param RAT
     * @return
     */
    public List<PredictInputData> addRAndT(List<PredictInputData> WaterStation, List<PredictInputData> RAT) {
        List<PredictInputData> result = new ArrayList<>();
        for (int i = 0; i < WaterStation.size(); i++) {
            for (int j = 0; j < RAT.size(); j++) {
                Boolean dateCompare = timeUtils.DateCompare(RAT.get(j).getDates(), WaterStation.get(i).getDates(), "日");
                if (dateCompare) {
                    double rain = RAT.get(j).getRainfall();
                    WaterStation.get(i).setRainfall(rain);
                    double temperature = RAT.get(j).getTemperature();
                    WaterStation.get(i).setTemperature(temperature);
                }
            }
            if (WaterStation.get(i).getRainfall() == null) {
                WaterStation.get(i).setRainfall(0.0);
            }
        }
        //为空日期赋值，赋值为0
        for (PredictInputData predictInputData : WaterStation) {
            if (predictInputData.getTemperature() == null) {
                Double tem = setNullTemperature(timeUtils.getSpecificDate(predictInputData.getDates()).get("月"));
                predictInputData.setTemperature(tem);
            }
        }
        result = WaterStation;
        return result;
    }


    /**
     * 温度转化为蒸发量
     *
     * @param data
     * @return
     */
    public Object[][] temToEva(Object[][] data) {
        //按温度分配蒸发
        double[] temperature = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            temperature[i] = (Double) data[i][1];
        }
        double minValue = Arrays.stream(temperature).min().orElse(Double.NaN);
        for (int i = 0; i < temperature.length; i++) {
            temperature[i] -= minValue;
        }
        double sum = 0;
        for (double num : temperature) {
            sum += num;
        }

        //按月份分配蒸发
        double evaporation = 0.0;
        for (int i = 0; i < data.length; i++) {
            int month = timeUtils.getSpecificDate((Date) data[i][0]).get("月");
            switch (month) {
                case 1:
                    evaporation = 9.0 / (0.2 * 0.2 * 3.14) / 31 / 24;
                    break;
                case 2:
                    evaporation = 16.4 / (0.2 * 0.2 * 3.14) / 28 / 24;
                    break;
                case 3:
                    evaporation = 56.7 / (0.2 * 0.2 * 3.14) / 31 / 24;
                    break;
                case 4:
                    evaporation = 177.3 / (0.2 * 0.2 * 3.14) / 30 / 24;
                    break;
                case 5:
                    evaporation = 270.1 / (0.2 * 0.2 * 3.14) / 31 / 24;
                    break;
                case 6:
                    evaporation = 294.8 / (0.2 * 0.2 * 3.14) / 30 / 24;
                    break;
                case 7:
                    evaporation = 315.3 / (0.2 * 0.2 * 3.14) / 31 / 24;
                    break;
                case 8:
                    evaporation = 275.3 / (0.2 * 0.2 * 3.14) / 31 / 24;
                    break;
                case 9:
                    evaporation = 187.7 / (0.2 * 0.2 * 3.14) / 30 / 24;
                    break;
                case 10:
                    evaporation = 101.8 / (0.2 * 0.2 * 3.14) / 31 / 24;
                    break;
                case 11:
                    evaporation = 26.9 / (0.2 * 0.2 * 3.14) / 30 / 24;
                    break;
                case 12:
                    evaporation = 8.0 / (0.2 * 0.2 * 3.14) / 31 / 24;
                    break;
            }
        }
        for (int i = 0; i < data.length; i++) {
            if (sum == 0) {
                data[i][1] = evaporation;
            } else {
                if (temperature[i] < 0) {
                    temperature[i] = 0;
                    data[i][1] = evaporation * temperature[i] / sum;
                }
                data[i][1] = evaporation * temperature[i] / sum;
            }
        }

        for (int i = 0; i < data.length; i++) {
            data[i][1] = 0.016;
        }
        return data;
    }

    /**
     * 输入数据区分部分（训练、测试、区分融雪、区分丰枯）
     */

    /**
     * 模型预报时的输入
     *
     * @param dataList
     * @param
     * @return
     */
    public double[][] inputData_Real(List<double[]> dataList, ForecastInputParam param) {
        /**
         * 前n旬流量+平均流量
         */
        int a = dataList.get(0).length;
        int n = param.getHistory_day();
//		 一般训练期：检验期=3:1
        double[][] data = new double[a - n + 1][n + 2];
        for (int i = 0; i < a - n + 1; i++) {
            data[i][0] = dataList.get(0)[i + n - 1];//时间戳
            for (int j = 0; j < n - 1; j++) {
                data[i][j + 1] = dataList.get(1)[i + j];//前N旬流量
            }
            data[i][n] = dataList.get(2)[i];//平均流量
            data[i][n + 1] = dataList.get(1)[i + n - 1];//预报径流
        }
        return data;
    }

    /**
     * 训练模型时的输入
     *
     * @param dataList
     * @param
     * @param isTest
     * @return
     * @throws Exception
     */
    public double[][] inputData_Train(List<double[]> dataList, ForecastInputParam param, boolean isTest) {

        int a = dataList.get(0).length;
        int b = a / 4 * 3;
        int c = a - b;
        int n = param.getHistory_day();
        /**
         * 前n旬流量+平均流量
         */
//		 一般训练期：检验期=3:1
        double[][] trainData = new double[b - n + 1][n + 2];// 第一维样本数据的长度，第二维输入节点输出节点的值
        double[][] testData = new double[c - n + 1][n + 2];

        for (int i = 0; i < b - n + 1; i++) {
            trainData[i][0] = dataList.get(0)[i + n - 1];//时间戳
            for (int j = 0; j < n - 1; j++) {
                trainData[i][j + 1] = dataList.get(1)[i + j];//前N旬流量
            }
            trainData[i][n] = dataList.get(2)[i];//平均流量
            trainData[i][n + 1] = dataList.get(1)[i + n - 1];//预报径流
        }


        for (int i = b; i < a - n + 1; i++) {
            testData[i - b][0] = dataList.get(0)[i + n - 1];//时间戳
            for (int j = 0; j < n - 1; j++) {
                testData[i - b][j + 1] = dataList.get(1)[i + j];//前N旬流量
            }
            testData[i - b][n] = dataList.get(2)[i];//平均流量
            testData[i - b][n + 1] = dataList.get(1)[i + n - 1];//预报径流
        }

        if (isTest) {
            return testData;
        } else {
            return trainData;
        }

    }

    /**
     * 融雪模型数据输入
     *
     * @param dataList
     * @param param
     * @return
     */
    public double[][] inputData_Real_Snow(List<double[]> dataList, ForecastInputParam param) {
        /**
         * 前3天流量+前3天温度+前3天降水
         */
        int a = dataList.get(0).length;
        int n = param.getHistory_day();
        int m = 0;//前m天的数据
        if (dataList.size() > 4) {//有降雨数据
            m = 3;
        } else {
            m = 2;
        }
        double[][] data = new double[a - n][n * m + 2];
        for (int i = 0; i < a - n; i++) {
            data[i][0] = dataList.get(0)[i + n];//时间戳
            for (int j = 0; j < n; j++) {
                data[i][j + 1] = dataList.get(1)[i + j];//前N天流量
                data[i][j + 1 + n] = dataList.get(3)[i + j];//温度
                if (dataList.size() > 4) {//有降雨数据
                    data[i][j + 1 + 2 * n] = dataList.get(4)[i + j];//降水
                }
            }

            data[i][n * m + 1] = dataList.get(1)[i + n];//预报径流
        }
        return data;
    }

    /**
     * 融雪模型训练数据输入
     *
     * @param dataList
     * @param param
     * @param isTest
     * @return
     * @throws Exception
     */
    public double[][] inputData_Train_Snow(List<double[]> dataList, ForecastInputParam param, boolean isTest) {

        int a = dataList.get(0).length;
        int b = a / 4 * 3;
        int c = a - b;
        int n = param.getHistory_day();//前期天数
        int m = 0;
        if (dataList.size() > 4) {//有降雨数据
            m = 3;
        } else {
            m = 2;
        }
        /**
         * 前n旬流量+温度+降水
         */
//		 一般训练期：检验期=3:1
        double[][] trainData = new double[b - n][n * m + 2];
        double[][] testData = new double[c - n][n * m + 2];

        for (int i = 0; i < b - n; i++) {
            trainData[i][0] = dataList.get(0)[i + n];//时间戳
            for (int j = 0; j < n; j++) {
                trainData[i][j + 1] = dataList.get(1)[i + j];//前N天流量
                trainData[i][j + 1 + n] = dataList.get(3)[i + j];//温度
                if (dataList.size() > 4) {
                    trainData[i][j + 1 + 2 * n] = dataList.get(4)[i + j];//降水
                }
            }
            trainData[i][n * m + 1] = dataList.get(1)[i + n];//预报径流
        }

        for (int i = b; i < a - n; i++) {
            testData[i - b][0] = dataList.get(0)[i + n];//时间戳
            for (int j = 0; j < n; j++) {
                testData[i - b][j + 1] = dataList.get(1)[i + j];//前N天流量
                testData[i - b][j + 1 + n] = dataList.get(3)[i + j];//温度
                if (dataList.size() > 4) {
                    testData[i - b][j + 1 + 2 * n] = dataList.get(4)[i + j];//降水
                }
            }
            testData[i - b][n * m + 1] = dataList.get(1)[i + n];//预报径流
        }
        if (isTest) {
            return testData;
        } else {
            return trainData;
        }
    }

    /**
     * 获得丰水期和枯水期的预报开始时间和预报数量
     *
     * @param param
     * @return result.get(0)丰水期
     * result.get(1)枯水期
     */
    public List<Object[]> getSelectedData(ForecastInputParam param) {
        List<Object[]> result = new ArrayList<>();
        Object[] Feng = new Object[2];
        Object[] Ku = new Object[2];
        Date dateStart = param.getPreStartTime();
        int number = param.getPeriodStepNumber() * param.getPeriodStepSize();
        Date[][] date = new Date[number][1];
        int fengNumber = 0;
        int kuNumber = 0;
        int month = 0;
        switch (param.getPeriod()) {
            case "月":
                date = timeUtils.getMonthDateList(dateStart, number);
                break;
            case "旬":
                date = timeUtils.getDateList(dateStart, number, 10, 0);
                break;
            case "日":
                date = timeUtils.getDateList(dateStart, number, 1, 0);
                break;
        }
        for (int i = 0; i < number; i++) {
            month = timeUtils.getSpecificDate(date[i][0]).get("月");
            if (month <= 9 && month >= 5) {
                fengNumber++;
            } else {
                kuNumber++;
            }
        }
        month = timeUtils.getSpecificDate(date[0][0]).get("月");
        if (month <= 9 && month >= 5) {
            Feng[0] = date[0];
            for (int i = 0; i < number; i++) {
                month = timeUtils.getSpecificDate(date[i][0]).get("月");
                if (month == 10) {
                    Ku[0] = date[i];
                    break;
                }
            }
        } else {
            Ku[0] = date[0];
            for (int i = 0; i < number; i++) {
                month = timeUtils.getSpecificDate(date[i][0]).get("月");
                if (month == 5) {
                    Feng[0] = date[i];
                    break;
                }
            }
        }
        Feng[1] = fengNumber;
        Ku[1] = kuNumber;
        result.add(Feng);
        result.add(Ku);
        return result;
    }

    /**
     * 筛选枯水期丰水期,5~9月为丰水期
     *
     * @param input
     * @param preStartTime
     * @return
     */
    public Object[][] SelectDate(Object[][] input, Date preStartTime) {
        List<Object[]> KuData = new ArrayList<>();
        Object[] kudata = new Object[input[0].length];
        List<Object[]> FengData = new ArrayList<>();
        Object[] fengdata = new Object[input[0].length];
        for (int i = 0; i < input.length; i++) {
            Date time = (Date) input[i][0];
            int month = timeUtils.getSpecificDate(time).get("月");
            kudata = new Object[input[0].length];
            fengdata = new Object[input[0].length];
            if (month <= 4 || month >= 10) {
                for (int j = 0; j < input[0].length; j++) {
                    kudata[j] = input[i][j];
                }
                KuData.add(kudata);
            } else {
                for (int j = 0; j < input[0].length; j++) {
                    fengdata[j] = input[i][j];
                }
                FengData.add(fengdata);
            }
        }
        Date time2 = preStartTime;
        int month2 = timeUtils.getSpecificDate(time2).get("月");
        Object[][] longForecastInput;
        if (month2 <= 4 || month2 >= 10) {
            longForecastInput = new Object[KuData.size()][input[0].length];
            for (int i = 0; i < KuData.size(); i++) {
                longForecastInput[i] = KuData.get(i);
            }

        } else {
            longForecastInput = new Object[FengData.size()][input[0].length];
            for (int i = 0; i < FengData.size(); i++) {
                longForecastInput[i] = FengData.get(i);
            }
        }

        return longForecastInput;
    }

    /**
     * 筛选融雪期,楼头区间3月融雪，楼庄子上游5~7月份融雪
     *
     * @param input 历史径流+温度+降雨
     * @return
     */
    public Object[][] snowMeltDate(Object[][] input, String location) {
        List<Object[]> Data = new ArrayList<>();
        Object[] data;
        int factor = 3;
        List<Object[]> snowData = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (!(input[i][3] instanceof String)) {
//                if ((double) input[i][3] == 0.0) {
//                    snowData.add(input[i]);
//                }
                snowData.add(input[i]);
            }
        }
        for (int i = 0; i < snowData.size(); i++) {
            Date time = (Date) snowData.get(i)[0];
            int month = timeUtils.getSpecificDate(time).get("月");
            data = new Object[factor];
            if (location.equals("楼头区间")) {
                if (month == 3) {
                    for (int j = 0; j < factor; j++) {
                        data[j] = snowData.get(i)[j];
                    }
                    Data.add(data);
                }
            } else {
                if (month >= 5 && month <= 7) {
                    for (int j = 0; j < factor; j++) {
                        data[j] = snowData.get(i)[j];
                    }
                    Data.add(data);
                }
            }
        }
        Object[][] rongXueInput = new Object[Data.size()][factor];
        for (int i = 0; i < Data.size(); i++) {
            rongXueInput[i] = Data.get(i);
        }
        return rongXueInput;
    }

    /**
     * 径流数据处理部分（距平值）
     */

    /**
     * 输入数据的处理，求与均值之间的偏差
     *
     * @param input
     * @param param
     * @return
     */
    public Object[][] inputProcessing(Object[][] input, ForecastInputParam param) {
        int month = 0;
        for (int i = 0; i < input.length; i++) {
            if (param.getLocation().equals("3号桥") || param.getLocation().equals("楼庄子")) {
                Date date = (Date) input[i][0];
                month = timeUtils.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        input[i][1] = (1.29 - (double) input[i][1]) / 1.29;
                        break;
                    case 2:
                        input[i][1] = (1.16 - (double) input[i][1]) / 1.16;
                        break;
                    case 3:
                        input[i][1] = (1.44 - (double) input[i][1]) / 1.44;
                        break;
                    case 4:
                        input[i][1] = (2.57 - (double) input[i][1]) / 2.57;
                        break;
                    case 5:
                        input[i][1] = (7.95 - (double) input[i][1]) / 7.95;
                        break;
                    case 6:
                        input[i][1] = (17.865 - (double) input[i][1]) / 17.865;
                        break;
                    case 7:
                        input[i][1] = (21.63 - (double) input[i][1]) / 21.63;
                        break;
                    case 8:
                        input[i][1] = (16.07 - (double) input[i][1]) / 16.07;
                        break;
                    case 9:
                        input[i][1] = (7.5 - (double) input[i][1]) / 7.5;
                        break;
                    case 10:
                        input[i][1] = (3.76 - (double) input[i][1]) / 3.76;
                        break;
                    case 11:
                        input[i][1] = (2.27 - (double) input[i][1]) / 2.27;
                        break;
                    case 12:
                        input[i][1] = (1.62 - (double) input[i][1]) / 1.62;
                        break;
                }
            } else if (param.getLocation().equals("楼头区间")) {
                Date date = (Date) input[i][0];
                month = timeUtils.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        input[i][1] = (1.29 * 0.116 - (double) input[i][1]) / 1.29 * 0.116;
                        break;
                    case 2:
                        input[i][1] = (1.16 * 0.0957 - (double) input[i][1]) / 1.16 * 0.0957;
                        break;
                    case 3:
                        input[i][1] = (1.44 * 0.538 - (double) input[i][1]) / 1.44 * 0.538;
                        break;
                    case 4:
                        input[i][1] = (2.57 * 0.316 - (double) input[i][1]) / 2.57 * 0.316;
                        break;
                    case 5:
                        input[i][1] = (7.95 * 0.072 - (double) input[i][1]) / 7.95 * 0.072;
                        break;
                    case 6:
                        input[i][1] = (17.865 * 0.0484 - (double) input[i][1]) / 17.865 * 0.0484;
                        break;
                    case 7:
                        input[i][1] = (21.63 * 0.044 - (double) input[i][1]) / 21.63 * 0.044;
                        break;
                    case 8:
                        input[i][1] = (16.07 * 0.0395 - (double) input[i][1]) / 16.07 * 0.0395;
                        break;
                    case 9:
                        input[i][1] = (7.5 * 0.0419 - (double) input[i][1]) / 7.5 * 0.0419;
                        break;
                    case 10:
                        input[i][1] = (3.76 * 0.0383 - (double) input[i][1]) / 3.76 * 0.0383;
                        break;
                    case 11:
                        input[i][1] = (2.27 * 0.0365 - (double) input[i][1]) / 2.27 * 0.0365;
                        break;
                    case 12:
                        input[i][1] = (1.62 * 0.001524 - (double) input[i][1]) / 1.62 * 0.001524;
                        break;
                }
            }
        }
        return input;
    }

    /**
     * 将最后预测的相对误差转换为实际径流
     *
     * @param input
     * @return
     */
    public Object[][] resultProcessing(Object[][] input, ForecastInputParam param) {
        int month;
        for (int i = 0; i < input.length; i++) {
            if (param.getLocation().equals("3号桥") || param.getLocation().equals("楼庄子")) {
                Date date = (Date) input[i][0];
                month = timeUtils.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        input[i][1] = (1 - (double) input[i][1]) * 1.29;
                        if ((double) input[i][1] < 0.66) {
                            input[i][1] = 0.66;
                        }
                        if ((double) input[i][1] > 3) {
                            input[i][1] = 2.38;
                        }
                        break;
                    case 2:
                        input[i][1] = (1 - (double) input[i][1]) * 1.16;
                        if ((double) input[i][1] < 0.57) {
                            input[i][1] = 0.57;
                        }
                        if ((double) input[i][1] > 3) {
                            input[i][1] = 1.88;
                        }
                        break;
                    case 3:
                        input[i][1] = (1 - (double) input[i][1]) * 1.44;
                        if ((double) input[i][1] < 0.68) {
                            input[i][1] = 0.68;
                        }
                        if ((double) input[i][1] > 5) {
                            input[i][1] = 3.94;
                        }
                        break;
                    case 4:
                        input[i][1] = (1 - (double) input[i][1]) * 2.57;
                        if ((double) input[i][1] < 1.38) {
                            input[i][1] = 1.38;
                        }
                        if ((double) input[i][1] > 5) {
                            input[i][1] = 4.06;
                        }
                        break;
                    case 5:
                        input[i][1] = (1 - (double) input[i][1]) * 7.95;
                        if ((double) input[i][1] < 3.61) {
                            input[i][1] = 3.61;
                        }
                        if ((double) input[i][1] > 20) {
                            input[i][1] = 14.7;
                        }
                        break;
                    case 6:
                        input[i][1] = (1 - (double) input[i][1]) * 17.865;
                        if ((double) input[i][1] < 9.54) {
                            input[i][1] = 9.54;
                        }
                        if ((double) input[i][1] > 35) {
                            input[i][1] = 32.99;
                        }
                        break;
                    case 7:
                        input[i][1] = (1 - (double) input[i][1]) * 21.63;
                        if ((double) input[i][1] < 12.1) {
                            input[i][1] = 12.1;
                        }
                        if ((double) input[i][1] > 50) {
                            input[i][1] = 46.1;
                        }
                        break;
                    case 8:
                        input[i][1] = (1 - (double) input[i][1]) * 16.07;
                        if ((double) input[i][1] < 9.19) {
                            input[i][1] = 9.19;
                        }
                        if ((double) input[i][1] > 30) {
                            input[i][1] = 27.2;
                        }
                        break;
                    case 9:
                        input[i][1] = (1 - (double) input[i][1]) * 7.5;
                        if ((double) input[i][1] < 3.62) {
                            input[i][1] = 3.62;
                        }
                        if ((double) input[i][1] > 20) {
                            input[i][1] = 14.7;
                        }
                        break;
                    case 10:
                        input[i][1] = (1 - (double) input[i][1]) * 3.76;
                        if ((double) input[i][1] < 1.95) {
                            input[i][1] = 1.95;
                        }
                        if ((double) input[i][1] > 10) {
                            input[i][1] = 6.66;
                        }
                        break;
                    case 11:
                        input[i][1] = (1 - (double) input[i][1]) * 2.27;
                        if ((double) input[i][1] < 1.13) {
                            input[i][1] = 1.13;
                        }
                        if ((double) input[i][1] > 5) {
                            input[i][1] = 3.36;
                        }
                        break;
                    case 12:
                        input[i][1] = (1 - (double) input[i][1]) * 1.62;
                        if ((double) input[i][1] < 0.86) {
                            input[i][1] = 0.86;
                        }
                        if ((double) input[i][1] > 5) {
                            input[i][1] = 3.16;
                        }
                        break;
                }
            }
            //各个月份的区间比例
            else if (param.getLocation().equals("楼头区间")) {
                Date date = (Date) input[i][0];
                month = timeUtils.getSpecificDate(date).get("月");
                double proportion = 0.058;
                switch (month) {
                    case 1:
                        proportion = 0.116;
                        input[i][1] = (1 - (double) input[i][1]) * 1.29 * proportion;
                        if ((double) input[i][1] < 0.66 * proportion) {
                            input[i][1] = 0.66 * proportion;
                        }
                        if ((double) input[i][1] > 3 * proportion) {
                            input[i][1] = 2.38 * proportion;
                        }
                        break;
                    case 2:
                        proportion = 0.0957;
                        input[i][1] = (1 - (double) input[i][1]) * 1.16 * proportion;
                        if ((double) input[i][1] < 0.57 * proportion) {
                            input[i][1] = 0.57 * proportion;
                        }
                        if ((double) input[i][1] > 3 * proportion) {
                            input[i][1] = 1.88 * proportion;
                        }
                        break;
                    case 3:
                        proportion = 0.538;
                        input[i][1] = (1 - (double) input[i][1]) * 1.44 * proportion;
                        if ((double) input[i][1] < 0.68 * proportion) {
                            input[i][1] = 0.68 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.94 * proportion;
                        }
                        break;
                    case 4:
                        proportion = 0.316;
                        input[i][1] = (1 - (double) input[i][1]) * 2.57 * proportion;
                        if ((double) input[i][1] < 1.38 * proportion) {
                            input[i][1] = 1.38 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 4.06 * proportion;
                        }
                        break;
                    case 5:
                        proportion = 0.072;
                        input[i][1] = (1 - (double) input[i][1]) * 7.95 * proportion;
                        if ((double) input[i][1] < 3.61 * proportion) {
                            input[i][1] = 3.61 * proportion;
                        }
                        if ((double) input[i][1] > 20 * proportion) {
                            input[i][1] = 14.7 * proportion;
                        }
                        break;
                    case 6:
                        proportion = 0.0484;
                        input[i][1] = (1 - (double) input[i][1]) * 17.865 * proportion;
                        if ((double) input[i][1] < 9.54 * proportion) {
                            input[i][1] = 9.54 * proportion;
                        }
                        if ((double) input[i][1] > 35 * proportion) {
                            input[i][1] = 32.99 * proportion;
                        }
                        break;
                    case 7:
                        proportion = 0.044;
                        input[i][1] = (1 - (double) input[i][1]) * 21.63 * proportion;
                        if ((double) input[i][1] < 12.1 * proportion) {
                            input[i][1] = 12.1 * proportion;
                        }
                        if ((double) input[i][1] > 50 * proportion) {
                            input[i][1] = 46.1 * proportion;
                        }
                        break;
                    case 8:
                        proportion = 0.0395;
                        input[i][1] = (1 - (double) input[i][1]) * 16.07 * proportion;
                        if ((double) input[i][1] < 9.19 * proportion) {
                            input[i][1] = 9.19 * proportion;
                        }
                        if ((double) input[i][1] > 30 * proportion) {
                            input[i][1] = 27.2 * proportion;
                        }
                        break;
                    case 9:
                        proportion = 0.0419;
                        input[i][1] = (1 - (double) input[i][1]) * 7.5 * proportion;
                        if ((double) input[i][1] < 3.62 * proportion) {
                            input[i][1] = 3.62 * proportion;
                        }
                        if ((double) input[i][1] > 20 * proportion) {
                            input[i][1] = 14.7 * proportion;
                        }
                        break;
                    case 10:
                        proportion = 0.0383;
                        input[i][1] = (1 - (double) input[i][1]) * 3.76 * proportion;
                        if ((double) input[i][1] < 1.95 * proportion) {
                            input[i][1] = 1.95 * proportion;
                        }
                        if ((double) input[i][1] > 10 * proportion) {
                            input[i][1] = 6.66 * proportion;
                        }
                        break;
                    case 11:
                        proportion = 0.0365;
                        input[i][1] = (1 - (double) input[i][1]) * 2.27 * proportion;
                        if ((double) input[i][1] < 1.13 * proportion) {
                            input[i][1] = 1.13 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.36 * proportion;
                        }
                        break;
                    case 12:
                        proportion = 0.001524;
                        input[i][1] = (1 - (double) input[i][1]) * 1.62 * proportion;
                        if ((double) input[i][1] < 0.86 * proportion) {
                            input[i][1] = 0.86 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.16 * proportion;
                        }
                        break;
                }
            }
        }
        return input;
    }


}


