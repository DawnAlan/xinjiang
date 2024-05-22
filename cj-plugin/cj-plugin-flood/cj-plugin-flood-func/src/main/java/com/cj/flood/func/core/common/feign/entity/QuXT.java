package com.cj.flood.func.core.common.feign.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class QuXT {

    @JsonProperty("drop")
    private DropDTO drop;
    @JsonProperty("tab")
    private List<TabDTO> tab;
    @JsonProperty("level")
    private List<Double> level;
    @JsonProperty("flow")
    private List<Double> flow;

    @NoArgsConstructor
    @Data
    public static class DropDTO {
        @JsonProperty("name")
        private String name;
        @JsonProperty("id")
        private String id;
    }

    @NoArgsConstructor
    @Data
    public static class TabDTO {
        @JsonProperty("id")
        private String id;
        @JsonProperty("v0")
        private Double v0;
        @JsonProperty("v1")
        private Double v1;
    }
}
