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

    @Getter
    List<Date> Time = new ArrayList<>();
    List<Double> Q_Input = new ArrayList<>();
    List<Double> Q_Interval= new ArrayList<>();
    List<Double> MaxQ = new ArrayList<>();
    List<Double> MinQ = new ArrayList<>();

    int choice;
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
    }




    //头屯河常规调度
    public List<List<Double>> Calculate_S2(){

        /*
        （1）当入库流量不大于下游河道安全泄量120m3/s时，库水位不超过汛限水位,水库下泄流量不超过120m3/s，泄洪方式采用放水涵洞，持续泄洪直至水位回落到汛限水位;
        （2）当入库流量大于下游河道安全泄量120m3/s但不超过 590m3/s时，库水位不超过防洪高水位 989.60m，按水库的限泄流量下泄，泄洪方式采用放水涵洞和泄水隧洞相结合。通过拦洪错峰延长 泄洪时段发挥水库调洪功能，持续泄洪直至水位回落到汛限水位;
        （3）当入库流量大于下游河道安全泄量120m3/s但不超过590m3/s时，库水位超过防洪高水位（989.60m）时，并且库水位持续上涨，防洪调度方式转入水库保坝安全的调度阶段。放水涵洞、泄水隧洞及溢洪道均参与泄洪，力求大坝安全。放水涵洞控制下泄120m3/s，泄水隧洞及溢洪道敞开自由泄洪。持续泄洪直至水位回落到汛限水位;
        （4）当入库流量超过590m3/s，但低于校核洪水1013m3/s，库水位超过防洪高水位（989.60m）时，泄水建筑物全部敞开泄流，泄洪方式采用放水涵洞、泄水隧洞、溢洪 道相结合。尽量控制库水位不超过校核洪水位（992.54m）。
        （5）当入库流量继续增加或无减小的趋势，且库水位已达到或超过校核洪水位（992.54m）后，调度运行转入头屯河水库抢险应急预案进行处理，按照预案要求进行应急调度和人员转移撤离。
        */

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
            double endH;
            double Q_in = Q_Input.get(i);
            double minQ = MinQ.get(i);
            double Q_out;
            double Q_1;
            double Q_2;
            double Q_3;
            double V;
            double retain;
            double Q_max;
            double[] H_Limit;


            if(i==0){
                beginH=H_begin;
            }
            else{
                beginH=H_e.get(i-1);
            }

            double[] Q;
            Q=ConventionalCalculate(beginH,Q_in,minQ);
            Q_out=Q[0];
            Q_1=Q[1];
            Q_2=Q[2];
            Q_3=Q[3];
            endH=OnceBalance1(beginH,Q_in,Q_out);
            V=GetV(endH);
            retain=Math.max(0,V-GetV(H_begin));

            H_b.add(beginH);
            H_e.add(endH);
            Q_Release.add(Q_out);
            Q_Release1.add(Q_1);
            Q_Release2.add(Q_2);
            Q_Release3.add(Q_3);
            V_list.add(V);
            Retain_list.add(retain);

//            H_b.add(BigDecimal.valueOf(beginH).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            H_e.add(BigDecimal.valueOf(endH).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            Q_Release.add(BigDecimal.valueOf(Q_out).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            Q_Release1.add(BigDecimal.valueOf(Q_1).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            Q_Release2.add(BigDecimal.valueOf(Q_2).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            Q_Release3.add(BigDecimal.valueOf(Q_3).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            V_list.add(BigDecimal.valueOf(V).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            Retain_list.add(BigDecimal.valueOf(retain).setScale(2, RoundingMode.HALF_UP).doubleValue());
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
                Q3 = Math.min(Q_out - Q1 - Q2, GetQ3((H_b+H_e)/2));
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

        if(option_temp.isEmpty()){
            throw new RuntimeException("无法达成末水位:"+H_end);
        }
        else{
            int num =0;
            double Value1 = option_temp.get(0).get(8).get(0);
            double value1 = option_temp.get(0).get(9).get(0);
            for (int i = 1; i < option_temp.size(); i++) {
                double Value2 = option_temp.get(i).get(8).get(0);
                double value2 = option_temp.get(i).get(9).get(0);
                if(Value2<Value1||(Value2==Value1&&value2<value1)){
                    Value1=Value2;
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
                Q3 = Math.min(Q_out - Q1 - Q2, GetQ3((H_b+H_e)/2));
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

        if(option_temp.isEmpty()){
            throw new RuntimeException("无法达成末水位:"+H_end);
        }
        else{
            int num =0;
            double Value1 = option_temp.get(0).get(8).get(0);
            double value1 = option_temp.get(0).get(9).get(0);
            for (int i = 1; i < option_temp.size(); i++) {
                double Value2 = option_temp.get(i).get(8).get(0);
                double value2 = option_temp.get(i).get(9).get(0);
                if(Value2<Value1||(Value2==Value1&&value2<value1)){
                    Value1=Value2;
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
                Q3 = Math.min(Q_out - Q1 - Q2, GetQ3((H_b+H_e)/2));
                V=GetV(H_e);

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
                List<Double> Retain_list= new ArrayList<>();
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
                    Q3 = Math.min(Q_out - Q1 - Q2, GetQ3((H_b+H_e)/2));
                    V=GetV(H_e);

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
                    List<Double> Retain_list= new ArrayList<>(option1.get(10));
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

    //水库泄流能力限制造成的水位限制(优化调度)
    public double[] H_Limit(double level,double Q_Input,double MinQ){

        //闸门最大下泄能力
        double QQ0;
        if(Q_Input>=1013||level>=ProofLevel){
            QQ0=GetQ1(level)+GetQ2(level)+GetQ3(level);
        }
        else if(Q_Input>=590){
            QQ0=GetQ1(level)+GetQ2(level)+GetQ3(level);
        }
        else if(Q_Input>=120){
            if(level>=HeightLevel){
                QQ0=GetQ1(level)+GetQ2(level)+GetQ3(level);
            }
            else{
                QQ0=GetQ1(level)+0.05*GetQ2(level);
            }
        }
        else{
            QQ0=Math.min(GetQ1(level),120);
        }

        double Hend=OnceBalance1(level,Q_Input,QQ0);

        if(Q_Input>=1013||level>=ProofLevel){
            QQ0=GetQ1((level+Hend)/2)+GetQ2((level+Hend)/2)+GetQ3((level+Hend)/2);
        }
        else if(Q_Input>=590){
            QQ0=GetQ1((level+Hend)/2)+GetQ2((level+Hend)/2)+GetQ3((level+Hend)/2);
        }
        else if(Q_Input>=120){
            if(level>=HeightLevel){
                QQ0=GetQ1((level+Hend)/2)+GetQ2((level+Hend)/2)+GetQ3((level+Hend)/2);
            }
            else{
                QQ0=GetQ1((level+Hend)/2)+0.1*GetQ2((level+Hend)/2);
            }
        }
        else{
            QQ0=Math.min(GetQ1((level+Hend)/2),120);
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
        else if(NormalLevel>=H_Limit[0]&&NormalLevel<=H_Limit[1]){
            H_Limit[1]=NormalLevel;
        }
        else if(LimitLevel+1>=H_Limit[0]&&LimitLevel+1<=H_Limit[1]){
            H_Limit[1]=LimitLevel+1;
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

    public void setQ_Input(List<Double> q_Input) {
        Q_Input = new ArrayList<>();
        for (int i = 0; i < Q_Interval.size(); i++) {
            double qIn = Q_Interval.get(i)+q_Input.get(i);
            Q_Input.add(BigDecimal.valueOf(qIn).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
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

    public static Date stringToDate (String input){
        Date result =new Date();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        LocalDateTime dateTime = LocalDateTime.parse(input, formatter);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("Asia/Shanghai"));
        result = Date.from(zonedDateTime.toInstant());
        return result;
    }

    /**
     * 粒子数
     */
    int n=100;
    /**
     * 粒子维度（时段数）
     */
    int D;
    /**
     * 迭代次数
     */
    int K=100;
    /**
     * 惯性权重
     */
    double w;
    double wMax=1;
    double wMin=0.1;
    /**
     * 个体学习因子
     */
    double c1=1;
    /**
     * 群体学习因子
     */
    double c2=1;
    /**
     * 个体随机因子
     */
    double r1;
    /**
     * 群体随机因子
     */
    double r2;
    /**
     * 粒子群
     */
    Option[][] birds;
    /**
     * 粒子速度
     */
    double[][] V;
    /**
     * 个体最优
     */
    Option[][] IndividualBest;
    /**
     * 群体最优
     */
    Option[] GroupBest;

    public List<Option> MinLevel(List<Option> Initial, String name){
        initialize(Initial,name);
        for (int i = 0; i < K; i++) {
            Iterate_MinLevel();
        }

        List<Option> result = new ArrayList<>();
        result.addAll(Arrays.asList(GroupBest));

        //补全最优结果的其他项
        double H_b;
        double H_e;
        double Q_out;
        double Q1;
        double Q2;
        double Q3;
        double V;
        double retain;
        double percent1;
        double percent2;

        for (int i = 0; i < result.size(); i++) {
            H_b=result.get(i).getH1();
            H_e=result.get(i).getH2();
            Q_out=result.get(i).getQOut();
            Q1 = Math.min(GetQ1((H_b+H_e)/2),Q_out);
            Q2 = Math.min(Q_out-Q1,GetQ2((H_b+H_e)/2));
            Q3 = 0;
            V = GetV(H_e);
            retain=Math.max(0,V-GetV(H_begin));
            double[] aa=getPercentage_tth(V);
            percent1=aa[0];
            percent2=aa[1];

            //保留两位小数
            Q1=BigDecimal.valueOf(Q1).setScale(2,RoundingMode.HALF_UP).doubleValue();
            Q2=BigDecimal.valueOf(Q2).setScale(2,RoundingMode.HALF_UP).doubleValue();
            Q3=BigDecimal.valueOf(Q3).setScale(2,RoundingMode.HALF_UP).doubleValue();
            V=BigDecimal.valueOf(V).setScale(2,RoundingMode.HALF_UP).doubleValue();
            retain=BigDecimal.valueOf(retain).setScale(2,RoundingMode.HALF_UP).doubleValue();

            result.get(i).setType("最小拦蓄");
            result.get(i).setQ1(Q1);
            result.get(i).setQ2(Q2);
            result.get(i).setQ3(Q3);
            result.get(i).setV(V);
            result.get(i).setRetain(retain);
            result.get(i).setPercentage1(percent1);
            result.get(i).setPercentage2(percent2);
        }
        return result;
    }
    public List<Option> MinDischarge(List<Option> Initial, String name){
        initialize(Initial,name);
        for (int i = 0; i < K; i++) {
            Iterate_MinDischarge();
        }

        List<Option> result = new ArrayList<>();
        result.addAll(Arrays.asList(GroupBest));

        //补全最优结果的其他项
        double H_b;
        double H_e;
        double Q_out;
        double Q1;
        double Q2;
        double Q3;
        double V;
        double retain;
        double percent1;
        double percent2;

        for (int i = 0; i < result.size(); i++) {
            H_b=result.get(i).getH1();
            H_e=result.get(i).getH2();
            Q_out=result.get(i).getQOut();
            Q1 = Math.min(GetQ1((H_b+H_e)/2),Q_out);
            Q2 = Math.min(Q_out-Q1,GetQ2((H_b+H_e)/2));
            Q3 = 0;
            V = GetV(H_e);
            retain=Math.max(0,V-GetV(H_begin));
            double[] aa=getPercentage_tth(V);
            percent1=aa[0];
            percent2=aa[1];

            //保留两位小数
            Q1=BigDecimal.valueOf(Q1).setScale(2,RoundingMode.HALF_UP).doubleValue();
            Q2=BigDecimal.valueOf(Q2).setScale(2,RoundingMode.HALF_UP).doubleValue();
            Q3=BigDecimal.valueOf(Q3).setScale(2,RoundingMode.HALF_UP).doubleValue();
            V=BigDecimal.valueOf(V).setScale(2,RoundingMode.HALF_UP).doubleValue();
            retain=BigDecimal.valueOf(retain).setScale(2,RoundingMode.HALF_UP).doubleValue();

            result.get(i).setType("最大削峰");
            result.get(i).setQ1(Q1);
            result.get(i).setQ2(Q2);
            result.get(i).setQ3(Q3);
            result.get(i).setV(V);
            result.get(i).setRetain(retain);
            result.get(i).setPercentage1(percent1);
            result.get(i).setPercentage2(percent2);
        }
        return result;
    }

    /**
     * 初始化
     */
    public void initialize(List<Option> Initial,String name){
        //先从常规调度的结果中获得初始解
        List<Option> Initial_op = new ArrayList<>();
        for (int i = 0; i < Initial.size(); i++) {
            if(Initial.get(i).getName().equals(name)) Initial_op.add(Initial.get(i));
        }
        D=Initial.size()/2;

        //初始化鸟群
        birds = new Option[n][D];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < D; j++) {
                birds[i][j]=new Option();
                birds[i][j].setName(Initial_op.get(j).getName());
                birds[i][j].setTime(Initial_op.get(j).getTime());
                birds[i][j].setQIn(Initial_op.get(j).getQIn());
                birds[i][j].setH1(Initial_op.get(j).getH1());
                birds[i][j].setH2(Initial_op.get(j).getH2());
                birds[i][j].setQOut(Initial_op.get(j).getQOut());
            }
        }
        //初始化个体最优
        IndividualBest = new Option[n][D];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < D; j++) {
                IndividualBest[i][j]=new Option();
                IndividualBest[i][j].setName(Initial_op.get(j).getName());
                IndividualBest[i][j].setTime(Initial_op.get(j).getTime());
                IndividualBest[i][j].setQIn(Initial_op.get(j).getQIn());
                IndividualBest[i][j].setH1(Initial_op.get(j).getH1());
                IndividualBest[i][j].setH2(Initial_op.get(j).getH2());
                IndividualBest[i][j].setQOut(Initial_op.get(j).getQOut());
            }
        }
        //初始化群体最优
        GroupBest = new Option[D];
        for (int j = 0; j < D; j++) {
            GroupBest[j]=new Option();
            GroupBest[j].setName(Initial_op.get(j).getName());
            GroupBest[j].setTime(Initial_op.get(j).getTime());
            GroupBest[j].setQIn(Initial_op.get(j).getQIn());
            GroupBest[j].setH1(Initial_op.get(j).getH1());
            GroupBest[j].setH2(Initial_op.get(j).getH2());
            GroupBest[j].setQOut(Initial_op.get(j).getQOut());
        }
        //初始化速度
        Random random = new Random();
        V=new double[n][D];
        for (int i = 0; i < n; i++) {
            double v_temp = 0.01*(random.nextInt(10)-5);
            for (int j = 0; j < D; j++) {
                V[i][j] = v_temp;
            }
        }
        //初始化随机因子
        r1=random.nextDouble();
        r2=random.nextDouble();
        //初始化惯性权重
        w=wMax;
    }

    /**
     * 一次迭代
     */
    public void Iterate_MinLevel(){
        UpdateLocation();
        UpdateIndividualBest_MinLevel();
        UpdateGroupBest_MinLevel();
        UpdateV();
        UpdateW();
    }
    /**
     * 一次迭代
     */
    public void Iterate_MinDischarge(){
        UpdateLocation();
        UpdateIndividualBest_MinDischarge();
        UpdateGroupBest_MinDischarge();
        UpdateV();
        UpdateW();
    }

    /**
     * 更新位置
     */
    public void UpdateLocation(){
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < D; j++) {
                //继承上一时段的末水位作为本阶段初水位
                if(j==0){
                    birds[i][j].setH1(H_begin);
                }else{
                    birds[i][j].setH1(birds[i][j-1].getH2());
                }
                //计算末水位可行范围
                double H1_after=birds[i][j].getH1();
                double Q_Input =birds[i][j].getQIn();
                double MinQ=this.MinQ.get(j);
                double[] limit=H_Limit(H1_after,Q_Input,MinQ);
                //更新本阶段末水位
                double H2_before=birds[i][j].getH2();
                double H2_after =H2_before+V[i][j];
                //判断是否超出可行范围
                if(j==D-1){
                    H2_after=H_end;
                }
                else{
                    if(H2_after>limit[1]){
                        H2_after=limit[1];
                    }else if (H2_after<limit[0]){
                        H2_after=limit[0];
                    }
                }
                //确定更新后的末水位
                birds[i][j].setH2(H2_after);
                //计算更新后的出库流量
                double Q_out = OnceBalance2(H1_after,Q_Input,H2_after);
                birds[i][j].setQOut(Q_out);
            }
        }
    }
    /**
     * 更新速度
     */
    public void UpdateV(){
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < D; j++) {
                V[i][j]=w*V[i][j]+c1*r1*(IndividualBest[i][j].getH2()-birds[i][j].getH2())+c2*r2*(GroupBest[j].getH2()-birds[i][j].getH2());
            }
        }
    }
    /**
     * 更新惯性权重
     */
    public void UpdateW(){
        w=wMax-(wMax-wMin)/K;
    }
    /**
     * 更新个体最优水位
     */
    public void UpdateIndividualBest_MinLevel(){
        for (int i = 0; i < n; i++) {
            double best=MaxLevel(IndividualBest[i]);
            double value=MaxLevel(birds[i]);
            if(value<best){
                for (int j = 0; j < D; j++) {
                    IndividualBest[i][j].setName(birds[i][j].getName());
                    IndividualBest[i][j].setTime(birds[i][j].getTime());
                    IndividualBest[i][j].setQIn(birds[i][j].getQIn());
                    IndividualBest[i][j].setH1(birds[i][j].getH1());
                    IndividualBest[i][j].setH2(birds[i][j].getH2());
                    IndividualBest[i][j].setQOut(birds[i][j].getQOut());
                }
            }
        }
    }
    /**
     * 更新群体最优水位
     */
    public void UpdateGroupBest_MinLevel(){
        int num=-1;
        double best=MaxLevel(GroupBest);
        for (int i = 0; i < n; i++) {
            double value=MaxLevel(IndividualBest[i]);
            if(value<best){
                best=value;
                num=i;
            }
        }
        if (num!=-1){
            for (int i = 0; i < D; i++) {
                GroupBest[i].setName(IndividualBest[num][i].getName());
                GroupBest[i].setTime(IndividualBest[num][i].getTime());
                GroupBest[i].setQIn(IndividualBest[num][i].getQIn());
                GroupBest[i].setH1(IndividualBest[num][i].getH1());
                GroupBest[i].setH2(IndividualBest[num][i].getH2());
                GroupBest[i].setQOut(IndividualBest[num][i].getQOut());
            }
        }
    }
    /**
     * 更新个体最优流量
     */
    public void UpdateIndividualBest_MinDischarge(){
        for (int i = 0; i < n; i++) {
            double best=MaxDischarge(IndividualBest[i]);
            double value=MaxDischarge(birds[i]);
            if(value<best){
                for (int j = 0; j < D; j++) {
                    IndividualBest[i][j].setName(birds[i][j].getName());
                    IndividualBest[i][j].setTime(birds[i][j].getTime());
                    IndividualBest[i][j].setQIn(birds[i][j].getQIn());
                    IndividualBest[i][j].setH1(birds[i][j].getH1());
                    IndividualBest[i][j].setH2(birds[i][j].getH2());
                    IndividualBest[i][j].setQOut(birds[i][j].getQOut());
                }
            }
        }
    }
    /**
     * 更新群体最优流量
     */
    public void UpdateGroupBest_MinDischarge(){
        int num=-1;
        double best=MaxDischarge(GroupBest);
        for (int i = 0; i < n; i++) {
            double value=MaxDischarge(IndividualBest[i]);
            if(value<best){
                best=value;
                num=i;
            }
        }
        if (num!=-1){
            for (int i = 0; i < D; i++) {
                GroupBest[i].setName(IndividualBest[num][i].getName());
                GroupBest[i].setTime(IndividualBest[num][i].getTime());
                GroupBest[i].setQIn(IndividualBest[num][i].getQIn());
                GroupBest[i].setH1(IndividualBest[num][i].getH1());
                GroupBest[i].setH2(IndividualBest[num][i].getH2());
                GroupBest[i].setQOut(IndividualBest[num][i].getQOut());
            }
        }
    }
    /**
     * 最高水位
     */
    public double MaxLevel(Option[] options){
        double max=options[0].getH2();
        for (int i = 0; i < options.length; i++) {
            double level = options[i].getH2();
            if(level>=max){
                max=level;
            }
        }
        return max;
    }
    /**
     * 最大流量
     */
    public double MaxDischarge(Option[] options){
        double max=options[0].getQOut();
        for (int i = 0; i < options.length; i++) {
            double level = options[i].getQOut();
            if(level>=max){
                max=level;
            }
        }
        return max;
    }



    public static double[] getPercentage_tth(double V){
        double[] result = new double[2];
        result[0]=100*Math.max(0,(V-1297.03))/223.816;
        result[1]=100*Math.max(0,(V-1297.03))/541.681;
        result[0]=BigDecimal.valueOf(result[0]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        result[1]=BigDecimal.valueOf(result[1]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return result;
    }


}
