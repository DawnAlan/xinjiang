package com.cj.model.func.modular.watertransfer.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

@Data
public class ExcelDemo {
    //时间
    @Excel(name = "Time")
    private Date time;
    //站点名称
    @Excel(name = "StationName")
    private String stationName;
    //供水
    @Excel(name = "WaterSupply")
    private double waterSupply;
    //蓄水量
    @Excel(name = "deltawater")
    private double deltaWater;
    //水量平衡
    @Excel(name = "waterBalance")
    private double waterBalance;

}
