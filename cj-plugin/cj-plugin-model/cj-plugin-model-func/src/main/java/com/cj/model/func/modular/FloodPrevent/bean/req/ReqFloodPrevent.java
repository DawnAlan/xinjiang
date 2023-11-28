package com.cj.model.func.modular.FloodPrevent.bean.req;

import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.FloodPrevent.entity.DataFloodPrevent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReqFloodPrevent {

    @ApiModelProperty(value = "预报断面")
    private Map<String, List<DataFloodPrevent>> data;

    @ApiModelProperty(value = "曲线")
    private List<CurveParam> curveParam;

    @ApiModelProperty(value = "楼庄子起调水位")
    private double H1_begin;

    @ApiModelProperty(value = "楼庄子期末控制水位")
    private double H1_end;

    @ApiModelProperty(value = "头屯河起调水位")
    private double H2_begin;

    @ApiModelProperty(value = "头屯河期末控制水位")
    private double H2_end;

    @ApiModelProperty(value = "楼庄子模型精度(0.05~0.2)")
    private double Step1;

    @ApiModelProperty(value = "头屯河模型精度(0.05~0.2)")
    private double Step2;

    @ApiModelProperty(value = "来水预报结果名称")
    private String programmeName;
}
