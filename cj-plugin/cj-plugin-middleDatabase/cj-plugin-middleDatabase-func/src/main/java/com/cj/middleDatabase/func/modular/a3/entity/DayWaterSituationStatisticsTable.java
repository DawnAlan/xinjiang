package com.cj.middleDatabase.func.modular.a3.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class DayWaterSituationStatisticsTable implements Serializable {

    @ApiModelProperty(value = "主键ID")
    protected String id;
    @ApiModelProperty(value = "记录日期")
    protected Date recordTime;
    @ApiModelProperty(value = "时刻")
    protected String time;
    @ApiModelProperty(value = "表头ID")
    protected String tableHeadId;
    @ApiModelProperty(value = "值")
    protected BigDecimal v;
    @ApiModelProperty(value = "前端历史表头")
    protected String frontTableList;
    @ApiModelProperty(value = "后台使用表头")
    protected String endTableList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}