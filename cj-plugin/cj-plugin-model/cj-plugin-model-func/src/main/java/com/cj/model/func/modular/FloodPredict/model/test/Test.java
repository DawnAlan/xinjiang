package com.cj.model.func.modular.FloodPredict.model.test;

import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.cj.model.func.modular.FloodPredict.model.TouTunHe.getFloodList;
import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.emptyProcessing;
import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.duration;
import static com.cj.model.func.modular.FloodPredict.utils.InputUtils.judgeDate;

public class Test {
    public static void main(String[] args) throws IOException, ParseException, InvalidFormatException, ExecutionException, InterruptedException {

        //模型参数输入设置
        ForecastInputParamNew paramForecastInputParamNew = new ForecastInputParamNew();
        paramForecastInputParamNew.setModelType(3);//3为场次
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        paramForecastInputParamNew.setPredictionTime(sFormat.parse("2022-07-18 00:00:00"));
        paramForecastInputParamNew.setDataStartTime(sFormat.parse("2023-01-01 00:00:00"));
        paramForecastInputParamNew.setPeriodTimeType(4);//1为月，2为旬，3为日，4为小时
        paramForecastInputParamNew.setPeriodTimeStep(1);//预报步长
        paramForecastInputParamNew.setPeriodTimeNum(144);//预报数量


        paramForecastInputParamNew =objectToList(paramForecastInputParamNew);//读取表格
        List<RainFallDto> rainPre = rainPredict(paramForecastInputParamNew);
        paramForecastInputParamNew.setRainFallDtos(rainPre);
        paramForecastInputParamNew = emptyProcessing(paramForecastInputParamNew);//异常值处理
        List<Date> a = judgeDate(paramForecastInputParamNew.getPredictionTime(), paramForecastInputParamNew.getPeriodTimeNum());

        TemporaryXlsx result = new TemporaryXlsx();
        result= getFloodList(paramForecastInputParamNew);
    }
    /**
     * 读取表格赋予初始值
     * @return
     * @throws IOException
     */
    public static ForecastInputParamNew objectToList (ForecastInputParamNew forecastInputParamNew) throws IOException {
        ForecastInputParamNew input = new ForecastInputParamNew();
        input.setModelType(forecastInputParamNew.getModelType());//3为场次
        input.setPredictionTime(forecastInputParamNew.getPredictionTime());
        input.setDataStartTime(forecastInputParamNew.getDataStartTime());
        input.setPeriodTimeType(forecastInputParamNew.getPeriodTimeType());//1为月，2为旬，3为日，4为小时
        input.setPeriodTimeStep(forecastInputParamNew.getPeriodTimeStep());//预报步长
        input.setPeriodTimeNum(forecastInputParamNew.getPeriodTimeNum());//预报数量
        LzzHydrologyParam lzzParam = new LzzHydrologyParam();
        IrrigatedHydrologyParam qjParam = new IrrigatedHydrologyParam();
        //楼庄子雨量站List
        Object[][] lzzRainObject = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\雨量站2022-7.xlsx","LZZ_RAINFALL_STATION_2023121317");
        List<LzzRainfallStation> bylcList=new ArrayList<>();
        List<LzzRainfallStation> dngList=new ArrayList<>();
        List<LzzRainfallStation> hgList=new ArrayList<>();
        List<LzzRainfallStation> jpsList=new ArrayList<>();
        List<LzzRainfallStation> ksgList=new ArrayList<>();
        List<LzzRainfallStation> mkgList=new ArrayList<>();
        List<LzzRainfallStation> sedwList=new ArrayList<>();
        List<LzzRainfallStation> wmgList=new ArrayList<>();
        List<LzzRainfallStation> zedList=new ArrayList<>();
        List<LzzRainfallStation> zccList=new ArrayList<>();
        for (int i = 1; i < lzzRainObject.length; i++) {
            if (lzzRainObject[i][1]==null){
                lzzRainObject[i][1]=0;
            }
            if (lzzRainObject[i][1].equals("八一林场自动雨量站")){//0
                for (int j = 0; j <lzzRainObject[0].length ; j++) {
                    if (lzzRainObject[i][j]==null){
                        lzzRainObject[i][j]=0;
                    }
                }
                LzzRainfallStation bylc=new LzzRainfallStation();//1
                bylc.setId((String) lzzRainObject[i][0]);
                bylc.setStationName((String) lzzRainObject[i][1]);
                BigDecimal rainDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][2]));
                bylc.setRainfall(rainDecimal);
                BigDecimal temDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][5]));
                bylc.setTemperature(temDecimal);
                bylc.setTime((Date) lzzRainObject[i][3]);
                bylc.setYear(lzzRainObject[i][4].toString());
                bylc.setTreeId(lzzRainObject[i][6].toString());
                bylcList.add(bylc);//2
            }
            if (lzzRainObject[i][1].equals("东南沟自动雨量站")){
                for (int j = 0; j <lzzRainObject[0].length ; j++) {
                    if (lzzRainObject[i][j]==null){
                        lzzRainObject[i][j]=0;
                    }
                }
                LzzRainfallStation dng=new LzzRainfallStation();//1
                dng.setId((String) lzzRainObject[i][0]);
                dng.setStationName((String) lzzRainObject[i][1]);
                BigDecimal rainDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][2]));
                dng.setRainfall(rainDecimal);
                BigDecimal temDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][5]));
                dng.setTemperature(temDecimal);
                dng.setTime((Date) lzzRainObject[i][3]);
                dng.setYear(lzzRainObject[i][4].toString());
                dng.setTreeId(lzzRainObject[i][6].toString());
                dngList.add(dng);
            }
            if (lzzRainObject[i][1].equals("黑沟自动雨量站")){
                for (int j = 0; j <lzzRainObject[0].length ; j++) {
                    if (lzzRainObject[i][j]==null){
                        lzzRainObject[i][j]=0;
                    }
                }
                LzzRainfallStation hg=new LzzRainfallStation();//1
                hg.setId((String) lzzRainObject[i][0]);
                hg.setStationName((String) lzzRainObject[i][1]);
                BigDecimal rainDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][2]));
                hg.setRainfall(rainDecimal);
                BigDecimal temDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][5]));
                hg.setTemperature(temDecimal);
                hg.setTime((Date) lzzRainObject[i][3]);
                hg.setYear(lzzRainObject[i][4].toString());
                hg.setTreeId(lzzRainObject[i][6].toString());
                hgList.add(hg);
            }
            if (lzzRainObject[i][1].equals("加普沙自动雨量站")){//0
                for (int j = 0; j <lzzRainObject[0].length ; j++) {
                    if (lzzRainObject[i][j]==null){
                        lzzRainObject[i][j]=0;
                    }
                }
                LzzRainfallStation jps=new LzzRainfallStation();//1
                jps.setId((String) lzzRainObject[i][0]);
                jps.setStationName((String) lzzRainObject[i][1]);
                BigDecimal rainDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][2]));
                jps.setRainfall(rainDecimal);
                BigDecimal temDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][5]));
                jps.setTemperature(temDecimal);
                jps.setTime((Date) lzzRainObject[i][3]);
                jps.setYear(lzzRainObject[i][4].toString());
                jps.setTreeId(lzzRainObject[i][6].toString());
                jpsList.add(jps);//2
            }
            if (lzzRainObject[i][1].equals("喀什沟自动雨量站")){//0
                for (int j = 0; j <lzzRainObject[0].length ; j++) {
                    if (lzzRainObject[i][j]==null){
                        lzzRainObject[i][j]=0;
                    }
                }
                LzzRainfallStation ksg=new LzzRainfallStation();//1
                ksg.setId((String) lzzRainObject[i][0]);
                ksg.setStationName((String) lzzRainObject[i][1]);
                BigDecimal rainDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][2]));
                ksg.setRainfall(rainDecimal);
                BigDecimal temDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][5]));
                ksg.setTemperature(temDecimal);
                ksg.setTime((Date) lzzRainObject[i][3]);
                ksg.setYear(lzzRainObject[i][4].toString());
                ksg.setTreeId(lzzRainObject[i][6].toString());
                ksgList.add(ksg);//2
            }
            if (lzzRainObject[i][1].equals("煤矿沟自动雨量站")){//0
                for (int j = 0; j <lzzRainObject[0].length ; j++) {
                    if (lzzRainObject[i][j]==null){
                        lzzRainObject[i][j]=0;
                    }
                }
                LzzRainfallStation mkg=new LzzRainfallStation();//1
                mkg.setId((String) lzzRainObject[i][0]);
                mkg.setStationName((String) lzzRainObject[i][1]);
                BigDecimal rainDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][2]));
                mkg.setRainfall(rainDecimal);
                BigDecimal temDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][5]));
                mkg.setTemperature(temDecimal);
                mkg.setTime((Date) lzzRainObject[i][3]);
                mkg.setYear(lzzRainObject[i][4].toString());
                mkg.setTreeId(lzzRainObject[i][6].toString());
                mkgList.add(mkg);//2
            }
            if (lzzRainObject[i][1].equals("萨尔达万自动雨量站")){//0
                for (int j = 0; j <lzzRainObject[0].length ; j++) {
                    if (lzzRainObject[i][j]==null){
                        lzzRainObject[i][j]=0;
                    }
                }
                LzzRainfallStation sedw=new LzzRainfallStation();//1
                sedw.setId((String) lzzRainObject[i][0]);
                sedw.setStationName((String) lzzRainObject[i][1]);
                BigDecimal rainDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][2]));
                sedw.setRainfall(rainDecimal);
                BigDecimal temDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][5]));
                sedw.setTemperature(temDecimal);
                sedw.setTime((Date) lzzRainObject[i][3]);
                sedw.setYear(lzzRainObject[i][4].toString());
                sedw.setTreeId(lzzRainObject[i][6].toString());
                sedwList.add(sedw);//2
            }
            if (lzzRainObject[i][1].equals("无名沟自动雨量站")){//0
                for (int j = 0; j <lzzRainObject[0].length ; j++) {
                    if (lzzRainObject[i][j]==null){
                        lzzRainObject[i][j]=0;
                    }
                }
                LzzRainfallStation wmg=new LzzRainfallStation();//1
                wmg.setId((String) lzzRainObject[i][0]);
                wmg.setStationName((String) lzzRainObject[i][1]);
                BigDecimal rainDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][2]));
                wmg.setRainfall(rainDecimal);
                BigDecimal temDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][5]));
                wmg.setTemperature(temDecimal);
                wmg.setTime((Date) lzzRainObject[i][3]);
                wmg.setYear(lzzRainObject[i][4].toString());
                wmg.setTreeId(lzzRainObject[i][6].toString());
                wmgList.add(wmg);//2
            }
            if (lzzRainObject[i][1].equals("宰尔德自动雨量站")){//0
                for (int j = 0; j <lzzRainObject[0].length ; j++) {
                    if (lzzRainObject[i][j]==null){
                        lzzRainObject[i][j]=0;
                    }
                }
                LzzRainfallStation zed=new LzzRainfallStation();//1
                zed.setId((String) lzzRainObject[i][0]);
                zed.setStationName((String) lzzRainObject[i][1]);
                BigDecimal rainDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][2]));
                zed.setRainfall(rainDecimal);
                BigDecimal temDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][5]));
                zed.setTemperature(temDecimal);
                zed.setTime((Date) lzzRainObject[i][3]);
                zed.setYear(lzzRainObject[i][4].toString());
                zed.setTreeId(lzzRainObject[i][6].toString());
                zedList.add(zed);//2
            }
            if (lzzRainObject[i][1].equals("制材厂自动雨量站")){//0
                for (int j = 0; j <lzzRainObject[0].length ; j++) {
                    if (lzzRainObject[i][j]==null){
                        lzzRainObject[i][j]=0;
                    }
                }
                LzzRainfallStation zcc=new LzzRainfallStation();//1
                zcc.setId((String) lzzRainObject[i][0]);
                zcc.setStationName((String) lzzRainObject[i][1]);
                BigDecimal rainDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][2]));
                zcc.setRainfall(rainDecimal);
                BigDecimal temDecimal = new BigDecimal(String.valueOf(lzzRainObject[i][5]));
                zcc.setTemperature(temDecimal);
                zcc.setTime((Date) lzzRainObject[i][3]);
                zcc.setYear(lzzRainObject[i][4].toString());
                zcc.setTreeId(lzzRainObject[i][6].toString());
                zccList.add(zcc);//2
            }
        }
        lzzParam.setBylcRainfallStation(bylcList);
        lzzParam.setDngRainfallStation(dngList);
        lzzParam.setHgRainfallStation(hgList);
        lzzParam.setJpsRainfallStation(jpsList);
        lzzParam.setKsgRainfallStation(ksgList);
        lzzParam.setMkgRainfallStation(mkgList);
        lzzParam.setSedwRainfallStation(sedwList);
        lzzParam.setWmgRainfallStation(wmgList);
        lzzParam.setZrdRainfallStation(zedList);
        lzzParam.setZccRainfallStation(zccList);
        //楼庄子水位站List
        Object[][] lzzFlowObject = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\楼庄子库水位站2022-7.xlsx","LZZ_GAUGING_STATION_20231213173");
        List<LzzGaugingStation> shqList=new ArrayList<>();
        List<LzzGaugingStation> lzzrList=new ArrayList<>();
        List<LzzGaugingStation> lzzwList=new ArrayList<>();
        List<LzzGaugingStation> lzzoList=new ArrayList<>();
        for (int i = 1; i < lzzFlowObject.length; i++) {
            if (lzzFlowObject[i][1]==null){
                lzzFlowObject[i][1]=0.0;
            }
            if (lzzFlowObject[i][1].equals("3号桥水位站")){//0
                for (int j = 0; j <lzzFlowObject[0].length ; j++) {
                    if (lzzFlowObject[i][j]==null){
                        lzzFlowObject[i][j]=0.0;
                    }
                }
                LzzGaugingStation shq=new LzzGaugingStation();//1
                shq.setId((String) lzzFlowObject[i][0]);
                shq.setStationName((String) lzzFlowObject[i][1]);
                shq.setRelativeWaterLevel((Double) lzzFlowObject[i][2]);
                //Flow
                if(lzzFlowObject[i][3] instanceof Integer) {
                    Integer inttValue = (Integer) lzzFlowObject[i][3];
                    Double tValue = Double.valueOf(inttValue);
                    shq.setFlow(tValue);
                }else if (lzzFlowObject[i][3] instanceof Double){
                    shq.setFlow((Double) lzzFlowObject[i][3]);
                }

                shq.setGatherTime((Date) lzzFlowObject[i][4]);
                //Temperture
                if(lzzFlowObject[i][5] instanceof Integer) {
                    Integer inttValue = (Integer) lzzFlowObject[i][5];
                    Double tValue = Double.valueOf(inttValue);
                    shq.setTemperature(tValue);
                }else if (lzzFlowObject[i][5] instanceof Double){
                    shq.setTemperature((Double) lzzFlowObject[i][5]);
                }
                //StorageCapacity
                if(lzzFlowObject[i][6] instanceof Integer) {
                    Integer inttValue = (Integer) lzzFlowObject[i][6];
                    Double tValue = Double.valueOf(inttValue);
                    shq.setStorageCapacity(tValue);
                }else if (lzzFlowObject[i][6] instanceof Double){
                    shq.setStorageCapacity((Double) lzzFlowObject[i][6]);
                }
                shq.setTreeId(lzzFlowObject[i][7].toString());
                shqList.add(shq);//2
            }
            if (lzzFlowObject[i][1].equals("楼庄子入库水位站")){//0
                for (int j = 0; j <lzzFlowObject[0].length ; j++) {
                    if (lzzFlowObject[i][j]==null){
                        lzzFlowObject[i][j]=0;
                    }
                }
                LzzGaugingStation lzzIn=new LzzGaugingStation();//1
                lzzIn.setId((String) lzzFlowObject[i][0]);
                lzzIn.setStationName((String) lzzFlowObject[i][1]);
                lzzIn.setRelativeWaterLevel((Double) lzzFlowObject[i][2]);
                //Flow
                if(lzzFlowObject[i][3] instanceof Integer) {
                    Integer inttValue = (Integer) lzzFlowObject[i][3];
                    Double tValue = Double.valueOf(inttValue);
                    lzzIn.setFlow(tValue);
                }else if (lzzFlowObject[i][3] instanceof Double){
                    lzzIn.setFlow((Double) lzzFlowObject[i][3]);
                }
                lzzIn.setGatherTime((Date) lzzFlowObject[i][4]);
                //Temperture
                if(lzzFlowObject[i][5] instanceof Integer) {
                    Integer inttValue = (Integer) lzzFlowObject[i][5];
                    Double tValue = Double.valueOf(inttValue);
                    lzzIn.setTemperature(tValue);
                }else if (lzzFlowObject[i][5] instanceof Double){
                    lzzIn.setTemperature((Double) lzzFlowObject[i][5]);
                }

                Integer intSValue = (Integer) lzzFlowObject[i][6];
                Double sValue = Double.valueOf(intSValue);
                lzzIn.setStorageCapacity(sValue);
                lzzIn.setTreeId(lzzFlowObject[i][7].toString());
                lzzrList.add(lzzIn);//2
            }
            if (lzzFlowObject[i][1].equals("楼庄子库水位站")){//0
                for (int j = 0; j <lzzFlowObject[0].length ; j++) {
                    if (lzzFlowObject[i][j]==null){
                        lzzFlowObject[i][j]=0;
                    }
                }
                LzzGaugingStation lzzW=new LzzGaugingStation();//1
                lzzW.setId((String) lzzFlowObject[i][0]);
                lzzW.setStationName((String) lzzFlowObject[i][1]);
                lzzW.setRelativeWaterLevel((Double) lzzFlowObject[i][2]);
                //Flow
                if(lzzFlowObject[i][3] instanceof Integer) {
                    Integer inttValue = (Integer) lzzFlowObject[i][3];
                    Double tValue = Double.valueOf(inttValue);
                    lzzW.setFlow(tValue);
                }else if (lzzFlowObject[i][3] instanceof Double){
                    lzzW.setFlow((Double) lzzFlowObject[i][3]);
                }
                lzzW.setGatherTime((Date) lzzFlowObject[i][4]);
                //Temperture
                if(lzzFlowObject[i][5] instanceof Integer) {
                    Integer inttValue = (Integer) lzzFlowObject[i][5];
                    Double tValue = Double.valueOf(inttValue);
                    lzzW.setTemperature(tValue);
                }else if (lzzFlowObject[i][5] instanceof Double){
                    lzzW.setTemperature((Double) lzzFlowObject[i][5]);
                }
                lzzW.setTreeId(lzzFlowObject[i][7].toString());
                lzzwList.add(lzzW);//2
            }
            if (lzzFlowObject[i][1].equals("楼庄子出库水位站")){//0
                for (int j = 0; j <lzzFlowObject[0].length ; j++) {
                    if (lzzFlowObject[i][j]==null){
                        lzzFlowObject[i][j]=0;
                    }
                }
                LzzGaugingStation lzzo=new LzzGaugingStation();//1
                lzzo.setId((String) lzzFlowObject[i][0]);
                lzzo.setStationName((String) lzzFlowObject[i][1]);
                lzzo.setRelativeWaterLevel((Double) lzzFlowObject[i][2]);
                //Flow
                if(lzzFlowObject[i][3] instanceof Integer) {
                    Integer inttValue = (Integer) lzzFlowObject[i][3];
                    Double tValue = Double.valueOf(inttValue);
                    lzzo.setFlow(tValue);
                }else if (lzzFlowObject[i][3] instanceof Double){
                    lzzo.setFlow((Double) lzzFlowObject[i][3]);
                }
                lzzo.setGatherTime((Date) lzzFlowObject[i][4]);
                //Temperture
                if(lzzFlowObject[i][5] instanceof Integer) {
                    Integer inttValue = (Integer) lzzFlowObject[i][5];
                    Double tValue = Double.valueOf(inttValue);
                    lzzo.setTemperature(tValue);
                }else if (lzzFlowObject[i][5] instanceof Double){
                    lzzo.setTemperature((Double) lzzFlowObject[i][5]);
                }
                lzzo.setTreeId(lzzFlowObject[i][7].toString());
                lzzoList.add(lzzo);//2
            }
        }
        lzzParam.setThreeGaugingStation(shqList);
        lzzParam.setLzzInput(lzzrList);
        lzzParam.setLzzWaterLevel(lzzwList);
        lzzParam.setLzzOutput(lzzoList);
        input.setLzzHydrologyParam(lzzParam);

        Object[][] qjObject = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\区间.xlsx","区间");
        List<IrrigatedPlatformDataInfo> xqzList=new ArrayList<>();
        List<IrrigatedPlatformDataInfo> tjydList=new ArrayList<>();
        List<IrrigatedPlatformDataInfo> tthRList=new ArrayList<>();
        List<IrrigatedPlatformDataInfo> tthFList=new ArrayList<>();

        for (int i = 1; i < qjObject.length; i++) {
            if (qjObject[i][1]==null){
                qjObject[i][1]=0.0;
            }
            if (qjObject[i][3].equals("小渠子雨量站")){//0
                for (int j = 0; j <qjObject[0].length ; j++) {
                    if (qjObject[i][j]==null){
                        qjObject[i][j]=0.0;
                    }
                }
                IrrigatedPlatformDataInfo xqz=new IrrigatedPlatformDataInfo();//1
                xqz.setId((String) qjObject[i][0]);
                xqz.setMonitorId((String) qjObject[i][3]);
                Date date = (Date) qjObject[i][11];
                xqz.setMonitorTime(date);
                xqz.setYqRainFallOne((Double) qjObject[i][17]);
                xqzList.add(xqz);//2
            }
            if (qjObject[i][3].equals("团结一队雨量站")){//0
                for (int j = 0; j <qjObject[0].length ; j++) {
                    if (qjObject[i][j]==null){
                        qjObject[i][j]=0.0;
                    }
                }
                IrrigatedPlatformDataInfo tjyd=new IrrigatedPlatformDataInfo();//1
                tjyd.setId((String) qjObject[i][0]);
                tjyd.setMonitorId((String) qjObject[i][3]);
                Date date = (Date) qjObject[i][11];
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = dateFormat.format(date);
                tjyd.setMonitorTime(date);
                tjyd.setYqRainFallOne((Double) qjObject[i][17]);
                tjydList.add(tjyd);//2
            }
            if (qjObject[i][3].equals("头屯河水库雨量站")){//0
                for (int j = 0; j <qjObject[0].length ; j++) {
                    if (qjObject[i][j]==null){
                        qjObject[i][j]=0.0;
                    }
                }
                IrrigatedPlatformDataInfo tthR=new IrrigatedPlatformDataInfo();//1
                tthR.setId((String) qjObject[i][0]);
                tthR.setMonitorId((String) qjObject[i][3]);
                Date date = (Date) qjObject[i][11];
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = dateFormat.format(date);
                tthR.setMonitorTime(date);
                tthR.setYqRainFallOne((Double) qjObject[i][17]);
                tthRList.add(tthR);//2
            }
            if (qjObject[i][3].equals("入库流量")){//0
                for (int j = 0; j <qjObject[0].length ; j++) {
                    if (qjObject[i][j]==null){
                        qjObject[i][j]=0.0;
                    }
                }
                IrrigatedPlatformDataInfo tthF=new IrrigatedPlatformDataInfo();//1
                tthF.setId((String) qjObject[i][0]);
                tthF.setMonitorId((String) qjObject[i][3]);
                tthF.setSqMonitorFlow((Double) qjObject[i][5]);
                Date date = (Date) qjObject[i][11];
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = dateFormat.format(date);
                tthF.setMonitorTime(date);
                tthF.setYqRainFallOne((Double) qjObject[i][17]);
                tthFList.add(tthF);//2
            }
            qjParam.setTthInput(tthFList);
            qjParam.setTthGaugingStation(tthRList);
            qjParam.setTjydGaugingStation(tjydList);
            qjParam.setXqzGaugingStation(xqzList);
        }
        input.setIrrigatedHydrologyParam(qjParam);
        return input;
    }

    /**
     * 预报雨量赋值
     * @param input
     * @return
     */
    public static List<RainFallDto> rainPredict (ForecastInputParamNew input){
        List<RainFallDto> result = new ArrayList<>();
        RainFallDto rainFallDto = new RainFallDto();
        Date dateNew = new Date();
        Date dateStart = input.getPredictionTime();
        int start_end = input.getPeriodTimeStep()*input.getPeriodTimeNum();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStart);
        calendar.add(Calendar.HOUR_OF_DAY, start_end);
        Date dateEnd = calendar.getTime();
        int length = duration(dateNew,dateEnd,"小时");
        for (int i = 0; i < length; i++) {
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("喀什沟自动雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("黑沟自动雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("煤矿沟自动雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("无名沟自动雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("加普沙自动雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("宰尔德自动雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("东南沟自动雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("八一林场自动雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("萨尔达万自动雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("制材厂自动雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("小渠子雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("团结一队雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            rainFallDto = new RainFallDto();
            rainFallDto.setArea("头屯河水库雨量站");
            rainFallDto.setRainFall(i*0.1);
            rainFallDto.setDate(String.valueOf(dateNew));
            result.add(rainFallDto);
            calendar.setTime(dateNew);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            dateNew = calendar.getTime();
        }
        List<String> stationName =new ArrayList<>();
        List<RainFallDto> resultSort =new ArrayList<>();
        if(!result.isEmpty()){
            for (int i = 0; i < result.size(); i++) //后续更改，目前写死为13个雨量站
            {
                stationName.add(result.get(i).getArea());
            }
            RainFallDto rainFallDto1 = new RainFallDto();
            for (int i = 0; i < 13; i++) {
                for (RainFallDto fallDto : result) {
                    if (fallDto.getArea().equals(stationName.get(i))) {
                        rainFallDto1 = fallDto;
                        resultSort.add(rainFallDto1);
                    }
                }
            }
        }
        return resultSort;
    }
}
