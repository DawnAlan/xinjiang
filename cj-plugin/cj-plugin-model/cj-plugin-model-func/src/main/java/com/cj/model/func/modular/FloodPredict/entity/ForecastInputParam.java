package com.cj.model.func.modular.FloodPredict.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class ForecastInputParam {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dataSetStartTime;//率定数据集开始时间，必要

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateSetEndTime;//率定数据集结束时间，必要

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date testSetStartTime;//测试集开始时间，必要

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date testSetEndTime;//测试集结束时间，必要

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preStartTime;//预报开始时间，必要

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preEndTime;//预报结束时间，必要

    //预报断面
    private String location;
    //预报时间类型
    private String period;
    //时段步长
    private Integer periodStepSize;
    //时段数量
    private Integer periodStepNumber;
    //是否为实时预报
    private Boolean isRealtime;
    //是否为短期预报
    private Boolean isShortForecast;
    //是否为历史复验
    private Boolean isHistory;
    //是否为模拟降雨
    private Boolean isSimulation;
    //是否为融雪模型
    private Boolean isSnowMeltModel;
    //是否需要重新训练模型
    private Boolean isTrain;
    //模型名称
    private String model;
    //分解层数
    public Integer vmdK;
    //模型参数表格路径
    private List<TemporaryXlsx> xlsx;
    //前期径流
    private Double preFlow;
    //前期累计降雨
    private Double preRainFall;
    //前期因子的数量
    public Integer history_factor;
    //前期天数
    public Integer history_day;


    private String netClass;//神经网络模式，必要

    private String clusterMethod;//聚类方式,在径向基网络下，必要

    private Integer[] inputIndex;//输入节点的序号,必要

    private Double q_max;//流量阈值，最大值

    private Double q_min;//流量阈值，最小值

    private String layerCount;//神经网络节点,必要

    private Double ERROR;//训练误差阈值

    private Integer trainNum;//训练次数

    private double width;//mean-shift算法半径

    private double shiftError;//mean-shift算法偏差

    private double rate;//学习率

    private double mobp;//动量系数

    private double maxRate;//最大学习率

    private double minRate;//最小学习率

    public double maxGamma;//

    public double minGamma;

    public double maxC;

    public double minC;

}
