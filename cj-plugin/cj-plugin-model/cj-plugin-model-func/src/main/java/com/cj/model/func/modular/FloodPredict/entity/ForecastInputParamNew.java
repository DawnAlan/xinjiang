package com.cj.model.func.modular.FloodPredict.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ForecastInputParamNew {

    //模型类型(1-中长期 2-短期 3-场次)
    private Integer modelType;
    //预报时间
    private Date predictionTime;
    //预报时间
    private Date dataStartTime;
    //时段类型(1-月 2-旬 3-日 4-小时)
    private Integer periodTimeType;
    //时段步长
    private Integer periodTimeStep;
    //时间数量
    private Integer periodTimeNum;
    //楼庄子历史数据
    private LzzHydrologyParam lzzHydrologyParam;
    //灌区实时雨量站信息
    private IrrigatedHydrologyParam irrigatedHydrologyParam;
    //预报雨量
    private List<RainFallDto> rainFallDtos;
    //前期径流
    private Double preFlow;
    //前期累计降雨
    private Double preRainFall;
    //是否为模拟降雨
    private Boolean isSimulation;
}
