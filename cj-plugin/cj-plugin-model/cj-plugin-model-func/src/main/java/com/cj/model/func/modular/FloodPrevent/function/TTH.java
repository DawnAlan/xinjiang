package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.model.func.modular.FloodPrevent.entity.*;
import com.cj.model.func.modular.FloodPrevent.model.Model;

import java.util.*;

public class TTH {
    public static List<Option> Calculate(String fileName, Object[][] pre, int delta){
        List<Option> result;

        Basin basin = new Basin();
        //配置文件
        Cascade.Read(basin,fileName);
        //头屯河水库
        Reservoir reservoir = basin.getReservoirs().get(1);

        //预报来水数据
        List<Date> Time = new ArrayList<>();
        List<Double> Q_Input=new ArrayList<>();
        for (Object[] objects : pre) {
            Date t = (Date) objects[1];
            Time.add(t);
            Q_Input.add((double) objects[2]);
        }
        reservoir.setTime(Time);
        reservoir.setQ_Input(Q_Input);
        reservoir.setT_Delta(delta);
        //起调水位
        reservoir.setH_begin(988);
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

        result= Model.Calculate_S1(reservoir);
        return result;
    }

}
