package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 头屯河水库日水情统计表(DayWaterSituationStatisticsTableTth)表实体类
 *
 * @author makejava
 * @since 2023-12-23 16:01:12
 */
@Data
public class DayWaterSituationStatisticsTableTth extends Model<DayWaterSituationStatisticsTableTth> {
    //主键ID
    private String id;
    //记录日期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;
    //时刻
    private String time;
    //表头ID
    private String tableHeadId;
    //值
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double v;


    private String frontTableList;

    private String endTableList;
}

