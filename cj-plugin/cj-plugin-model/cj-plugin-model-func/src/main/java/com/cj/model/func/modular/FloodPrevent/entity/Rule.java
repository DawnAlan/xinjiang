package com.cj.model.func.modular.FloodPrevent.entity;

import lombok.Data;

import java.util.List;

@Data
public class Rule {

    private double minQ;
    private double maxQ;
    private double minH;
    private double maxH;

    private List<String> gates;

    private double qOut;

}
