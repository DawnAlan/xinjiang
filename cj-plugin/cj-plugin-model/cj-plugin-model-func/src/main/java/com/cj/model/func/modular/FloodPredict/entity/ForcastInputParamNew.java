package com.cj.model.func.modular.FloodPredict.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ForcastInputParamNew {

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

    private Double preFlow;

    private Double preRainFall;

    private Boolean isSimulation;

}
