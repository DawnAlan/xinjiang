package com.cj.flood.func.modular.prediction.bean.req;

import com.cj.model.func.modular.FloodPredict.entity.RainFallDto;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IncomingWaterForecastAddReq implements Serializable {

    @ApiModelProperty(value = "模拟降雨数据")
    private List<RainFallDto> rainFallDtos;

    @ApiModelProperty(value = "来水预报字段数据")
    private IncomingWaterForecast incomingWaterForecast;
}
