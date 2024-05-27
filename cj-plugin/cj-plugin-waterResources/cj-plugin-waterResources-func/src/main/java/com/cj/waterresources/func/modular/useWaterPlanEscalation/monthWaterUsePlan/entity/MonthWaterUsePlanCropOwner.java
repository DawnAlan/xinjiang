package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * (MonthWaterUsePlanCropOwner)表实体类
 *
 * @author makejava
 * @since 2024-03-23 17:57:00
 */
@Data
public class MonthWaterUsePlanCropOwner extends Model<MonthWaterUsePlanCropOwner> {
    
    private String id;
    
    private String unit;
    
    private String irrigatedCrop;
    
    private String cropType;
    
    private Double irrigatedArea;
    
    private Double irrigatedQuota;
    
    private Double monthIrrigatedCount;
    
    private Double waterDemandOwner;
    
    private Double earlyOctoberCount;
    
    private Double midDayCount;
    
    private Double laterOctoberCount;
    
    private Double totalCount;
    
    private Date createTime;
    
    private String createBy;
    
    private Date updateTime;
    
    private String updateBy;
    
    private Integer del;
    
    private Integer year;
    
    private String area;
    
    private Double irrigationQuota;
    
    private String unitId;
    
    private Integer month;
    
    private Double earlyOctoberWaterDemandOwner;
    
    private Double midDayWaterDemandOwner;
    
    private Double laterOctoberWaterDemandOwner;
    
    private Double totalCountWaterDemandOwner;

    @ApiModelProperty(value = "绑定A3ID")
    private String bindId;

}

