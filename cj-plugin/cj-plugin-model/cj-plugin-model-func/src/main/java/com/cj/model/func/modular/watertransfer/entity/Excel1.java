package com.cj.model.func.modular.watertransfer.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Excel1 {
    //时间

    private Date time;
    //用水类型
    private double levelBegin;
    //站点类型
    private double levelEnd;
    //站点名称
    private String stationName;
    //需水数据
    private double waterDemand;
    //来水数据
    private double inflowWater;
    //蓄水数据
    private double deltawater;
    //弃水数据
    private double wasteWater;

}
