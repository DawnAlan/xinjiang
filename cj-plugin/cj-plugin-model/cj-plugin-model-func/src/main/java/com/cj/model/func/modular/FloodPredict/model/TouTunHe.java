package com.cj.model.func.modular.FloodPredict.model;

import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.model.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.model.function.*;
import com.cj.model.func.modular.FloodPredict.utils.*;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqCurve;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import com.cj.model.func.modular.entity.Flood;
import lombok.SneakyThrows;


import java.text.SimpleDateFormat;
import java.util.*;

import static com.cj.model.func.modular.FloodPredict.utils.Tools.resultToXlsx;


public class TouTunHe {
    DataUtils du = new DataUtils();
    MachineDataUtils mdu = new MachineDataUtils();
    TimeUtils tu = new TimeUtils();
    String paramPath;
    String maxPath;

    public TemporaryXlsx getFloodList(ForecastInputParamNew forecastParam) {
        List<Flood> Flood_Lzz;List<Flood> Flood_Three;List<Flood> Flood_qj;
        //异常值处理
        du.emptyProcessing(forecastParam);
        //判断是否添加新数据
        String s = mdu.intervalData(forecastParam);
        //参数转化
        ForecastInputParam param = mdu.paramConvert(forecastParam);
        //判断是否为模拟径流
        if (param.getIsReferenceWater()){
            param.setLocation("楼庄子");
            Flood_Lzz = new SimulatedRunoff().simulation(param);
            param.setLocation("3号桥");
            Flood_Three = new SimulatedRunoff().simulation(param);
            param.setLocation("楼头区间");
            Flood_qj = new SimulatedRunoff().simulation(param);
        }else {
            //数据输入
            Map<String, InputDataSet> stationsData = getOneStationDataList(forecastParam);
            //楼庄子
            InputDataSet LZZDATA = stationsData.get("楼庄子");
            Flood_Lzz = getOneStationFlood(LZZDATA, param, "楼庄子");
            //三号桥
            InputDataSet SHQDATA = stationsData.get("3号桥");
            Flood_Three = getOneStationFlood(SHQDATA, param, "3号桥");
            //区间
            InputDataSet QJDATA = stationsData.get("楼头区间");
            Flood_qj = getOneStationFlood(QJDATA, param, "楼头区间");
        }
        //头屯河入库
        List<Flood> Flood_Tth = getTTH(param,forecastParam.getInflowRunoffs(),Flood_Lzz, Flood_qj);
        List<Flood> result = new ArrayList<>();
        result.addAll(Flood_Three);
        result.addAll(Flood_Lzz);
        result.addAll(Flood_qj);
        result.addAll(Flood_Tth);
        TemporaryXlsx temporaryXlsx;
        //返回文件路径
        temporaryXlsx = resultToXlsx(result);
        temporaryXlsx.setUpdateFilePath(s);
        if (param.getIsTrain()){
            temporaryXlsx.setUpdateMaxPath(maxPath);
            temporaryXlsx.setUpdateParamPath(paramPath);
        }
        return temporaryXlsx;
    }

    /**
     * A3表数据修改
     * 添加历史模拟时的历史降水数据
     * 根据站点名称获得相应的List<List<PredictInputData>> 数据
     * @return 三个站点的日尺度历史径流，小时尺度雨量和日尺度雨量
     */
    @SneakyThrows
    public Map<String, InputDataSet> getOneStationDataList(ForecastInputParamNew paramNew){
        Map<String, InputDataSet> threeResults = new HashMap<>();
        //日尺度径流数据（从云端文件或者A3表中获取)
        List<PredictInputData> LZZ = new ArrayList<>();//从A3中获取的
        List<PredictInputData> QJ = new ArrayList<>();//从A3中获取的
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean isrecentPredict = (paramNew.getPredictionTime().after(sdf.parse("2024-05-01 00:00:00")) && paramNew.getModelType() == 3);
        if (paramNew.getPredictionTime().before(InputUtils.historyDate)&&!isrecentPredict) {
            Object[][] lzzData = InputUtils.historyData.get("楼庄子日");
            Object[][] tthData = InputUtils.historyData.get("楼头区间日");
            Date startTime = paramNew.getDataStartTime();
            Date endTime = paramNew.getPredictionTime();
            for (Object[] lzzDatum : lzzData) {
                PredictInputData predictInputData = new PredictInputData();
                if (((Date) lzzDatum[0]).before(endTime) && ((Date) lzzDatum[0]).after(startTime)) {
                    predictInputData.setLocation("楼庄子");
                    predictInputData.setDates(((Date) lzzDatum[0]));
                    predictInputData.setFlow((double) lzzDatum[1]);
                    predictInputData.setTemperature((double) lzzDatum[2]);
                    LZZ.add(predictInputData);
                }
            }
            for (Object[] tthDatum : tthData) {
                PredictInputData predictInputData = new PredictInputData();
                if (((Date) tthDatum[0]).before(endTime) && ((Date) tthDatum[0]).after(startTime)) {
                    predictInputData.setLocation("楼头区间");
                    predictInputData.setDates(((Date) tthDatum[0]));
                    predictInputData.setFlow((double) tthDatum[1]);
                    predictInputData.setTemperature((double) tthDatum[2]);
                    QJ.add(predictInputData);
                }
            }
        }
        else {//本地文件未能记录该数据，从A3表中读取
            List<PredictInputData> flowData = new ArrayList<>();
            for (PredictInputData data : paramNew.getInflowRunoffs()) {
                flowData.add(data.clone());
            }
            for (PredictInputData flowDatum : flowData) {
                if (flowDatum.getLocation().equals("楼庄子")) {
                    if (flowDatum.getFlow() != null&&flowDatum.getDataType().equals("flow")&&flowDatum.getFlow()<500.0) {
                        LZZ.add(flowDatum);
                    }
                    if (flowDatum.getFlow() != null&&flowDatum.getDataType().equals("waterLevel")&&flowDatum.getFlow()>1000.0) {
                        InputUtils.lzzWaterLevel = flowDatum.getFlow();
                    }
                } else if (flowDatum.getLocation().equals("头屯河")) {
                    if (flowDatum.getFlow() != null&&flowDatum.getDataType().equals("flow")&&flowDatum.getFlow()<500.0) {
                        QJ.add(flowDatum);
                    }
                    if (flowDatum.getFlow() != null&&flowDatum.getDataType().equals("waterLevel")&&flowDatum.getFlow()>1000.0) {
                        InputUtils.tthWaterLevel = flowDatum.getFlow();
                    }
                }
            }
            for (PredictInputData predictInputData : QJ) {
                boolean hasValidFlow = false; // 标志变量
                for (PredictInputData lzz : LZZ) {
                    if (tu.DateCompare(predictInputData.getDates(), lzz.getDates(), "小时")) {
                        double f = (predictInputData.getFlow() - lzz.getFlow() >= 0 ? predictInputData.getFlow() - lzz.getFlow() : 0);
                        predictInputData.setFlow(f);
                        hasValidFlow = true; // 找到匹配，设置标志
                    }
                }
                // 如果没有有效的匹配，Flow 不变
                if (!hasValidFlow) {
                    predictInputData.setFlow(predictInputData.getFlow()); // 保持原值
                }
            }
        }
        //返回所需数据类型数据
        InputDataSet QJResult = new InputDataSet();
        InputDataSet LZZResult = new InputDataSet();
        if (paramNew.getModelType() != 1) {
            List<RainFallDto> RAT = du.getRAndT(paramNew);//获得相应的温度和降水
            LZZ = du.addRAndT(LZZ, RAT);
            QJ = du.addRAndT(QJ, RAT);
        }
        LZZResult.setFlowData(LZZ);
        QJResult.setFlowData(QJ);
        if (paramNew.getModelType() == 3) {
            InputDataSet integration = du.rainIntegration(paramNew);//整合雨量站数据转为模型所需类型
            LZZResult.setRainHourData(integration.getRainHourData());//预报降雨
            LZZResult.setRainDayData(integration.getRainDayData());//前期雨量
            QJResult.setRainHourData(integration.getRainHourData());
            QJResult.setRainDayData(integration.getRainDayData());
        }
        threeResults.put("楼庄子", LZZResult);
        threeResults.put("3号桥", LZZResult);
        threeResults.put("楼头区间", QJResult);
        return threeResults;
    }

    /**
     * 获取单个站点的洪水数据
     */
    public List<Flood> getOneStationFlood(InputDataSet Data, ForecastInputParam param, String stationName) {
        param.setLocation(stationName);
        String Option = stationName + param.getPeriod();
        Hydrology hydrology = new Hydrology();
        for (Hydrology station: param.getFloodBasin().getHydrologies()){
            if (station.getStationName().equals(stationName)){
                hydrology = station;
            }
        }
        Object[][] historyInput = InputUtils.historyData.get(Option);
        //数据驱动模型输入
        List<PredictInputData> machineData = Data.getFlowData();
        //数据驱动模型数据输入尺度转换
        List<PredictInputData> re = tu.ChangeDate(machineData, param.getPeriod());
        Object[][] machineInputData = mdu.listToObject(re, stationName);
        //判断是否为短期预报，是则使用物理模型
        if (param.getIsShortForecast()) {
            Date time = param.getPreStartTime();
            int before = hydrology.getSnowMonth()[0];
            int after = hydrology.getSnowMonth()[1];
            int month = tu.getSpecificDate(time).get("月");
            int l = param.getPeriodStepNumber() / 24 + 1;
            Object[][] snowFlood = new Object[l][2];
            //根据月份判断融雪洪水
            if (month >= before && month <= after) {
                param.setIsSnowMeltModel(true);
                for (int i = 0; i < l; i++) {
                    Date date = param.getPreStartTime();
                    Object[][] machineInput = mdu.getDataInput(historyInput, machineInputData, param);
                    //划分历年融雪数据
                    Object[][] snowMeltInput = mdu.snowMeltDate(machineInput, param.getLocation());
                    if (param.getIsAverage()){
                        snowMeltInput = mdu.inputProcessing(snowMeltInput, param.getLocation());//获得距平值
                    }
                    //训练模型获得参数以及其储存路径
                    SnowMeltModel model = new SnowMeltModel();
                    //是否训练模型
                    if (param.getIsTrain()) {
                        model.snowTrain(snowMeltInput, param);
                    }
                    //中长期预报预报融雪效果
                    List<Flood> snowList  = model.snowForecast(snowMeltInput, param);
                    snowFlood[i][0] = snowList.get(0).getTime();
                    snowFlood[i][1] = snowList.get(0).getPreQ();
                    date = tu.addCalendar(date, "日", 1);
                    param.setPreStartTime(date);
                }
                //陕北模型预报
                param.setPreStartTime(time);
                SubBasinForecast subBasinForecast = new SubBasinForecast();
                return subBasinForecast.getShortResult(param, Data, snowFlood);
            }
            //非融雪洪水
            else {
                param.setIsSnowMeltModel(false);
                SubBasinForecast subBasinForecast = new SubBasinForecast();
                snowFlood = new Object[0][];
                return subBasinForecast.getShortResult(param, Data, snowFlood);
            }
        }
        //机器模型中长期预报
        else {
            List<Flood> result;
            Object[][] machineInput = mdu.getDataInput(historyInput, machineInputData, param);
            if (param.getIsAverage()){
                machineInput = mdu.inputProcessing(machineInput, param.getLocation());//获得距平值
            }
            if (param.getPeriod().equals("日")){
                param.setIsSnowMeltModel(false);
                SnowMeltModel model = new SnowMeltModel();
                machineInput = mdu.SelectDate(machineInput, param.getPreStartTime());
                if (param.getIsTrain()) {
                    model.snowTrain(machineInput, param);
                }
                //中长期预报预报
                result = model.snowForecast(machineInput, param);
            }else {
                MachineModel model = new MachineModel();
                if (param.getIsTrain()) {
                    ModelSaveEntity entity = model.modelTrain(machineInput, param);
                    paramPath = entity.getTempXlsx().getUpdateParamPath();
                    maxPath = entity.getTempXlsx().getUpdateMaxPath();
                }
                //中长期预报预报
                result = model.machineForecast(machineInput, param);
            }
            return result;
        }
    }

    /**
     * 返回楼庄子出库和头屯河入库
     */
    public List<Flood> getTTH(ForecastInputParam param,List<PredictInputData> tthInflow,List<Flood> Lzz, List<Flood> qj) {
        List<Flood> result = new ArrayList<>();
        List<PredictInputData> QJ = new ArrayList<>();
        int timeLength = Integer.parseInt(Lzz.get(0).getScale());
        Object[][] tthIn;
        double lzzIn = 0.0;
        double qjFlood = 0.0;
        double lzzFlood = 0.0;
        int late = 2;
        if (param.getIsShortForecast()){
            int hours;
            if (!param.getIsReferenceWater()){
                for (PredictInputData flowDatum : tthInflow) {
                    if (flowDatum.getFlow() != null&&flowDatum.getDataType().equals("flow")&&flowDatum.getLocation().equals("头屯河")) {
                        QJ.add(flowDatum);
                    }
                }
                param.setIsSnowMeltModel(false);
                double[] inFlow = new SubBasinForecast().flowDistribution(param,new Object[0][0],QJ,new ArrayList<>());
                hours = InputUtils.beforeHours;
                tthIn= new Object[Lzz.size() - hours][2];
                if (Lzz.get(0).getFloodLevel().equals("一年一遇")){
                    for (int i = hours; i < Lzz.size(); i++) {
                        tthIn[i-hours][0] = Lzz.get(i).getTime();
                        tthIn[i-hours][1] = (inFlow[i-hours]+qj.get(i).getPreQ());
                        lzzIn += Lzz.get(i).getPreQ();
                        lzzFlood += inFlow[i-hours];
                        qjFlood += qj.get(i).getPreQ();
                    }
                }else {
                    for (int i = hours; i < Lzz.size(); i++) {
                        tthIn[i-hours][0] = Lzz.get(i).getTime();
                        tthIn[i-hours][1] = (i - late > hours ? Lzz.get(i - late).getOutQ() + qj.get(i - late).getPreQ() : Lzz.get(hours).getOutQ() + qj.get(hours).getPreQ());
                        lzzIn += Lzz.get(i).getPreQ();
                        lzzFlood += Lzz.get(i).getOutQ();
                        qjFlood += qj.get(i).getPreQ();
                    }
                }
            }else {
                hours = 0;
                tthIn= new Object[Lzz.size()][2];
                for (int i = 0; i < Lzz.size(); i++) {
                    tthIn[i][0] = Lzz.get(i).getTime();
                    tthIn[i][1] = (i - late > 0 ? Lzz.get(i - late).getOutQ() + qj.get(i - late).getPreQ() : Lzz.get(0).getOutQ() + qj.get(0).getPreQ());
                    lzzIn += Lzz.get(i).getPreQ();
                    lzzFlood += Lzz.get(i).getOutQ();
                    qjFlood += qj.get(i).getPreQ();
                }
            }
            //头屯河入库
            SubBasinForecast subBasinForecast = new SubBasinForecast();
            List<Object[][]> tthInformation = subBasinForecast.getFloodInformation(tthIn);
            String level = subBasinForecast.getFloodLevel(tthIn, "头屯河");
            Object[][] tthIndex = tthInformation.get(0);
            Object[][] floodNature = tthInformation.get(1);
            StringBuilder tthRain = new StringBuilder();
            ReqCurve reqCurve = new ReqCurve();
            List<Option> tthInList = TTH.Calculate(param.getBasinStr(),tthIn, timeLength,reqCurve,param.getIsReferenceWater());
            Object[][] water_outQ = new Object[tthInList.size()][3];//水位、出库流量、汛限水位
            int waterLevel = tu.getSpecificDate(Lzz.get(0).getTime()).get("月") == 7 ? 987 : 988;
            for (int i = 0; i < tthInList.size(); i++) {
                water_outQ[i][0] = tthInList.get(i).getH1();
                water_outQ[i][1] = tthInList.get(i).getQOut();
                water_outQ[i][2] = (((double) water_outQ[i][0] > waterLevel) ? 1 : 0);
            }
            //洪水来源和洪水组成
            String data = Lzz.get(0).getQCause() + qj.get(0).getQCause();
            String[] pairs = data.split(",");
            for (String pair : pairs) {
                String[] splitPair = pair.split(":");
                String area = splitPair[0];
                double value = Double.parseDouble(splitPair[1]);
                if (area.contains("自动雨量站")) {
                    tthRain.append(area.replaceAll("自动雨量站", "")).append(":").append(Math.round((float) value * lzzIn / (qjFlood + lzzIn) * 100) / 100.0).append(",");
                } else {
                    tthRain.append(area.replaceAll("雨量站", "")).append(":").append(Math.round((float) value * qjFlood  / (qjFlood + lzzIn) * 100) / 100.0).append(",");
                }
            }
            //连续列的赋值
            for (int i = 0; i < hours; i++) {
                Flood tth = new Flood();
                tth.setLocation("头屯河");//断面位置
                tth.setScale(String.valueOf(timeLength));//尺度
                tth.setPeakIndex(0);//洪号
                tth.setTime(Lzz.get(i).getTime());//时间
                tth.setPeakFlood((double) floodNature[2][1]);//洪峰
                tth.setPeakTime((Date) floodNature[3][1]);//峰现时间
                tth.setPeakDuration((String) floodNature[1][1]);//洪峰持续时间
                tth.setFloodVolume((double) floodNature[0][1]);//洪量
                tth.setQCause(tthRain.toString());//洪水来源
                tth.setQComposition("区间来水:" + Math.round((float) qjFlood / (qjFlood + lzzFlood) * 100) / 100.0 + "," + "楼庄子出库:" + Math.round((float) lzzFlood / (qjFlood + lzzFlood) * 100) / 100.0);//洪水组成
                tth.setFloodLevel(level);//洪水等级
                tth.setWarningTime(0);//是否超过汛限水位
                tth.setRainProcess(Math.round((Lzz.get(i).getRainProcess() * 0.7514 + qj.get(i).getRainProcess() * 0.2486) * 100) / 100.0);//雨情
                tth.setConfluenceTime(qj.get(i).getConfluenceTime());//汇流时间
                result.add(tth);
            }
            for (int i = 0; i < Lzz.size()-hours; i++) {
                Flood tth = new Flood();
                tth.setLocation("头屯河");//断面位置
                tth.setScale(String.valueOf(timeLength));//尺度
                tth.setPeakIndex((Integer) tthIndex[i][0]);//洪号
                tth.setTime((Date) tthIn[i][0]);//时间
                tth.setPreQ(Math.round((double) tthIn[i][1] * 100.0) / 100.0);//预报流量
                tth.setWaterLevel((Double) water_outQ[i][0]);//相应水位
                tth.setPeakFlood((double) floodNature[2][1]);//洪峰
                tth.setPeakTime((Date) floodNature[3][1]);//峰现时间
                tth.setPeakDuration((String) floodNature[1][1]);//洪峰持续时间
                tth.setFloodVolume(timeLength < 3600 * 24 ? (double) floodNature[0][1] : Math.round((double) tthIn[i][1] * 100.0) / 100.0 * timeLength / 10000);//洪量
                tth.setQCause(tthRain.toString());//洪水来源
                tth.setQComposition("区间来水:" + Math.round((float) qjFlood / (qjFlood + lzzFlood) * 100) / 100.0 + "," + "楼庄子出库:" + Math.round((float) lzzFlood / (qjFlood + lzzFlood) * 100) / 100.0);//洪水组成
                tth.setFloodLevel(level);//洪水等级
                tth.setWarningTime((Integer) water_outQ[i][2]);//超警时段
                tth.setOutQ((Double) water_outQ[i][1]);//出库流量
                tth.setRainProcess(Lzz.get(i).getRainProcess() * 0.7514 + qj.get(i).getRainProcess() * 0.2486);//雨情
                tth.setConfluenceTime(qj.get(i).getConfluenceTime());//汇流时间
                result.add(tth);
            }
        } else {
            //连续列的赋值
            for (int i = 0; i < Lzz.size(); i++) {
                Flood tth = new Flood();
                int days = mdu.getDays(param.getPeriod(),Lzz.get(i).getTime());
                tth.setLocation("头屯河");//断面位置
                tth.setScale(String.valueOf(timeLength));//尺度
                tth.setPeakIndex(0);//洪号
                tth.setTime(Lzz.get(i).getTime());//时间
                tth.setPreQ(Math.round((Lzz.get(i).getPreQ()+qj.get(i).getPreQ()) * 100.0) / 100.0);//预报流量
                tth.setFloodVolume((double) Math.round(tth.getPreQ() * 3600*24*days / 10000));//洪量
                tth.setQCause("");//洪水来源
                tth.setQComposition("");//洪水组成
                tth.setPeakDuration("");//洪峰持续时间
                tth.setFloodLevel("");//洪水等级
                tth.setWarningTime(0);//是否超过汛限水位
                tth.setRainProcess(0.0);//雨情
                tth.setConfluenceTime("");//汇流时间
                result.add(tth);
            }
        }
        return result;

    }

}
