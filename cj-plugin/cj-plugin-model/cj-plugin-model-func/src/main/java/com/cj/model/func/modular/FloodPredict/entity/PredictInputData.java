package com.cj.model.func.modular.FloodPredict.entity;

import lombok.Data;

import java.util.Date;

@Data
public class PredictInputData {
    private Date dates;//日期

    private Double flow;//径流量

    private Double temperature;//温度

    private Double rainfall;//降雨

    private String location;//站点位置
}
