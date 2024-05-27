package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class A3StatisticsAllRes {

    private Map<String, List<A3StatisticsRes>> statistics;

    private List<A3StatisticsRes> maximum;
    private List<A3StatisticsRes> minimum;
}
