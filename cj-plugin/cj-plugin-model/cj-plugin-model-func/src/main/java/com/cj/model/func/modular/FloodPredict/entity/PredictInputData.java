package com.cj.model.func.modular.FloodPredict.entity;

import lombok.Data;

import java.util.Date;
@Data
public class PredictInputData implements Cloneable {
    private Date dates;//日期

    private Double flow;//径流量

    private String dataType;//flow,level

    private Double temperature;//温度

    private Double rainfall;//降雨

    private String location;//站点位置
    // Getters and setters

    @Override
    public PredictInputData clone() {
        try {
            return (PredictInputData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning not supported", e);
        }
    }
}


