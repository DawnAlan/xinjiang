package com.cj.model.func.modular.FloodPredict.model;




import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPrevent.model.ModelOfTTH;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.cj.model.func.modular.FloodPredict.model.PhysicalForcast.selectPeakFlood;
import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.*;



public class TouTunHe {

    public static void main(String[] args) throws IOException, ParseException, InvalidFormatException {

        //模型参数输入设置
        ForcastInputParamNew paramForcastInputParamNew = new ForcastInputParamNew();
        paramForcastInputParamNew.setModelType(3);//3为场次
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        paramForcastInputParamNew.setPredictionTime(sFormat.parse("2023-06-06 24:00:00"));
        paramForcastInputParamNew.setPeriodTimeType(4);//1为月，2为旬，3为日，4为小时
        paramForcastInputParamNew.setPeriodTimeStep(1);//预报步长
        paramForcastInputParamNew.setPeriodTimeNum(12);//预报数量
        paramForcastInputParamNew=objectToList(paramForcastInputParamNew);//读取表格
        List<RainFallDto> rainPre = rainPredict(paramForcastInputParamNew);
        paramForcastInputParamNew.setRainFallDtos(rainPre);
        paramForcastInputParamNew = emptyProcessing(paramForcastInputParamNew);//异常值处理
        List<Date> a = judgeDate(paramForcastInputParamNew.getPredictionTime());

        TemporaryXlsx result = new TemporaryXlsx();
        result= getFloodList(paramForcastInputParamNew);

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
        intervalData(paramForcastInputParamNew);//添加新数据
        paramForcastInputParamNew = emptyProcessing(paramForcastInputParamNew);//异常值处理

        ForcastInputParam param = new ForcastInputParam();
        TemporaryXlsx temporaryXlsx;
        //模型类型
        param.setIsRealtime(true);
        param.setIsShortForecast(paramForcastInputParamNew.getModelType() == 3);
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
        Map<String,List<List<PredictInputData>>>stationsData = OneStationDataList(paramForcastInputParamNew);
        //三号桥
        param.setPreStartTime(date);
        param.setPeriodStepSize(l);
        param.setPeriodStepNumber(n);
        List<List<PredictInputData>> SHQDATA;
        SHQDATA=stationsData.get("3号桥");
        Object[][] Flood_Three;
        Flood_Three = getOneStationFlood(SHQDATA,param,"3号桥");
        //楼庄子
        List<List<PredictInputData>> LZZDATA;
        LZZDATA=stationsData.get("楼庄子");
        Object[][] Flood_Lzz;
        Flood_Lzz = getOneStationFlood(LZZDATA,param,"楼庄子");
        String judgeYear = (String) Flood_Lzz[0][12];

        //区间
        param.setPreStartTime(date);
        param.setPeriodStepSize(l);
        param.setPeriodStepNumber(n);
        Object[][] Flood_qj;
        List<List<PredictInputData>> QJDATA;
        QJDATA=stationsData.get("楼头区间");
        Flood_qj = getOneStationFlood(QJDATA,param,"楼头区间");
        //整合3号桥+楼庄子+区间
        List<Object[][]> floodList = new ArrayList<>();
        floodList.add(Flood_Three);
        floodList.add(Flood_Lzz);
        floodList.add(Flood_qj);
        //头屯河入库

        List<Object[][]> tthList =getTthInput(Flood_Lzz,Flood_qj,paramForcastInputParamNew);
        Object[][] Flood_tthIn = tthList.get(0);
        floodList.add(Flood_tthIn);
        Object[][] Flood = AddObject(floodList);
        for (int i = 0; i < Flood.length; i++) {
            Flood[i][12]=judgeYear;
        }
        //返回文件路径
        temporaryXlsx = ObjectToXlsx(Flood);
        return temporaryXlsx;

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
            Object[][] Input=dataIntegration(historyInput,machineInputData,param);
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
     * 返回罗庄子出库和头屯河入库
     * @param Flood_Lzz
     * @param Flood_qj
     * @return
     */
    public static List<Object[][]> getTthInput(Object[][] Flood_Lzz,Object[][] Flood_qj,ForcastInputParamNew param)  {
        List<Object[][]> result = new ArrayList<>();
        if (param.getPeriodTimeType()==4){
            int timeLength =Integer.parseInt((String) Flood_Lzz[1][1]);
            //头屯河入库
            Object[][] tthInXlsx=new Object[Flood_Lzz.length][14];
            Object[][] tthIn =new Object[Flood_Lzz.length][2];
            double qjFlood = 0.0;
            double lzzFlood = 0.0;
            for (int i = 0; i < Flood_Lzz.length; i++) {
                tthIn[i][0]=Flood_Lzz[i][3];
                tthIn[i][1]=(double)Flood_Lzz[i][13]+(double)Flood_qj[i][4];
                lzzFlood += (double)Flood_Lzz[i][13];
                qjFlood += (double)Flood_qj[i][4];
            }
            List<Object[][]> tthInformation = selectPeakFlood(tthIn);
            Object[][] tthIndex = tthInformation.get(0);
            ModelOfTTH tthin = new ModelOfTTH(tthIn,timeLength);
            List<List<Double>> tthInList = tthin.Calculate_S2();
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
                tthInXlsx[i][5]=tthInList.get(2).get(i);//相应水位
                Object[][] floodNature = tthInformation.get(1);
                tthInXlsx[i][6]=floodNature[2][1];//洪峰
                tthInXlsx[i][7]=floodNature[3][1];//峰现时间
                tthInXlsx[i][8]=floodNature[1][1];//洪峰持续时间
                tthInXlsx[i][9]=floodNature[0][1];//洪量
                tthInXlsx[i][10]=tthRain.toString();//洪水来源
                tthInXlsx[i][11]="区间来水:"+Math.round((float)  qjFlood / (qjFlood + lzzFlood)*100)/100.0+","+"楼庄子出库:"+Math.round((float)  lzzFlood / (qjFlood + lzzFlood)*100)/100.0;//洪水组成
                tthInXlsx[i][12]=Flood_Lzz[i][12];
                tthInXlsx[i][13]=tthInList.get(3).get(i);
            }
            result.add(tthInXlsx);
            return result;
        }else {
            Object[][] tthInXlsx=new Object[Flood_Lzz.length][14];
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
            }
            result.add(tthInXlsx);
        }
        return result;
    }

    /**
     * 判断来水年的类别，丰平枯是根据历史来水量作为评判标准的
     * @param input
     * @return
     */
    public static String judgingYear(Object[][] input,String period,String stationName){
        String result = "";
        double[] water = new double[input.length];
        for (int i = 0; i < water.length; i++) {
            water[i]= (double) input[i][9];
        }
        if (period.equals("月")){
            double waterSum =0.0;
            for (double v : water) {
                waterSum += v;
            }
            waterSum = waterSum/10000;
            if (stationName.equals("楼庄子")){
                if (waterSum >=2.476){
                    result = "丰水年";
                }
                if (waterSum<2.246&&waterSum>=1.998){
                    result = "平水年";
                }
                if (waterSum<1.998){
                    result = "枯水年";
                }
            }
        }
        return result;
    }

    /**
     * 判断需要从数据库获取哪些数据
     * @return
     * @throws IOException
     */
    public static List<Date> judgeDate (Date predictTime) throws IOException {
        List<Date> result = new ArrayList<>();
        Object[][] historyInput = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx", "3号桥日");
        Date historyTime = (Date) historyInput[historyInput.length-1][0];
        int number = duration(predictTime,historyTime,"日");
        if (number > 20){
            result.add(historyTime);
        }else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(historyTime);
            calendar.add(Calendar.DAY_OF_MONTH, -20);
            Date startTime = calendar.getTime();
            result.add(startTime);
        }
        result.add(predictTime);
        return result;
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
    public static Object[][] getOneStationFlood(List<List<PredictInputData>> Data,ForcastInputParam param,String stationName)throws IOException, InvalidFormatException, ParseException {
        param.setModel("Elman神经网络");
        param.setLocation(stationName);
        String Option = stationName + param.getPeriod();
        Object[][] historyInput = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx", Option);
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
        List<Object[][]> forcastResultList = new ArrayList<>();
        //判断是否为短期预报，是则使用物理模型
        if (param.getIsShortForecast()) {
            forcastResultList = shortTimeForcast(historyInput, machineInputData, Data, param);
        }
        //机器模型中长期预报
        else {
            MachineForcast machineForcast = new MachineForcast();
            List<Object[][]> resultList = new ArrayList<>();
            Object[][] Input = dataIntegration(historyInput, machineInputData, param);
            List<Object[]> selectDate = getSelectedData(param);
            //训练模型获得参数以及其储存路径
            MachineModel train = new MachineModel();
            result = train.ModelTrain(Input, param);
            param.setXlsx(result);
            //中长期预报预报
            resultList.add(machineForcast.Forcast(Input, param).get(0));
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
            Object[][] resultObject = DataUtils.AddObject(resultList);
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
     * 根据站点名称获得相应的List<List<PredictInputData>> 数据
     * @param paramNew
     * @return 三个站点的日尺度历史径流，小时尺度雨量和日尺度雨量
     */
    public static  Map<String,List<List<PredictInputData>>> OneStationDataList(ForcastInputParamNew paramNew)
            throws IOException, InvalidFormatException, ParseException {
        Map<String,List<List<PredictInputData>>> threeResults = new HashMap<>();
        List<List<PredictInputData>> threeDataConversions = new ArrayList<>();
        threeDataConversions = DataUtils.lzzDataConversion(paramNew);//对输入数据进行处理
        List<PredictInputData> RAT = RAT(paramNew);//获得相应的温度和降水
        List<List<PredictInputData>> integration = new ArrayList<>();
        if (paramNew.getModelType()==3)//场次洪水
        {
            integration = LzzRainIntegration(paramNew);//整合雨量站数据转为模型所需类型
        }

        List<List<PredictInputData>> THQResult = new ArrayList<>();
        List<List<PredictInputData>> QJResult = new ArrayList<>();
        List<List<PredictInputData>> LZZResult = new ArrayList<>();
        //三号桥历史径流日尺度
        List<PredictInputData> THQ = threeDataConversions.get(0);
        THQ = AddRAndT(THQ, RAT);
        THQResult.add(THQ);
        if (paramNew.getModelType()==3){
            THQResult.add(integration.get(0));
            THQResult.add(integration.get(1));
        }
        threeResults.put("3号桥",THQResult);

        //楼庄子历史径流日尺度
        List<PredictInputData> LZZ = threeDataConversions.get(1);
        LZZ = AddRAndT(LZZ, RAT);
        LZZResult.add(LZZ);
        if (paramNew.getModelType()==3){
            LZZResult.add(integration.get(0));
            LZZResult.add(integration.get(1));
        }
        threeResults.put("楼庄子",LZZResult);

        //楼庄子出库日径流
        List<PredictInputData> QJ = Scaling(LZZ);
        QJ =AddRAndT(QJ, RAT);
        QJResult.add(QJ);
        if (paramNew.getModelType()==3)
        {
            //获得上游雨量站的温度
            List<PredictInputData> Temperature = integration.get(0);
            //添加到区间的数据中
            List<List<PredictInputData>> QJRain = IrrigateRainIntegration(paramNew);
            for (int i = 0; i < QJRain.get(0).size(); i++) {
                double T = Temperature.get(i).getTemperature();
                QJRain.get(0).get(i).setTemperature(T);
            }
            //后续更改，目前直接等同于上游的降水情况
            if (QJRain.get(0).size()/3==integration.get(0).size()/10){
                QJResult.add(QJRain.get(0));
                QJResult.add(QJRain.get(1));
            }else {
                QJResult.add(integration.get(0));
                QJResult.add(integration.get(1));
            }
        }
        threeResults.put("楼头区间",QJResult);

        return threeResults;
    }

    /**
     * 补充新数据的方法
     * @param paramForcastInputParamNew
     * @throws IOException
     * @throws ParseException
     * @throws InvalidFormatException
     */
    public static void intervalData(ForcastInputParamNew paramForcastInputParamNew) throws IOException, ParseException, InvalidFormatException {
        //数据不足，补充新时段数据
        Map<String,List<List<PredictInputData>>> stationsData = OneStationDataList(paramForcastInputParamNew);
        List<List<PredictInputData>> Three = stationsData.get("3号桥");
        List<List<PredictInputData>> Lou = stationsData.get("楼庄子");
        List<List<PredictInputData>> Qu = stationsData.get("楼头区间");
        dataObject (Three,"3号桥");
        dataObject (Lou,"楼庄子");
        dataObject (Qu,"楼头区间");
    }

    /**
     * 分尺度进行处理
     * @param input
     * @param station
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static void dataObject(List<List<PredictInputData>> input,String station) throws IOException, InvalidFormatException {
        differentInput(input,station,"日");
        differentInput(input,station,"旬");
        differentInput(input,station,"月");
    }

    /**
     * 对数据进行整合处理
     * @param input
     * @param station
     * @param period
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static void differentInput (List<List<PredictInputData>> input,String station,String period) throws IOException, InvalidFormatException {
        String Option = station + period;
        Object[][] historyInput = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx", Option);
        //数据驱动模型输入
        List<PredictInputData> machineData = input.get(0);
        //数据驱动模型数据输入尺度转换
        List<PredictInputData> re = DataUtils.ChangeDate(machineData, period);
        Object[][] machineInputData = new Object[re.size()][4];
        for (int i = 0; i < re.size(); i++) {
            machineInputData[i][0] = re.get(i).getDates();
            machineInputData[i][1] = re.get(i).getFlow();
            machineInputData[i][2] = re.get(i).getTemperature();
            machineInputData[i][3] = re.get(i).getRainfall();
        }
        ForcastInputParam param = new ForcastInputParam();
        param.setPreStartTime(input.get(0).get(0).getDates());
        param.setPeriod(period);
        Object[][] result = dataIntegration(historyInput, machineInputData, param);
        ExcelTool.writeObjectExcel("D:\\头屯河历史数据1.xlsx",station+period,result);
    }

    /**
     * 历史数据与前期数据的整合
     * @param historyInput
     * @param preliminaryData
     * @return
     */
    public static Object[][] dataIntegration(Object[][] historyInput ,Object[][] preliminaryData, ForcastInputParam param){
        Object[][] result = new Object[0][];
        Date dateEnd = (Date) historyInput[historyInput.length-1][0];
        Date dateStart = (Date) preliminaryData[0][0];
        if (param.getPeriod().equals("日")){
            int dayDuration =duration(dateStart,dateEnd,"日");
            if (dayDuration > 0){//输入数据在历史中有
            }else {
                dayDuration = 0;
            }
            result = intergration(historyInput,preliminaryData,dayDuration);
        }
        if (param.getPeriod().equals("旬")){
            int xunDuration =duration(dateStart,dateEnd,"日")/10;
            if (xunDuration > 0){//输入数据在历史中有
            }else {
                xunDuration = 0;
            }
            result = intergration(historyInput,preliminaryData,xunDuration);
        }
        if (param.getPeriod().equals("月")){
            int monthDuration =duration(dateStart,dateEnd,"月");
            if (monthDuration > 0){//输入数据在历史中有
            }else {
                monthDuration = 0;
            }
            result = intergration(historyInput,preliminaryData,monthDuration);
        }
        return result;
    }

    /**
     * 数据整合
     * @param historyInput 历史数据
     * @param preliminaryData 获取数据
     * @param dayDuration 之间的差距
     * @return
     */
    public static Object[][] intergration(Object[][] historyInput ,Object[][] preliminaryData,int dayDuration){
        int hisLength = historyInput.length;
        int preLength = preliminaryData.length;
        int width = historyInput[0].length;
        Object[][] input;
        if (hisLength+preLength-dayDuration>1000){//如果历史加目前输入大于1000天
            input = new Object[1000][width];
            if (preLength>1000){
                for (int i = 0; i <1000 ; i++) {
                    System.arraycopy(preliminaryData[preliminaryData.length-1000+i],0, input[i], 0, width);
                }
            }else {
                for (int i = 0; i <1000-preLength ; i++) {
                    System.arraycopy(historyInput[hisLength+preliminaryData.length-dayDuration-1000+i],0, input[i], 0, width);
                }
                for (int i = 1000-preLength; i < 1000; i++) {
                    System.arraycopy(preliminaryData[i+preLength-1000],0, input[i], 0, width);
                }
            }
        }else {//历史加目前小于1000天
            input = new Object[hisLength+preLength-dayDuration][4];
            for (int i = 0; i < hisLength-dayDuration; i++) {
                System.arraycopy(historyInput[i],0, input[i], 0, width);
            }
            for (int i = hisLength-dayDuration; i < hisLength+preLength-dayDuration; i++) {
                System.arraycopy(preliminaryData[i+dayDuration-hisLength],0, input[i], 0,width);
            }
        }
        return input;
    }




    /**
     * 读取表格赋予初始值
     * @param input
     * @return
     * @throws IOException
     */
    public static  ForcastInputParamNew objectToList (ForcastInputParamNew input) throws IOException {
    ForcastInputParamNew result = new ForcastInputParamNew();
    result.setModelType(input.getModelType());
    result.setPredictionTime(input.getPredictionTime());
    result.setPeriodTimeStep(input.getPeriodTimeStep());
    result.setPeriodTimeNum(input.getPeriodTimeNum());
    result.setPeriodTimeType(input.getPeriodTimeType());
    LzzHydrologyParam lzzParam = new LzzHydrologyParam();
    IrrigatedHydrologyParam qjParam = new IrrigatedHydrologyParam();
    //楼庄子雨量站List
    Object[][] lzzRainObject = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\雨量站2.xlsx","LZZ_RAINFALL_STATION_2023121317");
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
    Object[][] lzzFlowObject = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\楼庄子库水位站2.xlsx","LZZ_GAUGING_STATION_20231213173");
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

    Object[][] qjObject = ExcelTool.readExcel("D:\\204\\2.头屯河\\资料\\2.小时尺度数据\\区间2.xlsx","区间");
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

    /**
     * 预报雨量赋值
     * @param input
     * @return
     */
    public static List<RainFallDto> rainPredict (ForcastInputParamNew input){
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
            for (int i = 0; i < 13; i++) //后续更改，目前写死为13个雨量站
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
