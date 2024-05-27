package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * (MonthWaterUsePlanOwner)表实体类
 *
 * @author makejava
 * @since 2024-03-23 17:57:24
 */
@Data
public class MonthWaterUsePlanOwner extends Model<MonthWaterUsePlanOwner> {
    
    private String id;
    
    private String unit;
    
    private Double earlyOctober;
    
    private Double midDay;
    
    private Double laterOctober;
    
    private Double total;
    
    private Integer year;
    
    private String area;
    
    private Date createTime;
    
    private String createBy;
    
    private Date updateTime;
    
    private String updateBy;
    
    private Integer del;
    
    private Integer month;
    
    private String unitId;

    @ApiModelProperty(value = "绑定A3ID")
    private String bindId;

}

