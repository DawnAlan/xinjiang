package com.cj.waterresources.func.modular.waterResourceAllcation.bean.res;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        private Double water;
        @JsonProperty("data")
        private Map<String, List<UnitsDTO>> data;

        @NoArgsConstructor
        @Data
        public static class UnitsDTO {
            @JsonProperty("unit")
            private String unit;
            @JsonProperty("water")
            private Double water;
        }

    }
}
