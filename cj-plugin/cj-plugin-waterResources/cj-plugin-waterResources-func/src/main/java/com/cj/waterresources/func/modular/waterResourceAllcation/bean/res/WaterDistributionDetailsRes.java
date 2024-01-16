package com.cj.waterresources.func.modular.waterResourceAllcation.bean.res;

import lombok.Data;

import java.io.Serializable;

@Data
public class WaterDistributionDetailsRes implements Serializable {

    private String name;
    private String time;
    private Double value;

}
