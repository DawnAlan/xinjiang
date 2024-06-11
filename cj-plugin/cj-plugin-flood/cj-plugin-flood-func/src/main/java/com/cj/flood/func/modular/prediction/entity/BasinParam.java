package com.cj.flood.func.modular.prediction.entity;

import com.cj.common.serializer.DoubleScale2Serializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class BasinParam {

    @JsonProperty("name")
    @ApiModelProperty(value = "流域名称")
    private String name;
    @JsonProperty("reservoirs")
    @ApiModelProperty(value = "水库集合")
    private List<ReservoirsDTO> reservoirs;

    @NoArgsConstructor
    @Data
    public static class ReservoirsDTO {
        @JsonProperty("name")
        @ApiModelProperty(value = "水库名称")
        private String name;
        @JsonProperty("DeadLevel")
        @ApiModelProperty(value = "死水位")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double deadLevel;
        @JsonProperty("LimitLevel")
        @ApiModelProperty(value = "汛限水位")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double limitLevel;
        @JsonProperty("NormalLevel")
        @ApiModelProperty(value = "正常蓄水位")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double normalLevel;
        @JsonProperty("HeightLevel")
        @ApiModelProperty(value = "防洪高水位")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double heightLevel;
        @JsonProperty("DesignLevel")
        @ApiModelProperty(value = "设计洪水位")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double designLevel;
        @JsonProperty("ProofLevel")
        @ApiModelProperty(value = "校核洪水位")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double proofLevel;
        @JsonProperty("DownStream")
        @ApiModelProperty(value = "下断面安全泄量")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double downStream;
        @JsonProperty("DownInput")
        @ApiModelProperty(value = "下断面超标准洪水流量")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double downInput;
        @JsonProperty("Rise")
        @ApiModelProperty(value = "单日水位增幅")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double rise;
        @JsonProperty("Decline")
        @ApiModelProperty(value = "单日水位降幅")
        @JsonSerialize(using = DoubleScale2Serializer.class)
        private Double decline;



        @JsonProperty("coefficient")
        @ApiModelProperty(value = "水位库容曲线数量级,万m³对应10000")
        private Integer coefficient;
        @JsonProperty("capacityCurve")
        @ApiModelProperty(value = "水位库容曲线集合")
        private List<CapacityCurveDTO> capacityCurve;
        @JsonProperty("gates")
        @ApiModelProperty(value = "闸门集合")
        private List<GatesDTO> gates;
        @JsonProperty("conventionalRules")
        @ApiModelProperty(value = "常规调度规程集合,说明流量在[minQ, maxQ)之间,水位在[minH, maxH)之间可用的闸门gates,maxH-999999设置很大的值,代表无上限,qOut是最大出库流量,条数不固定")
        private List<GateRulesDTO> conventionalRules;
        @JsonProperty("flexibleRules")
        @ApiModelProperty(value = "灵活调度规程集合,结构同conventionalRules")
        private List<GateRulesDTO> flexibleRules;
        @JsonProperty("LimitLevels")
        @ApiModelProperty(value = "动态讯限水位")
        private List<Double> limitLevels;
        @JsonProperty("eco")
        @ApiModelProperty(value = "生态流量")
        private List<Double> eco;

        @NoArgsConstructor
        @Data
        public static class CapacityCurveDTO {
            @JsonProperty("level")
            @ApiModelProperty(value = "水位")
            @JsonSerialize(using = DoubleScale2Serializer.class)
            private Double level;
            @JsonProperty("value")
            @ApiModelProperty(value = "水位对应的库容")
            @JsonSerialize(using = DoubleScale2Serializer.class)
            private Double value;
        }

        @NoArgsConstructor
        @Data
        public static class GatesDTO {
            @JsonProperty("name")
            @ApiModelProperty(value = "闸门名称")
            private String name;
            @JsonProperty("minLevel")
            @ApiModelProperty(value = "溢洪设施底坎高程")
            @JsonSerialize(using = DoubleScale2Serializer.class)
            private Double minLevel;
            @JsonProperty("curve")
            @ApiModelProperty(value = "闸门的泄流能力曲线")
            private List<CurveDTO> curve;

            @NoArgsConstructor
            @Data
            public static class CurveDTO {
                @JsonProperty("level")
                @ApiModelProperty(value = "水位")
                @JsonSerialize(using = DoubleScale2Serializer.class)
                private Double level;
                @JsonProperty("value")
                @ApiModelProperty(value = "水位对应的泄流能力")
                @JsonSerialize(using = DoubleScale2Serializer.class)
                private Double value;
            }
        }

        @NoArgsConstructor
        @Data
        public static class GateRulesDTO {
            @JsonProperty("minQ")
            @ApiModelProperty(value = "最小流量")
            @JsonSerialize(using = DoubleScale2Serializer.class)
            private Double minQ;
            @JsonProperty("maxQ")
            @ApiModelProperty(value = "最大流量")
            @JsonSerialize(using = DoubleScale2Serializer.class)
            private Double maxQ;
            @JsonProperty("minH")
            @ApiModelProperty(value = "最低水位")
            @JsonSerialize(using = DoubleScale2Serializer.class)
            private Double minH;
            @JsonProperty("maxH")
            @ApiModelProperty(value = "最高水位")
            @JsonSerialize(using = DoubleScale2Serializer.class)
            private Double maxH;
            @JsonProperty("gates")
            @ApiModelProperty(value = "闸门")
            private List<String> gates;
            @JsonProperty("qOut")
            @ApiModelProperty(value = "最大出库流量")
            @JsonSerialize(using = DoubleScale2Serializer.class)
            private Double qOut;
        }
    }
}
