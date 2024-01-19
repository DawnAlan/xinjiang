package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class JobRes implements Serializable {

    private Map<String, List<String>> map;

    private List<String> collect;
}
