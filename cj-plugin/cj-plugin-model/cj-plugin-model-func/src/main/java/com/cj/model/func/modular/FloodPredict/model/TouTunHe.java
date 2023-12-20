package com.cj.model.func.modular.FloodPredict.model;


import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
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

import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.*;


public class TouTunHe {

    public static void main(String[] args) {
        try {
            //模型参数输入设置
            ForcastInputParamNew paramForcastInputParamNew = new ForcastInputParamNew();
            paramForcastInputParamNew.setModelType(3);
            SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            paramForcastInputParamNew.setPredictionTime(sFormat.parse("2022-11-05 00:00:00"));
            paramForcastInputParamNew.setPeriodTimeType(4);
            paramForcastInputParamNew.setPeriodTimeStep(1);
            paramForcastInputParamNew.setPeriodTimeNum(26);
            paramForcastInputParamNew=objectToList(paramForcastInputParamNew);//读取表格
            //模型输出
            TemporaryXlsx result = new TemporaryXlsx();
            result= getFloodList(paramForcastInputParamNew);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 调用方法
     * @param paramForcastInputParamNew 前端给的参数
     * @return Flood表的临时路径
     * @throws IOException
     * @throws ParseException
     * @throws InvalidFormatException
     */
    public static TemporaryXlsx getFloodList(ForcastInputParamNew paramForcastInputParamNew) throws IOException, ParseException, InvalidFormatException {
        paramForcastInputParamNew = emptyProcessing(paramForcastInputParamNew);//异常值处理
        ForcastInputParam param = new ForcastInputParam();
        TemporaryXlsx temporaryXlsx ;
       //模型类型
        param.setIsRealtime(true);
        if (paramForcastInputParamNew.getModelType()==3){
            param.setIsShortForecast(true);
        }else {
            param.setIsShortForecast(false);
        }
        //预报时间
        Date date=  paramForcastInputParamNew.getPredictionTime();
        param.setPreStartTime(date);
        //时段
        if (paramForcastInputParamNew.getPeriodTimeType()==1){
            param.setPeriod("月");
        } else if (paramForcastInputParamNew.getPeriodTimeType()==2) {
            param.setPeriod("旬");
        } else if (paramForcastInputParamNew.getPeriodTimeType()==3) {
            param.setPeriod("日");
        } else if (paramForcastInputParamNew.getPeriodTimeType()==4) {
            param.setPeriod("日");
        }
        //预报长度
        int l = paramForcastInputParamNew.getPeriodTimeStep();
        param.setPeriodStepSize(l);
        int n = paramForcastInputParamNew.getPeriodTimeNum();
        param.setPeriodStepNumber(n);
        //数据输入
        //区间
        Object[][] Flood_qj;
        List<List<PredictInputData>> QJDATA;
        QJDATA=OneStationList(paramForcastInputParamNew,"楼头区间");
        Flood_qj = getOneStationFlood(QJDATA,param,"楼头区间");
        //三号桥
        List<List<PredictInputData>> SHQDATA;
        SHQDATA=OneStationList(paramForcastInputParamNew,"三号桥");
        Object[][] Flood_Three;
        Flood_Three = getOneStationFlood(SHQDATA,param,"三号桥");
        //楼庄子
        List<List<PredictInputData>> LZZDATA;
        LZZDATA=OneStationList(paramForcastInputParamNew,"楼庄子");
        Object[][] Flood_Lzz;
        Flood_Lzz = getOneStationFlood(LZZDATA,param,"楼庄子");

        //整合
        List<Object[][]> floodList = new ArrayList<>();
        floodList.add(Flood_Three);
        floodList.add(Flood_Lzz);
        floodList.add(Flood_qj);
        Object[][] Flood = AddObject(floodList);
        //返回文件路径
        temporaryXlsx = ObjectToXlsx(Flood);
        return temporaryXlsx;

    }
    public static Object[][] getOneStationFlood(List<List<PredictInputData>> Data,ForcastInputParam param,String stationName)throws IOException, InvalidFormatException, ParseException {
        param.setModel("Elman神经网络");
        param.setLocation(stationName);
        String Option = stationName + param.getPeriod();
        Object[][] historyInput = ExcelTool.readExcel("D:\\tth_system\\end\\file\\头屯河历史数据.xlsx", Option);
        //数据驱动模型输入
        List<PredictInputData> machineData = Data.get(0);
        //数据驱动模型数据输入尺度转换
        List<PredictInputData> re = DataUtils.ChangeDate(machineData, param.getPeriod());
        Object[][] machineInputData = new Object[re.size()][4];
        for (int i = 0; i < re.size(); i++) {
            machineInputData[i][0] = re.get(i).getDates();
            machineInputData[i][1] = re.get(i).getFlow();
            machineInputData[i][2] = re.get(i).getTemperature();
            machineInputData[i][3] = re.get(i).getRainfall();
        }
        List<TemporaryXlsx> result;
        //判断是否为实时预报
        List<Object[][]> forcastResultList;
        //判断是否为短期预报，是则使用物理模型
        if (param.getIsShortForecast()) {
            forcastResultList = shortTimeForcast(historyInput, machineInputData, Data, param);
        }
        //机器模型中长期预报
        else {
            MachineForcast machineForcast = new MachineForcast();
            Object[][] Input = dataIntegration(historyInput, machineInputData);
            //划分丰水期枯水期
            Object[][] longForecastInput = DataUtils.SelectDate(Input, param.getPreStartTime());
            //训练模型获得参数以及其储存路径
            MachineModel train = new MachineModel();
            result = train.ModelTrain(longForecastInput, param);
            param.setXlsx(result);
            //中长期预报预报
            forcastResultList = machineForcast.Forcast(longForecastInput, param);
        }
        Object[][] forcastResult = forcastResultList.get(0);
        return forcastResult;
    }

    /**
     * 根据站点名称获得相应的List<List<PredictInputData>> 数据
     * @param paramNew
     * @param StationName
     * @return
     */
    public static  List<List<PredictInputData>> OneStationList(ForcastInputParamNew paramNew, String StationName){
        List<List<PredictInputData>> result = new ArrayList<>();
        if (StationName.equals("楼头区间")){
            //楼庄子出库日径流
            List<PredictInputData> LZZIN = DataUtils.lzzDataConversion(paramNew.getLzzHydrologyParam()).get(1);
            List<PredictInputData> QJ = Scaling(LZZIN);
            QJ =AddRAndT(QJ, paramNew);
            result.add(QJ);
            //获得上游雨量站的温度
            List<List<PredictInputData>> integration = LzzRainIntegration(paramNew);
            List<PredictInputData> Temperature = integration.get(0);
            //添加到区间的数据中
            List<List<PredictInputData>> QJRain = IrrigateRainIntegration(paramNew);
            for (int i = 0; i < QJRain.get(0).size(); i++) {
                double T = Temperature.get(i).getTemperature();
                QJRain.get(0).get(i).setTemperature(T);
            }
            result.add(QJRain.get(0));
            result.add(QJRain.get(1));
        }
        if (StationName.equals("三号桥")){
            //三号桥历史径流日尺度
            List<PredictInputData> THQ = DataUtils.lzzDataConversion(paramNew.getLzzHydrologyParam()).get(0);
            THQ = AddRAndT(THQ, paramNew);
            result.add(THQ);
            List<List<PredictInputData>> integration = LzzRainIntegration(paramNew);//整合雨量站数据转为模型所需类型
            result.add(integration.get(0));
            result.add(integration.get(1));
        }
        if (StationName.equals("楼庄子")){
            //楼庄子历史径流日尺度
            List<PredictInputData> LZZ = DataUtils.lzzDataConversion(paramNew.getLzzHydrologyParam()).get(1);
            LZZ = AddRAndT(LZZ, paramNew);
            result.add(LZZ);
            List<List<PredictInputData>> integration = LzzRainIntegration(paramNew);
            result.add(integration.get(0));
            result.add(integration.get(1));
        }
        return result;
    }
    /**
     * 场次洪水预报
     * @param historyInput
     * @param machineInputData
     * @param Data
     * @param param
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     * @throws ParseException
     */
    public static List<Object[][]> shortTimeForcast(Object[][] historyInput,Object[][] machineInputData,List<List<PredictInputData>> Data, ForcastInputParam param) throws IOException, InvalidFormatException, ParseException {
        List<TemporaryXlsx> result;
        List<Object[][]> floodList = new ArrayList<>();
        Date time = param.getPreStartTime();
        int month = getSpecificDate(time).get("月");
        //根据月份判断融雪洪水
        if (month>=5 && month<=7){
            param.setIsSnowMeltModel(true);
            Object[][] Input=dataIntegration(historyInput,machineInputData);
            //划分历年融雪数据
            Object[][] snowMeltInput= DataUtils.snowMeltDate(Input);
            //训练模型获得参数以及其储存路径
            SnowMeltModel model = new SnowMeltModel();
            result = model.SnowTrain(snowMeltInput, param);
            param.setXlsx(result);
            //中长期预报预报融雪效果
            Object[][] snowFlood=model.Forcast(snowMeltInput, param);
            //陕北模型预报
            PhysicalForcast physicalForcast = new PhysicalForcast();
            Object[][] peakFlood = physicalForcast.getphysicalresult(param, Data, snowFlood);
            floodList.add(peakFlood);
        }
        //非融雪洪水
        else {
            param.setIsSnowMeltModel(false);
            PhysicalForcast physicalForcast = new PhysicalForcast();
            Object[][] snowFlood = new Object[0][];
            Object[][] peakFlood = physicalForcast.getphysicalresult(param, Data, snowFlood);
            floodList.add(peakFlood);
        }
        return floodList;
    }

    /**
     * 历史数据与前期数据的整合
     * @param historyInput
     * @param preliminaryData
     * @return
     */
    public static Object[][] dataIntegration(Object[][] historyInput ,Object[][] preliminaryData){
        Object[][] Input=new Object[historyInput.length + preliminaryData.length][historyInput[0].length];
        for (int i = 0; i < historyInput.length; i++) {
            for (int j = 0; j < historyInput[0].length; j++) {
                Input[i][j]=historyInput[i][j];
            }
        }
        for (int i = historyInput.length; i < historyInput.length + preliminaryData.length; i++) {
            for (int j = 0; j < historyInput[0].length; j++) {
                if(preliminaryData[i - historyInput.length][j] == null){
                    if (i==historyInput.length){
                        preliminaryData[i - historyInput.length][j]=historyInput[i-1][j];
                    }else {
                        preliminaryData[i - historyInput.length][j]=preliminaryData[i - historyInput.length-1][j];
                    }
                }
                Input[i][j]=preliminaryData[i - historyInput.length][j];
            }
        }
        return Input;
    }

public static  ForcastInputParamNew objectToList ( ForcastInputParamNew input) throws IOException {
    ForcastInputParamNew result = new ForcastInputParamNew();
    result.setModelType(input.getModelType());
    result.setPredictionTime(input.getPredictionTime());
    result.setPeriodTimeStep(input.getPeriodTimeStep());
    result.setPeriodTimeNum(input.getPeriodTimeNum());
    result.setPeriodTimeType(input.getPeriodTimeType());
    LzzHydrologyParam lzzParam = new LzzHydrologyParam();
    IrrigatedHydrologyParam qjParam = new IrrigatedHydrologyParam();
    //楼庄子雨量站List
    Object[][] lzzRainObject = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\雨量站.xlsx","LZZ_RAINFALL_STATION_2023121317");
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
    Object[][] lzzFlowObject = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\楼庄子库水位站.xlsx","LZZ_GAUGING_STATION_20231213173");
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
    result.setLzzHydrologyParam(lzzParam);

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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = dateFormat.format(date);
            xqz.setMonitorTime(dateString);
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
            tjyd.setMonitorTime(dateString);
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
            tthR.setMonitorTime(dateString);
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
            tthF.setMonitorTime(dateString);
            tthF.setYqRainFallOne((Double) qjObject[i][17]);
            tthFList.add(tthF);//2
        }
        qjParam.setTthInput(tthFList);
        qjParam.setTthGaugingStation(tthRList);
        qjParam.setTjydGaugingStation(tjydList);
        qjParam.setXqzGaugingStation(xqzList);
    }
    result.setIrrigatedHydrologyParam(qjParam);
    return result;
}
}
