package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 河西管理站日水情统计表(DayWaterSituationStatisticsTableHx)表实体类
 *
 * @author makejava
 * @since 2023-12-23 15:59:12
 */
@Data
public class DayWaterSituationStatisticsTableHx extends Model<DayWaterSituationStatisticsTableHx> {
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
}

