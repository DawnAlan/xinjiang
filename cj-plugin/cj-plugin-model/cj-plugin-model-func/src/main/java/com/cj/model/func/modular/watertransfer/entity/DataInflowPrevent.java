package com.cj.model.func.modular.watertransfer.entity;

import lombok.Data;


import java.util.Date;


@Data
public class DataInflowPrevent {
    private Date time;
    private double preQ;
    private int scale;
    private String location;
}
