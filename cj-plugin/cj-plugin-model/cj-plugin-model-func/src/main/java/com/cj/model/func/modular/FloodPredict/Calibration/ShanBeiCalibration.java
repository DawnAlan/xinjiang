package com.cj.model.func.modular.FloodPredict.Calibration;

import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParamNew;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.entity.calibrationParam;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.Calibration.ParameterValidation.NashSutcliffeEfficiency;
import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.IrrigateRainIntegration;
import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.LzzRainIntegration;


public class ShanBeiCalibration {
    /// 流域面积 单位平方公里
    static double Area;
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

    static ShanBeiModel shanbeiModel = new ShanBeiModel();



    public static List<Object[][]> calibration(calibrationParam inputData) throws ParseException, IOException, InvalidFormatException {
        ForcastInputParamNew forcastInputParamNew = new ForcastInputParamNew();
        forcastInputParamNew.setIrrigatedHydrologyParam(inputData.getIrrigatedHydrologyParam());
        forcastInputParamNew.setLzzHydrologyParam(inputData.getLzzHydrologyParam());
        forcastInputParamNew.setModelType(3);
        List<List<PredictInputData>> lzzIntegration = LzzRainIntegration(forcastInputParamNew);//整合雨量站数据转为模型所需类型
        List<List<PredictInputData>> qjIntegration = IrrigateRainIntegration(forcastInputParamNew);//小时雨量和日尺度雨量
        if (!inputData.getModelType()){

        }
        return null;
    }
    /**
     * 需要前期降水、温度和径流，返回率定的参数和率定好的径流
     * @param
     * @return
     * @throws IOException
     */
    public static Object[][]oneStationCalibration(List<Object[][]> inputData) {
        Object[][] area = inputData.get(0);//面积
        Area= (double) area[0][0];
        preREData = inputData.get(1);//蒸发降雨
        historyRData = inputData.get(2);//前20天雨量
        historyFData = inputData.get(3);//前期径流

        // 定义模型各参数的有效范围
        Interval[] regionIntervals = new Interval[]{
                new Interval(0.1, 1),
                new Interval(80, 80),
                new Interval(1, 1),
                new Interval(30, 30),
                new Interval(60, 60),
                new Interval(1, 3),
                new Interval(1, 5),
                new Interval(0.1, 1),
                new Interval(1, 5),
                //前期径流
                new Interval(1, 10),
        };

        // 创建PSO算法的问题域
        Domain domain = new Domain(regionIntervals, ShanBeiCalibration::Evaluate, 1);

        // 创建PSO算法实例
        PSO pso = new PSO(domain);

        // 运行算法并存储结果
        PSOResult result = pso.Execute(1000, 300);

        // 输出结果
        System.out.println(result);

        Object[][] results =new Object[hisData.length][2];
        for (int i = 0; i < hisData.length; i++) {
            results[i][0]=hisData[i];
            results[i][1]=preData[i];
        }
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
        int L = (int)shanbeiParams[9];
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
            preData[i]= shanbeiModel.Q[i+L]+params[9];
        }
        return NashSutcliffeEfficiency(hisData, preData);
    }
}
