package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 制材厂日水情统计表(DayWaterSituationStatisticsTableZcc)表实体类
 *
 * @author makejava
 * @since 2023-12-23 16:01:31
 */
@Data
public class DayWaterSituationStatisticsTableZcc extends Model<DayWaterSituationStatisticsTableZcc> {
    //主键ID
    private String id;
    //记录日期
    private Date recordTime;
    //表头ID
    private String tableHeadId;
    //值
    private String v;

    private String frontTableList;

    private String endTableList;
}

