package com.cj.waterresources.func.modular.homePage.inspection.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class AbnormalRes implements Serializable {

    private String auditBy;
    private String auditByName;
    private String bpmStatus;
    private String createBy;
    private String createTime;
    private String exceptionNote;
    private String exceptionPosition;
    private String exceptionType;
    private String exceptionTypeText;
    private String id;
    private String matter;
    private String part;
    private String percussionCondition;
    private String point;
    private String pointName;
    private String processBy;
    private String processInstId;
    private String processTaskId;
    private String processedByName;
    private String processedIllustrate;
    private String processedTime;
    private String processorBy;
    private String processorByName;
    private Integer result;
    private String sysOrgCode;
    private String taskId;
    private String taskIdText;
    private String taskSchemeType;
    private String templateId;
    private String templateIdDictText;
    private String typeValue;
    private String updateBy;
    private String updateTime;

}
