package com.cj.model.func.modular.FloodPredict.entity;

import lombok.Data;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Data
public class ForecastInputParamNew {

    //模型类型(1-中长期 2-短期 3-场次)
    private Integer modelType;
    //预报时间
    private Date predictionTime;
    //数据开始时间
    private Date dataStartTime;
    //时段类型(1-月 2-旬 3-日 4-小时)
    private Integer periodTimeType;
    //时段步长
    private Integer periodTimeStep;
    //时间数量
    private Integer periodTimeNum;
    //楼庄子历史数据
    private LzzHydrologyParam lzzHydrologyParam;
    //灌区实时雨量站信息
    private IrrigatedHydrologyParam irrigatedHydrologyParam;
    //预报雨量
    private List<RainFallDto> rainFallDtos;
    //A3表日均进库
    private List<PredictInputData> inflowRunoffs;
    //前期径流
    private Double preFlow;
    //前期累计降雨
    private Double preRainFall;
    //是否为模拟降雨
    private Boolean isSimulation;

    private String filePath;
    //文件
    public InputStream machineParam;//机器模型参数
    public InputStream machineMaxMin;//机器模型最大最小值
    public InputStream historyData;//模型历史参数
    public InputStream hortonParam;//超渗产流模型参数
}
