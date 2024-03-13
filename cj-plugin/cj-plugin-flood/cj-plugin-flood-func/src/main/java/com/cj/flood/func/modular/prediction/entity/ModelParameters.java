package com.cj.flood.func.modular.prediction.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 陕北模型参数(ModelParameters)表实体类
 *
 * @author makejava
 * @since 2024-03-13 12:27:10
 */
@SuppressWarnings("serial")
public class ModelParameters extends Model<ModelParameters> {
    
    private String id;
    //站点名称
    private String siteName;
    //面积
    private Double area;
    //透水系数
    private Double fb;
    //张力水蓄水
容量
    private Double wm;
    //蒸散发折
减系数
    private Double kc;
    //流域土壤稳定下
渗率
    private Double fc;
    //流域土壤最大
下渗率
    private Double fm;
    //霍尔顿下渗曲
线方程
    private Double k;
    //下渗能力分
布系数
    private Double b;
    //地面径流消
退系数
    private Double cs;
    //汇流滞时
    private Double l;
    //前期径流
    private Integer puelwpactdax;
    //降水系数
    private String deriodiengthe;
    //时间
    private Date date;
    //合格率
    private String rate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Double getFb() {
        return fb;
    }

    public void setFb(Double fb) {
        this.fb = fb;
    }

    public Double getWm() {
        return wm;
    }

    public void setWm(Double wm) {
        this.wm = wm;
    }

    public Double getKc() {
        return kc;
    }

    public void setKc(Double kc) {
        this.kc = kc;
    }

    public Double getFc() {
        return fc;
    }

    public void setFc(Double fc) {
        this.fc = fc;
    }

    public Double getFm() {
        return fm;
    }

    public void setFm(Double fm) {
        this.fm = fm;
    }

    public Double getK() {
        return k;
    }

    public void setK(Double k) {
        this.k = k;
    }

    public Double getB() {
        return b;
    }

    public void setB(Double b) {
        this.b = b;
    }

    public Double getCs() {
        return cs;
    }

    public void setCs(Double cs) {
        this.cs = cs;
    }

    public Double getL() {
        return l;
    }

    public void setL(Double l) {
        this.l = l;
    }

    public Integer getPuelwpactdax() {
        return puelwpactdax;
    }

    public void setPuelwpactdax(Integer puelwpactdax) {
        this.puelwpactdax = puelwpactdax;
    }

    public String getDeriodiengthe() {
        return deriodiengthe;
    }

    public void setDeriodiengthe(String deriodiengthe) {
        this.deriodiengthe = deriodiengthe;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

}

