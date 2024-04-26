package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParamNew;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import com.cj.model.func.modular.FloodPredict.model.TouTunHe;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;


public class InputUtils {

    TimeUtils timeUtils = new TimeUtils();


    public int beforeDays = 30;

    public int beforeHours = 10;
    //参数的路径
    public String paramPath = "D:\\tth_system\\end\\file\\";
    //数据的存储路径
    public String dataPath = "D:\\";

    /**
     * 判断需要从数据库获取哪些数据
     *
     * @param
     * @return
     * @throws IOException
     */
    public List<Date> judgeDate(Date predictTime, int n) throws IOException {
        List<Date> result = new ArrayList<>();
        Object[][] historyInput = ExcelTool.readExcel(dataPath + "头屯河历史数据1.xlsx", "3号桥日");
        Date historyTime = (Date) historyInput[historyInput.length - 1][0];
        int number = timeUtils.duration(historyTime, predictTime, "日");
        if (number > beforeDays) {
            result.add(historyTime);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(predictTime);
            calendar.add(Calendar.DAY_OF_MONTH, -beforeDays);
            Date startTime = calendar.getTime();
            result.add(startTime);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(predictTime);
        calendar.add(Calendar.DAY_OF_MONTH, (n / 24) + 1);
        predictTime = calendar.getTime();
        result.add(predictTime);
        return result;
    }

    public ForecastInputParam paramConvert(ForecastInputParamNew forecastParam) {
        ForecastInputParam param = new ForecastInputParam();
        //模型类型
        param.setIsRealtime(true);
        param.setIsShortForecast(forecastParam.getModelType() == 3);
        //预报时间
        Date date = forecastParam.getPredictionTime();
        param.setPreStartTime(date);
        //时段
        if (forecastParam.getPeriodTimeType() == 1) {
            param.setPeriod("月");
        } else if (forecastParam.getPeriodTimeType() == 2) {
            param.setPeriod("旬");
        } else if (forecastParam.getPeriodTimeType() == 3) {
            param.setPeriod("日");
        } else if (forecastParam.getPeriodTimeType() == 4) {
            param.setPeriod("日");
        }
        if (forecastParam.getIsSimulation() == null) {
            param.setIsSimulation(false);
        } else {
            param.setIsSimulation(forecastParam.getIsSimulation());
        }
        //预报长度
        int l = forecastParam.getPeriodTimeStep();
        int n = forecastParam.getPeriodTimeNum();
        param.setPeriodStepSize(l);
        param.setPeriodStepNumber(n);
        param.setPreFlow(forecastParam.getPreFlow());
        param.setPreRainFall(forecastParam.getPreRainFall());
        return param;
    }

    /**
     * 根据本地文件的最末时间和预报时间判断是否需要补充数据
     *
     * @param paramForecastInputParamNew
     * @throws IOException
     * @throws ParseException
     * @throws InvalidFormatException
     */
    public void intervalData(ForecastInputParamNew paramForecastInputParamNew)
            throws IOException, ParseException, InvalidFormatException {
        Object[][] historyInput = ExcelTool.readExcel(dataPath + "头屯河历史数据1.xlsx", "3号桥日");
        Date historyTime = (Date) historyInput[historyInput.length - 1][0];
        Date predictTime = paramForecastInputParamNew.getPredictionTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(predictTime);
        calendar.add(Calendar.DAY_OF_MONTH, -beforeDays);
        predictTime = calendar.getTime();
        //预报时间超过储存时间
        if (predictTime.after(historyTime)) {
            //数据不足，补充新时段数据
            TouTunHe touTunHe = new TouTunHe();
            Map<String, List<List<PredictInputData>>> stationsData = touTunHe.getOneStationDataList(paramForecastInputParamNew);
            List<List<PredictInputData>> Three = stationsData.get("3号桥");
            List<List<PredictInputData>> Lou = stationsData.get("楼庄子");
            List<List<PredictInputData>> Qu = stationsData.get("楼头区间");
            dataObject(Three, "3号桥");
            dataObject(Lou, "楼庄子");
            dataObject(Qu, "楼头区间");
        }
    }

    /**
     * 分断面进行输入数据处理
     *
     * @param input
     * @param station
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void dataObject(List<List<PredictInputData>> input, String station)
            throws IOException, InvalidFormatException {
        differentInput(input, station, "日");
        differentInput(input, station, "旬");
        differentInput(input, station, "月");
    }

    /**
     * 对数据进行整合处理
     *
     * @param input
     * @param station
     * @param period
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void differentInput(List<List<PredictInputData>> input, String station, String period)
            throws IOException, InvalidFormatException {
        String Option = station + period;
        Object[][] historyInput = ExcelTool.readExcel(dataPath + "头屯河历史数据1.xlsx", Option);
        //数据驱动模型输入
        List<PredictInputData> machineData = input.get(0);
        //数据驱动模型数据输入尺度转换
        List<PredictInputData> re = timeUtils.ChangeDate(machineData, period);
        Object[][] machineInputData = listToObject(re, station);
        ForecastInputParam param = new ForecastInputParam();
        param.setPreStartTime(input.get(0).get(0).getDates());
        param.setPeriod(period);
        Object[][] result = dataIntegration(historyInput, machineInputData, param);
        ExcelTool.writeObjectExcel(dataPath + "头屯河历史数据1.xlsx", station + period, result);
    }

    /**
     * 历史数据与前期数据的整合
     *
     * @param historyInput
     * @param preliminaryData
     * @return
     */
    public Object[][] dataIntegration(Object[][] historyInput, Object[][] preliminaryData, ForecastInputParam param) {
        Date dateEnd = (Date) historyInput[historyInput.length - 1][0];
        Date dateStart = (param.getPreStartTime().before(dateEnd) ? param.getPreStartTime() : (Date) preliminaryData[0][0]);
        int duration = 0;
        if (param.getPeriod().equals("日")) {//计算预报时间和历史记录时间的相差天数
            duration = timeUtils.duration(dateStart, dateEnd, "日");
        }
        if (param.getPeriod().equals("旬")) {
            duration = timeUtils.xunDuration(dateStart, dateEnd);
        }
        if (param.getPeriod().equals("月")) {
            duration = timeUtils.duration(dateStart, dateEnd, "月");
        }
        if (duration < 0) {//输入数据在历史中没有
            duration = 0;
        }
        if (param.getPreStartTime().before(dateEnd)) {
            Object[][] result = new Object[historyInput.length - duration][historyInput[0].length];
            System.arraycopy(historyInput, 0, result, 0, historyInput.length - duration);
            return result;
        }
        Object[][] result = integration(historyInput, preliminaryData, duration);
        return result;
    }

    /**
     * 数据整合
     *
     * @param historyInput    历史数据
     * @param preliminaryData 获取数据
     * @param dayDuration     之间的差距
     * @return
     */
    public Object[][] integration(Object[][] historyInput, Object[][] preliminaryData, int dayDuration) {
        int hisLength = historyInput.length;
        int preLength = preliminaryData.length;
        int width = historyInput[0].length;
        Object[][] input;
        input = new Object[hisLength + preLength - dayDuration][4];
        for (int i = 0; i < hisLength - dayDuration; i++) {
            System.arraycopy(historyInput[i], 0, input[i], 0, width);
        }
        for (int i = hisLength - dayDuration; i < hisLength + preLength - dayDuration; i++) {
            System.arraycopy(preliminaryData[i + dayDuration - hisLength], 0, input[i], 0, width);
        }
        return input;
    }

    /**
     * 数据驱动模型参数存储位置
     *
     * @param param
     * @return
     */
    public ForecastInputParam getMachineParams(ForecastInputParam param) {
        String location = param.getLocation().equals("3号桥") ? "楼庄子" : param.getLocation();
        String period = param.getPeriod();
        List<TemporaryXlsx> xlsxList = new ArrayList<>();
        TemporaryXlsx machineParam = new TemporaryXlsx();
        if (param.getIsSnowMeltModel() == null || !param.getIsSnowMeltModel()) {
            machineParam.setPath(paramPath + location + period + "-PARAM.xlsx");
            machineParam.setSheetName("模型参数");
            xlsxList.add(machineParam);
            TemporaryXlsx maxMIn = new TemporaryXlsx();
            maxMIn.setPath(paramPath + location + period + "最大最小值.xlsx");
            maxMIn.setSheetName("最大最小值");
            xlsxList.add(maxMIn);
            param.setXlsx(xlsxList);
        } else {
            machineParam.setPath(paramPath + location + "融雪-PARAM.xlsx");
            machineParam.setSheetName("模型参数");
            xlsxList.add(machineParam);
            TemporaryXlsx maxMIn = new TemporaryXlsx();
            maxMIn.setPath(paramPath + location + "融雪最大最小值.xlsx");
            maxMIn.setSheetName("最大最小值");
            xlsxList.add(maxMIn);
            param.setXlsx(xlsxList);
        }
        return param;
    }

    public Object[][] listToObject(List<PredictInputData> inputData, String location) {
        Object[][] machineInputData = new Object[inputData.size()][4];
        for (int i = 0; i < inputData.size(); i++) {
            machineInputData[i][0] = inputData.get(i).getDates();
            machineInputData[i][1] = (location.equals("楼头区间") ? inputData.get(i).getFlow() * setProportion(inputData.get(i).getDates()) : inputData.get(i).getFlow());
            machineInputData[i][2] = inputData.get(i).getTemperature();
            machineInputData[i][3] = inputData.get(i).getRainfall();
        }
        return machineInputData;
    }

    /**
     * 获取不同月份区间来水占头屯河比
     *
     * @param
     * @return
     */
    public double setProportion(Date date) {
        int month = timeUtils.getSpecificDate(date).get("月");
        double proportion = 0.058;
        switch (month) {
            case 1:
                proportion = 0.116;
                break;
            case 2:
                proportion = 0.0957;
                break;
            case 3:
                proportion = 0.538;
                break;
            case 4:
                proportion = 0.316;
                break;
            case 5:
                proportion = 0.072;
                break;
            case 6:
                proportion = 0.0484;
                break;
            case 7:
                proportion = 0.044;
                break;
            case 8:
                proportion = 0.0395;
                break;
            case 9:
                proportion = 0.0419;
                break;
            case 10:
                proportion = 0.0383;
                break;
            case 11:
                proportion = 0.0365;
                break;
            case 12:
                proportion = 0.001524;
                break;
        }
        return proportion;
    }
}
