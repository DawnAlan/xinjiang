package com.cj.waterresources.func.modular.waterResourceAllcation.bean.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class IncomingWaterForecastDto implements Serializable {
    private String id;

    private String programmeName;

    private String modelResultAddress;
}
