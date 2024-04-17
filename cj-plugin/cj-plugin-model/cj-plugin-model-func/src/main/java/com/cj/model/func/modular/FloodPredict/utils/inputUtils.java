package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParamNew;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;


import static com.cj.model.func.modular.FloodPredict.model.TouTunHe.getOneStationDataList;
import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.*;

public class InputUtils {


    /**
     * 判断需要从数据库获取哪些数据
     * @param
     * @return
     * @throws IOException
     */
    public static List<Date> judgeDate (Date predictTime, int n) throws IOException {
        List<Date> result = new ArrayList<>();
        Object[][] historyInput = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx", "3号桥日");
        Date historyTime = (Date) historyInput[historyInput.length-1][0];
        int number = duration(historyTime,predictTime,"日");
        if (number > 20){
            result.add(historyTime);
        }else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(predictTime);
            calendar.add(Calendar.DAY_OF_MONTH, -20);
            Date startTime = calendar.getTime();
            result.add(startTime);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(predictTime);
        calendar.add(Calendar.DAY_OF_MONTH, (n/24)+1);
        predictTime = calendar.getTime();
        result.add(predictTime);
        return result;
    }

    public static ForecastInputParam paramConvert(ForecastInputParamNew forecastParam){
        ForecastInputParam param = new ForecastInputParam();
        //模型类型
        param.setIsRealtime(true);
        param.setIsShortForecast(forecastParam.getModelType() == 3);
        //预报时间
        Date date= forecastParam.getPredictionTime();
        param.setPreStartTime(date);
        //时段
        if (forecastParam.getPeriodTimeType()==1) {
            param.setPeriod("月");
        }
        else if (forecastParam.getPeriodTimeType()==2) {
            param.setPeriod("旬");
        }
        else if (forecastParam.getPeriodTimeType()==3) {
            param.setPeriod("日");
        }
        else if (forecastParam.getPeriodTimeType()==4) {
            param.setPeriod("日");
        }
        //预报长度
        int l = forecastParam.getPeriodTimeStep();
        param.setPeriodStepSize(l);
        int n = forecastParam.getPeriodTimeNum();
        param.setPeriodStepNumber(n);
        if (forecastParam.getIsSimulation()==null){
            param.setIsSimulation(false);
        }else {
            param.setIsSimulation(forecastParam.getIsSimulation());
        }
        param.setPreFlow(forecastParam.getPreFlow());
        param.setPreRainFall(forecastParam.getPreRainFall());
        return param;
    }

    /**
     * 根据本地文件的最末时间和预报时间判断是否需要补充数据
     * @param paramForecastInputParamNew
     * @throws IOException
     * @throws ParseException
     * @throws InvalidFormatException
     */
    public static void intervalData(ForecastInputParamNew paramForecastInputParamNew)
            throws IOException, ParseException, InvalidFormatException {
        Object[][] historyInput = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx", "3号桥日");
        Date historyTime = (Date) historyInput[historyInput.length-1][0];
        Date predictTime = paramForecastInputParamNew.getPredictionTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(predictTime);
        calendar.add(Calendar.DAY_OF_MONTH, -20);
        predictTime = calendar.getTime();
        //预报时间超过储存时间
        if (predictTime.after(historyTime))
        {
            //数据不足，补充新时段数据
            Map<String,List<List<PredictInputData>>> stationsData = getOneStationDataList(paramForecastInputParamNew);
            List<List<PredictInputData>> Three = stationsData.get("3号桥");
            List<List<PredictInputData>> Lou = stationsData.get("楼庄子");
            List<List<PredictInputData>> Qu = stationsData.get("楼头区间");
            dataObject (Three,"3号桥");
            dataObject (Lou,"楼庄子");
            dataObject (Qu,"楼头区间");
        }
    }

    /**
     * 分断面进行输入数据处理
     * @param input
     * @param station
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static void dataObject(List<List<PredictInputData>> input,String station)
            throws IOException, InvalidFormatException {
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
    public static void differentInput (List<List<PredictInputData>> input,String station,String period)
            throws IOException, InvalidFormatException {
        String Option = station + period;
        Object[][] historyInput = ExcelTool.readExcel("D:\\头屯河历史数据1.xlsx", Option);
        //数据驱动模型输入
        List<PredictInputData> machineData = input.get(0);
        //数据驱动模型数据输入尺度转换
        List<PredictInputData> re = ChangeDate(machineData, period);
        Object[][] machineInputData = new Object[re.size()][4];
        for (int i = 0; i < re.size(); i++) {
            machineInputData[i][0] = re.get(i).getDates();
            machineInputData[i][1] = re.get(i).getFlow();
            machineInputData[i][2] = re.get(i).getTemperature();
            machineInputData[i][3] = re.get(i).getRainfall();
        }
        ForecastInputParam param = new ForecastInputParam();
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
    public static Object[][] dataIntegration(Object[][] historyInput ,Object[][] preliminaryData, ForecastInputParam param){

        if (preliminaryData.length==0){
            Date dateEnd = (Date) historyInput[historyInput.length-1][0];
            Date dateStart = param.getPreStartTime();
            int duration = 0;
            if (param.getPeriod().equals("日")){//计算预报时间和历史记录时间的相差天数
                duration =duration(dateStart,dateEnd,"日") + 1;
            }
            if (param.getPeriod().equals("旬")){
                duration =xunDuration(dateStart,dateEnd) + 1;
            }
            if (param.getPeriod().equals("月")){
                duration =duration(dateStart,dateEnd,"月") + 1;
            }
            if (duration < 0){//输入数据在历史中没有
                duration = 0;
            }
            Object[][] result = new Object[historyInput.length-duration][historyInput[0].length];
            System.arraycopy(historyInput, 0, result, 0, historyInput.length-duration);
            return result;
        }
        Date dateEnd = (Date) historyInput[historyInput.length-1][0];
        Date dateStart = (Date) preliminaryData[0][0];
        int duration = 0;
        if (param.getPeriod().equals("日")){//计算预报时间和历史记录时间的相差天数
            duration =duration(dateStart,dateEnd,"日");
        }
        if (param.getPeriod().equals("旬")){
            duration =xunDuration(dateStart,dateEnd);
        }
        if (param.getPeriod().equals("月")){
            duration =duration(dateStart,dateEnd,"月");
        }
        if (duration < 0){//输入数据在历史中没有
            duration = 0;
        }
        Object[][] result = integration(historyInput,preliminaryData,duration);
        return result;
    }

    /**
     * 数据整合
     * @param historyInput 历史数据
     * @param preliminaryData 获取数据
     * @param dayDuration 之间的差距
     * @return
     */
    public static Object[][] integration(Object[][] historyInput,Object[][] preliminaryData, int dayDuration){
        int hisLength = historyInput.length;
        int preLength = preliminaryData.length;
        int width = historyInput[0].length;
        Object[][] input;
        input = new Object[hisLength+preLength-dayDuration][4];
        for (int i = 0; i < hisLength-dayDuration; i++) {
            System.arraycopy(historyInput[i],0, input[i], 0, width);
        }
        for (int i = hisLength-dayDuration; i < hisLength+preLength-dayDuration; i++) {
            System.arraycopy(preliminaryData[i+dayDuration-hisLength],0, input[i], 0,width);
        }
//        if (hisLength+preLength-dayDuration>10000){//如果历史加目前输入大于1000天
//            input = new Object[10000][width];
//            if (preLength>10000){
//                for (int i = 0; i <10000 ; i++) {
//                    System.arraycopy(preliminaryData[preliminaryData.length-10000+i],0, input[i], 0, width);
//                }
//            }else {
//                for (int i = 0; i <10000-preLength ; i++) {
//                    System.arraycopy(historyInput[hisLength+preLength-dayDuration-10000+i],0, input[i], 0, width);
//                }
//                for (int i = 10000-preLength; i < 10000; i++) {
//                    System.arraycopy(preliminaryData[i+preLength-10000],0, input[i], 0, width);
//                }
//            }
//        }else {//历史加目前小于3000天
//            input = new Object[hisLength+preLength-dayDuration][4];
//            for (int i = 0; i < hisLength-dayDuration; i++) {
//                System.arraycopy(historyInput[i],0, input[i], 0, width);
//            }
//            for (int i = hisLength-dayDuration; i < hisLength+preLength-dayDuration; i++) {
//                System.arraycopy(preliminaryData[i+dayDuration-hisLength],0, input[i], 0,width);
//            }
//        }
        return input;
    }

    /**
     * 数据驱动模型参数存储位置
     * @param param
     * @return
     */
    public static ForecastInputParam getMachineParams(ForecastInputParam param){
        String location = param.getLocation();
        String period = param.getPeriod();
        if(location.equals("3号桥")){//楼庄子与3号桥运用参数一致
            location = "楼庄子";
        }
        List<TemporaryXlsx> xlsxList = new ArrayList<>();
        TemporaryXlsx machineParam = new TemporaryXlsx();
        if(param.getIsSnowMeltModel()==null||!param.getIsSnowMeltModel()){
            machineParam.setPath("D:\\tth_system\\end\\file\\"+location+period+"-PARAM.xlsx");
            machineParam.setSheetName("模型参数");
            xlsxList.add(machineParam);
            TemporaryXlsx maxMIn = new TemporaryXlsx();
            maxMIn.setPath("D:\\tth_system\\end\\file\\"+location+period+"最大最小值.xlsx");
            maxMIn.setSheetName("最大最小值");
            xlsxList.add(maxMIn);
            param.setXlsx(xlsxList);
        }else {
            machineParam.setPath("D:\\tth_system\\end\\file\\"+location+"融雪-PARAM.xlsx");
            machineParam.setSheetName("模型参数");
            xlsxList.add(machineParam);
            TemporaryXlsx maxMIn = new TemporaryXlsx();
            maxMIn.setPath("D:\\tth_system\\end\\file\\"+location+"融雪最大最小值.xlsx");
            maxMIn.setSheetName("最大最小值");
            xlsxList.add(maxMIn);
            param.setXlsx(xlsxList);
        }
        return param;
    }
}
