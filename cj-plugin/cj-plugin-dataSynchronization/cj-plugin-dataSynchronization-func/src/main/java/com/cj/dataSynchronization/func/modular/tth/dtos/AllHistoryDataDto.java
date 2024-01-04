package com.cj.dataSynchronization.func.modular.tth.dtos;

import lombok.Data;

@Data
public class AllHistoryDataDto {

    /**
     * REGION_ID : 2-310-017
     * VOLTAGE : 12.34
     * P_TOTAL_FLOW : null
     * USER_ID : admin
     * DOWN_WATER : null
     * IG_CO : null
     * MONITOR_FLOW : 0
     * MONITOR_ID : 8a8181d2798e094b0179c6f933ab0020
     * OPERATE_TIME : 2023-12-05 20:02:24
     * MONITOR_TIME : 2023-12-05 20:00:00
     * GATE_OPEN_HOLES : null
     * ID : 4028dc818b45a5ca018c39dba0c63d63
     * REMARK : null
     * WATER : 22.184
     * CAPACITY : 548.354
     * N_TOTAL_FLOW : null
     * IS_SURPASS : 0
     * USER_NAME : 超级管理员
     * INPUT_FLOW : 0.223
     * GATE_HEIGHT : null
     * WATER_LEVEL : 980.314
     * TOTAL_FLOW : null
     * MONITOR_FLOW_RATE : null
     * IG_SP : null
     * DOWN_LEVEL : null
     * GATE_HEIGHT_SHOW :
     */

    private String REGION_ID;
    private Double VOLTAGE;
    private Double P_TOTAL_FLOW;//
    private String USER_ID;//
    private Object DOWN_WATER;//
    private Object IG_CO;//
    private Double MONITOR_FLOW;
    private String MONITOR_ID;
    private String OPERATE_TIME;
    private String MONITOR_TIME;
    private Object GATE_OPEN_HOLES;
    private String ID;
    private String REMARK;
    private Double WATER;
    private Double CAPACITY;
    private Double N_TOTAL_FLOW;
    private String IS_SURPASS;
    private String USER_NAME;
    private Double INPUT_FLOW;
    private Double GATE_HEIGHT;
    private Double WATER_LEVEL;
    private Double TOTAL_FLOW;
    private Double MONITOR_FLOW_RATE;
    private Object IG_SP;
    private Object DOWN_LEVEL;
    private String GATE_HEIGHT_SHOW;
    private Object IS_NULL_PIPE;
    private Object PIPE_PRESSURE;
}
