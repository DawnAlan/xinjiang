package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * (TenDayWaterUsePlanOwner)表实体类
 *
 * @author makejava
 * @since 2024-03-25 16:55:15
 */
@Data
public class TenDayWaterUsePlanOwner extends Model<TenDayWaterUsePlanOwner> {
    
    private String id;
    
    private String area;
    
    private String useWaterUser;
    
    private String cropType;
    
    private Integer year;
    
    private Integer month;
    
    private String tenDays;
    
    private String irrigatedCrop;
    
    private Double irrigatedArea;
    
    private Double irrigatedQuota;
    
    private Double irrigationCount;
    
    private Date createTime;
    
    private String createBy;
    
    private Date updateTime;
    
    private String updateBy;
    
    private Double irrigationQuota;
    
    private Double waterDemandOwner;
}

