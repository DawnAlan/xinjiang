package com.cj.model.func.modular.FloodPredict.Calibration;

import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationFlow;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationOutput;
import com.cj.model.func.modular.FloodPredict.Calibration.pso.Domain;
import com.cj.model.func.modular.FloodPredict.Calibration.pso.Interval;
import com.cj.model.func.modular.FloodPredict.Calibration.pso.PSO;
import com.cj.model.func.modular.FloodPredict.Calibration.pso.PSOResult;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParamNew;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationParam;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static com.cj.model.func.modular.FloodPredict.Calibration.pso.ParameterValidation.NashSutcliffeEfficiency;
import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.*;
import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.*;


public class ShanBeiCalibration {

    /// 流域面积 单位平方公里
    static double Area;
    ///基础流量
    static double baseAve;
    //预报流量
    static double[] preData;
    //历史流量
    static double[] hisData;
    //楼庄子蒸发降雨
    static Object[][] preREData;
    //前期雨量
    static Object[][] historyRData;
    //参与率定的真实径流
    static Object[][] historyFData;
    //预报的时间
    static int duration;

    static ShanBeiModel shanbeiModel = new ShanBeiModel();

    /**
     * @param inputData
     * 从数据库中捞取预报开始时间前20天到预报结束时间的数据
     * @return 1.断面的陕北模型参数，2.真实径流，率定前的预报径流序列和率定后的预报径流序列
     */
    public CalibrationOutput calibration(CalibrationParam inputData) throws ParseException, IOException, InvalidFormatException {
        CalibrationOutput result;
        ForecastInputParamNew forecastInputParamNew = new ForecastInputParamNew();
        forecastInputParamNew.setPredictionTime(inputData.getStartTime());
        duration = duration(inputData.getStartTime(),inputData.getEndTime(),"小时");
        forecastInputParamNew.setPeriodTimeNum(duration);
        forecastInputParamNew.setPeriodTimeStep(1);
        forecastInputParamNew.setIrrigatedHydrologyParam(inputData.getIrrigatedHydrologyParam());
        forecastInputParamNew.setLzzHydrologyParam(inputData.getLzzHydrologyParam());
        forecastInputParamNew.setModelType(3);
        String location = inputData.getLocation();
        Object[][] imitateFlow = inputData.getImitateFlow();//方案中的结果
        List<Object[][]> data = calibrationDataInput(location, forecastInputParamNew);
        data.add(imitateFlow);

        if (inputData.getIsAutomatic()){
            result = oneStationCalibration(data);
        }else {
            ShanbeiParam three = inputData.getManualParam();
            result = oneStationHuman(three,data);
        }
        result.setLocation(location);
        return result;
    }

    /**
     * 获得单个站点的参数率定输入数据（面积，蒸发降雨，累积雨量，真实径流，基础流量）
     * @param location
     * @param paramNew
     * @return
     * @throws ParseException
     */
    public List<Object[][]> calibrationDataInput(String location, ForecastInputParamNew paramNew) throws ParseException {
        List<Object[][]> result = new ArrayList<>();
        List<PredictInputData> hour = new ArrayList<>();
        List<PredictInputData> day = new ArrayList<>();
        List<LzzGaugingStation> flow =new ArrayList<>();
        List<IrrigatedPlatformDataInfo> qjFlow = new ArrayList<>();
        Object[][] area = new Object[1][1];//面积

        if (location.equals("3号桥")){
            area[0][0] = 690.0;
            List<List<PredictInputData>> lzzIntegration = lzzRainIntegration(paramNew);
            hour = pointToSurface(lzzIntegration.get(0),"小时","3号桥");//前10小时以及期间的降雨
            day = pointToSurface(lzzIntegration.get(1),"日","3号桥");//前20天累积雨量
            flow = paramNew.getLzzHydrologyParam().getThreeGaugingStation();//3号桥流量
        }
        if (location.equals("楼庄子")){
            area[0][0] = 1174.0;
            List<List<PredictInputData>> lzzIntegration = lzzRainIntegration(paramNew);
            hour = pointToSurface(lzzIntegration.get(0),"小时","楼庄子");//前10小时以及期间的降雨
            day = pointToSurface(lzzIntegration.get(1),"日","楼庄子");//前20天累积雨量
            flow = paramNew.getLzzHydrologyParam().getLzzInput();//流量
        }
        if (location.equals("楼头区间")){
            area[0][0] = 388.0;
            List<List<PredictInputData>> qjIntegration = irrigateRainIntegration(paramNew);
            hour = pointToSurface(qjIntegration.get(0),"小时","楼头区间");//前10小时以及期间的降雨
            day = pointToSurface(qjIntegration.get(1),"日","楼头区间");//前20天累积雨量
            qjFlow = paramNew.getIrrigatedHydrologyParam().getTthInput();//流量
        }
        //蒸发降雨
        Object[][] preRE = new Object[hour.size()][3];
        for (int i = 0; i < hour.size(); i++) {
            preRE[i][0]=hour.get(i).getDates();
            preRE[i][1]=hour.get(i).getTemperature();
            preRE[i][2]=hour.get(i).getRainfall();
        }
        preRE = temToEva(preRE);
        //历史雨量
        Object[][] hisR = new Object[day.size()][2];
        for (int i = 0; i < day.size(); i++) {
            hisR[i][0]=day.get(i).getDates();
            hisR[i][1]=day.get(i).getRainfall();
        }
        //获得前十个小时到预报结束时间的径流
        Calendar calendar = Calendar.getInstance();
        Date date = paramNew.getPredictionTime();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, -10);
        Date dateStart = calendar.getTime();
        List<Date> dateList = new ArrayList<>();
        Date date1 = dateStart;
        for (int i = 0; i < preRE.length; i++) {
            dateList.add(date1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(date);
            calendar1.add(Calendar.HOUR_OF_DAY, 1);
            date1 = calendar1.getTime();
        }
        Object[][] hisF = new Object[preRE.length][2];
        baseAve = 0.0;
        Object[][] baseflow = new Object[1][1];
        if (!location.equals("楼头区间")){
            for (int i = 0; i < preRE.length; i++) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(dateStart);
                for (int j = 0; j < 20; j++) {
                    Double baseFlow = 0.0;
                    int n = flow.size() -1- j;
                    if (flow.get(n).getFlow() != null){
                        baseFlow = flow.get(n).getFlow();
                    }else {
                        n--;
                    }
                    baseAve += baseFlow;
                }
                baseAve = baseAve / 20;
                for (int j = 0; j < flow.size(); j++) {
                    if (DateCompare(dateStart,flow.get(j).getGatherTime(),"小时")){
                        hisF[i][0]=flow.get(j).getGatherTime();
                        hisF[i][1]=flow.get(j).getFlow();
                        break;
                    }else {
                        hisF[i][0]=dateStart;
                        int n = findNearestTime(dateList,dateStart);
                        hisF[i][1]=flow.get(n).getFlow();
                    }
                }
                calendar1.add(Calendar.HOUR_OF_DAY, 1);//获得前十个小时到预报结束时间的径流
                dateStart=calendar1.getTime();
            }
        }
        else {
            for (int j = 0; j < 20; j++) {
                Double baseFlow = 0.0;
                int n = qjFlow.size() -1- j;
                if (qjFlow.get(n).getYesterdayAvgFlow() != null){
                    baseFlow = qjFlow.get(n).getYesterdayAvgFlow();
                }else {
                    n--;
                }
                baseAve += baseFlow;
            }
            baseAve = baseAve / 20;
            for (int i = 0; i < preRE.length; i++) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(dateStart);
                for (int j = 0; j < qjFlow.size(); j++) {
                    if (DateCompare(dateStart,qjFlow.get(j).getMonitorTime(),"小时")){
                        hisF[i][0]=qjFlow.get(j).getMonitorTime();
                        hisF[i][1]=qjFlow.get(j).getSqMonitorFlow();
                        break;
                    }else {
                        hisF[i][0]=dateStart;
                        int n = findNearestTime(dateList,dateStart);
                        hisF[i][1]=qjFlow.get(n).getSqMonitorFlow();
                    }
                }
                calendar1.add(Calendar.HOUR_OF_DAY, 1);//获得前十个小时到预报结束时间的径流
                dateStart=calendar1.getTime();
            }
        }
        baseflow[0][0]=baseAve;
        result.add(area);
        result.add(preRE);
        result.add(hisR);
        result.add(hisF);
        result.add(baseflow);
        return result;
    }

    /**
     * 需要前期降水、温度和径流，返回率定的参数和率定好的径流
     * @param
     * @return
     * @throws IOException
     */
    public CalibrationOutput oneStationCalibration(List<Object[][]> inputData) throws IOException, InvalidFormatException {
        CalibrationOutput results = new CalibrationOutput();
        Object[][] area = inputData.get(0);//面积
        Area= (double) area[0][0];
        preREData = inputData.get(1);//蒸发降雨
        historyRData = inputData.get(2);//前20天雨量
        historyFData = inputData.get(3);//前期径流
        baseAve = (double) inputData.get(4)[0][0];//基础径流
        Object[][] preParamFlow = inputData.get(5);//前期参数径流
        // 定义模型各参数的有效范围
        Interval[] regionIntervals = new Interval[]{
                new Interval(0.01, 0.02),
                new Interval(50, 80),
                new Interval(1, 1),
                new Interval(18, 30),
                new Interval(60, 120),
                new Interval(0.01, 0.02),
                new Interval(0.2, 0.3),
                new Interval(0.96, 0.995),
                new Interval(1, 5),
                //前期径流
                new Interval(baseAve, baseAve),
        };

        // 创建PSO算法的问题域
        Domain domain = new Domain(regionIntervals, ShanBeiCalibration::Evaluate, 1);

        // 创建PSO算法实例
        PSO pso = new PSO(domain);

        // 运行算法并存储结果
        PSOResult result = pso.Execute(100, 300);

//        // 输出结果
//        System.out.println(result);
        List<CalibrationFlow> flowList = new ArrayList<>();
        for (int i = 0; i < hisData.length; i++) {
            CalibrationFlow flow = new CalibrationFlow();
            flow.setTime((Date) preREData[i][0]);
            flow.setHistoryFlow(hisData[i]);
            flow.setPreParamFlow((Double) preParamFlow[i][1]);
            flow.setNewParamFlow(preData[i]);
            flowList.add(flow);
        }
        Double error = qualifyRate(hisData,preData);
        ShanbeiParam shanbeiParam = new ShanbeiParam();
        shanbeiParam.setArea(Area);
        shanbeiParam.setFB(result.Position[0]);
        shanbeiParam.setWM(result.Position[1]);
        shanbeiParam.setKC(result.Position[2]);
        shanbeiParam.setFC(result.Position[3]);
        shanbeiParam.setFM(result.Position[4]);
        shanbeiParam.setK(result.Position[5]);
        shanbeiParam.setB(result.Position[6]);
        shanbeiParam.setCS(result.Position[7]);
        shanbeiParam.setL((int) result.Position[8]);
        shanbeiParam.setQC(error);

        Object[][] flowData =new Object[hisData.length + 1][2];
        flowData[0][0]="真实径流";
        flowData[0][1]="修正径流";
        for (int i = 1; i < hisData.length + 1; i++) {
            flowData[i][0]=hisData[i-1];
            flowData[i][1]=preData[i-1];
        }
        Object[][] param = new Object[11][2];
        param[0][0] = "面积";param[0][1] = Area;
        param[1][0] = "FB";param[1][1] = result.Position[0];//FB
        param[2][0] = "WM";param[2][1] = result.Position[1];//WM
        param[3][0] = "KC";param[3][1] = result.Position[2];//KC
        param[4][0] = "FC"; param[4][1] = result.Position[3];//FC
        param[5][0] = "WM"; param[5][1] = result.Position[4];//FM
        param[6][0] = "K";param[6][1] = result.Position[5];//K
        param[7][0] = "B"; param[7][1] = result.Position[6];//B
        param[8][0] = "CS"; param[8][1] = result.Position[7];//CS
        param[9][0] = "L"; param[9][1] = result.Position[8];//L
        param[10][0] = "合格率";param[10][1] = error;

        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\系统模型参数率定结果.xlsx", String.valueOf(Area),flowData);

        results.setFlowList(flowList);
        results.setParam(shanbeiParam);
        return results;
    }

    // 定义PSO算法目标函数
    public static double Evaluate(double[] params) {
        double[] shanbeiParams=new double[10];
        shanbeiParams[0]=Area;//流域面积
        shanbeiParams[1]=params[0];//不透水面积的比例，透水面积比例为1-FB
        shanbeiParams[2]=params[1];//张力水蓄水容量，或最大蓄水量 60-80mm
        shanbeiParams[3]=params[2];//蒸散发折减系数 KC
        shanbeiParams[4]=params[3];//流域土壤稳定下渗率 0.3-0.5 mm/min
        shanbeiParams[5]=params[4];//流域土壤最大下渗率 1-2 mm/min
        shanbeiParams[6]=params[5];//K,霍尔顿下渗曲线方程中的土质系数 0.04~0.05/min
        shanbeiParams[7]=params[6];//B反映下渗能力在透水面积上的分布特性 1~5
        shanbeiParams[8]=params[7];//CS 为地面径流消退系数 0.1~1
        shanbeiParams[9]=params[8];//L为汇流滞时（时段数）
        int L = (int)params[8];
        //洪水过程推演
        shanbeiModel.InputData(shanbeiParams,preREData,historyRData)
                .InitialMoistureContentCalculation()
                .RunoffYieldCalculation_UnevenInfiltration()
                .ConfluenceCalculation();
        //洪水过程对比
        preData=new double[duration];
        hisData=new double[duration];
        for (int i = 0; i < duration; i++) {
            hisData[i]= (double) historyFData[i+L][1];
            preData[i]= shanbeiModel.Q[i+L]+params[9];
        }
        return NashSutcliffeEfficiency(hisData, preData);
    }

    /**
     * 人工率定参数
     * @param param
     * @param inputData
     * @return
     */
    public CalibrationOutput oneStationHuman(ShanbeiParam param, List<Object[][]> inputData){
        CalibrationOutput result = new CalibrationOutput();
        Object[][] area = inputData.get(0);//面积
        Area= (double) area[0][0];
        preREData = inputData.get(1);//蒸发降雨
        historyRData = inputData.get(2);//前20天雨量
        historyFData = inputData.get(3);//前期径流
        baseAve = (double) inputData.get(4)[0][0];
        Object[][] preParamFlow = inputData.get(5);//前期参数径流
        double[] shanbeiParams = new double[10];
        shanbeiParams[0] = Area;
        shanbeiParams[1] = param.getFB();//FB
        shanbeiParams[2] = param.getWM();//WM
        shanbeiParams[3] = param.getKC();//KC
        shanbeiParams[4] = param.getFC();//FC
        shanbeiParams[5] = param.getFM();//FM
        shanbeiParams[6] = param.getK();//K
        shanbeiParams[7] = param.getB();//B
        shanbeiParams[8] = param.getCS();//CS
        shanbeiParams[9] = param.getL();//L
        int L = param.getL();
        //洪水过程推演
        shanbeiModel.InputData(shanbeiParams,preREData,historyRData)
                .InitialMoistureContentCalculation()
                .RunoffYieldCalculation_UnevenInfiltration()
                .ConfluenceCalculation();
        //洪水过程对比
        preData=new double[preREData.length-L];
        hisData=new double[preREData.length-L];
        for (int i = 0; i < historyFData.length-L; i++) {
            hisData[i]= (double) historyFData[i+L][1];
        }
        for (int i = 0; i < shanbeiModel.Q.length-L; i++) {
            preData[i]= shanbeiModel.Q[i+L]+baseAve;
        }

        List<CalibrationFlow> flowList = new ArrayList<>();
        for (int i = 0; i < hisData.length; i++) {
            CalibrationFlow flow = new CalibrationFlow();
            flow.setTime((Date) preREData[i][0]);
            flow.setHistoryFlow(hisData[i]);
            flow.setPreParamFlow((Double) preParamFlow[i][1]);
            flow.setNewParamFlow(preData[i]);
            flowList.add(flow);
        }

        Double error = qualifyRate(hisData,preData);
        ShanbeiParam shanbeiParam = new ShanbeiParam();
        shanbeiParam.setArea(Area);
        shanbeiParam.setFB(param.getFB());
        shanbeiParam.setWM(param.getWM());
        shanbeiParam.setKC(param.getKC());
        shanbeiParam.setFC(param.getFC());
        shanbeiParam.setFM(param.getFM());
        shanbeiParam.setK(param.getK());
        shanbeiParam.setB(param.getB());
        shanbeiParam.setCS(param.getCS());
        shanbeiParam.setL(param.getL());
        shanbeiParam.setQC(error);

//        Object[][] flowData =new Object[hisData.length][2];
//        for (int i = 0; i < hisData.length; i++) {
//            flowData[i][0]=hisData[i];
//            flowData[i][1]=preData[i];
//        }
//        Object[][] paramResult = new Object[10][2];
//        paramResult[0][0] = Area;
//        paramResult[0][1] = error;
//        paramResult[1][0] = param.getFB();//FB
//        paramResult[2][0] = param.getWM();//WM
//        paramResult[3][0] = param.getKC();//KC
//        paramResult[4][0] = param.getFC();//FC
//        paramResult[5][0] = param.getFM();//FM
//        paramResult[6][0] = param.getK();//K
//        paramResult[7][0] = param.getB();//B
//        paramResult[8][0] = param.getCS();//CS
//        paramResult[9][0] = param.getL();//L
        result.setFlowList(flowList);
        result.setParam(shanbeiParam);
        return result;
    }

    /**
     * 以真实值的20%为许可误差计算合格率
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
}
