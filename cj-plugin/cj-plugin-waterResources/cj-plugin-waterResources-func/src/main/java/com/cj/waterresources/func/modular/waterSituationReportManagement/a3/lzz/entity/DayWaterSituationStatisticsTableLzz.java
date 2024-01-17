package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 楼庄子水库日水情统计表(DayWaterSituationStatisticsTableLzz)表实体类
 *
 * @author makejava
 * @since 2023-12-23 15:59:33
 */
@Data
public class DayWaterSituationStatisticsTableLzz extends Model<DayWaterSituationStatisticsTableLzz> {
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

