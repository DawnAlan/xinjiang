package com.cj.middleDatabase.func.modular.a3.entity;

import java.io.Serializable;

import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
* 头屯河水库日水情统计表
* @TableName DAY_WATER_SITUATION_STATISTICS_TABLE_TTH
*/
@TableName(value ="DAY_WATER_SITUATION_STATISTICS_TABLE_TTH")
@Data
@ApiModel(value = "头屯河水库日水情统计表", description = "头屯河水库日水情统计表")
public class DayWaterSituationStatisticsTableTth extends DayWaterSituationStatisticsTable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
