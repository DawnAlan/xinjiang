package com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HydrographRes {

    private String name;

    private String time;

    private Double flow;

    private Double waterLevel;
    //温度
    private BigDecimal temperature;
    //降雨量
    private BigDecimal rainfall;
}
