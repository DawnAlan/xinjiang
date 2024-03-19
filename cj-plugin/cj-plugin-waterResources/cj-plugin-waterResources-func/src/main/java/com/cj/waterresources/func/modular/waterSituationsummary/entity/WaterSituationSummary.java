package com.cj.waterresources.func.modular.waterSituationsummary.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * (WaterSituationSummary)表实体类
 *
 * @author makejava
 * @since 2024-03-19 17:25:30
 */
@SuppressWarnings("serial")
public class WaterSituationSummary extends Model<WaterSituationSummary> {
    //主键id
    private Integer id;
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
    private BigDecimal value;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReservoirName() {
        return reservoirName;
    }

    public void setReservoirName(String reservoirName) {
        this.reservoirName = reservoirName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    }

