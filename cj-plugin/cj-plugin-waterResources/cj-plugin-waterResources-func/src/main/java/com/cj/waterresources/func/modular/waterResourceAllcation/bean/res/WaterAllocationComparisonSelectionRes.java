package com.cj.waterresources.func.modular.waterResourceAllcation.bean.res;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class WaterAllocationComparisonSelectionRes {

    private List<WaterRatioDTO> waterRatio;
    private WaterStatisticsDTO waterStatistics;
    private String appraise;

    @NoArgsConstructor
    @Data
    public static class WaterRatioDTO {
        private String area;
        private String unit;
        private List<Double> waterLack;
        private List<Double> proportion;
    }

    @NoArgsConstructor
    @Data
    public static class WaterStatisticsDTO {
        private List<Double> ecologyProportion;
        private List<Double> cityProportion;
        private List<Double> industryProportion;
        private List<Double> irrigateProportion;
        private List<Double> greeningProportion;
    }
}
