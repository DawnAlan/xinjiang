package com.cj.flood.func.modular.prediction.bean.req;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cj.flood.func.core.common.PageTool;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class IncomingWaterForecastListReq extends PageTool implements Serializable {

    @ApiModelProperty(value = "方案名称")
    private String programmeName;

    @ApiModelProperty(value = "时段类型(1-月 2-旬 3-日 4-小时)")
    private Integer periodTimeType;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty(value = "预报时间")
    private Date predictionTime;

    @ApiModelProperty(value = "制作人")
    private String createBy;

    @ApiModelProperty(value = "模型类型(1-中长期 2-短期 3-场次)")
    private Integer modelType;
}
