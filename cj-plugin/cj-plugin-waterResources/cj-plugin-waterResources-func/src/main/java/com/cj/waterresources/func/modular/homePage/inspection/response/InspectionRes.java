package com.cj.waterresources.func.modular.homePage.inspection.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class InspectionRes implements Serializable {

    private String actualEndTime;
    private String actualStartTime;
    private String auditBy;
    private String createBy;
    private String createTime;
    private Integer distance;
    private String duration;
    private String id;
    private String patrolBy;
    private String patrolByText;
    private String patrolBy_dictText;
    private String patrolDateTime;
    private String patrolPathId;
    private String placeId;
    private String placeName;
    private String proCompleteCount;
    private Integer proCount;
    private String proType;
    private String processInstId;
    private String processTaskId;
    private String remark;
    private String sysOrgCode;
    private String taskFlag;
    private String taskFlag_dictText;
    private String taskId;
    private String taskId_dictText;
    private String taskSchemeType;
    private String taskSchemeTypeText;
    private String taskSchemeType_dictText;
    private String taskType;
    private String taskType_dictText;
    private String updateBy;
    private String updateTime;
    private String workingSchemeId;

}
