package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.bean.res;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.bean.vo.QsFlowListTotalVo;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class selectListFlowRes implements Serializable {


    @ApiModelProperty(value = "开始时间")
    private Map<Date, List<DayWaterSituationStatisticsTableQs>> flowDetail;

    @ApiModelProperty(value = "结束时间")
    private List<QsFlowListTotalVo> flowTotal;


}
