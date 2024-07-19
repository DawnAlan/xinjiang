package com.cj.model.func.modular.FloodPredict.Calibration.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CalibrationOutput {
    //返回的径流序列（历史真实径流，前期参数预报径流，新率定参数径流）
    private List<CalibrationFlow> flowList;
    //新率定的陕北模型参数
    private Map<String,ShanbeiParam> param;
    //报错信息
    private String error;
}
