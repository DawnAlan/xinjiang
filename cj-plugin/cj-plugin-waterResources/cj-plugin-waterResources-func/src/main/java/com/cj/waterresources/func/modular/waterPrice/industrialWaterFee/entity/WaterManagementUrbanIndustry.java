package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 税费管理-城市工业(WaterManagementUrbanIndustry)表实体类
 *
 * @author makejava
 * @since 2024-04-01 10:52:03
 */
@Data
public class WaterManagementUrbanIndustry extends Model<WaterManagementUrbanIndustry> {
    //站点编号
    private String siteCode;
    //站点名称
    private String siteName;
    //年
    private Integer year;
    //月
    private Integer month;
    //农业水费
    private Double agriculturalWaterPrice;
    //非农业水费
    private Double notAgriculturalWaterPrice;
    //工业水价
    private Double industrialWaterPrice;
    //水资源征收标准
    private Double waterResourceTaxes;
    //农业水量
    private Integer agriculturalProportion;
    //非农业水量
    private Integer notAgriculturalProportion;
    //工业水量
    private Integer industrialProportion;
    //应交水费合计
    private Double totalWaterFeesPayable;
    //已交水费合计
    private Double totalPaidWaterFees;
    //盈余水费合计
    private Double totalSurplusWaterFees;
    //应交农业水费
    private Double agriculturalWaterFeesPayable;
    //已交农业水费
    private Double agriculturalPaidWaterFees;
    //盈余农业水费
    private Double agriculturalSurplusWaterFees;
    //应交非农业水费
    private Double notAgriculturalWaterFeesPayable;
    //已交非农业水费
    private Double notAgriculturalPaidWaterFees;
    //盈余非农业水费
    private Double notAgriculturalSurplusWaterFees;
    //应交工业水费
    private Double industrialWaterFeesPayable;
    //已交工业水费
    private Double industrialPaidWaterFees;
    //盈余工业水费
    private Double industrialSurplusWaterFees;
    //应交工业水资源费
    private Double waterResourceWaterFeesPayable;
    //预交工业水资源费
    private Double waterResourcePaidWaterFees;
    //盈余水资源费
    private Double waterResourceSurplusWaterFees;
    //年累计用水量
    private Double annualCumulativeWaterConsumption;
    //ID
    private String id;
    //备注
    private String remarks;
}

