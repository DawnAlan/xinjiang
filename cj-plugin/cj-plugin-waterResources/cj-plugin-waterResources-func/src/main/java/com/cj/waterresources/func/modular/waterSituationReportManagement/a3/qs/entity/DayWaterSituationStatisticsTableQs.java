package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 渠首管理站日水情统计表(DayWaterSituationStatisticsTableQs)表实体类
 *
 * @author makejava
 * @since 2023-12-23 15:59:55
 */
@Data
public class DayWaterSituationStatisticsTableQs extends Model<DayWaterSituationStatisticsTableQs> {
    //主键ID
    private String id;
    //记录日期
    private Date recordTime;
    //时刻
    private String time;
    //表头ID
    private String tableHeadId;
    //值
    private Double v;
}

