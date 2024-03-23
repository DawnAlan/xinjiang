package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * (YearWaterUsePlanTrunkCanalOwner)表实体类
 *
 * @author makejava
 * @since 2024-03-22 19:40:53
 */
@Data
public class YearWaterUsePlanTrunkCanalOwner extends Model<YearWaterUsePlanTrunkCanalOwner> {
    
    private String id;
    
    private Integer year;
    
    private String area;
    
    private String unit;
    
    private Double february;
    
    private Double march;
    
    private Double april;
    
    private Double may;
    
    private Double june;
    
    private Double july;
    
    private Double august;
    
    private Double september;
    
    private Double october;
    
    private Double november;
    
    private Double amountCount;
    
    private Date createTime;
    
    private String createBy;
    
    private Date updateTime;
    
    private String updateBy;
    
    private Integer del;
    
    private Double january;
    
    private Double december;
    
    private String useWaterPlan;
    
    private String unitId;
}

