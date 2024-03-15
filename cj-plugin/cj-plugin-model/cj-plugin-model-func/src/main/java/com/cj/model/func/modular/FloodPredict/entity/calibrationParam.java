package com.cj.model.func.modular.FloodPredict.entity;
import lombok.Data;
@Data
public class calibrationParam {
    //模型类型(true-自动率定，false-人工率定)
    private Boolean modelType;
    //当前陕北模型参数
    private shanbeiParam param;
    //人工率定参数
    private shanbeiParam manualParam;
    //楼庄子历史数据
    private LzzHydrologyParam lzzHydrologyParam;
    //灌区实时雨量站信息
    private IrrigatedHydrologyParam irrigatedHydrologyParam;
}
