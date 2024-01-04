package com.cj.model.func.modular.FloodPredict.entity;

import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import lombok.Data;

import java.util.List;

@Data
public class LzzHydrologyParam {

    //3号桥
    private List<LzzGaugingStation> threeGaugingStation;

    //楼庄子入库水位站
    private List<LzzGaugingStation> lzzInput;

    //楼庄子出库水位站
    private List<LzzGaugingStation> lzzOutput;

    //楼庄子库水位站
    private List<LzzGaugingStation> lzzWaterLevel;

    //喀什沟雨量站
    private List<LzzRainfallStation> ksgRainfallStation;

    //黑沟雨量站
    private List<LzzRainfallStation> hgRainfallStation;

    //煤矿沟雨量站
    private List<LzzRainfallStation> mkgRainfallStation;

    //无名沟雨量站
    private List<LzzRainfallStation> wmgRainfallStation;

    //加普沙雨量站
    private List<LzzRainfallStation> jpsRainfallStation;

    //宰尔德雨量站
    private List<LzzRainfallStation> zrdRainfallStation;

    //东南沟雨量站
    private List<LzzRainfallStation> dngRainfallStation;

    //八一林场雨量站
    private List<LzzRainfallStation> bylcRainfallStation;

    //萨尔达万雨量站
    private List<LzzRainfallStation> sedwRainfallStation;

    //制材厂雨量站
    private List<LzzRainfallStation> zccRainfallStation;
}
