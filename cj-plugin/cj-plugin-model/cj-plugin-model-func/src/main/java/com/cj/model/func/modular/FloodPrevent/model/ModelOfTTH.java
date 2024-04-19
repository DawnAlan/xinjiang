package com.cj.model.func.modular.FloodPrevent.model;



import com.cj.model.func.modular.FloodPrevent.entity.DataFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
            Date t = (Date) pre[i][0];
            Time.add(t);
            Q_Input.add((double)pre[i][1]);
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
    public ModelOfTTH(ReqFloodPrevent reqFloodPrevent) throws Exception {

        try{
            List<DataFloodPrevent> data_FloodPrevent_all = reqFloodPrevent.getData().get("lat");

            //时间戳、区间流量、生态流量、最大泄流
            for (DataFloodPrevent dataFloodPrevent : data_FloodPrevent_all) {
                Time.add(dataFloodPrevent.getTime());
                Q_Interval.add(dataFloodPrevent.getPre());

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dataFloodPrevent.getTime());
                int month = calendar.get(Calendar.MONTH) + 1;
                if (month >= 4 && month <= 9) {
                    MinQ.add(1.48);
                } else {
                    MinQ.add(0.74);
                }
                MaxQ.add(180.0);
            }

        }
        catch (Exception e){
            throw new Exception("输入预报数据异常");
        }
        try{
            List<DataFloodPrevent> data_FloodPrevent_all = reqFloodPrevent.getData().get("lat");

            //时间间隔、初末水位、寻优精度
            T_Delta= data_FloodPrevent_all.get(0).getScale();
            H_begin= reqFloodPrevent.getH2_begin();
            H_end  = reqFloodPrevent.getH2_end();
            Step = reqFloodPrevent.getStep2();

        }
        catch (Exception e){
            throw new Exception("输入边界约束异常");
        }
        try{
            LimitLevels= reqFloodPrevent.getLimitLevels_tth();
            for (int i = 0; i < 12; i++) {
                limits.add(LimitLevels[i]);
            }
        }
        catch (Exception e){
            throw new Exception("输入汛限水位异常");
        }
        try{
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


        }
        catch (Exception e){
            throw new Exception("输入特性曲线异常");
        }

        //默认特征水位、特征库容
        this.DeadLevel  =972;
        this.LimitLevel =987;
        this.NormalLevel=989.6;
        this.HeightLevel=989.6;
        this.DesignLevel=991.2;
        this.ProofLevel =992.54;




    }


    /**
     * 头屯河常规调度
     */
    public List<Option> Calculate_S1(){

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
     * 头屯河灵活调度
     */
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

            double[] Q=FlexibleCalculate(beginH,Q_in,minQ);
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
            option.setType("灵活调度");
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
     * 预泄调度
     */
    public List<Option> Calculate_S3(){
        Date time;
        double Q_in;
        double Q_out;
        double H1;
        double H2;
        double Q_eco;
        double Q1;
        double Q2;
        double Q3;
        double V;
        double Retain;
        double p1;
        double p2;

        double[] Limit;



        double decline=H_begin;

        List<List<Option>> all = new ArrayList<>();
        while (decline>=DeadLevel){
            List<Option> options = new ArrayList<>();
            boolean judge=false;
            for (int i = 0; i < Time.size(); i++) {
                time=Time.get(i);
                Q_in=Q_Input.get(i);
                Q_eco=MinQ.get(i);
                if(i==0){
                    H1=H_begin;
                }
                else{
                    H1=options.get(i-1).getH2();
                }

                Option option = new Option();
                option.setQIn(Q_in);
                option.setH1(H1);
                option.setTime(Time.get(i));
                UpdateLimitLevel(time);
                //泄流能力限制
                Limit =H_LimitFront(H1,Q_in,Q_eco);
                //水位变化速率限制
                double[] H_delta = new double[2];
                H_delta[0] = H1- (double) T_Delta /3600/24*2;
                H_delta[1] = H1+ (double) T_Delta /3600/24*2;
                //计算本时段末水位、流量
                if(!judge){
                    H2 = Math.max(Math.max(Limit[0],H_delta[0]),decline);
                    Q_out=OnceBalance2(H1,Q_in,H2);
                    if(H2==decline){
                        judge=true;
                    }
                }
                else{
                    Q_out=FlexibleCalculate(H1,Q_in,Q_eco)[0];
                    H2=OnceBalance1(H1,Q_in,Q_out);
                }
                option.setH2(H2);
                option.setQOut(Q_out);
                options.add(option);
            }

            all.add(options);
            decline=decline-0.1;
        }
        //选出最优解
        int num = 0;
        double best = Value(all.get(0));
        for (int i = 0; i < all.size(); i++) {
            double value = Value(all.get(i));
            if(value<best){
                best=value;
                num=i;
            }
        }
        //补全其他数据
        for (int i = 0; i < all.get(num).size(); i++) {
            Option option = all.get(num).get(i);
            H1=option.getH1();
            H2=option.getH2();
            Q_out= option.getQOut();
            Q1=Math.min(Q_out,GetQ1((H1+H2)/2));
            Q2=Math.min(Q_out-Q1,GetQ2((H1+H2)/2));
            Q3=Q_out-Q1-Q2;
            V=GetV(H2);
            Retain=Math.max(0,V-GetV(H_begin));
            p1=getPercentage_tth(V)[0];
            p2=getPercentage_tth(V)[1];


            option.setName("头屯河");
            option.setType("预泄调度");
            option.setTime(Time.get(i));
            option.setQ1(Q1);
            option.setQ2(Q2);
            option.setQ3(Q3);
            option.setV(V);
            option.setRetain(Retain);
            option.setPercentage1(p1);
            option.setPercentage2(p2);
            option.setLimits(limits);
        }



        return all.get(num);
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
                MaxQ=Math.min(GetQ1(level)+GetQ2(level),120);
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
    /**
     *灵活调度流量计算
     */
    public double[] FlexibleCalculate(double level, double Q_Input, double MinQ){
        double[] Q = new double[4];

        //最大下泄能力
        double MaxQ;
        if(Q_Input>=590||level>=HeightLevel){
            MaxQ=GetQ1(level)+GetQ2(level)+GetQ3(level);
        }
        else{
            MaxQ=Math.min(GetQ1(level)+GetQ2(level)+GetQ3(level),120);
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
    public double MaxQ_out(double H1,double Q_in){
        double Q1;
        double Q2=0;
        double H2;

        T_Delta = T_Delta/100;
        for (int i = 0; i < 100; i++) {
            if(Q_in>=590||H1>=HeightLevel){
                Q1=GetQ1(H1)+GetQ2(H1)+GetQ3(H1);
            }
            else{
                Q1=Math.min(GetQ1(H1)+GetQ2(H1)+GetQ3(H1),120);
            }
            H1=OnceBalance1(H1,Q_in,Q1);
            if(H1<=DeadLevel){
                H1=DeadLevel;
                Q1=OnceBalance2(H1,Q_in,DeadLevel);
            }
            Q2=Q2+Q1;
        }
        Q2=Q2/100;
        T_Delta=T_Delta*100;



//        if(Q_in>=590||H1>=HeightLevel){
//            Q1=GetQ1(H1)+GetQ2(H1)+GetQ3(H1);
//        }
//        else{
//            Q1=Math.min(GetQ1(H1)+GetQ2(H1)+GetQ3(H1),120);
//        }
//        H2=OnceBalance1(H1,Q_in,Q1);
//
//        if(H2<=DeadLevel){
//            Q2=OnceBalance2(H1,Q_in,DeadLevel);
//        }
//        else{
//            if(Q_in>=590||(H1+H2)/2>=HeightLevel){
//                Q2=GetQ1((H1+H2)/2)+GetQ2((H1+H2)/2)+GetQ3((H1+H2)/2);
//            }
//            else{
//                Q2=Math.min(GetQ1((H1+H2)/2)+GetQ2((H1+H2)/2)+GetQ3((H1+H2)/2),120);
//            }
//        }

        return Q2;
    }



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
    public double[] getPercentage_tth(double V){
        double[] result = new double[2];
        result[0]=100*Math.max(0,(V-1297.03))/223.816;
        result[1]=100*Math.max(0,(V-1297.03))/541.681;
        result[0]=BigDecimal.valueOf(result[0]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        result[1]=BigDecimal.valueOf(result[1]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return result;
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
     * 返回最低水位的序号
     */
    public int MinLevel(List<Option> options){
        double max=options.get(0).getH2();
        int num=0;
        for (int i = 0; i < options.size(); i++) {
            double level = options.get(i).getH2();
            if(level<=max){
                max=level;
                num=i;
            }
        }
        return num;
    }
    /**
     * 计算目标函数
     */
    public double Value(List<Option> options){
        double delta;
        double over;
        double value;
        int min = MinLevel(options);
        int max = MaxLevel(options);

        delta=options.get(max).getH2()-options.get(min).getH2();
        over=Math.max(0,options.get(max).getH2()-LimitLevel);
        value=delta+10*over;

        return value;
    }

    /**
     * 设定入库流量
     */
    public void setQ_Input2(List<Option> op_lzz){
        Q_Input = new ArrayList<>();
        for (int i = 0; i < op_lzz.size(); i++) {
            double qIn = Q_Interval.get(i)+op_lzz.get(i).getQOut();
            Q_Input.add(BigDecimal.valueOf(qIn).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
    }

}
