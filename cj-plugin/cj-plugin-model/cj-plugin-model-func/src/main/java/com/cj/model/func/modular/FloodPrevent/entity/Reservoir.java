package com.cj.model.func.modular.FloodPrevent.entity;


import lombok.Data;
import java.util.*;

@Data
public class Reservoir {
    //通过配置文件获取的参数
    private String name;
    private double DeadLevel;
    private double LimitLevel;
    private double NormalLevel;
    private double HeightLevel;
    private double DesignLevel;
    private double ProofLevel;
    private int coefficient;
    private List<CurveParam> capacityCurve;
    private List<Gate> gates;
    private List<Rule> conventionalRules;
    private List<Rule> flexibleRules;
    /**
     * 下断面安全泄量
     */
    private double downStream;
    /**
     * 下断面超标准洪水流量
     */
    private double downInput;
    /**
     * 单日水位增幅
     */
    private double rise;
    /**
     * 单日水位降幅
     */
    private double decline;

    //配置文件中有默认值，前端也可修改
    private double[] LimitLevels;
    private double[] eco;

    //通过前端获取的参数
    private double H_begin;
    private int T_Delta;
    private double weight;
    private List<Date> Time =new ArrayList<>();
    private List<Double> levels;
    private List<Double> flows;

    //自行计算的参数(特征库容，计算过程中使用，不需要传参)
    private double DeadVolume;
    private double LimitVolume;
    private double NormalVolume;
    private double HeightVolume;
    private double DesignVolume;
    private double ProofVolume;
    //结合上游出库及前端传入数据计算(入库流量，计算过程中使用，不需要传参)
    private List<Double> Q_Input =new ArrayList<>();
    //最大下泄流量(计算过程中使用，不需要传参)
    double MaxQ;
    //转化而来的参数(计算过程中使用，不需要传参)
    private double[][] LV_Curve;
    Map<String,double[][]> LQ_Curves;
    private List<Double> limits = new ArrayList<>();
    private List<Double> MinQ =new ArrayList<>();

    //下断面区间流量(若有下断面，则从前端传参读取，若无下断面，则全置为0)
    private List<Double> Q_Interval =new ArrayList<>();


}
