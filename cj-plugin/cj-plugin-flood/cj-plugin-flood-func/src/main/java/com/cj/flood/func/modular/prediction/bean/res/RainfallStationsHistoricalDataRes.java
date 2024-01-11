package com.cj.flood.func.modular.prediction.bean.res;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 雨量站历史数据返回实例
 */
@Data
public class RainfallStationsHistoricalDataRes implements Serializable {

    @ApiModelProperty("降雨量")
    private Double rainfall;

    @ApiModelProperty("时间")
    private String time;

}
