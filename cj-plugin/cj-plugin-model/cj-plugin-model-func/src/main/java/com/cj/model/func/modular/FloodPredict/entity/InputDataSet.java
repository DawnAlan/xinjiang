package com.cj.model.func.modular.FloodPredict.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InputDataSet {

    //雨量站小时尺度
    private Map<String, List<RainFallDto>> rainHourData;

    //雨量站日尺度
    private Map<String, List<RainFallDto>> rainDayData;

    //水库水文站
    private List<PredictInputData> flowData;

}
