package dataExtraction.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dataExtraction.ghd.dao.WpdRrInfoDao;
import dataExtraction.ghd.dao.WpdStPptnRDao;
import dataExtraction.ghd.entity.WpdRrInfo;
import dataExtraction.ghd.entity.WpdStPptnR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RainfallService {

    @Autowired
    WpdStPptnRDao wpdStPptnRDao;

    @Autowired
    DataShowService dataShowService;

    @Autowired
    WpdRrInfoDao wpdRrInfoDao;
    private final static SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");

    public JSONArray queryRainfall(String typeName, String time) {
        JSONArray jsonArrayPP = new JSONArray();
        String infoName = "雨量站";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAllData(typeName, infoName);
        for (WpdRrInfo wfo : wpdRrInfos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", wfo.getName());
            map.put("time", time);
            map.put("id", wfo.getId());
            //3/6/9/12时间段降雨
            Double integer3 = wpdStPptnRDao.queryDaySumData3(time, wfo.getId());
            Double integer6 = wpdStPptnRDao.queryDaySumData6(time, wfo.getId());
            Double integer12 = wpdStPptnRDao.queryDaySumData12(time, wfo.getId());
            if (integer3 != null) {
                map.put("hour3", integer3);
            } else {
                map.put("hour3", -9999999);
            }
            if (integer6 != null) {
                map.put("hour6", integer6);
            } else {
                map.put("hour6", -9999999);
            }
            if (integer12 != null) {
                map.put("hour12", integer12);
            } else {
                map.put("hour12", -9999999);
            }
            jsonArrayPP.add(map);
        }
        return jsonArrayPP;
    }

    public JSONArray queryRainfallCurve(String ndcdId, String beginTime, String endTime) throws ParseException {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        JSONArray jsonArrayPP = new JSONArray();
        String infoName = "雨量站";
        WpdRrInfo wpdRrInfos = wpdRrInfoDao.findId(ndcdId);
        Integer LinTime = dataShowService.hour(beginTime, endTime) + 1;
        JSONObject jsonObject = new JSONObject();
        List<Object> listTime = new ArrayList<Object>();
        List<Object> listVal = new ArrayList<Object>();
        for (int i = 0; i < LinTime; i++) {
            Date dts = sdfTime.parse(beginTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dts);
            cal.add(Calendar.HOUR, +i);
            String time2 = sdfTime.format(cal.getTime());
            String id = ndcdId;
            WpdStPptnR wpdStPptnR = wpdStPptnRDao.fingMon(time2, id);
            listTime.add(time2);
            if (wpdStPptnR != null) {
                listVal.add(new BigDecimal(wpdStPptnR.getDrp()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            } else {
                listVal.add(null);
            }
        }

        jsonObject.put("name", wpdRrInfos.getName());
        jsonObject.put("time", listTime);
        jsonObject.put("value", listVal);
        jsonArrayPP.add(jsonObject);

        return jsonArrayPP;
    }

    public JSONArray queryHourRainfall(String typeName, String time, Integer hour) throws ParseException {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        JSONArray jsonArrayPP = new JSONArray();
        String infoName = "雨量站";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAllData(typeName, infoName);
        for (WpdRrInfo wfo : wpdRrInfos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", wfo.getName());
            map.put("time", time);
            map.put("id", wfo.getId());
            //当天降雨量
            Double IntPptn = wpdStPptnRDao.fingDaySum(time, wfo.getId());
            if (IntPptn == null) {
                map.put("sun", -9999999);
            } else {
                map.put("sun", IntPptn);
            }
            for (int i = 0; i < 24; i++) {
                Date dts = sdfTime.parse(time);
                //两小时降雨量
                Calendar cal = Calendar.getInstance();
                cal.setTime(dts);
                cal.add(Calendar.HOUR, -i);
                String time2 = sdfTime.format(cal.getTime());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                // 创建两个时间对象
                LocalDateTime Localtime1 = LocalDateTime.parse(time2, formatter);
                LocalDateTime Localtime2 = LocalDateTime.now();

                // 比较两个时间的大小
                if (Localtime2.isBefore(Localtime1)) {
                    map.put(time2, -9999999);
                    continue;
                }
                if (hour == 1) {
                    Double integer1 = wpdStPptnRDao.queryDaySumData1(time2, wfo.getId());
                    if (integer1 != null) {
                        map.put(time2, integer1);
                    } else {
                        map.put(time2, -9999999);
                    }
                }
                if (hour == 3) {
                    Double integer3 = wpdStPptnRDao.queryDaySumData3(time2, wfo.getId());
                    if (integer3 != null) {
                        map.put(time2, integer3);
                    } else {
                        map.put(time2, -9999999);
                    }
                }
                if (hour == 6) {
                    Double integer6 = wpdStPptnRDao.queryDaySumData6(time2, wfo.getId());
                    if (integer6 != null) {
                        map.put(time2, integer6);
                    } else {
                        map.put(time2, -9999999);
                    }
                }
                if (hour == 12) {
                    Double integer12 = wpdStPptnRDao.queryDaySumData12(time2, wfo.getId());
                    if (integer12 != null) {
                        map.put(time2, integer12);
                    } else {
                        map.put(time2, -9999999);
                    }
                }
            }
            jsonArrayPP.add(map);
        }
        return jsonArrayPP;
    }

    public JSONArray queryTenDayRainfall(String typeName, String time, Integer ten) throws ParseException {
        JSONArray jsonArrayPP = new JSONArray();
        Date date = shortSdf.parse(time);
        Date ksData = getTenDayStartTime(date, ten);
        Date jsData = getTenDayEndTime(date, ten);
        Integer lin = dataShowService.day(shortSdf.format(ksData), shortSdf.format(jsData)) + 1;
        String infoName = "雨量站";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAllData(typeName, infoName);
        for (WpdRrInfo wfo : wpdRrInfos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", wfo.getName());
            map.put("time", time);
            map.put("id", wfo.getId());
            int daySize = 0;
            Double NumRain = 0.0;
            Double maxDrp = 0.0;
            String maxTime = "";
            for (int i = 0; i < lin; i++) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(ksData);
                cal.add(Calendar.DATE, +i);
                String newTime = shortSdf.format(cal.getTime());
                String id = wfo.getId();
                Double integerPptn = wpdStPptnRDao.fingDaySum(newTime, id);
                if (integerPptn == null) {
                    map.put(newTime, -9999999);
                } else {
                    map.put(newTime, integerPptn);
                    NumRain = NumRain + integerPptn;
                    if (integerPptn > 0) {
                        daySize = daySize + 1;
                    }
                    if (integerPptn > maxDrp) {
                        maxDrp = integerPptn;
                        maxTime = newTime;
                    }
                }
            }
            //总降雨量
            //Integer NumRain = wpdStPptnRDao.querySum(shortSdf.format(ksData),shortSdf.format(jsData),wfo.getId());
            map.put("num", NumRain);
            //降雨天数
            map.put("daySize", daySize);
            //最大日量、出现日期
            //Map<String,Object> mapMaxDay = wpdStPptnRDao.queryMaxData(shortSdf.format(ksData),shortSdf.format(jsData),wfo.getId());
            map.put("maxDrp", maxDrp);
            map.put("maxTime", maxTime);
            jsonArrayPP.add(map);
        }
        return jsonArrayPP;
    }

    public Map<Object, Object> queryYearDayRainfall(String ndcdId, String time) throws ParseException {
        String xTime = time + "-01-01";
        Map<Object, Object> mapMonth = new LinkedHashMap<>();
        Map<Object, Object> mapTop = new LinkedHashMap<>();
        int day = 0;
        List<Double> raData = new ArrayList<>();
        List<String> timeData = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Date date = shortSdf.parse(xTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, +i);
            String newTime = shortSdf.format(cal.getTime());
            Integer lin = dataShowService.getDaysOfMonth(shortSdf.parse(newTime));
            Map<Object, Object> mapDay = new LinkedHashMap<>();
            for (int j = 0; j < lin; j++) {
                Date datej = shortSdf.parse(newTime);
                Calendar calj = Calendar.getInstance();
                calj.setTime(datej);
                calj.add(Calendar.DATE, +j);
                String newTimej = shortSdf.format(calj.getTime());
                Double drp = wpdStPptnRDao.fingYearDaySum(newTimej, ndcdId);
                if (drp == null) {
                    mapDay.put(j + 1, -9999999);
                } else {
                    mapDay.put(j + 1, drp);
                    day = day + 1;
                    raData.add(drp);
                    timeData.add(newTimej);
                }
            }
            mapMonth.put(i + 1, mapDay);
        }
        mapTop.put("top", mapMonth);
        //年总降雨量
        Double yearData = wpdStPptnRDao.fingYearDrp(xTime, ndcdId);
        if (yearData == null) {
            mapTop.put("yearSum", -9999999);
        } else {
            mapTop.put("yearSum", yearData);
        }
        //年最大月份
        Map<String, Object> mapMax = wpdStPptnRDao.queryYearMaxData(xTime, ndcdId);
        mapTop.put("days", day);
        if (mapMax != null && mapMax.get("drp") != null) {
            mapTop.put("mapMax1", mapMax.get("drp"));
            mapTop.put("timeMax1", mapMax.get("tm"));
        } else {
            mapTop.put("mapMax1", null);
            mapTop.put("timeMax1", null);
        }
        Map<String, Object> mapAll = annualValue(time, raData, timeData);
        mapTop.put("max3", mapAll.get("max3"));
        mapTop.put("time3", mapAll.get("time3"));
        mapTop.put("max7", mapAll.get("max7"));
        mapTop.put("time7", mapAll.get("time7"));
        mapTop.put("max15", mapAll.get("max15"));
        mapTop.put("time15", mapAll.get("time15"));
        mapTop.put("max30", mapAll.get("max30"));
        mapTop.put("time30", mapAll.get("time30"));
        return mapTop;
    }

    public JSONArray queryYearMonthRainfall(String typeName, String time, String month) {
        JSONArray jsonArrayPP = new JSONArray();
        String infoName = "雨量站";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAllData(typeName, infoName);
        String newTime = time + "-" + month + "-01";
        //String agedTime = String.valueOf(Integer.valueOf(time)-1)+"-"+month+"-01";
        for (WpdRrInfo wfo : wpdRrInfos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", wfo.getName());
            map.put("id", wfo.getId());
            Double drp = wpdStPptnRDao.fingMonthSum(newTime, wfo.getId());
            if (drp == null) {
                map.put("drp", -9999999);
            } else {
                map.put("drp", drp);
            }
            Double agedDrp = wpdStPptnRDao.fingManyDrp(newTime, wfo.getId());
            List<String> listDrp = wpdStPptnRDao.fingManyYearDrp(newTime, wfo.getId());
            if (agedDrp == null) {
                map.put("agedDrp", -9999999);
            } else {
                agedDrp = Double.valueOf(String.format("%.2f", agedDrp / listDrp.size()));
                map.put("agedDrp", agedDrp);
            }
            if (drp == null || agedDrp == null) {
                map.put("anomaly", -9999999);
            } else {
                Double anomaly = Double.valueOf(String.format("%.2f", (drp - agedDrp) / agedDrp * 100));
                map.put("anomaly", anomaly);
            }
            jsonArrayPP.add(map);
        }
        return jsonArrayPP;
    }


    /**
     * 获取所属旬开始时间
     *
     * @param date
     * @return
     */
    public static Date getTenDayStartTime(Date date, Integer ten) {
        try {
            if (ten == 1) {
                return getMonthStartTime(date);
            } else if (ten == 2) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-11");
                return shortSdf.parse(df.format(date));
            } else {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-21");
                return shortSdf.parse(df.format(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;


    }

    /**
     * 获取所属旬结束时间
     *
     * @param date
     * @return
     */
    public static Date getTenDayEndTime(Date date, Integer ten) {
        try {
            if (ten == 1) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-10");
                return shortSdf.parse(df.format(date));
            } else if (ten == 2) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-20");
                return shortSdf.parse(df.format(date));
            } else {
                return getMonthEndTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得本月的开始时间
     *
     * @return
     */
    public static Date getMonthStartTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Date dt = null;
        try {
            c.set(Calendar.DATE, 1);
            dt = shortSdf.parse(shortSdf.format(c.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dt;
    }

    /**
     * 本月的结束时间
     *
     * @return
     */
    public static Date getMonthEndTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Date dt = null;
        try {
            c.set(Calendar.DATE, 1);
            c.add(Calendar.MONTH, 1);
            c.add(Calendar.DATE, -1);
            dt = shortSdf.parse(shortSdf.format(c.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dt;
    }

    public Map<String,Object> annualValue(String year,List<Double> rainfall,List<String> listTime) {
        //Integer integer = dataShowService.yearDay(year);
        Map<String,Object> map = new LinkedHashMap<>();
        Double[] days = new Double[rainfall.size()];
        for (int i = 0; i < rainfall.size(); i++) {
            days[i] = rainfall.get(i);
        }
        if(days.length>3){
            // 计算3天最大值和开始时间
            Double max3 = 0.0;
            int start3 = -1;
            for (int i = 0; i < days.length - 2; i++) {
                Double sum = days[i] + days[i + 1] + days[i + 2];
                if (sum > max3) {
                    max3 = sum;
                    start3 = i;
                }
            }
            if(start3==-1){
                map.put("max3",-9999999);
                map.put("time3",-9999999);
            }else {
                map.put("max3",max3);
                map.put("time3",listTime.get(start3));
            }
        }
        if (days.length>7){
            // 计算7天最大值和开始时间
            Double max7 = 0.0;
            int start7 = -1;
            for (int i = 0; i < days.length - 6; i++) {
                Double sum = days[i] + days[i + 1] + days[i + 2] + days[i + 3] + days[i + 4] + days[i + 5] + days[i + 6];
                if (sum > max7) {
                    max7 = sum;
                    start7 = i;
                }
            }
            if(start7==-1){
                map.put("max7",-9999999);
                map.put("time7",-9999999);
            }else {
                map.put("max7",max7);
                map.put("time7",listTime.get(start7));
            }
        }else {
            map.put("max7",-9999999);
            map.put("time7",-9999999);
        }
        if (days.length>15){
            // 计算15天最大值和开始时间
            Double max15 = 0.0;
            int start15 = -1;
            for (int i = 0; i < days.length - 14; i++) {
                Double sum = days[i] + days[i + 1] + days[i + 2] + days[i + 3] + days[i + 4] + days[i + 5] + days[i + 6] + days[i + 7] + days[i + 8] + days[i + 9] + days[i + 10] + days[i + 11] + days[i + 12] + days[i + 13];
                if (sum > max15) {
                    max15 = sum;
                    start15 = i;
                }
            }
            if(start15==-1){
                map.put("max15",-9999999);
                map.put("time15",-9999999);
            }else {
                map.put("max15",max15);
                map.put("time15",listTime.get(start15));
            }
        }else {
            map.put("max15",-9999999);
            map.put("time15",-9999999);
        }
        if (days.length>30){
            // 计算30天最大值和开始时间
            Double max30 = 0.0;
            int start30 = -1;
            for (int i = 0; i < days.length - 29; i++) {
                Double sum = days[i] + days[i + 1] + days[i + 2] + days[i + 3] + days[i + 4] + days[i + 5] + days[i + 6] + days[i + 7] + days[i + 8] + days[i + 9] + days[i + 10] + days[i + 11] + days[i + 12] + days[i + 13] + days[i + 14] + days[i + 15] + days[i + 16] + days[i + 17] + days[i + 18] + days[i + 19] + days[i + 20] + days[i + 21] + days[i + 22] + days[i + 23] + days[i + 24] + days[i + 25] + days[i + 26] + days[i + 27] + days[i + 28];
                if (sum > max30) {
                    max30 = sum;
                    start30 = i;
                }
            }
            if(start30==-1){
                map.put("max30",-9999999);
                map.put("time30",-9999999);
            }else {
                map.put("max30",max30);
                map.put("time30",listTime.get(start30));
            }
        }else {
            map.put("max30",-9999999);
            map.put("time30",-9999999);
        }
        return map;
    }

    public JSONArray queryHour(String typeName, String time, Integer hour) throws ParseException {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        JSONArray jsonArrayPP = new JSONArray();
        String infoName = "雨量站";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAllData(typeName, infoName);
        for (WpdRrInfo wfo : wpdRrInfos) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", wfo.getName());
            map.put("time", time);
            map.put("id", wfo.getId());
            //当天降雨量
            Double IntPptn = wpdStPptnRDao.fingDaySum(time, wfo.getId());
            if (IntPptn == null) {
                map.put("sun", -9999999);
            } else {
                map.put("sun", IntPptn);
            }
            for (int i = 0; i < 24; i++) {
                Date dts = sdfTime.parse(time);
                //两小时降雨量
                Calendar cal = Calendar.getInstance();
                cal.setTime(dts);
                cal.add(Calendar.HOUR, -i);
                String time2 = sdfTime.format(cal.getTime());
                String id = wfo.getId();
                if (hour == 1) {
                    Double integer1 = wpdStPptnRDao.queryDaySumData1(time2, wfo.getId());
                    if (integer1 != null) {
                        map.put(time2, integer1);
                    } else {
                        map.put(time2, -9999999);
                    }
                }
                if (hour == 3) {
                    Double integer3 = wpdStPptnRDao.queryDaySumData3(time2, wfo.getId());
                    if (integer3 != null) {
                        map.put(time2, integer3);
                    } else {
                        map.put(time2, -9999999);
                    }
                }
                if (hour == 6) {
                    Double integer6 = wpdStPptnRDao.queryDaySumData6(time2, wfo.getId());
                    if (integer6 != null) {
                        map.put(time2, integer6);
                    } else {
                        map.put(time2, -9999999);
                    }
                }
                if (hour == 12) {
                    Double integer12 = wpdStPptnRDao.queryDaySumData12(time2, wfo.getId());
                    if (integer12 != null) {
                        map.put(time2, integer12);
                    } else {
                        map.put(time2, -9999999);
                    }
                }
            }
            jsonArrayPP.add(map);
        }
        return jsonArrayPP;
    }
}