package com.cj.model.func.modular.watertransfer.res;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ContrastNewRes implements Serializable {

    private String time;

    private Map<Integer,Object> data;
}
