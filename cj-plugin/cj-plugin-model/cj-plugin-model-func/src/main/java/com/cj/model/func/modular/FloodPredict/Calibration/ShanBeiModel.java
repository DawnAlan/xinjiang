package com.cj.model.func.modular.FloodPredict.Calibration;

import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.model.TouTunHe;
import com.cj.model.func.modular.FloodPredict.utils.InputUtils;

import java.util.Arrays;

public class ShanBeiModel {

    /// 流域面积 单位平方公里
    double Area;

    /// 不透水面积的比例，透水面积比例为1-FB
    double FB;

    /// 初始土壤含水量W0,mm
    double W0;

    ///  张力水蓄水容量，或最大蓄水量 60-80mm
    double WM;

    /// 蒸散发折减系数 KC 为流域蒸散发能力与实测水面蒸发器实测值之比, 由于干旱地区资料等方面的原因，在实际模拟计算中 KC 值往往变化很大，最后须经调试后确定，必要时可分月份优选
    double KC;

//     流域土壤初始下渗率,相当于土壤干燥时的下渗率 1-2 mm/min,60-120 mm/h
    double f0;

    /// 流域土壤稳定下渗率 0.3-0.5 mm/min,18-30 mm/h
    double fc;

    /// 流域土壤最大单点下渗率 1-2 mm/min,60-120 mm/h
    double fm;

    /// 霍尔顿下渗曲线方程中的系数  0.04~0.05/min， 可取0.05/min, 3/h。
    double K;

    /// 参数 B反映下渗能力在透水面积上的分布特性。 B值取决于流域的土壤结构。
    double B;

    /// CS 为地面径流消退系数
    double CS;

    /// L为汇流滞时（时段数）
    int L = 10;

    //前期降雨为0的时段数量
    int zero = 0;
    /// 计算初始土壤含水量时，用到的前期天数  >20d
    int PreImpactdays = 20;

    /// 时段长度 单位（min）2~5min  可以取5min, 实际数据只有h的，以h来做
    int PeriodLength = 1;
    /// 预报期内的时段数
    int NumPeriod;

    /// 各时段蒸发量 单位：mm/h
    double[] E;

    /// 各时段降雨强度 单位：mm/h
    double[] P;

    /// 土壤含水量W,各时段有一个值,mm
    double[] W;

    /// 流域土壤下渗率，各时段有一个值,mm/h
    double[] f;

    /// 霍顿曲线中，对应流域土壤下渗率和含水量的那个时间系列,h
    double[] Time;

    /// 不透水层产流量,mm
    double[] R1;

    /// 透水层产流量,mm
    double[] R2;

    /// 总的产流量,mm
    double[] R;

    /// 为入流过程(m3/s) 汇流计算中用到
    double[] I;

    /// 为出流过程(m3/s) 汇流计算中用到
    public double[] Q;

    /// 计算初始土壤含水量时，用到的日雨量 mm
    double[] PreImpact_P;

    InputUtils inputUtils = new InputUtils();
    public ShanBeiModel InputData(ShanbeiParam shanbeiParam, Object[][] input, Object[][] predata)  {
        Area = shanbeiParam.getArea();
        FB = shanbeiParam.getFB(); //不透水面积的比例，透水面积比例为1-FB
        WM = shanbeiParam.getWM(); //张力水蓄水容量，或最大蓄水量 60-80mm
        KC = shanbeiParam.getKC(); //蒸散发折减系数 KC
        fc = shanbeiParam.getFC(); //流域土壤稳定下渗率 0.3-0.5 mm/min
        fm = shanbeiParam.getFM(); //流域土壤最大下渗率 1-2 mm/min
        K = shanbeiParam.getK(); //霍尔顿下渗曲线方程中的土质系数 0.04~0.05/min
        B = shanbeiParam.getB();  //B反映下渗能力在透水面积上的分布特性
        CS = shanbeiParam.getCS(); //CS 为地面径流消退系数


        for (int i = 0; i < input.length; i++) {
            if (Double.parseDouble(input[i][2].toString())==0.0){
                zero++;
            }else {
                break;
            }
        }
        NumPeriod = input.length-zero;


        E = new double[NumPeriod]; // 输入数据
        P = new double[NumPeriod]; // 输入数据
        W = new double[NumPeriod + 1]; // 计算结果、输出数据,一般说时段初、时段末的W，所以+1
        f = new double[NumPeriod + 1]; // 计算结果、输出数据,一般说时段初、时段末的f，所以+1
        Time = new double[NumPeriod + 1]; // 计算结果（时间点）、输出数据
        R1 = new double[NumPeriod]; // 中间结果，输出数据
        R2 = new double[NumPeriod]; // 中间结果，输出数据
        R = new double[NumPeriod]; // 中间结果，输出数据
        I = new double[NumPeriod]; // 中间结果，输入数据
        Q = new double[NumPeriod]; // 计算结果、输出数据
        PreImpact_P = new double[PreImpactdays]; // 输入数据

        for (int i = 0; i < NumPeriod; i++) {
            E[i] = Double.parseDouble(input[i+zero][1].toString());
            P[i] = Double.parseDouble(input[i+zero][2].toString());
            W[i] = 0;
            f[i] = 0;
            I[i] = 0;
            Q[i] = 0;
        }

        for (int i = 0; i < PreImpactdays; i++) {
            PreImpact_P[i] = Double.parseDouble(predata[i][1].toString());
        }
        return this;
    }

    /// 土壤初始含水量计算
    public ShanBeiModel InitialMoistureContentCalculation() {
        // 计算初始含水量W0

        double Wt_1 = 0; // 前一天的土壤含水量,最开始的时候（20天前）假定其为0；
        double Wt = 0;// 当前天的土壤含水量
        KC = 1.0;
        for (int j = 0; j < PreImpactdays; j++) {
            Wt = PreImpact_P[j] + KC * Wt_1;
            if (Wt > WM) {
                Wt = WM;
            }
            Wt_1 = Wt;
        }

        W0 = Wt;

//        W0 = 10;//这里是直接定义的初始含水量
        return this;
    }

    /// 基于陕北模型的长流计算 假定流域各点下渗能力一致
    public ShanBeiModel RunoffYieldCalculation_UniformInfiltration() {
        // 初始假设W，来推求下渗过程和蓄水过程

        // 已知初始含水量W0,推求其在霍尔顿下渗曲线方程中的对应点（W[0], f[0], Time[0]）
        double TempW = 0;
        double Tempf = 0;
        double BeginTime = 0;
        double Tempt = W0 / fc;
        W[0] = W0;

        TempW = fc * Tempt + (1 - Math.exp(-K * Tempt)) * (fm - fc) / K;
        if (TempW > WM) {
            TempW = WM;
        }
        while (Math.abs(TempW - W0) > 0.01) {
            Tempf = fm - K * (TempW - fc * Tempt);
            if (Tempf > fm) {
                Tempf = fm;
            }
            //Tempt = Tempt + Math.Abs(TempW - W0) / Tempf;
            Tempt = Tempt + (W0 - TempW) / Tempf;// 假定霍顿曲线规律是时间越长，土壤含水越大、下渗率越小
            TempW = fc * Tempt + (1 - Math.exp(-K * Tempt)) * (fm - fc) / K;
            if (TempW > WM) {
                TempW = WM;
            }

        }
        f[0] = Tempf;
        Time[0] = Tempt;
        BeginTime = Tempt;

        // 所得结果单位是mm,mm/h,h
        //产流量计算

        for (int j = 0; j < NumPeriod; j++) {
            double Ave_f = 0;// 时段平均下渗率

            R1[j] = P[j] - E[j];
            if (R1[j] < 0) {
                R1[j] = 0;
            }

            Time[j + 1] = Time[j] + PeriodLength;


            double TempW2 = TempW; // 假定一个时段末的土壤含水量

            //一次计算
            //f[j + 1] = fm - K * (TempW2 - fc * Time[j + 1]);//用这个算f越算越大
            f[j + 1] = fc + (fm - fc) * Math.exp(-K * Time[j + 1]);
            if (f[j + 1] > fm) {
                f[j + 1] = fm;
            }

            Ave_f = (f[j] + f[j + 1]) / 2;
            if (P[j] <= Ave_f) {
                R2[j] = 0;
                W[j + 1] = W[j] + P[j] * PeriodLength - E[j] * PeriodLength;
                if (W[j + 1] > WM)// 蓄水达到上限
                {
                    R2[j] = R2[j] + W[j + 1] - WM;
                    W[j + 1] = WM;
                }
            } else {
                R2[j] = (P[j] - Ave_f) * PeriodLength;
                W[j + 1] = W[j] + Ave_f * PeriodLength - E[j] * PeriodLength;
                if (W[j + 1] > WM)// 蓄水达到上限
                {
                    R2[j] = R2[j] + W[j + 1] - WM;
                    W[j + 1] = WM;
                }
            }

            while (Math.abs(TempW2 - W[j + 1]) > 0.01) {
                TempW2 = TempW2 - (TempW2 - W[j + 1]) / 10;

                //再次计算

//                f[j + 1] = fm - K * (TempW2 - fc * Time[j + 1]);//用这个算f越算越大
                f[j + 1] = fc + (fm - fc) * Math.exp(-K * Time[j + 1]);

                if (f[j + 1] > fm) {
                    f[j + 1] = fm;
                }
                Ave_f = (f[j] + f[j + 1]) / 2;

                if (P[j] <= Ave_f) {
                    R2[j] = 0;
                    W[j + 1] = W[j] + P[j] * PeriodLength - E[j] * PeriodLength;

                    if (W[j + 1] > WM)// 蓄水达到上限
                    {
                        R2[j] = R2[j] + W[j + 1] - WM;
                        W[j + 1] = WM;
                    }

                } else {
                    R2[j] = (P[j] - Ave_f) * PeriodLength;
                    W[j + 1] = W[j] + Ave_f * PeriodLength - E[j] * PeriodLength;

                    if (W[j + 1] > WM)// 蓄水达到上限
                    {
                        R2[j] = R2[j] + W[j + 1] - WM;
                        W[j + 1] = WM;
                    }
                }

            }
        }

        for (int j = 0; j < NumPeriod; j++) {
            Time[j] = Time[j] - BeginTime;
        }


        //流域总产流量计算
        for (int j = 0; j < NumPeriod; j++) {
            R[j] = R1[j] * FB + R2[j] * (1 - FB);// 单位mm/h
            I[j] = R1[j] / 1000 / 3600 * FB * Area * 1000000 + R2[j] / 1000 / 3600 * (1 - FB) * Area * 1000000;//  Area单位是km2
        }
        return this;
    }


    // 前一种不考虑流域下渗能力分布不均时，由于流域面积很大，降雨与下渗前度的大小关系对结果的影响很大，有时候没径流，有时候产生很大径流
    // 还有就是下渗曲线对结果的影响很大，几个参数稍微调整都会对结果产生较大的影响。尤其是K
    // 初始假设W，来推求下渗过程和蓄水过程  并考虑流域不同点下渗能力的不同
    // 已知初始含水量W0,推求其在霍尔顿下渗曲线方程中的对应点（W[0], f[0], Time[0]）
    public ShanBeiModel RunoffYieldCalculation_UnevenInfiltration() {
        double TempW = 0;
        double Tempf = 0;
        double BeginTime = 0;
        double Tempt = W0 / fm;
        W[0] = W0;

        TempW = fc * Tempt + (1 - Math.exp(-K * Tempt)) * (fm - fc) / K;
        if (TempW > WM) {
            TempW = WM;
        }
        while (Math.abs(TempW - W0) > 0.01) {
            Tempf = fm - K * (TempW - fc * Tempt);// 此处计算f的上下两种方法又没有区别了
            //Tempf = fc + (fm - fc) * Math.Exp(-K * Tempt);
            if (Tempf > fm) {
                Tempf = fm;
            }
            //Tempt = Tempt + Math.Abs(TempW - W0) / Tempf;
            //Tempt = Tempt + (W0 - TempW) / Tempf;// 假定霍顿曲线规律是时间越长，土壤含水越大、下渗率越小
            Tempt = Tempt + (W0 - TempW) / Tempf / 10;// 假定霍顿曲线规律是时间越长，土壤含水越大、下渗率越小
            TempW = fc * Tempt + (1 - Math.exp(-K * Tempt)) * (fm - fc) / K;
            if (TempW > WM) {
                TempW = WM;
            }

        }
        f[0] = Tempf;
        Time[0] = Tempt;
        BeginTime = Tempt;

        // 所得结果单位是mm,mm/h,h
        // 产流量计算

        for (int j = 0; j < NumPeriod; j++) {
            double Ave_f = 0;// 时段平均下渗率

            R1[j] = P[j] - E[j];
            if (R1[j] < 0) {
                R1[j] = 0;
            }

            Time[j + 1] = Time[j] + PeriodLength;


            double TempW2 = TempW; // 假定一个时段末的土壤含水量

            //一次计算
//            f[j + 1] = fm - K * (TempW2 - fc * Time[j + 1]);//用这个算f越算越大
            f[j + 1] = fc + (fm - fc) * Math.exp(-K * Time[j + 1]);

            if (f[j + 1] > fm) {
                f[j + 1] = fm;
            }

            Ave_f = (f[j] + f[j + 1]) / 2;// 时段平均下渗量
//            Ave_f = fm / (1 + B);// 流域的平均下渗量
            if (P[j] - E[j] < fm)// 部分流域上产流
            {
                if (P[j] - E[j] < 0) {
                    R2[j] = P[j] - E[j] - Ave_f * (1 - Math.pow(1 - (0) / fm, B + 1));
                } else {
                    double a = Ave_f * (1 - Math.pow(1 - (P[j] - E[j]) / fm, B + 1));
                    R2[j] = P[j] - E[j] - Ave_f * (1 - Math.pow(1 - (P[j] - E[j]) / fm, B + 1));
                }
                if (j>260){
                    int a = 0;
                }
                if (R2[j] < 0) {
                    R2[j] = 0;
                }
                W[j + 1] = W[j] + P[j] * PeriodLength - E[j] * PeriodLength - R2[j] * PeriodLength;
                if (W[j + 1] >= WM)// 蓄水达到上限
                {
                    R2[j] = R2[j] + W[j + 1] - WM;
                    W[j + 1] = WM;
                }
            } else // 全流域上产流
            {
                R2[j] = P[j] - E[j] - Ave_f;
                if (R2[j] < 0) {
                    R2[j] = 0;
                }

                W[j + 1] = W[j] + P[j] * PeriodLength - E[j] * PeriodLength - R2[j] * PeriodLength;
                if (W[j + 1] >= WM)// 蓄水达到上限
                {
                    R2[j] = R2[j] + W[j + 1] - WM;
                    W[j + 1] = WM;
                }

            }


            while (Math.abs(TempW2 - W[j + 1]) > 0.01) {
                TempW2 = TempW2 - (TempW2 - W[j + 1]) / 10;

                //再次计算
//                f[j + 1] = fm - K * (TempW2 - fc * Time[j + 1]);//用这个算f越算越大
                f[j + 1] = fc + (fm - fc) * Math.exp(-K * Time[j + 1]);
                if (f[j + 1] > fm) {
                    f[j + 1] = fm;
                }

                Ave_f = (f[j] + f[j + 1]) / 2;// 时段平均下渗量
//                Ave_f = fm / (1 + B);// 流域的平均下渗量

                if (P[j] - E[j] < fm)// 部分流域上产流
                {
                    if (P[j] - E[j] < 0) {
                        R2[j] = P[j] - E[j] - Ave_f * (1 - Math.pow(1 - (0) / fm, B + 1));
                    } else {
                        R2[j] = P[j] - E[j] - Ave_f * (1 - Math.pow(1 - (P[j] - E[j]) / fm, B + 1));
                    }
                    if (R2[j] < 0) {
                        R2[j] = 0;
                    }
                    W[j + 1] = W[j] + P[j] * PeriodLength - E[j] * PeriodLength - R2[j] * PeriodLength;
                    if (W[j + 1] >= WM)// 蓄水达到上限
                    {
                        R2[j] = R2[j] + W[j + 1] - WM;
                        W[j + 1] = WM;
                    }
                } else // 全流域上产流
                {
                    R2[j] = P[j] - E[j] - Ave_f;
                    if (R2[j] < 0) {
                        R2[j] = 0;
                    }
                    W[j + 1] = W[j] + P[j] * PeriodLength - E[j] * PeriodLength - R2[j] * PeriodLength;
                    if (W[j + 1] >= WM)// 蓄水达到上限
                    {
                        R2[j] = R2[j] + W[j + 1] - WM;
                        W[j + 1] = WM;
                    }

                }
            }
        }

        for (int j = 0; j < NumPeriod; j++) {
            Time[j] = Time[j] - BeginTime;
        }


        //流域总产流量计算

        for (int j = 0; j < NumPeriod; j++) {
            R[j] = R1[j] * FB + R2[j] * (1 - FB);// 单位mm/h
            I[j] = R1[j] / 1000 / 3600 * FB * Area * 1000000 + R2[j] / 1000 / 3600 * (1 - FB) * Area * 1000000;//  Area单位是km2
        }
        return this;
    }


    // 汇流计算
    public ShanBeiModel ConfluenceCalculation() {
        int hours = inputUtils.beforeHours;
        for (int j = 0; j < NumPeriod; j++) {
            if (P[j] >= 10){
                L = 1;
            } else if (P[j] >= 5 && P[j] < 10) {
                L = 3;
            } else if (P[j] >= 1 && P[j] < 5) {
                L = 4;
            } else if (P[j] > 0 && P[j] < 1) {
                L = 5;
            }
            if (j < L) {
                if (j == 0) {
                    Q[j] = 0;
                } else {
                    Q[j] = CS * Q[j - 1];
                }
            } else {
                Q[j] = CS * Q[j - 1] + (1 - CS) * I[j - L];
            }
        }
        double[] qResult = new double[NumPeriod + zero];
        System.arraycopy(Q,0,qResult,zero,NumPeriod);
        Q = new double[zero + NumPeriod - hours];
        System.arraycopy(qResult,hours,Q,0,Q.length);
        return this;
    }

}
