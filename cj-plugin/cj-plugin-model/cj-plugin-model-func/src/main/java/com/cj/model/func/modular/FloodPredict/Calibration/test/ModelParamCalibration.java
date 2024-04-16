package com.cj.model.func.modular.FloodPredict.Calibration.test;


import com.cj.model.func.modular.FloodPredict.Calibration.*;
import com.cj.model.func.modular.FloodPredict.Calibration.pso.Domain;
import com.cj.model.func.modular.FloodPredict.Calibration.pso.Interval;
import com.cj.model.func.modular.FloodPredict.Calibration.pso.PSO;
import com.cj.model.func.modular.FloodPredict.Calibration.pso.PSOResult;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;

import static com.cj.model.func.modular.FloodPredict.Calibration.pso.ParameterValidation.NashSutcliffeEfficiency;


/**
 * 粒子群优化算法示例：
 * 以纳什效率系数为评价函数率定陕北模型9个参数
 *
 */
public class ModelParamCalibration {
    /// 流域面积 单位平方公里
    static double Area;

    /// 计算初始土壤含水量时，用到的前期天数  >20d
    static int PreImpactdays;

    /// 时段长度 单位（min）2~5min  可以取5min, 实际数据只有h的，以h来做
    static int PeriodLength;
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

    public static void main(String[] args) throws IOException, InvalidFormatException {

        Double area = 1174.0;
        Double preFlow = 5.9;
        Double preRain = 38.63;
        String sheetName = "06-23~06-27";
//        Object[][]Flood= ShanBeiCalibration(area,sheetName);
//        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\率定结果.xlsx",sheetName+"三号桥", Flood);
        double[] shanbeiParams=new double[12];
        shanbeiParams[0]=area;//Area
        shanbeiParams[1]=0.008;//FB
        shanbeiParams[2]=102;//WM张力水蓄水容量，或最大蓄水量 60-80mm
        shanbeiParams[3]=1;//蒸散发折减系数 KC
        shanbeiParams[4]=32;//fc流域土壤稳定下渗率 0.3-0.5 mm/min
        shanbeiParams[5]=60;//fm流域土壤最大下渗率 1-2 mm/min
        shanbeiParams[6]=0.022;//K霍尔顿下渗曲线方程
        shanbeiParams[7]=0.3;//B反映下渗能力在透水面积上的分布特性
        shanbeiParams[8]=0.966;//CS 为地面径流消退系数
        shanbeiParams[9]=4;//L汇流滞时（时段数）
        shanbeiParams[10]=preRain;//计算初始土壤含水量时，用到的前期天数
        shanbeiParams[11]=1;
        int L = (int) shanbeiParams[9];

        preREData = ExcelTool.readExcel("D:\\204\\2.头屯河\\径流预报数据文件\\陕北-DATA-2023.xlsx",sheetName+"蒸发降雨");
        historyRData = ExcelTool.readExcel("D:\\204\\2.头屯河\\径流预报数据文件\\陕北-DATA-2023.xlsx",sheetName+"前期雨量");
        historyFData = ExcelTool.readExcel("D:\\204\\2.头屯河\\径流预报数据文件\\陕北-DATA-2023.xlsx",sheetName+"前期径流");
//        洪水过程推演
        shanbeiModel.InputData(shanbeiParams,preREData,historyRData);
//        shanbeiModel.InitialMoistureContentCalculation();
        shanbeiModel.RunoffYieldCalculation_UnevenInfiltration();
//        shanbeiModel.RunoffYieldCalculation_UniformInfiltration();
        shanbeiModel.ConfluenceCalculation();
        //洪水过程对比
        preData=new double[preREData.length-L];
        hisData=new double[preREData.length-L];
        for (int i = 0; i < preData.length; i++) {
            preData[i]=shanbeiModel.Q[i+L]+preFlow;
        }
        for (int i = 0; i < preData.length; i++) {
            hisData[i]=(double) historyFData[i+L][1];
        }
        Object[][] results =new Object[hisData.length][2];
        for (int i = 0; i < hisData.length; i++) {
            results[i][0]=hisData[i];
            results[i][1]=preData[i];
        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\手动率定结果-2023.xlsx",sheetName,results);
    }
    public static Object[][]ShanBeiCalibration(Double data,String sheetName) throws IOException {
        Area=data;
        PreImpactdays=20;
        PeriodLength=1;
        preREData = ExcelTool.readExcel("D:\\204\\2.头屯河\\径流预报数据文件\\陕北-DATA.xlsx",sheetName+"蒸发降雨");
        historyRData = ExcelTool.readExcel("D:\\204\\2.头屯河\\径流预报数据文件\\陕北-DATA.xlsx",sheetName+"前期雨量");
        historyFData = ExcelTool.readExcel("D:\\204\\2.头屯河\\径流预报数据文件\\陕北-DATA.xlsx",sheetName+"前期径流");

        // 定义模型各参数的有效范围
        Interval[] regionIntervals = new Interval[]{
                new Interval(0, 0.3),//FB
                new Interval(60, 80),//WM
                new Interval(0.68, 1),//KC
                new Interval(10, 30),//FC
                new Interval(60, 120),//FM
                new Interval(0, 10),//K
                new Interval(0.3, 0.3),//B
                new Interval(0.8, 0.995),//CS
                new Interval(1, 5),//L
                //前期径流
                new Interval(10, 10),
        };

        // 创建PSO算法的问题域
        Domain domain = new Domain(regionIntervals, ModelParamCalibration::Evaluate, 1);

        // 创建PSO算法实例
        PSO pso = new PSO(domain);

        // 运行算法并存储结果
        PSOResult result = pso.Execute(100, 1000);

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
        double[] shanbeiParams=new double[12];

        shanbeiParams[0]=Area;//流域面积
        shanbeiParams[1]=params[0];//不透水面积的比例，透水面积比例为1-FB
        shanbeiParams[2]=params[1];//张力水蓄水容量，或最大蓄水量 60-80mm
        shanbeiParams[3]=params[2];//蒸散发折减系数 KC 为流
        shanbeiParams[4]=params[3];//流域土壤稳定下渗率 0.3-0.5 mm/min
        shanbeiParams[5]=params[4];//流域土壤最大下渗率 1-2 mm/min
        shanbeiParams[6]=params[5];//K,霍尔顿下渗曲线方程中的土质系数 0.04~0.05/min
        shanbeiParams[7]=params[6];//B反映下渗能力在透水面积上的分布特性 1~5
        shanbeiParams[8]=params[7];//CS 为地面径流消退系数 0.1~1
        shanbeiParams[9]=params[8];//L为汇流滞时（时段数）
        shanbeiParams[10]=PreImpactdays;//计算初始土壤含水量时，用到的前期天数 20d
        shanbeiParams[11]=PeriodLength;//1h
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
