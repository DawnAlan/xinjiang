package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 对口率日水情统计表(DayWaterSituationStatisticsTableDkl)表实体类
 *
 * @author makejava
 * @since 2023-12-23 15:58:23
 */
@Data
public class DayWaterSituationStatisticsTableDkl extends Model<DayWaterSituationStatisticsTableDkl> {
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

    private String frontTableList;

    private String endTableList;
}

