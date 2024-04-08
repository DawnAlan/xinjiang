package com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.bean.res;

import lombok.Data;

import java.io.Serializable;

@Data
public class SelectTotalForIndexWarningRes implements Serializable {

    private String name;

    private Double proportionalWaterQuantity;

    private Double actualWaterVolume;
}
