package com.cj.model.func.modular.FloodPrevent.bean.req;

import com.cj.model.func.modular.FloodPrevent.entity.DataFloodPrevent;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReqCalculus {
    //调洪演算模式(水位控制为true,流量控制为false)
    private Map<String, Boolean> modelType;
    //各水库起调水位
    private Map<String,Double> beginLevels;
    //各水库汛限水位
    private Map<String,Double> limitLevel;
    //各水库生态流量
    private Map<String,Double> eco;

    //各水库上游区间流量
    private Map<String, List<DataFloodPrevent>> intervals;
    //传入过程
    private Map<String,List<Double>> process;




}
