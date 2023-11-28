package com.cj.waterresources.func.modular.trendsTable.bean.res;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WaterDailyParamSelectRes implements Serializable {

    private String id;

    private String pId;

    private String paramName;

    private String paramCode;

    private String isParent;

    private Integer orderNum;

    private List<WaterDailyParamSelectRes> children;
}
