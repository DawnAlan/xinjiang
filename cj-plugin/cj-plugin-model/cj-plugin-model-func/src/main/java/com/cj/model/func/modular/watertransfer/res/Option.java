package com.cj.model.func.modular.watertransfer.res;

import lombok.Data;

import java.util.Date;

@Data
public class Option {

    private String id;
//    时间
    private Date Time;
    //    水库名称
    private String StationName;
//    类型
    private String TypeName;
//    开始水位
    private double LevelBegin;
//    末水位
    private double LevelEnd;
//    库容
    private double Capacity;
//    库容比例
    private double Capacity_Proportion;
//    来水
    private double inflow;
//    下泄
    private double outflow;
//    下泄水量
    private double outFlowWater;
//    来水水量
    private double inflow_water;
//     上月末可用水量
    private double WaterAvailability;
//    水量平衡
    private double WaterBalance;
//    水库蓄水
    private double deltawater;

    //
//     生态比例
    private double Ecology_Proportion;
//    城市用水比例
    private double City_Proportion;
//    工业用水比例
    private double Industry_Proportion;
//    农业用水比例
    private double Irrigate_Proportion;
//    绿化用水比例
    private double Greening_Proportion;

//    需水计划
    private double WaterDemand;
//    供水计划
    private double WaterSupply;

    private double allWater;
//    生态流量
    private double ecologyFlow;
//    生态水量
    private double ecologyWater;
}
