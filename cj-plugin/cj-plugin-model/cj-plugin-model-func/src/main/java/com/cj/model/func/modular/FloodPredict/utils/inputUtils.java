package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParamNew;
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
        Date today = new Date();
        if (predictTime.before(today)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DAY_OF_MONTH, (n/24+1));
            predictTime = calendar.getTime();
            result.add(predictTime);
        }else {
            result.add(predictTime);
        }
        return result;
    }

    /**
     * 补充新数据的方法
     * @param paramForcastInputParamNew
     * @throws IOException
     * @throws ParseException
     * @throws InvalidFormatException
     */
    public static void intervalData(ForcastInputParamNew paramForcastInputParamNew)
            throws IOException, ParseException, InvalidFormatException {
        //数据不足，补充新时段数据
        Map<String,List<List<PredictInputData>>> stationsData = getOneStationDataList(paramForcastInputParamNew);
        List<List<PredictInputData>> Three = stationsData.get("3号桥");
        List<List<PredictInputData>> Lou = stationsData.get("楼庄子");
        List<List<PredictInputData>> Qu = stationsData.get("楼头区间");
        dataObject (Three,"3号桥");
        dataObject (Lou,"楼庄子");
        dataObject (Qu,"楼头区间");
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
        if (preliminaryData.length==0){
            return historyInput;
        }
        Date dateEnd = (Date) historyInput[historyInput.length-1][0];
        Date dateStart = (Date) preliminaryData[0][0];
        if (param.getPeriod().equals("日")){
            int dayDuration =duration(dateStart,dateEnd,"日");
            if (dayDuration > 0){//输入数据在历史中有
            }else {
                dayDuration = 0;
            }
            result = integration(historyInput,preliminaryData,dayDuration);
        }
        if (param.getPeriod().equals("旬")){
            int xunDuration =xunDuration(dateStart,dateEnd);
            if (xunDuration > 0){//输入数据在历史中有
            }else {
                xunDuration = 0;
            }
            result = integration(historyInput,preliminaryData,xunDuration);
        }
        if (param.getPeriod().equals("月")){
            int monthDuration =duration(dateStart,dateEnd,"月");
            if (monthDuration > 0){//输入数据在历史中有
            }else {
                monthDuration = 0;
            }
            result = integration(historyInput,preliminaryData,monthDuration);
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
    public static Object[][] integration(Object[][] historyInput , Object[][] preliminaryData, int dayDuration){
        int hisLength = historyInput.length;
        int preLength = preliminaryData.length;
        int width = historyInput[0].length;
        Object[][] input;
        if (hisLength+preLength-dayDuration>3000){//如果历史加目前输入大于3000天
            input = new Object[3000][width];
            if (preLength>3000){
                for (int i = 0; i <3000 ; i++) {
                    System.arraycopy(preliminaryData[preliminaryData.length-3000+i],0, input[i], 0, width);
                }
            }else {
                for (int i = 0; i <3000-preLength ; i++) {
                    System.arraycopy(historyInput[hisLength+preLength-dayDuration-3000+i],0, input[i], 0, width);
                }
                for (int i = 3000-preLength; i < 1000; i++) {
                    System.arraycopy(preliminaryData[i+preLength-3000],0, input[i], 0, width);
                }
            }
        }else {//历史加目前小于3000天
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

    public static ForcastInputParam getMachineParams(ForcastInputParam param){
        String location = param.getLocation();
        String period = param.getPeriod();
        if(location.equals("3号桥")){
            location = "楼庄子";
        }
        List<TemporaryXlsx> xlsxList = new ArrayList<>();
        TemporaryXlsx machineParam = new TemporaryXlsx();
        machineParam.setPath("D:\\tth_system\\end\\file\\"+location+period+"-PARAM.xlsx");
        machineParam.setSheetName("模型参数");
        xlsxList.add(machineParam);
        TemporaryXlsx maxMIn = new TemporaryXlsx();
        maxMIn.setPath("D:\\tth_system\\end\\file\\"+location+period+"最大最小值.xlsx");
        maxMIn.setSheetName("最大最小值");
        xlsxList.add(maxMIn);
        param.setXlsx(xlsxList);
        return param;
    }
}
