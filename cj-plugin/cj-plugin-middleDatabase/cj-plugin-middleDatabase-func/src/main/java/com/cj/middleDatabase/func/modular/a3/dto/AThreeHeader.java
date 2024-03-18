package com.cj.middleDatabase.func.modular.a3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class AThreeHeader {
    @JsonProperty("id")
    private String id;
    @JsonProperty("paramName")
    private String paramName;
    @JsonProperty("paramCode")
    private Object paramCode;
    @JsonProperty("isParent")
    private String isParent;
    @JsonProperty("orderNum")
    private Integer orderNum;
    @JsonProperty("useWaterType")
    private String useWaterType;
    @JsonProperty("children")
    private List<AThreeHeader> children;
    @JsonProperty("pid")
    private String pid;
}
