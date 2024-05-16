package com.cj.model.func.modular.FloodPrevent.bean.req;


import com.cj.model.func.modular.FloodPrevent.entity.DataFloodPrevent;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReqFloodPrevent {
    //预报方案名称
    private String programmeName;
    //各水库上游区间流量
    private Map<String, List<DataFloodPrevent>> intervals;
    //各水库起调水位
    private Map<String,Double> beginLevels;
    //各库权重
    private Map<String,Double> weights;
    //各水库动态汛限水位
    private Map<String,double[]> limitLevels;
    //各水库动态生态流量
    private Map<String,double[]> eco;

}
