package com.cj.model.func.modular.FloodPrevent.model;



import com.cj.model.func.modular.FloodPrevent.entity.DataFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ModelOfTTH {

    int T_Delta;
    double H_begin;
    double H_end;
    double Step = 0.05;

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
            MaxQ.add(120.0);
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

        for (int i = 0; i < Q_Input.size(); i++) {
            UpdateLimitLevel(Time.get(i));
            double beginH;
            double endH;
            double Q_in = Q_Input.get(i);
            double minQ = MinQ.get(i);
            double maxQ = MaxQ.get(i);
            double Q_out;
            double Q_1;
            double Q_2;
            double Q_3;
            double V;
            double Q_max;
            double[] H_Limit;


            if(i==0){
                beginH=H_begin;
            }
            else{
                beginH=H_e.get(i-1);
            }

            if(Q_in>=1013||beginH>=ProofLevel){
                throw new RuntimeException("水位有超过校核水位的风险");
            }
            else if(Q_in>=590){
                //泄水建筑物全部敞开泄流，泄洪方式采用放水涵洞、泄水隧洞、溢洪道相结合。尽量控制库水位不超过校核洪水位。
                Q_max = GetQ1(beginH)+GetQ2(beginH)+GetQ3(beginH);
                endH =OnceBalance1(beginH,Q_in,Q_max);
                H_Limit= H_Limit_S2(beginH,Q_in,minQ);
                if(H_Limit[0]>=ProofLevel){
                    throw new RuntimeException("水位有超过校核水位的风险");
                } //判断能否将水位控制在校核洪水位以下
                else if(H_Limit[0]>HeightLevel){
                    Q_1 = GetQ1((beginH+endH)/2);
                    Q_2 = GetQ2((beginH+endH)/2);
                    Q_3 = GetQ3((beginH+endH)/2);
                    Q_out=Q_1+Q_2+Q_3;
                } //判断能否将水位控制在防洪高水位以下
                else{
                    endH=HeightLevel;
                    Q_out=OnceBalance2(beginH,Q_in,endH);
                    Q_1 = Math.min(Q_out,GetQ1((beginH+endH)/2));
                    Q_2 = Math.min(Q_out-Q_1,GetQ2((beginH+endH)/2));
                    Q_3 = Math.min(Q_out-Q_1-Q_2,GetQ3((beginH+endH)/2));
                }
            }
            else if(Q_in>=maxQ){
                if(beginH>=HeightLevel){
                    //放水涵洞、泄水隧洞及溢洪道均参与泄洪，力求大坝安全。放水涵洞控制下泄120m3/s，泄水隧洞及溢洪道敞开自由泄洪。持续泄洪直至水位回落到汛限水位;
                    Q_max = GetQ1(beginH)+GetQ2(beginH)+GetQ3(beginH);
                    endH =OnceBalance1(beginH,Q_in,Q_max);
                    H_Limit= H_Limit_S2(beginH,Q_in,minQ);
                    //判断能否回落到汛限水位
                    if(H_Limit[0]>LimitLevel){
                        Q_1 = Math.min(120,GetQ1((beginH+endH)/2));
                        Q_2 = GetQ2((beginH+endH)/2);
                        Q_3 = GetQ3((beginH+endH)/2);
                        Q_out=Q_1+Q_2+Q_3;
                        endH=OnceBalance1(beginH,Q_in,Q_out);
                    }
                    else{
                        endH=LimitLevel;
                        Q_out=OnceBalance2(beginH,Q_in,endH);
                        Q_1 = Math.min(120,Math.min(Q_out,GetQ1((beginH+endH)/2)));
                        Q_2 = Math.min(Q_out-Q_1,GetQ2((beginH+endH)/2));
                        Q_3 = Math.min(Q_out-Q_1-Q_2,GetQ3((beginH+endH)/2));
                    }
                }
                else{
                    //按水库的限泄流量下泄，泄洪方式采用放水涵洞和泄水隧洞相结合。通过拦洪错峰延长泄洪时段发挥水库调洪功能，持续泄洪直至水位回落到汛限水位;
                    Q_max = GetQ1(beginH)+GetQ2(beginH);
                    endH =OnceBalance1(beginH,Q_in,Q_max);
                    H_Limit= H_Limit_S2(beginH,Q_in,minQ);
                    //判断能否回落到汛限水位
                    if(H_Limit[0]>LimitLevel){
                        Q_1 = Math.min(120,GetQ1((beginH+endH)/2));
                        Q_2 = GetQ2((beginH+endH)/2);
                        Q_3 = 0;
                        Q_out=Q_1+Q_2+Q_3;
                        endH=OnceBalance1(beginH,Q_in,Q_out);
                    }
                    else{
                        endH=LimitLevel;
                        Q_out=OnceBalance2(beginH,Q_in,endH);
                        Q_1 = Math.min(120,Math.min(Q_out,GetQ1((beginH+endH)/2)));
                        Q_2 = Q_out-Q_1;
                        Q_3 = 0;
                    }
                }
            }
            else{
                //库水位不超过汛限水位,水库下泄流量不超过120m3/s，泄洪方式采用放水涵洞，持续泄洪直至水位回落到汛限水位

                H_Limit= H_Limit_S2(beginH,Q_in,minQ);
                //判断能否回落到汛限水位
                if(H_Limit[0]>LimitLevel){
                    Q_max = Math.min(120,GetQ1(beginH));
                    endH =OnceBalance1(beginH,Q_in,Q_max);
                    Q_1 = Math.min(120,GetQ1((beginH+endH)/2));
                    Q_2 = 0;
                    Q_3 = 0;
                    Q_out=Q_1+Q_2+Q_3;
                    endH=OnceBalance1(beginH,Q_in,Q_out);
                }
                else{
                    endH=LimitLevel;
                    Q_out=OnceBalance2(beginH,Q_in,endH);
                    Q_1 = Math.min(Q_out,GetQ1((beginH+endH)/2));
                    Q_2 = 0;
                    Q_3 = 0;
                }
            }
            V=GetV(beginH);

            H_b.add(BigDecimal.valueOf(beginH).setScale(2, RoundingMode.HALF_UP).doubleValue());
            H_e.add(BigDecimal.valueOf(endH).setScale(2, RoundingMode.HALF_UP).doubleValue());
            Q_Release.add(BigDecimal.valueOf(Q_out).setScale(2, RoundingMode.HALF_UP).doubleValue());
            Q_Release1.add(BigDecimal.valueOf(Q_1).setScale(2, RoundingMode.HALF_UP).doubleValue());
            Q_Release2.add(BigDecimal.valueOf(Q_2).setScale(2, RoundingMode.HALF_UP).doubleValue());
            Q_Release3.add(BigDecimal.valueOf(Q_3).setScale(2, RoundingMode.HALF_UP).doubleValue());
            V_list.add(BigDecimal.valueOf(V).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        Result.add(Q_Input);
        Result.add(H_b);
        Result.add(H_e);
        Result.add(Q_Release);
        Result.add(Q_Release1);
        Result.add(Q_Release2);
        Result.add(Q_Release3);
        Result.add(V_list);

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
            System.out.println(i+1);
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
                Value1.add(BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Value2.add(BigDecimal.valueOf(value2).setScale(2, RoundingMode.HALF_UP).doubleValue());

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
            System.out.println(i+1);
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
                Value1.add(BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Value2.add(BigDecimal.valueOf(value2).setScale(2, RoundingMode.HALF_UP).doubleValue());

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

                Value1.add(BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Value2.add(BigDecimal.valueOf(value2).setScale(2, RoundingMode.HALF_UP).doubleValue());

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
                    Value1.add(BigDecimal.valueOf(value1).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    Value2.add(BigDecimal.valueOf(value2).setScale(2, RoundingMode.HALF_UP).doubleValue());

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



    //头屯河水库泄流能力限制造成的水位限制（常规调度）
    public double[] H_Limit_S2(double level, double Q_Input, double MinQ){

        /*
        （1）当入库流量不大于下游河道安全泄量120m3/s时，库水位不超过汛限水位,水库下泄流量不超过120m3/s，泄洪方式采用放水涵洞，持续泄洪直至水位回落到汛限水位;
        （2）当入库流量大于下游河道安全泄量120m3/s但不超过 590m3/s时，库水位不超过防洪高水位 989.60m，按水库的限泄流量下泄，泄洪方式采用放水涵洞和泄水隧洞相结合。通过拦洪错峰延长 泄洪时段发挥水库调洪功能，持续泄洪直至水位回落到汛限水位;
        （3）当入库流量大于下游河道安全泄量120m3/s但不超过590m3/s时，库水位超过防洪高水位（989.60m）时，并且库水位持续上涨，防洪调度方式转入水库保坝安全的调度阶段。放水涵洞、泄水隧洞及溢洪道均参与泄洪，力求大坝安全。放水涵洞控制下泄120m3/s，泄水隧洞及溢洪道敞开自由泄洪。持续泄洪直至水位回落到汛限水位;
        （4）当入库流量超过590m3/s，但低于校核洪水1013m3/s，库水位超过防洪高水位（989.60m）时，泄水建筑物全部敞开泄流，泄洪方式采用放水涵洞、泄水隧洞、溢洪 道相结合。尽量控制库水位不超过校核洪水位（992.54m）。
        （5）当入库流量继续增加或无减小的趋势，且库水位已达到或超过校核洪水位（992.54m）后，调度运行转入头屯河水库抢险应急预案进行处理，按照预案要求进行应急调度和人员转移撤离。
        */

        double MaxQ1=0;
        double MinQ2=0;
        double QQ0;
        //按最大下泄计算时段最低末水位
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
                QQ0=GetQ1(level)+GetQ2(level);
            }
        }
        else{
            QQ0=Math.min(GetQ1(level),120);
        }
        double Hend=OnceBalance1(level,Q_Input,QQ0);
        //按最低末水位求时段平均水位，再按平均水位计算最大下泄能力
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
                QQ0=GetQ1((level+Hend)/2)+GetQ2((level+Hend)/2);
            }
        }
        else{
            QQ0=Math.min(GetQ1((level+Hend)/2),120);
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
