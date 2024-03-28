package com.cj.model.func.modular.watertransfer.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Waterdemand {

    private Date date;
    //用水计划类型
    private String useWaterPlan;
    //用水数据
    private double waterDemendData;
    //地区
    private String area;
    //单位
    private String unit;
    //日计划填写
    private String subArea;
    //表头
    private String colName;
    //子节点
    private int children;
}