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
* 楼庄子水库日水情统计表
* @TableName DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ
*/
@TableName(value ="DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ")
@Data
@ApiModel(value = "楼庄子水库日水情统计表", description = "楼庄子水库日水情统计表")
public class DayWaterSituationStatisticsTableLzz extends DayWaterSituationStatisticsTable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
