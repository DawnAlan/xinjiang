package com.cj.model.func.modular.watertransfer.method;

import lombok.Data;

import java.time.DateTimeException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Data
public class WaterTransfer {
    private Date[] time;
    private double [][]levelbegin;
    private double[][] levelend;
    //时段末库容
    private double [][]endCapacity;
    private double[][]inflow;
    //来水水量
    private double[][]inflow_water;
    //头屯河实际来水流量
    private double []inflow_toutunhe;
    //头屯河实际来水水量
    private double []inflow_water_toutunhe;
    //下泄流量
    private double [][]outflow;
    //下泄水量
    private double [][]outflow_water;
    //预测可供水量
    private double[][]preSupplyWater;
    //库容变化
    private double [][]deltawater;
    //用水需求
    private double [][]waterdemand;
    //用水总需求
    private double []waterdemand_all;
    //实际供水
    private double [][]waterSupply;
    //总供水
    private double []waterSupply_all;
    //两水库缺额
    private double [][]water_shortage;
    //3,4西干东干支渠供水
    private double [][]waterSupply3;
    private double [][]waterSupply4;

    private Map<String, Object> dataDemand;
    //适应度计算
    private double []fitness;

    private double []allWater;
    //供水比例
    private double [][]proportion;
    //评价
    private String evaluation;
    //两水库供水
    private double [][]supply_water_two;
    //两库需水
    private double [][]reservoirWaterdemand;
    //两库供水
    private double [][] ReservoirWatersupply;
    //库容比例
    private double [][]capacity_proportion;
    //来水减去供水
    private double [][]inflowWater_supply;

    //绿化配水
    private double [][]waterSupplyGreenEast;
    private double [][]waterSupplyGreenWest;
    private double [][]waterSupplyGreenQushou;
    //工业配水
    private double [][]waterSupplyIndustry;
    //绿化配水
    private double [][]waterDemandGreenEast;
    //生态流量
    private double[][]ecologyFlow;
    private double[][]ecologyWater;
    private double[][]ecologyWaterNeed;

    private String [] nameAgricultureQushou;
    private String [] nameAgricultureEast;

    private double [][]waterDemandGreenWest;
    private double [][]waterDemandGreenQushou;
    //工业配水
    private double [][]waterDemandIndustry;
    private double [][]waterDemand3;
    private double [][]waterDemand4;
    //绿化配水比例
    private double [][]proportionGreenEast;
    private double [][]proportionGreenWest;
    private double [][]proportionGreenQushou;
    //工业配水比例
    private double [][]proportionIndustry;
    private double[][] proportion3;
    private double[][] proportion4;
    //绿化地名
    private String []nameQushou;
    //东干绿化地名
    private String []nameGreenQushou;
    //西干绿化地名
    private String []nameGreenEast;
    private String []nameGreenWest;

    private String []nameEast;
    private String []nameWest;

}
