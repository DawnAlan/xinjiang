package com.cj.waterresources.func.modular.waterResourceAllcation.bean.res;

import com.cj.common.serializer.DoubleListScale2Serializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Data
public class WaterAllocationComparisonSelectionRes {

    private List<WaterRatioDTO> waterRatio;
    private WaterStatisticsDTO waterStatistics;
    private List<WaterAmountDTO> waterAmount;
    private String appraise;

    @NoArgsConstructor
    @Data
    public static class WaterRatioDTO {
        private String area;
        private String unit;
        @JsonSerialize(using = DoubleListScale2Serializer.class)
        private List<Double> waterLack;
        @JsonSerialize(using = DoubleListScale2Serializer.class)
        private List<Double> proportion;
    }

    @NoArgsConstructor
    @Data
    public static class WaterStatisticsDTO {
        @JsonSerialize(using = DoubleListScale2Serializer.class)
        private List<Double> ecologyProportion;
        @JsonSerialize(using = DoubleListScale2Serializer.class)
        private List<Double> cityProportion;
        @JsonSerialize(using = DoubleListScale2Serializer.class)
        private List<Double> industryProportion;
        @JsonSerialize(using = DoubleListScale2Serializer.class)
        private List<Double> irrigateProportion;
        @JsonSerialize(using = DoubleListScale2Serializer.class)
        private List<Double> greeningProportion;
    }

    @NoArgsConstructor
    @Data
    public static class WaterAmountDTO {
        private Date date;
        @JsonSerialize(using = DoubleListScale2Serializer.class)
        private List<Double> incomingWaterAmount;
        @JsonSerialize(using = DoubleListScale2Serializer.class)
        private List<Double> proportionWaterAmount;
        @JsonSerialize(using = DoubleListScale2Serializer.class)
        private List<Double> waterLackAmount;
        @JsonSerialize(using = DoubleListScale2Serializer.class)
        private List<Double> wasteWaterAmount;
    }
}
