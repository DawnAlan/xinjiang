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


public class ModelOfTTH {

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
    double[][] LQ_Curve3;

    double[] LimitLevels;
    List<Double> limits = new ArrayList<>();

    @Getter
    List<Date> Time = new ArrayList<>();
    List<Double> Q_Input = new ArrayList<>();
    List<Double> Q_Interval= new ArrayList<>();
    List<Double> MaxQ = new ArrayList<>();
    List<Double> MinQ = new ArrayList<>();

    int coefficient =10000 ;
    public ModelOfTTH(Object[][] pre,int delta) {
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
        H_begin=988;
        T_Delta=delta;

        LV_Curve=new double[][]{{   955,   956,   957,   958,   959,   960,   961,   962,   963,   964,   966,   968,   970,   972,   974,   976,   978,   980,   982,   984,   986,   988,   990,   992,   },
                {   0.03,  0.28,  0.81,  1.59,  3.31,  6.78,  11.84, 18.36, 26.52, 36.29, 61.6,  95.28, 137.82,    191.56,    256.88,    335.89,    428.83,    549.14,    694.74,    863.67,    1063.26,   1297.03,   1576.8,    1838.71,   }
        };
        LQ_Curve1=new double[][]{{  950,   952,   960,   962,   965,   978,   985,   989.6, 990,   991,   992.5, 993.5, },
                {   5.1,   30.8,  112.3, 120,   120,   120,   120,   120,   120,   120,   120,   120,   }
        };
        LQ_Curve2=new double[][]{{  950,   952,   960,   962,   965,   978,   985,   989.6, 990,   991,   992.5, 993.5, },
                {   0, 0, 0, 4, 16,    42.8,  47.7,  50.6,  50.9,  51.5,  52.4,  53,    }
        };
        LQ_Curve3=new double[][]{{  950,   952,   960,   962,   965,   978,   985,   989.6, 990,   991,   992.5, 993.5, },
                {   0, 0, 0, 0, 0, 0, 0, 0, 23.7,  154.9, 497,   813.5, }
        };
        this.DeadLevel  =972;
        this.LimitLevel =987;
        this.NormalLevel=989.6;
        this.HeightLevel=989.6;
        this.DesignLevel=991.2;
        this.ProofLevel =992.54;
        DeadVolume = GetV(DeadLevel);
        LimitVolume = GetV(LimitLevel);
        NormalVolume = GetV(NormalLevel);
        HeightVolume = GetV(HeightLevel);
        DesignVolume = GetV(DesignLevel);
        ProofVolume = GetV(ProofLevel);
        LimitLevels = new double[]{988,988,988,988,988,988,987,988,988,988,988,988};
        for (int i = 0; i < 12; i++) {
            limits.add(LimitLevels[i]);
        }

    }
    public ModelOfTTH(ReqFloodPrevent reqFloodPrevent){
        List<DataFloodPrevent> data_FloodPrevent_all = reqFloodPrevent.getData().get("lat");

        //时间戳、区间流量、生态流量、最大泄流
        for (int i = 0; i < data_FloodPrevent_all.size(); i++) {
            DataFloodPrevent dataFloodPrevent = data_FloodPrevent_all.get(i);
            Time.add(dataFloodPrevent.getTime());
            Q_Interval.add(dataFloodPrevent.getPre());

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

        //时间间隔、初末水位、寻优精度
        T_Delta= data_FloodPrevent_all.get(0).getScale();
        H_begin= reqFloodPrevent.getH2_begin();
        H_end  = reqFloodPrevent.getH2_end();
        Step = reqFloodPrevent.getStep2();

        //库容曲线、泄流曲线
        List<Double> LV_l = new ArrayList<>();
        List<Double> LV_v = new ArrayList<>();
        List<Double> LQ1_l = new ArrayList<>();
        List<Double> LQ1_q = new ArrayList<>();
        List<Double> LQ2_l = new ArrayList<>();
        List<Double> LQ2_q = new ArrayList<>();
        List<Double> LQ3_l = new ArrayList<>();
        List<Double> LQ3_q = new ArrayList<>();
        for (int i = 0; i < reqFloodPrevent.getCurveParam().size(); i++) {
            CurveParam piece = reqFloodPrevent.getCurveParam().get(i);
            int ID = piece.getId();
            double level = piece.getLevel();
            double value = piece.getValue();
            switch (ID){
                case 200:
                    LV_l.add(level);
                    LV_v.add(value);
                    break;
                case 201:
                    LQ1_l.add(level);
                    LQ1_q.add(value);
                    break;
                case 202:
                    LQ2_l.add(level);
                    LQ2_q.add(value);
                    break;
                case 203:
                    LQ3_l.add(level);
                    LQ3_q.add(value);
                    break;
            }
        }
        LV_Curve=new double[2][LV_l.size()];
        LQ_Curve1 = new double[2][LQ1_l.size()];
        LQ_Curve2 = new double[2][LQ2_l.size()];
        LQ_Curve3 = new double[2][LQ3_l.size()];
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
        for (int i = 0; i < LQ_Curve3[0].length; i++) {
            LQ_Curve3[0][i]=LQ3_l.get(i);
            LQ_Curve3[1][i]=LQ3_q.get(i);
        }
        Arrays.sort(LV_Curve[0]);
        Arrays.sort(LV_Curve[1]);
        Arrays.sort(LQ_Curve1[0]);
        Arrays.sort(LQ_Curve1[1]);
        Arrays.sort(LQ_Curve2[0]);
        Arrays.sort(LQ_Curve2[1]);
        Arrays.sort(LQ_Curve3[0]);
        Arrays.sort(LQ_Curve3[1]);

        //特征水位、特征库容
        this.DeadLevel  =972;
        this.LimitLevel =987;
        this.NormalLevel=989.6;
        this.HeightLevel=989.6;
        this.DesignLevel=991.2;
        this.ProofLevel =992.54;
        DeadVolume = GetV(DeadLevel);
        LimitVolume = GetV(LimitLevel);
        NormalVolume = GetV(NormalLevel);
        HeightVolume = GetV(HeightLevel);
        DesignVolume = GetV(DesignLevel);
        ProofVolume = GetV(ProofLevel);

        LimitLevels= reqFloodPrevent.getLimitLevels_tth();
        for (int i = 0; i < 12; i++) {
            limits.add(LimitLevels[i]);
        }
    }


    //头屯河常规调度
    public List<Option> Calculate_S2(){

        /*
        （1）当入库流量不大于下游河道安全泄量120m3/s时，库水位不超过汛限水位,水库下泄流量不超过120m3/s，泄洪方式采用放水涵洞，持续泄洪直至水位回落到汛限水位;
        （2）当入库流量大于下游河道安全泄量120m3/s但不超过 590m3/s时，库水位不超过防洪高水位 989.60m，按水库的限泄流量下泄，泄洪方式采用放水涵洞和泄水隧洞相结合。通过拦洪错峰延长 泄洪时段发挥水库调洪功能，持续泄洪直至水位回落到汛限水位;
        （3）当入库流量大于下游河道安全泄量120m3/s但不超过590m3/s时，库水位超过防洪高水位（989.60m）时，并且库水位持续上涨，防洪调度方式转入水库保坝安全的调度阶段。放水涵洞、泄水隧洞及溢洪道均参与泄洪，力求大坝安全。放水涵洞控制下泄120m3/s，泄水隧洞及溢洪道敞开自由泄洪。持续泄洪直至水位回落到汛限水位;
        （4）当入库流量超过590m3/s，但低于校核洪水1013m3/s，库水位超过防洪高水位（989.60m）时，泄水建筑物全部敞开泄流，泄洪方式采用放水涵洞、泄水隧洞、溢洪 道相结合。尽量控制库水位不超过校核洪水位（992.54m）。
        （5）当入库流量继续增加或无减小的趋势，且库水位已达到或超过校核洪水位（992.54m）后，调度运行转入头屯河水库抢险应急预案进行处理，按照预案要求进行应急调度和人员转移撤离。
        */

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


            if(i==0){
                beginH=H_begin;
            }
            else{
                beginH=result.get(i-1).getH2();
            }

            double[] Q=ConventionalCalculate(beginH,Q_in,minQ);
            Q_out=Q[0];
            Q_1=Q[1];
            Q_2=Q[2];
            Q_3=Q[3];
            endH=OnceBalance1(beginH,Q_in,Q_out);
            V=GetV(endH);
            retain=Math.max(0,V-GetV(H_begin));

            double[] aa=getPercentage_tth(V);
            double percent1=aa[0];
            double percent2=aa[1];

            option.setTime(time);
            option.setType("常规调度");
            option.setName("头屯河");
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
        /*
        （1）当入库流量不大于下游河道安全泄量120m3/s时，库水位不超过汛限水位,水库下泄流量不超过120m3/s，泄洪方式采用放水涵洞，持续泄洪直至水位回落到汛限水位;
        （2）当入库流量大于下游河道安全泄量120m3/s但不超过 590m3/s时，库水位不超过防洪高水位 989.60m，按水库的限泄流量下泄，泄洪方式采用放水涵洞和泄水隧洞相结合。通过拦洪错峰延长 泄洪时段发挥水库调洪功能，持续泄洪直至水位回落到汛限水位;
        （3）当入库流量大于下游河道安全泄量120m3/s但不超过590m3/s时，库水位超过防洪高水位（989.60m）时，并且库水位持续上涨，防洪调度方式转入水库保坝安全的调度阶段。放水涵洞、泄水隧洞及溢洪道均参与泄洪，力求大坝安全。放水涵洞控制下泄120m3/s，泄水隧洞及溢洪道敞开自由泄洪。持续泄洪直至水位回落到汛限水位;
        （4）当入库流量超过590m3/s，但低于校核洪水1013m3/s，库水位超过防洪高水位（989.60m）时，泄水建筑物全部敞开泄流，泄洪方式采用放水涵洞、泄水隧洞、溢洪 道相结合。尽量控制库水位不超过校核洪水位（992.54m）。
        （5）当入库流量继续增加或无减小的趋势，且库水位已达到或超过校核洪水位（992.54m）后，调度运行转入头屯河水库抢险应急预案进行处理，按照预案要求进行应急调度和人员转移撤离。
        */


        double[] Q = new double[4];

        //最大下泄能力
        double MaxQ;
        if(Q_Input>=590||level>=ProofLevel){
            MaxQ=GetQ1(level)+GetQ2(level)+GetQ3(level);
        }
        else if(Q_Input>=120){
            if(level>=HeightLevel){
                MaxQ=GetQ1(level)+GetQ2(level)+GetQ3(level);
            }
            else{
                MaxQ=GetQ1(level)+GetQ2(level);
            }
        }
        else{
            MaxQ=Math.min(GetQ1(level),120);
        }

        //恢复至汛限水位所需下泄流量
        double Q_limit=(GetV(level)-LimitVolume)*coefficient/T_Delta+Q_Input;

        if(MinQ<=Q_limit&&Q_limit<=MaxQ){
            Q[0]=Q_limit;
            Q[1]=Math.min(Q[0],GetQ1(level));
            Q[2] = Math.min(Q[0] - Q[1], GetQ2(level));
        }
        else if(Q_limit<=MinQ){
            Q[0]=MinQ;
            Q[1]=MinQ;
            Q[2]=0;
        }else{
            Q[0]=MaxQ;
            Q[1]=GetQ1(level);
            Q[2]=Math.min(Q[0] - Q[1], GetQ2(level));
        }
        Q[3]=Q[0]-Q[1]-Q[2];
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

        double SumQ_in = 0;
        double SumQ_out= 0;
        double SumQ_eco= 0;
        for (int i = 0; i < Initial_op.size(); i++) {
            SumQ_in=SumQ_in+Initial_op.get(i).getQIn();
            SumQ_out=SumQ_out+Initial_op.get(i).getQOut();
            SumQ_eco=SumQ_eco+MinQ.get(i);
        }

        double QIn;
        double QOut;
        double H1;
        double H2;
        double Q_eco;
        double[] Limit;

        double Out_limit;
        double H2_limit;

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


            //本时段末水位
            if(i==Initial_op.size()-1){
                QOut=SumQ_out;
                H2=H_end;
                Initial_op.get(i).setH2(H2);
                Initial_op.get(i).setQOut(QOut);
            }
            else{
                H2 = Math.max(Math.max(Math.max(Limit[0],H2_limit),H_delta[0]),H_begin-1);
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
            double Q1 = Math.min(QOut,GetQ1(H1));
            double Q2 = Math.min(QOut-Q1,GetQ2((H1+H2)/2));
            double Q3 = QOut-Q1-Q2;
            double V=GetV(H2);
            double[] aa=getPercentage_tth(V);
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
            H_delta[0] = H1- (double) T_Delta /3600/24*1;
            H_delta[1] = H1+ (double) T_Delta /3600/24*4;
            Q_delta[0]=OnceBalance2(H1,QIn,H_delta[1]);
            Q_delta[1]=OnceBalance2(H1,QIn,H_delta[0]);


            //流量限制
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
                    double rate = 1.4;
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
            double Q1 = Math.min(QOut,GetQ1(H1));
            double Q2 = Math.min(QOut-Q1,GetQ2((H1+H2)/2));
            double Q3 = QOut-Q1-Q2;
            double V=GetV(H2);
            double[] aa=getPercentage_tth(V);
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
    /**
     * 计算最大下泄能力
     */
    public double MaxQ_out(double level,double Q_in){
        double Q1;
        double Q2;
        double Q_max;
        double H2;

        if(Q_in>=590||level>=ProofLevel){
            Q1=GetQ1(level)+GetQ2(level)+GetQ3(level);
        }
        else if(Q_in>=120){
            if(level>=HeightLevel){
                Q1=GetQ1(level)+GetQ2(level)+GetQ3(level);
            }
            else{
                Q1=GetQ1(level)+GetQ2(level);
            }
        }
        else{
            Q1=Math.min(GetQ1(level),120);
        }

        Q_max=Q1;

        while (true){
            H2=OnceBalance1(level,Q_in,Q_max);
            if(Q_in>=590||(level+H2)/2>=ProofLevel){
                Q2=GetQ1((level+H2)/2)+GetQ2((level+H2)/2)+GetQ3((level+H2)/2);
            }
            else if(Q_in>=120){
                if((level+H2)/2>=HeightLevel){
                    Q2=GetQ1((level+H2)/2)+GetQ2((level+H2)/2)+GetQ3((level+H2)/2);
                }
                else{
                    Q2=GetQ1((level+H2)/2)+GetQ2((level+H2)/2);
                }
            }
            else{
                Q2=Math.min(GetQ1((level+H2)/2),120);
            }

            if(Q2>=Q_max){
                return Q2;
            }else{
                Q_max=Q_max-0.05;
            }
        }





    }
    /**
     * 计算库容占用比例
     */
    public static double[] getPercentage_tth(double V){
        double[] result = new double[2];
        result[0]=100*Math.max(0,(V-1297.03))/223.816;
        result[1]=100*Math.max(0,(V-1297.03))/541.681;
        result[0]=BigDecimal.valueOf(result[0]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        result[1]=BigDecimal.valueOf(result[1]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return result;
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
    public double GetQ3(double level){
        double MaxQ1;
        int n=0;
        if (level<=LQ_Curve3[0][0]){
            n=0;
        }else if(level>=LQ_Curve3[0][LQ_Curve3[0].length-1]){
            n=LQ_Curve3[0].length;
        }else{
            for (int i = 1; i < LQ_Curve3[0].length - 1; i++) {
                if(level>=LQ_Curve3[0][i-1]&&level<LQ_Curve3[0][i]){
                    n=i;
                    break;
                }
            }
        }

        if(n==0){
            MaxQ1=LQ_Curve3[1][0]+(level-LQ_Curve3[0][0])*(LQ_Curve3[1][1]-LQ_Curve3[1][0])/(LQ_Curve3[0][1]-LQ_Curve3[0][0]);
        }else if(n==LQ_Curve3[0].length){
            MaxQ1=LQ_Curve3[1][n-1]+(level-LQ_Curve3[0][n-1])*(LQ_Curve3[1][n-1]-LQ_Curve3[1][n-2])/(LQ_Curve3[0][n-1]-LQ_Curve3[0][n-2]);
        }else{
            MaxQ1=LQ_Curve3[1][n-1]+(level-LQ_Curve3[0][n-1])*(LQ_Curve3[1][n]-LQ_Curve3[1][n-1])/(LQ_Curve3[0][n]-LQ_Curve3[0][n-1]);
        }
        return MaxQ1;
    }


    public void setQ_Input2(List<Option> op_lzz){
        Q_Input = new ArrayList<>();
        for (int i = 0; i < op_lzz.size(); i++) {
            double qIn = Q_Interval.get(i)+op_lzz.get(i).getQOut();
            Q_Input.add(BigDecimal.valueOf(qIn).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
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
