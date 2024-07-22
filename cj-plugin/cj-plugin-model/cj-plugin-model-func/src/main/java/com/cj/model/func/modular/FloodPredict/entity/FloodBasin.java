package com.cj.model.func.modular.FloodPredict.entity;

import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.Calibration.pso.Interval;
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

    @JsonProperty("paramRange")
    @ApiModelProperty(value = "水文参数取值范围")
    private Map<String, List<Item>> paramRange;

    public class Item {
        private Double[] values;
        private String comment;

        public Double[] getValues() {
            return values;
        }

        public void setValues(Double[] values) {
            this.values = values;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
