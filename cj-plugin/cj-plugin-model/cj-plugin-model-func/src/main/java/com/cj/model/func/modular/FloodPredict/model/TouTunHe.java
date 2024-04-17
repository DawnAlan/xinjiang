package com.cj.model.func.modular.FloodPredict.model;


import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.model.function.MachineForecast;
import com.cj.model.func.modular.FloodPredict.model.function.MachineModel;
import com.cj.model.func.modular.FloodPredict.model.function.PhysicalForecast;
import com.cj.model.func.modular.FloodPredict.model.function.SnowMeltModel;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.InputUtils;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import com.cj.model.func.modular.FloodPrevent.model.ModelOfTTH;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.cj.model.func.modular.FloodPredict.utils.Tools.AddObject;
import static com.cj.model.func.modular.FloodPredict.utils.Tools.ObjectToXlsx;


public class TouTunHe {
    DataUtils dataUtils = new DataUtils();

    TimeUtils timeUtils = new TimeUtils();

    InputUtils inputUtils =new InputUtils();

    public TemporaryXlsx getFloodList(ForecastInputParamNew forecastParam)
            throws IOException, ParseException, InvalidFormatException {

        //异常值处理
        forecastParam = dataUtils.emptyProcessing(forecastParam);
        //判断是否添加新数据
        inputUtils.intervalData(forecastParam);
        //参数转化
        ForecastInputParam param = inputUtils.paramConvert(forecastParam);
        //数据输入
        Map<String,List<List<PredictInputData>>>stationsData = getOneStationDataList(forecastParam);
        //楼庄子
        List<List<PredictInputData>> LZZDATA;
        LZZDATA=stationsData.get("楼庄子");
        Object[][] Flood_Lzz;
        Flood_Lzz = getOneStationFlood(LZZDATA,param,"楼庄子");
        //三号桥
        List<List<PredictInputData>> SHQDATA;
        SHQDATA=stationsData.get("3号桥");
        Object[][] Flood_Three;
        Flood_Three = getOneStationFlood(SHQDATA,param,"3号桥");
        //区间
        Object[][] Flood_qj;
        List<List<PredictInputData>> QJDATA;
        QJDATA = stationsData.get("楼头区间");
        Flood_qj = getOneStationFlood(QJDATA,param,"楼头区间");
        //头屯河入库
        List<Object[][]> tthList =getTthInput(Flood_Lzz,Flood_qj,forecastParam);
        Object[][] Flood_tthIn = tthList.get(0);
        //整合3号桥+楼庄子+区间+头屯河入库
        for (int i = 0; i < Flood_tthIn.length; i++) //超过汛限水位
        {
            Flood_Three[i][14] = Flood_Lzz[i][14];
            Flood_qj[i][14] = Flood_tthIn[i][14];
        }
        List<Object[][]> floodList = new ArrayList<>();
        floodList.add(Flood_Three);
        floodList.add(Flood_Lzz);
        floodList.add(Flood_qj);
        floodList.add(Flood_tthIn);
        Object[][] Flood = AddObject(floodList);

        String judgeYear = (String) Flood_Lzz[0][12];
        for (int i = 0; i < Flood.length; i++)//洪水等级
        {
            Flood[i][12]=judgeYear;
        }
        TemporaryXlsx temporaryXlsx;
        //返回文件路径
        temporaryXlsx = ObjectToXlsx(Flood);
        return temporaryXlsx;

    }

    /**
     * A3表数据修改
     * 添加历史模拟时的历史降水数据
     * 根据站点名称获得相应的List<List<PredictInputData>> 数据
     * @param paramNew
     * @return 三个站点的日尺度历史径流，小时尺度雨量和日尺度雨量
     */
    public  Map<String,List<List<PredictInputData>>> getOneStationDataList(ForecastInputParamNew paramNew)
            throws ParseException, IOException {
        Map<String,List<List<PredictInputData>>> threeResults = new HashMap<>();
        //从数据库中获取数据
//        List<List<PredictInputData>> lzzDataConversions = DataUtils.lzzDataConversion(paramNew);//获取楼庄子和3号桥的日均流量
//        List<PredictInputData> LZZ = lzzDataConversions.get(1);//从数据库中获取的

        List<PredictInputData> flowData = paramNew.getInflowRunoffs();//从A3表获取楼庄子和3号桥的日均流量
        List<PredictInputData> RAT = dataUtils.getRAndT(paramNew);//获得相应的温度和降水
        List<List<PredictInputData>> integration = new ArrayList<>();
        if (paramNew.getModelType()==3)//场次洪水
        {
            integration = dataUtils.lzzRainIntegration(paramNew);//整合雨量站数据转为模型所需类型
        }

        List<List<PredictInputData>> THQResult = new ArrayList<>();
        List<List<PredictInputData>> QJResult = new ArrayList<>();
        List<List<PredictInputData>> LZZResult = new ArrayList<>();
        //三号桥历史径流日尺度
        List<PredictInputData> LZZ = new ArrayList<>();//从A3中获取的
        List<PredictInputData> QJ = new ArrayList<>();//从A3中获取的
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dataDate = sdf.parse("2024-01-01 00:00:00");
        if (paramNew.getPredictionTime().before(dataDate)){
            Object[][] lzzData = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx","楼庄子日");
            Object[][] tthData = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx","楼头区间日");
            Date startTime = paramNew.getDataStartTime();
            Date endTime = paramNew.getPredictionTime();
            for (int i = 0; i < lzzData.length; i++) {
                PredictInputData predictInputData = new PredictInputData();
                if (((Date) lzzData[i][0]).before(endTime)&&((Date) lzzData[i][0]).after(startTime)){
                    predictInputData.setLocation("楼庄子");
                    predictInputData.setDates(((Date) lzzData[i][0]));
                    predictInputData.setFlow((double)lzzData[i][1]);
                    predictInputData.setTemperature((double)lzzData[i][2]);
                    LZZ.add(predictInputData);
                }
            }
            for (int i = 0; i < tthData.length; i++) {
                PredictInputData predictInputData = new PredictInputData();
                if (((Date) tthData[i][0]).before(endTime)&&((Date) tthData[i][0]).after(startTime)){
                    predictInputData.setLocation("楼头区间");
                    predictInputData.setDates(((Date) tthData[i][0]));
                    predictInputData.setFlow((double)tthData[i][1]);
                    predictInputData.setTemperature((double)tthData[i][2]);
                    QJ.add(predictInputData);
                }
            }
        }
        else {//本地文件未能记录该数据，从A3表中读取
            for (int i = 0; i < flowData.size(); i++) {
                if (flowData.get(i).getLocation().equals("楼庄子")){
                    if (flowData.get(i).getFlow()!=null){
                        LZZ.add(flowData.get(i));
                    }
                }else {
                    if (flowData.get(i).getFlow()!=null){
                        QJ.add(flowData.get(i));
                    }
                }
            }
        }

        //楼庄子历史径流日尺度
        LZZ = dataUtils.addRAndT(LZZ, RAT);
        LZZResult.add(LZZ);
        if (paramNew.getModelType()==3){
            LZZResult.add(integration.get(0));
            LZZResult.add(integration.get(1));
        }
        threeResults.put("楼庄子",LZZResult);
        threeResults.put("3号桥",LZZResult);

        //头屯河日径流
//        List<PredictInputData> QJ = DataUtils.irrigatedDataConversion(paramNew.getIrrigatedHydrologyParam()).get(0);//从数据库中获取的
        QJ = dataUtils.addRAndT(QJ, RAT);
        QJResult.add(QJ);
        if (paramNew.getModelType()==3)
        {
            //获得上游雨量站的温度
            List<PredictInputData> Temperature = integration.get(0);
            //添加到区间的数据中
            List<List<PredictInputData>> QJRain = dataUtils.irrigateRainIntegration(paramNew);
            for (int i = 0; i < QJRain.get(0).size(); i++) {
                QJRain.get(0).get(i).setTemperature(Temperature.get(i).getTemperature());
            }
            QJResult.add(QJRain.get(0));
            QJResult.add(QJRain.get(1));
        }
        threeResults.put("楼头区间",QJResult);

        return threeResults;
    }

    /**
     * 获取单个站点的洪水数据
     * @param Data
     * @param param
     * @param stationName
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     * @throws ParseException
     */
    public Object[][] getOneStationFlood(List<List<PredictInputData>> Data, ForecastInputParam param, String stationName)
            throws IOException, InvalidFormatException {
        param.setVmdK(1);
        param.setModel("Elman神经网络");
        param.setLocation(stationName);
        String Option = stationName + param.getPeriod();
        Object[][] historyInput = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx", Option);
        //数据驱动模型输入
        List<PredictInputData> machineData = Data.get(0);
        //数据驱动模型数据输入尺度转换
        List<PredictInputData> re = timeUtils.ChangeDate(machineData, param.getPeriod());
        Object[][] machineInputData = new Object[re.size()][4];
        for (int i = 0; i < re.size(); i++) {
            machineInputData[i][0] = re.get(i).getDates();
            machineInputData[i][1] = re.get(i).getFlow();
            machineInputData[i][2] = re.get(i).getTemperature();
            machineInputData[i][3] = re.get(i).getRainfall();
        }
        Object[][] machineInput = inputUtils.dataIntegration(historyInput, machineInputData, param);

        //判断是否为实时预报
        //判断是否为短期预报，是则使用物理模型
        if (param.getIsShortForecast()) {
            Object[][] result = locationShortForecast(machineInput, Data, param);
            return result;
        }
        //机器模型中长期预报
        else {
            MachineForecast machineForecast = new MachineForecast();
            /**
             * 训练模型获得参数以及其储存路径
             */
//            MachineModel train = new MachineModel();
//            List<TemporaryXlsx> xlsxPath;
//            xlsxPath = train.modelTrain(machineInput, param);
//            param.setXlsx(xlsxPath);
            machineInput = dataUtils.inputProcessing(machineInput,param);//获得距平值
            param = inputUtils.getMachineParams(param);
            //中长期预报预报
            Object[][] result = machineForecast.machineForecast(machineInput, param);
            return result;
        }
    }

    /**
     * 根据断面区分场次洪水是否叠加融雪径流
     * @param machineInput
     * @param Data
     * @param param
     * @return
     */
    public Object[][] locationShortForecast(Object[][] machineInput, List<List<PredictInputData>> Data, ForecastInputParam param)
            throws IOException {
        Object[][] floodResult;
        int before;
        int after;
        if (param.getLocation().equals("楼头区间")){
            before = 3;
            after = 3;
        }else{
            before = 5;
            after = 7;
        }
        floodResult = shortForecast(machineInput,Data,param,before,after);
        return floodResult;
    }

    /**
     * 场次洪水预报，混合融雪期和场次洪水
     * @param machineInput
     * @param Data
     * @param param
     * @param before
     * @param after
     * @return
     */
    public Object[][] shortForecast(Object[][] machineInput, List<List<PredictInputData>> Data, ForecastInputParam param, int before , int after)
            throws IOException {
        Date time = param.getPreStartTime();
        int month = timeUtils.getSpecificDate(time).get("月");
        //根据月份判断融雪洪水
        if (month>=before && month<=after){
            param.setIsSnowMeltModel(true);
            //划分历年融雪数据
            Object[][] snowMeltInput= dataUtils.snowMeltDate(machineInput, param.getLocation());
            //训练模型获得参数以及其储存路径
            SnowMeltModel model = new SnowMeltModel();
            /**
             * 是否训练模型
             */
//            result = model.snowTrain(snowMeltInput, param);
//            param.setXlsx(result);
            param = inputUtils.getMachineParams(param);
            //中长期预报预报融雪效果
            Object[][] snowFlood=model.snowForecast(snowMeltInput, param);
            //陕北模型预报
            PhysicalForecast physicalForecast = new PhysicalForecast();
            Object[][] peakFlood = physicalForecast.getShortResult(param, Data, snowFlood);
            return peakFlood;
        }
        //非融雪洪水
        else {
            param.setIsSnowMeltModel(false);
            PhysicalForecast physicalForecast = new PhysicalForecast();
            Object[][] snowFlood = new Object[0][];
            Object[][] peakFlood = physicalForecast.getShortResult(param, Data, snowFlood);
            return peakFlood;
        }
    }

    /**
     * 返回楼庄子出库和头屯河入库
     * @param Flood_Lzz
     * @param Flood_qj
     * @return
     */
    public List<Object[][]> getTthInput(Object[][] Flood_Lzz, Object[][] Flood_qj, ForecastInputParamNew param) {

        List<Object[][]> result = new ArrayList<>();
        if (param.getPeriodTimeType()==4){
            int timeLength =Integer.parseInt((String) Flood_Lzz[1][1]);
            //头屯河入库
            Object[][] tthInXlsx=new Object[Flood_Lzz.length][15];
            Object[][] tthIn =new Object[Flood_Lzz.length][2];
            double qjFlood = 0.0;
            double lzzFlood = 0.0;
            for (int i = 0; i < Flood_Lzz.length; i++) {
                tthIn[i][0]=Flood_Lzz[i][3];
                tthIn[i][1]=(double)Flood_Lzz[i][13]+(double)Flood_qj[i][4];
                lzzFlood += (double)Flood_Lzz[i][13];
                qjFlood += (double)Flood_qj[i][4];
            }
            PhysicalForecast physicalForecast = new PhysicalForecast();
            List<Object[][]> tthInformation = physicalForecast.getFloodInformation(tthIn);
            String level = physicalForecast.getFloodLevel(tthIn,"头屯河");
            Object[][] tthIndex = tthInformation.get(0);
            ModelOfTTH tthin = new ModelOfTTH(tthIn,timeLength);
            List<Option> tthInList = tthin.Calculate_S2();
            String data = Flood_Lzz[1][10].toString()+","+Flood_qj[1][10].toString();
            StringBuilder tthRain= new StringBuilder();
            String[] pairs = data.split(",");
            double sum = 0.0;
            for (String pair : pairs) {
                String[] splitPair = pair.split(":");
                String area = splitPair[0];
                double value = Double.parseDouble(splitPair[1]);
                if (tthRain.length() == 0){
                    tthRain = new StringBuilder(area + ":" + Math.round((float) value * lzzFlood / (qjFlood + lzzFlood)*100)/100.0 );
                    sum += Math.round((float) value * lzzFlood / (qjFlood + lzzFlood)*100)/100.0;
                }
                else {
                    if (area.equals("东南沟地区")||area.equals("3号桥地区")||area.equals("制材厂地区")){
                        tthRain.append(",").append(area).append(":").append(Math.round((float) value * lzzFlood / (qjFlood + lzzFlood)*100)/100.0);
                        sum += Math.round((float) value * lzzFlood / (qjFlood + lzzFlood)*100)/100.0;
                    }else {
                        if (!area.equals("头屯河入库")){
                            tthRain.append(",").append(area).append(":").append(Math.round((float) value * qjFlood / (qjFlood + lzzFlood)*100)/100.0);
                            sum += Math.round((float) value * qjFlood / (qjFlood + lzzFlood)*100)/100.0;
                        }
                        else {
                            tthRain.append(",").append(area).append(":").append(Math.round((float) (1-sum)*100)/100.0);
                        }
                    }
                }
            }
            //连续列的赋值
            for (int i = 0; i < Flood_Lzz.length; i++) {
                tthInXlsx[i][0]="头屯河";//断面位置
                tthInXlsx[i][1]=Integer.toString(timeLength);//尺度
                tthInXlsx[i][2]=tthIndex[i][0];//洪号
                tthInXlsx[i][3]=tthIn[i][0];//时间
                tthInXlsx[i][4]=Math.round((double) tthIn[i][1] * 100.0) / 100.0;//预报流量
                tthInXlsx[i][5]=tthInList.get(i).getH1();//相应水位
                if (tthInList.get(i).getH1()>988.46){//防洪高水位
                    tthInXlsx[i][14]=1;
                }
                else {
                    tthInXlsx[i][14]=0;
                }
                Object[][] floodNature = tthInformation.get(1);
                tthInXlsx[i][6]=floodNature[2][1];//洪峰
                tthInXlsx[i][7]=floodNature[3][1];//峰现时间
                tthInXlsx[i][8]=floodNature[1][1];//洪峰持续时间
                tthInXlsx[i][9]=floodNature[0][1];//洪量
                tthInXlsx[i][10]=tthRain.toString();//洪水来源
                tthInXlsx[i][11]="区间来水:"+Math.round((float)  qjFlood / (qjFlood + lzzFlood)*100)/100.0+","+"楼庄子出库:"+Math.round((float)  lzzFlood / (qjFlood + lzzFlood)*100)/100.0;//洪水组成
                tthInXlsx[i][12]=level;
                tthInXlsx[i][13]=tthInList.get(i).getQOut();
            }
            result.add(tthInXlsx);
            return result;
        }
        else {
            Object[][] tthInXlsx=new Object[Flood_Lzz.length][15];
            for (int i = 0; i < Flood_Lzz.length; i++) {
                tthInXlsx[i][0]="头屯河";//断面位置
                tthInXlsx[i][1]=Flood_Lzz[i][1];//尺度
                tthInXlsx[i][2]=Flood_Lzz[i][2];//洪号
                tthInXlsx[i][3]=Flood_Lzz[i][3];//时间
                tthInXlsx[i][4]=Math.round(((double) Flood_Lzz[i][4]+(double) Flood_qj[i][4]) * 100.0) / 100.0;//预报流量
                tthInXlsx[i][5]=Flood_qj[i][5];//相应水位
                tthInXlsx[i][6]=Math.round(((double) Flood_Lzz[i][6]+(double) Flood_qj[i][6]) * 100.0) / 100.0;//洪峰
                tthInXlsx[i][7]=Flood_Lzz[i][7];//峰现时间
                tthInXlsx[i][8]=Flood_Lzz[i][8];//洪峰持续时间
                tthInXlsx[i][9]=Math.round(((double) Flood_Lzz[i][9]+(double) Flood_qj[i][9]) * 100.0) / 100.0;//洪量
                tthInXlsx[i][12]=Flood_Lzz[i][12];
                tthInXlsx[i][13]=tthInXlsx[i][4];//出库流量
                tthInXlsx[i][14]=0;
            }
            result.add(tthInXlsx);
        }
        return result;
    }

}
