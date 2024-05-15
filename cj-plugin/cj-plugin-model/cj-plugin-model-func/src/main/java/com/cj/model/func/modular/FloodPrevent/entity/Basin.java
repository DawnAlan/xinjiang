package com.cj.model.func.modular.FloodPrevent.entity;

import lombok.Data;

import java.util.*;

@Data
public class Basin {
    //流域名称
    private String name;
    //流域水库序列
    private List<Reservoir> reservoirs;
}
