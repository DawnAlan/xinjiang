package com.cj.model.func.modular.watertransfer.method;

import java.time.DateTimeException;
import java.util.Date;

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



    public void setTime(Date[] time){
        this.time = time;
    }
    public Date[] getTime() {
        return time;
    }
    public void setLevelbegin(double [][]levelbegin){
        this.levelbegin = levelbegin;
    }

    public double[][] getLevelbegin() {
        return levelbegin;
    }
    public void setLevelend(double[][] levelend){
        this.levelend = levelend;
    }

    public double[][] getLevelend() {
        return levelend;
    }

    public void setEndCapacity(double[][] endCapacity){
        this.endCapacity =endCapacity;
    }

    public double[][] getEndCapacity() {
        return endCapacity;
    }
    public void setPreSupplyWater(double[][] preSupplyWater){
        this.preSupplyWater =preSupplyWater;
    }

    public double[][] getPreSupplyWater() {
        return preSupplyWater;
    }
    public void setInflow(double [][]inflow){
        this.inflow = inflow;
    }

    public double[][] getInflow() {
        return inflow;
    }

    public void setInflow_water(double [][]inflow_water){
        this.inflow_water = inflow_water;
    }

    public double[][] getInflow_water() {
        return inflow_water;
    }
    public void setInflow_water_toutunhe(double []inflow_water_toutunhe){
        this.inflow_water_toutunhe = inflow_water_toutunhe;
    }

    public double[] getInflow_water_toutunhe() {
        return inflow_water_toutunhe;
    }

    public void setInflow_toutunhe(double []inflow_toutunhe){
        this.inflow_toutunhe = inflow_toutunhe;
    }

    public double[] getInflow_toutunhe() {
        return inflow_toutunhe;
    }

    public void setOutflow(double[][] outflow){
        this.outflow = outflow;
    }

    public double[][] getOutflow() {
        return outflow;
    }
    public void setOutflow_water(double [][]outflow_water){
        this.outflow_water = outflow_water;
    }

    public double[][] getOutflow_water() {
        return outflow_water;
    }
    public void setDeltawater(double[] []deltawater){
        this.deltawater = deltawater;
    }

    public double[] []getDeltawater() {
        return deltawater;
    }

    public void setWaterdemand(double[][] waterdemand){
        this.waterdemand = waterdemand;
    }

    public double[][] getWaterdemand() {
        return waterdemand;
    }

    public void setWaterSupply(double[][] waterSupply){
        this.waterSupply = waterSupply;
    }

    public double[][] getWaterSupply() {
        return waterSupply;
    }
    public void setWaterdemand_all(double []waterdemand_all){
        this.waterdemand_all = waterdemand_all;
    }

    public double[] getWaterdemand_all() {
        return waterdemand_all;
    }
    public void setWaterSupply_all(double []waterSupply_all){
        this.waterSupply_all = waterSupply_all;
    }

    public double[] getWaterSupply_all() {
        return waterSupply_all;
    }
    public void setProportion(double[][] proportion){
        this.proportion = proportion;
    }

    public double[][] getProportion() {
        return proportion;
    }
    public void setWaterSupply3(double[][] waterSupply3){
        this.waterSupply3 = waterSupply3;
    }

    public double[][] getWaterSupply3() {
        return waterSupply3;
    }
    public void setWaterSupply4(double[][] waterSupply4){
        this.waterSupply4 = waterSupply4;
    }

    public double[][] getWaterSupply4() {
        return waterSupply4;
    }

    public void setSupply_water_two(double[][] supply_water_two){
        this.supply_water_two = supply_water_two;
    }

    public double[][] getSupply_water_two() {
        return supply_water_two;
    }


    public void setWater_shortage(double[][] water_shortage){
        this.water_shortage = water_shortage;
    }

    public double[][] getWater_shortage() {
        return water_shortage;
    }

    public void setReservoirWaterdemand(double[][]reservoirWaterdemand){
        this.reservoirWaterdemand = reservoirWaterdemand;
    }
    public double[][] getReservoirWaterdemand() {
        return reservoirWaterdemand;
    }

    public void setReservoirWatersupply(double[][]reservoirWatersupply){
        this.ReservoirWatersupply = reservoirWatersupply;
    }
    public double[][] getReservoirWatersupply() {
        return ReservoirWatersupply;
    }

    public void setInflowWater_supply(double[][]inflowWater_supply){
        this.inflowWater_supply = inflowWater_supply;
    }
    public double[][] getInflowWater_supply() {
        return inflowWater_supply;
    }
    public void setCapacity_proportion(double[][] capacity_proportion){
        this.capacity_proportion = capacity_proportion;
    }

    public double[][] getCapacity_proportion() {
        return capacity_proportion;
    }
    public void setEvaluation(int id){

        if (id==1){
            evaluation=" 以供水缺额最小为目标，考虑各用水单元的优先级，结合用水单元的用水需求，" +
                    "根据水库现有库容予以分配，尽可能满足用水单元用水需求，做到供水缺额最小";
        }
        else if (id==2){
            evaluation="保证各用水单元实际供水占需水的比例最大，结合各用水单元的优先级及用水需求，" +
                    "尽可能满足供水比例最大，结合实际需求选择该方案";
        }
    }

    public String getEvaluation() {
        return evaluation;
    }
}
