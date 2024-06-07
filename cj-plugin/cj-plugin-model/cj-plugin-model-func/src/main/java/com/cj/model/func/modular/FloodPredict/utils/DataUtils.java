package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.entity.*;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 数据处理方法
 *
 * @author leileilei
 */
public class DataUtils {
    TimeUtils timeUtils = new TimeUtils();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 从数据库导入的数据进行处理，包括对三号桥、楼庄子进库站异常流量的处理，上游雨量站温度空值的处理
     */
    public void emptyProcessing(ForecastInputParamNew result) {
        //输入数据的转化
        Date nowDate = new Date();
        int l = result.getPeriodTimeNum() * result.getPeriodTimeStep();
        Date startDate = result.getPredictionTime();
        Date endTime = timeUtils.addCalendar(result.getPredictionTime(), "小时", l);
        //水位站空值数据处理
        stationEmpty(result);
        //雨情预报数据处理
        if (result.getIsSimulation() == null) {
            result.setIsSimulation(false);
        }
        if (result.getModelType() == 3 && endTime.after(nowDate) && result.getRainFallDtos().isEmpty()) {
//            throw new RuntimeException("未获得预报降雨数据");
            List<RainFallDto> rainFallDtos = new ArrayList<>();
            for (int i = 0; i < l; i++) {
                RainFallDto rainFallDto = new RainFallDto();
                rainFallDto.setDate(sdf.format(timeUtils.addCalendar(startDate,"小时",i)));
                rainFallDto.setRainFall(0.0);
                rainFallDto.setTemperature(0.0);
                rainFallDto.setArea("面雨量");
                rainFallDtos.add(rainFallDto);
            }
            result.setRainFallDtos(rainFallDtos);
        }
        if (endTime.after(InputUtils.historyDate)&&(result.getInflowRunoffs() == null || result.getInflowRunoffs().isEmpty())) {
            throw new RuntimeException("未获得A3表中进库流量数据");
        }
        //日尺度预报数据的处理
        if (result.getPeriodTimeType()==3||result.getPeriodTimeType()==4){
            getDaysData(result);
        }
        //区间雨量站数据转化
        IrrigatedHydrologyParam irrigatedHydrologyParam = irrigateMinuteToHour(result.getIrrigatedHydrologyParam());//区间
        result.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
        //参数的输入
        ShanbeiParam param3 = defaultParam();
        ShanbeiParam paramLzz =  defaultParam();
        ShanbeiParam paramTth =  defaultParam();
        if (!result.getParamMap().containsKey("3号桥")){
            param3.setArea(690.0);
            result.getParamMap().put("3号桥",param3);
        }
        if (!result.getParamMap().containsKey("楼庄子")){
            paramLzz.setArea(1174.0);
            result.getParamMap().put("楼庄子",paramLzz);
        }
        if (!result.getParamMap().containsKey("头屯河")){
            paramTth.setArea(259.0);
            result.getParamMap().put("楼头区间",paramTth);
        }
        if (result.getParamMap().containsKey("头屯河")){
            result.getParamMap().put("楼头区间",result.getParamMap().get("头屯河"));
        }
    }
    private ShanbeiParam defaultParam() {
        ShanbeiParam shanbeiParam = new ShanbeiParam();
        shanbeiParam.setArea(0.0);
        shanbeiParam.setFB(0.008);
        shanbeiParam.setWM(120.0);
        shanbeiParam.setKC(1.0);
        shanbeiParam.setFC(32.0);
        shanbeiParam.setFM(100.0);
        shanbeiParam.setK(0.022);
        shanbeiParam.setB(0.3);
        shanbeiParam.setCS(0.966);
        return shanbeiParam;
    }
    public void stationEmpty(ForecastInputParamNew result){
        LzzHydrologyParam lzzHydrologyParam = result.getLzzHydrologyParam();
        /*
         * 水位站数据的前期处理
         */
//        //三号桥流量异常去除
//        List<LzzGaugingStation> THQ = lzzHydrologyParam.getThreeGaugingStation();
//        if (THQ.isEmpty()) {
//            throw new RuntimeException("未获取数据库中三号桥近二十日流量数据");
//        }
//        List<LzzGaugingStation> THQresult = new ArrayList<>();
//        for (int i = 0; i < THQ.size(); i++) {
//            LzzGaugingStation station = THQ.get(i);
//            //去除空值数据
//            String id = station.getId();
//            String[] parts = id.split(":");
//            String bridgeNumber = parts[0];
//            long numericValue = Long.parseLong(parts[1]);
//            Date date = new Date(numericValue); // 根据时间戳创建日期对象
//            int month = timeUtils.getSpecificDate(date).get("月");
//            if (parts.length > 1 && bridgeNumber.length() > 1) {
//                if (month <= 6 || month >= 9) {
//                    if (station.getFlow() != null && station.getFlow() <= 100) {
//                        THQresult.add(THQ.get(i));
//                    }
//                } else {
//                    if (station.getFlow() != null && station.getFlow() <= 300) {
//                        THQresult.add(THQ.get(i));
//                    }
//                }
//            }
//        }
//        lzzHydrologyParam.setThreeGaugingStation(THQresult);
        //楼庄子进库站流量去除异常
        List<LzzGaugingStation> LZZ = lzzHydrologyParam.getLzzInput();
        if (LZZ.isEmpty()) {
            Date time = result.getPredictionTime();
            for (int i = 0; i < 480; i++) {
                LzzGaugingStation empty = new LzzGaugingStation();
                empty.setFlow(0.0);
                empty.setStationName("楼庄子入库水位站");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(time);
                calendar.add(Calendar.HOUR_OF_DAY, (i - 480));
                empty.setGatherTime(calendar.getTime());
                long timestampInMilliseconds = calendar.getTime().toInstant().toEpochMilli();
                empty.setId("楼庄子入库水位站:" + timestampInMilliseconds);
                LZZ.add(empty);
            }
        }
        List<LzzGaugingStation> LZZresult = new ArrayList<>();
        for (LzzGaugingStation station : LZZ) {
            String id = station.getId();
            String[] parts = id.split(":");
            String bridgeNumber = parts[0];
            long numericValue = Long.parseLong(parts[1]);
            Date date = new Date(numericValue); // 根据时间戳创建日期对象
            int month = timeUtils.getSpecificDate(date).get("月");
            if (bridgeNumber.length() > 1) {
                if (month <= 6 || month >= 9) {
                    if (station.getFlow() != null && station.getFlow() <= 100) {
                        LZZresult.add(station);
                    }
                } else {
                    if (station.getFlow() != null && station.getFlow() <= 300) {
                        LZZresult.add(station);
                    }
                }
            }
        }
        lzzHydrologyParam.setLzzInput(LZZresult);
        lzzHydrologyParam = lzzNullTemOrFlow(lzzHydrologyParam);
        result.setLzzHydrologyParam(lzzHydrologyParam);
        /*
         * 区间数据的前期处理
         */
        //区间数据站点名空值处理
        IrrigatedHydrologyParam irrigatedHydrologyParam = result.getIrrigatedHydrologyParam();
        irrigatedHydrologyParam = irrigateStationProcessing(irrigatedHydrologyParam);
        result.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
    }

    public void getDaysData(ForecastInputParamNew result){
        int l = result.getPeriodTimeNum() * result.getPeriodTimeStep();
        Date predictionTime = result.getPredictionTime();
        Date endTime = timeUtils.addCalendar(predictionTime, "小时", l);
        List<PredictInputData> preRainTem = new ArrayList<>();
        if (endTime.before(InputUtils.historyDate)){
            //不需要预报数据，根据实测数据导入
            preRainTem = new ArrayList<>();
            Object[][] three = InputUtils.historyData.get("3号桥日");
            List<Object[]> threeList = timeUtils.getTimeIntervalList(three,predictionTime,endTime);
            Object[][] lzz = InputUtils.historyData.get("楼庄子日");
            List<Object[]> lzzList = timeUtils.getTimeIntervalList(lzz,predictionTime,endTime);
            Object[][] qj = InputUtils.historyData.get("楼头区间日");
            List<Object[]> qjList = timeUtils.getTimeIntervalList(qj,predictionTime,endTime);
            for (int i = 0; i < l; i++) {
                Date date = timeUtils.addCalendar(predictionTime,"日",i);
                preRainTem.add(daysDataListToPID(threeList,"3号桥",date));
                preRainTem.add(daysDataListToPID(lzzList,"楼庄子",date));
                preRainTem.add(daysDataListToPID(qjList,"楼头区间",date));
            }
        }
        else {
            //需要气象局提供预报的天气数据
            if (result.getPreRainTem()==null){
                List<PredictInputData> three = new ArrayList<>();
                List<PredictInputData> lzz = new ArrayList<>();
                List<PredictInputData> qj = new ArrayList<>();
                for (int i = 0; i < l; i++) {
                    PredictInputData dataLzz = new PredictInputData();
                    PredictInputData dataQj = new PredictInputData();
                    double[] tfLzz = setNullTempRain("楼庄子", timeUtils.addCalendar(result.getPredictionTime(),"日",i));
                    double[] tfQj = setNullTempRain("楼头区间", timeUtils.addCalendar(result.getPredictionTime(),"日",i));
                    dataLzz.setDates( timeUtils.addCalendar(result.getPredictionTime(),"日",i));
                    dataQj.setDates( timeUtils.addCalendar(result.getPredictionTime(),"日",i));
                    dataLzz.setTemperature(tfLzz[0]);
                    dataQj.setTemperature(tfQj[0]);
                    dataLzz.setRainfall(0.0);
                    dataQj.setRainfall(0.0);
                    three.add(dataLzz);
                    lzz.add(dataLzz);
                    qj.add(dataQj);
                }
                preRainTem.addAll(three);
                preRainTem.addAll(lzz);
                preRainTem.addAll(qj);
            }else {
                preRainTem = result.getPreRainTem();
                List<PredictInputData> three = new ArrayList<>();
                List<PredictInputData> lzz = new ArrayList<>();
                List<PredictInputData> qj = new ArrayList<>();
                for (PredictInputData predictInputData : preRainTem) {
                    if (predictInputData.getLocation().equals("3号桥")) {
                        three.add(predictInputData);
                    } else if (predictInputData.getLocation().equals("楼庄子")) {
                        lzz.add(predictInputData);
                    } else {
                        qj.add(predictInputData);
                    }
                }
                if (three.size()<l){
                    three = daysDataEmpty(three,"3号桥",l,predictionTime);
                }
                if (lzz.size()<l){
                    lzz = daysDataEmpty(three,"楼庄子",l,predictionTime);
                }
                if (qj.size()<l){
                    qj = daysDataEmpty(three,"楼头区间",l,predictionTime);
                }
                preRainTem.addAll(three);
                preRainTem.addAll(lzz);
                preRainTem.addAll(qj);
            }

        }
        result.setPreRainTem(preRainTem);
    }

    /**
     * 如果提供预报日尺度数据缺少，则补充均值
     */
    public List<PredictInputData> daysDataEmpty(List<PredictInputData> three,String location,int l ,Date predictionTime){
        List<Object[]> threeList = new ArrayList<>();
        for (PredictInputData predictInputData : three) {
            Object[] a = new Object[4];
            a[0] = predictInputData.getDates();
            a[1] = "不提供预报径流";
            a[2] = predictInputData.getTemperature();
            a[3] = predictInputData.getRainfall();
            threeList.add(a);
        }
        three = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            Date date = timeUtils.addCalendar(predictionTime,"日",i);
            three.add(daysDataListToPID(threeList,location,date));
        }
        return three;
    }

    /**
     * 从List里面获取所需日期的数据，如果没有则赋值月均值
     */
    public PredictInputData daysDataListToPID(List<Object[]> threeList, String location, Date date){
        PredictInputData threeData = new PredictInputData();
        threeData.setLocation(location);
        threeData.setDates(date);
        boolean getData = false;
        for (Object[] objects : threeList) {
            if (timeUtils.DateCompare((Date) objects[0], date, "日")) {
                threeData.setFlow((Double) objects[1]);
                threeData.setTemperature((Double) objects[2]);
                threeData.setRainfall(rainStringToDouble(objects));
                getData = true;
            }
        }
        if (!getData){
            double[] temFlow = setNullTempRain(location, date);
            threeData.setTemperature(temFlow[0]);
            threeData.setFlow(temFlow[1]);
            threeData.setRainfall(0.0);
        }
        return threeData;
    }

    /**
     * 根据月份来提供空值的温度和径流
     * @return result[0]为温度
     * result[1]为径流
     */
    public double[] setNullTempRain(String location,Date date) {
        int month = timeUtils.getSpecificDate(date).get("月");
        double[] result = new double[2];
        double[] temperatures;
        double[] flow;
        if (location.equals("3号桥")||location.equals("楼庄子")){
            temperatures = new double[]{-11.47 , -8.2, 0.3, 8.12, 17.6, 18.06, 20.37, 19.2, 13.76, 6.14, -2.21, -9.5};
            flow = new double[]{1.409, 1.29, 1.47, 2.64, 8.87, 19.21, 19.35, 14.7, 7.02, 3.65, 2.38, 1.7};
        }
        else {
            temperatures = new double[]{-11.47 , -8.2, 0.3, 8.12, 17.6, 18.06, 20.37, 19.2, 13.76, 6.14, -2.21, -9.5};
            flow = new double[]{0.16, 0.12, 0.83, 0.84, 0.64, 0.93, 0.85, 0.58, 0.29, 0.14, 0.08, 0.003};
        }
        result[0] = temperatures[month-1];
        result[1] = flow[month-1];
        return result;
    }

    /**
     * 把降水更换可输入的格式（4.5A）
     */
    public Double rainStringToDouble(Object[] input){
        Double result;
        int month = timeUtils.getSpecificDate((Date) input[0]).get("月");
        if (month>=6&&month<=9){
            if (input[3] instanceof String) {
                String r = (String) input[3];
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(r);
                if (!matcher.find()) {
                    result = 0.0;
                } else {
                    result = Double.parseDouble(r.replaceAll("[^0-9.]", ""));
                }
            }
            else {
                result = (Double) input[3];
            }
        }
        else {
            if (input[3] instanceof String) {
                result = 0.0;
            }else {
                result = (Double) input[3];
            }
        }
        return result;
    }

    /**
     * 预报降水小时转日
     */
    @SneakyThrows
    public List<PredictInputData> preRainHourToDay(List<RainFallDto> input)  {
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
     */
    @SneakyThrows
    public List<PredictInputData> getHoursRain(ForecastInputParamNew param, List<PredictInputData> input,String location){
        List<PredictInputData> result = new ArrayList<>();
        PredictInputData data = new PredictInputData();
        //获得开始时间和结束时间，分情况判断
        Date dateStart = param.getPredictionTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStart);
        calendar.add(Calendar.HOUR_OF_DAY, -InputUtils.beforeHours);
        dateStart = calendar.getTime();//找到落地雨前n小时
        int n = InputUtils.beforeHours + param.getPeriodTimeNum() * param.getPeriodTimeStep();//需要预报的时间长度
        calendar.add(Calendar.HOUR_OF_DAY, n);
        Date dataEnd = calendar.getTime();//预报结束时间
        String station ;
        List<RainFallDto> rainPre;

        //找到最贴近的时间
        List<Date> dateList = new ArrayList<>();
        for (PredictInputData predictInputData : input) {
            dateList.add(predictInputData.getDates());
        }
        int d = timeUtils.findNearestTime(dateList, dateStart);

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
                                    data.setLocation(location);
                                    data.setDates(dateStart);
                                    data.setTemperature(rainPre.get(j).getTemperature());
                                    data.setRainfall(rainPre.get(j).getRainFall());
                                    break;
                                } else {
                                    data = assignmentNullRAndT(dateStart, location);
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
                            }
                            else {
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

        return result;
    }

    /**
     * 保留前20天雨量
     */
    public List<PredictInputData> getTwentyDaysRain(ForecastInputParamNew param, List<PredictInputData> input,String location) {
        List<PredictInputData> result = new ArrayList<>();
        Date dateStart = param.getPredictionTime();
        PredictInputData data = new PredictInputData();
        if (input.isEmpty()){//未从数据库中获取雨量站数据
            for (int i = 0; i < InputUtils.beforeDays; i++) {
                Date date = timeUtils.addCalendar(dateStart,"日",i);
                data = assignmentNullRAndT(date,location);
                result.add(data);
            }
            return result;
        }
        List<Date> dateList = new ArrayList<>();
        for (PredictInputData predictInputData : input) {
            dateList.add(predictInputData.getDates());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStart);
        calendar.add(Calendar.DAY_OF_MONTH, -InputUtils.beforeDays);
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
     * @return surfaceData
     * 该流域的面雨量
     */
    public List<PredictInputData> pointToSurface(Map<String,List<PredictInputData>> pointData,  String location) {
        List<PredictInputData> result = new ArrayList<>();
        switch (location) {
            case "3号桥": {
                int l = pointData.get("八一林场自动雨量站").size();
                for (int i = 0; i < l; i++) {
                    double rainFall = 0.0;
                    double temperature;
                    PredictInputData hourResult = new PredictInputData();
                    rainFall += pointData.get("八一林场自动雨量站").get(i).getRainfall() * 0.344401;
                    rainFall += pointData.get("加普沙自动雨量站").get(i).getRainfall() * 0.147571;
                    rainFall += pointData.get("东南沟自动雨量站").get(i).getRainfall() * 0.156022;
                    rainFall += pointData.get("宰尔德自动雨量站").get(i).getRainfall() * 0.042438;
                    rainFall += pointData.get("无名沟自动雨量站").get(i).getRainfall() * 0.019251;
                    rainFall += pointData.get("萨尔达万自动雨量站").get(i).getRainfall() * 0.024912;
                    rainFall += pointData.get("煤矿沟自动雨量站").get(i).getRainfall() * 0.018891;
                    temperature = pointData.get("制材厂自动雨量站").get(i).getTemperature();
                    hourResult.setDates(pointData.get("八一林场自动雨量站").get(i).getDates());
                    hourResult.setLocation("面雨量");
                    hourResult.setRainfall(rainFall);
                    hourResult.setTemperature(temperature);
                    result.add(hourResult);
                }
                break;
            }
            case "楼庄子": {
                int l = pointData.get("八一林场自动雨量站").size();
                for (int i = 0; i < l; i++) {
                    double rainFall = 0.0;
                    double temperature;
                    PredictInputData hourResult = new PredictInputData();
                    rainFall += pointData.get("八一林场自动雨量站").get(i).getRainfall() * 0.344401;
                    rainFall += pointData.get("加普沙自动雨量站").get(i).getRainfall() * 0.147571;
                    rainFall += pointData.get("东南沟自动雨量站").get(i).getRainfall() * 0.156022;
                    rainFall += pointData.get("宰尔德自动雨量站").get(i).getRainfall() * 0.042438;
                    rainFall += pointData.get("无名沟自动雨量站").get(i).getRainfall() * 0.019251;
                    rainFall += pointData.get("萨尔达万自动雨量站").get(i).getRainfall() * 0.024912;
                    rainFall += pointData.get("煤矿沟自动雨量站").get(i).getRainfall() * 0.018891;
                    rainFall += pointData.get("黑沟自动雨量站").get(i).getRainfall() * 0.044157;
                    rainFall += pointData.get("喀什沟自动雨量站").get(i).getRainfall() * 0.082419;
                    rainFall += pointData.get("制材厂自动雨量站").get(i).getRainfall() * 0.115105;
                    temperature = pointData.get("制材厂自动雨量站").get(i).getTemperature();
                    hourResult.setDates(pointData.get("八一林场自动雨量站").get(i).getDates());
                    hourResult.setLocation("面雨量");
                    hourResult.setRainfall(rainFall);
                    hourResult.setTemperature(temperature);
                    result.add(hourResult);
                }
                break;
            }
            case "楼头区间":
            case "头屯河": {
                int l = pointData.get("小渠子雨量站").size();
                for (int i = 0; i < l; i++) {
                    double rainFall = 0.0;
                    PredictInputData hourResult = new PredictInputData();
                    rainFall += pointData.get("小渠子雨量站").get(i).getRainfall() * 0.284;
                    rainFall += pointData.get("团结一队雨量站").get(i).getRainfall() * 0.1948;
                    rainFall += pointData.get("头屯河水库雨量站").get(i).getRainfall() * 0.51188;
                    hourResult.setDates(pointData.get("小渠子雨量站").get(i).getDates());
                    hourResult.setLocation("面雨量");
                    hourResult.setRainfall(rainFall);
                    hourResult.setTemperature(setNullTempRain("楼头区间", pointData.get("小渠子雨量站").get(i).getDates())[0]);
                    result.add(hourResult);
                }
                break;
            }
        }
        return result;
    }

    /*
     * 楼庄子数据库数据处理
     */

    /**
     * 将楼庄子输入数据转化为PredictInputData格式
     *
     * @param entity 楼庄子上游数据
     * @return
     * result.get(0)喀什沟雨量站小时尺度站点+时间+降水+温度
     * result.get(1)黑沟雨量站小时尺度站点+时间+降水+温度
     * result.get(2)煤矿沟雨量站小时尺度站点+时间+降水+温度
     * result.get(3)无名沟雨量站小时尺度站点+时间+降水+温度
     * result.get(4)加普沙雨量站小时尺度站点+时间+降水+温度
     * result.get(5)宰尔德雨量站小时尺度站点+时间+降水+温度
     * result.get(6)东南沟雨量站小时尺度站点+时间+降水+温度
     * result.get(7)八一林场雨量站小时尺度站点+时间+降水+温度
     * result.get(8)萨尔达万雨量站小时尺度站点+时间+降水+温度
     * result.get(9)制材厂雨量站小时尺度站点+时间+降水+温度
     * result.get(10)三号桥日尺度站点+时间+径流
     * result.get(11)楼庄子入库日尺度站点+时间+径流
     * result.get(12)楼庄子出库日尺度站点+时间+径流
     */
    public List<List<PredictInputData>> lzzDataConversion(ForecastInputParamNew entity) {
        List<List<PredictInputData>> result = new ArrayList<>();
        Date dateStart = entity.getDataStartTime();
        Date dateEnd = entity.getPredictionTime();
        //喀什沟雨量站
        List<LzzRainfallStation> KSG = entity.getLzzHydrologyParam().getKsgRainfallStation();
        List<PredictInputData> KASHI = lzzRainConversion(KSG,dateStart,dateEnd,"喀什沟自动雨量站");
        result.add(KASHI);
        //黑沟雨量站
        List<LzzRainfallStation> HG = entity.getLzzHydrologyParam().getHgRainfallStation();
        List<PredictInputData> HEIGOU = lzzRainConversion(HG,dateStart,dateEnd,"黑沟自动雨量站");
        result.add(HEIGOU);
        //煤矿沟雨量站
        List<LzzRainfallStation> MKG = entity.getLzzHydrologyParam().getMkgRainfallStation();
        List<PredictInputData> MEI = lzzRainConversion(MKG,dateStart,dateEnd,"煤矿沟自动雨量站");
        result.add(MEI);
        //无名沟雨量站
        List<LzzRainfallStation> WMG = entity.getLzzHydrologyParam().getWmgRainfallStation();
        List<PredictInputData> WUMING = lzzRainConversion(WMG,dateStart,dateEnd,"无名沟自动雨量站");
        result.add(WUMING);
        //加普沙雨量站
        List<LzzRainfallStation> JPS = entity.getLzzHydrologyParam().getJpsRainfallStation();
        List<PredictInputData> JIA = lzzRainConversion(JPS,dateStart,dateEnd,"加普沙自动雨量站");
        result.add(JIA);
        //宰尔德雨量站
        List<LzzRainfallStation> ZED = entity.getLzzHydrologyParam().getZrdRainfallStation();
        List<PredictInputData> ZAI = lzzRainConversion(ZED,dateStart,dateEnd,"宰尔德自动雨量站");
        result.add(ZAI);
        //东南沟雨量站
        List<LzzRainfallStation> DNG = entity.getLzzHydrologyParam().getDngRainfallStation();
        List<PredictInputData> DONG = lzzRainConversion(DNG,dateStart,dateEnd,"东南沟自动雨量站");
        result.add(DONG);
        //八一林场雨量站
        List<LzzRainfallStation> BYLC = entity.getLzzHydrologyParam().getBylcRainfallStation();
        List<PredictInputData> BAYI = lzzRainConversion(BYLC,dateStart,dateEnd,"八一林场自动雨量站");
        result.add(BAYI);
        //萨尔达万雨量站
        List<LzzRainfallStation> SEDW = entity.getLzzHydrologyParam().getSedwRainfallStation();
        List<PredictInputData> SAER = lzzRainConversion(SEDW,dateStart,dateEnd,"萨尔达万自动雨量站");
        result.add(SAER);
        //制材厂雨量站
        List<LzzRainfallStation> ZCC = entity.getLzzHydrologyParam().getZccRainfallStation();
        List<PredictInputData> ZHI = lzzRainConversion(ZCC,dateStart,dateEnd,"制材厂自动雨量站");
        result.add(ZHI);
        if (entity.getModelType() != 3) {
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
        }
        return result;
    }

    /**
     * 楼庄子上游流量数据转化
     * @return 站点名称、日尺度时间、流量
     * （选择时间为2024年及以后则返回每一天的值，24年以前之间返回原始数据不做其他处理）
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
//            int year = timeUtils.getSpecificDate(date).get("年");
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
        /*
         * 保证数据连续性
         */
        List<PredictInputData> resultEnd = new ArrayList<>();
        if (yearEnd >= 2024) {
            // 计算相差天数并返回
            int n = timeUtils.duration(dateStart, dateEnd, "日");
            for (int i = 0; i < n; i++) {
                PredictInputData data = new PredictInputData();
                for (PredictInputData predictInputData : result) {
                    Date date = predictInputData.getDates();
                    Boolean dateCompare = timeUtils.DateCompare(date, dateStart, "日");
                    if (dateCompare) {
                        data = predictInputData;
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
     */
    public List<PredictInputData> lzzFlowError(List<PredictInputData> input) {
        List<PredictInputData> result;
        for (PredictInputData predictInputData : input) {
            if (predictInputData.getFlow() == 0) {
                Date date = predictInputData.getDates();
                int month = timeUtils.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        predictInputData.setFlow(1.29);
                        break;
                    case 2:
                        predictInputData.setFlow(1.19);
                        break;
                    case 3:
                        predictInputData.setFlow(1.77);
                        break;
                    case 4:
                        predictInputData.setFlow(2.78);
                        break;
                    case 5:
                        predictInputData.setFlow(8.13);
                        break;
                    case 6:
                        predictInputData.setFlow(17.92);
                        break;
                    case 7:
                        predictInputData.setFlow(22.05);
                        break;
                    case 8:
                        predictInputData.setFlow(16.13);
                        break;
                    case 9:
                        predictInputData.setFlow(7.84);
                        break;
                    case 10:
                        predictInputData.setFlow(3.85);
                        break;
                    case 11:
                        predictInputData.setFlow(2.23);
                        break;
                    case 12:
                        predictInputData.setFlow(1.52);
                        break;
                }
            }
            //去除枯水月份过大流量
            Date date = predictInputData.getDates();
            int month = timeUtils.getSpecificDate(date).get("月");
            if (month <= 2 || month >= 10) {
                if (predictInputData.getFlow() >= 10) {
                    switch (month) {
                        case 1:
                            predictInputData.setFlow(1.29);
                            break;
                        case 2:
                            predictInputData.setFlow(1.19);
                            break;
                        case 10:
                            predictInputData.setFlow(3.85);
                            break;
                        case 11:
                            predictInputData.setFlow(2.23);
                            break;
                        case 12:
                            predictInputData.setFlow(1.52);
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
     * @return 小时尺度站点名、时间、雨量、温度
     */
    public List<PredictInputData> lzzRainConversion(List<LzzRainfallStation> input,Date dateStart,Date dateEnd,String location) {
        List<PredictInputData> resultMid = new ArrayList<>();
        if (input.isEmpty()){
            int l = timeUtils.duration(dateStart,dateEnd,"小时");
            for (int i = 0; i < l; i++) {
                PredictInputData predictInputData = new PredictInputData();
                predictInputData.setRainfall(0.0);
                predictInputData.setDates(timeUtils.addCalendar(dateStart,"小时",i));
                predictInputData.setTemperature(setNullTempRain(location, dateStart)[0]);
                predictInputData.setLocation(location);
                resultMid.add(predictInputData);
            }
        }else {
            for (LzzRainfallStation lzzRainfallStation : input) {
                String id = lzzRainfallStation.getId();
                // 使用间隔符提取数字部分
                String[] parts = id.split(":");
                String bridgeNumber = parts[0];
                long numericValue = Long.parseLong(parts[1]);
                Date date = new Date(numericValue); // 根据时间戳创建日期对象
                //储存相应数据
                PredictInputData piece = new PredictInputData();
                piece.setLocation(bridgeNumber);
                piece.setDates(date);
                piece.setRainfall(lzzRainfallStation.getRainfall().doubleValue());
                piece.setTemperature(lzzRainfallStation.getTemperature().doubleValue());
                resultMid.add(piece);
            }
        }
        return resultMid;
    }

    /**
     * 楼庄子上游雨量站小时尺度转日尺度
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
     * @return 前期雨量和小时尺度降水
     */
    @SneakyThrows
    public List<Map<String,List<PredictInputData>>> lzzRainIntegration(ForecastInputParamNew paramNew) {
        List<Map<String,List<PredictInputData>>> result = new ArrayList<>();
        //雨量站整合
        Map<String,List<PredictInputData>> RainHour = new HashMap<>();
        Map<String,List<PredictInputData>> RainDay = new HashMap<>();
        //喀什沟
        List<List<PredictInputData>> lzzData = lzzDataConversion(paramNew);
        List<PredictInputData> KSG = lzzData.get(0);
        KSG = getHoursRain(paramNew, KSG,"喀什沟自动雨量站");
        List<PredictInputData> KSGDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getKsgRainfallStation());
        KSGDAY = getTwentyDaysRain(paramNew, KSGDAY,"喀什沟自动雨量站");
        RainDay.put("喀什沟自动雨量站",KSGDAY);
        RainHour.put("喀什沟自动雨量站",KSG);
        //黑沟
        List<PredictInputData> HG = lzzData.get(1);
        HG = getHoursRain(paramNew, HG,"黑沟自动雨量站");
        List<PredictInputData> HGDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getHgRainfallStation());
        HGDAY = getTwentyDaysRain(paramNew, HGDAY,"黑沟自动雨量站");
        RainDay.put("黑沟自动雨量站",HGDAY);
        RainHour.put("黑沟自动雨量站",HG);
        //煤矿沟
        List<PredictInputData> MKG = lzzData.get(2);
        MKG = getHoursRain(paramNew, MKG,"煤矿沟自动雨量站");
        List<PredictInputData> MKGDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getMkgRainfallStation());
        MKGDAY = getTwentyDaysRain(paramNew, MKGDAY,"煤矿沟自动雨量站");
        RainDay.put("煤矿沟自动雨量站",MKGDAY);
        RainHour.put("煤矿沟自动雨量站",MKG);
        //无名沟
        List<PredictInputData> WMG = lzzData.get(3);
        WMG = getHoursRain(paramNew, WMG,"无名沟自动雨量站");
        List<PredictInputData> WMGDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getWmgRainfallStation());
        WMGDAY = getTwentyDaysRain(paramNew, WMGDAY,"无名沟自动雨量站");
        RainDay.put("无名沟自动雨量站",WMGDAY);
        RainHour.put("无名沟自动雨量站",WMG);
        //加普沙
        List<PredictInputData> JPS = lzzData.get(4);
        JPS = getHoursRain(paramNew, JPS,"加普沙自动雨量站");
        List<PredictInputData> JPSDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getJpsRainfallStation());
        JPSDAY = getTwentyDaysRain(paramNew, JPSDAY,"加普沙自动雨量站");
        RainDay.put("加普沙自动雨量站",JPSDAY);
        RainHour.put("加普沙自动雨量站",JPS);
        //宰尔德
        List<PredictInputData> ZED = lzzData.get(5);
        ZED = getHoursRain(paramNew, ZED,"宰尔德自动雨量站");
        List<PredictInputData> ZEDDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getZrdRainfallStation());
        ZEDDAY = getTwentyDaysRain(paramNew, ZEDDAY,"宰尔德自动雨量站");
        RainDay.put("宰尔德自动雨量站",ZEDDAY);
        RainHour.put("宰尔德自动雨量站",ZED);
        //东南沟
        List<PredictInputData> DNG = lzzData.get(6);
        DNG = getHoursRain(paramNew, DNG,"东南沟自动雨量站");
        List<PredictInputData> DNGDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getDngRainfallStation());
        DNGDAY = getTwentyDaysRain(paramNew, DNGDAY,"东南沟自动雨量站");
        RainDay.put("东南沟自动雨量站",DNGDAY);
        RainHour.put("东南沟自动雨量站",DNG);
        //八一林场
        List<PredictInputData> BYLC = lzzData.get(7);
        BYLC = getHoursRain(paramNew, BYLC,"八一林场自动雨量站");
        List<PredictInputData> BYLCDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getBylcRainfallStation());
        BYLCDAY = getTwentyDaysRain(paramNew, BYLCDAY,"八一林场自动雨量站");
        RainDay.put("八一林场自动雨量站",BYLCDAY);
        RainHour.put("八一林场自动雨量站",BYLC);
        //萨尔达万
        List<PredictInputData> SEDW = lzzData.get(8);
        SEDW = getHoursRain(paramNew, SEDW,"萨尔达万自动雨量站");
        List<PredictInputData> SEDWDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getSedwRainfallStation());
        SEDWDAY = getTwentyDaysRain(paramNew, SEDWDAY,"萨尔达万自动雨量站");
        RainDay.put("萨尔达万自动雨量站",SEDWDAY);
        RainHour.put("萨尔达万自动雨量站",SEDW);
        //制材厂
        List<PredictInputData> ZCC = lzzData.get(9);
        ZCC = getHoursRain(paramNew, ZCC,"制材厂自动雨量站");
        List<PredictInputData> ZCCDAY = lzzRainHourToDay(paramNew.getLzzHydrologyParam().getZccRainfallStation());
        ZCCDAY = getTwentyDaysRain(paramNew, ZCCDAY,"制材厂自动雨量站");
        RainDay.put("制材厂自动雨量站",ZCCDAY);
        RainHour.put("制材厂自动雨量站",ZCC);
        //添加小时尺度雨量和日尺度雨量
        result.add(RainHour);
        result.add(RainDay);
        return result;
    }


    /**
     * 对没有值的流量和温度进行赋值，赋值为最近的一个
     */
    public LzzHydrologyParam lzzNullTemOrFlow(LzzHydrologyParam lzzHydrologyParam){
        /*
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
        List<LzzGaugingStation> LZZ = lzzHydrologyParam.getLzzInput();
        LZZ = lzzFlowProcessing(LZZ);
        lzzHydrologyParam.setLzzInput(LZZ);
        List<LzzGaugingStation> Three = lzzHydrologyParam.getThreeGaugingStation();
        Three = lzzFlowProcessing(Three);
        lzzHydrologyParam.setThreeGaugingStation(Three);
        return lzzHydrologyParam;
    }

    /**
     * 温度值的处理，空值时等同于下一个有值的温度
     */
    public List<LzzRainfallStation> lzzTemProcessing(List<LzzRainfallStation> inputData) {
        List<LzzRainfallStation> result = new ArrayList<>();
        for (LzzRainfallStation inputDatum : inputData) {
            //去除空值异常
            String id = inputDatum.getId();
            String[] parts = id.split(":");
            String bridgeNumber = parts[0];
            if (parts.length > 1 && bridgeNumber.length() > 1) {
                result.add(inputDatum);
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
                    if (n == result.size()){
                        result.get(i).setTemperature(BigDecimal.valueOf(10.0));
                    }else {
                        BigDecimal T = result.get(i - 1).getTemperature();
                        result.get(i).setTemperature(T);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 流量的处理，空值时等同于下一个有值的流量
     */
    public List<LzzGaugingStation> lzzFlowProcessing(List<LzzGaugingStation> inputData) {
        List<LzzGaugingStation> result = new ArrayList<>();
        for (LzzGaugingStation inputDatum : inputData) {
            //去除空值异常
            String id = inputDatum.getId();
            String[] parts = id.split(":");
            String bridgeNumber = parts[0];
            if (parts.length > 1 && bridgeNumber.length() > 1) {
                result.add(inputDatum);
            }
        }
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getFlow() == null) {
                int n = 0;
                for (int j = 0; i + j < result.size(); j++) {
                    if ((result.get(i + j).getFlow() == null)) {
                        n++;
                    } else break;
                }
                if (n == result.size()){
                    result.get(i).setFlow(0.0);
                }
                if (i + n < result.size()) {
                    Double T = result.get(i + n).getFlow();
                    result.get(i).setFlow(T);
                } else {
                    Double T = result.get(i - 1).getFlow();
                    result.get(i).setFlow(T);
                }
            }
        }
        return result;
    }

    /*
     * 头屯河数据库数据处理
     */

    /**
     * 区间数据转化为PredictInputData格式
     * @return result.get(0)头屯河入库日尺度站点+时间+径流
     * result.get(1)小渠子站小时尺度站点+时间+降雨
     * result.get(2)团结一队小时尺度站点+时间+降雨
     * result.get(3)头屯河水库雨量站小时尺度站点+时间+降水
     */
    public List<List<PredictInputData>> irrigatedDataConversion(ForecastInputParamNew entity) {
        List<List<PredictInputData>> result = new ArrayList<>();
        Date dateStart = entity.getDataStartTime();
        Date dateEnd = entity.getPredictionTime();
        //头屯河入库流量
        List<IrrigatedPlatformDataInfo> TTHI = entity.getIrrigatedHydrologyParam().getTthInput();
        List<PredictInputData> TOIN = irrigateFlowConversion(TTHI);
        result.add(TOIN);
        //小渠子雨量站
        List<IrrigatedPlatformDataInfo> XQZ = entity.getIrrigatedHydrologyParam().getXqzGaugingStation();
        List<PredictInputData> XIAO = irrigateRainConversion(XQZ,dateStart,dateEnd,"小渠子雨量站");
        result.add(XIAO);
        //团结一队雨量站
        List<IrrigatedPlatformDataInfo> TJYD = entity.getIrrigatedHydrologyParam().getTjydGaugingStation();
        List<PredictInputData> TUANJIE = irrigateRainConversion(TJYD,dateStart,dateEnd,"团结一队雨量站");
        result.add(TUANJIE);
        //头屯河水库雨量站
        List<IrrigatedPlatformDataInfo> TTHR = entity.getIrrigatedHydrologyParam().getTthGaugingStation();
        List<PredictInputData> TORAIN = irrigateRainConversion(TTHR,dateStart,dateEnd,"头屯河水库雨量站");
        result.add(TORAIN);
        return result;
    }

    /**
     * 头屯河入库流量数据转化
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
     * @return 小时尺度的站点名、时间、雨量
     */
    public List<PredictInputData> irrigateRainConversion(List<IrrigatedPlatformDataInfo> input,Date dateStart,Date dateEnd,String location) {
        List<PredictInputData> result = new ArrayList<>();
        if (input.isEmpty()){
            int l = timeUtils.duration(dateStart,dateEnd,"小时");
            for (int i = 0; i < l; i++) {
                PredictInputData predictInputData = new PredictInputData();
                predictInputData.setRainfall(0.0);
                predictInputData.setDates(timeUtils.addCalendar(dateStart,"小时",i));
                predictInputData.setTemperature(setNullTempRain(location, dateStart)[0]);
                predictInputData.setLocation(location);
                result.add(predictInputData);
            }
        }else {
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
        }
        return result;
    }

    /**
     * 区间雨量站数据转化为日尺度
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
     * @return 24小时雨量和20天雨量
     */
    public List<Map<String,List<PredictInputData>>> irrigateRainIntegration(ForecastInputParamNew paramNew) {
        List<Map<String,List<PredictInputData>>> result = new ArrayList<>();

        //雨量站整合
        Map<String,List<PredictInputData>> RainHour = new HashMap<>();
        Map<String,List<PredictInputData>> RainDay= new HashMap<>();
        List<List<PredictInputData>> rainStation = irrigatedDataConversion(paramNew);
        //小渠子
        List<PredictInputData> XQZ = rainStation.get(1);
        XQZ = getHoursRain(paramNew, XQZ,"小渠子雨量站");
        List<PredictInputData> XQZDAY = irrigateRainHourToDay(paramNew.getIrrigatedHydrologyParam().getXqzGaugingStation());
        XQZDAY = getTwentyDaysRain(paramNew, XQZDAY,"小渠子雨量站");
        RainDay.put("小渠子雨量站",XQZDAY);
        RainHour.put("小渠子雨量站",XQZ);
        //团结一队
        List<PredictInputData> TJYD = rainStation.get(2);
        TJYD = getHoursRain(paramNew, TJYD,"团结一队雨量站");
        List<PredictInputData> TJYDDAY = irrigateRainHourToDay(paramNew.getIrrigatedHydrologyParam().getTjydGaugingStation());
        TJYDDAY = getTwentyDaysRain(paramNew, TJYDDAY,"团结一队雨量站");
        RainDay.put("团结一队雨量站",TJYDDAY);
        RainHour.put("团结一队雨量站",TJYD);
        //头屯河水库
        List<PredictInputData> TTHR = rainStation.get(3);
        TTHR = getHoursRain(paramNew, TTHR,"头屯河水库雨量站");
        List<PredictInputData> TTHRDAY = irrigateRainHourToDay(paramNew.getIrrigatedHydrologyParam().getTthGaugingStation());
        TTHRDAY = getTwentyDaysRain(paramNew, TTHRDAY,"头屯河水库雨量站");
        RainDay.put("头屯河水库雨量站",TTHRDAY);
        RainHour.put("头屯河水库雨量站",TTHR);
        //添加小时尺度雨量和日尺度雨量
        result.add(RainHour);
        result.add(RainDay);
        return result;
    }

    /**
     * 区间数据站点名为空的处理
     */
    public IrrigatedHydrologyParam irrigateStationProcessing(IrrigatedHydrologyParam inputData) {
        IrrigatedHydrologyParam result = new IrrigatedHydrologyParam();
        List<IrrigatedPlatformDataInfo> XQZ = inputData.getXqzGaugingStation();
        List<IrrigatedPlatformDataInfo> XQZresult = new ArrayList<>();
        for (IrrigatedPlatformDataInfo station : XQZ) {
            //去除空值数据
            String id = station.getId();
            String[] parts = id.split("-");
            String bridgeNumber = parts[0];
            if (parts.length > 1 && bridgeNumber.length() > 1) {
                XQZresult.add(station);
            }
        }
        result.setXqzGaugingStation(XQZresult);
        //团结一队雨量站
        List<IrrigatedPlatformDataInfo> TJYD = inputData.getTjydGaugingStation();
        List<IrrigatedPlatformDataInfo> TJYDresult = new ArrayList<>();
        for (IrrigatedPlatformDataInfo station : TJYD) {
            //去除空值数据
            String id = station.getId();
            String[] parts = id.split("-");
            String bridgeNumber = parts[0];
            if (parts.length > 1 && bridgeNumber.length() > 1) {
                TJYDresult.add(station);
            }

        }
        result.setTjydGaugingStation(TJYDresult);
        //头屯河雨量站
        List<IrrigatedPlatformDataInfo> TTH = inputData.getTthGaugingStation();
        List<IrrigatedPlatformDataInfo> TTHresult = new ArrayList<>();
        for (IrrigatedPlatformDataInfo station : TTH) {
            //去除空值数据
            String id = station.getId();
            String[] parts = id.split("-");
            String bridgeNumber = parts[0];
            if (parts.length > 1 && bridgeNumber.length() > 1) {
                TTHresult.add(station);
            }
        }
        result.setTthGaugingStation(TTHresult);
        //头屯河入库流量
        List<IrrigatedPlatformDataInfo> TTHI = inputData.getTthInput();
        List<IrrigatedPlatformDataInfo> TTHIresult = new ArrayList<>();
        for (IrrigatedPlatformDataInfo station : TTHI) {
            //去除空值数据
            String id = station.getId();
            String[] parts = id.split("-");
            String bridgeNumber = parts[0];
            if (parts.length > 1 && bridgeNumber.length() > 1) {
                TTHIresult.add(station);
            }
        }
        result.setTthInput(TTHIresult);
        return result;
    }

    /**
     * 区间数据尺度转化
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
                    info.setSqMonitorFlow(monitorFlow/number);
                    info.setYqRainFallOne(rainFall/number);
                }
                rainFall = 0.0;
                monitorFlow = 0.0;
                number = 0;
                resultList.add(info);
            }
        }
        return resultList;
    }

    /*
     * 温度值和降雨量的处理
     */


    /**
     * 温度和降水空值赋值
     */
    public PredictInputData assignmentNullRAndT(Date date, String location) {
        PredictInputData result = new PredictInputData();
        result.setDates(date);
        Double tem = setNullTempRain(location,date)[0];
        result.setTemperature(tem);
        result.setLocation(location);
        result.setRainfall(0.0);
        return result;
    }

    /**
     * 从数据库中获得水位站的对应日尺度温度与降水
     * @param param 从数据库中获得的数据
     * @return 日尺度温度与降水
     */
    public List<PredictInputData> getRAndT(ForecastInputParamNew param) {
        //雨量站整合
        Map<String,List<PredictInputData>> RainDay = new HashMap<>();
        //喀什沟
        List<PredictInputData> KSGDAY = lzzRainHourToDay(param.getLzzHydrologyParam().getKsgRainfallStation());
        KSGDAY = getTwentyDaysRain(param,KSGDAY,"喀什沟自动雨量站");
        RainDay.put("喀什沟自动雨量站",KSGDAY);
        //黑沟
        List<PredictInputData> HGDAY = lzzRainHourToDay(param.getLzzHydrologyParam().getHgRainfallStation());
        HGDAY = getTwentyDaysRain(param,HGDAY,"黑沟自动雨量站");
        RainDay.put("黑沟自动雨量站",HGDAY);
        //煤矿沟
        List<PredictInputData> MKGDAY = lzzRainHourToDay(param.getLzzHydrologyParam().getMkgRainfallStation());
        MKGDAY = getTwentyDaysRain(param,MKGDAY,"煤矿沟自动雨量站");
        RainDay.put("煤矿沟自动雨量站",MKGDAY);
        //无名沟
        List<PredictInputData> WMGDAY = lzzRainHourToDay(param.getLzzHydrologyParam().getWmgRainfallStation());
        WMGDAY = getTwentyDaysRain(param,WMGDAY,"无名沟自动雨量站");
        RainDay.put("无名沟自动雨量站",WMGDAY);
        //加普沙
        List<PredictInputData> JPSDAY = lzzRainHourToDay(param.getLzzHydrologyParam().getJpsRainfallStation());
        JPSDAY = getTwentyDaysRain(param,JPSDAY,"加普沙自动雨量站");
        RainDay.put("加普沙自动雨量站",JPSDAY);
        //宰尔德
        List<PredictInputData> ZEDDAY = lzzRainHourToDay(param.getLzzHydrologyParam().getZrdRainfallStation());
        ZEDDAY = getTwentyDaysRain(param,ZEDDAY,"宰尔德自动雨量站");
        RainDay.put("宰尔德自动雨量站",ZEDDAY);
        //东南沟
        List<PredictInputData> DNGDAY = lzzRainHourToDay(param.getLzzHydrologyParam().getDngRainfallStation());
        DNGDAY = getTwentyDaysRain(param,DNGDAY,"东南沟自动雨量站");
        RainDay.put("东南沟自动雨量站",DNGDAY);
        //八一林场
        List<PredictInputData> BYLCDAY = lzzRainHourToDay(param.getLzzHydrologyParam().getBylcRainfallStation());
        BYLCDAY = getTwentyDaysRain(param,BYLCDAY,"八一林场自动雨量站");
        RainDay.put("八一林场自动雨量站",BYLCDAY);
        //萨尔达万
        List<PredictInputData> SEDWDAY = lzzRainHourToDay(param.getLzzHydrologyParam().getSedwRainfallStation());
        SEDWDAY = getTwentyDaysRain(param,SEDWDAY,"萨尔达万自动雨量站");
        RainDay.put("萨尔达万自动雨量站",SEDWDAY);
        //制材厂
        List<PredictInputData> ZCCDAY = lzzRainHourToDay(param.getLzzHydrologyParam().getZccRainfallStation());
        ZCCDAY = getTwentyDaysRain(param,ZCCDAY,"制材厂自动雨量站");
        RainDay.put("制材厂自动雨量站",ZCCDAY);
        //添加日尺度温度与降水
        return pointToSurface(RainDay,  "楼庄子");
    }

    /**
     * 为水位站添加温度和降水
     */
    public List<PredictInputData> addRAndT(List<PredictInputData> WaterStation, List<PredictInputData> RAT) {
        List<PredictInputData> result;
        for (PredictInputData inputData : WaterStation) {
            for (PredictInputData predictInputData : RAT) {
                Boolean dateCompare = timeUtils.DateCompare(predictInputData.getDates(), inputData.getDates(), "日");
                if (dateCompare) {
                    double rain = predictInputData.getRainfall();
                    inputData.setRainfall(rain);
                    double temperature = predictInputData.getTemperature();
                    inputData.setTemperature(temperature);
                }
            }
            if (inputData.getRainfall() == null) {
                inputData.setRainfall(0.0);
            }
        }
        //为空日期赋值，赋值为0
        for (PredictInputData predictInputData : WaterStation) {
            if (predictInputData.getTemperature() == null) {
                Double tem = setNullTempRain(predictInputData.getLocation(),predictInputData.getDates())[0];
                predictInputData.setTemperature(tem);
            }
        }
        result = WaterStation;
        return result;
    }

    /**
     * 温度转化为蒸发量
     */
    public Object[][] temToEva(Object[][] data) {
        //按温度分配蒸发
//        double[] temperature = new double[data.length];
//        for (int i = 0; i < data.length; i++) {
//            temperature[i] = (Double) data[i][1];
//        }
//        double minValue = Arrays.stream(temperature).min().orElse(Double.NaN);
//        for (int i = 0; i < temperature.length; i++) {
//            temperature[i] -= minValue;
//        }
//        double sum = 0;
//        for (double num : temperature) {
//            sum += num;
//        }

        //按月份分配蒸发
        double evaporation = 0.0;
        for (Object[] datum : data) {
            int month = timeUtils.getSpecificDate((Date) datum[0]).get("月");
            switch (month) {
                case 1:
                    evaporation = 9.0 / 31 / 24;
                    break;
                case 2:
                    evaporation = 16.4 / 28 / 24;
                    break;
                case 3:
                    evaporation = 56.7 / 31 / 24;
                    break;
                case 4:
                    evaporation = 177.3 / 30 / 24;
                    break;
                case 5:
                    evaporation = 270.1 / 31 / 24;
                    break;
                case 6:
                    evaporation = 294.8 / 30 / 24;
                    break;
                case 7:
                    evaporation = 315.3 / 31 / 24;
                    break;
                case 8:
                    evaporation = 275.3 / 31 / 24;
                    break;
                case 9:
                    evaporation = 187.7 / 30 / 24;
                    break;
                case 10:
                    evaporation = 101.8 / 31 / 24;
                    break;
                case 11:
                    evaporation = 26.9 / 30 / 24;
                    break;
                case 12:
                    evaporation = 8.0 / 31 / 24;
                    break;
            }
        }
//        for (int i = 0; i < data.length; i++) {
//            if (sum == 0) {
//                data[i][1] = evaporation;
//            } else {
//                if (temperature[i] < 0) {
//                    temperature[i] = 0;
//                    data[i][1] = evaporation * temperature[i] / sum;
//                }
//                data[i][1] = evaporation * temperature[i] / sum;
//            }
//        }

        for (int i = 0; i < data.length; i++) {
            data[i][1] = evaporation;
        }
        return data;
    }

}


