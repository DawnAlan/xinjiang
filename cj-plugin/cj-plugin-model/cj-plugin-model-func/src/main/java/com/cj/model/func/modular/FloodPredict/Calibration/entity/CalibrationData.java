package com.cj.model.func.modular.FloodPredict.Calibration.entity;

import lombok.Data;

@Data
public class CalibrationData {
    //流域面积
    private double area;
    //蒸散发数据
    private Object[][] preRE;
    //历史降雨
    private Object[][] hisR;
    //历史径流
    private Object[][] hisF;
    //基础径流
    private Object[][] baseFlow;
}
