package com.cj.flood.func.modular.prediction.bean.req;

import com.cj.flood.func.modular.prediction.entity.BasinParam;
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

    @ApiModelProperty(value = "前期径流")
    private Double preFlow;

    @ApiModelProperty(value = "前期累计降雨")
    private Double preRainFall;

    @ApiModelProperty(value = "是否是模拟降雨")
    private Boolean isSimulation;

    @ApiModelProperty(value = "是否是参考来水")
    private Boolean isReferenceWater;

    @ApiModelProperty(value = "来水预报字段数据")
    private IncomingWaterForecast incomingWaterForecast;

    @ApiModelProperty(value = "流域参数")
    private BasinParam basinParam;
}
