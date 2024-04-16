package com.cj.model.func.modular.FloodPredict.Calibration.entity;
import lombok.Data;
@Data
public class ShanbeiParam {
    //流域面积
    private Double Area;
    //透水系数
    private Double FB;
    //张力水蓄水含量
    private Double WM;
    //蒸散发折减系数
    private Double KC;
    //流域土壤稳定下渗率
    private Double FC;
    //流域土壤最大土壤下渗率
    private Double FM;
    //霍尔顿下渗流量系数
    private Double K;
    //下渗能力分布系数
    private Double B;
    //地面径流消退系数
    private Double CS;
    //汇流滞时
    private Integer L;
    //合格率

    private Double QC;
}
