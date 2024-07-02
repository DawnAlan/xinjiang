package com.cj.common.feign.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ExternStations {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("typeName")
    private String typeName;
    @JsonProperty("typeOf")
    private String typeOf;
    @JsonProperty("unit")
    private String unit;
    @JsonProperty("facilityClass")
    private String facilityClass;
    @JsonProperty("dataProvider")
    private String dataProvider;
    @JsonProperty("dataResource")
    private String dataResource;
    @JsonProperty("pid")
    private String pid;
}
