package com.cj.waterresources.func.modular.waterResourceAllcation.bean.res;

import lombok.Data;

import java.io.Serializable;

@Data
public class RealTimeReservoirLevelRes implements Serializable {

    private String date;

    private Double waterAmount ;

    private Double capacity;
}
