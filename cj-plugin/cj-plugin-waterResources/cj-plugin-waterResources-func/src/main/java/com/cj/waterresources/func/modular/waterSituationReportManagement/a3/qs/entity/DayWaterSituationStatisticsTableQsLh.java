package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * (DayWaterSituationStatisticsTableQsLh)表实体类
 *
 * @author makejava
 * @since 2024-03-21 10:58:52
 */
@Data
public class DayWaterSituationStatisticsTableQsLh extends Model<DayWaterSituationStatisticsTableQsLh> {
    //主键ID
    private String id;
    //记录日期
    private Date recordTime;
    //时刻
    private String time;
    //表头ID
    private String tableHeadId;
    //值
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double v;
    //前端历史表头
    private String frontTableList;
    //后台使用表头
    private String endTableList;
}

