package com.cj.model.func.modular.FloodPredict.Calibration.entity;

import lombok.Data;

import java.util.List;

@Data
public class CalibrationOutput {
    //断面位置
    private String location;

    //返回的径流序列（历史真实径流，前期参数预报径流，新率定参数径流）
    private List<CalibrationFlow> flowList;
    //新率定的陕北模型参数
    private ShanbeiParam param;
}
