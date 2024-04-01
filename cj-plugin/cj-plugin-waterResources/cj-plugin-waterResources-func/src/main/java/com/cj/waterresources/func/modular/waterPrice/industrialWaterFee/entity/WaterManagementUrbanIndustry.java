package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 税费管理-城市工业(WaterManagementUrbanIndustry)表实体类
 *
 * @author makejava
 * @since 2024-04-01 10:52:03
 */
@SuppressWarnings("serial")
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
    //工业水价
    private Double industrialWaterPrice;
    //水资源征收标准
    private Double waterResourceTaxes;
    //农业水量
    private Integer agriculturalProportion;
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


    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Double getAgriculturalWaterPrice() {
        return agriculturalWaterPrice;
    }

    public void setAgriculturalWaterPrice(Double agriculturalWaterPrice) {
        this.agriculturalWaterPrice = agriculturalWaterPrice;
    }

    public Double getIndustrialWaterPrice() {
        return industrialWaterPrice;
    }

    public void setIndustrialWaterPrice(Double industrialWaterPrice) {
        this.industrialWaterPrice = industrialWaterPrice;
    }

    public Double getWaterResourceTaxes() {
        return waterResourceTaxes;
    }

    public void setWaterResourceTaxes(Double waterResourceTaxes) {
        this.waterResourceTaxes = waterResourceTaxes;
    }

    public Integer getAgriculturalProportion() {
        return agriculturalProportion;
    }

    public void setAgriculturalProportion(Integer agriculturalProportion) {
        this.agriculturalProportion = agriculturalProportion;
    }

    public Integer getIndustrialProportion() {
        return industrialProportion;
    }

    public void setIndustrialProportion(Integer industrialProportion) {
        this.industrialProportion = industrialProportion;
    }

    public Double getTotalWaterFeesPayable() {
        return totalWaterFeesPayable;
    }

    public void setTotalWaterFeesPayable(Double totalWaterFeesPayable) {
        this.totalWaterFeesPayable = totalWaterFeesPayable;
    }

    public Double getTotalPaidWaterFees() {
        return totalPaidWaterFees;
    }

    public void setTotalPaidWaterFees(Double totalPaidWaterFees) {
        this.totalPaidWaterFees = totalPaidWaterFees;
    }

    public Double getTotalSurplusWaterFees() {
        return totalSurplusWaterFees;
    }

    public void setTotalSurplusWaterFees(Double totalSurplusWaterFees) {
        this.totalSurplusWaterFees = totalSurplusWaterFees;
    }

    public Double getAgriculturalWaterFeesPayable() {
        return agriculturalWaterFeesPayable;
    }

    public void setAgriculturalWaterFeesPayable(Double agriculturalWaterFeesPayable) {
        this.agriculturalWaterFeesPayable = agriculturalWaterFeesPayable;
    }

    public Double getAgriculturalPaidWaterFees() {
        return agriculturalPaidWaterFees;
    }

    public void setAgriculturalPaidWaterFees(Double agriculturalPaidWaterFees) {
        this.agriculturalPaidWaterFees = agriculturalPaidWaterFees;
    }

    public Double getAgriculturalSurplusWaterFees() {
        return agriculturalSurplusWaterFees;
    }

    public void setAgriculturalSurplusWaterFees(Double agriculturalSurplusWaterFees) {
        this.agriculturalSurplusWaterFees = agriculturalSurplusWaterFees;
    }

    public Double getIndustrialWaterFeesPayable() {
        return industrialWaterFeesPayable;
    }

    public void setIndustrialWaterFeesPayable(Double industrialWaterFeesPayable) {
        this.industrialWaterFeesPayable = industrialWaterFeesPayable;
    }

    public Double getIndustrialPaidWaterFees() {
        return industrialPaidWaterFees;
    }

    public void setIndustrialPaidWaterFees(Double industrialPaidWaterFees) {
        this.industrialPaidWaterFees = industrialPaidWaterFees;
    }

    public Double getIndustrialSurplusWaterFees() {
        return industrialSurplusWaterFees;
    }

    public void setIndustrialSurplusWaterFees(Double industrialSurplusWaterFees) {
        this.industrialSurplusWaterFees = industrialSurplusWaterFees;
    }

    public Double getWaterResourceWaterFeesPayable() {
        return waterResourceWaterFeesPayable;
    }

    public void setWaterResourceWaterFeesPayable(Double waterResourceWaterFeesPayable) {
        this.waterResourceWaterFeesPayable = waterResourceWaterFeesPayable;
    }

    public Double getWaterResourcePaidWaterFees() {
        return waterResourcePaidWaterFees;
    }

    public void setWaterResourcePaidWaterFees(Double waterResourcePaidWaterFees) {
        this.waterResourcePaidWaterFees = waterResourcePaidWaterFees;
    }

    public Double getWaterResourceSurplusWaterFees() {
        return waterResourceSurplusWaterFees;
    }

    public void setWaterResourceSurplusWaterFees(Double waterResourceSurplusWaterFees) {
        this.waterResourceSurplusWaterFees = waterResourceSurplusWaterFees;
    }

    public Double getAnnualCumulativeWaterConsumption() {
        return annualCumulativeWaterConsumption;
    }

    public void setAnnualCumulativeWaterConsumption(Double annualCumulativeWaterConsumption) {
        this.annualCumulativeWaterConsumption = annualCumulativeWaterConsumption;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.siteCode;
    }
    }

