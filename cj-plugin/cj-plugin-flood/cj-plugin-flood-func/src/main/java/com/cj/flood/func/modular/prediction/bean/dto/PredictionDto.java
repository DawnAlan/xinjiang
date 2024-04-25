package com.cj.flood.func.modular.prediction.bean.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PredictionDto implements Serializable {

    @ApiModelProperty(value = "时间")
    private Date time;

    @ApiModelProperty(value = "来水过程\\入库流量")
    private Double preQ;

    @ApiModelProperty(value = "水位")
    private Double waterLevel;

    @ApiModelProperty(value = "来水过程\\入库流量")
    private Double outQ;

    @ApiModelProperty("洪量")
    private Double floodVolume;

    @ApiModelProperty(value = "降雨量")
    private Double rainfall;
}
