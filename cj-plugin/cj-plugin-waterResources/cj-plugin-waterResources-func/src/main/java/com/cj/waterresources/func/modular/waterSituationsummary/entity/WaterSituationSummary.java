package com.cj.waterresources.func.modular.waterSituationsummary.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * (WaterSituationSummary)表实体类
 *
 * @author makejava
 * @since 2024-03-19 17:25:30
 */
@Data
public class WaterSituationSummary extends Model<WaterSituationSummary> {
    //主键id
    private String id;
    //水库名称
    private String reservoirName;
    //站点名称
    private String siteName;
    //记录时间
    private String dateTime;
    //年
    private Integer year;
    //月
    private Integer month;
    //日
    private Integer day;
    //值
    private Double value;

}

