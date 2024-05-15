package com.cj.model.func.modular.FloodPrevent.entity;

import lombok.Data;

import java.util.List;

@Data
public class Gate {
    private String name;
    private List<CurveParam> curve;
}
