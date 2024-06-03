package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.model.func.modular.FloodPrevent.bean.req.ReqCalculus;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqCurve;
import com.cj.model.func.modular.FloodPrevent.entity.*;
import com.cj.model.func.modular.FloodPrevent.model.Model;



import java.util.*;

import static com.cj.model.func.modular.FloodPrevent.function.Cascade.Read;
import static com.cj.model.func.modular.FloodPrevent.function.Cascade.Update;

public class FloodCalculus {

    public static Map<String,List<Option>> Initialize(String basinStr,ReqCurve reqCurve,ReqCalculus reqCalculus) throws Exception {
        Basin basin = new Basin();
        //配置文件
        Read(basin,basinStr);
        //判断是否从水情管理处获得曲线数据
        String inform=Update(basin,reqCurve);
        //前端传来的数据(本水库数据)
        for (Reservoir reservoir : basin.getReservoirs()) {
            //水库名
            String reservoirName = reservoir.getName();
            //水库起调水位
            try{
                reservoir.setH_begin(reqCalculus.getBeginLevels().get(reservoirName));
            }
            catch (Exception e){
                throw new Exception("输入初始水位异常或与配置文件不符");
            }
            //时段长度、时间戳
            try{
                List<DataFloodPrevent> list = reqCalculus.getIntervals().get(reservoirName);
                reservoir.setT_Delta(list.get(0).getScale());
                List<Date> dates = new ArrayList<>();
                for (DataFloodPrevent dataFloodPrevent : list) {
                    dates.add(dataFloodPrevent.getTime());
                }
                reservoir.setTime(dates);
            }
            catch (Exception e){
                throw new Exception("输入预报数据异常或与配置文件不符");
            }
            //汛限水位
            try{
                double[] limitLevels = new double[12];
                for (int i = 0; i < 12; i++) {
                    limitLevels[i]=reqCalculus.getLimitLevel().get(reservoirName);
                }
                reservoir.setLimitLevels(limitLevels);
            }
            catch (Exception e){
                throw new Exception("输入汛限水位异常或与配置文件不符");
            }
            //生态流量
            try{
                double[] ecos = new double[12];
                for (int i = 0; i < 12; i++) {
                    ecos[i]=reqCalculus.getEco().get(reservoirName);
                }
                reservoir.setEco(ecos);
            }
            catch (Exception e){
                throw new Exception("输入生态流量异常或与配置文件不符");
            }
        }
        //前端传来的数据(本水库下游断面数据)
        for (int i = 0; i < basin.getReservoirs().size(); i++) {
            Reservoir reservoir = basin.getReservoirs().get(i);
            if(i==basin.getReservoirs().size()-1){
                List<Double> Q_interval = new ArrayList<>();
                for (int j = 0; j < reservoir.getTime().size(); j++) {
                    Q_interval.add(0.0);
                }
                reservoir.setQ_Interval(Q_interval);
            }
            else{
                Reservoir nextReservoir = basin.getReservoirs().get(i+1);
                String name = nextReservoir.getName();
                List<DataFloodPrevent> dataFloodPrevents= reqCalculus.getIntervals().get(name);
                List<Double> Q_interval = new ArrayList<>();
                for (DataFloodPrevent dataFloodPrevent : dataFloodPrevents) {
                    Q_interval.add(dataFloodPrevent.getPre());
                }
                reservoir.setQ_Interval(Q_interval);
            }

        }
        //自动更新和转化的参数
        for (Reservoir reservoir : basin.getReservoirs()) {
            //库容曲线数组
            List<CurveParam> curve1 = reservoir.getCapacityCurve();
            double[][] curve2 = new double[2][curve1.size()];
            for (int i = 0; i < curve1.size(); i++) {
                curve2[0][i]=curve1.get(i).getLevel();
                curve2[1][i]=curve1.get(i).getValue();
            }
            reservoir.setLV_Curve(curve2);
            //闸门曲线数组
            Map<String,double[][]> curve_temp = new LinkedHashMap<>();
            List<Gate> gates = reservoir.getGates();
            for (Gate gate : gates) {
                String gateName = gate.getName();
                List<CurveParam> curve = gate.getCurve();
                double[][] curve3 = new double[2][curve.size()];
                for (int j = 0; j < curve.size(); j++) {
                    curve3[0][j] = curve.get(j).getLevel();
                    curve3[1][j] = curve.get(j).getValue();
                }
                curve_temp.put(gateName, curve3);
            }
            reservoir.setLQ_Curves(curve_temp);
            //汛限水位列表
            List<Double> limits = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                limits.add(reservoir.getLimitLevels()[i]);
            }
            reservoir.setLimits(limits);
            //生态流量列表
            List<Double> MinQ = new ArrayList<>();
            for (int i = 0; i < reservoir.getTime().size(); i++) {
                Date date = reservoir.getTime().get(i);
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(date);
                int mon= calendar.get(Calendar.MONTH);
                MinQ.add(reservoir.getEco()[mon]);
            }
            reservoir.setMinQ(MinQ);
            //特征库容
            reservoir.setDeadVolume(Model.GetV(reservoir,reservoir.getDeadLevel()));
            reservoir.setLimitVolume(Model.GetV(reservoir,reservoir.getLimitLevel()));
            reservoir.setNormalVolume(Model.GetV(reservoir,reservoir.getNormalLevel()));
            reservoir.setHeightVolume(Model.GetV(reservoir,reservoir.getHeightLevel()));
            reservoir.setDesignVolume(Model.GetV(reservoir,reservoir.getDesignLevel()));
            reservoir.setProofVolume(Model.GetV(reservoir,reservoir.getProofLevel()));
        }
        //计算一套初始结果
        Map<String,List<Option>> result = new HashMap<>();
        List<Double> upStream = new ArrayList<>();
        //逐水库计算常规调度结果
        for (Reservoir reservoir:basin.getReservoirs()) {
            //水库名
            String reservoirName = reservoir.getName();
            //断面上游区间流量
            List<DataFloodPrevent> q_Interval;
            try{
                q_Interval  = reqCalculus.getIntervals().get(reservoirName);
            }
            catch (Exception e){
                throw new Exception("输入断面预报数据异常");
            }
            //入库流量
            Model.setQInput(reservoir,q_Interval,upStream);

            //常规调度计算
            List<Option> options=Model.Calculate_S1(reservoir);
            result.put(reservoirName,options);

            //保存出库流量
            upStream = new ArrayList<>();
            for (Option option:options) {
                upStream.add(option.getH2());
            }
        }

        //转换输出结果格式
        Map<String,List<Object>> FinalResult =new HashMap<>();


        return result;
    }

    public static Map<String,List<Option>> Calculator(String basinStr,ReqCurve reqCurve,ReqCalculus reqCalculus) throws Exception {
        Basin basin = new Basin();
        //配置文件
        Read(basin,basinStr);
        //判断是否从水情管理处获得曲线数据
        String inform="";
        if(reqCurve==null){
            inform+="未获得曲线；";
        }
        else{
            if(reqCurve.getCapacityCurves()==null){
                inform+="未获得库容曲线；";
            }
            else{
                Map<String,List<CurveParam>> capacityCurves = reqCurve.getCapacityCurves();
                for(Reservoir reservoir:basin.getReservoirs()){
                    boolean judge=true;
                    String reservoirName = reservoir.getName();
                    //库容曲线
                    for(String string:capacityCurves.keySet()){
                        if(string.equals(reservoirName)&&capacityCurves.get(string).size()!=0){
                            reservoir.setCapacityCurve(capacityCurves.get(string));
                            judge=false;
                            break;
                        }
                    }
                    if(judge) inform+="未获得"+reservoirName+"库容曲线；";
                }
            }

            if(reqCurve.getGateCurves()==null){
                inform+="未获得闸门曲线；";
            }
            else{
                Map<String,Map<String,List<CurveParam>>> gateCurves=reqCurve.getGateCurves();
                //创建一个全为true的judge键值对
                Map<String,Map<String,Boolean>> judge =new HashMap<>();
                for(Reservoir reservoir:basin.getReservoirs()){
                    Map<String,Boolean> judge1=new HashMap<>();
                    for (Gate gate:reservoir.getGates()){
                        judge1.put(gate.getName(),true);
                    }
                    judge.put(reservoir.getName(),judge1);

                }
                //逐个水库的逐个闸门进行遍历
                for(Reservoir reservoir:basin.getReservoirs()){
                    String reservoirName = reservoir.getName();
                    for (Gate gate:reservoir.getGates()){
                        String gateName = gate.getName();
                        for(String string:gateCurves.keySet()){
                            if(string.equals(reservoirName)&&gateCurves.get(string)!=null){
                                for(String str:gateCurves.get(string).keySet()){
                                    if(str.equals(gateName)&&gateCurves.get(string).get(str).size()!=0){
                                        gate.setCurve(gateCurves.get(string).get(str));
                                        judge.get(reservoirName).put(gateName,false);
                                    }
                                }
                            }
                        }
                    }
                }
                //判断哪个曲线未进行更新
                for(String string:judge.keySet()){
                    for(String str:judge.get(string).keySet()){
                        if(judge.get(string).get(str)){
                            inform+="未获得"+string+str+"闸门曲线；";
                        }
                    }
                }
            }
        }
        //前端传来的数据(本水库数据)
        for (Reservoir reservoir : basin.getReservoirs()) {
            //水库名
            String reservoirName = reservoir.getName();
            //水库起调水位
            try{
                reservoir.setH_begin(reqCalculus.getBeginLevels().get(reservoirName));
            }
            catch (Exception e){
                throw new Exception("输入初始水位异常或与配置文件不符");
            }
            //时段长度、时间戳
            try{
                List<DataFloodPrevent> list = reqCalculus.getIntervals().get(reservoirName);
                reservoir.setT_Delta(list.get(0).getScale());
                List<Date> dates = new ArrayList<>();
                for (DataFloodPrevent dataFloodPrevent : list) {
                    dates.add(dataFloodPrevent.getTime());
                }
                reservoir.setTime(dates);
            }
            catch (Exception e){
                throw new Exception("输入预报数据异常或与配置文件不符");
            }
            //汛限水位
            try{
                double[] limitLevels = new double[12];
                for (int i = 0; i < 12; i++) {
                    limitLevels[i]=reqCalculus.getLimitLevel().get(reservoirName);
                }
                reservoir.setLimitLevels(limitLevels);
            }
            catch (Exception e){
                throw new Exception("输入汛限水位异常或与配置文件不符");
            }
            //生态流量
            try{
                double[] ecos = new double[12];
                for (int i = 0; i < 12; i++) {
                    ecos[i]=reqCalculus.getEco().get(reservoirName);
                }
                reservoir.setEco(ecos);
            }
            catch (Exception e){
                throw new Exception("输入生态流量异常或与配置文件不符");
            }
        }
        //自动更新和转化的参数
        for (Reservoir reservoir : basin.getReservoirs()) {
            //库容曲线数组
            List<CurveParam> curve1 = reservoir.getCapacityCurve();
            double[][] curve2 = new double[2][curve1.size()];
            for (int i = 0; i < curve1.size(); i++) {
                curve2[0][i]=curve1.get(i).getLevel();
                curve2[1][i]=curve1.get(i).getValue();
            }
            reservoir.setLV_Curve(curve2);
            //闸门曲线数组
            Map<String,double[][]> curve_temp = new LinkedHashMap<>();
            List<Gate> gates = reservoir.getGates();
            for (Gate gate : gates) {
                String gateName = gate.getName();
                List<CurveParam> curve = gate.getCurve();
                double[][] curve3 = new double[2][curve.size()];
                for (int j = 0; j < curve.size(); j++) {
                    curve3[0][j] = curve.get(j).getLevel();
                    curve3[1][j] = curve.get(j).getValue();
                }
                curve_temp.put(gateName, curve3);
            }
            reservoir.setLQ_Curves(curve_temp);
            //汛限水位列表
            List<Double> limits = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                limits.add(reservoir.getLimitLevels()[i]);
            }
            reservoir.setLimits(limits);
            //生态流量列表
            List<Double> MinQ = new ArrayList<>();
            for (int i = 0; i < reservoir.getTime().size(); i++) {
                Date date = reservoir.getTime().get(i);
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(date);
                int mon= calendar.get(Calendar.MONTH);
                MinQ.add(reservoir.getEco()[mon]);
            }
            reservoir.setMinQ(MinQ);
            //特征库容
            reservoir.setDeadVolume(Model.GetV(reservoir,reservoir.getDeadLevel()));
            reservoir.setLimitVolume(Model.GetV(reservoir,reservoir.getLimitLevel()));
            reservoir.setNormalVolume(Model.GetV(reservoir,reservoir.getNormalLevel()));
            reservoir.setHeightVolume(Model.GetV(reservoir,reservoir.getHeightLevel()));
            reservoir.setDesignVolume(Model.GetV(reservoir,reservoir.getDesignLevel()));
            reservoir.setProofVolume(Model.GetV(reservoir,reservoir.getProofLevel()));
        }


        //结果计算
        Map<String,List<Option>> result = new HashMap<>();
        List<Double> upStream = new ArrayList<>();
        for (Reservoir reservoir:basin.getReservoirs()){
            List<Option> options;
            //水库名
            String reservoirName = reservoir.getName();
            //断面上游区间流量
            List<DataFloodPrevent> q_Interval;
            try{
                q_Interval  = reqCalculus.getIntervals().get(reservoirName);
            }
            catch (Exception e){
                throw new Exception("输入断面预报数据异常");
            }
            //入库流量
            Model.setQInput(reservoir,q_Interval,upStream);

            if(reqCalculus.getModelType().get(reservoirName)){
                //水位控制模式
                reservoir.setLevels(reqCalculus.getProcess().get(reservoirName));
                options=LevelControl(reservoir);
            }
            else{
                //流量控制模式
                reservoir.setFlows(reqCalculus.getProcess().get(reservoirName));
                options=FlowControl(reservoir);
            }
            result.put(reservoirName,options);

            //保存出库流量
            upStream = new ArrayList<>();
            for (Option option:options) {
               upStream.add(option.getH2());
            }
        }

        //转换输出结果格式
        Map<String,List<Object>> FinalResult =new HashMap<>();

        
        
        
        
        return result;
    }

    public static List<Option> LevelControl(Reservoir reservoir){
        List<Option> result =new ArrayList<>();
        for (int i = 0; i < reservoir.getTime().size(); i++) {
            Date time = reservoir.getTime().get(i);
            double in;
            double out;
            double H1;
            double H2;

            //当前阶段变量计算
            if(i==0){
                H1= reservoir.getH_begin();
            }
            else{
                H1=result.get(i-1).getH2();
            }
            in=reservoir.getQ_Input().get(i);
            H2=reservoir.getLevels().get(i);
            out=Model.OnceBalance2(reservoir,H1,in,H2);

            Option option = new Option();
            option.setTime(time);
            option.setQIn(in);
            option.setQOut(out);
            option.setH1(H1);
            option.setH2(H2);

            result.add(option);
        }
        return result;
    }
    public static List<Option> FlowControl(Reservoir reservoir){
        List<Option> result =new ArrayList<>();
        for (int i = 0; i < reservoir.getTime().size(); i++) {
            Date time = reservoir.getTime().get(i);
            double in;
            double out;
            double H1;
            double H2;

            if(i==0){
                H1= reservoir.getH_begin();
            }
            else{
                H1=result.get(i-1).getH2();
            }
            in=reservoir.getQ_Input().get(i);
            out=reservoir.getFlows().get(i);
            H2=Model.OnceBalance1(reservoir,H1,in,out);

            Option option = new Option();
            option.setTime(time);
            option.setQIn(in);
            option.setQOut(out);
            option.setH1(H1);
            option.setH2(H2);

            result.add(option);
        }
        return result;
    }


}
