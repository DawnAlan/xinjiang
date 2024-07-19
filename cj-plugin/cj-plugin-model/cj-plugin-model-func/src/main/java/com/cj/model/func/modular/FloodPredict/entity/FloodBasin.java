package com.cj.model.func.modular.FloodPredict.entity;

import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class FloodBasin {
    @JsonProperty("name")
    @ApiModelProperty(value = "流域名称")
    private String name;
    @JsonProperty("hydrologies")
    @ApiModelProperty(value = "流域水文信息")
    private List<Hydrology> hydrologies;
    @JsonProperty("paramMap")
    @ApiModelProperty(value = "流域水文参数")
    private Map<String, ShanbeiParam> paramMap;
}
