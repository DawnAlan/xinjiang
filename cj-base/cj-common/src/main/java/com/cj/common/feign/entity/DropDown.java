package com.cj.common.feign.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DropDown {
    @JsonProperty("dataId")
    private String dataId;
    @JsonProperty("ndcdId")
    private String ndcdId;
    @JsonProperty("enable")
    private String enable;
    @JsonProperty("name")
    private String name;
    @JsonProperty("id")
    private String id;
}
