package com.cj.model.func.modular.FloodPredict.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Hydrology {
    //断面名称
    private String stationName;
    //断面位置(0为上游，1为区间，2为下游)
    private Integer position;
    //包含的断面
    private List<String> includingStation;
    //包含的水位站
    private Map<Integer,String> includingWater;
    //包含的雨量站
    private List<String> rainStation;
    //融雪月份
    private Integer[] snowMonth;
}
