package com.cj.project.modular.pointstat.service;

import com.cj.project.modular.pointstat.result.PointStatResult;

import java.util.List;

public interface PointStatService {


    /**
     * 按仪器类型、部位统计
     *
     */
    PointStatResult GetInstrumentStat(String projectCode, String instrumentStr);


    /**
     * 按测点树统计
     *
     */
    PointStatResult GetTreeStat(String projectCode, String nodeID);


}
