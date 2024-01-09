package com.cj.model.func.modular.FloodPrevent.model;


import com.cj.model.func.modular.FloodPrevent.entity.DataFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.stringToDate;

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

    @Getter
    List<Date> Time =new ArrayList<>();
    List<Double> Q_Input =new ArrayList<>();
    List<Double> Q_Interval =new ArrayList<>();
    List<Double> MaxQ =new ArrayList<>();
    List<Double> MinQ =new ArrayList<>();

    int choice;
    int coefficient =10000 ;


    public ModelOfLZZ(Object[][] pre,int delta) {
        for (int i = 0; i < pre.length; i++) {
            Date t = stringToDate(pre[i][1].toString());
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
    }

    //楼庄子常规调度
    public List<List<Double>> Calculate_S1(){

        //楼庄子
        //来水小于750时，为标准内洪水：
        //（1）当库水位达到1394.5m，且库水位继续上涨时，控制溢洪洞下泄流量不超过125m3/s，持续泄洪直至水位回落到汛限水位。
        //（2）当库水位达到1397.21m，且库水位继续上涨时，全开溢洪洞下泄洪水，同时根据入库流量和库水位涨幅情况，控制泄洪冲沙兼导流洞下泄流量，尽量保证下泄洪水在安全范围内，持续泄洪直至水位回落到汛限水位。
        //（3）当库水位达到1397.41m，且库水位继续上涨时，全开溢洪洞、泄洪冲沙兼导流洞下泄洪水，持续泄洪直至水位回落到汛限水位，力求大坝安全。
        //来水超过750时，为超标准洪水，泄洪冲沙兼导流洞、溢洪洞自由下泄，力求大坝安全。

        List<List<Double>> Result = new ArrayList<>();
        List<Double> H_b =new ArrayList<>();
        List<Double> H_e =new ArrayList<>();
        List<Double> Q_Release =new ArrayList<>();
        List<Double> Q_Release1=new ArrayList<>();
        List<Double> Q_Release2=new ArrayList<>();
        List<Double> Q_Release3=new ArrayList<>();
        List<Double> V_list=new ArrayList<>();
        List<Double> Retain_list=new ArrayList<>();

        for (int i = 0; i < Q_Input.size(); i++) {
            UpdateLimitLevel(Time.get(i));
            double beginH;
            double Q_in = Q_Input.get(i);
            double minQ = MinQ.get(i);
            double Q_out;
            double Q_1;
            double Q_2;
            double Q_3=0;
            double V;
            double endH;
            double Q_max;
            double retain;
            double[] H_Limit=new double[2];
            double[] H_Limit1;
            double[] H_Limit2;

            //设定时段初水位
            if(i==0){
                beginH=H_begin;
            }
            else{
                beginH=H_e.get(i-1);
            }

            if(Q_in<=750){
                if(beginH>=1397.41){
                    Q_max = GetQ1(beginH)+GetQ2(beginH);
                    endH =OnceBalance1(beginH,Q_in,Q_max);
                    H_Limit1= H_Limit_Delta(H_e);
                    H_Limit2= H_Limit_S1(beginH,Q_in,minQ);
                    H_Limit[0]=Math.max(H_Limit1[0],H_Limit2[0]);
                    H_Limit[1]=Math.min(H_Limit1[1],H_Limit2[1]);
                    //判断水位是否能回到汛限水位，确定时段末水位
                    if(LimitLevel>=H_Limit[0]&&LimitLevel<=H_Limit[1]){
                        endH=LimitLevel;
                        Q_out=OnceBalance2(beginH,Q_in,endH);
                        Q_1 = Math.min(Q_out,GetQ1((beginH+endH)/2));
                        Q_2 = Q_out-Q_1;
                    }
                    else{
                        if(LimitLevel<H_Limit[0]) endH=H_Limit[0];
                        if(LimitLevel>H_Limit[1]) endH=H_Limit[1];
                        Q_out=OnceBalance2(beginH,Q_in,endH);
                        Q_1 = Math.min(Q_out,GetQ1((beginH+endH)/2));
                        Q_2 = Q_out-Q_1;
                    }
                }
                else if(beginH>=1397.21){
                    Q_max = GetQ1(beginH)+GetQ2(beginH);
                    endH =OnceBalance1(beginH,Q_in,Q_max);
                    H_Limit1= H_Limit_Delta(H_e);
                    H_Limit2= H_Limit_S1(beginH,Q_in,minQ);
                    H_Limit[0]=Math.max(H_Limit1[0],H_Limit2[0]);
                    H_Limit[1]=Math.min(H_Limit1[1],H_Limit2[1]);
                    if(LimitLevel>=H_Limit[0]&&LimitLevel<=H_Limit[1]){
                        endH=LimitLevel;
                        Q_out=OnceBalance2(beginH,Q_in,endH);
                        Q_1=Math.min(Q_out,GetQ1((beginH+endH)/2));
                        Q_2=Q_out-Q_1;
                    }
                    else{
                        if(LimitLevel<H_Limit[0]) endH=H_Limit[0];
                        if(LimitLevel>H_Limit[1]) endH=H_Limit[1];
                        Q_1 = GetQ1((beginH+endH)/2);
                        Q_2 = GetQ2((beginH+endH)/2);
                        Q_out = Q_1+Q_2;
                    }

                }
                else if (beginH>=1394.5) {
                    Q_max = Math.min(GetQ1(beginH),125);
                    endH =OnceBalance1(beginH,Q_in,Q_max);
                    H_Limit1= H_Limit_Delta(H_e);
                    H_Limit2= H_Limit_S1(beginH,Q_in,minQ);
                    H_Limit[0]=Math.max(H_Limit1[0],H_Limit2[0]);
                    H_Limit[1]=Math.min(H_Limit1[1],H_Limit2[1]);
                    if(LimitLevel>=H_Limit[0]&&LimitLevel<=H_Limit[1]){
                        endH=LimitLevel;
                        Q_out=OnceBalance2(beginH,Q_in,endH);
                        Q_1=Math.min(Q_out,GetQ1((beginH+endH)/2));
                    }
                    else{
                        if(LimitLevel<H_Limit[0]) endH=H_Limit[0];
                        if(LimitLevel>H_Limit[1]) endH=H_Limit[1];
                        Q_out=OnceBalance2(beginH,Q_in,endH);
                        Q_1 = Math.min(GetQ1((beginH+endH)/2),Math.min(Q_out,125));
                    }
                    Q_2=0;
                }
                else{
                    Q_max = Math.min(GetQ1(beginH),125);
                    endH =OnceBalance1(beginH,Q_in,Q_max);
                    H_Limit1= H_Limit_Delta(H_e);
                    H_Limit2= H_Limit_S1(beginH,Q_in,minQ);
                    H_Limit[0]=Math.max(H_Limit1[0],H_Limit2[0]);
                    H_Limit[1]=Math.min(H_Limit1[1],H_Limit2[1]);
                    if(LimitLevel<H_Limit[0]) endH=H_Limit[0];
                    if(LimitLevel>H_Limit[1]) endH=H_Limit[1];
                    if(Q_in<=Math.min(GetQ1((beginH+endH)/2),125)){
                        Q_1=Q_in;
                        Q_2=0;
                    }
                    else{
                        Q_1=Math.min(GetQ1((beginH+endH)/2),125);
                        Q_2=0;
                    }
                    Q_out = Q_1+Q_2;
                    endH = OnceBalance1(beginH,Q_in,Q_out);
                }
            }
            else{
                Q_max = GetQ1(beginH)+GetQ2(beginH);
                endH =OnceBalance1(beginH,Q_in,Q_max);
                H_Limit1= H_Limit_Delta(H_e);
                H_Limit2= H_Limit_S1(beginH,Q_in,minQ);
                H_Limit[0]=Math.max(H_Limit1[0],H_Limit2[0]);
                H_Limit[1]=Math.min(H_Limit1[1],H_Limit2[1]);
                if(LimitLevel>=H_Limit[0]&&LimitLevel<=H_Limit[1]){
                    endH=LimitLevel;
                    Q_out=OnceBalance2(beginH,Q_in,endH);
                    Q_1 = Math.min(Q_out,GetQ1((beginH+endH)/2));
                    Q_2 = Q_out-Q_1;
                }
                else{
                    if(LimitLevel<H_Limit[0]) endH=H_Limit[0];
                    if(LimitLevel>H_Limit[1]) endH=H_Limit[1];
                    Q_out=OnceBalance2(beginH,Q_in,endH);
                    Q_1 = Math.min(Q_out,GetQ1((beginH+endH)/2));
                    Q_2 = Q_out-Q_1;
                }
            }

            V=BigDecimal.valueOf(GetV(endH)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            retain=Math.max(0,V-GetV(H_begin));

            H_b.add(BigDecimal.valueOf(beginH).setScale(2, RoundingMode.HALF_UP).doubleValue());
            H_e.add(BigDecimal.valueOf(endH).setScale(2, RoundingMode.HALF_UP).doubleValue());
            Q_Release.add(BigDecimal.valueOf(Q_out).setScale(2, RoundingMode.HALF_UP).doubleValue());
            Q_Release1.add(BigDecimal.valueOf(Q_1).setScale(2, RoundingMode.HALF_UP).doubleValue());
            Q_Release2.add(BigDecimal.valueOf(Q_2).setScale(2, RoundingMode.HALF_UP).doubleValue());
            Q_Release3.add(BigDecimal.valueOf(Q_3).setScale(2, RoundingMode.HALF_UP).doubleValue());
            V_list.add(BigDecimal.valueOf(V).setScale(2, RoundingMode.HALF_UP).doubleValue());
            Retain_list.add(BigDecimal.valueOf(retain).setScale(2, RoundingMode.HALF_UP).doubleValue());

        }
        Result.add(Q_Input);
        Result.add(H_b);
        Result.add(H_e);
        Result.add(Q_Release);
        Result.add(Q_Release1);
        Result.add(Q_Release2);
        Result.add(Q_Release3);
        Result.add(V_list);
        Result.add(Retain_list);

        return Result;
    }
    //最大调洪水位最小
    public List<List<Double>> MinLevel(){
        choice=1;
        List<List<List<Double>>> Result =new ArrayList<>();
        List<List<List<Double>>> option_temp =new ArrayList<>();
        for (int i = 0; i < Q_Input.size()-1; i++) {
            UpdateLimitLevel(Time.get(i));
            Result=OneStage(Result);
        }

        UpdateLimitLevel(Time.get(Time.size()-1));
        double Q_in = Q_Input.get(Q_Input.size()-1);
        for (int i = 0; i < Result.size(); i++) {
            List<List<Double>> option = new ArrayList<>(Result.get(i));
            double H_b = option.get(2).get(option.get(2).size()-1);
            double H_e = H_end;
            double Q_out;
            double Q1;
            double Q2;
            double Q3;
            double V;
            double value1;
            double value2;
            double retain;

            double[] H_Limit = H_Limit(H_b,Q_in,MinQ.get(0));

            if(H_e>=H_Limit[0]&&H_e<=H_Limit[1]){
                Q_out = OnceBalance2(H_b,Q_in,H_e);
                Q1 = Math.min(GetQ1((H_b+H_e)/2),Q_out);
                Q2 = Math.min(Q_out-Q1,GetQ2((H_b+H_e)/2));
                Q3=0;
                V=GetV(H_e);

                List<List<Double>> Result_OnePoint = new ArrayList<>();
                List<Double> Q_in_list = new ArrayList<>(option.get(0));
                List<Double> H_b_list= new ArrayList<>(option.get(1));
                List<Double> H_e_list= new ArrayList<>(option.get(2));
                List<Double> Q_out_list= new ArrayList<>(option.get(3));
                List<Double> Q1_list= new ArrayList<>(option.get(4));
                List<Double> Q2_list= new ArrayList<>(option.get(5));
                List<Double> Q3_list= new ArrayList<>(option.get(6));
                List<Double> V_list= new ArrayList<>(option.get(7));
                List<Double> Value1= new ArrayList<>();
                List<Double> Value2= new ArrayList<>();
                List<Double> Retain_list= new ArrayList<>(option.get(10));

                Q_in_list.add(BigDecimal.valueOf(Q_in).setScale(2, RoundingMode.HALF_UP).doubleValue());
                H_b_list.add(BigDecimal.valueOf(H_b).setScale(2, RoundingMode.HALF_UP).doubleValue());
                H_e_list.add(BigDecimal.valueOf(H_e).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q_out_list.add(BigDecimal.valueOf(Q_out).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q1_list.add(BigDecimal.valueOf(Q1).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q2_list.add(BigDecimal.valueOf(Q2).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q3_list.add(BigDecimal.valueOf(Q3).setScale(2, RoundingMode.HALF_UP).doubleValue());
                V_list.add(BigDecimal.valueOf(V).setScale(2, RoundingMode.HALF_UP).doubleValue());

                value1=GetObj1(H_e_list,Q_out_list,Q_Interval);
                value2=GetObj2(H_e_list);
                retain=Math.max(0,V-GetV(H_begin));
                Value1.add(value1);
                Value2.add(value2);
                Retain_list.add(retain);

                Result_OnePoint.add(Q_in_list);
                Result_OnePoint.add(H_b_list);
                Result_OnePoint.add(H_e_list);
                Result_OnePoint.add(Q_out_list);
                Result_OnePoint.add(Q1_list);
                Result_OnePoint.add(Q2_list);
                Result_OnePoint.add(Q3_list);
                Result_OnePoint.add(V_list);
                Result_OnePoint.add(Value1);
                Result_OnePoint.add(Value2);
                Result_OnePoint.add(Retain_list);

                option_temp.add(Result_OnePoint);
            }

        }

        if(option_temp.isEmpty()){
            throw new RuntimeException("无法达成末水位:"+H_end);
        }
        else{
            int num=0;
            double Value1 = option_temp.get(0).get(8).get(0);
            double value1 = option_temp.get(0).get(9).get(0);
            for (int i = 1; i < option_temp.size(); i++) {
                double Value2 = option_temp.get(i).get(8).get(0);
                double value2 = option_temp.get(i).get(9).get(0);
                if(Value2<Value1||(Value2==Value1&&value2<value1)){
                    Value1=Value2;
                    value1=value2;
                    num=i;
                }
            }
            return option_temp.get(num);

        }
    }
    //最大下泄流量最小
    public List<List<Double>> MinDischarge(){
        choice=2;
        List<List<List<Double>>> Result =new ArrayList<>();
        List<List<List<Double>>> option_temp =new ArrayList<>();
        for (int i = 0; i < Q_Input.size()-1; i++) {
            UpdateLimitLevel(Time.get(i));
            Result=OneStage(Result);
        }

        UpdateLimitLevel(Time.get(Time.size()-1));
        double Q_in = Q_Input.get(Q_Input.size()-1);
        for (int i = 0; i < Result.size(); i++) {
            List<List<Double>> option = new ArrayList<>(Result.get(i));
            double H_b = option.get(2).get(option.get(2).size()-1);
            double H_e = H_end;
            double Q_out;
            double Q1;
            double Q2;
            double Q3;
            double V;
            double value1;
            double value2;
            double retain;

            double[] H_Limit = H_Limit(H_b,Q_in,MinQ.get(0));

            if(H_e>=H_Limit[0]&&H_e<=H_Limit[1]){
                Q_out = OnceBalance2(H_b,Q_in,H_e);
                Q1 = Math.min(GetQ1((H_b+H_e)/2),Q_out);
                Q2 = Math.min(Q_out-Q1,GetQ2((H_b+H_e)/2));
                Q3=0;
                V=GetV(H_e);

                List<List<Double>> Result_OnePoint = new ArrayList<>();
                List<Double> Q_in_list = new ArrayList<>(option.get(0));
                List<Double> H_b_list= new ArrayList<>(option.get(1));
                List<Double> H_e_list= new ArrayList<>(option.get(2));
                List<Double> Q_out_list= new ArrayList<>(option.get(3));
                List<Double> Q1_list= new ArrayList<>(option.get(4));
                List<Double> Q2_list= new ArrayList<>(option.get(5));
                List<Double> Q3_list= new ArrayList<>(option.get(6));
                List<Double> V_list= new ArrayList<>(option.get(7));
                List<Double> Value1= new ArrayList<>();
                List<Double> Value2= new ArrayList<>();
                List<Double> Retain_list = new ArrayList<>();
                Q_in_list.add(BigDecimal.valueOf(Q_in).setScale(2, RoundingMode.HALF_UP).doubleValue());
                H_b_list.add(BigDecimal.valueOf(H_b).setScale(2, RoundingMode.HALF_UP).doubleValue());
                H_e_list.add(BigDecimal.valueOf(H_e).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q_out_list.add(BigDecimal.valueOf(Q_out).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q1_list.add(BigDecimal.valueOf(Q1).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q2_list.add(BigDecimal.valueOf(Q2).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q3_list.add(BigDecimal.valueOf(Q3).setScale(2, RoundingMode.HALF_UP).doubleValue());
                V_list.add(BigDecimal.valueOf(V).setScale(2, RoundingMode.HALF_UP).doubleValue());

                value1=GetObj1(H_e_list,Q_out_list,Q_Interval);
                value2=GetObj2(H_e_list);
                retain=Math.max(0,V-GetV(H_begin));
                Value1.add(value1);
                Value2.add(value2);
                Retain_list.add(retain);

                Result_OnePoint.add(Q_in_list);
                Result_OnePoint.add(H_b_list);
                Result_OnePoint.add(H_e_list);
                Result_OnePoint.add(Q_out_list);
                Result_OnePoint.add(Q1_list);
                Result_OnePoint.add(Q2_list);
                Result_OnePoint.add(Q3_list);
                Result_OnePoint.add(V_list);
                Result_OnePoint.add(Value1);
                Result_OnePoint.add(Value2);
                Result_OnePoint.add(Retain_list);

                option_temp.add(Result_OnePoint);
            }
        }

        if(option_temp.isEmpty()){
            throw new RuntimeException("无法达成末水位:"+H_end);
        }
        else{
            int num=0;
            double Value1 = option_temp.get(0).get(8).get(0);
            double value1 = option_temp.get(0).get(9).get(0);
            for (int i = 1; i < option_temp.size(); i++) {
                double Value2 = option_temp.get(i).get(8).get(0);
                double value2 = option_temp.get(i).get(9).get(0);
                if(Value2<Value1||(Value2==Value1&&value2<value1)){
                    Value1=Value2;
                    value1=value2;
                    num=i;
                }
            }
            return option_temp.get(num);

        }
    }



    public List<List<List<Double>>> OneStage(List<List<List<Double>>> option_last){
        List<List<List<Double>>> option_final = new ArrayList<>();
        List<List<List<Double>>> option_temp = new ArrayList<>();

        if(option_last.isEmpty()){
            double Q_in = Q_Input.get(0);
            double H_b = H_begin;
            double H_e;
            double Q_out;
            double Q1;
            double Q2;
            double Q3;
            double V;
            double value1;
            double value2;
            double retain;

            double[] H_Limit = H_Limit(H_b,Q_in,MinQ.get(0));
            List<Double> points = Discrete(H_b,H_Limit);
            for (int i = 0; i < points.size(); i++) {
                H_e= points.get(i);
                Q_out = OnceBalance2(H_b,Q_in,H_e);
                Q1 = Math.min(GetQ1((H_b+H_e)/2),Q_out);
                Q2 = Math.min(Q_out-Q1,GetQ2((H_b+H_e)/2));
                Q3 = 0;
                V  = GetV(H_e);
                List<List<Double>> Result_OnePoint = new ArrayList<>();
                List<Double> Q_in_list = new ArrayList<>();
                List<Double> H_b_list= new ArrayList<>();
                List<Double> H_e_list= new ArrayList<>();
                List<Double> Q_out_list= new ArrayList<>();
                List<Double> Q1_list= new ArrayList<>();
                List<Double> Q2_list= new ArrayList<>();
                List<Double> Q3_list= new ArrayList<>();
                List<Double> V_list= new ArrayList<>();
                List<Double> Value1= new ArrayList<>();
                List<Double> Value2= new ArrayList<>();
                List<Double> Retain_list = new ArrayList<>();
                Q_in_list.add(BigDecimal.valueOf(Q_in).setScale(2, RoundingMode.HALF_UP).doubleValue());
                H_b_list.add(BigDecimal.valueOf(H_b).setScale(2, RoundingMode.HALF_UP).doubleValue());
                H_e_list.add(BigDecimal.valueOf(H_e).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q_out_list.add(BigDecimal.valueOf(Q_out).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q1_list.add(BigDecimal.valueOf(Q1).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q2_list.add(BigDecimal.valueOf(Q2).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Q3_list.add(BigDecimal.valueOf(Q3).setScale(2, RoundingMode.HALF_UP).doubleValue());
                V_list.add(BigDecimal.valueOf(V).setScale(2, RoundingMode.HALF_UP).doubleValue());

                value1=GetObj1(H_e_list,Q_out_list,Q_Interval);
                value2=GetObj2(H_e_list);
                retain=Math.max(0,V-GetV(H_begin));
                Value1.add(BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Value2.add(BigDecimal.valueOf(value2).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Retain_list.add(BigDecimal.valueOf(retain).setScale(2, RoundingMode.HALF_UP).doubleValue());

                Result_OnePoint.add(Q_in_list);
                Result_OnePoint.add(H_b_list);
                Result_OnePoint.add(H_e_list);
                Result_OnePoint.add(Q_out_list);
                Result_OnePoint.add(Q1_list);
                Result_OnePoint.add(Q2_list);
                Result_OnePoint.add(Q3_list);
                Result_OnePoint.add(V_list);
                Result_OnePoint.add(Value1);
                Result_OnePoint.add(Value2);
                Result_OnePoint.add(Retain_list);

                option_temp.add(Result_OnePoint);
            }
        }
        else{
            int num = option_last.get(0).get(0).size();
            for (int i = 0; i < option_last.size(); i++) {
                List<List<Double>> option1 = option_last.get(i);
                double Q_in = Q_Input.get(num);
                double H_b = option1.get(2).get(num-1);
                double H_e;
                double Q_out;
                double Q1;
                double Q2;
                double Q3;
                double V;
                double value1;
                double value2;
                double retain;

                double[] H_Limit = H_Limit(H_b,Q_in,MinQ.get(0));
                List<Double> points = Discrete(H_b,H_Limit);
                for (int j = 0; j < points.size(); j++) {
                    H_e= points.get(j);
                    Q_out = OnceBalance2(H_b,Q_in,H_e);
                    Q1 = Math.min(GetQ1((H_b+H_e)/2),Q_out);
                    Q2 = Math.min(Q_out-Q1,GetQ2((H_b+H_e)/2));
                    Q3=0;
                    V  = GetV(H_e);

                    List<List<Double>> Result_OnePoint = new ArrayList<>();
                    List<Double> Q_in_list = new ArrayList<>(option1.get(0));
                    List<Double> H_b_list= new ArrayList<>(option1.get(1));
                    List<Double> H_e_list= new ArrayList<>(option1.get(2));
                    List<Double> Q_out_list= new ArrayList<>(option1.get(3));
                    List<Double> Q1_list= new ArrayList<>(option1.get(4));
                    List<Double> Q2_list= new ArrayList<>(option1.get(5));
                    List<Double> Q3_list= new ArrayList<>(option1.get(6));
                    List<Double> V_list= new ArrayList<>(option1.get(7));
                    List<Double> Value1= new ArrayList<>();
                    List<Double> Value2= new ArrayList<>();
                    List<Double> Retain_list = new ArrayList<>(option1.get(10));
                    Q_in_list.add(BigDecimal.valueOf(Q_in).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    H_b_list.add(BigDecimal.valueOf(H_b).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    H_e_list.add(BigDecimal.valueOf(H_e).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    Q_out_list.add(BigDecimal.valueOf(Q_out).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    Q1_list.add(BigDecimal.valueOf(Q1).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    Q2_list.add(BigDecimal.valueOf(Q2).setScale(2, RoundingMode.DOWN).doubleValue());
                    Q3_list.add(BigDecimal.valueOf(Q3).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    V_list.add(BigDecimal.valueOf(V).setScale(2, RoundingMode.HALF_UP).doubleValue());

                    value1=GetObj1(H_e_list,Q_out_list,Q_Interval);
                    value2=GetObj2(H_e_list);
                    retain=Math.max(0,V-GetV(H_begin));
                    Value1.add(BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    Value2.add(BigDecimal.valueOf(value2).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    Retain_list.add(BigDecimal.valueOf(retain).setScale(2, RoundingMode.HALF_UP).doubleValue());

                    Result_OnePoint.add(Q_in_list);
                    Result_OnePoint.add(H_b_list);
                    Result_OnePoint.add(H_e_list);
                    Result_OnePoint.add(Q_out_list);
                    Result_OnePoint.add(Q1_list);
                    Result_OnePoint.add(Q2_list);
                    Result_OnePoint.add(Q3_list);
                    Result_OnePoint.add(V_list);
                    Result_OnePoint.add(Value1);
                    Result_OnePoint.add(Value2);
                    Result_OnePoint.add(Retain_list);

                    option_temp.add(Result_OnePoint);
                }
            }
        }
        int[] mark= new int[option_temp.size()];
        for (int i = 0; i < option_temp.size(); i++) {
            if(mark[i]==0){
                int num=i;
                mark[i]=1;
                List<List<Double>> option1 = option_temp.get(i);
                double H1 = option1.get(2).get(option1.get(2).size()-1);
                double Value1 = option1.get(8).get(0);
                double value1 = option1.get(9).get(0);
                for (int j = i; j <option_temp.size(); j++) {
                    if(mark[j]==0){
                        List<List<Double>> option2 = option_temp.get(j);
                        double H2 = option2.get(2).get(option2.get(2).size()-1);
                        double Value2 = option2.get(8).get(0);
                        double value2 = option2.get(9).get(0);
                        if(H1==H2){
                            mark[j]=1;
                            if(Value2<Value1||(Value2==Value1&&value2<value1)){
                                Value1=Value2;
                                value1=value2;
                                num=j;
                            }
                        }
                    }
                }
                option_final.add(option_temp.get(num));
            }
        }
        return option_final;
    }

    //水库水位变化限制(常规调度与优化调度均须遵循)
    public double[] H_Limit_Delta(List<Double> H){
        double[] Delta_H=new double[2];
        int n = 24*3600/T_Delta;
        double max;
        double min;
        if(H.isEmpty()){
            max=min=H_begin;
        }
        else{
            max=min=H.get(H.size()-1);
        }

        if(H.size()<=n) {
            for (int i = 0; i < H.size(); i++) {
                if(H.get(i)>max) max=H.get(i);
                if(H.get(i)<min) min=H.get(i);
            }
        }
        else{
            for (int i = 0; i < n; i++) {
                int num = H.size()-1-n;
                if(H.get(num)>max) max=H.get(num);
                if(H.get(num)<min) min=H.get(num);
            }
        }
        Delta_H[0]=max-1.5;
        Delta_H[1]=min+2;
        return Delta_H;
    }
    //楼庄子水库泄流能力限制造成的水位限制（常规调度）
    public double[] H_Limit_S1(double level, double Q_Input, double MinQ){
        double MaxQ1=0;
        double MinQ2=0;
        //最大下泄能力
        double QQ0;
        //按最大下泄计算时段最低末水位
        if(Q_Input<=750){
            if(level>=1397.41){
                QQ0=GetQ1(level)+GetQ2(level);
            }
            else if (level>=1397.21) {
                QQ0=GetQ1(level)+GetQ2(level);
            }
            else if (level>=1394.5) {
                QQ0=Math.min(GetQ1(level),125);
            }
            else{
                QQ0=Math.min(GetQ1(level)+GetQ2(level),125);
            }
        }
        else{
            QQ0=GetQ1(level)+GetQ2(level);
        }
        double Hend=OnceBalance1(level,Q_Input,QQ0);
        //按最低末水位求时段平均水位，再按平均水位计算最大下泄能力
        if(Q_Input<=750){
            if(level>=1397.41){
                QQ0=GetQ1((level+Hend)/2)+GetQ2((level+Hend)/2);
            }
            else if (level>=1397.21) {
                QQ0=GetQ1((level+Hend)/2)+GetQ2((level+Hend)/2);
            }
            else if (level>=1394.5) {
                QQ0=Math.min(GetQ1((level+Hend)/2),125);
            }
            else{
                QQ0=Math.min(GetQ1((level+Hend)/2)+GetQ2((level+Hend)/2),125);
            }
        }
        else{
            QQ0=GetQ1((level+Hend)/2)+GetQ2((level+Hend)/2);
        }


        double QQ1=(GetV(level)-DeadVolume)*coefficient/T_Delta+Q_Input;
        double QQ2=(GetV(level)-LimitVolume)*coefficient/T_Delta+Q_Input;
        double QQ3=(GetV(level)-DesignVolume)*coefficient/T_Delta+Q_Input;
        double QQ4=(GetV(level)-ProofVolume)*coefficient/T_Delta+Q_Input;

        if(QQ0<=QQ4){
            //将超过校核水位
            MaxQ1=MinQ2=QQ0;
        }else if(QQ0<=QQ3){
            //将超过正常蓄水位
            MaxQ1=MinQ2=QQ0;
        }else if(QQ0<=QQ2){
            //将超过汛限水位
            MaxQ1=MinQ2=QQ0;
        }else{
            MaxQ1 = Math.min(QQ0,QQ1);
            MinQ2 = Math.max(MinQ,QQ2);
        }


        double[] Q_Limit= new double[2];
        Q_Limit[0]= MinQ2;
        Q_Limit[1]=MaxQ1;

        double[] H_Limit= new double[2];
        H_Limit[0] = OnceBalance1(level,Q_Input,Q_Limit[1]);
        H_Limit[1] = OnceBalance1(level,Q_Input,Q_Limit[0]);

        return H_Limit;
    }
    //水库泄流能力限制造成的水位限制(优化调度)
    public double[] H_Limit(double level,double Q_Input,double MinQ){
        double d = 0.1;
        //闸门最大下泄能力
        double QQ0;
        if(Q_Input<=750){
                if (level>=1397.21) {
                    QQ0=GetQ1(level)+GetQ2(level);
                }
                else {
                    QQ0=Math.min(GetQ1(level)+d*GetQ2(level),125);
                }
        }
        else{
                QQ0=GetQ1(level)+GetQ2(level);
        }

        double Hend=OnceBalance1(level,Q_Input,QQ0);

        if(Q_Input<=750){
            if ((level+Hend)/2>=1397.21) {
                QQ0=GetQ1((level+Hend)/2)+GetQ2((level+Hend)/2);
            }
            else{
                QQ0=Math.min(GetQ1((level+Hend)/2)+d*GetQ2((level+Hend)/2),125);
            }
        }
        else{
            QQ0=GetQ1((level+Hend)/2)+GetQ2((level+Hend)/2);
        }

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

    /**
     * 产生每一阶段的离散点
     * @param H_b 阶段初水位
     * @param H_Limit 阶段水位末范围
     * @return 离散点序列
     */
    public List<Double> Discrete(double H_b , double[] H_Limit){
        double H2_min = BigDecimal.valueOf(H_Limit[0]).setScale(2, RoundingMode.UP).doubleValue();
        double H2_max = BigDecimal.valueOf(H_Limit[1]).setScale(2, RoundingMode.DOWN).doubleValue();
        List<Double> DiscretePoints = new ArrayList<>();
        if(H2_max-H2_min>Step){
            double H_e=BigDecimal.valueOf(DeadLevel).setScale(2, RoundingMode.UP).doubleValue();
            while (H_e<=H2_max){
                if(H_e>=H2_min) DiscretePoints.add(H_e);
                H_e=BigDecimal.valueOf(H_e+Step).setScale(2, RoundingMode.UP).doubleValue();;
            }

        }
        else{
            double H_e=BigDecimal.valueOf((H2_min+H2_max)/2).setScale(2, RoundingMode.UP).doubleValue();
            DiscretePoints.add(H_e);
        }
        return DiscretePoints;
    }

    //计算目标函数
    public double GetObj1(List<Double> LineOfH, List<Double> LineOfQ, List<Double> Q_Interval){
        double Obj=0;
        if(choice==1){
            //全过程最大水位
            Obj = LineOfH.get(0);
            for (int i = 1; i < LineOfH.size(); i++) {
                if(LineOfH.get(i)>Obj){
                    Obj=LineOfH.get(i);
                }
            }
        }else if(choice==2){
            //全过程最大流量
            Obj = LineOfQ.get(0)+Q_Interval.get(0);
            for (int i = 1; i < LineOfQ.size(); i++) {
                if(LineOfQ.get(i)+Q_Interval.get(i)>Obj){
                    Obj=LineOfQ.get(i)+Q_Interval.get(i);
                }
            }
        }
        return Obj;
    }
    public double GetObj2(List<Double> LineOfH){
        double Obj2=0;

        for (int i = 1; i < LineOfH.size(); i++) {
            Obj2=Obj2+(LineOfH.get(i)-H_end)*(LineOfH.get(i)-H_end)+Math.abs(LineOfH.get(i)-LineOfH.get(i-1));
        }
        return Obj2;
    }

    //水量平衡，计算末水位、出库流量
    public double OnceBalance1(double H_begin, double Q_in, double Q_out){
        double V_end=GetV(H_begin)*coefficient+(Q_in-Q_out)*T_Delta;
        return GetH(V_end/coefficient);
    }
    public double OnceBalance2(double H_begin, double Q_in, double H_end){
        return Q_in-(GetV(H_end)-GetV(H_begin))*coefficient/T_Delta;
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
        return level;
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

}
