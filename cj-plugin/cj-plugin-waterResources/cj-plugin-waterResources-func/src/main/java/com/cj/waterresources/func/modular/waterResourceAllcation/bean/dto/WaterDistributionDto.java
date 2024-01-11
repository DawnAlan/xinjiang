package com.cj.waterresources.func.modular.waterResourceAllcation.bean.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WaterDistributionDto implements Serializable {

    @Excel(name = "time")
    private Date time;

    @Excel(name = "typeName")
    private String typeName;

    @Excel(name = "stationType")
    private String stationType;

    @Excel(name = "stationName")
    private String stationName;

    @Excel(name = "water")
    private Double water;

    @Excel(name = "proportion")
    private Double proportion;

    @Excel(name = "waterLack")
    private Double waterLack;
}
