package com.cj.model.func.modular.FloodPredict.model;


import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;


import java.io.IOException;
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
            paramForcastInputParamNew.setModelType(1);
            SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            paramForcastInputParamNew.setPredictionTime(sFormat.parse("2022-11-05 00:00:00"));
            paramForcastInputParamNew.setPeriodTimeType(1);
            paramForcastInputParamNew.setPeriodTimeStep(1);
            paramForcastInputParamNew.setPeriodTimeNum(4);

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
            param.setPeriod("小时");
        }
        //预报长度
        int l = paramForcastInputParamNew.getPeriodTimeStep();
        param.setPeriodStepSize(l);
        int n = paramForcastInputParamNew.getPeriodTimeNum();
        param.setPeriodStepNumber(n);
        //数据输入
        List<List<PredictInputData>> SHQDATA;
        SHQDATA=OneStationList(paramForcastInputParamNew,"三号桥");
        Object[][] Flood_Three;
        Flood_Three = getOneStationFlood(SHQDATA,param,"三号桥");
        temporaryXlsx=ObjectToXlsx(Flood_Three);
        String path = temporaryXlsx.getPath();
        String sheetName = temporaryXlsx.getSheetName();
        List<List<PredictInputData>> LZZDATA;
        LZZDATA=OneStationList(paramForcastInputParamNew,"楼庄子");
        Object[][] Flood_Lzz;
        Flood_Lzz = getOneStationFlood(LZZDATA,param,"楼庄子");
        ExcelTool.writeFloodExcel(path,sheetName,Flood_Lzz);
        Object[][] Flood_qj;
        List<List<PredictInputData>> QJDATA;
        QJDATA=OneStationList(paramForcastInputParamNew,"楼头区间");
        Flood_qj = getOneStationFlood(QJDATA,param,"楼头区间");
        ExcelTool.writeFloodExcel(path,sheetName,Flood_qj);
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


}
