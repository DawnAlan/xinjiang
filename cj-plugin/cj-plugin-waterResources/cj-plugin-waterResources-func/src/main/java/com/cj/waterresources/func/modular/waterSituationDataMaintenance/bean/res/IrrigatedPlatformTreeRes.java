package com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res;

import lombok.Data;

import java.util.List;

@Data
public class IrrigatedPlatformTreeRes {
    private String id;

    private String name;

    private String parentId;

    private String beginTime;

    private String beginTimeMark;

    private String elevation;

    private String isWaterLevel;

    private String locationType;

    private String locationTypeName;

    private String measureType;

    private String monitorType;

    private String nodetype;

    private String selfCode;

    private String waterlevelNotnormal;

    private List<IrrigatedPlatformTreeRes> children;
}
