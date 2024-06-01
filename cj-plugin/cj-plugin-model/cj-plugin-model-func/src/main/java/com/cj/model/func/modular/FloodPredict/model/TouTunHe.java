package com.cj.model.func.modular.FloodPredict.model;


import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.model.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.model.function.MachineModel;
import com.cj.model.func.modular.FloodPredict.model.function.PhysicalForecast;
import com.cj.model.func.modular.FloodPredict.model.function.SimulatedRunoff;
import com.cj.model.func.modular.FloodPredict.model.function.SnowMeltModel;
import com.cj.model.func.modular.FloodPredict.utils.*;
import com.cj.model.func.modular.entity.Flood;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
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
        if (param.getIsSimulation()){
            param.setLocation("楼庄子");
            Flood_Lzz = new SimulatedRunoff().simulation(param);
            param.setLocation("3号桥");
            Flood_Three = new SimulatedRunoff().simulation(param);
            param.setLocation("楼头区间");
            Flood_qj = new SimulatedRunoff().simulation(param);
        }else {
            //数据输入
            Map<String, List<Map<String,List<PredictInputData>>>> stationsData = getOneStationDataList(forecastParam);
            //楼庄子
            List<Map<String,List<PredictInputData>>> LZZDATA;
            LZZDATA = stationsData.get("楼庄子");
            Flood_Lzz = getOneStationFlood(LZZDATA, param, "楼庄子");
            //三号桥
            List<Map<String,List<PredictInputData>>> SHQDATA;
            SHQDATA = stationsData.get("3号桥");
            Flood_Three = getOneStationFlood(SHQDATA, param, "3号桥");
            //区间
            List<Map<String,List<PredictInputData>>> QJDATA;
            QJDATA = stationsData.get("楼头区间");
            Flood_qj = getOneStationFlood(QJDATA, param, "楼头区间");
        }
        //头屯河入库
        List<Flood> Flood_Tth = getTTH(Flood_Lzz, Flood_qj);
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
     *
     * @param paramNew
     * @return 三个站点的日尺度历史径流，小时尺度雨量和日尺度雨量
     */
    @SneakyThrows
    public Map<String, List<Map<String,List<PredictInputData>>>> getOneStationDataList(ForecastInputParamNew paramNew){
        Map<String, List<Map<String,List<PredictInputData>>>> threeResults = new HashMap<>();

        List<PredictInputData> flowData = paramNew.getInflowRunoffs();//从A3表获取楼庄子和3号桥的日均流量
        List<Map<String,List<PredictInputData>>> integration = ((paramNew.getModelType() == 3) ? du.lzzRainIntegration(paramNew) : new ArrayList<>());//整合雨量站数据转为模型所需类型
        List<PredictInputData> RAT = du.getRAndT(paramNew);//获得相应的温度和降水

        List<Map<String,List<PredictInputData>>> QJResult = new ArrayList<>();
        List<Map<String,List<PredictInputData>>> LZZResult = new ArrayList<>();
        //三号桥历史径流日尺度
        List<PredictInputData> LZZ = new ArrayList<>();//从A3中获取的
        List<PredictInputData> QJ = new ArrayList<>();//从A3中获取的
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date dataDate = sdf.parse("2024-06-01 00:00:00");

        if (paramNew.getPredictionTime().before(InputUtils.historyDate)) {
            Object[][] lzzData = InputUtils.historyData.get("楼庄子日");
            Object[][] tthData = InputUtils.historyData.get("楼头区间日");
            Date startTime = paramNew.getDataStartTime();
            Date endTime = paramNew.getPredictionTime();
            for (int i = 0; i < lzzData.length; i++) {
                PredictInputData predictInputData = new PredictInputData();
                if (((Date) lzzData[i][0]).before(endTime) && ((Date) lzzData[i][0]).after(startTime)) {
                    predictInputData.setLocation("楼庄子");
                    predictInputData.setDates(((Date) lzzData[i][0]));
                    predictInputData.setFlow((double) lzzData[i][1]);
                    predictInputData.setTemperature((double) lzzData[i][2]);
                    LZZ.add(predictInputData);
                }
            }
            for (int i = 0; i < tthData.length; i++) {
                PredictInputData predictInputData = new PredictInputData();
                if (((Date) tthData[i][0]).before(endTime) && ((Date) tthData[i][0]).after(startTime)) {
                    predictInputData.setLocation("楼头区间");
                    predictInputData.setDates(((Date) tthData[i][0]));
                    predictInputData.setFlow((double) tthData[i][1]);
                    predictInputData.setTemperature((double) tthData[i][2]);
                    QJ.add(predictInputData);
                }
            }
        } else {//本地文件未能记录该数据，从A3表中读取
            for (int i = 0; i < flowData.size(); i++) {
                if (flowData.get(i).getLocation().equals("楼庄子")) {
                    if (flowData.get(i).getFlow() != null) {
                        LZZ.add(flowData.get(i));
                    }
                } else if (flowData.get(i).getLocation().equals("头屯河")) {
                    if (flowData.get(i).getFlow() != null) {
                        QJ.add(flowData.get(i));
                    }
                }
            }
            for (int i = 0; i < QJ.size(); i++) {
                for (int j = 0; j < LZZ.size(); j++) {
                    if (tu.DateCompare(QJ.get(i).getDates(), LZZ.get(j).getDates(), "日")) {
                        double f = (QJ.get(i).getFlow() - LZZ.get(j).getFlow() >= 0 ? QJ.get(i).getFlow() - LZZ.get(j).getFlow() : 0);
                        QJ.get(i).setFlow(f);
                    }
                }
            }
        }

        //楼庄子历史径流日尺度
        LZZ = du.addRAndT(LZZ, RAT);
        Map<String,List<PredictInputData>> LZZFlow = new HashMap<>();
        LZZFlow.put("流量",LZZ);
        LZZResult.add(LZZFlow);
        if (paramNew.getModelType() == 3) {
            LZZResult.add(integration.get(0));//前期雨量
            LZZResult.add(integration.get(1));//预报降雨
        }
        threeResults.put("楼庄子", LZZResult);
        threeResults.put("3号桥", LZZResult);

        QJ = du.addRAndT(QJ, RAT);
        Map<String,List<PredictInputData>> QJFlow = new HashMap<>();
        QJFlow.put("流量",QJ);
        QJResult.add(QJFlow);
        if (paramNew.getModelType() == 3) {
            List<Map<String,List<PredictInputData>>> QJRain = du.irrigateRainIntegration(paramNew);
            QJResult.add(QJRain.get(0));
            QJResult.add(QJRain.get(1));
        }
        threeResults.put("楼头区间", QJResult);

        return threeResults;
    }

    /**
     * 获取单个站点的洪水数据
     *
     * @param Data
     * @param param
     * @param stationName
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     * @throws ParseException
     */
    public List<Flood> getOneStationFlood(List<Map<String,List<PredictInputData>>> Data, ForecastInputParam param, String stationName) {
        param.setLocation(stationName);
        String Option = stationName + param.getPeriod();
        Object[][] historyInput = InputUtils.historyData.get(Option);
        //数据驱动模型输入
        List<PredictInputData> machineData = Data.get(0).get("流量");
        //数据驱动模型数据输入尺度转换
        List<PredictInputData> re = tu.ChangeDate(machineData, param.getPeriod());
        Object[][] machineInputData = mdu.listToObject(re, stationName);

        //判断是否为短期预报，是则使用物理模型
        if (param.getIsShortForecast()) {
            Date time = param.getPreStartTime();
            int before = (param.getLocation().equals("楼头区间")) ? 3 : 5;
            int after = (param.getLocation().equals("楼头区间")) ? 3 : 7;
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
                PhysicalForecast physicalForecast = new PhysicalForecast();
                List<Flood> result = physicalForecast.getShortResult(param, Data, snowFlood);
                return result;
            }
            //非融雪洪水
            else {
                param.setIsSnowMeltModel(false);
                PhysicalForecast physicalForecast = new PhysicalForecast();
                snowFlood = new Object[0][];
                List<Flood> result = physicalForecast.getShortResult(param, Data, snowFlood);
                return result;
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
     *
     * @param Lzz
     * @param qj
     * @return
     */
    public List<Flood> getTTH(List<Flood> Lzz, List<Flood> qj) {
        List<Flood> result = new ArrayList<>();
        int timeLength = Integer.parseInt(Lzz.get(0).getScale());
        //头屯河入库
        Object[][] tthIn = new Object[Lzz.size()][2];
        double qjFlood = 0.0;
        double lzzFlood = 0.0;
        int late = Lzz.get(0).getPreQ() > 60 ? 2 : Lzz.get(0).getPreQ() > 20 ? 3 : 4;
        for (int i = 0; i < Lzz.size(); i++) {
            tthIn[i][0] = Lzz.get(i).getTime();
            if (Integer.parseInt(Lzz.get(0).getScale()) < 3600 * 24) {
                tthIn[i][1] = (i - late > 0 ? Lzz.get(i - late).getOutQ() + qj.get(i - late).getPreQ() : Lzz.get(0).getOutQ() + qj.get(0).getPreQ());
            } else {
                tthIn[i][1] = Lzz.get(i).getOutQ() + qj.get(i).getPreQ();
            }
            lzzFlood += Lzz.get(i).getOutQ();
            qjFlood += qj.get(i).getPreQ();
        }
        PhysicalForecast physicalForecast = new PhysicalForecast();
        List<Object[][]> tthInformation = physicalForecast.getFloodInformation(tthIn);
        String level = physicalForecast.getFloodLevel(tthIn, "头屯河");
        Object[][] tthIndex = tthInformation.get(0);
        Object[][] floodNature = tthInformation.get(1);
        StringBuilder tthRain = new StringBuilder();
//        List<Option> tthInList = TTH.Calculate("../file/Basin.json",tthIn, timeLength);
//        List<Option> tthInList = TTH.Calculate("D:\\tth_system\\end\\file/Basin.json",tthIn, timeLength);
        //洪水来源和洪水组成
        if (timeLength < 3600 * 24) {
            String data = Lzz.get(0).getQCause() + "," + qj.get(0).getQCause();
            String[] pairs = data.split(",");
            double sum = 0.0;
            for (String pair : pairs) {
                String[] splitPair = pair.split(":");
                String area = splitPair[0];
                double value = Double.parseDouble(splitPair[1]);
                if (tthRain.length() == 0) {
                    tthRain = new StringBuilder(area + ":" + Math.round((float) value * lzzFlood / (qjFlood + lzzFlood) * 100) / 100.0);
                    sum += Math.round((float) value * lzzFlood / (qjFlood + lzzFlood) * 100) / 100.0;
                } else {
                    if (area.equals("东南沟地区") || area.equals("3号桥地区") || area.equals("制材厂地区")) {
                        tthRain.append(",").append(area).append(":").append(Math.round((float) value * lzzFlood / (qjFlood + lzzFlood) * 100) / 100.0);
                        sum += Math.round((float) value * lzzFlood / (qjFlood + lzzFlood) * 100) / 100.0;
                    } else {
                        if (!area.equals("头屯河入库")) {
                            tthRain.append(",").append(area).append(":").append(Math.round((float) value * qjFlood / (qjFlood + lzzFlood) * 100) / 100.0);
                            sum += Math.round((float) value * qjFlood / (qjFlood + lzzFlood) * 100) / 100.0;
                        } else {
                            tthRain.append(",").append(area).append(":").append(Math.round((float) (1 - sum) * 100) / 100.0);
                        }
                    }
                }
            }
        }
        int waterLevel = tu.getSpecificDate(Lzz.get(0).getTime()).get("月") == 7 ? 987 : 988;
        //连续列的赋值
        for (int i = 0; i < Lzz.size(); i++) {
            Flood tth = new Flood();
            tth.setLocation("头屯河");//断面位置
            tth.setScale(String.valueOf(timeLength));//尺度
            tth.setPeakIndex((Integer) tthIndex[i][0]);//洪号
            tth.setTime((Date) tthIn[i][0]);//时间
            tth.setPreQ(Math.round((double) tthIn[i][1] * 100.0) / 100.0);//预报流量
            tth.setWaterLevel(900.0);//相应水位
            tth.setPeakFlood((double) floodNature[2][1]);//洪峰
            tth.setPeakTime((Date) floodNature[3][1]);//峰现时间
            tth.setPeakDuration((String) floodNature[1][1]);//洪峰持续时间
            tth.setFloodVolume(timeLength < 3600 * 24 ? (double) floodNature[0][1] : Math.round((double) tthIn[i][1] * 100.0) / 100.0 * timeLength / 10000);//洪量
            tth.setQCause(tthRain.toString());//洪水来源
            tth.setQComposition("区间来水:" + Math.round((float) qjFlood / (qjFlood + lzzFlood) * 100) / 100.0 + "," + "楼庄子出库:" + Math.round((float) lzzFlood / (qjFlood + lzzFlood) * 100) / 100.0);//洪水组成
            tth.setFloodLevel(level);//洪水等级
            tth.setWarningTime(((double) tthIn[i][1] > 60.0) ? 1 : 0);//超警时段
            tth.setOutQ((double) tthIn[i][1]);//出库流量
            tth.setRainProcess(Math.round((Lzz.get(i).getRainProcess() * 0.7514 + qj.get(i).getRainProcess() * 0.2486) * 100) / 100.0);//雨情
            result.add(tth);
        }
        return result;
    }

}
