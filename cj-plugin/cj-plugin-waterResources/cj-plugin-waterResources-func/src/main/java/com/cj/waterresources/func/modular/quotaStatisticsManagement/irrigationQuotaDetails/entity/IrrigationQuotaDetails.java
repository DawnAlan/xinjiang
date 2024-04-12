package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 灌溉明细表(IrrigationQuotaDetails)表实体类
 *
 * @author makejava
 * @since 2024-02-02 10:59:15
 */
@Data
public class IrrigationQuotaDetails extends Model<IrrigationQuotaDetails> {
    //主键ID
    private String id;
    //用水户
    private String waterUser;
    //作物类型
    private String cropType;
    //灌溉作物
    private String irrigationCrop;
    //灌溉面积
    private Double irrigationArea;
    //灌溉水量
    private Double irrigationWaterVolume;
    //管理站
    private String station;
    //时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String totalId;

    private String tenDays;

}

