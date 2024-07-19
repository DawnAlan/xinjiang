package com.cj.model.func.modular.FloodPredict.model;

import com.alibaba.fastjson.JSONObject;
import com.cj.model.func.modular.FloodPredict.Calibration.ShanBeiModel;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.entity.RainFallDto;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import com.cj.model.func.modular.FloodPredict.model.function.SimulatedRunoff;
import com.cj.model.func.modular.FloodPredict.model.function.SnowMeltModel;
import com.cj.model.func.modular.FloodPredict.utils.*;
import com.cj.model.func.modular.FloodPrevent.entity.BasinParam;
import com.cj.model.func.modular.entity.Flood;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.model.Test.readJSONFromFile;




public class GBHM {
    static TimeUtils tu = new TimeUtils();
    static DataUtils du = new DataUtils();
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static int hours = InputUtils.beforeHours;
    static MachineDataUtils mdu = new MachineDataUtils();
    @SneakyThrows
    public static void main(String[] args) {
        String level = "千年一遇";
//        Object[][] rainData_Hour = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游雨量站22年.xlsx", "06-01~08-31逐小时逐断面整理数据");
        Object[][] rainData_Hour = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\模拟降雨.xlsx", level);
        /**
         * 通过模拟降雨来实现模拟洪水
         */
        List<RainFallDto> rain = new ArrayList<>();
        for (int i = 1; i < rainData_Hour.length; i++) {
            RainFallDto jps = new RainFallDto();
            jps.setArea("加普沙自动雨量站");
            jps.setDate(sdf.format(rainData_Hour[i][0]));
            jps.setRainFall((double)rainData_Hour[i][1]);
            rain.add(jps);
            RainFallDto dng = new RainFallDto();
            dng.setArea("东南沟自动雨量站");
            dng.setDate(sdf.format(rainData_Hour[i][0]));
            dng.setRainFall((double)rainData_Hour[i][2]);
            rain.add(dng);
            RainFallDto zed = new RainFallDto();
            zed.setArea("宰尔德自动雨量站");
            zed.setDate(sdf.format(rainData_Hour[i][0]));
            zed.setRainFall((double)rainData_Hour[i][3]);
            rain.add(zed);
            RainFallDto wmg = new RainFallDto();
            wmg.setArea("无名沟自动雨量站");
            wmg.setDate(sdf.format(rainData_Hour[i][0]));
            wmg.setRainFall((double)rainData_Hour[i][4]);
            rain.add(wmg);
            RainFallDto bylc = new RainFallDto();
            bylc.setArea("八一林场自动雨量站");
            bylc.setDate(sdf.format(rainData_Hour[i][0]));
            bylc.setRainFall((double)rainData_Hour[i][5]);
            rain.add(bylc);
            RainFallDto sedw = new RainFallDto();
            sedw.setArea("萨尔达万自动雨量站");
            sedw.setDate(sdf.format(rainData_Hour[i][0]));
            sedw.setRainFall((double)rainData_Hour[i][6]);
            rain.add(sedw);
            RainFallDto mkg = new RainFallDto();
            mkg.setArea("煤矿沟自动雨量站");
            mkg.setDate(sdf.format(rainData_Hour[i][0]));
            mkg.setRainFall((double)rainData_Hour[i][7]);
            rain.add(mkg);
            RainFallDto hg = new RainFallDto();
            hg.setArea("黑沟自动雨量站");
            hg.setDate(sdf.format(rainData_Hour[i][0]));
            hg.setRainFall((double)rainData_Hour[i][8]);
            rain.add(hg);
            RainFallDto ksg = new RainFallDto();
            ksg.setArea("喀什沟自动雨量站");
            ksg.setDate(sdf.format(rainData_Hour[i][0]));
            ksg.setRainFall((double)rainData_Hour[i][9]);
            rain.add(ksg);
            RainFallDto zcc = new RainFallDto();
            zcc.setArea("制材厂自动雨量站");
            zcc.setDate(sdf.format(rainData_Hour[i][0]));
            zcc.setRainFall((double)rainData_Hour[i][10]);
            rain.add(zcc);
            RainFallDto lzz = new RainFallDto();
            lzz.setArea("甘沟雨量站");
            lzz.setDate(sdf.format(rainData_Hour[i][0]));
            lzz.setRainFall((double)rainData_Hour[i][11]);
            rain.add(lzz);
            RainFallDto xqz = new RainFallDto();
            xqz.setArea("小渠子雨量站");
            xqz.setDate(sdf.format(rainData_Hour[i][0]));
            xqz.setRainFall((double)rainData_Hour[i][12]);
            rain.add(xqz);
            RainFallDto tjyd = new RainFallDto();
            tjyd.setArea("团结一队雨量站");
            tjyd.setDate(sdf.format(rainData_Hour[i][0]));
            tjyd.setRainFall((double)rainData_Hour[i][13]);
            rain.add(tjyd);
            RainFallDto tth = new RainFallDto();
            tth.setArea("头屯河水库雨量站");
            tth.setDate(sdf.format(rainData_Hour[i][0]));
            tth.setRainFall((double)rainData_Hour[i][14]);
            rain.add(tth);
        }

        ForecastInputParam param = new ForecastInputParam();
        param.setIsReferenceWater(true);
        param.setIsShortForecast(true);
//        param.setBasinStr(JSONObject.toJSONString(JSONObject.parseObject(IOUtils.toString(URI.create("D:\\204\\2.头屯河\\Basin.json"), StandardCharsets.UTF_8), BasinParam.class)));
        param.setBasinStr(readJSONFromFile("D:\\204\\2.头屯河\\Basin.json"));
        param.setLocation("楼庄子");
        param.setPreFlow(7.5);
        param.setPreRainFall(60.0);
        param.setRainFallDtos(rain);
        param.setPeriodStepNumber(rainData_Hour.length-1);
        param.setPeriodStepSize(1);
        param.setPreStartTime((Date) rainData_Hour[1][0]);
        List<Flood> Flood_Lzz;List<Flood> Flood_qj;
        param.setLocation("楼庄子");
        Flood_Lzz = new SimulatedRunoff().simulation(param);
        param.setLocation("楼头区间");
        Flood_qj = new SimulatedRunoff().simulation(param);
        List<PredictInputData> a = new ArrayList<>();
        List<Flood> Flood_Tth = new TouTunHe().getTTH(param,a,Flood_Lzz, Flood_qj);
        List<Flood> result = new ArrayList<>();
        result.addAll(Flood_Lzz);
        result.addAll(Flood_qj);
        result.addAll(Flood_Tth);
        //返回文件
//        testXlsx(result);
//        excelSimulation2(level,rainData_Hour);
//        excelSimulation(level,sdf.parse("2022-06-15 00:00:00"),sdf.parse("2022-06-20 01:00:00"),rainData_Hour);
//        excelSimulation(level,sdf.parse("2022-06-23 00:00:00"),sdf.parse("2022-06-28 01:00:00"),rainData_Hour);
//        excelSimulation(level,sdf.parse("2022-07-18 00:00:00"),sdf.parse("2022-07-25 01:00:00"),rainData_Hour);

    }
    @SneakyThrows
    public static Object[][] snowFlood(Date date){

        ForecastInputParam param = new ForecastInputParam();
        param.setBasinStr("D:\\tth_system\\end\\file\\");
        param.setLocation("楼庄子");
        param.setPreStartTime(date);
        param.setPeriod("日");
        param.setIsSnowMeltModel(true);
        param.setIsAverage(false);
        param.setIsTrain(false);
        param.setPeriodStepNumber(1);
        param.setPeriodStepSize(1);
        param.setPredict_day(1);
        InputUtils.getData2(param.getBasinStr());
        //当天温度
        List<PredictInputData> preRainTem = new ArrayList<>();
        int l = param.getPeriodStepNumber()*param.getPeriodStepSize();
        Date endTime = tu.addCalendar(date,"日",l);
        preRainTem = new ArrayList<>();
        Object[][] lzz = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\径流预报数据文件\\融雪期.xlsx","楼庄子日");
//        Object[][] lzz = InputUtils.historyData.get("楼庄子日");
        List<Object[]> lzzList = tu.getTimeIntervalList(lzz,date,endTime);
        for (int i = 0; i < l; i++) {
            Date date1 = tu.addCalendar(date,"日",i);
            preRainTem.add(du.daysDataListToPID(lzzList,"楼庄子",date1));
        }
        param.setPreRainTem(preRainTem);
        Object[][] data = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\径流预报数据文件\\融雪期.xlsx","楼庄子日");
        //训练模型获得参数以及其储存路径
        param.setPreStartTime(sdf.parse("2023-08-30 00:00:00"));
        Object[][] machineInput0 = mdu.getDataInput(data, new Object[0][0], param);
        Object[][] snowMeltInput0 = mdu.snowMeltDate(machineInput0, "楼庄子");
        if (param.getIsAverage()){
            snowMeltInput0 = mdu.inputProcessing(snowMeltInput0, param.getLocation());//获得距平值
        }
        SnowMeltModel model = new SnowMeltModel();
        //是否训练模型
        if (param.getIsTrain()) {
            model.snowTrain(snowMeltInput0, param);
        }


        param.setPreStartTime(date);
        Object[][] machineInput = mdu.getDataInput(data, new Object[0][0], param);
        Object[][] snowMeltInput = mdu.snowMeltDate(machineInput, "楼庄子");
        List<Flood> snow = new SnowMeltModel().snowForecast(snowMeltInput, param);
        Object[][] result = new Object[snow.size()][2];
        for (int i = 0; i < snow.size(); i++) {
            result[i][0]=snow.get(i).getTime();
            result[i][1]=snow.get(i).getPreQ();
        }
        return result;
    }
    @SneakyThrows
    public static void excelSimulation(String level,Date startTime,Date endTime,Object[][] rainData_Hour){
        /**
         * 通过本地文件实现对历史径流或者模拟径流的复现
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
//        Date startTime = sdf.parse("2022-06-23 00:00:00");
//        Date endTime = sdf.parse("2022-08-01 07:00:00");
//        Date endTime = sdf.parse("2022-06-28 01:00:00");
        int month = tu.getSpecificDate(startTime).get("月");
        double base = 7.5;
        int l = tu.duration(startTime,endTime,"小时");
        int d = tu.duration(startTime,endTime,"日");
        Object[][] snowData = new Object[d][2];
        for (int i = 0; i < d; i++) {
            Object[][] snow = snowFlood(tu.addCalendar(startTime,"日",i));
            snowData[i]=snow[0];
        }

        Object[][] rainData_Day = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游雨量站22年.xlsx", "06-01~08-31逐日逐断面整理数据");

        List<String> location = new ArrayList<>();
        location.add("八一林场自动雨量站");
        location.add("东南沟自动雨量站");
        location.add("黑沟自动雨量站");
        location.add("加普沙自动雨量站");
        location.add("喀什沟自动雨量站");
        location.add("煤矿沟自动雨量站");
        location.add("萨尔达万自动雨量站");
        location.add("无名沟自动雨量站");
        location.add("宰尔德自动雨量站");
        location.add("制材厂自动雨量站");
//        location.add("小渠子雨量站");
//        location.add("团结一队雨量站");
//        location.add("楼庄子库区雨量站");
//        location.add("头屯河水库雨量站");
        List<Double> area = new ArrayList<>();
        area.add(411.23);//八一林场
        area.add(186.29);//东南沟
        area.add(52.27);//黑沟
        area.add(176.21);//加普沙
        area.add(98.41);//喀什沟
        area.add(35.52);//煤矿沟
        area.add(22.55);//萨尔达万
        area.add(22.98);//无名沟
        area.add(50.67);//宰尔德
        area.add(137.44);//制材厂
//        area.add(52.27);//黑沟
//        area.add(98.41);//喀什沟
//        area.add(137.44);//制材厂
//        area.add(44.04);
//        area.add(82.56);
//        area.add(75.84);
//        area.add(56.54);
        List<Double> wm = new ArrayList<>();
        wm.add(120.0);//八一林场
        wm.add(100.0);//东南沟
        wm.add(100.0);//黑沟
        wm.add(120.0);//加普沙
        wm.add(100.0);//喀什沟
        wm.add(100.0);//煤矿沟
        wm.add(100.0);//萨尔达万
        wm.add(100.0);//无名沟
        wm.add(100.0);//宰尔德
        wm.add(100.0);//制材厂
        List<Double> bList = new ArrayList<>();
        bList.add(0.2);//bayi
        bList.add(0.2);//dong
        bList.add(0.1);//hei
        bList.add(0.2);//jia
        bList.add(0.1);//ka
        bList.add(0.1);//mei
        bList.add(0.1);//sa
        bList.add(0.1);//wu
        bList.add(0.1);//zai
        bList.add(0.2);//zhi
//        bList.add(0.1);
//        bList.add(0.1);
//        bList.add(0.1);
//        bList.add(0.1);
        List<Double> csList = new ArrayList<>();
        csList.add(0.96);//ba
        csList.add(0.94);//dong
        csList.add(0.8);//hei
        csList.add(0.94);//jia
        csList.add(0.8);//ka
        csList.add(0.8);//mei
        csList.add(0.8);//sa
        csList.add(0.8);//wu
        csList.add(0.8);//zai
        csList.add(0.8);//zhi

        List<Integer> LList = new ArrayList<>();
        LList.add(24);//八一林场
        LList.add(18);//dong
        LList.add(5);//黑沟
        LList.add(16);//jia
        LList.add(6);//喀什沟
        LList.add(6);//mei
        LList.add(6);//sa
        LList.add(8);//wu
        LList.add(8);//zai
        LList.add(1);//制材厂
        LList.add(4);LList.add(3);LList.add(4);LList.add(2);
        Object[][] result  =new Object[1+l][19];
        result[0][0] = "时间";result[0][1] = "八一林场";result[0][2] = "东南沟";result[0][3] = "黑沟";result[0][4] = "加普沙";result[0][5] = "喀什沟";
        result[0][6] = "煤矿沟";result[0][7] = "萨尔达万";result[0][8] = "无名沟";result[0][9] = "宰尔德";result[0][10] = "制材厂";
        result[0][11] = "小渠子";result[0][12] = "团结队";result[0][13] = "楼庄子库区";result[0][14] = "头屯河水库";
        for (int i = 0; i < location.size(); i++) {//不同雨量站
            String station = location.get(i);
            int s = 0;
            for (int j = 0; j < rainData_Hour[0].length; j++) {
                if (station.equals(rainData_Hour[0][j])){
                    s=j;
                }
            }
            Object[][] rain_hour = new Object[l][3];
            double max = 0.0;
            for (int j = 0; j < rainData_Hour.length-1; j++) {//期间降雨蒸发
                for (int k = 0; k < l; k++) {
                    if (tu.DateCompare((Date) rainData_Hour[j+1][0],tu.addCalendar(startTime,"小时",k),"小时")){
                        rain_hour[k][0] = rainData_Hour[j+1][0];
                        rain_hour[k][1] = 0.2;
                        rain_hour[k][2] = rainData_Hour[j+1][s];
                        if ((Double)rainData_Hour[j+1][s]>max){
                            max = (Double)rainData_Hour[j+1][s];
                        }
                    }
                }
            }
            Date stime = sdf.parse("2022-05-01 00:00:00");
            int m = tu.duration(stime,startTime,"日");
            Object[][] rain_day = new Object[30][2];
            for (int j = 0; j < rainData_Day.length-1; j++) {//前期降雨
                Date beforeTime = tu.addCalendar(startTime,"日",-rain_day.length);
                for (int k = 0; k < rain_day.length; k++) {
                    if (tu.DateCompare((Date) rainData_Day[j+1][0],tu.addCalendar(beforeTime,"日",k),"日")){
                        rain_day[k][0] = rainData_Day[j+1][0];
                        rain_day[k][1] = rainData_Day[j+1][s];
//                        rain_day[k][1] = 3;
                    }
                }
            }
            ShanBeiModel sm = new ShanBeiModel();
            ShanbeiParam shanbeiparam = new ShanbeiParam();
            shanbeiparam.setArea(area.get(s-1));
            shanbeiparam.setFB(0.008);
            shanbeiparam.setWM(wm.get(s-1));
            shanbeiparam.setKC(0.96);
//            if (month==6){
//                shanbeiparam.setKC(0.99);
//            } else if (month==7) {
////                shanbeiparam.setKC(0.96);
//                shanbeiparam.setKC(1.0);
//            }else {
//                shanbeiparam.setKC(1.0);
//            }
            shanbeiparam.setFC(20.0);
            shanbeiparam.setFM(80.0);
            shanbeiparam.setK(0.2);
            shanbeiparam.setB(bList.get(s-1));
            if (max >= 16){
                if (area.get(s-1)==411.23||area.get(s-1)==186.29||area.get(s-1)==176.21){
                    shanbeiparam.setCS(csList.get(s-1)-0.02);
                }else {
                    shanbeiparam.setCS(csList.get(s-1)-0.2);
                }
                shanbeiparam.setL(LList.get(s-1)-4);
            } else if (max >= 8) {
                if (area.get(s-1)==411.23||area.get(s-1)==186.29||area.get(s-1)==176.21){
                    shanbeiparam.setCS(csList.get(s-1)-0.01);
                }else {
                    shanbeiparam.setCS(csList.get(s-1)-0.1);
                }
                shanbeiparam.setL(LList.get(s-1)-2);
            }else {
                shanbeiparam.setCS(csList.get(s-1));
                shanbeiparam.setL(LList.get(s-1));
            }

            sm.InputData(shanbeiparam, rain_hour, rain_day);
            sm.InitialMoistureContentCalculation();
            sm.RunoffYieldCalculation_UnevenInfiltration();
            sm.ConfluenceCalculation2();
            for (int j = 0; j < l; j++) {
                result[1+j][s] = sm.Q[j];
            }
        }
        Object[][] flow = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游22年流量.xlsx", "进库");
        for (int i = 1; i < l+1; i++) {
            result[0][16] = "模拟";
            result[0][15] = "实测";
            result[0][17] = "融雪";
            result[0][18] = "叠加融雪";
            for (int j = 0; j < flow.length; j++) {
                if (tu.DateCompare((Date) flow[j][2],tu.addCalendar(startTime,"小时",i),"小时")){
                    result[i][0] = flow[j][2];
                    result[i][15] = flow[j][1];
                }
            }
        }
        for (int i = 0; i < l; i++) {//上游
            double sum = 0.0;
            for (int j = 0; j < 10; j++) {
                sum += (double)result[1+i][1+j];
            }
            result[1+i][16] = sum + base;
        }

        for (int i = 1; i < l+1; i++) {
            for (int j = 0; j < snowData.length; j++) {
                if (tu.DateCompare((Date) result[i][0],(Date) snowData[j][0],"日")){
                    result[i][17] = snowData[j][1];
                    result[i][18] = (Double)result[i][16] + (Double) snowData[j][1] - base;
                }
            }
        }


//        for (int i = 0; i < l; i++) {//区间
//            double sum = 0.0;
//            for (int j = 0; j < 4; j++) {
//                sum += (double)result[1+i][11+j];
//            }
//            result[1+i][16] = sum + 4;
//        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\子流域预报.xlsx", sdf1.format(startTime),result);
//        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\子流域预报.xlsx", level,result);
    }
    @SneakyThrows
    public static void excelSimulation2(String level,Object[][] rainData_Hour){
        /**
         * 通过本地文件实现对历史径流或者模拟径流的复现
         */
        Object[][] rainData_Day = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游雨量站22年.xlsx", "06-01~08-31逐日逐断面整理数据");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = sdf.parse("2022-07-30 00:00:00");
//        Date endTime = sdf.parse("2022-08-01 07:00:00");
        Date endTime = sdf.parse("2022-08-06 00:00:00");
        int l = tu.duration(startTime,endTime,"小时");
        List<String> location = new ArrayList<>();
        location.add("加普沙自动雨量站");
        location.add("东南沟自动雨量站");
        location.add("宰尔德自动雨量站");
        location.add("无名沟自动雨量站");
        location.add("八一林场自动雨量站");
        location.add("萨尔达万自动雨量站");
        location.add("煤矿沟自动雨量站");
        location.add("黑沟自动雨量站");
        location.add("喀什沟自动雨量站");
        location.add("制材厂自动雨量站");
        location.add("小渠子雨量站");
        location.add("团结一队雨量站");
        location.add("楼庄子库区雨量站");
        location.add("头屯河水库雨量站");
        List<Double> area = new ArrayList<>();
        area.add(176.21);
        area.add(186.29);
        area.add(50.67);
        area.add(22.98);
        area.add(411.23);
        area.add(22.55);
        area.add(35.52);
        area.add(52.27);
        area.add(98.41);
        area.add(137.44);
        area.add(44.04);
        area.add(82.56);
        area.add(75.84);
        area.add(56.54);
        List<Integer> LList = new ArrayList<>();
        LList.add(18);LList.add(14);LList.add(12);LList.add(12);LList.add(14);LList.add(10);LList.add(10);LList.add(6);LList.add(4);LList.add(2);
        LList.add(4);LList.add(3);LList.add(4);LList.add(2);
        Object[][] result  =new Object[1+l][18];
        result[0][0] = "时间";result[0][1] = "加普沙";result[0][2] = "东南沟";result[0][3] = "宰尔德";result[0][4] = "无名沟";result[0][5] = "八一林场";
        result[0][6] = "萨尔达万";result[0][7] = "煤矿沟";result[0][8] = "黑沟";result[0][9] = "喀什沟";result[0][10] = "制材厂";
        result[0][11] = "小渠子";result[0][12] = "团结队";result[0][13] = "楼庄子库区";result[0][14] = "头屯河水库";
        for (int i = 0; i < location.size(); i++) {//不同雨量站
            String station = location.get(i);
            int s = 0;
            for (int j = 0; j < rainData_Hour[0].length; j++) {
                if (station.equals(rainData_Hour[0][j])){
                    s=j;
                }
            }
            Object[][] rain_hour = new Object[l][3];
            for (int j = 0; j < rainData_Hour.length-1; j++) {//期间降雨蒸发
                for (int k = 0; k < l; k++) {
                    if (tu.DateCompare((Date) rainData_Hour[j+1][0],tu.addCalendar(startTime,"小时",k),"小时")){
                        rain_hour[k][0] = rainData_Hour[j+1][0];
                        rain_hour[k][1] = 0.2;
                        rain_hour[k][2] = rainData_Hour[j+1][s];
                    }
                }
            }
            Object[][] rain_day = new Object[20][2];
            for (int j = 0; j < rainData_Day.length-1; j++) {//前期降雨
                Date beforeTime = tu.addCalendar(startTime,"日",-rain_day.length);
                for (int k = 0; k < rain_day.length; k++) {
                    if (tu.DateCompare((Date) rainData_Day[j+1][0],tu.addCalendar(beforeTime,"日",k),"日")){
                        rain_day[k][0] = rainData_Day[j+1][0];
//                        rain_day[k][1] = rainData_Day[j+1][s];
                        rain_day[k][1] = 3;
                    }
                }
            }
            ShanBeiModel sm = new ShanBeiModel();
            ShanbeiParam shanbeiparam = new ShanbeiParam();
            shanbeiparam.setArea(area.get(s-1));
            shanbeiparam.setFB(0.008);
            shanbeiparam.setWM(70.0);
            shanbeiparam.setKC(1.0);
            shanbeiparam.setFC(20.0);
            shanbeiparam.setFM(60.0);
            shanbeiparam.setK(0.2);
            shanbeiparam.setB(0.3);
            shanbeiparam.setCS(0.96);
            shanbeiparam.setL(LList.get(s-1));
            sm.InputData(shanbeiparam, rain_hour, rain_day);
            sm.InitialMoistureContentCalculation();
            sm.RunoffYieldCalculation_UnevenInfiltration();
            sm.ConfluenceCalculation2();
            for (int j = 0; j < l; j++) {
                if (j<hours){
                    result[1+j][s] = 0.0;
                }else {
                    result[1+j][s] = sm.Q[j-hours];
                }
            }
        }
//        Object[][] flow = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\楼庄子上游22年流量.xlsx", "进库");
//        for (int i = 1; i < l+1; i++) {
//            result[0][11] = "实测";
//            for (int j = 0; j < flow.length; j++) {
//                if (tu.DateCompare((Date) flow[j][2],tu.addCalendar(startTime,"小时",i),"小时")){
//                    result[i][0] = flow[j][2];
//                    result[i][11] = flow[j][1];
//                }
//            }
//        }
        for (int i = 0; i < l; i++) {//上游
            double sum = 0.0;
            for (int j = 0; j < 10; j++) {
                sum += (double)result[1+i][1+j];
            }
            result[1+i][15] = sum + 7.5;
        }

        for (int i = 0; i < l; i++) {//区间
            double sum = 0.0;
            for (int j = 0; j < 4; j++) {
                sum += (double)result[1+i][11+j];
            }
            result[1+i][16] = sum + 4;
        }
//        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\子流域预报.xlsx", sdf1.format(startTime),result);
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\子流域预报.xlsx", level,result);
    }
}
