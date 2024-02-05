package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 工业水费(IndustrialWaterFee)表实体类
 *
 * @author makejava
 * @since 2024-01-31 20:11:19
 */
@Data
public class IndustrialWaterFee extends Model<IndustrialWaterFee> {
    
    private String id;
    //管理站
    private String station;
    //年份
    private Integer year;
    //月份
    private Integer month;
    //记录日期
    private Date recordTime;
    //流量
    private Double flow;
}

