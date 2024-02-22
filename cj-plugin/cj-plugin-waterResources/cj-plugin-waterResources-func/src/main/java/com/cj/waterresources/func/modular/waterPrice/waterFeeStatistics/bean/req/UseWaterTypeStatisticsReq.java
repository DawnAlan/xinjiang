package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UseWaterTypeStatisticsReq implements Serializable {

    private String station;

    private String useType;

    private String startTime;

    private String endTime;

    private List<String> stationList;
}
