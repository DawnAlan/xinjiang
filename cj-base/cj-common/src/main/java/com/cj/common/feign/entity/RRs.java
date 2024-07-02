package com.cj.common.feign.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RRs {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("typeName")
    private String typeName;
    @JsonProperty("typeOf")
    private Object typeOf;
    @JsonProperty("riverId")
    private Object riverId;
    @JsonProperty("typeZ")
    private Object typeZ;
    @JsonProperty("latitude")
    private Object latitude;
    @JsonProperty("longitude")
    private Object longitude;
    @JsonProperty("belongingWatershed")
    private Object belongingWatershed;
    @JsonProperty("position")
    private Object position;
    @JsonProperty("unit")
    private String unit;
    @JsonProperty("buildName")
    private Object buildName;
    @JsonProperty("elevation")
    private Object elevation;
    @JsonProperty("riseBfb")
    private Object riseBfb;
    @JsonProperty("superAlertLevel")
    private Object superAlertLevel;
    @JsonProperty("jumpAlarm")
    private Object jumpAlarm;
    @JsonProperty("facilityType")
    private Object facilityType;
    @JsonProperty("startupTime")
    private String startupTime;
    @JsonProperty("designFlow")
    private Object designFlow;
    @JsonProperty("alertLevelU")
    private Object alertLevelU;
    @JsonProperty("alertLevelB")
    private Object alertLevelB;
    @JsonProperty("superAlertLevelU")
    private Object superAlertLevelU;
    @JsonProperty("superAlertLevelB")
    private Object superAlertLevelB;
    @JsonProperty("alertFlowU")
    private Object alertFlowU;
    @JsonProperty("alertFlowB")
    private Object alertFlowB;
    @JsonProperty("superAlertFlowU")
    private Object superAlertFlowU;
    @JsonProperty("superAlertFlowB")
    private Object superAlertFlowB;
    @JsonProperty("alertIndicator1")
    private Object alertIndicator1;
    @JsonProperty("alertIndicator2")
    private Object alertIndicator2;
    @JsonProperty("alertIndicator3")
    private Object alertIndicator3;
    @JsonProperty("alertIndicator4")
    private Object alertIndicator4;
    @JsonProperty("facilityClass")
    private Object facilityClass;
    @JsonProperty("monitorId")
    private Object monitorId;
    @JsonProperty("dataProvider")
    private String dataProvider;
    @JsonProperty("description")
    private Object description;
    @JsonProperty("overallSituationUnitMgr")
    private OverallSituationUnitMgrDTO overallSituationUnitMgr;
    @JsonProperty("trendsTableParam")
    private Object trendsTableParam;
    @JsonProperty("childList")
    private Object childList;
    @JsonProperty("pid")
    private String pid;
    @JsonProperty("dataResource")
    private Object dataResource;
    @JsonProperty("a3Id")
    private Object a3Id;

    @NoArgsConstructor
    @Data
    public static class OverallSituationUnitMgrDTO {
        @JsonProperty("id")
        private String id;
        @JsonProperty("monitorId")
        private String monitorId;
        @JsonProperty("dataResource")
        private Object dataResource;
    }
}
