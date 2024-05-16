package com.cj.model.func.modular.FloodPredict.Calibration.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CalibrationFlow {

    //预报时间
    private Date Time;

    //历史径流
    private Double historyFlow;

    //前期参数模型预报径流
    private Double preParamFlow;

    //率定参数模型预报径流
    private Double newParamFlow;
    //率定期雨量
    private Double historyRainfall;
}
