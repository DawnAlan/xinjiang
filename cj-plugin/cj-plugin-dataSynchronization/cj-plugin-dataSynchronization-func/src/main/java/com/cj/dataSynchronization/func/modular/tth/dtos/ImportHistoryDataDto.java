package com.cj.dataSynchronization.func.modular.tth.dtos;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

@Data
public class ImportHistoryDataDto implements Serializable {

    @Excel(name = "测点id")
    private String monitorId;

    //记录时间
    @Excel(name = "监测时间")
    private String monitorTime;

    //水深
    @Excel(name = "水深")
    private Double sqWaterLevel;

    //瞬时流速
    @Excel(name = "瞬时流速")
    private Double sqMonitorFlowRate;

    //瞬时流量
    @Excel(name = "瞬时流量")
    private Double sqMonitorFlow;

    //累计流量
    @Excel(name = "累计流量")
    private Double sqTotalFlow;

    //流量
    @Excel(name = "流量")
    private Double sqMonitorFlow1;

    //水位
    @Excel(name = "水位")
    private Double sqWaterLevel1;

    //库容
    @Excel(name = "库容")
    private Double sqCapacity;

    //时段雨量
    @Excel(name = "时段雨量")
    private Double yqRainFallOne;
}
