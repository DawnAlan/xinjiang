package com.cj.waterresources.func.modular.waterResourceAllcation.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DayWaterUsePlanV {
    @JsonProperty("id")
    private String id;
    @JsonProperty("useWaterPlan")
    private String useWaterPlan;
    @JsonProperty("area")
    private String area;
    @JsonProperty("unitName")
    private String unitName;
    @JsonProperty("children")
    private List<DayWaterUsePlanV> children;
    @JsonProperty("pid")
    private String pid;
    @JsonProperty("flow")
    private String flow;
    @JsonProperty("waterPlan")
    private String waterPlan;
}
