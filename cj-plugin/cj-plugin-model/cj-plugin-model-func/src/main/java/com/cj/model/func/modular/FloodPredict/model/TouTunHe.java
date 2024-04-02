package com.cj.model.func.modular.FloodPredict.model;


import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import com.cj.model.func.modular.FloodPrevent.model.ModelOfTTH;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static com.cj.model.func.modular.FloodPredict.model.MachineForecast.judgingYear;
import static com.cj.model.func.modular.FloodPredict.model.PhysicalForecast.getFloodLevel;
import static com.cj.model.func.modular.FloodPredict.model.PhysicalForecast.getFloodInformation;
import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.*;
import static com.cj.model.func.modular.FloodPredict.utils.InputUtils.*;
import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.*;
import static com.cj.model.func.modular.FloodPredict.utils.Tools.AddObject;
import static com.cj.model.func.modular.FloodPredict.utils.Tools.ObjectToXlsx;


public class TouTunHe {

    static Object[][] snow = new Object[0][];

    /**
     * 主方法
     * @param paramForcastInputParamNew 前端给的参数
     * @return Flood表的临时路径
     * @throws IOException
     * @throws ParseException
     * @throws InvalidFormatException
     */
    public static TemporaryXlsx getFloodList(ForcastInputParamNew paramForcastInputParamNew)
            throws IOException, ParseException, InvalidFormatException {
        //异常值处理
        paramForcastInputParamNew = emptyProcessing(paramForcastInputParamNew);
        //判断是否添加新数据
        Object[][] historyInput = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx", "3号桥日");
        Date historyTime = (Date) historyInput[historyInput.length-1][0];
        Date predictTime = paramForcastInputParamNew.getPredictionTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(predictTime);
        calendar.add(Calendar.DAY_OF_MONTH, -20);
        predictTime = calendar.getTime();
        //预报时间超过储存时间并且非场次洪水
        if (predictTime.after(historyTime)&&!(paramForcastInputParamNew.getModelType()==3))
        {
            intervalData(paramForcastInputParamNew);//添加新数据
        }
        //输入数据转化为模型所需
        ForcastInputParam param = new ForcastInputParam();
        TemporaryXlsx temporaryXlsx;
        //模型类型
        param.setIsRealtime(true);
        param.setIsShortForecast(paramForcastInputParamNew.getModelType() == 3);
        //预报时间
        Date date= paramForcastInputParamNew.getPredictionTime();
        param.setPreStartTime(date);

        //时段
        if (paramForcastInputParamNew.getPeriodTimeType()==1) {
            param.setPeriod("月");
        }
        else if (paramForcastInputParamNew.getPeriodTimeType()==2) {
            param.setPeriod("旬");
        }
        else if (paramForcastInputParamNew.getPeriodTimeType()==3) {
            param.setPeriod("日");
        }
        else if (paramForcastInputParamNew.getPeriodTimeType()==4) {
            param.setPeriod("日");
        }
        //预报长度
        int l = paramForcastInputParamNew.getPeriodTimeStep();
        param.setPeriodStepSize(l);
        int n = paramForcastInputParamNew.getPeriodTimeNum();
        param.setPeriodStepNumber(n);
        param.setIsSimulation(paramForcastInputParamNew.getIsSimulation());
        param.setPreFlow(paramForcastInputParamNew.getPreFlow());
        param.setPreRainFall(paramForcastInputParamNew.getPreRainFall());
        //数据输入
        Map<String,List<List<PredictInputData>>>stationsData = getOneStationDataList(paramForcastInputParamNew);
        //楼庄子
        List<List<PredictInputData>> LZZDATA;
        LZZDATA=stationsData.get("楼庄子");
        Object[][] Flood_Lzz;
        Flood_Lzz = getOneStationFlood(LZZDATA,param,"楼庄子");
        String judgeYear = (String) Flood_Lzz[0][12];
        //三号桥
        param.setPreStartTime(date);
        param.setPeriodStepSize(l);
        param.setPeriodStepNumber(n);
        List<List<PredictInputData>> SHQDATA;
        SHQDATA=stationsData.get("3号桥");
        Object[][] Flood_Three = new Object[Flood_Lzz.length][Flood_Lzz[0].length];
        if (!param.getPeriod().equals("小时")){
            for (int i = 0; i < Flood_Lzz.length; i++) {
                for (int j = 0; j < Flood_Lzz[0].length; j++) {
                    Flood_Three[i][j] =Flood_Lzz[i][j];
                }
            }
            for (int i = 0; i < Flood_Lzz.length; i++) {
                Flood_Three[i][0] = "3号桥";
            }
        }else {
            Flood_Three = getOneStationFlood(SHQDATA,param,"3号桥");
        }
        for (int i = 0; i < Flood_Three.length; i++) //超过汛限水位
        {
            Flood_Three[i][14] = Flood_Lzz[i][14];
        }
        //区间
        param.setPreStartTime(date);
        param.setPeriodStepSize(l);
        param.setPeriodStepNumber(n);
        Object[][] Flood_qj;
        List<List<PredictInputData>> QJDATA;
        QJDATA = stationsData.get("楼头区间");
        Flood_qj = getOneStationFlood(QJDATA,param,"楼头区间");
        //头屯河入库
        List<Object[][]> tthList =getTthInput(Flood_Lzz,Flood_qj,paramForcastInputParamNew);
        Object[][] Flood_tthIn = tthList.get(0);
        for (int i = 0; i < Flood_tthIn.length; i++) //超过汛限水位
        {
            Flood_qj[i][14] = Flood_tthIn[i][14];
        }
        //整合3号桥+楼庄子+区间+头屯河入库
        List<Object[][]> floodList = new ArrayList<>();
        floodList.add(Flood_Three);
        floodList.add(Flood_Lzz);
        floodList.add(Flood_qj);
        floodList.add(Flood_tthIn);
        Object[][] Flood = AddObject(floodList);
        for (int i = 0; i < Flood.length; i++)//洪水等级
        {
            Flood[i][12]=judgeYear;
        }
        //返回文件路径
        temporaryXlsx = ObjectToXlsx(Flood);
        return temporaryXlsx;

    }

    /**
     * 添加历史模拟时的历史降水数据
     * 根据站点名称获得相应的List<List<PredictInputData>> 数据
     * @param paramNew
     * @return 三个站点的日尺度历史径流，小时尺度雨量和日尺度雨量
     */
    public static  Map<String,List<List<PredictInputData>>> getOneStationDataList(ForcastInputParamNew paramNew)
            throws ParseException {
        Map<String,List<List<PredictInputData>>> threeResults = new HashMap<>();
        List<List<PredictInputData>> threeDataConversions = new ArrayList<>();
        threeDataConversions = DataUtils.lzzDataConversion(paramNew);//对输入数据进行处理
        List<PredictInputData> RAT = getRAndT(paramNew);//获得相应的温度和降水
        List<List<PredictInputData>> integration = new ArrayList<>();
        if (paramNew.getModelType()==3)//场次洪水
        {
            integration = lzzRainIntegration(paramNew);//整合雨量站数据转为模型所需类型
        }

        List<List<PredictInputData>> THQResult = new ArrayList<>();
        List<List<PredictInputData>> QJResult = new ArrayList<>();
        List<List<PredictInputData>> LZZResult = new ArrayList<>();
        //三号桥历史径流日尺度
        List<PredictInputData> THQ = threeDataConversions.get(0);
        THQ = addRAndT(THQ, RAT);
        THQResult.add(THQ);
        if (paramNew.getModelType()==3){
            THQResult.add(integration.get(0));
            THQResult.add(integration.get(1));
        }
        threeResults.put("3号桥",THQResult);

        //楼庄子历史径流日尺度
        List<PredictInputData> LZZ = threeDataConversions.get(1);
        LZZ = addRAndT(LZZ, RAT);
        LZZResult.add(LZZ);
        if (paramNew.getModelType()==3){
            LZZResult.add(integration.get(0));
            LZZResult.add(integration.get(1));
        }
        threeResults.put("楼庄子",LZZResult);

        //头屯河日径流
//        List<PredictInputData> QJ = Scaling(LZZ);
        List<PredictInputData> QJ = DataUtils.irrigatedDataConversion(paramNew.getIrrigatedHydrologyParam()).get(0);
        QJ = addRAndT(QJ, RAT);
        QJResult.add(QJ);
        if (paramNew.getModelType()==3)
        {
            //获得上游雨量站的温度
            List<PredictInputData> Temperature = integration.get(0);
            //添加到区间的数据中
            List<List<PredictInputData>> QJRain = irrigateRainIntegration(paramNew);
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
    public static Object[][] getOneStationFlood(List<List<PredictInputData>> Data,ForcastInputParam param,String stationName)
            throws IOException, InvalidFormatException {
        param.setModel("Elman神经网络");
        param.setLocation(stationName);
        String Option = stationName + param.getPeriod();
        Object[][] historyInput = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx", Option);
        //数据驱动模型输入
        List<PredictInputData> machineData = Data.get(0);
        //数据驱动模型数据输入尺度转换
        List<PredictInputData> re = ChangeDate(machineData, param.getPeriod());
        Object[][] machineInputData = new Object[re.size()][4];
        for (int i = 0; i < re.size(); i++) {
            machineInputData[i][0] = re.get(i).getDates();
            machineInputData[i][1] = re.get(i).getFlow();
            machineInputData[i][2] = re.get(i).getTemperature();
            machineInputData[i][3] = re.get(i).getRainfall();
        }
        Object[][] machineInput = dataIntegration(historyInput, machineInputData, param);
        List<TemporaryXlsx> result;
        //判断是否为实时预报
        List<Object[][]> forcastResultList = new ArrayList<>();
        //判断是否为短期预报，是则使用物理模型
        if (param.getIsShortForecast()) {
            if (param.getLocation().equals("楼庄子")){
                forcastResultList = locationShortForecast(machineInput, snow, Data, param);
                snow = forcastResultList.get(1);
            }
            if (param.getLocation().equals("3号桥")){
                forcastResultList = locationShortForecast(machineInput, snow, Data, param);
            }
            if (param.getLocation().equals("楼头区间")){
                snow = new Object[0][];
                forcastResultList = locationShortForecast(machineInput, snow, Data, param);
            }
        }
        //机器模型中长期预报
        else {
            MachineForecast machineForecast = new MachineForecast();
            List<Object[][]> resultList = new ArrayList<>();
            List<Object[]> selectDate = getSelectedData(param);
//            if (param.getPeriod().equals("日")){
//                //训练模型获得参数以及其储存路径
//                SnowMeltModel model = new SnowMeltModel();
//                result = model.snowTrain(machineInput, param);
//                param.setXlsx(result);
//                //短期预报效果
//                Object[][] snowFlood=model.snowForecast(machineInput, param);
//                resultList.add(snowFlood);
//            }
//            else {
                //训练模型获得参数以及其储存路径
            param.setVmdK(6);
            MachineModel train = new MachineModel();
            result = train.modelTrain(machineInput, param);
            param.setXlsx(result);
            param = getMachineParams(param);
            //中长期预报预报
            resultList.add(machineForecast.machineForecast(machineInput, param).get(0));
//            }

//            if ((int)selectDate.get(1)[1]!=0)//有枯水期
//            {
//                //划分丰水期
//                Object[][] FengForecastInput = DataUtils.SelectDate(Input, sFormat.parse("2024-10-01 00:00:00"));
//                Date[] date1 = (Date[]) selectDate.get(1)[0];
//                Date date2=date1[0];
//                param.setPreStartTime(date2);
//                param.setPeriodStepNumber((int)selectDate.get(1)[1]);
//                //训练模型获得参数以及其储存路径
//                MachineModel train = new MachineModel();
//                result = train.ModelTrain(FengForecastInput, param);
//                param.setXlsx(result);
//                //中长期预报预报
//                resultList.add(machineForcast.Forcast(FengForecastInput, param).get(0));
//            }
//            if ((int)selectDate.get(0)[1]!=0)//有丰水期
//            {
//                //划分丰水期
//                Object[][] FengForecastInput = DataUtils.SelectDate(Input, sFormat.parse("2024-05-01 00:00:00"));
//                Date[] date1 = (Date[]) selectDate.get(0)[0];
//                Date date2=date1[0];
//                param.setPreStartTime(date2);
//                param.setPeriodStepNumber((int)selectDate.get(0)[1]);
//                //训练模型获得参数以及其储存路径
//                MachineModel train = new MachineModel();
//                result = train.ModelTrain(FengForecastInput, param);
//                param.setXlsx(result);
//                //中长期预报预报
//                resultList.add(machineForcast.Forcast(FengForecastInput, param).get(0));
//            }
            Object[][] resultObject = AddObject(resultList);
            List<Object[]> ListSort = new ArrayList<>(Arrays.asList(resultObject));
            Object[][] resultSort = new Object[resultObject.length][resultObject[0].length];
            for (int j = 0; j < resultObject.length; j++) {
                int n =0;
                Date date = (Date)ListSort.get(0)[3];
                long time =date.getTime();
                for (int i = 0; i < ListSort.size(); i++) {
                    Date date1 = (Date)ListSort.get(i)[3];
                    long timeSort=date1.getTime();
                    if (timeSort <= time){
                        time = timeSort;
                        n=i;
                    }
                }
                resultSort[j]=ListSort.get(n);
                ListSort.remove(n);
            }
            for (int i = 0; i < resultSort.length; i++) {
                resultSort[i][12]=judgingYear(resultSort,param.getPeriod(),stationName);
            }
            forcastResultList.add(resultSort);
        }
        Object[][] forcastResult = forcastResultList.get(0);
        return forcastResult;
    }

    /**
     * 根据断面区分场次洪水是否叠加融雪径流
     * @param machineInput
     * @param Data
     * @param param
     * @return
     */
    public static List<Object[][]> locationShortForecast(Object[][] machineInput, Object[][] snow, List<List<PredictInputData>> Data, ForcastInputParam param)
            throws IOException, InvalidFormatException {
        List<Object[][]> floodList = new ArrayList<>();
        if (param.getLocation().equals("楼头区间")){
            int before = 3;
            int after = 5;
            floodList = shortForecast(machineInput,snow,Data,param,before,after);
        }else{
            int before = 5;
            int after = 7;
            floodList = shortForecast(machineInput,snow,Data,param,before,after);
        }
        return floodList;
    }

    /**
     * 场次洪水预报，混合融雪期和场次洪水
     * @param machineInput
     * @param snow
     * @param Data
     * @param param
     * @param before
     * @param after
     * @return
     */
    public static List<Object[][]> shortForecast(Object[][] machineInput, Object[][] snow , List<List<PredictInputData>> Data, ForcastInputParam param, int before , int after)
            throws IOException, InvalidFormatException {
        List<TemporaryXlsx> result;
        List<Object[][]> floodList = new ArrayList<>();
        Date time = param.getPreStartTime();
        int month = getSpecificDate(time).get("月");
        //根据月份判断融雪洪水
        if (month>=before && month<=after){
            param.setIsSnowMeltModel(true);
            //划分历年融雪数据
            Object[][] snowMeltInput= DataUtils.snowMeltDate(machineInput, param.getLocation());
            //训练模型获得参数以及其储存路径
            SnowMeltModel model = new SnowMeltModel();
            result = model.snowTrain(snowMeltInput, param);
            param.setXlsx(result);
            //中长期预报预报融雪效果
            Object[][] snowFlood=model.snowForecast(snowMeltInput, param);
            if (param.getLocation().equals("3号桥"))//3号桥融雪预报值为楼庄子的0.73
            {
                for (int i = 0; i < snow.length; i++) {
                    snowFlood[i][1]=(double)snow[i][1]*0.73365;
                }
            }
            //陕北模型预报
            PhysicalForecast physicalForecast = new PhysicalForecast();
            Object[][] peakFlood = physicalForecast.getShortResult(param, Data, snowFlood);
            floodList.add(peakFlood);
            floodList.add(snowFlood);
        }
        //非融雪洪水
        else {
            param.setIsSnowMeltModel(false);
            PhysicalForecast physicalForecast = new PhysicalForecast();
            Object[][] snowFlood = new Object[0][];
            Object[][] peakFlood = physicalForecast.getShortResult(param, Data, snowFlood);
            floodList.add(peakFlood);
            floodList.add(snowFlood);
        }
        return floodList;
    }

    /**
     * 返回楼庄子出库和头屯河入库
     * @param Flood_Lzz
     * @param Flood_qj
     * @return
     */
    public static List<Object[][]> getTthInput(Object[][] Flood_Lzz,Object[][] Flood_qj,ForcastInputParamNew param) {
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
            List<Object[][]> tthInformation = getFloodInformation(tthIn);
            String level = getFloodLevel(tthIn,"头屯河");
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
