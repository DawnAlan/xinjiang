package com.cj.model.func.modular.FloodPredict.entity;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class calibrationParam {
    //模型类型(true-自动率定，false-人工率定)
    private Boolean modelType;
    //率定开始时间
    private Date startTime;
    //率定结束时间
    private Date endTime;
    //当前3个站点陕北模型参数
    private Map<String,shanbeiParam> param;
    //人工率定3个站点参数
    private Map<String,shanbeiParam> manualParam;
    //当前预报径流,<断面,率定期间的时间和径流>

    private Map<String,Object[][]> imitateFlow;
    //楼庄子历史数据
    private LzzHydrologyParam lzzHydrologyParam;
    //灌区实时雨量站信息
    private IrrigatedHydrologyParam irrigatedHydrologyParam;
}
