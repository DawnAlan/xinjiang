package com.cj.flood.func.modular.prediction.bean.res;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cj.flood.func.modular.prediction.bean.dto.FloodPeakDto;
import com.cj.flood.func.modular.prediction.bean.dto.IncomingWaterForecastKVDto;
import com.cj.flood.func.modular.prediction.bean.dto.IncomingWaterForecastViewDto;
import com.cj.flood.func.modular.prediction.bean.dto.PredictionProcessDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class IncomingWaterForecastDetailsRes implements Serializable {

    @ApiModelProperty(value = "方案名称")
    private String programmeName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "预报时间")
    private Date predictionTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "预报时间")
    private Date endTime;

    @ApiModelProperty(value = "视图图表")
    private Map<String, IncomingWaterForecastViewDto> view;
}
