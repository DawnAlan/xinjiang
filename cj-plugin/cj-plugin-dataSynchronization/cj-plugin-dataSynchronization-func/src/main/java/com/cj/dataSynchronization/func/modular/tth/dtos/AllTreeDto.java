package com.cj.dataSynchronization.func.modular.tth.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * 河东水位站水位计
 */
@Data
public class AllTreeDto implements Serializable {

    /**
     * BEGIN_TIME : 1990-01-01 20:00:00
     * BEGIN_TIME_MARK : 02
     * ELEVATION : 0
     * ID : 8a8181d2798e094b0179c6f7d04f0016
     * IS_WATER_LEVEL : 01
     * LOCATION_TYPE : 01
     * LOCATION_TYPE_NAME : 渠道
     * MEASURE_TYPE : 08
     * MONITOR_TYPE : 01
     * NAME : 入库流量
     * NODETYPE : Monitor
     * PARENT_ID : 01
     * SELF_CODE : 0001
     * WATERLEVEL_NOTNORMAL : null
     */

    private String BEGIN_TIME;
    private String BEGIN_TIME_MARK;
    private String ELEVATION;
    private String ID;
    private String IS_WATER_LEVEL;
    private String LOCATION_TYPE;
    private String LOCATION_TYPE_NAME;
    private String MEASURE_TYPE;
    private String MONITOR_TYPE;
    private String NAME;
    private String NODETYPE;
    private String PARENT_ID;
    private String SELF_CODE;
    private String WATERLEVEL_NOTNORMAL;
}
