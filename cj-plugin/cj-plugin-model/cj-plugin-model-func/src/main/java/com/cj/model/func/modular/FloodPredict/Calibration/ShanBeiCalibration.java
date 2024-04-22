package com.cj.model.func.modular.FloodPredict.Calibration;

import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.*;
import com.cj.model.func.modular.FloodPredict.Calibration.pso.*;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParamNew;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.model.function.SnowMeltModel;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.InputUtils;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;


public class ShanBeiCalibration {

    /// 流域面积 单位平方公里
    private double Area;

    ///基础流量
    private List<Object[][]> baseAveList = new ArrayList<>();
    //楼庄子蒸发降雨
    private List<Object[][]> preREDataList = new ArrayList<>();
    //前期雨量
    private List<Object[][]> historyRDataList = new ArrayList<>();
    //参与率定的真实径流
    private List<Object[][]> historyFDataList = new ArrayList<>();
    //预报的时间
    private List<Integer> durationList = new ArrayList<>();
    //选择的模型参数
    private ShanbeiParam shanbeiParamOld = new ShanbeiParam();
    //人工修改的模型参数
    private ShanbeiParam shanbeiParamMan = new ShanbeiParam();
    //是否为人工率定
    private Boolean isAutomatic;
    private ShanBeiModel shanbeiModel = new ShanBeiModel();

    private DataUtils dataUtils = new DataUtils();

    private TimeUtils timeUtils = new TimeUtils();

    private InputUtils inputUtils = new InputUtils();

    @SneakyThrows
    public Map<String, CalibrationOutput> calibration(CalibrationParam input) {
        Map<String, CalibrationOutput> result = new HashMap<>();
        OneCalibrationParam three = new OneCalibrationParam();
        isAutomatic = input.getIsAutomatic();
        three.setLzzHydrologyParam(input.getLzzHydrologyParam());
        three.setIrrigatedHydrologyParam(input.getIrrigatedHydrologyParam());
        three.setIsAutomatic(input.getIsAutomatic());
        three.setStartTime(input.getStartTime());
        three.setEndTime(input.getEndTime());
        result.put("头屯河", threeCalibration("头屯河", input, three));
        String msg = null;
        for (int i = 0; i < 3; i++) {
            String name = i == 0 ? "头屯河" : i == 1 ? "楼庄子" : "3号桥";
            try {
                result.put(name, threeCalibration(name, input, three));
            } catch (Exception ex) {
                msg += ex.getMessage();
            }
        }
//        if (msg != null){
//            new RuntimeException(msg);
//        }

//        result.put("楼庄子", threeCalibration("楼庄子", input, three));
//        result.put("头屯河", threeCalibration("头屯河", input, three));
//        result.put("3号桥", threeCalibration("3号桥", input, three));
        return result;
    }

    @SneakyThrows
    public CalibrationOutput threeCalibration(String location, CalibrationParam input, OneCalibrationParam param) {
        param.setLocation(location);
        if (!isAutomatic) {
            param.setManualParam(input.getManualParam().get(location));
        }
        param.setHistoryParam(input.getHistoryParam().get(location));
        CalibrationOutput threeOutput = new CalibrationOutput();
//        try {
        threeOutput = oneStationCalibration(param);
//        } catch (RuntimeException | IOException | InvalidFormatException | ParseException e) {
//            e.printStackTrace();
//            threeOutput.setError(String.valueOf(e));
//        }
        return threeOutput;
    }

    /**
     * @param inputData 从数据库中捞取预报开始时间前20天到预报结束时间的数据
     * @return 1.断面的陕北模型参数，2.真实径流，率定前的预报径流序列和率定后的预报径流序列
     */
    @SneakyThrows
    public void oneCalibration(OneCalibrationParam inputData) {
        FlowSelect flowSelect = new FlowSelect();
        String location = inputData.getLocation();
        List<Object[]> dateList = new ArrayList<>();
        dateList = flowSelect.getFloodDate(inputData);
        List<CalibrationData> dataList = new ArrayList<>();
        for (int i = 0; i < dateList.size(); i++) {
            ForecastInputParamNew forecastInputParamNew = new ForecastInputParamNew();
            Date startTime = (Date) dateList.get(i)[0];
            Date endTime = (Date) dateList.get(i)[1];
            forecastInputParamNew.setPredictionTime(startTime);//预报时间应当是先筛选出径流过程
            int duration;
            duration = timeUtils.duration(startTime, endTime, "小时");
            durationList.add(duration);
            forecastInputParamNew.setPeriodTimeNum(duration);
            forecastInputParamNew.setPeriodTimeStep(1);
            forecastInputParamNew.setIrrigatedHydrologyParam(inputData.getIrrigatedHydrologyParam());
            forecastInputParamNew.setLzzHydrologyParam(inputData.getLzzHydrologyParam());
            forecastInputParamNew.setModelType(3);
            CalibrationData data = calibrationDataInput(location, forecastInputParamNew);
            dataList.add(data);
        }
        Area = dataList.get(0).getArea();//面积
        shanbeiParamOld = inputData.getHistoryParam();

        for (int i = 0; i < dataList.size(); i++) {
            preREDataList.add(dataList.get(i).getPreRE());//蒸发降雨
            historyFDataList.add(dataList.get(i).getHisF());//前期径流
            historyRDataList.add(dataList.get(i).getHisR());//前20天雨量
            baseAveList.add(dataList.get(i).getBaseFlow());//基础径流
        }
    }

    /**
     * 获得单个站点的参数率定输入数据（面积，蒸发降雨，累积雨量，真实径流，融雪流量）
     *
     * @param location
     * @param paramNew
     * @return
     * @throws ParseException
     */
    @SneakyThrows
    public CalibrationData calibrationDataInput(String location, ForecastInputParamNew paramNew) {
        CalibrationData result = new CalibrationData();
        List<PredictInputData> hour = new ArrayList<>();
        List<PredictInputData> day = new ArrayList<>();
        List<LzzGaugingStation> threeFlow = new ArrayList<>();
        List<IrrigatedPlatformDataInfo> qjFlow = new ArrayList<>();
        List<LzzGaugingStation> flow = new ArrayList<>();
        List<IrrigatedPlatformDataInfo> flowQJ = new ArrayList<>();
        double area = 0.0;//面积
        Date startTime = paramNew.getPredictionTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        cal.add(Calendar.HOUR_OF_DAY, paramNew.getPeriodTimeNum());
        Date endTime = cal.getTime();//预报结束时间
        cal.add(Calendar.DAY_OF_MONTH, -inputUtils.beforeDays);
        Date startTimeBefore = cal.getTime();
        if (location.equals("3号桥")) {
            area = 690.0;
            List<List<PredictInputData>> lzzIntegration = dataUtils.lzzRainIntegration(paramNew);
            hour = dataUtils.pointToSurface(lzzIntegration.get(0), "小时", "3号桥");//前24小时以及期间的降雨
            day = dataUtils.pointToSurface(lzzIntegration.get(1), "日", "3号桥");//前20天累积雨量
            threeFlow = paramNew.getLzzHydrologyParam().getThreeGaugingStation();//3号桥流量
            for (int i = 0; i < threeFlow.size(); i++) {
                if (threeFlow.get(i).getGatherTime().after(startTimeBefore) && threeFlow.get(i).getGatherTime().before(endTime)) {
                    flow.add(threeFlow.get(i));
                }
            }
        }
        if (location.equals("楼庄子")) {
            area = 1174.0;
            List<List<PredictInputData>> lzzIntegration = dataUtils.lzzRainIntegration(paramNew);
            hour = dataUtils.pointToSurface(lzzIntegration.get(0), "小时", "楼庄子");//前10小时以及期间的降雨
            day = dataUtils.pointToSurface(lzzIntegration.get(1), "日", "楼庄子");//前20天累积雨量
            threeFlow = paramNew.getLzzHydrologyParam().getLzzInput();//流量
            for (int i = 0; i < threeFlow.size(); i++) {
                if (threeFlow.get(i).getGatherTime().after(startTimeBefore) && threeFlow.get(i).getGatherTime().before(endTime)) {
                    flow.add(threeFlow.get(i));
                }
            }
        }
        if (location.equals("头屯河")) {
            area = 380.0;
            List<List<PredictInputData>> qjIntegration = dataUtils.irrigateRainIntegration(paramNew);
            hour = dataUtils.pointToSurface(qjIntegration.get(0), "小时", "头屯河");//前10小时以及期间的降雨
            day = dataUtils.pointToSurface(qjIntegration.get(1), "日", "头屯河");//前20天累积雨量
            qjFlow = paramNew.getIrrigatedHydrologyParam().getTthInput();//流量
            for (int i = 0; i < qjFlow.size(); i++) {
                if (qjFlow.get(i).getMonitorTime().after(startTimeBefore) && qjFlow.get(i).getMonitorTime().before(endTime)) {
                    flowQJ.add(qjFlow.get(i));
                }
            }
        }
        //蒸发降雨
        Object[][] preRE = new Object[hour.size()][3];
        for (int i = 0; i < hour.size(); i++) {
            preRE[i][0] = hour.get(i).getDates();
            preRE[i][1] = hour.get(i).getTemperature();
            preRE[i][2] = hour.get(i).getRainfall();
        }
        preRE = dataUtils.temToEva(preRE);
        //历史雨量
        Object[][] hisR = new Object[day.size()][2];
        for (int i = 0; i < day.size(); i++) {
            hisR[i][0] = day.get(i).getDates();
            hisR[i][1] = day.get(i).getRainfall();
        }
        //获得前24个小时到预报结束时间的径流
        Calendar calendar = Calendar.getInstance();
        Date date = paramNew.getPredictionTime();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, -inputUtils.beforeHours);
        Date dateStart = calendar.getTime();
        List<Date> dateList = new ArrayList<>();
        Date date1 = dateStart;
        for (int i = 0; i < preRE.length; i++) {
            dateList.add(date1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(date1);
            calendar1.add(Calendar.HOUR_OF_DAY, 1);
            date1 = calendar1.getTime();
        }
        Object[][] hisF = new Object[preRE.length][2];
        Object[][] snow = new Object[hour.size() / 24 + 1][2];
        double baseAve = 0.0;

        if (!location.equals("头屯河")) {
            for (int i = 0; i < preRE.length; i++) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(dateStart);

                for (int j = 0; j < flow.size(); j++) {
                    if (timeUtils.DateCompare(dateStart, flow.get(j).getGatherTime(), "小时")) {
                        hisF[i][0] = flow.get(j).getGatherTime();
                        hisF[i][1] = flow.get(j).getFlow();
                        break;
                    } else {
                        hisF[i][0] = dateStart;
                        int n = timeUtils.findNearestTime(dateList, dateStart);
                        hisF[i][1] = flow.get(n).getFlow();
                    }
                }
                calendar1.add(Calendar.HOUR_OF_DAY, 1);//获得前十个小时到预报结束时间的径流
                dateStart = calendar1.getTime();
            }
            int month = timeUtils.getSpecificDate(paramNew.getPredictionTime()).get("月");
            if (month >= 5 & month <= 7) {
                snow = oneSnowFlow(location, paramNew.getPredictionTime(), hour.size());
            } else {
                for (int j = 0; j < flow.size(); j++) {
                    if (flow.get(j).getGatherTime().after(startTime)) {
                        baseAve = flow.get(j).getFlow();
                        break;
                    }
                }
                for (int i = 0; i < snow.length; i++) {
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(startTime);
                    snow[i][0] = startTime;
                    cal1.add(Calendar.DAY_OF_MONTH, 1);
                    startTime = cal1.getTime();
                    snow[i][1] = baseAve;
                }
            }
        } else {
//            int number = Math.min(20,flowQJ.size()/288)==0 ? 1 : Math.min(20,flowQJ.size()/288);
//            for (int j = 0; j < number; j++) {
//                Double baseFlow = 0.0;
//                int n = flowQJ.size() -1- j;
//                if (flowQJ.get(n).getYesterdayAvgFlow() != null){
//                    baseFlow = flowQJ.get(n).getYesterdayAvgFlow();
//                }else {
//                    n--;
//                }
//                baseAve += baseFlow;
//            }
//            baseAve = baseAve / number;
            for (int i = 0; i < preRE.length; i++) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(dateStart);
                for (int j = 0; j < flowQJ.size(); j++) {
                    if (timeUtils.DateCompare(dateStart, flowQJ.get(j).getMonitorTime(), "小时")) {
                        hisF[i][0] = flowQJ.get(j).getMonitorTime();
                        hisF[i][1] = flowQJ.get(j).getSqMonitorFlow();
                        break;
                    } else {
                        hisF[i][0] = dateStart;
                        int n = timeUtils.findNearestTime(dateList, dateStart);
                        hisF[i][1] = flowQJ.get(n).getSqMonitorFlow();
                    }
                }
                calendar1.add(Calendar.HOUR_OF_DAY, 1);//获得前十个小时到预报结束时间的径流
                dateStart = calendar1.getTime();
            }
            int month = timeUtils.getSpecificDate(paramNew.getPredictionTime()).get("月");
            if (month == 3) {
                snow = oneSnowFlow(location, paramNew.getPredictionTime(), hour.size());
            } else {
                for (int j = 0; j < flowQJ.size(); j++) {
                    if (flowQJ.get(j).getMonitorTime().after(startTime)) {
                        baseAve = flowQJ.get(j).getAvgFlow();
                        break;
                    }
                }
                for (int i = 0; i < snow.length; i++) {
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(startTime);
                    snow[i][0] = startTime;
                    cal1.add(Calendar.DAY_OF_MONTH, 1);
                    startTime = cal1.getTime();
                    snow[i][1] = baseAve;
                }
            }
        }
        result.setArea(area);
        result.setPreRE(preRE);
        result.setHisR(hisR);
        result.setHisF(hisF);
        result.setBaseFlow(snow);
        return result;
    }

    /**
     * 获取融雪基础径流
     *
     * @param location
     * @param startTime
     * @param number
     * @return
     * @throws IOException
     */
    public Object[][] oneSnowFlow(String location, Date startTime, int number) throws IOException {
        Object[][] result = new Object[number / 24 + 1][2];
        location = (location.equals("头屯河") ? "楼头区间" : location);
        Object[][] input = ExcelTool.readExcel("C:\\头屯河历史数据1.xlsx", location + "日");
        int days = number / 24 + 1;
        for (int i = 0; i < days; i++) {
            int l = 0;//截至到预报时间目前的日尺度数据
            for (int j = 0; j < input.length; j++) {
                if (((Date) input[j][0]).before(startTime)) {
                    l++;
                }
            }
            Object[][] snowData = new Object[l][4];
            System.arraycopy(input, 0, snowData, 0, l);
            Object[][] snowMeltInput = dataUtils.snowMeltDate(snowData, location);
            SnowMeltModel snowMeltModel = new SnowMeltModel();
            ForecastInputParam snowParam = new ForecastInputParam();
            InputUtils inputUtils = new InputUtils();
            snowParam.setLocation(location);
            snowParam.setPreStartTime(startTime);
            snowParam.setIsSnowMeltModel(true);
            snowParam.setPeriodStepNumber(1);
            snowParam.setPeriodStepSize(1);
            snowParam = inputUtils.getMachineParams(snowParam);
            Object[][] snow = snowMeltModel.snowForecast(snowMeltInput, snowParam);
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTime);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            startTime = cal.getTime();
            result[i] = snow[0];
        }
        return result;
    }

    /**
     * 需要前期降水、温度和径流，返回率定的参数和率定好的径流
     *
     * @param
     * @return
     * @throws IOException
     */
    public CalibrationOutput oneStationCalibration(OneCalibrationParam inputData) throws IOException, InvalidFormatException, ParseException {
        CalibrationOutput results = new CalibrationOutput();
        // 定义模型各参数的有效范围
        Interval[] regionIntervals = new Interval[]{
                new Interval(0.008, 0.2),//FB
                new Interval(60, 100),//WM
                new Interval(0.9, 1),//K
                new Interval(18, 40),//FC
                new Interval(60, 120),//FM
                new Interval(0.1, 0.3),//K
                new Interval(0.3, 0.4),//B
                new Interval(0.96, 0.98),//CS
        };
        //导入输入数据

        oneCalibration(inputData);
        // 创建PSO算法的问题域

        Domain domain = new Domain(regionIntervals, params -> Evaluate(params), 0);


        // 创建PSO算法实例
        PSO pso = new PSO(domain);

        // 运行算法并存储结果
        PSOResult result = pso.Execute(60, 100);

        System.out.println(result);
        // 输出结果
        ShanbeiParam shanbeiParamNew = new ShanbeiParam();
        shanbeiParamNew.setArea(Area);
        shanbeiParamNew.setFB(result.Position[0]);
        shanbeiParamNew.setWM(result.Position[1]);
        shanbeiParamNew.setKC(result.Position[2]);
        shanbeiParamNew.setFC(result.Position[3]);
        shanbeiParamNew.setFM(result.Position[4]);
        shanbeiParamNew.setK(result.Position[5]);
        shanbeiParamNew.setB(result.Position[6]);
        shanbeiParamNew.setCS(result.Position[7]);

        List<CalibrationFlow> flowList = new ArrayList<>();
        List<Double> hisList = new ArrayList<>();
        List<Double> preList = new ArrayList<>();
        for (int i = 0; i < historyRDataList.size(); i++) {
            Object[][] preREData = preREDataList.get(i);
            Object[][] historyRData = historyRDataList.get(i);
            Object[][] historyFData = historyFDataList.get(i);
            double baseAve = 0.0;
            int duration = durationList.get(i);

            //洪水过程推演
            if (isAutomatic) {
                shanbeiModel.InputData(shanbeiParamNew, preREData, historyRData)
                        .InitialMoistureContentCalculation()
                        .RunoffYieldCalculation_UnevenInfiltration()
                        .ConfluenceCalculation();
            } else {
                shanbeiModel.InputData(shanbeiParamMan, preREData, historyRData)
                        .InitialMoistureContentCalculation()
                        .RunoffYieldCalculation_UnevenInfiltration()
                        .ConfluenceCalculation();
            }
            Object[] timeData = new Object[duration];
            double[] hisData = new double[duration];
            double[] preData = new double[duration];
            int beforeHours = inputUtils.beforeHours;
            //洪水过程对比
            for (int j = 0; j < duration; j++) {
                for (int k = 0; k < baseAveList.get(i).length; k++) {
                    if (timeUtils.DateCompare((Date) historyFData[j + beforeHours][0], (Date) baseAveList.get(i)[k][0], "日")) {
                        baseAve = (double) baseAveList.get(i)[k][1];
                    } else {
                        baseAve = (double) historyFData[0][1];
                    }
                }

                timeData[j] = historyFData[j + beforeHours][0];
                hisData[j] = (double) historyFData[j + beforeHours][1];
                hisList.add((double) historyFData[j + beforeHours][1]);
                preData[j] = shanbeiModel.Q[j] + baseAve;
                preList.add(shanbeiModel.Q[j] + baseAve);
            }
            //前期参数的洪水过程推演
            shanbeiModel.InputData(shanbeiParamOld, preREData, historyRData)
                    .InitialMoistureContentCalculation()
                    .RunoffYieldCalculation_UnevenInfiltration()
                    .ConfluenceCalculation();
            double[] preDataOld = new double[duration];
            //洪水过程对比
            for (int j = 0; j < duration; j++) {
                preDataOld[j] = shanbeiModel.Q[j] + baseAve;
            }
            for (int j = 0; j < duration; j++) {
                CalibrationFlow flow = new CalibrationFlow();
                flow.setTime((Date) timeData[j]);
                flow.setHistoryFlow(hisData[j]);
                flow.setPreParamFlow(preDataOld[j]);
                flow.setNewParamFlow(preData[j]);
                flowList.add(flow);
            }

            //写表格
            Object[][] flowData = new Object[hisData.length + 1][2];
            flowData[0][0] = "真实径流";
            flowData[0][1] = "修正径流";
            for (int k = 1; k < hisData.length + 1; k++) {
                flowData[k][0] = hisData[k - 1];
                flowData[k][1] = preData[k - 1];
            }
            ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\系统模型参数率定结果.xlsx", String.valueOf(i), flowData);
        }
        double[] hisData = new double[hisList.size()];
        double[] preData = new double[preList.size()];
        for (int i = 0; i < hisData.length; i++) {
            hisData[i] = hisList.get(i);
            preData[i] = preList.get(i);
        }
        Double error = qualifyRate(hisData, preData);
        shanbeiParamNew.setL(shanbeiModel.L);
        shanbeiParamNew.setQC(error);


        results.setFlowList(flowList);
        results.setParam(shanbeiParamNew);
        return results;
    }

    // 定义PSO算法目标函数
    public double Evaluate(double[] params) {
        ParameterValidation parameterValidation = new ParameterValidation();
        List<Double> hisFlowList = new ArrayList<>();
        List<Double> preFlowList = new ArrayList<>();
        ShanbeiParam shanbeiParam = new ShanbeiParam();
        ShanBeiModel shanBeiModel = new ShanBeiModel();
        shanbeiParam.setArea(Area);//流域面积
        shanbeiParam.setFB(params[0]);//不透水面积的比例，透水面积比例为1-FB
        shanbeiParam.setWM(params[1]);//张力水蓄水容量，或最大蓄水量 60-80mm
        shanbeiParam.setKC(params[2]);//蒸散发折减系数 KC
        shanbeiParam.setFC(params[3]);//流域土壤稳定下渗率 0.3-0.5 mm/min
        shanbeiParam.setFM(params[4]);//流域土壤最大下渗率 1-2 mm/min
        shanbeiParam.setK(params[5]);//K,霍尔顿下渗曲线方程中的土质系数 0.04~0.05/min
        shanbeiParam.setB(params[6]);//B反映下渗能力在透水面积上的分布特性 1~5
        shanbeiParam.setCS(params[7]);//CS 为地面径流消退系数 0.1~1

        for (int i = 0; i < historyRDataList.size(); i++) {
            Object[][] preREData = preREDataList.get(i);
            Object[][] historyRData = historyRDataList.get(i);
            Object[][] historyFData = historyFDataList.get(i);
            double baseAve = 0.0;
            int duration = durationList.get(i);
            //洪水过程推演
            shanBeiModel.InputData(shanbeiParam, preREData, historyRData)
                    .InitialMoistureContentCalculation()
                    .RunoffYieldCalculation_UnevenInfiltration()
                    .ConfluenceCalculation();
            //洪水过程对比
            for (int j = 0; j < duration; j++) {
                for (int k = 0; k < baseAveList.get(i).length; k++) {
                    if (timeUtils.DateCompare((Date) historyFData[j][0], (Date) baseAveList.get(i)[k][0], "日")) {
                        baseAve = (double) baseAveList.get(i)[k][1];
                    } else {
                        baseAve = (double) historyFData[0][1];
                    }
                }
                hisFlowList.add((double) historyFData[j][1]);
                preFlowList.add(shanBeiModel.Q[j] + baseAve);
            }
        }
        double[] historyFlow = new double[hisFlowList.size()];
        double[] predictFlow = new double[preFlowList.size()];
        for (int j = 0; j < historyFlow.length; j++) {
            historyFlow[j] = hisFlowList.get(j);
            predictFlow[j] = preFlowList.get(j);
        }

//        return parameterValidation.NashSutcliffeEfficiency(historyFlow, predictFlow);

        return RMSE(historyFlow, predictFlow);
    }


    /**
     * 以真实值的20%为许可误差计算合格率
     *
     * @param real
     * @param estimate
     * @return
     */
    public double qualifyRate(double[] real, double[] estimate) {
        int size = real.length;
        int[] qualifyNum = new int[real.length];
        double[] qr = new double[real.length];
        for (int j = 0; j < real.length; j++) {
            qualifyNum[j] = 0;
            if (Math.abs(estimate[j] - real[j]) / real[j] <= 0.2) {
                qualifyNum[j]++;
            }
        }
        double sum = 0;
        for (int i = 0; i < qr.length; i++) {
            qr[i] = (double) qualifyNum[i] / size;
            sum += qr[i];
        }

        return sum;
    }

    public static double RMSE(double[] a, double[] b) {
        if (a.length != b.length) {
            return -1;
        }
        double result = 0;
        for (int i = 0; i < a.length; i++) {
            result += Math.pow(a[i] - b[i], 2);
        }
        result = Math.pow(result / a.length, 0.5);

        return result;
    }
}
