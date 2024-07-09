package com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class SelectInfoListByIdsReq {

    @ApiModelProperty(value = "id")
    private String ids;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "查询时间区间-开始时间")
    private String startTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "查询时间区间-结束时间")
    private String endTime;
}
