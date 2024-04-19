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
    //率定开始时间
    @ApiModelProperty("率定开始时间")
    private Date startTime;
    //率定结束时间
    @ApiModelProperty("率定结束时间")
    private Date endTime;
    @ApiModelProperty("断面参数列表")
    private List<ModelParameters> parametersList;
}
