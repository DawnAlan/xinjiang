package com.cj.model.func.modular.FloodPrevent.model;

import com.cj.model.func.modular.FloodPrevent.entity.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Model {

    /**
     * 常规调度
     */
    public static List<Option> Calculate_S1(Reservoir reservoir){
        List<Option> result = new ArrayList<>();
        for (int i = 0; i < reservoir.getQ_Input().size(); i++) {
            Option option = new Option();
            Date time = reservoir.getTime().get(i);
            UpdateLimitLevel(reservoir,time);
            double beginH;
            double endH;
            double Q_in = reservoir.getQ_Input().get(i);
            double Q_interval = reservoir.getQ_Interval().get(i);
            double nextInput;
            double nextInterval;
            double lastOut;
            if(i==reservoir.getQ_Input().size()-1){
                nextInput=0;
                nextInterval=0;
            }
            else{
                nextInput=reservoir.getQ_Input().get(i+1);
                nextInterval=reservoir.getQ_Interval().get(i+1);
            }
            if(i==0){
                lastOut=999999;
            }
            else{
                lastOut=result.get(i-1).getQOut();
            }
            double minQ = reservoir.getMinQ().get(i);
            double Q_out;
            List<Double> qSingle = new ArrayList<>();
            double V;
            double retain;

            //获取时段初水位
            if(i==0){
                beginH=reservoir.getH_begin();
            }
            else{
                beginH=result.get(i-1).getH2();
            }
            //计算流量过程
            double[] Q =ConventionalCalculate(reservoir,beginH,Q_in,Q_interval,nextInput,nextInterval,lastOut,minQ);
            //补全方案
            Q_out=Q[0];
            for (int j = 1; j < Q.length; j++) {
                qSingle.add(BigDecimal.valueOf(Q[j]).setScale(2,RoundingMode.HALF_UP).doubleValue());
            }
            endH=OnceBalance1(reservoir,beginH,Q_in,Q_out);
            V=GetV(reservoir,endH);
            retain=Math.max(0,V-GetV(reservoir,reservoir.getH_begin()));
            double[] aa=getPercentage(reservoir,V);
            double percent1=aa[0];
            double percent2=aa[1];
            option.setTime(time);
            option.setType("常规调度");
            option.setName(reservoir.getName());
            option.setH1(beginH);
            option.setH2(endH);
            option.setQIn(Q_in);
            option.setQOut(Q_out);
            option.setQSingleString(qSingle.toString());
            option.setV(V);
            option.setRetain(retain);
            option.setPercentage1(percent1);
            option.setPercentage2(percent2);
            option.setLimitString(reservoir.getLimits().toString());
            result.add(option);
        }
        return result;
    }
    /**
     * 灵活调度
     */
    public static List<Option> Calculate_S2(Reservoir reservoir){
        List<Option> result = new ArrayList<>();
        for (int i = 0; i < reservoir.getQ_Input().size(); i++) {
            Option option = new Option();
            Date time = reservoir.getTime().get(i);
            UpdateLimitLevel(reservoir,time);
            double beginH;
            double endH;
            double Q_in = reservoir.getQ_Input().get(i);
            double Q_interval = reservoir.getQ_Interval().get(i);
            double nextInput;
            double nextInterval;
            double lastOut;
            if(i==reservoir.getQ_Input().size()-1){
                nextInput=0;
                nextInterval=0;
            }
            else{
                nextInput=reservoir.getQ_Input().get(i+1);
                nextInterval=reservoir.getQ_Interval().get(i+1);
            }
            if(i==0){
                lastOut=999999;
            }
            else{
                lastOut=result.get(i-1).getQOut();
            }
            double minQ = reservoir.getMinQ().get(i);
            double Q_out;
            List<Double> qSingle = new ArrayList<>();
            double V;
            double retain;

            //设定时段初水位
            if(i==0){
                beginH=reservoir.getH_begin();
            }
            else{
                beginH=result.get(i-1).getH2();
            }
            //计算流量
            double[] Q =FlexibleCalculate(reservoir,beginH,Q_in,Q_interval,nextInput,nextInterval,lastOut,minQ);
            Q_out=Q[0];
            for (int j = 1; j < Q.length; j++) {
                qSingle.add(BigDecimal.valueOf(Q[j]).setScale(2,RoundingMode.HALF_UP).doubleValue());
            }
            endH=OnceBalance1(reservoir,beginH,Q_in,Q_out);
            V=GetV(reservoir,endH);
            retain=Math.max(0,V-GetV(reservoir,reservoir.getH_begin()));
            double[] aa=getPercentage(reservoir,V);
            double percent1=aa[0];
            double percent2=aa[1];
            //补全方案
            option.setTime(time);
            option.setType("灵活调度");
            option.setName(reservoir.getName());
            option.setH1(beginH);
            option.setH2(endH);
            option.setQIn(Q_in);
            option.setQOut(Q_out);
            option.setQSingleString(qSingle.toString());
            option.setV(V);
            option.setRetain(retain);
            option.setPercentage1(percent1);
            option.setPercentage2(percent2);
            option.setLimitString(reservoir.getLimits().toString());
            result.add(option);
        }
        return result;
    }
    /**
     * 预泄调度
     */
    public static List<Option> Calculate_S3(Reservoir reservoir){
        Date time;
        double H1;
        double Q_in;
        double Q_interval;
        double Q_eco;

        double Q_out;
        double H2;
        double V;
        double Retain;
        double p1;
        double p2;
        double[] Limit;
        double decline=reservoir.getH_begin();

        double stepH = 0.2;
        int stepT = 6;
        List<List<Option>> all = new ArrayList<>();
        while (decline>=reservoir.getDeadLevel()){
            //泄流时机
            int t = 0;
            while (t<=reservoir.getTime().size()){
                List<Option> options = new ArrayList<>();
                //最低泄流水位
                boolean judge=false;
                for (int i = 0; i < reservoir.getTime().size(); i++) {
                    List<Double> qSingle = new ArrayList<>();
                    //获取当前时段时间、入库、生态流量、初水位等参数
                    time=reservoir.getTime().get(i);
                    UpdateLimitLevel(reservoir,time);
                    Q_in=reservoir.getQ_Input().get(i);
                    Q_interval = reservoir.getQ_Interval().get(i);
                    Q_eco=reservoir.getMinQ().get(i);
                    double nextInput;
                    double nextInterval;
                    double lastOut;
                    if(i==reservoir.getQ_Input().size()-1){
                        nextInput=0;
                        nextInterval=0;
                    }
                    else{
                        nextInput=reservoir.getQ_Input().get(i+1);
                        nextInterval=reservoir.getQ_Interval().get(i+1);
                    }
                    if(i==0){
                        lastOut=999999;
                    }
                    else{
                        lastOut=options.get(i-1).getQOut();
                    }
                    if(i==0){
                        H1=reservoir.getH_begin();
                    }
                    else{
                        H1=options.get(i-1).getH2();
                    }

                    Option option = new Option();
                    option.setQIn(Q_in);
                    option.setH1(H1);
                    option.setTime(reservoir.getTime().get(i));

                    //泄流能力限制
                    Limit =H_LimitFront(reservoir,H1,Q_in,Q_eco);
                    //水位变化速率限制
                    double[] H_delta = new double[2];
                    H_delta[0] = H1- (double) reservoir.getT_Delta() /3600/24*2;
                    H_delta[1] = H1+ (double) reservoir.getT_Delta() /3600/24*2;

                    //泄流时机之前，正常调度
                    if(i<t){
                        Q_out=FlexibleCalculate(reservoir,H1,Q_in,Q_interval,nextInput,nextInterval,lastOut,Q_eco)[0];
                    }
                    //泄流时机之后，优化调度
                    else{
                        //达到最低水位之前，尽力泄流
                        if(!judge){
                            H2 = Math.max(Math.max(Limit[0],H_delta[0]),decline);
                            Q_out=OnceBalance2(reservoir,H1,Q_in,H2);
                            //达到最低水位或者水位开始攀升，预泄结束
                            if(H2<=decline||H2>=H1){
                                judge=true;
                            }
                        }
                        //泄水结束之后
                        else{
                            //水位未达到汛限水位，维持低水位
                            if(H1<reservoir.getLimitLevel()){
                                List<Gate> gates = GetFlexibleGates(reservoir,H1,Q_in);
                                double Q_max=Model.GetMaxRelease(reservoir,gates,H1);
                                Q_out=Math.min(Q_in,Q_max);
                            }
                            //水位超过汛限水位，按灵活调度进行
                            else {
                                Q_out=FlexibleCalculate(reservoir,H1,Q_in,Q_interval,nextInput,nextInterval,lastOut,Q_eco)[0];
                            }
                        }
                    }
                    H2=OnceBalance1(reservoir,H1,Q_in,Q_out);

                    //各个可用闸门流量
                    double sumQ=Q_out;
                    for (int j = 0; j < reservoir.getGates().size(); j++) {
                        String gateName =  reservoir.getGates().get(j).getName();
                        double qMax = GetSingleQ(reservoir,H1,gateName);
                        double q = Math.min(qMax,sumQ);
                        sumQ=sumQ-q;
                        q=BigDecimal.valueOf(q).setScale(2,RoundingMode.HALF_UP).doubleValue();
                        qSingle.add(q);
                    }

                    option.setQSingleString(qSingle.toString());
                    option.setH2(H2);
                    option.setQOut(Q_out);
                    options.add(option);
                }
                all.add(options);
                t=t+stepT;
            }
            decline=decline-stepH;
        }
        //选出最优解
        int num = 0;
        double best = Value(reservoir,all.get(0));
        for (int i = 0; i < all.size(); i++) {
            double value = Value(reservoir,all.get(i));
            if(value<best){
                best=value;
                num=i;
            }
        }
        //补全其他数据
        for (int i = 0; i < all.get(num).size(); i++) {
            Option option = all.get(num).get(i);
            H2=option.getH2();
            V=GetV(reservoir,H2);
            Retain=Math.max(0,V-GetV(reservoir,reservoir.getH_begin()));
            p1=getPercentage(reservoir,V)[0];
            p2=getPercentage(reservoir,V)[1];

            option.setName(reservoir.getName());
            option.setType("预泄调度");
            option.setTime(reservoir.getTime().get(i));
            option.setV(V);
            option.setRetain(Retain);
            option.setPercentage1(p1);
            option.setPercentage2(p2);
            option.setLimitString(reservoir.getLimits().toString());
        }

        return all.get(num);
    }


    public static double[] ConventionalCalculate(Reservoir reservoir,double level,double input,double interval,double nextInput,double nextInterval,double lastOut,double minQ){
        //流量数组
        double[] Q = new double[reservoir.getGates().size()+1];
        double out= GetMaxQ1(reservoir,level,input,interval);

        //避免流量过大，水位骤降
        double tryLevel = OnceBalance1(reservoir,level,input,out);
        if(level>=reservoir.getProofLevel()&&tryLevel<reservoir.getProofLevel()){
            //保证下一时段水位不会回升
            double nextQ = GetMaxQ1(reservoir, tryLevel,nextInput,nextInterval);
            while (nextQ<nextInput&&tryLevel<reservoir.getProofLevel()){
                out-=3;
                tryLevel = OnceBalance1(reservoir,level,input,out);
                nextQ = GetMaxQ1(reservoir, tryLevel,nextInput,nextInterval);
            }
            //保证本时段水位缓慢下降
            if(level>=reservoir.getProofLevel()&&tryLevel<reservoir.getProofLevel()){
                out=OnceBalance2(reservoir,level,input,(level+tryLevel)/2);
                if(out>lastOut) out=lastOut;
            }

        }
        else if(level>=reservoir.getDesignLevel()&&tryLevel<reservoir.getDesignLevel()){
            //保证下一时段水位不会回升
            double nextQ = GetMaxQ1(reservoir, tryLevel,nextInput,nextInterval);
            while (nextQ<nextInput&&tryLevel<reservoir.getDesignLevel()){
                out-=3;
                tryLevel = OnceBalance1(reservoir,level,input,out);
                nextQ = GetMaxQ1(reservoir, tryLevel,nextInput,nextInterval);
            }
            //保证本时段水位缓慢下降
            if(level>=reservoir.getDesignLevel()&&tryLevel<reservoir.getDesignLevel()){
                out=OnceBalance2(reservoir,level,input,(level+tryLevel)/2);
                if(out>lastOut) out=lastOut;
            }
        }
        else if(level>=reservoir.getHeightLevel()&&tryLevel<reservoir.getHeightLevel()){
            //保证下一时段水位不会回升
            double nextQ = GetMaxQ1(reservoir, tryLevel,nextInput,nextInterval);
            while (nextQ<nextInput&&tryLevel<reservoir.getHeightLevel()){
                out-=3;
                tryLevel = OnceBalance1(reservoir,level,input,out);
                nextQ = GetMaxQ1(reservoir, tryLevel,nextInput,nextInterval);
            }
            //保证本时段水位缓慢下降
            if(level>=reservoir.getHeightLevel()&&tryLevel<reservoir.getHeightLevel()){
                out=OnceBalance2(reservoir,level,input,(level+tryLevel)/2);
                if(out>lastOut) out=lastOut;
            }
        }

        if(out<minQ) out=minQ;
        //总出库流量
        Q[0]=out;
        //可用闸门
        List<Gate> gates = GetConventionalGates(reservoir,level,input);
        //可用闸门流量
        double sum=0;
        for (int i = 0; i < gates.size(); i++) {
            Gate gate = gates.get(i);
            Q[i+1]=Math.min(GetSingleQ(reservoir,level,gate.getName()),Q[0]-sum);
            sum=sum+Q[i+1];
        }
        //不可用闸门流量
        for (int i = gates.size()+1; i < Q.length; i++) {
            Q[i]=0;
        }
        return Q;
    }
    public static double[] FlexibleCalculate(Reservoir reservoir,double level,double input,double interval,double nextInput,double nextInterval,double lastOut,double minQ){
        //流量数组
        double[] Q = new double[reservoir.getGates().size()+1];
        double out= GetMaxQ2(reservoir,level,input,interval);

        //避免流量过大，水位骤降
        double tryLevel = OnceBalance1(reservoir,level,input,out);
        if(level>=reservoir.getProofLevel()&&tryLevel<reservoir.getProofLevel()){
            //保证下一时段水位不会回升
            double nextQ = GetMaxQ2(reservoir, tryLevel,nextInput,nextInterval);
            while (nextQ<nextInput&&tryLevel<reservoir.getProofLevel()){
                out-=3;
                tryLevel = OnceBalance1(reservoir,level,input,out);
                nextQ = GetMaxQ2(reservoir, tryLevel,nextInput,nextInterval);
            }
            //保证本时段水位缓慢下降
            if(level>=reservoir.getProofLevel()&&tryLevel<reservoir.getProofLevel()){
                out=OnceBalance2(reservoir,level,input,(level+tryLevel)/2);
                if(out>lastOut) out=lastOut;
            }

        }
        else if(level>=reservoir.getDesignLevel()&&tryLevel<reservoir.getDesignLevel()){
            //保证下一时段水位不会回升
            double nextQ = GetMaxQ2(reservoir, tryLevel,nextInput,nextInterval);
            while (nextQ<nextInput&&tryLevel<reservoir.getDesignLevel()){
                out-=3;
                tryLevel = OnceBalance1(reservoir,level,input,out);
                nextQ = GetMaxQ2(reservoir, tryLevel,nextInput,nextInterval);
            }
            //保证本时段水位缓慢下降
            if(level>=reservoir.getDesignLevel()&&tryLevel<reservoir.getDesignLevel()){
                out=OnceBalance2(reservoir,level,input,(level+tryLevel)/2);
                if(out>lastOut) out=lastOut;
            }
        }
        else if(level>=reservoir.getHeightLevel()&&tryLevel<reservoir.getHeightLevel()){
            //保证下一时段水位不会回升
            double nextQ = GetMaxQ2(reservoir, tryLevel,nextInput,nextInterval);
            while (nextQ<nextInput&&tryLevel<reservoir.getHeightLevel()){
                out-=3;
                tryLevel = OnceBalance1(reservoir,level,input,out);
                nextQ = GetMaxQ2(reservoir, tryLevel,nextInput,nextInterval);
            }
            //保证本时段水位缓慢下降
            if(level>=reservoir.getHeightLevel()&&tryLevel<reservoir.getHeightLevel()){
                out=OnceBalance2(reservoir,level,input,(level+tryLevel)/2);
                if(out>lastOut) out=lastOut;
            }
        }
        if(out<minQ) out=minQ;
        //总出库流量
        Q[0]=out;
        //可用闸门
        List<Gate> gates = GetFlexibleGates(reservoir,level,input);
        //各个可用闸门流量
        double sum=0;
        for (int i = 0; i < gates.size(); i++) {
            Gate gate = gates.get(i);
            Q[i+1]=Math.min(GetSingleQ(reservoir,level,gate.getName()),Q[0]-sum);
            sum=sum+Q[i+1];
        }
        //不可用闸门流量
        for (int i = gates.size()+1; i < Q.length; i++) {
            Q[i]=0;
        }
        return Q;
    }
    public static List<Gate> GetConventionalGates(Reservoir reservoir,double level,double q){
        List<String> names = new ArrayList<>();
        List<Gate> gates = new ArrayList<>();
        for (Rule rule : reservoir.getConventionalRules()) {
            double minQ = rule.getMinQ();
            double maxQ = rule.getMaxQ();
            double minH = rule.getMinH();
            double maxH = rule.getMaxH();
            if (level >= minH && level < maxH && q >= minQ && q < maxQ) {
                reservoir.setMaxQ(rule.getQOut());
                names = rule.getGates();
                break;
            }
        }
        for (String name : names) {
            for (Gate gate : reservoir.getGates()) {
                if (gate.getName().equals(name)) {
                    gates.add(gate);
                    break;
                }
            }
        }
        return gates;
    }
    public static List<Gate> GetFlexibleGates(Reservoir reservoir,double level,double q){
        List<String> names = new ArrayList<>();
        List<Gate> gates = new ArrayList<>();
        for (Rule rule : reservoir.getFlexibleRules()) {
            double minQ = rule.getMinQ();
            double maxQ = rule.getMaxQ();
            double minH = rule.getMinH();
            double maxH = rule.getMaxH();
            if (level >= minH && level < maxH && q >= minQ && q < maxQ) {
                reservoir.setMaxQ(rule.getQOut());
                names = rule.getGates();
                break;
            }
        }
        for (String name : names) {
            for (Gate gate : reservoir.getGates()) {
                if (gate.getName().equals(name)) {
                    gates.add(gate);
                    break;
                }
            }

        }
        return gates;
    }
    /**
     *水库水位限制，从前往后推
     */
    public static double[] H_LimitFront(Reservoir reservoir,double level,double Q_Input,double MinQ){
        //闸门最大下泄能力
        List<Gate> gates = GetFlexibleGates(reservoir,level,Q_Input);
        double QQ0 = GetMaxRelease(reservoir,gates,level);

        double H_eco = OnceBalance1(reservoir,level,Q_Input,MinQ);
        double H_min =OnceBalance1(reservoir,level,Q_Input,QQ0);

        double[] H_Limit= new double[2];
        H_Limit[0]=Math.max(H_min,reservoir.getDeadLevel());
        H_Limit[1]=H_eco;

        if(reservoir.getProofLevel()>=H_Limit[0]&&reservoir.getProofLevel()<=H_Limit[1]) {
            H_Limit[1]=reservoir.getProofLevel();
        }
        else if(reservoir.getDesignLevel()>=H_Limit[0]&&reservoir.getDesignLevel()<=H_Limit[1]){
            H_Limit[1]=reservoir.getDesignLevel();
        }
        return H_Limit;
    }
    /**
     *最大泄流能力
     */
    public static double GetMaxRelease(Reservoir reservoir, List<Gate> gates, double level){
        double Q=0;
        for (Gate gate : gates) {
            Q = Q + GetSingleQ(reservoir,level, gate.getName());
        }
        //安全下泄
        Q=Math.min(Q,reservoir.getMaxQ());
        return Q;
    }


    public static double OnceBalance1(Reservoir reservoir,double H_begin, double Q_in, double Q_out){
        double V_end=GetV(reservoir,H_begin)*reservoir.getCoefficient()+(Q_in-Q_out)*reservoir.getT_Delta();
        return GetH(reservoir,V_end/reservoir.getCoefficient());
    }
    public static double OnceBalance2(Reservoir reservoir,double H_begin, double Q_in, double H_end){
        return Q_in-(GetV(reservoir,H_end)-GetV(reservoir,H_begin))*reservoir.getCoefficient()/reservoir.getT_Delta();
    }
    public static double GetV(Reservoir reservoir,double level){
        double[][] LV_Curve= reservoir.getLV_Curve();
        double Volume;
        int n;
        if(level<=LV_Curve[0][0]){
            n=0;
        }
        else if(level>=LV_Curve[0][LV_Curve[0].length-1]){
            n=LV_Curve[0].length;
        }
        else {
            n=0;
            for (int i = 1; i < LV_Curve[0].length; i++) {
                if(level>=LV_Curve[0][i-1]&&level<LV_Curve[0][i]){
                    n=i;
                    break;
                }
            }
        }

        if(n==0){
            Volume=LV_Curve[1][0]+(level-LV_Curve[0][0])*(LV_Curve[1][1]-LV_Curve[1][0])/(LV_Curve[0][1]-LV_Curve[0][0]);
        }
        else if(n==LV_Curve[0].length){
            Volume=LV_Curve[1][n-1]+(level-LV_Curve[0][n-1])*(LV_Curve[1][n-1]-LV_Curve[1][n-2])/(LV_Curve[0][n-1]-LV_Curve[0][n-2]);
        }
        else{
            Volume=LV_Curve[1][n-1]+(level-LV_Curve[0][n-1])*(LV_Curve[1][n]-LV_Curve[1][n-1])/(LV_Curve[0][n]-LV_Curve[0][n-1]);
        }
        return Volume;
    }
    public static double GetH(Reservoir reservoir,double Volume){
        double[][] LV_Curve= reservoir.getLV_Curve();
        double level;
        int n;
        if(Volume<=LV_Curve[1][0]){
            n=0;
        }
        else if(Volume>=LV_Curve[1][LV_Curve[1].length-1]){
            n=LV_Curve[1].length;
        }
        else {
            n=0;
            for (int i = 1; i < LV_Curve[1].length; i++) {
                if(Volume>=LV_Curve[1][i-1]&Volume<LV_Curve[1][i]){
                    n=i;
                    break;
                }
            }
        }

        if(n==0){
            level=LV_Curve[0][0]+(Volume-LV_Curve[1][0])*(LV_Curve[0][1]-LV_Curve[0][0])/(LV_Curve[1][1]-LV_Curve[1][0]);
        }
        else if(n==LV_Curve[0].length){
            level=LV_Curve[0][n-1]+(Volume-LV_Curve[1][n-1])*(LV_Curve[0][n-1]-LV_Curve[0][n-2])/(LV_Curve[1][n-1]-LV_Curve[1][n-2]);
        }
        else{
            level=LV_Curve[0][n-1]+(Volume-LV_Curve[1][n-1])*(LV_Curve[0][n]-LV_Curve[0][n-1])/(LV_Curve[1][n]-LV_Curve[1][n-1]);
        }
        return  level;
    }
    public static double GetSingleQ(Reservoir reservoir,double level, String name){
        double[][] LQ_Curve = reservoir.getLQ_Curves().get(name);
        double MaxQ;
        int n;
        if (level<=LQ_Curve[0][0]){
            n=0;
        }
        else if(level>=LQ_Curve[0][LQ_Curve[0].length-1]){
            n=LQ_Curve[0].length;
        }
        else{
            n=0;
            for (int i = 1; i < LQ_Curve[0].length; i++) {
                if(level>=LQ_Curve[0][i-1]&&level<LQ_Curve[0][i]){
                    n=i;
                    break;
                }
            }
        }

        if(n==0){
            MaxQ=LQ_Curve[1][0]+(level-LQ_Curve[0][0])*(LQ_Curve[1][1]-LQ_Curve[1][0])/(LQ_Curve[0][1]-LQ_Curve[0][0]);
        }
        else if(n==LQ_Curve[0].length){
            MaxQ=LQ_Curve[1][n-1]+(level-LQ_Curve[0][n-1])*(LQ_Curve[1][n-1]-LQ_Curve[1][n-2])/(LQ_Curve[0][n-1]-LQ_Curve[0][n-2]);
        }
        else{
            MaxQ=LQ_Curve[1][n-1]+(level-LQ_Curve[0][n-1])*(LQ_Curve[1][n]-LQ_Curve[1][n-1])/(LQ_Curve[0][n]-LQ_Curve[0][n-1]);
        }
        return MaxQ;
    }

    public static void UpdateLimitLevel(Reservoir reservoir,Date time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int month = calendar.get(Calendar.MONTH);
        reservoir.setLimitLevel(reservoir.getLimitLevels()[month]);
        reservoir.setLimitVolume(GetV(reservoir,reservoir.getLimitLevel()));
    }
    public static double[] getPercentage(Reservoir reservoir,double V){

        double[] result = new double[2];
        result[0]=100*Math.max(0,(V-reservoir.getLimitVolume()))/(reservoir.getHeightVolume()-reservoir.getLimitVolume());
        result[1]=100*Math.max(0,(V-reservoir.getLimitVolume()))/(reservoir.getProofVolume()-reservoir.getLimitVolume());

        result[0]= BigDecimal.valueOf(result[0]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        result[1]=BigDecimal.valueOf(result[1]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return result;
    }
    /**
     * 返回最高水位的序号
     */
    public static int MaxLevel(List<Option> options){
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
    public static int MinLevel(List<Option> options){
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
    public static double Value(Reservoir reservoir,List<Option> options){
        double value=0;
        double rate = reservoir.getWeight();
        int max = MaxLevel(options);
        double maxLevel=options.get(max).getH2();
        double endLevel=options.get(options.size()-1).getH2();

        //最高水位超过校核水位的惩罚
        if(maxLevel>=reservoir.getProofLevel()){
            value=value+1000000*(maxLevel-reservoir.getProofLevel());
        }
        //最高水位超过设计水位的惩罚
        else if(maxLevel>=reservoir.getDesignLevel()){
            value=value+10000*(maxLevel-reservoir.getDesignLevel());
        }
        //最低水位低于汛限水位的惩罚
        double delta=0;
        int num=0;
        for(Option option:options){
            double h = option.getH2();
            if(h<reservoir.getLimitLevel()){
                delta=delta+reservoir.getLimitLevel()-h;
                num++;
            }
        }
        if(num!=0) delta=delta/num;
        //最高水位超过汛限水位的惩罚
        double over=Math.max(0,maxLevel-reservoir.getLimitLevel());
        value+=rate*delta+over;
        //末水位低于汛限水位的惩罚
        if(endLevel<reservoir.getLimitLevel()){
            value=value+100*(reservoir.getLimitLevel()-endLevel);
        }
        return value;
    }

    public static void setQInput(Reservoir reservoir, List<DataFloodPrevent> intervals, List<Double> upStream){
        List<Double> Q_Input=new ArrayList<>();
        if(upStream.isEmpty()){
            for (DataFloodPrevent interval : intervals) {
                Q_Input.add(interval.getPre());
            }
        }
        else{
            for (int i = 0; i < intervals.size(); i++) {
                Q_Input.add(intervals.get(i).getPre()+upStream.get(i));
            }
        }
        reservoir.setQ_Input(Q_Input);
    }



    public static double GetMaxQ1(Reservoir reservoir, double level, double input, double interval){
        double Q;
        //可用闸门
        List<Gate> gates = GetConventionalGates(reservoir,level,input);
        //最大下泄能力
        double maxQ = GetMaxRelease(reservoir,gates,level);
        //抑制水位下降的流量限制
        double decline = reservoir.getDecline()*reservoir.getT_Delta()/(24*3600);
        double maxQ1=OnceBalance2(reservoir,level,input,(level-decline));
        //控制下游入库导致的流量限制
        double maxQ2;
        //水位小于汛限水位，控制下游入库小于安全下泄流量
        if(level<=reservoir.getHeightLevel()){
            maxQ2=Math.max(0,reservoir.getDownStream()-interval);
        }
        //水位大于汛限水位，控制下游入库介于安全下泄流量和下游超标准洪水之间
        else{
            double limit =  (GetV(reservoir,level)- reservoir.getHeightLevel())
                    *(reservoir.getDownInput()-reservoir.getDownStream())
                    /(reservoir.getProofVolume()-reservoir.getHeightVolume())
                    +reservoir.getDownStream();
            maxQ2=Math.max(0,limit-interval);
        }
        //恢复至汛限水位所需下泄流量
        double maxQ3=(GetV(reservoir,level)-reservoir.getLimitVolume())*reservoir.getCoefficient()/reservoir.getT_Delta()+input;
        Q=Math.min(Math.min(maxQ,maxQ1),Math.min(maxQ2,maxQ3));
        return Q;
    }

    public static double GetMaxQ2(Reservoir reservoir, double level, double input, double interval){
        double Q;
        //可用闸门
        List<Gate> gates = GetFlexibleGates(reservoir,level,input);
        //最大下泄能力
        double maxQ = GetMaxRelease(reservoir,gates,level);
        //抑制水位下降的流量限制
        double decline = reservoir.getDecline()*reservoir.getT_Delta()/(24*3600);
        double maxQ1=OnceBalance2(reservoir,level,input,(level-decline));
        //控制下游入库导致的流量限制
        double maxQ2;
        //水位小于汛限水位，控制下游入库小于安全下泄流量
        if(level<=reservoir.getHeightLevel()){
            maxQ2=Math.max(0,reservoir.getDownStream()-interval);
        }
        //水位大于汛限水位，控制下游入库介于安全下泄流量和下游超标准洪水之间
        else{
            double limit =  (GetV(reservoir,level)- reservoir.getHeightLevel())
                    *(reservoir.getDownInput()-reservoir.getDownStream())
                    /(reservoir.getProofVolume()-reservoir.getHeightVolume())
                    +reservoir.getDownStream();
            maxQ2=Math.max(0,limit-interval);
        }
        //恢复至汛限水位所需下泄流量
        double maxQ3=(GetV(reservoir,level)-reservoir.getLimitVolume())*reservoir.getCoefficient()/reservoir.getT_Delta()+input;
        Q=Math.min(Math.min(maxQ,maxQ1),Math.min(maxQ2,maxQ3));
        return Q;
    }

}
