package com.cj.model.func.modular.watertransfer.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Waterdemand {
    private Date time;
    private double preQ;
    private int scale;
    private String location;
}