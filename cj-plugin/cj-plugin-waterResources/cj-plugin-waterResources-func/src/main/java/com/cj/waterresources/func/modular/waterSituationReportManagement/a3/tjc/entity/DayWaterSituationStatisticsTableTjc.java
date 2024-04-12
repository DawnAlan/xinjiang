package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tjc.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 调节池日水情统计表(DayWaterSituationStatisticsTableTjc)表实体类
 *
 * @author makejava
 * @since 2023-12-23 16:00:34
 */
@Data
public class DayWaterSituationStatisticsTableTjc extends Model<DayWaterSituationStatisticsTableTjc> {
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

