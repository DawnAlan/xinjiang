package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApprovalTrafficRes implements Serializable {
    private String id;
    private String useWaterPlan;
    private String area;
    private String unitName;
    private String pid;
    private String flow;
    private String waterPlan;
    private String unitId;
    private List<ApprovalTrafficRes> children;

}
