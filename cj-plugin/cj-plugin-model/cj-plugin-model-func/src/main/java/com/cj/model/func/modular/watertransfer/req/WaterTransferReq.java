package com.cj.model.func.modular.watertransfer.req;

import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.watertransfer.entity.DataInflowPrevent;
import com.cj.model.func.modular.watertransfer.entity.Waterdemand;
import com.cj.model.func.modular.watertransfer.method.Reservoir;
import lombok.Data;


import java.util.*;


@Data
public class WaterTransferReq {
    //自用
    private Reservoir[] reservoirs;
    //自用步长数
    private Integer calStep;
    //自用方案id
    private int id;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //配水类型
    private int name;
    //楼庄子汛限水位
    private double  floodWaterLevelLzz;
    //头屯河汛限水位
    private double  floodWaterLevelTth;
    //楼庄子起调水位
    private double levelBeginLzz;
    //头屯河起调水位
    private double levelBeginTth;
    //楼庄子期末水位
    private double levelEndLzz;
    //头屯河期末水位
    private double levelEndTth;
    //步长
    private int timeCalStep;
    //曲线
    private List<CurveParam> curve;
    //来水预报
    private Map<String, List<DataInflowPrevent>> data;
    //需水计划
    private Map<String, List<Waterdemand>> waterDemandData;

    private String typeName;
}
