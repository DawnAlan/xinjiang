package com.cj.model.func.modular.watertransfer.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

@Data
public class Excel2 {
    //时间
    @Excel(name = "time")
    private Date time;
    //用水类型
    @Excel(name = "typeName")
    private String typeName;
    //站点类型
    @Excel(name = "stationType")
    private String stationType;
    //站点名称
    @Excel(name = "stationName")
    private String stationName;
    //供水
    @Excel(name = "water")
    private double water;
    //缺额
    @Excel(name = "waterLack")
    private double waterLack;
    //供水比例
    @Excel(name = "proportion")
    private double proportion;

}
