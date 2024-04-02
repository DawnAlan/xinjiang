package com.cj.model.func.modular.FloodPredict.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class ForcastInputParam {

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    //需要输入的数据
    private Date preStartTime;
    private String location;
    private String period;
    public int periodStepSize;//时段步长
    public int periodStepNumber;//时段数量
    private Boolean isRealtime;
    private Boolean isShortForecast;

    //模型内使用不需要额外传入
    private Boolean isHistory;
    private String model;
    public int vmdK;//分解层数
    private List<TemporaryXlsx> xlsx;//表格路径
    public Boolean isSnowMeltModel;//是否为融雪模型

    private Double preFlow;

    private Double preRainFall;

    private Boolean isSimulation;
}
