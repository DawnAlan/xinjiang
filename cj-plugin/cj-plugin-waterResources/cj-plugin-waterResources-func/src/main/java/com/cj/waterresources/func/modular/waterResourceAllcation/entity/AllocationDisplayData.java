package com.cj.waterresources.func.modular.waterResourceAllcation.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

@Data
public class AllocationDisplayData {
    @Excel(name = "StationName")
    private String stationName;
    @Excel(name = "Type")
    private String type;
    @Excel(name = "Time")
    private Date time;
    @Excel(name = "LevelBegin")
    private double levelBegin;
    @Excel(name = "LevelEnd")
    private double levelEnd;
    @Excel(name = "Capacity")
    private double capacity;
    @Excel(name = "Capacity_proportion")
    private double capacityProportion;
    @Excel(name = "Inflow")
    private double inflow;
    @Excel(name = "Outflow")
    private double outflow;
    @Excel(name = "WaterDemand")
    private double waterDemand;
    @Excel(name = "WaterSupply")
    private double waterSupply;
    @Excel(name = "Inflow_Water")
    private double inflowWater;
    @Excel(name = "WaterAvailability")
    private double waterAvailability;
    @Excel(name = "waterBalance")
    private double waterBalance;
    @Excel(name = "EcologyProportion")
    private double ecologyProportion;
    @Excel(name = "City_Proportion")
    private double cityProportion;
    @Excel(name = "IndustryProportion")
    private double industryProportion;
    @Excel(name = "IrrigateProportion")
    private double irrigateProportion;
    @Excel(name = "GreeningProportion")
    private double greeningProportion;
    @Excel(name = "deltawater")
    private double deltaWater;
}
