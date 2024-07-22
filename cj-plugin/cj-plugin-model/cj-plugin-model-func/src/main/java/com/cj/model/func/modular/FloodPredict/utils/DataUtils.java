package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.model.func.modular.FloodPredict.entity.*;
import lombok.SneakyThrows;

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
    TimeUtils tu = new TimeUtils();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 从数据库导入的数据进行处理，包括对三号桥、楼庄子进库站异常流量的处理，上游雨量站温度空值的处理
     */
    public void emptyProcessing(ForecastInputParamNew param) {
        //输入数据的转化
        Date nowDate = new Date();
        int l = param.getPeriodTimeNum() * param.getPeriodTimeStep();
        Date startDate = param.getPredictionTime();
        Date endTime = tu.addCalendar(param.getPredictionTime(), "小时", l);
        //数据库中历史数据预处理
        if (param.getModelType()!=1) dataPreprocess(param);
        //雨情预报数据处理
        param.setIsSimulation(param.getIsSimulation() != null&&param.getIsSimulation());
        if (param.getModelType() == 3 && endTime.after(nowDate) && param.getRainFallDtos().isEmpty()) {
            List<RainFallDto> rainFallDtos = new ArrayList<>();
            for (int i = 0; i < l; i++) {
                RainFallDto rainFallDto = new RainFallDto();
                rainFallDto.setDate(tu.DateToString(tu.addCalendar(startDate, "小时", i)));
                rainFallDto.setRainFall(0.0);
                rainFallDto.setTemperature(setNullTemFlow("楼庄子",nowDate)[0]);
                rainFallDto.setArea("面雨量");
                rainFallDtos.add(rainFallDto);
            }
            param.setRainFallDtos(rainFallDtos);
        }
        if (endTime.after(InputUtils.historyDate)&&(param.getInflowRunoffs() == null || param.getInflowRunoffs().isEmpty())) {
            throw new RuntimeException("未获得A3表中进库流量数据");
        }
        //日尺度预报数据的处理
        if (param.getPeriodTimeType()==3||param.getPeriodTimeType()==4){
            getDaysData(param);
        }
    }
    /**
     * 获得日尺度预报时所需要的数据
     * @param result
     */
    public void getDaysData(ForecastInputParamNew result){
        int l = result.getPeriodTimeNum() * result.getPeriodTimeStep();
        Date predictionTime = result.getPredictionTime();
        Date endTime = tu.addCalendar(predictionTime, "小时", l);
        List<PredictInputData> preRainTem = new ArrayList<>();
        if (endTime.before(InputUtils.historyDate)){
            //不需要预报数据，根据实测数据导入
            preRainTem = new ArrayList<>();
            Object[][] three = InputUtils.historyData.get("3号桥日");
            List<Object[]> threeList = tu.getTimeIntervalList(three,predictionTime,endTime);
            Object[][] lzz = InputUtils.historyData.get("楼庄子日");
            List<Object[]> lzzList = tu.getTimeIntervalList(lzz,predictionTime,endTime);
            Object[][] qj = InputUtils.historyData.get("楼头区间日");
            List<Object[]> qjList = tu.getTimeIntervalList(qj,predictionTime,endTime);
            for (int i = 0; i < l; i++) {
                Date date = tu.addCalendar(predictionTime,"日",i);
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
                    double[] tfLzz = setNullTemFlow("楼庄子", tu.addCalendar(result.getPredictionTime(),"日",i));
                    double[] tfQj = setNullTemFlow("楼头区间", tu.addCalendar(result.getPredictionTime(),"日",i));
                    dataLzz.setDates( tu.addCalendar(result.getPredictionTime(),"日",i));
                    dataQj.setDates( tu.addCalendar(result.getPredictionTime(),"日",i));
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
    private List<PredictInputData> daysDataEmpty(List<PredictInputData> three,String location,int l ,Date predictionTime){
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
            Date date = tu.addCalendar(predictionTime,"日",i);
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
            if (tu.DateCompare((Date) objects[0], date, "日")) {
                threeData.setFlow((Double) objects[1]);
                threeData.setTemperature((Double) objects[2]);
                threeData.setRainfall(rainStringToDouble(objects));
                getData = true;
            }
        }
        if (!getData){
            double[] temFlow = setNullTemFlow(location, date);
            threeData.setTemperature(temFlow[0]);
            threeData.setFlow(temFlow[1]);
            threeData.setRainfall(0.0);
        }
        return threeData;
    }
    /**
     * 数据预处理（雨量站数据为空时赋值，温度为空时赋值，水文站空值赋值并且转换为小时尺度）
     * @param paramNew
     */
    public void dataPreprocess(ForecastInputParamNew paramNew){
        //雨量数据空值处理
        Date start = tu.addCalendar(paramNew.getPredictionTime(),"日",-InputUtils.beforeDays);
        Date end = tu.addCalendar(paramNew.getPredictionTime(),"小时",paramNew.getPeriodTimeNum()*paramNew.getPeriodTimeStep());
        paramNew.setRainfall(rainRemoveNull(paramNew.getRainfall(),start,end));
        //区间雨量站数据转化
        irrigateMinuteToHour(paramNew.getRainfall());
        //水文站数据处理
        Map<String,List<PredictInputData>> flowData = new HashMap<>();
        for (Map.Entry<String,List<LzzGaugingStation>> water: paramNew.getWaterLevel().entrySet()){
            String key = water.getKey();
            List<LzzGaugingStation> value = water.getValue();
            List<PredictInputData> flow = flowConversion(start,end,value);
            flowData.put(key,flow);
        }
        paramNew.setFlowData(flowData);
    }
    /**
     * 雨量站数据整合，前期雨量和小时尺度降水
     */
    @SneakyThrows
    public InputDataSet rainIntegration(ForecastInputParamNew paramNew) {
        InputDataSet result = new InputDataSet();
        //雨量站整合
        Map<String,List<RainFallDto>> RainHour = new HashMap<>();
        Map<String,List<RainFallDto>> RainDay = new HashMap<>();
        //各个雨量站
        Date start = tu.addCalendar(paramNew.getPredictionTime(),"日",-InputUtils.beforeDays);
        Date end = tu.addCalendar(paramNew.getPredictionTime(),"小时",paramNew.getPeriodTimeNum()*paramNew.getPeriodTimeStep());
        Map<String,List<RainFallDto>> lzzData = rainRemoveNull(paramNew.getRainfall(),start,end);
        for (Map.Entry<String, List<RainFallDto>> entry : lzzData.entrySet()) {
            String key = entry.getKey();
            List<RainFallDto> value = entry.getValue();
            RainHour.put(key, getHoursRain(paramNew,value,key));
            RainDay.put(key,getTwentyDaysRain(paramNew, rainHourToDay(value),key));
        }
        //添加小时尺度雨量和日尺度雨量
        result.setRainHourData(RainHour);
        result.setRainDayData(RainDay);
        return result;
    }

    /**
     * 返回预报开始时间前7天到预报结束时间之间的小时尺度的水文站流量数据
     * @param paramNew
     * @return
     */
    public Map<String,List<PredictInputData>> flowIntegration(ForecastInputParamNew paramNew){
        Map<String,List<PredictInputData>> result = new HashMap<>();
        Date start = tu.addCalendar(paramNew.getPredictionTime(),"日",-7);
        Date end = tu.addCalendar(paramNew.getPredictionTime(),"小时",paramNew.getPeriodTimeNum());
        Map<String,List<PredictInputData>> flowAll = paramNew.getFlowData();
        for (Map.Entry<String, List<PredictInputData>> entry : flowAll.entrySet()){
            String key = entry.getKey();
            List<PredictInputData> value = entry.getValue();
            List<PredictInputData> flow = new ArrayList<>();
            for (PredictInputData data:value){
                if (data.getDates().after(tu.addCalendar(start,"小时",-1)) && data.getDates().before(tu.addCalendar(end,"小时",1))){
                    flow.add(data);
                }
            }
            List<PredictInputData> flow1 = new ArrayList<>();
            for (int i = 0; i < tu.duration(start,end,"小时"); i++) {
                PredictInputData predictInputData = new PredictInputData();
                Date time = tu.addCalendar(start,"小时",i);
                if (flow.isEmpty()){
                    predictInputData.setDates(time);
                    predictInputData.setLocation(key);
                    predictInputData.setFlow(setNullTemFlow(key,time)[1]);
                }else {
                    List<Date> dates = new ArrayList<>();
                    for (PredictInputData data:flow){
                        dates.add(data.getDates());
                    }
                    predictInputData = flow.get(tu.findNearestTime(dates,time));
                }
                flow1.add(predictInputData);
            }
            result.put(key,flow1);
        }
        return result;
    }
    /**
     * 雨量信息，包括了前n小时落地雨和后期预报雨量
     */
    @SneakyThrows
    public List<RainFallDto> getHoursRain(ForecastInputParamNew param, List<RainFallDto> input,String location){
        List<RainFallDto> result = new ArrayList<>();
        RainFallDto data;
        //获得开始时间和结束时间，分情况判断
        Date dateStart = param.getPredictionTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStart);
        calendar.add(Calendar.HOUR_OF_DAY, -InputUtils.beforeHours);
        dateStart = calendar.getTime();//找到落地雨前n小时
        int n = InputUtils.beforeHours + param.getPeriodTimeNum() * param.getPeriodTimeStep();//需要预报的时间长度
        calendar.add(Calendar.HOUR_OF_DAY, n);
        Date dataEnd = calendar.getTime();//预报结束时间
        Date inputEnd;//数据库中结束时间
        if (input!=null&&!input.isEmpty()) {
            inputEnd = sdf.parse(input.get(input.size() - 1).getDate());
        } else {
            throw new RuntimeException("未能从数据库中获取"+location+"数据");
        }
        List<RainFallDto> rainPre = param.getRainFallDtos();;
        //找到最贴近的时间
        List<Date> dateList = new ArrayList<>();
        for (RainFallDto raindate : input) {
            dateList.add(sdf.parse(raindate.getDate()));
        }
        int d = tu.findNearestTime(dateList, dateStart);
        if (param.getIsSimulation()==null||param.getIsSimulation()){
            for (int i = 0; i < InputUtils.beforeHours; i++) {
                data = new RainFallDto();
                for (int j = 0; d + j < input.size() && j < n; j++) {
                    Boolean dateCompare = tu.DateCompare(dateStart, sdf.parse(input.get(d + j).getDate()), "小时");
                    if (dateCompare) {
                        data = input.get(d + j);
                        break;
                    } else {
                        data = setNoCorTime(dateStart, input.get(0).getArea());
                    }
                }
                calendar.setTime(dateStart);
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                dateStart = calendar.getTime();
                result.add(data);
            }
            int duration = tu.duration(param.getPredictionTime(),inputEnd,"小时");
            duration = Math.max(duration,0);
            if (duration>0){
                dateStart = getDate(input, location, result, dateStart, calendar, n, d, duration);
            }
            for (int i = duration; i < param.getPeriodTimeNum() * param.getPeriodTimeStep(); i++) {
                data = new RainFallDto();
                for (RainFallDto rainFallDto : rainPre) {
                    Boolean dateCompare = tu.DateCompare(dateStart, sdf.parse(rainFallDto.getDate()), "小时");
                    if (dateCompare && location.equals(rainFallDto.getArea())) {
                        data.setArea(location);
                        data.setDate(tu.DateToString(dateStart));
                        data.setTemperature(rainFallDto.getTemperature());
                        data.setRainFall(rainFallDto.getRainFall());
                        break;
                    } else {
                        data = setNoCorTime(dateStart, input.get(0).getArea());
                    }
                }
                calendar.setTime(dateStart);
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                dateStart = calendar.getTime();
                result.add(data);
            }
        }
        else {
            int end_inputEnd = tu.duration(dataEnd, inputEnd, "小时");
            if (end_inputEnd >= 0)//预报结束时间在数据库中有，也就是全部读取历史数据
            {
                //此时的dateFind是历史数据中与开始预报时间最接近的
                getDate(input, location, result, dateStart, calendar, n, d, n);
            } else //预报结束时间在数据库中没有，也就是需要读取预报雨量
            {
                int start_inputEnd = tu.duration(dateStart, inputEnd, "小时") + 1;
                start_inputEnd = Math.min(start_inputEnd,n);

                if (start_inputEnd < 0)//预报开始时间在数据库中没有，也就是全部读取预报值
                {
                    for (int i = 0; i < n; i++) {
                        data = new RainFallDto();
                        if (param.getRainFallDtos()==null||param.getRainFallDtos().isEmpty())//没有预报值，数据库中也没有数据
                        {
                            data = setNoCorTime(dateStart, input.get(0).getArea());
                        }
                        else //有预报值
                        {
                            int length = param.getRainFallDtos().size();
                            for (int j = 0; j < length; j++) {
                                Date date = sdf.parse(rainPre.get(j).getDate());
                                Boolean dateCompare = tu.DateCompare(dateStart, date, "小时");
                                if (rainPre.get(0).getArea().equals("面雨量")) {
                                    if (dateCompare)//日期相等
                                    {
                                        data.setArea(location);
                                        data.setDate(tu.DateToString(dateStart));
                                        data.setTemperature(rainPre.get(j).getTemperature());
                                        data.setRainFall(rainPre.get(j).getRainFall());
                                        break;
                                    } else {
                                        data = setNoCorTime(dateStart, location);
                                    }
                                } else {
                                    if (dateCompare && location.equals(rainPre.get(j).getArea()))//日期相等并且地点相等才能赋值
                                    {
                                        data.setArea(input.get(0).getArea());
                                        data.setDate(tu.DateToString(dateStart));
                                        data.setTemperature(rainPre.get(j).getTemperature());
                                        data.setRainFall(rainPre.get(j).getRainFall());
                                        break;
                                    } else {
                                        data = setNoCorTime(dateStart, input.get(0).getArea());
                                    }
                                }
                            }
                        }
                        calendar.setTime(dateStart);
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        dateStart = calendar.getTime();
                        result.add(data);
                    }
                } else //预报开始时间在数据库内，预报结束时间不在数据库内
                {
                    dateStart = getDate(input,location,result,dateStart,calendar,n,d,start_inputEnd);//从落地雨开始给其赋值到数据库末尾
                    //此时的dataStart==数据库末尾的时间
                    int inputEnd_dateEnd = tu.duration(dateStart, dataEnd, "小时");//数据库末尾到预报结束时间的距离
                    for (int i = 0; i < inputEnd_dateEnd; i++) {
                        data = new RainFallDto();
                        if (param.getRainFallDtos()==null||param.getRainFallDtos().isEmpty()){
                            data = setNoCorTime(dateStart, input.get(0).getArea());
                        }
                        else  {
                            int length = param.getRainFallDtos().size();
                            for (int j = 0; j < length; j++) {
                                Date date = sdf.parse(rainPre.get(j).getDate());
                                Boolean dateCompare = tu.DateCompare(dateStart, date, "小时");
                                if (rainPre.get(0).getArea().equals("面雨量")) {
                                    if (dateCompare)//日期相等
                                    {
                                        data.setArea(location);
                                        data.setDate(tu.DateToString(dateStart));
                                        data.setTemperature(rainPre.get(j).getTemperature());
                                        data.setRainFall(rainPre.get(j).getRainFall());
                                        break;
                                    } else {
                                        data = setNoCorTime(dateStart, location);
                                    }
                                }
                                else {
                                    if (dateCompare && location.equals(rainPre.get(j).getArea()))//日期相等并且地点相等才能赋值
                                    {
                                        data.setArea(location);
                                        data.setDate(tu.DateToString(dateStart));
                                        data.setTemperature(rainPre.get(j).getTemperature());
                                        data.setRainFall(rainPre.get(j).getRainFall());
                                        break;
                                    } else {
                                        data = setNoCorTime(dateStart, input.get(0).getArea());
                                    }
                                }
                            }
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

    private Date getDate(List<RainFallDto> input, String location, List<RainFallDto> result, Date dateStart, Calendar calendar, int n, int d, int duration) {
        RainFallDto data;
        for (int i = 0; i < duration; i++) {
            data = getNewHours(input,location,dateStart,d,n);//从数据库中获取该小时降雨
            calendar.setTime(dateStart);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            dateStart = calendar.getTime();
            result.add(data);
        }
        return dateStart;
    }

    @SneakyThrows
    private RainFallDto getNewHours(List<RainFallDto> input, String location, Date dateStart, int d, int n){
        RainFallDto data = new RainFallDto();
        Boolean dateCompare = false;
        for (int j = 0; d + j < input.size() && j < n; j++) {
            dateCompare = tu.DateCompare(dateStart, sdf.parse(input.get(d + j).getDate()), "小时");
            if (dateCompare) {
                break;
            }
        }
        if (dateCompare) {
            Double rainSum = 0.0;
            for (int j = 0; d + j < input.size() && j < n; j++){
                Date date0 = tu.addCalendar(sdf.parse(input.get(d + j).getDate()),"小时",-1);
                Date date = sdf.parse(input.get(d + j).getDate());
                int minute0 = tu.getSpecificDate(date0).get("分钟");
                int minute = tu.getSpecificDate(date).get("分钟");
                dateCompare = tu.DateCompare(dateStart, date0, "小时");
                if (dateCompare && minute0>=30){
                    rainSum += input.get(d + j).getRainFall();
                }
                dateCompare = tu.DateCompare(dateStart, date, "小时");
                if (dateCompare){
                    data = input.get(d + j);
                }
                if (dateCompare && minute<=30){
                    rainSum += input.get(d + j).getRainFall();
                }
            }
            data.setRainFall(rainSum);
        }
        else {
            data = setNoCorTime(dateStart, location);
        }
        return data;
    }
    /**
     * 保留前20天雨量
     */
    @SneakyThrows
    public List<RainFallDto> getTwentyDaysRain(ForecastInputParamNew param, List<RainFallDto> input, String location) {
        List<RainFallDto> result = new ArrayList<>();
        Date dateStart = param.getPredictionTime();
        RainFallDto data = new RainFallDto();
        if (input==null||input.isEmpty()){//未从数据库中获取雨量站数据
            for (int i = 0; i < InputUtils.beforeDays; i++) {
                Date date = tu.addCalendar(dateStart,"日",i);
                data = setNoCorTime(date,location);
                result.add(data);
            }
            return result;
        }
        List<Date> dateList = new ArrayList<>();
        for (RainFallDto predictInputData : input) {
            dateList.add(sdf.parse(predictInputData.getDate()));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStart);
        calendar.add(Calendar.DAY_OF_MONTH, -InputUtils.beforeDays);
        Date dateStart_20 = calendar.getTime();//找到前二十天
        Date inputDateEnd = sdf.parse(input.get(input.size() - 1).getDate());//数据库中最新时间
        int d = tu.findNearestTime(dateList, dateStart_20);//找到最贴近的时间
        int start_End = tu.duration(dateStart, inputDateEnd, "日");
        if (start_End > 0)//预报时间在数据库内全为历史值
        {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; d + j < input.size() && j < 20; j++) {
                    Boolean dateCompare = tu.DateCompare(dateStart_20, sdf.parse(input.get(d + j).getDate()), "日");
                    if (dateCompare) {
                        data = input.get(d + j);
                        break;
                    } else {
                        data = setNoCorTime(dateStart, input.get(0).getDate());
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
            if (rainFallDtoList==null){
                rainFallDtoList = new ArrayList<>();
                for (int i = 0; i < param.getPeriodTimeNum()*24; i++) {
                    RainFallDto rainFallDto = new RainFallDto();
                    rainFallDto.setDate(tu.DateToString(tu.addCalendar(dateStart, "小时", i)));
                    rainFallDto.setRainFall(0.0);
                    rainFallDto.setTemperature(setNullTemFlow("楼庄子",dateStart)[0]);
                    rainFallDto.setArea("面雨量");
                    rainFallDtoList.add(rainFallDto);
                }
            }
            List<RainFallDto> preRainDay = rainHourToDay(rainFallDtoList);
            int start_20_End = tu.duration(dateStart_20, inputDateEnd, "日");
            if (start_20_End < 0)//全部为预报值
            {
                for (int i = 0; i < 20; i++) {
                    if (!preRainDay.isEmpty()) {
                        for (RainFallDto predictInputData : preRainDay) {
                            Date date = sdf.parse(predictInputData.getDate());
                            Boolean dateCompare = tu.DateCompare(dateStart_20, date, "日");
                            if (dateCompare && predictInputData.getArea().equals(input.get(0).getArea()))//日期和站点都相等才能赋值
                            {
                                data = predictInputData;
                                break;
                            } else {
                                data = setNoCorTime(dateStart_20, input.get(0).getArea());
                            }
                        }
                    } else {
                        data = setNoCorTime(dateStart_20, input.get(0).getArea());
                    }
                    calendar.setTime(dateStart_20);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    dateStart_20 = calendar.getTime();
                    result.add(data);
                }
            } else //二十天一部分历史，一部分预报
            {
                int start_20_inputEnd = tu.duration(dateStart_20, inputDateEnd, "日");
                for (int i = 0; i < start_20_inputEnd; i++) {
                    for (int j = 0; d + j < input.size() && j < 20; j++) {
                        Boolean dateCompare = tu.DateCompare(dateStart_20, sdf.parse(input.get(d + j).getDate()), "日");
                        if (dateCompare) {
                            data = input.get(d + j);
                            break;
                        } else {
                            data = setNoCorTime(dateStart_20, input.get(0).getArea());
                        }
                    }
                    calendar.setTime(dateStart_20);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    dateStart_20 = calendar.getTime();
                    result.add(data);
                }
                int inputEnd_Start = tu.duration(inputDateEnd, dateStart, "日");
                for (int i = 0; i < inputEnd_Start; i++) {
                    if (!preRainDay.isEmpty()) {
                        for (RainFallDto predictInputData : preRainDay) {
                            Date date = sdf.parse(predictInputData.getDate());
                            Boolean dateCompare = tu.DateCompare(dateStart_20, date, "日");
                            if (dateCompare && predictInputData.getArea().equals(input.get(0).getArea()))//日期相等才能赋值
                            {
                                data = predictInputData;
                                break;
                            } else {
                                data = setNoCorTime(dateStart_20, input.get(0).getArea());
                            }
                        }
                    } else {
                        data = setNoCorTime(dateStart_20, input.get(0).getArea());
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
     * 将雨量站数据去除空值
     */
    public Map<String,List<RainFallDto>> rainRemoveNull(Map<String,List<RainFallDto>> rainStation,Date start,Date end) {
        Map<String,List<RainFallDto>> result = new HashMap<>();
        for (Map.Entry<String, List<RainFallDto>> entry : rainStation.entrySet()) {
            String key = entry.getKey();
            List<RainFallDto> value = entry.getValue();
            if (value.isEmpty()){
                int l = tu.duration(start,end,"小时");
                for (int i = 0; i < l; i++) {
                    RainFallDto piece = new RainFallDto();
                    piece.setRainFall(0.0);
                    piece.setDate(tu.DateToString(tu.addCalendar(start, "小时", i)));
                    piece.setTemperature(setNullTemFlow(key, start)[0]);
                    piece.setArea(key);
                    value.add(piece);
                }
            }else {
                for (RainFallDto rainfall : value) {
                    if (rainfall.getTemperature()==null){
                        rainfall.setTemperature(setNullTemFlow(key, start)[0]);
                    }
                }
            }
            result.put(key,value);
        }
        return result;
    }
    /**
     * 流量数据转化
     * @return 站点名称、日尺度时间、流量
     */
    public List<PredictInputData> flowConversion(Date dateStart, Date dateEnd, List<LzzGaugingStation> input) {
        List<PredictInputData> result = new ArrayList<>();
        double flowSum = 0;
        int flowNum = 1;
        int yearEnd = tu.getSpecificDate(dateEnd).get("年");
        for (int i = 0; i < input.size(); i++) {
            String id = input.get(i).getId();
            // 使用间隔符提取数字部分
            String[] parts = id.split(":");
            String bridgeNumber = parts[0];
            long numericValue = Long.parseLong(parts[1]);
            Date date = new Date(numericValue); // 根据时间戳创建日期对象
            int day = tu.getSpecificDate(date).get("日");
            int hour = tu.getSpecificDate(date).get("小时");
            int hourBefore = 0;
            int dayBefore = day;
            if (i != 0) {
                String id1 = input.get(i - 1).getId();
                // 使用间隔符提取数字部分
                String[] parts1 = id1.split(":");
                long numericValue1 = Long.parseLong(parts1[1]);
                Date date1 = new Date(numericValue1);
                hourBefore = tu.getSpecificDate(date1).get("小时");
                dayBefore = tu.getSpecificDate(date1).get("日");
            }
            if (((hour - hourBefore) < 0) || day != dayBefore) {
                double flowY = flowSum / flowNum;
                PredictInputData piece = new PredictInputData();
                piece.setLocation(bridgeNumber);
                piece.setDates(date);
                piece.setFlow(flowY);
                result.add(piece);
                flowSum = 0;
                flowNum = 1;

            }
            if (input.get(i).getFlow() != null) {
                flowSum = flowSum + input.get(i).getFlow();
                flowNum ++;
            }
        }
        /*
         * 保证数据连续性
         */
        List<PredictInputData> resultEnd = new ArrayList<>();
        int n = tu.duration(dateStart, dateEnd, "日");
        for (int i = 0; i < n; i++) {
            PredictInputData data = new PredictInputData();
            for (PredictInputData predictInputData : result) {
                Date date = predictInputData.getDates();
                Boolean dateCompare = tu.DateCompare(date, dateStart, "日");
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
        //为空和异常值赋值
        for (PredictInputData data : resultEnd) {
            if (data.getFlow() == null) {
                data.setFlow(setNullTemFlow("楼庄子",dateStart)[1]);
            }
            int month = tu.getSpecificDate(dateStart).get("月");
            if (month <= 2 || month >= 10){
                if (data.getFlow() >= 20){
                    data.setFlow(setNullTemFlow("楼庄子",dateStart)[1]);
                }
            }
        }
        return resultEnd;
    }
    /**
     * 区间数据尺度转化
     */
    public void irrigateMinuteToHour(Map<String,List<RainFallDto>> inputData){
        List<RainFallDto> xqzStation = inputData.get("小渠子雨量站");
        List<RainFallDto> tjydStation = inputData.get("团结一队雨量站");
        List<RainFallDto> ggStation = inputData.get("甘沟雨量站");
        List<RainFallDto> tthStation = inputData.get("头屯河水库雨量站");
        inputData.put("小渠子雨量站",irrigateListMinuteToHour(xqzStation));
        inputData.put("团结一队雨量站",irrigateListMinuteToHour(tjydStation));
        inputData.put("甘沟雨量站",irrigateListMinuteToHour(ggStation));
        inputData.put("头屯河水库雨量站",irrigateListMinuteToHour(tthStation));
    }
    /**
     * 区间雨量站和水文站数据从5min转为小时尺度
     */
    @SneakyThrows
    public List<RainFallDto> irrigateListMinuteToHour(List<RainFallDto> inputList){
        double rainFall = 0.0;
        int number = 0;
        List<RainFallDto> resultList = new ArrayList<>();
        for (int i = 0; i < inputList.size()-1; i++) {
            RainFallDto info = new RainFallDto();
            Boolean isSameHour = tu.DateCompare(sdf.parse(inputList.get(i).getDate()),sdf.parse(inputList.get(i+1).getDate()),"小时");
            info.setDate(inputList.get(i).getDate());
            info.setArea(inputList.get(i).getArea());
            if (isSameHour){
                rainFall += inputList.get(i).getRainFall()==null?0.0:inputList.get(i).getRainFall();
                number++;
            }else {
                if (number==0){//该小时只有一个时段
                    info.setRainFall(inputList.get(i).getRainFall());
                    info.setTemperature(inputList.get(i).getTemperature());
                }else {
                    info.setRainFall(rainFall/number);
                }
                rainFall = 0.0;
                number = 0;
                resultList.add(info);
            }
        }
        return resultList;
    }
    /**
     * 雨量站小时尺度转日尺度
     * @return 站点名称、日尺度时间、降水、温度
     */
    @SneakyThrows
    public List<RainFallDto> rainHourToDay(List<RainFallDto> input) {
        List<RainFallDto> result = new ArrayList<>();
        Double temperatureSum = 0.0;
        Double rainfallSum = 0.0;
        int temperatureNum = 0;
        for (int i = 0; i < input.size(); i++) {
            Date date = sdf.parse(input.get(i).getDate()); // 根据时间戳创建日期对象
            int hour = tu.getSpecificDate(date).get("小时");
            int day = tu.getSpecificDate(date).get("日");
            int hourBefore = 0;
            int day1 = day;
            String station1 = input.get(0).getArea();
            if (i != 0) {
                Date date1 = sdf.parse(input.get(i-1).getDate());
                hourBefore = tu.getSpecificDate(date1).get("小时");
                day1 = tu.getSpecificDate(date1).get("日");
                station1 = input.get(i - 1).getArea();
            }
            String station = input.get(i).getArea();
            if (((hour - hourBefore) < 0 || day != day1)&& station.equals(station1)) {
                Double temperatureY =temperatureNum == 0? setNullTemFlow(input.get(0).getArea(),sdf.parse(input.get(0).getDate()))[0]: (temperatureSum / temperatureNum);
                Double rainfallY = rainfallSum ;
                RainFallDto piece = new RainFallDto();
                piece.setArea(input.get(0).getArea());
                piece.setDate(input.get(i).getDate());
                piece.setTemperature(temperatureY);
                piece.setRainFall(rainfallY);
                result.add(piece);
                temperatureSum = 0.0;
                rainfallSum = 0.0;
                temperatureNum = 0;
            }
            if (input.get(i).getTemperature() != null) {
                temperatureSum += input.get(i).getTemperature();
                temperatureNum ++;
            }
            if (input.get(i).getRainFall() != null) {
                rainfallSum +=input.get(i).getRainFall();
            }
        }
        return result;
    }


    /**
     * 为获取相应时间的雨量站数据
     */
    @SneakyThrows
    public RainFallDto setNoCorTime(Date date, String location) {
        RainFallDto result = new RainFallDto();
        result.setDate(tu.DateToString(date));
        result.setTemperature(setNullTemFlow(location,date)[0]);
        result.setArea(location);
        result.setRainFall(0.0);
        return result;
    }
    /**
     * 各个雨量站点雨量
     * @return surfaceData
     * 该流域的面雨量
     */
    @SneakyThrows
    public List<RainFallDto> pointToSurface(Map<String,List<RainFallDto>> pointData, String location) {
        List<RainFallDto> result = new ArrayList<>();
        int l = 1000000;
        for (Map.Entry<String,List<RainFallDto>> entry:pointData.entrySet()){
            l = Math.min(l, entry.getValue().size());
        }
        switch (location) {
            case "3号桥": {
                for (int i = 0; i < l; i++) {
                    double rainFall = 0.0;
                    double temperature;
                    RainFallDto hourResult = new RainFallDto();
                    rainFall += pointData.get("八一林场自动雨量站").get(i).getRainFall() * 0.344401;
                    rainFall += pointData.get("加普沙自动雨量站").get(i).getRainFall() * 0.147571;
                    rainFall += pointData.get("东南沟自动雨量站").get(i).getRainFall() * 0.156022;
                    rainFall += pointData.get("宰尔德自动雨量站").get(i).getRainFall() * 0.042438;
                    rainFall += pointData.get("无名沟自动雨量站").get(i).getRainFall() * 0.019251;
                    rainFall += pointData.get("萨尔达万自动雨量站").get(i).getRainFall() * 0.024912;
                    rainFall += pointData.get("煤矿沟自动雨量站").get(i).getRainFall() * 0.018891;
                    temperature = pointData.get("制材厂自动雨量站").get(i).getTemperature();
                    hourResult.setDate(pointData.get("八一林场自动雨量站").get(i).getDate());
                    hourResult.setArea("面雨量");
                    hourResult.setRainFall(rainFall);
                    hourResult.setTemperature(temperature);
                    result.add(hourResult);
                }
                break;
            }
            case "楼庄子": {
                for (int i = 0; i < l; i++) {
                    double rainFall = 0.0;
                    double temperature;
                    RainFallDto hourResult = new RainFallDto();
                    rainFall += pointData.get("八一林场自动雨量站").get(i).getRainFall() * 0.344401;
                    rainFall += pointData.get("加普沙自动雨量站").get(i).getRainFall() * 0.147571;
                    rainFall += pointData.get("东南沟自动雨量站").get(i).getRainFall() * 0.156022;
                    rainFall += pointData.get("宰尔德自动雨量站").get(i).getRainFall() * 0.042438;
                    rainFall += pointData.get("无名沟自动雨量站").get(i).getRainFall() * 0.019251;
                    rainFall += pointData.get("萨尔达万自动雨量站").get(i).getRainFall() * 0.024912;
                    rainFall += pointData.get("煤矿沟自动雨量站").get(i).getRainFall() * 0.018891;
                    rainFall += pointData.get("黑沟自动雨量站").get(i).getRainFall() * 0.044157;
                    rainFall += pointData.get("喀什沟自动雨量站").get(i).getRainFall() * 0.082419;
                    rainFall += pointData.get("制材厂自动雨量站").get(i).getRainFall() * 0.115105;
                    temperature = pointData.get("制材厂自动雨量站").get(i).getTemperature();
                    hourResult.setDate(pointData.get("八一林场自动雨量站").get(i).getDate());
                    hourResult.setArea("面雨量");
                    hourResult.setRainFall(rainFall);
                    hourResult.setTemperature(temperature);
                    result.add(hourResult);
                }
                break;
            }
            case "楼头区间":
            case "头屯河": {
                for (int i = 0; i < l; i++) {
                    double rainFall = 0.0;
                    RainFallDto hourResult = new RainFallDto();
                    rainFall += pointData.get("小渠子雨量站").get(i).getRainFall() * 0.284;
                    rainFall += pointData.get("团结一队雨量站").get(i).getRainFall() * 0.1948;
                    rainFall += pointData.get("头屯河水库雨量站").get(i).getRainFall() * 0.51188;
                    hourResult.setDate(pointData.get("八一林场自动雨量站").get(i).getDate());
                    hourResult.setArea("面雨量");
                    hourResult.setRainFall(rainFall);
                    hourResult.setTemperature(setNullTemFlow("楼头区间", sdf.parse(pointData.get("小渠子雨量站").get(i).getDate()))[0]);
                    result.add(hourResult);
                }
                break;
            }
        }
        return result;
    }

    /**
     * 各个雨量站点雨量
     * @return surfaceData
     * 该流域的面雨量
     */
    @SneakyThrows
    public Object[][] pointToSurfaceObject(Map<String,Object[][]> pointData, String location) {
        int l = 1000000;
        for (Map.Entry<String,Object[][]> entry:pointData.entrySet()){
            l = Math.min(l, entry.getValue().length);
        }
        Object[][] result = new Object[l][2];
        switch (location) {
            case "3号桥": {
                for (int i = 0; i < l; i++) {
                    double rainFall = 0.0;
                    rainFall += (Double)pointData.get("八一林场自动雨量站")[i][2] * 0.344401;
                    rainFall += (Double)pointData.get("加普沙自动雨量站")[i][2] * 0.147571;
                    rainFall += (Double)pointData.get("东南沟自动雨量站")[i][2] * 0.156022;
                    rainFall += (Double)pointData.get("宰尔德自动雨量站")[i][2] * 0.042438;
                    rainFall += (Double)pointData.get("无名沟自动雨量站")[i][2] * 0.019251;
                    rainFall += (Double)pointData.get("萨尔达万自动雨量站")[i][2] * 0.024912;
                    rainFall += (Double)pointData.get("煤矿沟自动雨量站")[i][2] * 0.018891;
                    result[i][0]=pointData.get("八一林场自动雨量站")[i][0];
                    result[i][1]=rainFall;
                }
                break;
            }
            case "楼庄子": {
                for (int i = 0; i < l; i++) {
                    double rainFall = 0.0;
                    rainFall += (Double)pointData.get("八一林场自动雨量站")[i][2] * 0.344401;
                    rainFall += (Double)pointData.get("加普沙自动雨量站")[i][2] * 0.147571;
                    rainFall += (Double)pointData.get("东南沟自动雨量站")[i][2] * 0.156022;
                    rainFall += (Double)pointData.get("宰尔德自动雨量站")[i][2] * 0.042438;
                    rainFall += (Double)pointData.get("无名沟自动雨量站")[i][2] * 0.019251;
                    rainFall += (Double)pointData.get("萨尔达万自动雨量站")[i][2] * 0.024912;
                    rainFall += (Double)pointData.get("煤矿沟自动雨量站")[i][2] * 0.018891;
                    rainFall += (Double)pointData.get("黑沟自动雨量站")[i][2] * 0.044157;
                    rainFall += (Double)pointData.get("喀什沟自动雨量站")[i][2] * 0.082419;
                    rainFall += (Double)pointData.get("制材厂自动雨量站")[i][2] * 0.115105;
                    result[i][0]=pointData.get("八一林场自动雨量站")[i][0];
                    result[i][1]=rainFall;
                }
                break;
            }
            case "楼头区间":
            case "头屯河": {
                for (int i = 0; i < l; i++) {
                    double rainFall = 0.0;
                    rainFall += (Double)pointData.get("小渠子雨量站")[i][2] * 0.25;
                    rainFall += (Double)pointData.get("团结一队雨量站")[i][2] * 0.25;
                    rainFall += (Double)pointData.get("头屯河水库雨量站")[i][2] * 0.25;
                    rainFall += (Double)pointData.get("甘沟雨量站")[i][2] * 0.25;
                    result[i][0]=pointData.get("小渠子雨量站")[i][0];
                    result[i][1]=rainFall;
                }
            }
        }
        return result;
    }

    /**
     * 获取连续时间序列数据
     * @param input
     * @param start
     * @param l
     * @return
     */
    public Object[][] getContinuousSequences(Object[][] input,Date start,int l,String period,String location){
        if (input.length == 0){
            throw new RuntimeException("未获取"+location+"数据");
        }
        Object[][] result = new Object[l][2];
        List<Date> dateList = new ArrayList<>();
        List<Double> dataList = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (input[i][0] != null && input[i][1] != null){
                dateList.add((Date) input[i][0]);
                dataList.add((Double) input[i][1]);
            }
        }
        for (int i = 0; i < l; i++) {
            boolean existTime = false;
            Date date = tu.addCalendar(start,period,i);
            for (int j = 0; j < input.length; j++) {
                if (input[j][0] != null && input[j][1] != null && tu.DateCompare(date, (Date) input[j][0],period)){
                    existTime = true;
                    result[i] = input[j];
                }
            }
            if (!existTime){
                result[i][0] = date;
                int m = tu.findNearestTime(dateList,date);
                if (m - 1 >= 0){
                    int duration = tu.duration(dateList.get(m-1),dateList.get(m),period);
                    result[i][1] = dataList.get(m-1)+(dataList.get(m)-dataList.get(m-1))/(duration+1);
                } else if (m + 1< dateList.size()) {
                    int duration = tu.duration(dateList.get(m),dateList.get(m+1),period);
                    result[i][1] = dataList.get(m)+(dataList.get(m+1)-dataList.get(m))/(duration+1);
                }else {
                    result[i][1] = dataList.get(m);
                }
            }
        }
        return result;
    }

    /**
     * 从数据库中获得水位站的对应日尺度温度与降水
     */
    public List<RainFallDto> getRAndT(ForecastInputParamNew param) {
        //雨量站整合
        List<RainFallDto> ZCCDAY = new ArrayList<>();
        List<RainFallDto> ZCCHOUR = param.getRainfall().get("制材厂自动雨量站");
        if (ZCCHOUR!=null&&!ZCCHOUR.isEmpty()) {
            ZCCDAY = rainHourToDay(ZCCHOUR);
        }
        ZCCDAY = getTwentyDaysRain(param,ZCCDAY,"制材厂自动雨量站");
        //添加日尺度温度与降水
        return ZCCDAY;
    }
    /**
     * 为水位站添加温度和降水
     */
    @SneakyThrows
    public List<PredictInputData> addRAndT(List<PredictInputData> WaterStation, List<RainFallDto> RAT) {
        List<PredictInputData> result;
        for (PredictInputData inputData : WaterStation) {
            for (RainFallDto predictInputData : RAT) {
                Boolean dateCompare = tu.DateCompare(sdf.parse(predictInputData.getDate()), inputData.getDates(), "日");
                if (dateCompare) {
                    double rain = predictInputData.getRainFall();
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
                Double tem = setNullTemFlow(predictInputData.getLocation(),predictInputData.getDates())[0];
                predictInputData.setTemperature(tem);
            }
        }
        result = WaterStation;
        return result;
    }
    /**
     * 温度转化为蒸发量
     */
    @SneakyThrows
    public Object[][] temToEva(Object[][] data) {
        //按月份分配蒸发
        double[] evaporation = new  double[]{9.0 / 31 / 24,16.4 / 28 / 24,56.7 / 31 / 24,177.3 / 30 / 24,270.1 / 31 / 24,
                294.8 / 30 / 24,315.3 / 31 / 24,275.3 / 31 / 24,187.7 / 30 / 24,101.8 / 31 / 24,26.9 / 30 / 24,8.0 / 31 / 24};
        for (int i = 0; i < data.length; i++) {
            int month = tu.getSpecificDate(sdf.parse((String) data[i][0])).get("月");
            data[i][1] = evaporation[month-1];
        }
        return data;
    }
    /**
     * 根据月份来提供空值的温度和径流
     * @return result[0]为温度
     * result[1]为径流
     */
    public double[] setNullTemFlow(String location, Date date) {
        int month = tu.getSpecificDate(date).get("月");
        double[] result = new double[2];
        double[] temperatures;
        double[] flow;
        if (location.contains("3号桥")||location.contains("楼庄子")){
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
        int month = tu.getSpecificDate((Date) input[0]).get("月");
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

}


