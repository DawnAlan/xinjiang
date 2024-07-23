package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.model.func.modular.FloodPrevent.bean.req.ReqCurve;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.res.ResOption;
import com.cj.model.func.modular.FloodPrevent.entity.*;
import com.cj.model.func.modular.FloodPrevent.model.Model;

import java.io.File;
import java.util.*;

public class RollUpdate {

    public static List<ResOption> calculator(String basinStr, ReqFloodPrevent reqFloodPrevent, ReqCurve reqCurve, int choose) throws Exception {
        Basin basin = new Basin();
        //配置文件内包含的参数
        Cascade.Read(basin,basinStr);
        //从水情管理处获得的曲线数据
        String inform = Cascade.Update(basin,reqCurve);
        //前端传来的数据(本水库数据)
        for (Reservoir reservoir : basin.getReservoirs()) {
            //水库名
            String reservoirName = reservoir.getName();
            //水库起调水位
            try{
                reservoir.setH_begin(reqFloodPrevent.getBeginLevels().get(reservoirName));
            }
            catch (Exception e){
                throw new Exception("输入实时水位异常");
            }
            //时段长度、时间戳
            try{
                List<DataFloodPrevent> list = reqFloodPrevent.getIntervals().get(reservoirName);
                reservoir.setT_Delta(list.get(0).getScale());
                List<Date> dates = new ArrayList<>();
                for (DataFloodPrevent dataFloodPrevent : list) {
                    dates.add(dataFloodPrevent.getTime());
                }
                reservoir.setTime(dates);
            }
            catch (Exception e){
                throw new Exception("输入预报数据异常");
            }
            //动态汛限水位
            try{
                if (reqFloodPrevent.getLimitLevels() != null && reqFloodPrevent.getLimitLevels().get(reservoirName).length == 12) {
                    reservoir.setLimitLevels(reqFloodPrevent.getLimitLevels().get(reservoirName));
                }
            }
            catch (Exception e){
                throw new Exception("输入汛限水位异常");
            }
            //动态生态流量
            try{
                if (reqFloodPrevent.getEco() != null && reqFloodPrevent.getEco().get(reservoirName).length == 12) {
                    reservoir.setEco(reqFloodPrevent.getEco().get(reservoirName));
                }
            }
            catch (Exception e){
                throw new Exception("输入生态流量异常");
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
                List<DataFloodPrevent> dataFloodPrevents= reqFloodPrevent.getIntervals().get(name);
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

        List<Option> options = new ArrayList<>();
        List<Double> out= new ArrayList<>();
        //方案计算
        if(choose==1){
            //常规调度
            for (int i = 0; i < basin.getReservoirs().size(); i++) {
                //水库
                Reservoir reservoir = basin.getReservoirs().get(i);
                //水库名
                String reservoirName = reservoir.getName();
                //断面上游区间流量
                List<DataFloodPrevent> q_Interval;
                try{
                    q_Interval  = reqFloodPrevent.getIntervals().get(reservoirName);
                }
                catch (Exception e){
                    throw new Exception("输入断面预报数据异常");
                }
                //常规调度
                Model.setQInput(reservoir,q_Interval,out);
                List<Option> options_s1 = Model.Calculate_S1(reservoir);

                //保存出库流量
                out= new ArrayList<>();
                for (Option option : options_s1) {
                    out.add(option.getQOut());
                }
                //保存调度结果
                options.addAll(options_s1);
            }
        }
        else if(choose==2){
            //梯级联调
            for (int i = 0; i < basin.getReservoirs().size(); i++) {
                //水库
                Reservoir reservoir = basin.getReservoirs().get(i);
                //水库名
                String reservoirName = reservoir.getName();
                //断面上游区间流量
                List<DataFloodPrevent> q_Interval;
                try{
                    q_Interval  = reqFloodPrevent.getIntervals().get(reservoirName);
                }
                catch (Exception e){
                    throw new Exception("输入断面预报数据异常");
                }

                Model.setQInput(reservoir,q_Interval,out);
                List<Option> options_s1 = Model.Calculate_S2(reservoir);

                //保存出库流量
                out= new ArrayList<>();
                for (Option option : options_s1) {
                    out.add(option.getQOut());
                }
                //保存调度结果
                options.addAll(options_s1);
            }

        }
        else{
            return null;
        }

        //调度结果保存
        File tempFile = File.createTempFile("options",".xlsx");
        String path= tempFile.getAbsolutePath();
        Cascade.Write(path,options);

        //返回结果路径及文件名称
        List<ResOption> result = new ArrayList<>();

        ResOption resOption = new ResOption();
        resOption.setPath(path);
        if(choose==1){
            resOption.setName(reqFloodPrevent.getProgrammeName()+"-常规调度");
        }
        else{
            resOption.setName(reqFloodPrevent.getProgrammeName()+"-梯级联调");
        }

        resOption.setInform(inform);
        result.add(resOption);

        return result;
    }






}
