package com.cj.waterresources.func.modular.waterResourceAllcation.bean.res;

import com.cj.common.serializer.DoubleScale2Serializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class ViewModelRes implements Serializable {
    @JsonProperty("area")
    private String area;

    @JsonProperty("info")
    private AreaDTO info;
    @NoArgsConstructor
    @Data
    public static class AreaDTO {
        @JsonProperty("water")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double water;
        @JsonProperty("data")
        private Map<String, List<UnitsDTO>> data;

        @NoArgsConstructor
        @Data
        public static class UnitsDTO {
            @JsonProperty("unit")
            private String unit;
            @JsonProperty("water")
            @JsonSerialize(using = DoubleScale2Serializer.class)
            private Double water;
        }

    }
}
