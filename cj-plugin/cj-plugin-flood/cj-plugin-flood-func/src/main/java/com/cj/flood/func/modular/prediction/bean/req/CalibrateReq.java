package com.cj.flood.func.modular.prediction.bean.req;

import com.cj.flood.func.modular.prediction.entity.ModelParameters;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CalibrateReq {
    //模型类型(true-自动率定，false-人工率定)
    @ApiModelProperty("模型类型(true-自动率定，false-人工率定)")
    private Boolean isAutomatic;
    @ApiModelProperty("是否通过初筛选(true-程序自动筛选，false-人工筛选)")
    private Boolean isSelected;
    @ApiModelProperty("率定名称")
    private String modelName;
    //率定开始时间
    @ApiModelProperty("率定时间")
    private List<Date[]> time;
//    //率定结束时间
//    @ApiModelProperty("率定结束时间")
//    private Date endTime;
    @ApiModelProperty("断面参数列表")
    private List<ModelParameters> parametersList;
}
