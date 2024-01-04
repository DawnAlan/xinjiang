package com.cj.dataSynchronization.func.modular.tth.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryMonitorBasicDto implements Serializable {

    private String DEPT_ID;
    private String NAME;
    private String USER_ID;
    private String DATA_RECEIVE_STATUS;
    private String SELF_CODE;
    private String REMARK;
    private String DEPT_NAME;
    private String SHORT_NAME;
    private String OPERATE_TIME;
    private String MONITOR_TYPE;
    private String LATITUDE;
    private String REGION_ID;
    private String MONITOR_WAY;
    private String REGION_NAME;
    private String ELEVATION;
    private String MONITOR_CODE;
    private String USER_NAME;
    private String MONITOR_NAME;
    private String ID;
    private String LONGITUDE;
    private String PARENT_ID;
    private String POSITION;
}
