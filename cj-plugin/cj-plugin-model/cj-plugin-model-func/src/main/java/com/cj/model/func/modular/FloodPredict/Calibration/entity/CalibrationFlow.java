package com.cj.model.func.modular.FloodPredict.Calibration.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CalibrationFlow {

    //预报时间
    private Object[] Time;

    //历史径流
    private double[] historyFlow;

    //前期参数模型预报径流
    private double[] preParamFlow;

    //率定参数模型预报径流
    private double[] newParamFlow;
}
