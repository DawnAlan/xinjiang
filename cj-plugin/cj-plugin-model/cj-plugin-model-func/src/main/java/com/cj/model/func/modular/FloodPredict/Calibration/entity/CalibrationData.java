package com.cj.model.func.modular.FloodPredict.Calibration.entity;

import lombok.Data;
import java.util.Map;

@Data
public class CalibrationData {
    //蒸散发数据
    private Map<String,Object[][]> preRE;
    //历史降雨
    private Map<String,Object[][]> hisR;
    //历史径流
    private Object[][] hisF;
    //上游出库径流
    private Object[][] outF;
    //基础径流
    private Object[][] baseF;
}
