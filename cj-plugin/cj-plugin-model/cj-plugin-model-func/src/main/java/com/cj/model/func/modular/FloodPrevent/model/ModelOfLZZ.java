package com.cj.model.func.modular.FloodPrevent.model;


import com.cj.model.func.modular.FloodPrevent.entity.DataFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



public class ModelOfLZZ {

    int T_Delta;
    double H_begin;
    double H_end;
    double Step;

    double DeadLevel;
    double LimitLevel;
    double NormalLevel;
    double HeightLevel;
    double DesignLevel;
    double ProofLevel;

    double DeadVolume;
    double LimitVolume;
    double NormalVolume;
    double HeightVolume;
    double DesignVolume;
    double ProofVolume;

    double[][] LV_Curve;
    double[][] LQ_Curve1;
    double[][] LQ_Curve2;

    double[] LimitLevels;
    List<Double> limits = new ArrayList<>();

    @Getter
    List<Date> Time =new ArrayList<>();
    List<Double> Q_Input =new ArrayList<>();
    List<Double> Q_Interval =new ArrayList<>();
    List<Double> MaxQ =new ArrayList<>();
    List<Double> MinQ =new ArrayList<>();

    int choice;
    int coefficient =10000 ;

    public ModelOfLZZ(){};
    public ModelOfLZZ(Object[][] pre,int delta) {
        for (int i = 0; i < pre.length; i++) {
            Date t = (Date) pre[i][1];
            Time.add(t);
            Q_Input.add((double)pre[i][2]);
            MaxQ.add(180.0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(t);
            int month = calendar.get(Calendar.MONTH)+1;
            if(month>=4&&month<=9){
                MinQ.add(1.48);
            }
            else{
                MinQ.add(0.74);
            }

        }
        H_begin=1394.5;
        T_Delta=delta;

        LV_Curve= new double[][]{{1326, 1330, 1335, 1340, 1345, 1350, 1355, 1360, 1365, 1370, 1375, 1380, 1385, 1390, 1395, 1400, 1405, 1410, 1415, 1420,},
                {0,	3,	28,	92,	221,	417,	680,	1022,	1464,	2018,	2686,	3468,	4378,	5440,	6656,	8021,	9532,	11195,	13022,	15028,}};
        LQ_Curve1= new double[][]{{1335,	1336,	1340,	1345,	1355,	1365,	1370,	1380,	1380.5,	1381,	1381.5,	1382,	1382.5,	1383,	1383.5,	1384,	1384.5,	1385,	1385.5,	1386,	1386.5,	1387,	1387.5,	1388,	1388.5,	1389,	1389.5,	1390,	1390.5,	1391,	1391.5,	1392,	1392.5,	1393,	1394,	1394.1,	1394.2,	1394.3,	1394.4,	1394.5,	1394.6,	1394.7,	1394.8,	1394.9,	1395,	1395.1,	1395.2,	1395.3,	1395.4,	1395.5,	1395.6,	1395.7,	1395.8,	1395.9,	1396,	1396.1,	1396.2,	1396.3,	1396.4,	1396.44,	1396.5,	1396.6,	1396.7,	1396.8,	1396.9,	1397,	1397.1,	1397.2,	1397.21,	1397.22,	1397.23,	1397.24,	1397.25,	1397.26,	1397.27,	1397.28,	1397.29,	1397.3,	1397.31,	1397.32,	1397.33,	1397.34,	1397.35,	1397.36,	1397.37,	1397.38,	1397.39,	1397.41,	1397.5,	1397.6,	1397.63,	1398,},
                {0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	0,	1.3,	2.61,	3.91,	5.22,	6.52,	7.83,	9.13,	10.43,	11.74,	13.04,	14.35,	15.65,	16.96,	18.26,	19.57,	20.87,	22.17,	23.48,	24.78,	27.39,	27.65,	27.91,	28.17,	28.43,	28.7,	28.96,	29.22,	29.48,	29.74,	30,	36.6,	43.19,	49.79,	56.39,	62.99,	69.58,	76.18,	82.78,	89.38,	95.97,	102.57,	109.17,	115.76,	122.36,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	125,	246.6,	250,	253,	256,	300,}};
        LQ_Curve2= new double[][]{{1335,	1336,	1340,	1345,	1355,	1365,	1370,	1380,	1380.5,	1381,	1381.5,	1382,	1382.5,	1383,	1383.5,	1384,	1384.5,	1385,	1385.5,	1386,	1386.5,	1387,	1387.5,	1388,	1388.5,	1389,	1389.5,	1390,	1390.5,	1391,	1391.5,	1392,	1392.5,	1393,	1394,	1394.1,	1394.2,	1394.3,	1394.4,	1394.5,	1394.6,	1394.7,	1394.8,	1394.9,	1395,	1395.1,	1395.2,	1395.3,	1395.4,	1395.5,	1395.6,	1395.7,	1395.8,	1395.9,	1396,	1396.1,	1396.2,	1396.3,	1396.4,	1396.44,	1396.5,	1396.6,	1396.7,	1396.8,	1396.9,	1397,	1397.1,	1397.2,	1397.21,	1397.22,	1397.23,	1397.24,	1397.25,	1397.26,	1397.27,	1397.28,	1397.29,	1397.3,	1397.31,	1397.32,	1397.33,	1397.34,	1397.35,	1397.36,	1397.37,	1397.38,	1397.39,	1397.41,	1397.5,	1397.6,	1397.63,},
                {0,	15,	50.55,	98.52,	154.94,	195.72,	213.21,	244.46,	245.92,	247.37,	248.81,	250.24,	251.66,	253.08,	254.49,	255.89,	257.28,	258.67,	260.05,	261.42,	262.78,	264.14,	265.49,	266.83,	268.17,	269.5,	270.82,	272.14,	273.45,	274.76,	276.05,	277.35,	278.63,	279.91,	282.45,	282.71,	282.96,	283.21,	283.47,	283.72,	283.97,	284.22,	284.47,	284.72,	284.98,	285.23,	285.48,	285.73,	285.98,	286.23,	286.48,	286.73,	286.98,	287.22,	287.47,	287.72,	287.97,	288.22,	288.47,	288.57,	288.71,	288.96,	289.21,	289.46,	289.7,	289.95,	290.2,	290.44,	290.47,	290.49,	290.52,	290.54,	290.57,	290.59,	290.62,	290.64,	290.67,	290.69,	290.71,	290.74,	290.76,	290.79,	290.81,	290.84,	290.86,	290.89,	290.91,	290.96,	291.18,	299.43,	304,}};
        this.DeadLevel  =1353.3;
        this.LimitLevel =1394.5;
        this.NormalLevel=1394.5;
        this.HeightLevel=1397.21;
        this.DesignLevel=1397.41;
        this.ProofLevel =1397.63;
        DeadVolume = GetV(DeadLevel);
        LimitVolume = GetV(LimitLevel);
        NormalVolume = GetV(NormalLevel);
        HeightVolume = GetV(HeightLevel);
        DesignVolume = GetV(DesignLevel);
        ProofVolume = GetV(ProofLevel);
        LimitLevels = new double[]{1394.5,1394.5,1394.5,1394.5,1394.5,1394.5,1394.5,1394.5,1394.5,1394.5,1394.5,1394.5};
        for (int i = 0; i < 12; i++) {
            limits.add(LimitLevels[i]);
        }

    }
    public ModelOfLZZ(ReqFloodPrevent reqFloodPrevent){
        List<DataFloodPrevent> data_FloodPrevent_all = reqFloodPrevent.getData().get("lzz");

        //时间戳、入库流量、生态流量、最大泄流、区间流量
        for (int i = 0; i < data_FloodPrevent_all.size(); i++) {
            DataFloodPrevent dataFloodPrevent = data_FloodPrevent_all.get(i);
            Time.add(dataFloodPrevent.getTime());
            Q_Input.add(BigDecimal.valueOf(dataFloodPrevent.getPre()).setScale(2, RoundingMode.UP).doubleValue());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataFloodPrevent.getTime());
            int month = calendar.get(Calendar.MONTH)+1;
            if(month>=4&&month<=9){
                MinQ.add(1.48);
            }
            else{
                MinQ.add(0.74);
            }
            MaxQ.add(180.0);
        }
        List<DataFloodPrevent> dataFloodPrevent2 = reqFloodPrevent.getData().get("lat");
        for (int i = 0; i < dataFloodPrevent2.size(); i++) {
            DataFloodPrevent dataFloodPrevent = data_FloodPrevent_all.get(i);
            Q_Interval.add(BigDecimal.valueOf(dataFloodPrevent.getPre()).setScale(2, RoundingMode.UP).doubleValue());
        }

        //时间间隔、初末水位、寻优精度
        T_Delta= data_FloodPrevent_all.get(0).getScale();
        H_begin= reqFloodPrevent.getH1_begin();
        H_end  = reqFloodPrevent.getH1_end();
        Step = reqFloodPrevent.getStep1();

        //库容曲线、泄流曲线
        List<Double> LV_l = new ArrayList<>();
        List<Double> LV_v = new ArrayList<>();
        List<Double> LQ1_l = new ArrayList<>();
        List<Double> LQ1_q = new ArrayList<>();
        List<Double> LQ2_l = new ArrayList<>();
        List<Double> LQ2_q = new ArrayList<>();
        for (int i = 0; i < reqFloodPrevent.getCurveParam().size(); i++) {
            CurveParam piece = reqFloodPrevent.getCurveParam().get(i);
            int ID = piece.getId();
            double level = piece.getLevel();
            double value = piece.getValue();
            switch (ID){
                case 100:
                    LV_l.add(level);
                    LV_v.add(value);
                    break;
                case 101:
                    LQ1_l.add(level);
                    LQ1_q.add(value);
                    break;
                case 102:
                    LQ2_l.add(level);
                    LQ2_q.add(value);
                    break;
            }
        }
        LV_Curve=new double[2][LV_l.size()];
        LQ_Curve1 = new double[2][LQ1_l.size()];
        LQ_Curve2 = new double[2][LQ2_l.size()];
        for (int i = 0; i < LV_Curve[0].length; i++) {
            LV_Curve[0][i]=LV_l.get(i);
            LV_Curve[1][i]=LV_v.get(i);
        }
        for (int i = 0; i < LQ_Curve1[0].length; i++) {
            LQ_Curve1[0][i]=LQ1_l.get(i);
            LQ_Curve1[1][i]=LQ1_q.get(i);
        }
        for (int i = 0; i < LQ_Curve2[0].length; i++) {
            LQ_Curve2[0][i]=LQ2_l.get(i);
            LQ_Curve2[1][i]=LQ2_q.get(i);
        }
        Arrays.sort(LV_Curve[0]);
        Arrays.sort(LV_Curve[1]);
        Arrays.sort(LQ_Curve1[0]);
        Arrays.sort(LQ_Curve1[1]);
        Arrays.sort(LQ_Curve2[0]);
        Arrays.sort(LQ_Curve2[1]);

        //特征水位、特征库容
        this.DeadLevel  =1353.3;
        this.LimitLevel =1394.5;
        this.NormalLevel=1394.5;
        this.HeightLevel=1397.21;
        this.DesignLevel=1397.41;
        this.ProofLevel =1397.63;
        DeadVolume = GetV(DeadLevel);
        LimitVolume = GetV(LimitLevel);
        NormalVolume = GetV(NormalLevel);
        HeightVolume = GetV(HeightLevel);
        DesignVolume = GetV(DesignLevel);
        ProofVolume = GetV(ProofLevel);

        LimitLevels= reqFloodPrevent.getLimitLevels_lzz();
        for (int i = 0; i < 12; i++) {
            limits.add(LimitLevels[i]);
        }
    }

    //楼庄子常规调度
    public List<Option> Calculate_S1(){
        //楼庄子
        //来水小于750时，为标准内洪水：
        //（1）当库水位达到1394.5m，且库水位继续上涨时，控制溢洪洞下泄流量不超过125m3/s，持续泄洪直至水位回落到汛限水位。
        //（2）当库水位达到1397.21m，且库水位继续上涨时，全开溢洪洞下泄洪水，同时根据入库流量和库水位涨幅情况，控制泄洪冲沙兼导流洞下泄流量，尽量保证下泄洪水在安全范围内，持续泄洪直至水位回落到汛限水位。
        //（3）当库水位达到1397.41m，且库水位继续上涨时，全开溢洪洞、泄洪冲沙兼导流洞下泄洪水，持续泄洪直至水位回落到汛限水位，力求大坝安全。
        //来水超过750时，为超标准洪水，泄洪冲沙兼导流洞、溢洪洞自由下泄，力求大坝安全。
        List<Option> result = new ArrayList<>();
        for (int i = 0; i < Q_Input.size(); i++) {
            Option option = new Option();
            Date time = Time.get(i);
            double beginH;
            double endH;
            double Q_in = Q_Input.get(i);
            double minQ = MinQ.get(i);
            double Q_out;
            double Q_1;
            double Q_2;
            double Q_3;
            double V;
            double retain;
            UpdateLimitLevel(Time.get(i));

            //设定时段初水位
            if(i==0){
                beginH=H_begin;
            }
            else{
                beginH=result.get(i-1).getH2();
            }
            double[] Q =ConventionalCalculate(beginH,Q_in,minQ);
            Q_out=Q[0];
            Q_1=Q[1];
            Q_2=Q[2];
            Q_3=0;
            endH=OnceBalance1(beginH,Q_in,Q_out);
            V=GetV(endH);
            retain=Math.max(0,V-GetV(H_begin));

            double[] aa=getPercentage_lzz(V);
            double percent1=aa[0];
            double percent2=aa[1];

            option.setTime(time);
            option.setType("常规调度");
            option.setName("楼庄子");
            option.setH1(beginH);
            option.setH2(endH);
            option.setQIn(Q_in);
            option.setQOut(Q_out);
            option.setQ1(Q_1);
            option.setQ2(Q_2);
            option.setQ3(Q_3);
            option.setV(V);
            option.setRetain(retain);
            option.setPercentage1(percent1);
            option.setPercentage2(percent2);
            option.setLimits(limits);

            result.add(option);
        }

        return result;
    }
    /**
     * 常规调度计算流量
     */
    public double[] ConventionalCalculate(double level, double Q_Input, double MinQ){
        double[] Q = new double[4];

        //最大下泄能力
        double MaxQ;
        if(Q_Input<=750){
            if(level>=1397.41){
                MaxQ=GetQ1(level)+GetQ2(level);
            }
            else if (level>=1397.21) {
                MaxQ=Math.min(GetQ1(level)+GetQ2(level),125);
            }
            else if (level>=1394.5) {
                MaxQ=Math.min(GetQ1(level),125);
            }
            else{
                MaxQ=Math.min(GetQ1(level),125);
            }
        }
        else{
            MaxQ=GetQ1(level)+GetQ2(level);
        }
        //恢复至汛限水位所需下泄流量
        double Q_limit=(GetV(level)-LimitVolume)*coefficient/T_Delta+Q_Input;

        if(MinQ<=Q_limit&&Q_limit<=MaxQ){
            Q[0]=Q_limit;
            Q[1]=Math.min(Q[0],GetQ1(level));
        }
        else if(Q_limit<=MinQ){
            Q[0]=MinQ;
            Q[1]=MinQ;
        }else{
            Q[0]=MaxQ;
            Q[1]=GetQ1(level);
        }
        Q[2]=Q[0]-Q[1];
        Q[3]=0;
        return Q;
    }

    //最大调洪水位最小
    public List<Option> MinLevel(List<Option> Initial, String name){

        //先从常规调度的结果中获得初始解
        List<Option> Initial_op = new ArrayList<>();
        for (int i = 0; i < Initial.size(); i++) {
            if(Initial.get(i).getName().equals(name)){
                Option object = Initial.get(i);
                Option option = new Option();
                option.setType("最小拦蓄");
                option.setName(object.getName());
                option.setTime(object.getTime());
                option.setQIn(object.getQIn());
                option.setH1(object.getH1());
                option.setH2(object.getH2());
                option.setQOut(object.getQOut());
                Initial_op.add(option);
            }
        }

        //总入库、总出库、总生态流量
        double SumQ_in = 0;
        double SumQ_out= 0;
        double SumQ_eco= 0;
        for (int i = 0; i < Initial_op.size(); i++) {
            SumQ_in=SumQ_in+Initial_op.get(i).getQIn();
            SumQ_out=SumQ_out+Initial_op.get(i).getQOut();
            SumQ_eco=SumQ_eco+MinQ.get(i);
        }

        //时段入库、出库、初末水位、生态流量
        double QIn;
        double QOut;
        double H1;
        double H2;
        double Q_eco;

        double[] Limit;
        double Out_limit;
        double H2_limit;

        double decline =2;

        //更新总入库、总出库、总生态流量、平均出库
        SumQ_in = 0;
        SumQ_out= 0;
        SumQ_eco= 0;
        for (int i = 0; i < Initial_op.size(); i++) {
            SumQ_in=SumQ_in+Initial_op.get(i).getQIn();
            SumQ_out=SumQ_out+Initial_op.get(i).getQOut();
            SumQ_eco=SumQ_eco+MinQ.get(i);
        }

        //优化
        for (int i = 0; i < Initial_op.size(); i++) {
            QIn= Initial_op.get(i).getQIn();
            H1 = Initial_op.get(i).getH1();
            Q_eco=MinQ.get(i);
            Limit =H_LimitFront(H1,QIn,Q_eco);

            //保证后续时段生态流量
            Out_limit=SumQ_out-(SumQ_eco-MinQ.get(i));
            H2_limit=OnceBalance1(H1,QIn,Out_limit);

            //水位变化速率限制
            double[] H_delta = new double[2];
            H_delta[0] = H1- (double) T_Delta /3600/24*2;
            H_delta[1] = H1+ (double) T_Delta /3600/24*2;


            //最后一个时段
            if(i==Initial_op.size()-1){
                QOut=SumQ_out;
                H2=H_end;
                Initial_op.get(i).setH2(H2);
                Initial_op.get(i).setQOut(QOut);
            }
            else{
                H2 = Math.max(Math.max(Math.max(Limit[0],H2_limit),H_delta[0]),H_begin-decline);
                QOut=OnceBalance2(H1,QIn,H2);
                Initial_op.get(i).setH2(H2);
                Initial_op.get(i).setQOut(QOut);
                Initial_op.get(i+1).setH1(H2);
            }

            //剩余时段总出库、剩余时段总生态流量
            SumQ_out=SumQ_out-QOut;
            SumQ_eco=SumQ_eco-Q_eco;
        }

        //补全其他数据
        for (int i = 0; i < Initial_op.size(); i++) {
            Option option = Initial_op.get(i);
            QIn= option.getQIn();
            H1= option.getH1();
            H2= option.getH2();
            QOut = OnceBalance2(H1,QIn,H2);
            double Q1 = Math.min(QOut,GetQ1((H1+H2)/2));
            double Q2 = QOut-Q1;
            double Q3=0;
            double V=GetV(H2);
            double[] aa=getPercentage_lzz(V);
            double retain=Math.max(0,V-GetV(H_begin));
            double percent1=aa[0];
            double percent2=aa[1];
            option.setQOut(QOut);
            option.setQ1(Q1);
            option.setQ2(Q2);
            option.setQ3(Q3);
            option.setV(V);
            option.setRetain(retain);
            option.setPercentage1(percent1);
            option.setPercentage2(percent2);
            option.setLimits(limits);
        }
        return Initial_op;
    }

    //最大下泄流量最小
    public List<Option> MinDischarge(List<Option> Initial, String name){


        //先从常规调度的结果中获得初始解
        List<Option> Initial_op = new ArrayList<>();
        for (int i = 0; i < Initial.size(); i++) {
            if(Initial.get(i).getName().equals(name)){
                Option object = Initial.get(i);
                Option option = new Option();
                option.setType("最大削峰");
                option.setName(object.getName());
                option.setTime(object.getTime());
                option.setQIn(object.getQIn());
                option.setH1(object.getH1());
                option.setH2(object.getH2());
                option.setQOut(object.getQOut());
                Initial_op.add(option);
            }
        }

        //总入库、总出库、总生态流量、平均出库
        double SumQ_in = 0;
        double SumQ_out= 0;
        double SumQ_eco= 0;
        double Q_average;
        for (int i = 0; i < Initial_op.size(); i++) {
            SumQ_in=SumQ_in+Initial_op.get(i).getQIn();
            SumQ_out=SumQ_out+Initial_op.get(i).getQOut();
            SumQ_eco=SumQ_eco+MinQ.get(i);
        }

        //时段入库、出库、初末水位、生态流量、下泄能力、死水位流量
        double QIn;
        double QOut;
        double H1;
        double H2;
        double Q_eco;
        double Q_max;
        double Q_min;
        double Q_dead;
        double Q_proof;

        double Out_limit;

        SumQ_in = 0;
        SumQ_out= 0;
        SumQ_eco= 0;
        for (int i = 0; i < Initial_op.size(); i++) {
            SumQ_in=SumQ_in+Initial_op.get(i).getQIn();
            SumQ_out=SumQ_out+Initial_op.get(i).getQOut();
            SumQ_eco=SumQ_eco+MinQ.get(i);
        }
        Q_average=SumQ_out/Initial_op.size();


        for (int i = 0; i < Initial_op.size(); i++) {
            //时段入库、初水位、生态流量
            QIn= Initial_op.get(i).getQIn();
            H1 = Initial_op.get(i).getH1();
            Q_eco=MinQ.get(i);

            //保证后续时段生态流量
            Out_limit=SumQ_out-(SumQ_eco-MinQ.get(i));

            //水位变化速率限制
            double[] H_delta = new double[2];
            double[] Q_delta = new double[2];
            H_delta[0] = H1- (double) T_Delta /3600/24*4;
            H_delta[1] = H1+ (double) T_Delta /3600/24*4;
            Q_delta[0]=OnceBalance2(H1,QIn,H_delta[1]);
            Q_delta[1]=OnceBalance2(H1,QIn,H_delta[0]);

            //最大流量限制
            Q_dead=OnceBalance2(H1,QIn,DeadLevel);
            Q_proof=OnceBalance2(H1,QIn,ProofLevel);
            Q_max=MaxQ_out(H1,QIn);
            Q_max=Math.min(Q_max,Out_limit);
            Q_max=Math.min(Q_max,Q_dead);
            Q_max=Math.min(Q_max,Q_delta[1]);

            //最小流量限制
            Q_min=Math.max(Q_eco,Q_delta[0]);

            //本时段出库、末水位
            if(i==Initial_op.size()-1){
                H2=H_end;
                QOut=OnceBalance2(H1,QIn,H2);
            }
            else{
                if(Q_max<Q_proof){
                    //水位将超过校核水位
                    QOut=Q_max;
                }
                else{
                    //水位将超过设计水位,不超过校核水位
                    double[] Q_limit= new double[2];
                    Q_limit[0] = Math.max(Q_proof,Q_eco);
                    Q_limit[1] = Q_max;
                    double rate = 1.5;
                    double temp = rate*Q_average;
                    if(temp>Q_max){
                        QOut=Q_max;
                    }
                    else if(temp<Q_min){
                        QOut=Q_min;
                    }
                    else{
                        QOut=temp;
                    }

//                    while (temp>Q_max||temp<Q_min){
//                        if(temp>Q_max){
//                            rate=rate-0.1;
//                        }else{
//                            rate=rate+0.1;
//                        }
//                        temp = rate*Q_average;
//
//                    }
//                    QOut=temp;
                }
                H2=OnceBalance1(H1,QIn,QOut);
                Initial_op.get(i+1).setH1(H2);
            }

            Initial_op.get(i).setH2(H2);
            Initial_op.get(i).setQOut(QOut);

            //剩余时段总出库、剩余时段总生态流量
            SumQ_out=SumQ_out-QOut;
            SumQ_eco=SumQ_eco-Q_eco;
            Q_average=SumQ_out/(Initial_op.size()-i-1);
        }

        //补全其他数据
        for (int i = 0; i < Initial_op.size(); i++) {
            Option option = Initial_op.get(i);
            QIn= option.getQIn();
            H1= option.getH1();
            H2= option.getH2();
            QOut = OnceBalance2(H1,QIn,H2);
            double Q1 = Math.min(QOut,GetQ1((H1+H2)/2));
            double Q2 = QOut-Q1;
            double Q3=0;
            double V=GetV(H2);
            double[] aa=getPercentage_lzz(V);
            double retain=Math.max(0,V-GetV(H_begin));
            double percent1=aa[0];
            double percent2=aa[1];
            option.setQOut(QOut);
            option.setQ1(Q1);
            option.setQ2(Q2);
            option.setQ3(Q3);
            option.setV(V);
            option.setRetain(retain);
            option.setPercentage1(percent1);
            option.setPercentage2(percent2);
            option.setLimits(limits);
        }
        return Initial_op;
    }

    /**
     *水库水位限制，从前往后推
     */
    public double[] H_LimitFront(double level,double Q_Input,double MinQ){
        double d = 0;
        //闸门最大下泄能力
        double QQ0 = MaxQ_out(level,Q_Input);

        double H_eco = OnceBalance1(level,Q_Input,MinQ);
        double H_min =OnceBalance1(level,Q_Input,QQ0);

        double[] H_Limit= new double[2];
        H_Limit[0]=Math.max(H_min,DeadLevel);
        H_Limit[1]=H_eco;

        if(ProofLevel>=H_Limit[0]&&ProofLevel<=H_Limit[1]) {
            H_Limit[1]=ProofLevel;
        }
        else if(DesignLevel>=H_Limit[0]&&DesignLevel<=H_Limit[1]){
            H_Limit[1]=DesignLevel;
        }
        return H_Limit;
    }


    //水量平衡，计算末水位、出库流量
    public double OnceBalance1(double H_begin, double Q_in, double Q_out){
        double V_end=GetV(H_begin)*coefficient+(Q_in-Q_out)*T_Delta;
        return GetH(V_end/coefficient);
    }
    public double OnceBalance2(double H_begin, double Q_in, double H_end){
        double Q_out = Q_in-(GetV(H_end)-GetV(H_begin))*coefficient/T_Delta;
        return Q_out;
//        return BigDecimal.valueOf(Q_out).setScale(2,RoundingMode.HALF_UP).doubleValue();
    }
    public double GetV(double level){
        double Volume;
        int n=0;
        if(level<=LV_Curve[0][0]){
            n=0;
        }else if(level>=LV_Curve[0][LV_Curve[0].length-1]){
            n=LV_Curve[0].length;
        }else {
            for (int i = 1; i < LV_Curve[0].length; i++) {
                if(level>=LV_Curve[0][i-1]&&level<LV_Curve[0][i]){
                    n=i;
                    break;
                }
            }
        }
        if(n==0){
            Volume=LV_Curve[1][0]+(level-LV_Curve[0][0])*(LV_Curve[1][1]-LV_Curve[1][0])/(LV_Curve[0][1]-LV_Curve[0][0]);
        }else if(n==LV_Curve[0].length){
            Volume=LV_Curve[1][n-1]+(level-LV_Curve[0][n-1])*(LV_Curve[1][n-1]-LV_Curve[1][n-2])/(LV_Curve[0][n-1]-LV_Curve[0][n-2]);
        }else{
            Volume=LV_Curve[1][n-1]+(level-LV_Curve[0][n-1])*(LV_Curve[1][n]-LV_Curve[1][n-1])/(LV_Curve[0][n]-LV_Curve[0][n-1]);
        }
//        return BigDecimal.valueOf(Volume).setScale(2,RoundingMode.HALF_UP).doubleValue();
        return Volume;
    }
    public double GetH(double Volume){
        double level;
        int n=0;
        if(Volume<=LV_Curve[1][0]){
            n=0;
        }else if(Volume>=LV_Curve[1][LV_Curve[1].length-1]){
            n=LV_Curve[1].length;
        }else {
            for (int i = 1; i < LV_Curve[1].length; i++) {
                if(Volume>=LV_Curve[1][i-1]&Volume<LV_Curve[1][i]){
                    n=i;
                    break;
                }
            }
        }
        if(n==0){
            level=LV_Curve[0][0]+(Volume-LV_Curve[1][0])*(LV_Curve[0][1]-LV_Curve[0][0])/(LV_Curve[1][1]-LV_Curve[1][0]);
        }else if(n==LV_Curve[0].length){
            level=LV_Curve[0][n-1]+(Volume-LV_Curve[1][n-1])*(LV_Curve[0][n-1]-LV_Curve[0][n-2])/(LV_Curve[1][n-1]-LV_Curve[1][n-2]);
        }else{
            level=LV_Curve[0][n-1]+(Volume-LV_Curve[1][n-1])*(LV_Curve[0][n]-LV_Curve[0][n-1])/(LV_Curve[1][n]-LV_Curve[1][n-1]);
        }
//        return BigDecimal.valueOf(level).setScale(2,RoundingMode.HALF_UP).doubleValue();
        return  level;
    }
    public double GetQ1(double level){
        double MaxQ1;
        int n=0;
        if (level<=LQ_Curve1[0][0]){
            n=0;
        }else if(level>=LQ_Curve1[0][LQ_Curve1[0].length-1]){
            n=LQ_Curve1[0].length;
        }else{
            for (int i = 1; i < LQ_Curve1[0].length - 1; i++) {
                if(level>=LQ_Curve1[0][i-1]&&level<LQ_Curve1[0][i]){
                    n=i;
                    break;
                }
            }
        }

        if(n==0){
            MaxQ1=LQ_Curve1[1][0]+(level-LQ_Curve1[0][0])*(LQ_Curve1[1][1]-LQ_Curve1[1][0])/(LQ_Curve1[0][1]-LQ_Curve1[0][0]);
        }else if(n==LQ_Curve1[0].length){
            MaxQ1=LQ_Curve1[1][n-1]+(level-LQ_Curve1[0][n-1])*(LQ_Curve1[1][n-1]-LQ_Curve1[1][n-2])/(LQ_Curve1[0][n-1]-LQ_Curve1[0][n-2]);
        }else{
            MaxQ1=LQ_Curve1[1][n-1]+(level-LQ_Curve1[0][n-1])*(LQ_Curve1[1][n]-LQ_Curve1[1][n-1])/(LQ_Curve1[0][n]-LQ_Curve1[0][n-1]);
        }
        return MaxQ1;
    }
    public double GetQ2(double level){
        double MaxQ1;
        int n=0;
        if (level<=LQ_Curve2[0][0]){
            n=0;
        }else if(level>=LQ_Curve2[0][LQ_Curve2[0].length-1]){
            n=LQ_Curve2[0].length;
        }else{
            for (int i = 1; i < LQ_Curve2[0].length - 1; i++) {
                if(level>=LQ_Curve2[0][i-1]&&level<LQ_Curve2[0][i]){
                    n=i;
                    break;
                }
            }
        }

        if(n==0){
            MaxQ1=LQ_Curve2[1][0]+(level-LQ_Curve2[0][0])*(LQ_Curve2[1][1]-LQ_Curve2[1][0])/(LQ_Curve2[0][1]-LQ_Curve2[0][0]);
        }else if(n==LQ_Curve2[0].length){
            MaxQ1=LQ_Curve2[1][n-1]+(level-LQ_Curve2[0][n-1])*(LQ_Curve2[1][n-1]-LQ_Curve2[1][n-2])/(LQ_Curve2[0][n-1]-LQ_Curve2[0][n-2]);
        }else{
            MaxQ1=LQ_Curve2[1][n-1]+(level-LQ_Curve2[0][n-1])*(LQ_Curve2[1][n]-LQ_Curve2[1][n-1])/(LQ_Curve2[0][n]-LQ_Curve2[0][n-1]);
        }
        return MaxQ1;
    }

    public static double[] getPercentage_lzz(double V){

        double[] result = new double[2];
        result[0]=100*Math.max(0,(V-6534.4))/724.93;
        result[1]=100*Math.max(0,(V-6534.4))/839.59;
        result[0]=BigDecimal.valueOf(result[0]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        result[1]=BigDecimal.valueOf(result[1]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return result;
    }

    public void UpdateLimitLevel(Date time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int month = calendar.get(Calendar.MONTH);
        switch (month){
            case 0:
                LimitLevel=LimitLevels[0];
                LimitVolume = GetV(LimitLevel);
                break;
            case 1:
                LimitLevel=LimitLevels[1];
                LimitVolume = GetV(LimitLevel);
                break;
            case 2:
                LimitLevel=LimitLevels[2];
                LimitVolume = GetV(LimitLevel);
                break;
            case 3:
                LimitLevel=LimitLevels[3];
                LimitVolume = GetV(LimitLevel);
                break;
            case 4:
                LimitLevel=LimitLevels[4];
                LimitVolume = GetV(LimitLevel);
                break;
            case 5:
                LimitLevel=LimitLevels[5];
                LimitVolume = GetV(LimitLevel);
                break;
            case 6:
                LimitLevel=LimitLevels[6];
                LimitVolume = GetV(LimitLevel);
                break;
            case 7:
                LimitLevel=LimitLevels[7];
                LimitVolume = GetV(LimitLevel);
                break;
            case 8:
                LimitLevel=LimitLevels[8];
                LimitVolume = GetV(LimitLevel);
                break;
            case 9:
                LimitLevel=LimitLevels[9];
                LimitVolume = GetV(LimitLevel);
                break;
            case 10:
                LimitLevel=LimitLevels[10];
                LimitVolume = GetV(LimitLevel);
                break;
            case 11:
                LimitLevel=LimitLevels[11];
                LimitVolume = GetV(LimitLevel);
                break;
        }
    }

    public void SetEndH(double H){
        H_end=H;
    }


    /**
     * 返回最高水位的序号
     */
    public int MaxLevel(List<Option> options){
        double max=options.get(0).getH2();
        int num=0;
        for (int i = 0; i < options.size(); i++) {
            double level = options.get(i).getH2();
            if(level>=max){
                max=level;
                num=i;
            }
        }
        return num;
    }

    /**
     * 返回最高水位的序号
     */
    public int MaxRelease(List<Option> options){
        double max=options.get(0).getQOut();
        int num=0;
        for (int i = 0; i < options.size(); i++) {
            double level = options.get(i).getQOut();
            if(level>=max){
                max=level;
                num=i;
            }
        }
        return num;
    }

    /**
     * 计算最大下泄能力
     */
    public double MaxQ_out(double level,double Q_in){
        double Q1;
        double Q2;
        double Q_max;
        double H2;
        if(Q_in<=750){
            if (level>=1397.21) {
                Q1=GetQ1(level)+GetQ2(level);
            }
            else {
                Q1=Math.min(GetQ1(level),125);
            }
        }
        else{
            Q1=GetQ1(level)+GetQ2(level);
        }

        Q_max=Q1;

        while (true){
            H2=OnceBalance1(level,Q_in,Q_max);
            if(Q_in<=750){
                if ((level+H2)/2>=1397.21) {
                    Q2=GetQ1((level+H2)/2)+GetQ2((level+H2)/2);
                }
                else {
                    Q2=Math.min(GetQ1((level+H2)/2),125);
                }
            }
            else{
                Q2=GetQ1((level+H2)/2)+GetQ2((level+H2)/2);
            }

            if(Q2>=Q_max){
                return Q2;
            }else{
                Q_max=Q_max-0.05;
            }
        }
    }












}
